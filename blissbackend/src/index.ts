import { DurableObject } from "cloudflare:workers";

export interface Env {
  CHAT_ROOM: DurableObjectNamespace<ChatRoom>;
}

type ClientRole = "pending" | "viewer" | "client";

interface ClientAttachment {
  id: string;
  role: ClientRole;
  challenge: string;
  connectedAt: number;
  lastMessageAt?: number;
  username?: string;
  uuid?: string;
  serverAddress?: string;
}

interface ChatMessage {
  id: string;
  type: "chat";
  username: string;
  uuid: string;
  serverAddress: string;
  message: string;
  timestamp: number;
}

interface MessageRow extends Record<string, SqlStorageValue> {
  id: string;
  timestamp: number;
  username: string;
  uuid: string;
  server_address: string;
  message: string;
}

interface MojangProfile {
  id: string;
  name: string;
}

const MAX_MESSAGE_LENGTH = 240;
const MESSAGE_COOLDOWN_MS = 750;
const HISTORY_LIMIT = 75;

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);

    if (url.pathname === "/chat") {
      const id = env.CHAT_ROOM.idFromName("global");
      return env.CHAT_ROOM.get(id).fetch(request);
    }

    if (url.pathname === "/" || url.pathname === "/index.html") {
      return new Response(INDEX_HTML, {
        headers: {
          "content-type": "text/html; charset=utf-8",
          "cache-control": "no-store"
        }
      });
    }

    if (url.pathname === "/health") {
      return Response.json({ ok: true, service: "blissbackend" });
    }

    return new Response("Not found", { status: 404 });
  }
} satisfies ExportedHandler<Env>;

export class ChatRoom extends DurableObject<Env> {
  constructor(ctx: DurableObjectState, env: Env) {
    super(ctx, env);

    this.ctx.storage.sql.exec(`
      CREATE TABLE IF NOT EXISTS messages (
        id TEXT PRIMARY KEY,
        timestamp INTEGER NOT NULL,
        username TEXT NOT NULL,
        uuid TEXT NOT NULL,
        server_address TEXT NOT NULL,
        message TEXT NOT NULL
      )
    `);
  }

  async fetch(request: Request): Promise<Response> {
    if (request.headers.get("Upgrade") !== "websocket") {
      return new Response("Expected WebSocket upgrade", { status: 426 });
    }

    const pair = new WebSocketPair();
    const [client, server] = Object.values(pair) as [WebSocket, WebSocket];
    const attachment: ClientAttachment = {
      id: crypto.randomUUID(),
      role: "pending",
      challenge: randomChallenge(),
      connectedAt: Date.now()
    };

    server.serializeAttachment(attachment);
    this.ctx.acceptWebSocket(server);

    this.send(server, {
      type: "challenge",
      challenge: attachment.challenge,
      expiresAt: Date.now() + 60_000
    });

    return new Response(null, {
      status: 101,
      webSocket: client
    });
  }

  async webSocketMessage(ws: WebSocket, message: ArrayBuffer | string): Promise<void> {
    const attachment = readAttachment(ws);
    if (!attachment) {
      ws.close(1011, "missing attachment");
      return;
    }

    const payload = parsePayload(message);
    if (!payload) {
      this.sendError(ws, "Invalid message.");
      return;
    }

    switch (payload.type) {
      case "viewer":
        this.setViewer(ws, attachment);
        return;
      case "auth":
        await this.authenticate(ws, attachment, payload);
        return;
      case "chat":
        this.publishChat(ws, attachment, payload);
        return;
      default:
        this.sendError(ws, "Unsupported message type.");
    }
  }

  async webSocketClose(ws: WebSocket): Promise<void> {
    const attachment = readAttachment(ws);
    if (attachment?.role === "client" && attachment.username && attachment.uuid && attachment.serverAddress) {
      this.broadcast({
        type: "presence",
        action: "leave",
        username: attachment.username,
        uuid: attachment.uuid,
        serverAddress: attachment.serverAddress,
        timestamp: Date.now()
      });
    }
  }

  private setViewer(ws: WebSocket, attachment: ClientAttachment): void {
    attachment.role = "viewer";
    ws.serializeAttachment(attachment);

    this.send(ws, {
      type: "ready",
      role: "viewer"
    });
    this.sendHistory(ws);
    this.sendPresence(ws);
  }

  private async authenticate(ws: WebSocket, attachment: ClientAttachment, payload: JsonObject): Promise<void> {
    if (attachment.role === "client") return;

    const username = sanitizeUsername(payload.username);
    const claimedUuid = sanitizeUuid(payload.uuid);
    const serverAddress = sanitizeServerAddress(payload.serverAddress);

    if (!username || !claimedUuid || !serverAddress) {
      this.sendError(ws, "Missing or invalid auth fields.");
      ws.close(1008, "invalid auth");
      return;
    }

    const profile = await verifyMojangSession(username, attachment.challenge);
    if (!profile) {
      this.sendError(ws, "Mojang session verification failed.");
      ws.close(1008, "online-mode required");
      return;
    }

    const verifiedUuid = hyphenateUuid(profile.id);
    if (verifiedUuid !== claimedUuid) {
      this.sendError(ws, "UUID does not match verified Mojang profile.");
      ws.close(1008, "uuid mismatch");
      return;
    }

    attachment.role = "client";
    attachment.username = profile.name;
    attachment.uuid = verifiedUuid;
    attachment.serverAddress = serverAddress;
    ws.serializeAttachment(attachment);

    this.send(ws, {
      type: "ready",
      role: "client",
      username: attachment.username,
      uuid: attachment.uuid,
      serverAddress: attachment.serverAddress
    });
    this.sendHistory(ws);
    this.sendPresence(ws);
    this.broadcast({
      type: "presence",
      action: "join",
      username: attachment.username,
      uuid: attachment.uuid,
      serverAddress: attachment.serverAddress,
      timestamp: Date.now()
    });
  }

  private publishChat(ws: WebSocket, attachment: ClientAttachment, payload: JsonObject): void {
    if (attachment.role !== "client" || !attachment.username || !attachment.uuid || !attachment.serverAddress) {
      this.sendError(ws, "Authenticate from an online-mode Minecraft client first.");
      return;
    }

    const now = Date.now();
    if (attachment.lastMessageAt && now - attachment.lastMessageAt < MESSAGE_COOLDOWN_MS) {
      this.sendError(ws, "Slow down.");
      return;
    }

    const message = sanitizeChatMessage(payload.message);
    if (!message) {
      this.sendError(ws, "Message is empty.");
      return;
    }

    attachment.lastMessageAt = now;
    ws.serializeAttachment(attachment);

    const chatMessage: ChatMessage = {
      id: crypto.randomUUID(),
      type: "chat",
      username: attachment.username,
      uuid: attachment.uuid,
      serverAddress: attachment.serverAddress,
      message,
      timestamp: now
    };

    this.ctx.storage.sql.exec(
      "INSERT INTO messages (id, timestamp, username, uuid, server_address, message) VALUES (?, ?, ?, ?, ?, ?)",
      chatMessage.id,
      chatMessage.timestamp,
      chatMessage.username,
      chatMessage.uuid,
      chatMessage.serverAddress,
      chatMessage.message
    );
    this.ctx.storage.sql.exec(
      "DELETE FROM messages WHERE id NOT IN (SELECT id FROM messages ORDER BY timestamp DESC LIMIT ?)",
      HISTORY_LIMIT
    );

    this.broadcast(chatMessage);
  }

  private sendHistory(ws: WebSocket): void {
    const rows = [...this.ctx.storage.sql.exec<MessageRow>(
      "SELECT id, timestamp, username, uuid, server_address, message FROM messages ORDER BY timestamp DESC LIMIT ?",
      HISTORY_LIMIT
    )].reverse();

    this.send(ws, {
      type: "history",
      messages: rows.map(row => ({
        id: row.id,
        type: "chat",
        username: row.username,
        uuid: row.uuid,
        serverAddress: row.server_address,
        message: row.message,
        timestamp: row.timestamp
      }))
    });
  }

  private sendPresence(ws: WebSocket): void {
    this.send(ws, {
      type: "presence-list",
      users: this.onlineUsers()
    });
  }

  private broadcast(payload: unknown): void {
    for (const ws of this.ctx.getWebSockets()) {
      try {
        ws.send(JSON.stringify(payload));
      } catch {
        ws.close(1011, "send failed");
      }
    }
  }

  private send(ws: WebSocket, payload: unknown): void {
    ws.send(JSON.stringify(payload));
  }

  private sendError(ws: WebSocket, message: string): void {
    this.send(ws, {
      type: "error",
      message
    });
  }

  private onlineUsers(): Array<{ username: string; uuid: string; serverAddress: string }> {
    const users = new Map<string, { username: string; uuid: string; serverAddress: string }>();

    for (const ws of this.ctx.getWebSockets()) {
      const attachment = readAttachment(ws);
      if (attachment?.role !== "client" || !attachment.username || !attachment.uuid || !attachment.serverAddress) continue;
      users.set(attachment.uuid, {
        username: attachment.username,
        uuid: attachment.uuid,
        serverAddress: attachment.serverAddress
      });
    }

    return [...users.values()].sort((a, b) => a.username.localeCompare(b.username));
  }
}

type JsonObject = Record<string, unknown>;

function parsePayload(message: ArrayBuffer | string): JsonObject | null {
  if (typeof message !== "string") return null;
  if (message.length > 4096) return null;

  try {
    const parsed = JSON.parse(message) as unknown;
    if (!parsed || typeof parsed !== "object" || Array.isArray(parsed)) return null;
    return parsed as JsonObject;
  } catch {
    return null;
  }
}

function readAttachment(ws: WebSocket): ClientAttachment | null {
  try {
    return (ws.deserializeAttachment() as ClientAttachment | undefined) ?? null;
  } catch {
    return null;
  }
}

async function verifyMojangSession(username: string, challenge: string): Promise<MojangProfile | null> {
  const url = new URL("https://sessionserver.mojang.com/session/minecraft/hasJoined");
  url.searchParams.set("username", username);
  url.searchParams.set("serverId", challenge);

  const response = await fetch(url, {
    headers: {
      "accept": "application/json"
    }
  });

  if (!response.ok) return null;

  const profile = await response.json() as MojangProfile;
  if (!profile?.id || !profile.name) return null;
  return profile;
}

function sanitizeUsername(value: unknown): string | null {
  if (typeof value !== "string") return null;
  const username = value.trim();
  return /^[A-Za-z0-9_]{3,16}$/.test(username) ? username : null;
}

function sanitizeUuid(value: unknown): string | null {
  if (typeof value !== "string") return null;
  const uuid = value.trim().toLowerCase();
  return /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(uuid) ? uuid : null;
}

function sanitizeServerAddress(value: unknown): string | null {
  if (typeof value !== "string") return null;
  const address = value.replace(/[\u0000-\u001f\u007f]/g, "").trim();
  if (address.length < 1 || address.length > 120) return null;
  return address;
}

function sanitizeChatMessage(value: unknown): string | null {
  if (typeof value !== "string") return null;
  const message = value.replace(/[\u0000-\u001f\u007f]/g, " ").replace(/\s+/g, " ").trim();
  if (!message) return null;
  return message.slice(0, MAX_MESSAGE_LENGTH);
}

function randomChallenge(): string {
  const bytes = new Uint8Array(20);
  crypto.getRandomValues(bytes);
  return [...bytes].map(byte => byte.toString(16).padStart(2, "0")).join("");
}

function hyphenateUuid(value: string): string {
  const id = value.toLowerCase().replace(/-/g, "");
  return `${id.slice(0, 8)}-${id.slice(8, 12)}-${id.slice(12, 16)}-${id.slice(16, 20)}-${id.slice(20)}`;
}

const INDEX_HTML = `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Bliss Chat</title>
  <style>
    :root {
      color-scheme: dark;
      --bg: #110913;
      --panel: rgba(36, 15, 36, 0.82);
      --panel-strong: rgba(52, 20, 51, 0.94);
      --line: rgba(255, 115, 190, 0.24);
      --pink: #ff73be;
      --pink-hot: #ff4fa8;
      --blue: #65d6ff;
      --text: #fff6fb;
      --muted: #c8aabd;
      --shadow: rgba(0, 0, 0, 0.35);
    }

    * {
      box-sizing: border-box;
    }

    body {
      margin: 0;
      min-height: 100vh;
      font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      color: var(--text);
      background:
        linear-gradient(135deg, rgba(255, 115, 190, 0.18), transparent 36%),
        linear-gradient(315deg, rgba(101, 214, 255, 0.16), transparent 34%),
        repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.035) 0 1px, transparent 1px 56px),
        repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.025) 0 1px, transparent 1px 56px),
        var(--bg);
    }

    main {
      width: min(1180px, calc(100vw - 32px));
      min-height: 100vh;
      margin: 0 auto;
      display: grid;
      grid-template-columns: minmax(0, 1fr) 280px;
      gap: 18px;
      padding: 28px 0;
    }

    .shell,
    aside {
      border: 1px solid var(--line);
      background: var(--panel);
      box-shadow: 0 24px 60px var(--shadow);
      backdrop-filter: blur(18px);
    }

    .shell {
      min-height: calc(100vh - 56px);
      display: grid;
      grid-template-rows: auto minmax(0, 1fr) auto;
      border-radius: 8px;
      overflow: hidden;
    }

    header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 16px;
      padding: 18px 20px;
      border-bottom: 1px solid var(--line);
      background: linear-gradient(90deg, rgba(255, 115, 190, 0.18), rgba(101, 214, 255, 0.08));
    }

    h1,
    h2 {
      margin: 0;
      letter-spacing: 0;
    }

    h1 {
      font-size: clamp(1.4rem, 2vw, 2rem);
      line-height: 1.05;
    }

    h1 span {
      color: var(--pink);
    }

    .status {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      min-width: 118px;
      justify-content: center;
      padding: 8px 11px;
      border: 1px solid var(--line);
      background: rgba(0, 0, 0, 0.18);
      border-radius: 999px;
      color: var(--muted);
      font-size: 0.86rem;
      white-space: nowrap;
    }

    .dot {
      width: 9px;
      height: 9px;
      border-radius: 50%;
      background: #ffcc66;
      box-shadow: 0 0 18px currentColor;
    }

    .status.live .dot {
      background: #62f7b6;
    }

    .status.down .dot {
      background: #ff5f86;
    }

    .messages {
      display: flex;
      flex-direction: column;
      gap: 10px;
      min-height: 0;
      overflow: auto;
      padding: 18px;
    }

    .message {
      display: grid;
      grid-template-columns: 132px minmax(0, 1fr);
      gap: 12px;
      align-items: start;
      padding: 12px 14px;
      border: 1px solid rgba(255, 255, 255, 0.08);
      background: rgba(16, 7, 18, 0.48);
      border-radius: 8px;
    }

    .message:hover {
      border-color: rgba(255, 115, 190, 0.42);
      background: rgba(255, 115, 190, 0.08);
    }

    .name {
      min-width: 0;
      color: var(--pink);
      font-weight: 800;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: help;
    }

    .text {
      min-width: 0;
      overflow-wrap: anywhere;
      line-height: 1.45;
    }

    .time {
      margin-top: 3px;
      color: var(--muted);
      font-size: 0.76rem;
    }

    .composer {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 14px 16px;
      border-top: 1px solid var(--line);
      background: var(--panel-strong);
    }

    .composer input {
      width: 100%;
      min-width: 0;
      height: 42px;
      border: 1px solid rgba(255, 255, 255, 0.11);
      border-radius: 8px;
      background: rgba(0, 0, 0, 0.2);
      color: var(--muted);
      padding: 0 14px;
      font: inherit;
    }

    .badge {
      flex: 0 0 auto;
      display: inline-flex;
      align-items: center;
      height: 42px;
      padding: 0 13px;
      border-radius: 8px;
      color: #180911;
      background: linear-gradient(135deg, var(--pink), var(--blue));
      font-weight: 900;
    }

    aside {
      min-height: calc(100vh - 56px);
      border-radius: 8px;
      padding: 18px;
      overflow: hidden;
    }

    h2 {
      color: var(--blue);
      font-size: 1rem;
      line-height: 1.2;
      margin-bottom: 14px;
    }

    .users {
      display: flex;
      flex-direction: column;
      gap: 9px;
    }

    .user {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 10px;
      padding: 10px 11px;
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 8px;
      background: rgba(0, 0, 0, 0.18);
      cursor: help;
    }

    .user strong {
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: var(--pink);
    }

    .server {
      color: var(--muted);
      font-size: 0.78rem;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      max-width: 120px;
    }

    .empty {
      color: var(--muted);
      padding: 24px;
      text-align: center;
      border: 1px dashed var(--line);
      border-radius: 8px;
    }

    @media (max-width: 820px) {
      main {
        grid-template-columns: 1fr;
      }

      .shell,
      aside {
        min-height: auto;
      }

      .shell {
        height: 70vh;
      }

      .message {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>
<body>
  <main>
    <section class="shell">
      <header>
        <h1><span>Bliss</span> Chat</h1>
        <div class="status" id="status"><span class="dot"></span><span id="statusText">Connecting</span></div>
      </header>
      <div class="messages" id="messages">
        <div class="empty" id="empty">No messages yet.</div>
      </div>
      <div class="composer">
        <input value="Minecraft verified chat feed" readonly aria-label="Chat state">
        <div class="badge">Viewer</div>
      </div>
    </section>
    <aside>
      <h2>Online Players</h2>
      <div class="users" id="users">
        <div class="empty">No players online.</div>
      </div>
    </aside>
  </main>
  <script>
    const messagesEl = document.getElementById("messages");
    const usersEl = document.getElementById("users");
    const emptyEl = document.getElementById("empty");
    const statusEl = document.getElementById("status");
    const statusText = document.getElementById("statusText");
    const messages = new Map();
    let ws;

    function connect() {
      const scheme = location.protocol === "https:" ? "wss:" : "ws:";
      ws = new WebSocket(\`\${scheme}//\${location.host}/chat\`);

      ws.addEventListener("open", () => {
        setStatus("Live", "live");
        ws.send(JSON.stringify({ type: "viewer" }));
      });

      ws.addEventListener("close", () => {
        setStatus("Reconnecting", "down");
        setTimeout(connect, 1400);
      });

      ws.addEventListener("message", event => {
        const payload = JSON.parse(event.data);
        if (payload.type === "history") {
          messages.clear();
          messagesEl.replaceChildren();
          for (const message of payload.messages || []) addMessage(message);
          syncEmpty();
        } else if (payload.type === "chat") {
          addMessage(payload);
          syncEmpty();
        } else if (payload.type === "presence-list") {
          renderUsers(payload.users || []);
        } else if (payload.type === "presence") {
          requestPresenceSoon();
        }
      });
    }

    function setStatus(text, state) {
      statusText.textContent = text;
      statusEl.className = \`status \${state}\`;
    }

    function addMessage(message) {
      if (!message.id || messages.has(message.id)) return;
      messages.set(message.id, message);

      const row = document.createElement("div");
      row.className = "message";

      const meta = document.createElement("div");
      const name = document.createElement("div");
      name.className = "name";
      name.textContent = message.username || "Unknown";
      name.title = \`Playing on: \${message.serverAddress || "unknown"}\`;
      const time = document.createElement("div");
      time.className = "time";
      time.textContent = formatTime(message.timestamp);
      meta.append(name, time);

      const text = document.createElement("div");
      text.className = "text";
      text.textContent = message.message || "";

      row.append(meta, text);
      messagesEl.append(row);
      messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function renderUsers(users) {
      usersEl.replaceChildren();
      if (!users.length) {
        const empty = document.createElement("div");
        empty.className = "empty";
        empty.textContent = "No players online.";
        usersEl.append(empty);
        return;
      }

      for (const user of users) {
        const row = document.createElement("div");
        row.className = "user";
        row.title = \`Playing on: \${user.serverAddress || "unknown"}\`;
        const name = document.createElement("strong");
        name.textContent = user.username || "Unknown";
        const server = document.createElement("span");
        server.className = "server";
        server.textContent = user.serverAddress || "unknown";
        row.append(name, server);
        usersEl.append(row);
      }
    }

    let presenceTimer = 0;
    function requestPresenceSoon() {
      clearTimeout(presenceTimer);
      presenceTimer = setTimeout(() => {
        if (ws?.readyState === WebSocket.OPEN) ws.send(JSON.stringify({ type: "viewer" }));
      }, 120);
    }

    function syncEmpty() {
      if (emptyEl) emptyEl.remove();
      if (!messages.size) {
        const empty = document.createElement("div");
        empty.className = "empty";
        empty.id = "empty";
        empty.textContent = "No messages yet.";
        messagesEl.append(empty);
      }
    }

    function formatTime(timestamp) {
      if (!timestamp) return "";
      return new Intl.DateTimeFormat(undefined, { hour: "2-digit", minute: "2-digit" }).format(new Date(timestamp));
    }

    connect();
  </script>
</body>
</html>`;
