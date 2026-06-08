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

interface MetricRow extends Record<string, SqlStorageValue> {
  value: SqlStorageValue;
}

interface ServerMetricRow extends Record<string, SqlStorageValue> {
  server_address: string;
  sessions: number;
}

interface PlayerSighting {
  seenUsername: string;
  seenUuid: string;
  serverAddress: string;
  dimension: string;
  x: number;
  y: number;
  z: number;
  distance: number;
  health: number;
  maxHealth: number;
  ping: number;
  gameMode: string;
  sightingCount: number;
  totalVisibleMs: number;
  visibleMs: number;
}

interface MojangProfile {
  id: string;
  name: string;
}

const MAX_MESSAGE_LENGTH = 240;
const MESSAGE_COOLDOWN_MS = 750;
const HISTORY_LIMIT = 75;
const SIGHTING_RETENTION_MS = 14 * 24 * 60 * 60 * 1000;

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
    this.ctx.storage.sql.exec(`
      CREATE TABLE IF NOT EXISTS sessions (
        id TEXT PRIMARY KEY,
        connected_at INTEGER NOT NULL,
        disconnected_at INTEGER,
        username TEXT NOT NULL,
        uuid TEXT NOT NULL,
        server_address TEXT NOT NULL
      )
    `);
    this.ctx.storage.sql.exec(`
      CREATE TABLE IF NOT EXISTS presence_events (
        id TEXT PRIMARY KEY,
        timestamp INTEGER NOT NULL,
        action TEXT NOT NULL,
        username TEXT NOT NULL,
        uuid TEXT NOT NULL,
        server_address TEXT NOT NULL
      )
    `);
    this.ctx.storage.sql.exec(`
      CREATE TABLE IF NOT EXISTS player_sightings (
        id TEXT PRIMARY KEY,
        timestamp INTEGER NOT NULL,
        reporter_username TEXT NOT NULL,
        reporter_uuid TEXT NOT NULL,
        reporter_server_address TEXT NOT NULL,
        seen_username TEXT NOT NULL,
        seen_uuid TEXT NOT NULL,
        seen_server_address TEXT NOT NULL,
        dimension TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        distance REAL NOT NULL,
        health REAL NOT NULL,
        max_health REAL NOT NULL,
        ping INTEGER NOT NULL,
        game_mode TEXT NOT NULL,
        sighting_count INTEGER NOT NULL,
        total_visible_ms INTEGER NOT NULL,
        visible_ms INTEGER NOT NULL
      )
    `);
    this.ctx.storage.sql.exec("CREATE INDEX IF NOT EXISTS idx_player_sightings_timestamp ON player_sightings (timestamp)");
    this.ctx.storage.sql.exec("CREATE INDEX IF NOT EXISTS idx_player_sightings_seen_uuid ON player_sightings (seen_uuid)");
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
      case "seen_player":
        this.recordPlayerSighting(ws, attachment, payload);
        return;
      default:
        this.sendError(ws, "Unsupported message type.");
    }
  }

  async webSocketClose(ws: WebSocket): Promise<void> {
    const attachment = readAttachment(ws);
    if (attachment?.role === "client" && attachment.username && attachment.uuid && attachment.serverAddress) {
      const timestamp = Date.now();
      this.closeSession(attachment, timestamp);
      this.recordPresence("leave", attachment, timestamp);
      this.broadcast({
        type: "presence",
        action: "leave",
        username: attachment.username,
        uuid: attachment.uuid,
        serverAddress: attachment.serverAddress,
        timestamp
      });
      this.broadcastStats();
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
    this.sendStats(ws);
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

    const timestamp = Date.now();
    this.recordSession(attachment, timestamp);

    this.send(ws, {
      type: "ready",
      role: "client",
      username: attachment.username,
      uuid: attachment.uuid,
      serverAddress: attachment.serverAddress
    });
    this.sendPresence(ws);
    this.recordPresence("join", attachment, timestamp);
    this.broadcast({
      type: "presence",
      action: "join",
      username: attachment.username,
      uuid: attachment.uuid,
      serverAddress: attachment.serverAddress,
      timestamp
    });
    this.broadcastStats();
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
    this.broadcastStats();
  }

  private recordPlayerSighting(ws: WebSocket, attachment: ClientAttachment, payload: JsonObject): void {
    if (attachment.role !== "client" || !attachment.username || !attachment.uuid || !attachment.serverAddress) {
      this.sendError(ws, "Authenticate from an online-mode Minecraft client first.");
      return;
    }

    const sighting = sanitizePlayerSighting(payload, attachment.serverAddress);
    if (!sighting) {
      this.sendError(ws, "Invalid player sighting.");
      return;
    }

    const timestamp = Date.now();
    this.ctx.storage.sql.exec(
      `INSERT INTO player_sightings (
        id,
        timestamp,
        reporter_username,
        reporter_uuid,
        reporter_server_address,
        seen_username,
        seen_uuid,
        seen_server_address,
        dimension,
        x,
        y,
        z,
        distance,
        health,
        max_health,
        ping,
        game_mode,
        sighting_count,
        total_visible_ms,
        visible_ms
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      crypto.randomUUID(),
      timestamp,
      attachment.username,
      attachment.uuid,
      attachment.serverAddress,
      sighting.seenUsername,
      sighting.seenUuid,
      sighting.serverAddress,
      sighting.dimension,
      sighting.x,
      sighting.y,
      sighting.z,
      sighting.distance,
      sighting.health,
      sighting.maxHealth,
      sighting.ping,
      sighting.gameMode,
      sighting.sightingCount,
      sighting.totalVisibleMs,
      sighting.visibleMs
    );
    this.ctx.storage.sql.exec(
      "DELETE FROM player_sightings WHERE timestamp < ?",
      timestamp - SIGHTING_RETENTION_MS
    );

    this.broadcastStats();
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

  private sendStats(ws: WebSocket): void {
    this.send(ws, {
      type: "stats",
      stats: this.dashboardStats()
    });
  }

  private broadcastStats(): void {
    const payload = {
      type: "stats",
      stats: this.dashboardStats()
    };
    this.broadcast(payload);
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

  private recordSession(attachment: ClientAttachment, timestamp: number): void {
    if (!attachment.username || !attachment.uuid || !attachment.serverAddress) return;

    this.ctx.storage.sql.exec(
      "INSERT OR REPLACE INTO sessions (id, connected_at, disconnected_at, username, uuid, server_address) VALUES (?, ?, NULL, ?, ?, ?)",
      attachment.id,
      timestamp,
      attachment.username,
      attachment.uuid,
      attachment.serverAddress
    );
  }

  private closeSession(attachment: ClientAttachment, timestamp: number): void {
    this.ctx.storage.sql.exec(
      "UPDATE sessions SET disconnected_at = ? WHERE id = ? AND disconnected_at IS NULL",
      timestamp,
      attachment.id
    );
  }

  private recordPresence(action: "join" | "leave", attachment: ClientAttachment, timestamp: number): void {
    if (!attachment.username || !attachment.uuid || !attachment.serverAddress) return;

    this.ctx.storage.sql.exec(
      "INSERT INTO presence_events (id, timestamp, action, username, uuid, server_address) VALUES (?, ?, ?, ?, ?, ?)",
      crypto.randomUUID(),
      timestamp,
      action,
      attachment.username,
      attachment.uuid,
      attachment.serverAddress
    );
    this.ctx.storage.sql.exec(
      "DELETE FROM presence_events WHERE timestamp < ?",
      timestamp - 7 * 24 * 60 * 60 * 1000
    );
  }

  private dashboardStats(): DashboardStats {
    const now = Date.now();
    const onlineUsers = this.onlineUsers();
    const onlineServers = new Set(onlineUsers.map(user => user.serverAddress));
    const roles = this.roleCounts();
    const latestMessageAt = this.firstNumber("SELECT MAX(timestamp) AS value FROM messages");
    const latestSightingAt = this.firstNumber("SELECT MAX(timestamp) AS value FROM player_sightings");

    return {
      generatedAt: now,
      onlinePlayers: onlineUsers.length,
      onlineServers: onlineServers.size,
      viewersOnline: roles.viewers,
      clientsConnected: roles.clients,
      uniquePlayers: this.firstNumber("SELECT COUNT(DISTINCT uuid) AS value FROM sessions"),
      uniqueServers: this.firstNumber("SELECT COUNT(DISTINCT server_address) AS value FROM sessions"),
      totalSessions: this.firstNumber("SELECT COUNT(*) AS value FROM sessions"),
      totalMessages: this.firstNumber("SELECT COUNT(*) AS value FROM messages"),
      messagesLast15m: this.firstNumber("SELECT COUNT(*) AS value FROM messages WHERE timestamp >= ?", now - 15 * 60 * 1000),
      messagesLastHour: this.firstNumber("SELECT COUNT(*) AS value FROM messages WHERE timestamp >= ?", now - 60 * 60 * 1000),
      totalSightings: this.firstNumber("SELECT COUNT(*) AS value FROM player_sightings"),
      sightingsLast15m: this.firstNumber("SELECT COUNT(*) AS value FROM player_sightings WHERE timestamp >= ?", now - 15 * 60 * 1000),
      sightingsLastHour: this.firstNumber("SELECT COUNT(*) AS value FROM player_sightings WHERE timestamp >= ?", now - 60 * 60 * 1000),
      uniqueSeenPlayers: this.firstNumber("SELECT COUNT(DISTINCT seen_uuid) AS value FROM player_sightings"),
      joinsLastHour: this.firstNumber("SELECT COUNT(*) AS value FROM presence_events WHERE action = 'join' AND timestamp >= ?", now - 60 * 60 * 1000),
      averageSessionMinutes: this.firstNumber("SELECT AVG((COALESCE(disconnected_at, ?) - connected_at) / 60000.0) AS value FROM sessions", now),
      busiestServer: this.firstString("SELECT server_address AS value FROM sessions GROUP BY server_address ORDER BY COUNT(*) DESC, MAX(connected_at) DESC LIMIT 1"),
      latestMessageAt,
      latestSightingAt,
      topServers: this.topServers()
    };
  }

  private roleCounts(): { clients: number; viewers: number } {
    let clients = 0;
    let viewers = 0;

    for (const ws of this.ctx.getWebSockets()) {
      const attachment = readAttachment(ws);
      if (attachment?.role === "client") clients++;
      else if (attachment?.role === "viewer") viewers++;
    }

    return { clients, viewers };
  }

  private firstNumber(query: string, ...params: SqlStorageValue[]): number {
    const row = [...this.ctx.storage.sql.exec<MetricRow>(query, ...params)][0];
    const value = row?.value;
    return typeof value === "number" && Number.isFinite(value) ? value : Number(value ?? 0) || 0;
  }

  private firstString(query: string, ...params: SqlStorageValue[]): string {
    const row = [...this.ctx.storage.sql.exec<MetricRow>(query, ...params)][0];
    return typeof row?.value === "string" ? row.value : "No server data yet";
  }

  private topServers(): Array<{ serverAddress: string; sessions: number; online: number }> {
    const onlineCounts = new Map<string, number>();
    for (const user of this.onlineUsers()) {
      onlineCounts.set(user.serverAddress, (onlineCounts.get(user.serverAddress) ?? 0) + 1);
    }

    const rows = [...this.ctx.storage.sql.exec<ServerMetricRow>(
      "SELECT server_address, COUNT(*) AS sessions FROM sessions GROUP BY server_address ORDER BY sessions DESC, MAX(connected_at) DESC LIMIT 6"
    )];

    return rows.map(row => ({
      serverAddress: row.server_address,
      sessions: Number(row.sessions ?? 0),
      online: onlineCounts.get(row.server_address) ?? 0
    }));
  }
}

interface DashboardStats {
  generatedAt: number;
  onlinePlayers: number;
  onlineServers: number;
  viewersOnline: number;
  clientsConnected: number;
  uniquePlayers: number;
  uniqueServers: number;
  totalSessions: number;
  totalMessages: number;
  messagesLast15m: number;
  messagesLastHour: number;
  totalSightings: number;
  sightingsLast15m: number;
  sightingsLastHour: number;
  uniqueSeenPlayers: number;
  joinsLastHour: number;
  averageSessionMinutes: number;
  busiestServer: string;
  latestMessageAt: number;
  latestSightingAt: number;
  topServers: Array<{ serverAddress: string; sessions: number; online: number }>;
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

function sanitizePlayerSighting(payload: JsonObject, fallbackServerAddress: string): PlayerSighting | null {
  const seenUsername = sanitizeUsername(payload.seenUsername);
  const seenUuid = sanitizeUuid(payload.seenUuid);
  const serverAddress = sanitizeServerAddress(payload.serverAddress) ?? fallbackServerAddress;
  const dimension = sanitizeLabel(payload.dimension, 120) ?? "unknown";
  const gameMode = sanitizeLabel(payload.gameMode, 32) ?? "unknown";

  if (!seenUsername || !seenUuid || !serverAddress) return null;

  return {
    seenUsername,
    seenUuid,
    serverAddress,
    dimension,
    x: finiteNumber(payload.x, 0, -30_000_000, 30_000_000),
    y: finiteNumber(payload.y, 0, -2048, 2048),
    z: finiteNumber(payload.z, 0, -30_000_000, 30_000_000),
    distance: finiteNumber(payload.distance, 0, 0, 30_000_000),
    health: finiteNumber(payload.health, 0, 0, 2048),
    maxHealth: finiteNumber(payload.maxHealth, 0, 0, 2048),
    ping: finiteInteger(payload.ping, -1, -1, 120_000),
    gameMode,
    sightingCount: finiteInteger(payload.sightingCount, 1, 1, 2_147_483_647),
    totalVisibleMs: finiteInteger(payload.totalVisibleMs, 0, 0, 31_536_000_000),
    visibleMs: finiteInteger(payload.visibleMs, 0, 0, 31_536_000_000)
  };
}

function sanitizeLabel(value: unknown, maxLength: number): string | null {
  if (typeof value !== "string") return null;
  const label = value.replace(/[\u0000-\u001f\u007f]/g, "").trim();
  if (!label) return null;
  return label.slice(0, maxLength);
}

function finiteNumber(value: unknown, fallback: number, min: number, max: number): number {
  const number = typeof value === "number" && Number.isFinite(value) ? value : fallback;
  return Math.max(min, Math.min(max, number));
}

function finiteInteger(value: unknown, fallback: number, min: number, max: number): number {
  return Math.round(finiteNumber(value, fallback, min, max));
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

const BLISS_LOGO_DATA_URI = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQQAAABMCAYAAACLdMm/AAAAAW9yTlQBz6J3mgAALPpJREFUeNrtnXmcFdWd6L+/U1X39r6xNPuOiIACCooruMU1mqiZaDTJJCaZmU8meZMx42SZyfImk8xLMpOY9yaL2VwSjRoT1yxqjAgquAAim4Bg0yzd9L7creqc3/uj7u1uEFAR7HZyv35a7lZ1T9W553fO+a1Cnt8s+C4nXjGddFd2hDFyiXO6zDZs2VIxa5ZK1lFRV8qGOxo49em/5p3C0vPO48xHHmHVPzxI2bEBySCBOmHy9YsHu2lFigxJ/MKDHScop+agV7QG9LMY+ag/adptma7cgz1tsiv00nrh09cPdnvfMI/ccjeprUm2f+BfELE4K4TqEM8NdtOKFBmymMIDm1RQRXGAlgl6miDfEY87Kofr7FxkWHPjzwa7va/LL6+9lsabl7LtkV0cP7mCoGwaLtQRTvV8D6kxzrz1LylS5H8o/aNDQfIPBZH8SyUKC1WYbHMGqZLD+Y63latvu43aMXW8/yNnkLNSazs3XWmQnwJfUaSy/yqLFCmyP30CIeMyqANRWkR5SJBmFUXFqYqqigOB//f1rw92m/vQWVeBKs9/+9c8/cMf8div72HT7bfTsrOLpq3tVYh+yxl+psIlBqqMihotCoQiRQ5Gn0Co8qu4Y8VWkpUl7Vj5Z8V8ROFXKuxVrDNVgF/BxZvSbPzCLYPdbh65/o/cWnM5L/34SYbXDmOcfxxT2uqYca0h6yyeJxWKLAQqAAREisuDIkUOyT4DZNsX72NsNkVDWS14liibqsD3Tglqykq8yuTK2skzm1ubt6EdbZimFvZMuoeTt/0W8xWv7xx7l+4lmUsSlUTUfrEWd47D+xfvTTfsQDw+fhaLG17iia/dxujK4VSOLCXwEvR0Z4crjLHObkh4EmaySYJkNEaU3wNzgG6BB3HyKaClbsMf2VA9mb0dI1BfuPSblw12PxQpMiTYRyCMrhjNrv+9mj8tvYHq5JnULpqEX5lMqnM3Izoc1Z8p9jHflbVFvuCynQzb9irpEaMY/dnLeeV3W0meHGK3eqgB9XNUtbXQgzJhyeK31NDHPvkYmzbuZslVU6lI5HB42ChXbUTORvgQaGjh+nS6t3N9wy4Wz583qqc3dS9oRoWfiXV/dCmaxYVqaqtIiiUwIaUjyll1858549c3DnZfFCky6Oyjct/dsxv5h3rO+c1t7G5pxpQZnEYG0RrgQoSfIOY2a3J/pVE4PDF6LG2zZ9NRO4xoRUR7roO6npEY54/xnD92U/VxdFdPhbI6Mh87g+a7f/2mG/j1j/8b2qbUTGrnPR+ayPAKxUVRnXO5K8RwC8LPgctAanwv4M/PLOf79/+44gs/+NbMxuY93wg8/688592GSTRRnVRTX4WmOkrCXObEVMbWd3SEJHrbB7sfihQZEhxyT7395kcBLRXx7lSRd8evKiAdqDwJequqe6xk9pj2MBlgWyJqZlaT2p25yhmuB+5C9KFqHbknlcggfgn1x1W8qQbqPS+RyvaSdobudG+VCGeL6HWKLgatiw0iAugfl6194cN3/um+mcaYDwKzwzC8bt6M4zZ89JIPkRBDJtedCLzEXJBrET0bp5+urip/7FcPLedvbv3Hwe6LIkUGHX/gk+X/shxrHCbaipHcoY6rQfRSYLGIeSLb3HtbMG74Y9WTalvDVBqEQJQlCGepyge7temnXlruUrrSPZ+/AzSHa++k8gefglFzkT2rgVjUdHz9KaKtS+lMVFGZrGFbWzOKM54x52O8jyuco6JVhYbE9lGltbtjzIMrHv9PT8zZRnWkQ3cZT7xhNcMh6k1aLzEv8BPXgFwGTABJqeAZNWR8ihQpwgCB0HTVrwjPALMrxEvOxFkhneoEAbPPOmKfJ5UOLjHJ4DQcS3vbu29V5x5TX1TAqZIUOFOQauBRYGd23Dgacx14Y0cQ/ccDNK6uZWldxLTh01i7Zyzl4YPk5s5DiBg58xwatz6NqBdY9KMq+p79L0ABYwzt3Z3HRVE4SwWxAqi4EZXDklcvvnR+Jsxdq3CZwCT6t0ma/ytSpEge/57/upVpY6fSs74X06aQo1qi3pNVdU2irqYp296mcAgrgSqC1BrlMkSW4MnjonQABimMOuNH+WeJVDelNldqslo+9Z+uaKn69m/JZX1S0SqS01dCTRl+S3eNCo51q7q0FNQIoL7sM34LWwUQEVCM5J2rVMH3vLKrz7/sxigMT1KYDAfeHzmgZLB7oUiRIYJ/xfFL6OjYi5vt0xMpjuxIxHxLRHZHHV0/NWKWKxzanUc1r1rQKoHLJFKrUeSJ76GeQY3EftFOaXUO9cwch3xu67fuv0/VPDxultfc1Ogg41dpW/ZM8fzrEH7dmtt5ly0dFq9Q3tBc3v+hkkRJbXlZxZVKv5ZhfwRoBy4H/ubo3F/DftuyQcLm/w6Ez37K5TeIAuFRaGvA4fmLOCA6yHseh5zV3jaifDuP5HUfUfxdbc0ViM4yzmzyPDoig3pQrapzRGSR4K1QZOxBzyBAZBGnqFf4XTnPRA6xDvU88L2kGipsqcGMqEJb20sFOQd4lwhP73k5uh2hw8A1iJwDUgv6WBhlDn6PBgoIp6hzfS+r9LtfF5p4sDtdCqw7evf3ZOBzxINusLYnHnALcMdB3v874CIOLjAOds71wBeB1BFsaxL4MjDvTbbHB/4E/B8OfJ8vIZb5gx3Z9i3g8QO8ngD+FTjpTV73EccXTytx+lUxdPrwExcEzS6KnKoiYiqBc2X/m1x4JvG+INzZgm3voXTkMEx1KY78IFSQyOJUJ7ik/x+a05+Xnzz9se4Hn3UiOOLxeDbCAiAUqMuf2YGo6IEnLi18twNnLbkwIkxncSgqihIrMPriM5QDSQRRkDKBtUfPnXkUcOnb1ZmH4OlDvHcC8K7DOOcwjvzqxwMWAWcdxrGd5HeMB3hvCnDBEW7r4XDnQV43+es+e7Ab6Ofv33DgfGBJsqT0hWw6Xe00GjiO+kaMOodahxiDGBP7BOcsUXcHPa09BMMqSIysRRIJCn2jqkkVLkNY7FLZR0qnjl6f3bZH4v2+olC5f8Nkv27dT3uQ1ch222w4zIaRABjV+NfgFD+ZYOq0aVRUVqAHFgoZYIVAQ1YtNdmjdn+VeJk42NsGd5jvHYroMI87FMrhz5CHOm4oKI9fT4k9qCuDQhv9vrk8bupwNZzvl5chYYTLhah1sY4ARSV+mMtkMQh+IkCCeGvmARpFZJvaYs3/lNGodXhW+8S2QjXKlaayNGWNlBh76N+i5A8UBxorJSOQNQK/tL2ZCVj39wKiRlARgiDJ5GnTOOW00zh+5ixMUw+atv3ThpJRWCXoHSrcJyI72nqz/PSi98EvPzPYnVGkyKDjG/LjPY8TAMFLBHi+j40iojBEo6hvLy4KRBGRtUjoIXbf+VvVEQUGTXhopBjrKKwGABTK9hOWBQVVYv8GOhQDCrpGkaWo3ltZeub29taH/tmIESNCOsohiYCrr7mOWXNmU1ZZgc3myGh3/J0ikPCaidw3iNzdE2dVNzZs6iFpfAIRnrh23OBrc4oUGQL4cPB1jBrBJAISgY+LQmw2BKcYiWdkcfkIAvbfoktsChQBX3CqaHcaSfhoMuhT/FEYrNCBchPCCcASoCpuV/8qS2Pl1V7QnqbUMhK+RybK8WrLHlZuXItWlvKhCz9BIpkk1LzNUwRXliBRW4lfVbbXBuZu0x020pZhwl+fyYLxoxi/4FR+M9i9UKTIEMGX/N76QEKhMMhVBC9I4PkBYSbD7l2N1CbLKPeT8WAfcIxBSKXTmFxIMhFP+KqK68lgnEJpAudcn8CIlZeSQ/ltpPY/fTHnInKdImkJPDwVjIhx2EXA+SLmSd/mfrHilbWTNjW+wuZdDXSnUhwz8zicWhyKKBgRkvV1SEkSSfrY/MrHVRvk3GP597P/necam3iusSgOihQp4Oe3AIfMG2KM6dtXZMVxy5/uJ+kM58xdyDFjJ1IWlKCuIBZilwPcviKmT4FoXbyFKCj78tZBBVNWaboq50+6d+8T25eCmOCGS8nevAIVURAVpBQ433j+afWjxsjmpgZRzXtJaLyVCSKNVy3W4ZWXoJ6HAD4GNf1Wi8//6fODfe+LFBly+NaSQXhBYapAleznheRQXt35KrXV1QyrqMYJtKZ72LhtM6sbNnHC5GM4e+4pHDd6EiV+gLNKWXkpyWQiNv0Rz9aUBEjCR8qSaFd6H5WroogIkYE9d2/HrzctINRm2ukxpuBY0IcnUj51zETGjxjF1t0NrHxxNeW1NSRCxdgQtUpssRSMCJlslt07d9LV3Y0fBPTcsoo9bietXd0YfBZ8+qrB7oci//M5lDvMUEH87t29HZVjKm4U4Y8OrhM4TaBWUQTBWsvt999Da287F591LsdOmYExBuMbunIplm1aw4sNmzlx8rGcMftEpo+ZQJXpm7TxIkVVMdXliGdwnvT5FysgyYCgrIREIkloDFOmtbKy+Tm21tfzh5tf4dKyE+KtBTIg52N8fNJLMGvCdKaMnoBLeCSsIHnzo0PIhTm2bNrC8uXLWL9+HalUD77vM+Ofv0FdcgYTJySRunHYn9yD99ErB7szBuKARqCXftt6AEzM/1vk7SECGoAsb30wK9BxFNrYDew8QudSv2K4KEZaw8jc5ZnoERtGl2PdN/0gGCb56IHeTIpn1j7H2q0bmTFpOnvamjESxxA4o3Rmenhs/bM8u3U9cycdwyUXXMz8KePwrOKFNlbueV7B9Bf/G/gkKksIKsrzpksB65OY1MoZf/slAL785S9DueYH+QG8i/IRVCV+Ai+RzJ873oukchnufuA3PPf8ClKpHkTAOeWvLroSSUfJjOuck0nrjqinvWlsOOSiGTLAp4Fl5C26wDjgXmKhUOTtoRW4FtjC4bl370/nUWjjE8AnOEJ+DP7oX53BxvvA/PQpKjKZ9o5dLUv9ZNCTLCsdlqitQMt8nIAJEmSjHGs3r6MkmcAYEy/7XcHHQOh1IXuiFN2ew2TDeNevsSlT8/OcyUZoBKUj6vCNh0phqLt4MOddE7q6uuj63+txmo4DqAx6MHOIA0zBT8IXNDCo5+PSmdhcKgZUdfK4iSXnnHT6yTZyH0JYLOjfq5Q2mcz2o9BPbwkl/jG2DHgtOFKdXuQN44BmYO9gN+QQZIDdHCHnK1/uF953xhIWzr6ED46ZSSCB8XNgs7309qSw5Qk0FwGKiMETyS/e42U8TqmvHc6w+pEsOfsczli4iBGVdUg67IsqMoALLS6Tw/TmwCq+7+ddiwsxjAKYPiXjmu89yuSF4wgoJ8zkfJfLJffdOOyHEUxpgigQrIEkCd5z5nnMGTuFZ9aupjeXqvnQ+675cuii0w1mIkgKxEMM2KPhdPeWMa/zvMjbwzvhvr/h8L/Xwwe4a9njnHr8u/P7fkVjmyDiFNuZgnSuzyegMKNbdQQmYOr4SXzgiquYeeJcampqMQ4IHSY/0FUEJxC2d2NylryHwD7N7/NkFIUSx6Lxc2n0dzCecQkxcnxQlrza+N7JNhvirH1NbIKI0NLWSne2nUnTpxKUlCACSa+EWROPYeqYiYS4sXXltdeI9hlZFTT2lZIjci+LFHnH0+djX9nTSqiCODrVYyMiE4ABM3IcOOTU4RuPqaPHs+TERSycOZf6mVMoqalGncZrWmMwvqJGsL5BrEVcbGrUfTSD9O0DVKBhzw6uuOFGXtixzv96on5O0pP3K3KFKlO8wBfjeURhiA2jOJJywKlaW/Zy+x33MeXYGZx25plUl1ZQmspS7gWUBgnKjC9iDcbEgspZS2QjfJNjU89gB8EVKTI06BMIkT8aiQOalFhzaVHx0Hih7lRJGo9poybyroVncurs+QyvqikUd0E1v43oTWMzIaa6AjUSm/9svM2Qg6z2BQg83531qYu9WWOmnXD8qGPet6N5z5XHTpo2yVm8gsVGPcV4CUzgY7tTuMiCZ5D8NiaV7mblyqdYu+5FhlXV8NcXXEFF9XCcVYyJFZPOKVEuxEYRTpUaP829rw52NxQpMjToEwjpkhBfBMTVgFkA8UBUwPcCZo6dzgULT2fRnHmMrKmNPZlcvHb31EBvjnRzG2FzO6aylKCuEjWCGMA7cPwx+VetWu/2h359wsxRU64SzFUqTM2FoZG8hiEOpxYED7CIJzgF25PG873YG1HzOg4jZNM99IiHX5LELy0hyuZwzhFms9gois2ggFHwnN0nlqNIkb9k+gRCPotA4Vl+iEjaCBsvP+v8UWPGjho9qnZYrGNw8ecN8UC1u9vJ9GZxmSxGLaaqBPEhjCI2b3qZdavWcOG806kqqdjfVJATkZeaWlp+f/8Tf/yk8bz5gEGlz4tDAKeOZc+toLqmmhmTJlOeSMZbE6doLsI6Jcpk4+2NmLxwEFQMJpEg8HxcGBJmsrEwkIKPpCISC4YiRYrsIxBANPYHEIxF9QVUf+4n/EdOmn38Nx16iQ6wBwxUbLquFJoNMSYekOqUjRs38Ic/PcozTz9DmZfgnFknQ2nsOARYBy8J3Okbc88nv/HFnp5czwUiYgQIfJ9k3q8AQNWxdvN6Njds5rhpM1g0f2E4pbq+24jUoirOOZy1fWFQeftHHkGMwfg+IrmCkMkCK4GGlI0YlxuSOoToAM8PV3QNhfRh70QK+SyGMgNn8rdMn0CYWQLhsCqCtKGkokz9ipIOSoLtLgzbw1TGQl7BuI9GPh+HUKiaqGCMx9r16/juQ3fQ3Bqb0StGjevPtBIfvl7ggyaK1t6z7DHtSHWM9H2vT4EpxuD7Hm4/U0Qql+b5DWtYt/VlnTJibO60Y+cypX4cJX6SAbaKPmsJTjGqfRmcDKQUXkC5XeCh3lR2Z28my4bhQ04g+MBlwDFxs1HiDEVVb+DYLcTOKgUM8PxgX9A7lHLgamAPR8bt+Ani/jmSTAE+xhESCn0C4YKxsGNCPZWZJM4Y3yFnG5hjEsGTmssdK9Eb/77WtjZa21oRI305EPb/iEG3WRG944G7CfwEImBdhBhDmAvp7U31+yjk5Y1zDt835HLpxIadW0dt27uLY0dPZOExc3DOoc7GuVwlVoKGmQxhJof4XiQiz6rILc7qw7XX79rR9dPRBMkEnoHPfeef+Px3h1QptyTw2cM89hniH0hxI/TWqQa+doTOpcAHOfIC4UTgR0fqZP2pvTzJWwUEBaMoDh0hKu+Vg5gHVBUiixbCmcnHIQlxbcfCf+pil0YUFQeIeEEg2Uyaz3/yRmymm70dbSxf8xwvblpHOp1FxEcwoA5fPE6Zu4CebC+v7m4gtBYRIRVleKFhIy/v2sak8ROYfMw0tm7dSiabifUL1hHlshAKBnaodWvQXHPq1unYkgzJqJWsjOFg11ekyF8Y2i8QggB1FgspRF4SZQRQ8pppRkFU0cjiwggNo1gg5GdzYwTxTOyQ1JcrobCnj5fz1kasWLMqeGzln1m4YDEXHT+PUOH0ExawZtMGHnlyKUkTYImdmgRh3qzjmTJ5Eqs3vMSK1c+xY1cjoQ0RI/SEacKE8NHrP8bLmzfz1JPLaNmxuz/JqnO+KleKsEC8xH1hLv0LL+WvcX51OKI0yy//9X6u+eq7B7szihQZdPoFwtq16NjFZCO3q8TI9UbkYoRrVeREhPKBK1CbDbHZXKyxL2QqEMGJ8mrLbtZufxlc7KBQ0OTHDknSa8Rb8ec1K5bddMePvmJt7rcPPP7g05f8aplzmYyWB2WcdvwCTpg+m8g6bOxGGIsaBzWlVSyefyrzjpnN2lc2svzFZ9nWsC3vWi0kyypZcMppzJ51PNvWbaRCSwemtjTEBVs+jeh7NAjvBf6rx5kdE1NHKlhsSFCsSFXksOkXCJ2d4AVgUa9SdtWfGt68+7HE70AvQfULiI4DwGlsy897HmIMVpTd7XtZuuF5nlq/il1dLahRvPyyXQVN2+zKhO//pKa09oH/uv0Hxxjc/Z5wRf3wMQ9+5jtfefirH7/B9yXAOaWipJTYzcHtF/IcP64ur+L0089gzhkns2r1KpY/uRTPS8TtsUppeQVz5s4j88oeSIcDTpB/JDoR+LgqvytPBDte7MjxP4j5wNf77xYG+CNx3YIiRQ5Jv0AQoX3LJuadN4MtKxvpfTBAurLNXlJeQlwHcfht/FFAxOBwNLTvYfn61Ty1cQ272puJxIER1EEZPseMG8/sGbP05l/+fPv6hq0vLt+yqmXhhJnTjQFERqly/Y5dOy/b3bq3bPzIsbFZspAr4aDNjt+tqaljyeJzmHfCiezZuSvOh2AVm88c27ddOYSOwCBk3VC3LL0pZuX/BpKmKBCKvAH6BcL4yxl+4WnsWP4imssEEunxXtJ8AOTdokwuTDcOxYljZ8dulm1YzVPr19DY2YIVhxgF50iox/RRE1gy6yQWzjie3jBnnl+/9srANwuWzFxwTxhmX4misM+CqepG5ML9ZumBEmE/6VBYE3v5bG01NXXUVtZAOgvZCPVNbITRQptjDha29hegUhxydtUiQ5MBBUR2knp2Iwkb1XlByY3A+xQmyoCEagJEarlv5Z95ZNUymjraYuWhEXCQwHDM6AmcNWcBC6bPora8ClGP3lwLYsQTkSmC3OAZvy3SqFylENgUKxvjoIj4u2w6CwJ+SfI1SWAF0DB6AugQOEudqyHvjkzk8G1s3TAJnygbxvkUipaEIkVelz6B0NsrmMBDMzJMRN4PTIB8AhQBPMH3fTRSnt70Ig1tzfiejxEhYXymjhzNWbNP5JTpcxhWWoUoRC5fuVn2qdtsRGS4iPTViC1UYpR8FmYUcqk0NpUhUVZKorwUSQYFk2h8RDq3zOXC75jAP1fEXCfoGapaiSriFIwQjBmGdKWIOrqRTPhOU7UdaFZ/J8TmD0WUI6tsPRr94Pb7961w2Pkb+wTCRqeMEiFnBINo32D1BC8I8JJ+bFJMRxjj4RmTL50GZ8+ez9WLzmdYRS2K9iVXjbtAcc6RTqfJ5UKCILGPEDAiOOd4/NnlGPGYMHIsvon9D7ARYXcPUSqNV5ogWVGOl/BxceJVmXja9JadS7fdaUv0987px4xnviqRLSlkdHaBhz+iClNThutM9dimjlexbrJA2VHo0CNJFvgG8BKx27EDRhAXBK0f7Ma9A/kdsbfhW8UB04AvEHsxHilCYkXwz3nrQssCVwDvP5yDfb1a2fzVmzA/HR+LPVVVg8buwwHi+xjfoCafKC1fz7GAqjJp+GjqKmuw1g1Y3g/Y+KuSy+XoTacIoijOliQGzzN4nocKPLpyGS9u2sAps+dx+ryFDPPiJCdxmjaL9qZxqSx+WQleRRlSEhACX/vev3PD5/6lA9Hn1fczXmhKKBSPyVeUNb6PN6xqB6H9G9vcMU/hOhGZRiHdw9Er9nq4RMAjwPIBr40GPkNRIBwOL+f/jgRzgX86wu2z7Otu/laZxuEKhA1fSlO75hTsQqG705iysvIJqlrm+/GKwAFO4gjBUC1bdr1Kd7qXQtrTfMgATgQrjp0dzXR2djJ7/FQQg1FB8u6LTpUwijDGkEwk8MXbR7i0trfw8NJHWLn2BeZOmM7CabMZO7weTzxMFMdw5HpSmEyOp7etO33msEln/faJh1bOXTA/fd65F4qKYo2HJgw2l6N55w5G1o+iJJHEqnPeuGHbvFG1y3Krt90nhgtRmqwxjEgfvWqvbwH/AM+HnOT6C2SwC/e+EQ57S+NXbd6GOEzgvKlV5VylErzfCcPjTCkOFQhdxJbt2/ndiid4+vmVdPV2x96I+UpsFmVHRzPPbVrHyi1rmTp8DMeNn4yJnY/jVIlGKE0mSSaSeF4+kGng4kiJdQ0GmjpbeejZ3Tyzfg0nzZjNopknMKGmHl88RBVjLTt2NZxuAnNrhVf24Ne+8fXbT5q/sKSurg7nYgViOpfhl7+4lerqWs48azFTpk4hWVIiURRSXu41dOxuujlRUSN7xePqB37INYPdhUWKDAF8SWWTavxPAB8HjmW/UFnrLHf9/n7uffxhmjvaEKA0mcRofjnuCSu2vMhzm9fS1NlOqBFTR47pO8vOrlaWbViFM1BaWorJC69C0FJkLdkwRy6Xo6ykhESQiDMgeYY9PW08+PxSVry8lnlTZ3LqcXOZMnw0vvj5ms9mgip/l0gk3v21//ja9r/96MfLJk+egpc/R29PDxs3bmD9hnVMnTqF6qpqwjDkjIolTDhupFOxBKUVpGur4woIRYr8heMbX2tVuRaY1afBH5Cs0Fll7csbaGprwfP6l/ge4HuCbzwa25pJePEWwzjBqqW5u4OVm9fzxEsv0NjSREVFGV6hliMQWksuCgmjEBtZMtkMqXSKkmSS8tL4s5IPY27qaeOeZx7lT+tWctas+Zw9e2GYjnLdQG2c7kDGbdn88rjv//d/c9L8Eznl1NOprasBAc+DTKqbtWtWxwlgBa778LEkN+8mqptJ1L6SssY1g90PRYoMCXxBRWOXoj76/YFi64VIXBKtEFngeR6B71NIgyCxuQCXL5TyUuMrbGrczo7WveRwBHlFpIpgbUQYRmRzWSLnKJxEAescPekUmUyGZDJBkAgIjJ+v5Aw7O1u4c/kjPLbmWRc5m3WiajB96ZW6Ojt4/E+PsuqF55kz53gqyyvxPB9nLZ4xmPyFbQyEd/3dtcjEYs2TIkUGYg6kpup7KZ841eWjDilUhjWCkX0Pdc6RzmXpTKdoaGtma+tuIlxfYtXIWlKZNF2pXnpzaSIcmNjrUVVVRGzh2yOU3myGrnSKnmyGnI3yeRVi56OW7o5kR2/36CiMTBRFWI0dkQTF84SOzjbWrFrFu047m6sufg+Txk4gEF+7e1M6ftQEjin1WfbrdehDDw/2/S9SZEjhFzx89/ESzisLjYJmQzSK8pkNCmlX839qcOrI5uIkppG12HwxlYGegVYdvelUX4VoASIcRoQxtSNYMHNOx9qtL/9i3ZZNJ4kx84GEGoNTIR2GWOtYfMJJNLU2s6lhO1mNYicmp1i1OGsZVl3DqLrhtHa05d2aldrSSmbPn8HcqXPY2rC9rrqi8viFc+e2dHa57OSqHp5b38a6Hy9j1vWnD3Y/HAj3Os+HAkOxTcVrfgu+DL7Z7wr7BEPGkuroprejE5cOKdRr6rsr6siFEblcnJMgKFRiem35xfznte89wTCuejiL5pzI4rknM75+TC4XhT8/82+u+nZVVeUVqnI1jjkYEmIMOOGUY45n1tjJPLXhRR5f8yybd79KzkWx67SzjKgZzseu/CAbt2xmxZpn6Umn4/LzCrVVtSyYXTtarfvv9rbuB1B+4SGrTlo0Ocdp09CybyLXHG6CoqOCIfY7GEe/Y9JYhpbJK0lcZ7KbN28ObeHtVeNWAbVH4DwWGMOR91QUYCRwJIqM2rdyrX5f0cW4XZFGrinb2jUi29mT0EyEddE+8kYRsmFIxuWIrMWpEpigkM0Q5+Jqz5732ryeqop1jgn1Y/nMX/01U+vH4+ODg8D47kuf+Pvtl9V/6Nvn3nTevWfOOum7O1qaLt3WvAuj4EfC8JIqLpp7GqfMOJ6HVi1fceefH0459GSgzIhhZF09k0+fyLxZc9mw5WUqSuMsz3EEJSLIJJS/R7hcPX6za1fHt6P7XmoYt3vI+fqUAN8lrtvX31cwarAbNoDjgId5czOm5D//aeCBt7GtVwJfepNtPRhJoOIIty8BfAtYzJFJ6lp9uAf6AKJYRF8W5M50e8/qTFP7TSjj4jqOMVLYKYiQCws1R/tzJlrniGyEsw6T90AciHMO6yxhFFHiJxg/YjRePjGqRUll03LhyWfxh8duY1Prq9te/vZ9u5q7O3hy3Qs8te4FPOPFZSCcMqykkutOveBPv3nyj//d43IXq+oHjMhJzrlSdUp9zQhGLBgRp25zmm+8DJzHxgMfMUYeVFPSYEzrEe7ft4wwtAb/gUiSj3c5DI6k2+8boeottPXtoNDf497qid4qvrGu14nchOqa+ivnv7Txi3dPNcazcRiCqkJj1uYqRKR2YNijdYpBmDJuIhh4dUdDv8vywBWFaqxbiKI4X7T251m0OF5tauSpdavYtGUzVSWVLD5+Pty1QTwVGV01jCsXncPiWfMpTSRx6vLnBFHRtc1bGy8/7qwfLtuy+v5hVdWXemI+ACxQ1VLt2+Tk68dpLJRMPtgq3wnxQ/dO2Bb+j2FAYHqRAQyJe+K/8tijXVPq/F+kF53L3m/8HvK5kkF2qMhvW7o7793Q9OqNiFygAs4pvuczZdRozj/ldBYvXMRdjz7MK69ujz0SiQOmHYq1FpvfVvTXcoTIRbzStIM161/i6XWrae5qxzlLWbKULXte5TeP/8H/3Ac+liwJ4i1VfUVdPk6qv/qzqHLrtTdL1mvSLpfZ/f3PfuPm3Xv3PqwqF6vR60DnA6V9X+wsNp1BPYOXSCKehxTspsWyBUWKAOCPW3QaGvhkXtmDF4UImgH5qaf6SJ1UPX/sd66QKXVjsw7w8Jk8ahznnHYWZ590KpOG14MoXr6GU37OxamSC0PcfjOvIa7bsLulme/96ha6urpwoognGBH3xBfuYspnlswIMFfdu+LPmWvPuPBRpyxSKNcBOQ0KKxWLz9kzTuZjt3yRr//8e/qhi69qTCXlh2U5fVBELgZzHXASSEm8vXFEOYsLHSYRIL7BSrhv/YciRf6C8cd9+EwA1FvAuq98kYSzuyL0P0YmR+duev5ugKTgMWnUWM49+UzOW3A64+pH54OV4rLvMnAvAXnt/r6DzIjgG4NnPCJnae3uyIdQC6qYTC6aMfWGJe8SI1eryoxfLP39//rEksu/1BNmLwauFTEng5ZBv5lU8kM515BDCUjMhl0/WE9Znd3ZuLP95prqit8b5GJBrnXIfFRKRGMLic3kQJQy5/FQY3HLUKQIDDRj1YAJynCSsyUjq216TBk3zP6wuerydx/7/Ob1I6aOn8ykkWMJVHBOcYXy8IEHxuQdgw5uffIDPx+clK+iZOL9vVMII1ebzeX+U5ARQGDFOafqfvD4fU3vPut9P62Kuh5C5SKEa4FF5LcCzmURlMSEBADuzjtp7FqCa1rDuLGVKqINvZ5+vzI0D+Dcpah8ADhJICnEuohAoL1jsLuhSJGhQb899dMfYeYN5zLqzFnIiCpfRGd1OPevNZW1vzh/wZkLp9SPj/f/6uJ5OfCgohQZXomWBvlMRw61LraE7jfpiuxrunUooYvIRjlsFPmqOgYI4ndjZ+o/rHuK7//2e0R4TaX/9/Gf4ey1in4KZSmq2boKT1Rt/8W8//2c/PF6TrHnMa7pFHLvraUsSlLi+407mrb8ANw1oP8IPAVkBKRN4MpiVHGRIsDAFUJLFQ2PvEjQlSpPJoJPgnwImAFinM0nR8nXSdSKEkxFCSQ9rHE4AXWK8XymTJ1C/ah6nlnxTJ9VoJAuEelT+JMLo75thSvUbSjUnM7/38fjtuW/498ufC+Z689CfG93GIz+cZBu/B1I9aSxtSq+fc1FyQ3xGRrHPk5mY5I94xzDaycqubChrCTx/1KZ7P1q9CLQPda3lOYsRwlhaDgTeYf53tHAcOh8t4fbnkMdNxQk/uulNRsKmm3p/7FOaIEoiZj0KI1Doae89noAI/gVpbikhzOxa2LgJZg2aTrnn3ce55x3DuvWr2PFyhX9hhR97Z3QfPyzkk+yuv93aWwuFIEL/vla6oD/u1vxf3IXfnnVTpPwdk4aW0X1yDEHvbpx710Sf9dFF8HDD9PGiXT98JsEdaU72vZsvzmZrJGQHNGvbj5aN7gRuIPB7WwDrD3E+yuI7fRHTSruhwKvHuS9iDhdfMubbI9HnF3qYNrhTcDdDL5pb/tBXrf5625/k9d9xOkXCL4rWIgNgleY0IEdIAlgVCEcuW+EO0UULjr3Qt57yXsYM3YMXmBYt25dYcTHHEQuFrwbrbWvUUJK/j0BVuVfq/7tzSTXV9I0tRmiBF/9p22cPjX5uhcpD8dBTC/d+iOyt3di5jRQM2uYM57BlJbj11fB0Sne9Bxw3VE585vjUFrTm4EfD5H25IB/4/Bm9EMlUf0d8Ie3+RrfzHUXcioO+kqmXyCo5IurGlTEqdAgTu4Ffou4zwIXQ35OV4c4xcs5TOiYPmEyLoi9FtXlk6sN2AIo8bbADLhiR95h6QDCoPA9FrfPenvM3378NZ/78Ytv/GJnf/BEAD5lPkX6+TTzZB5tZW18cedzR+v+KoMs8d8AQ83EcjTaU+yHN0j/eGsvx/kRVugW+CHK0py1z/nijHie7RNdvkGsw6RDxAIKkbM4NQiiKFtaWlta1LmF7L9UzusarLVEzmIHVI0+EEdrfXfT728azHtepMiQpV+589XrcVELubQ0aSTf8irc00kjoYgxIoIGHlJdjhlWBSKIzZd3NwqCFWUL8K2En3jfLbf/7EdO++ujxSY+RxRFcbq0KHZaKtZOKVJkaNG/QqjuREwCCaxisLkWD1Pno2GEX1mOSQbgeygOE8WrLyUuiCLK/Q7+LWHNi7+487aos6fnOCNefrzrTkXWhmF0mqqrjD0ai5KgSJGhSJ9AkM4auGTfNxuf2RorB70gNhcWAgeJsyO7wGADz2Hk8aBCXli/dhO33n4HIj4IO1X1UVRvVTRjjPkNSOVgX3CRIkUOziFt5M7E+3gjeX1HbCXcYxOmGiNlGudKVEEIaxy7djSRTAiacutQPhLmsitqK+vS3Zne43izKgGVuAFFihR52zjkiIuLsCqKOmIb6rcVPuwS3gb1TCGeSUClzMDJc09h+ZYX2N7SuGZs9fg/l5WUpU3sofh6e4QM0PPal4tbiyJF3k4OuUIwoiAa4fR24P+4VPZZKU1WihLGnxCAJoW9yZ4kod/NuOph5NSxZtdqujNp5o6dedDzK4SCrlblDmKnjKtFOB0oU0E8cbiiUChS5G3jkAJh05gxnDu6LFT4ddMrDagmQDS/qpAmhYdAbwd9JrMrQVjdS2PnG8o+lEV1NXCXQ+9tamt+9cbzP6I/f/b+hwS5UJCrFe0x4oqhyUWKvI0cUiDMat1Lc1uWxk5Be31MtSNOly73A89Hzi0vrRjWm+ttwREh4SFncw+wqK5SuFOdu3d7+87t00dO0pqKGr7z5J0MK6vYO2XqvFs3v/zcH0CidM6BCwf7HhUp8hfDIQXC6DmT+h6rKo0rtoCjS8R9O6oszZVEhsaWlymbfAK1reuZPW/+a84RoTghFFgpqk+q6q+bOlsbpo8Yr9Oqx/By83ZMPrNBe6qDMNVNddXwJgXOPmYRG3ZtHOx7VKTIXwxveIOuquxcuQXyCVG8rLJlexO7R0/lyhsvQZ5//oDHTamfRDaXLTHGDN/b1rJr4ogxrjvdy67u5sG+9iJFiuzH/weFoIA21AwOLQAAAABJRU5ErkJggg==";

const INDEX_HTML = `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Bliss Client Dashboard</title>
  <style>
    :root {
      color-scheme: dark;
      --bg: #070b12;
      --surface: rgba(10, 15, 22, 0.9);
      --surface-2: rgba(13, 19, 27, 0.92);
      --surface-3: rgba(18, 25, 35, 0.88);
      --line: rgba(148, 163, 184, 0.16);
      --line-strong: rgba(99, 215, 255, 0.34);
      --accent: #63d7ff;
      --accent-2: #7cf2bd;
      --blue: #63d7ff;
      --green: #8af0a4;
      --amber: #ffd36a;
      --text: #f4f7fb;
      --muted: #a8b5c6;
      --soft: #748195;
      --shadow: rgba(0, 0, 0, 0.42);
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
        radial-gradient(circle at 12% -4%, rgba(99, 215, 255, 0.18), transparent 34%),
        radial-gradient(circle at 84% 12%, rgba(124, 242, 189, 0.13), transparent 30%),
        linear-gradient(180deg, rgba(12, 20, 31, 0.92), rgba(7, 11, 18, 0.98) 46%, var(--bg)),
        repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.03) 0 1px, transparent 1px 64px),
        repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.018) 0 1px, transparent 1px 64px);
    }

    main {
      width: min(1380px, calc(100vw - 28px));
      min-height: 100vh;
      margin: 0 auto;
      padding: 22px 0;
    }

    .app {
      display: grid;
      grid-template-columns: minmax(0, 1fr) 360px;
      gap: 14px;
    }

    .topbar,
    .panel,
    .metric {
      border: 1px solid var(--line);
      background: var(--surface);
      border-radius: 8px;
      box-shadow: 0 20px 56px var(--shadow), inset 0 1px 0 rgba(255, 255, 255, 0.03);
      backdrop-filter: blur(18px);
    }

    .workspace {
      display: grid;
      gap: 14px;
      min-width: 0;
    }

    .topbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 18px;
      padding: 16px 18px;
      border-color: var(--line-strong);
      background: linear-gradient(90deg, rgba(99, 215, 255, 0.12), rgba(124, 242, 189, 0.07)), var(--surface);
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 14px;
      min-width: 0;
    }

    .brand-logo {
      width: 112px;
      height: auto;
      object-fit: contain;
      padding: 6px 8px;
      border: 1px solid rgba(255, 255, 255, 0.7);
      border-radius: 6px;
      background: rgba(244, 247, 251, 0.94);
      filter: drop-shadow(0 8px 18px rgba(99, 215, 255, 0.22));
    }

    h1,
    h2,
    h3,
    p {
      margin: 0;
      letter-spacing: 0;
    }

    h1 {
      font-size: 1.25rem;
      line-height: 1.1;
      font-weight: 860;
    }

    .eyeline {
      margin-top: 4px;
      color: var(--muted);
      font-size: 0.86rem;
    }

    .status {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      min-width: 126px;
      justify-content: center;
      padding: 8px 11px;
      border: 1px solid var(--line);
      background: rgba(0, 0, 0, 0.18);
      border-radius: 8px;
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

    .metrics {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: 12px;
    }

    .metric {
      min-height: 118px;
      padding: 14px;
      display: grid;
      align-content: space-between;
      gap: 12px;
    }

    .metric.feature {
      grid-column: span 2;
      border-color: var(--line-strong);
      background: linear-gradient(135deg, rgba(99, 215, 255, 0.18), rgba(124, 242, 189, 0.10)), var(--surface);
    }

    .metric-label {
      color: var(--muted);
      font-size: 0.78rem;
      font-weight: 760;
      text-transform: uppercase;
      letter-spacing: 0.04em;
    }

    .metric-value {
      font-size: clamp(1.55rem, 3vw, 2.55rem);
      line-height: 0.95;
      font-weight: 900;
      font-variant-numeric: tabular-nums;
    }

    .metric-value.compact {
      font-size: 1.08rem;
      line-height: 1.15;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .metric-note {
      color: var(--soft);
      font-size: 0.82rem;
      line-height: 1.3;
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .metric.online .metric-value {
      color: var(--green);
    }

    .metric.chat .metric-value {
      color: var(--accent);
    }

    .dashboard-grid {
      display: grid;
      grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
      gap: 14px;
    }

    .panel {
      min-width: 0;
      padding: 16px;
      background: var(--surface-2);
    }

    .panel-header {
      display: flex;
      align-items: start;
      justify-content: space-between;
      gap: 12px;
      margin-bottom: 14px;
    }

    h2 {
      color: var(--text);
      font-size: 1rem;
      line-height: 1.2;
    }

    .panel-sub {
      color: var(--soft);
      font-size: 0.8rem;
      margin-top: 4px;
    }

    .chip {
      flex: 0 0 auto;
      color: #dff8ff;
      border: 1px solid rgba(99, 215, 255, 0.28);
      background: rgba(99, 215, 255, 0.12);
      border-radius: 999px;
      padding: 5px 8px;
      font-size: 0.75rem;
      font-weight: 900;
    }

    .server-list,
    .activity-list,
    .users,
    .messages {
      display: grid;
      gap: 9px;
    }

    .server-row {
      display: grid;
      grid-template-columns: minmax(0, 1fr) 76px;
      gap: 12px;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    }

    .server-row:last-child {
      border-bottom: 0;
    }

    .server-main {
      min-width: 0;
    }

    .server-name {
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-weight: 820;
      color: var(--text);
    }

    .server-meta {
      margin-top: 6px;
      height: 6px;
      border-radius: 8px;
      background: rgba(148, 163, 184, 0.12);
      overflow: hidden;
    }

    .server-bar {
      height: 100%;
      width: 0;
      background: linear-gradient(90deg, var(--accent), var(--accent-2));
      border-radius: inherit;
      box-shadow: 0 0 14px rgba(99, 215, 255, 0.22);
    }

    .server-count {
      color: var(--muted);
      font-size: 0.8rem;
      text-align: right;
      font-variant-numeric: tabular-nums;
    }

    .activity-list {
      grid-template-columns: repeat(8, minmax(0, 1fr));
      align-items: end;
      min-height: 150px;
      padding-top: 12px;
    }

    .bar {
      min-width: 0;
      display: grid;
      gap: 8px;
      align-items: end;
    }

    .bar-fill {
      min-height: 8px;
      height: 8px;
      border-radius: 8px 8px 2px 2px;
      background: linear-gradient(180deg, var(--accent-2), rgba(99, 215, 255, 0.68));
    }

    .bar span {
      color: var(--soft);
      font-size: 0.68rem;
      text-align: center;
      white-space: nowrap;
    }

    .side {
      display: grid;
      gap: 14px;
      min-width: 0;
      align-content: start;
    }

    .user,
    .message {
      border: 1px solid rgba(148, 163, 184, 0.14);
      background: rgba(7, 12, 19, 0.56);
      border-radius: 8px;
    }

    .user {
      display: grid;
      grid-template-columns: minmax(0, 1fr) auto;
      gap: 10px;
      align-items: center;
      padding: 10px 11px;
      cursor: help;
    }

    .user strong,
    .name {
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: var(--accent);
      font-weight: 900;
    }

    .server {
      color: var(--muted);
      font-size: 0.78rem;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      max-width: 150px;
    }

    .messages {
      max-height: 460px;
      overflow: auto;
    }

    .message {
      display: grid;
      grid-template-columns: 128px minmax(0, 1fr);
      gap: 12px;
      align-items: start;
      padding: 12px 13px;
    }

    .message:hover,
    .user:hover {
      border-color: var(--line-strong);
      background: rgba(99, 215, 255, 0.08);
    }

    .text {
      min-width: 0;
      overflow-wrap: anywhere;
      color: var(--text);
      line-height: 1.42;
    }

    .time {
      margin-top: 3px;
      color: var(--soft);
      font-size: 0.75rem;
    }

    .empty {
      color: var(--muted);
      padding: 22px;
      text-align: center;
      border: 1px dashed var(--line);
      border-radius: 8px;
    }

    .footer-line {
      color: var(--soft);
      font-size: 0.78rem;
      text-align: right;
      padding: 2px 2px 0;
    }

    @media (max-width: 1060px) {
      .app,
      .dashboard-grid {
        grid-template-columns: 1fr;
      }

      .metrics {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }
    }

    @media (max-width: 640px) {
      main {
        width: min(100vw - 18px, 1380px);
        padding: 9px 0;
      }

      .topbar {
        align-items: start;
        flex-direction: column;
      }

      .brand-logo {
        width: 96px;
      }

      .metrics,
      .metric.feature {
        grid-template-columns: 1fr;
        grid-column: auto;
      }

      .message {
        grid-template-columns: 1fr;
      }

      .activity-list {
        grid-template-columns: repeat(4, minmax(0, 1fr));
      }
    }
  </style>
</head>
<body>
  <main>
    <section class="app">
      <div class="workspace">
        <header class="topbar">
          <div class="brand">
            <img class="brand-logo" src="${BLISS_LOGO_DATA_URI}" alt="Bliss Client">
            <div>
              <h1>Bliss Client Control Room</h1>
              <p class="eyeline">Online-mode clients, server activity, and verified chat telemetry.</p>
            </div>
          </div>
          <div class="status" id="status"><span class="dot"></span><span id="statusText">Connecting</span></div>
        </header>

        <section class="metrics" aria-label="Bliss client metrics">
          <article class="metric feature online">
            <div class="metric-label">Online players</div>
            <div class="metric-value" id="onlinePlayers">0</div>
            <div class="metric-note" id="onlineNote">Waiting for verified clients</div>
          </article>
          <article class="metric">
            <div class="metric-label">Active servers</div>
            <div class="metric-value" id="onlineServers">0</div>
            <div class="metric-note">Live server spread</div>
          </article>
          <article class="metric chat">
            <div class="metric-label">Messages</div>
            <div class="metric-value" id="totalMessages">0</div>
            <div class="metric-note" id="messageNote">No chat yet</div>
          </article>
          <article class="metric">
            <div class="metric-label">15 min chat</div>
            <div class="metric-value" id="messagesLast15m">0</div>
            <div class="metric-note">Recent velocity</div>
          </article>
          <article class="metric">
            <div class="metric-label">Verified accounts</div>
            <div class="metric-value" id="uniquePlayers">0</div>
            <div class="metric-note">Unique Mojang profiles</div>
          </article>
          <article class="metric">
            <div class="metric-label">Known servers</div>
            <div class="metric-value" id="uniqueServers">0</div>
            <div class="metric-note">Where Bliss is used</div>
          </article>
          <article class="metric">
            <div class="metric-label">Sessions</div>
            <div class="metric-value" id="totalSessions">0</div>
            <div class="metric-note" id="sessionNote">Average 0m</div>
          </article>
          <article class="metric">
            <div class="metric-label">Joins / hour</div>
            <div class="metric-value" id="joinsLastHour">0</div>
            <div class="metric-note">Fresh client connects</div>
          </article>
          <article class="metric">
            <div class="metric-label">Messages / hour</div>
            <div class="metric-value" id="messagesLastHour">0</div>
            <div class="metric-note">Chat throughput</div>
          </article>
          <article class="metric">
            <div class="metric-label">Seen reports</div>
            <div class="metric-value" id="totalSightings">0</div>
            <div class="metric-note" id="sightingNote">No sightings yet</div>
          </article>
          <article class="metric">
            <div class="metric-label">Seen / hour</div>
            <div class="metric-value" id="sightingsLastHour">0</div>
            <div class="metric-note">Tracker throughput</div>
          </article>
          <article class="metric">
            <div class="metric-label">Unique seen</div>
            <div class="metric-value" id="uniqueSeenPlayers">0</div>
            <div class="metric-note">Shared player graph</div>
          </article>
          <article class="metric">
            <div class="metric-label">Dashboard viewers</div>
            <div class="metric-value" id="viewersOnline">0</div>
            <div class="metric-note">Open operator tabs</div>
          </article>
          <article class="metric">
            <div class="metric-label">Client sockets</div>
            <div class="metric-value" id="clientsConnected">0</div>
            <div class="metric-note">Authenticated sockets</div>
          </article>
          <article class="metric">
            <div class="metric-label">Busiest server</div>
            <div class="metric-value compact" id="busiestServer">None yet</div>
            <div class="metric-note" id="lastActivity">No activity</div>
          </article>
          <article class="metric">
            <div class="metric-label">Average session</div>
            <div class="metric-value compact" id="avgSessionValue">0m</div>
            <div class="metric-note">Closed and live sessions</div>
          </article>
          <article class="metric">
            <div class="metric-label">Last chat</div>
            <div class="metric-value compact" id="latestMessageMetric">None</div>
            <div class="metric-note">Message recency</div>
          </article>
          <article class="metric">
            <div class="metric-label">Stats updated</div>
            <div class="metric-value compact" id="generatedMetric">Pending</div>
            <div class="metric-note">Worker telemetry sync</div>
          </article>
        </section>

        <section class="dashboard-grid">
          <article class="panel">
            <div class="panel-header">
              <div>
                <h2>Server Mix</h2>
                <p class="panel-sub">Most common servers seen from verified Bliss sessions.</p>
              </div>
              <span class="chip" id="serverChip">0 live</span>
            </div>
            <div class="server-list" id="servers">
              <div class="empty">No server sessions yet.</div>
            </div>
          </article>

          <article class="panel">
            <div class="panel-header">
              <div>
                <h2>Chat Activity</h2>
                <p class="panel-sub">Recent message distribution from dashboard history.</p>
              </div>
              <span class="chip" id="activityChip">0 msgs</span>
            </div>
            <div class="activity-list" id="activity"></div>
          </article>
        </section>

        <article class="panel">
          <div class="panel-header">
            <div>
              <h2>Verified Chat Stream</h2>
              <p class="panel-sub">Names are online-mode verified. Hover names for the server address.</p>
            </div>
            <span class="chip">Live feed</span>
          </div>
          <div class="messages" id="messages">
            <div class="empty" id="empty">No messages yet.</div>
          </div>
        </article>

        <div class="footer-line" id="generatedAt">Metrics pending</div>
      </div>

      <aside class="side">
        <article class="panel">
          <div class="panel-header">
            <div>
              <h2>Online Players</h2>
              <p class="panel-sub">Current authenticated Bliss clients.</p>
            </div>
            <span class="chip" id="onlineChip">0 online</span>
          </div>
          <div class="users" id="users">
            <div class="empty">No players online.</div>
          </div>
        </article>

        <article class="panel">
          <div class="panel-header">
            <div>
              <h2>Usage Snapshot</h2>
              <p class="panel-sub">Session and chat health from the Worker.</p>
            </div>
          </div>
          <div class="server-list">
            <div class="server-row">
              <div class="server-main">
                <div class="server-name">Online density</div>
                <div class="server-meta"><div class="server-bar" id="densityBar"></div></div>
              </div>
              <div class="server-count" id="densityText">0%</div>
            </div>
            <div class="server-row">
              <div class="server-main">
                <div class="server-name">Chat recency</div>
                <div class="server-meta"><div class="server-bar" id="recencyBar"></div></div>
              </div>
              <div class="server-count" id="recencyText">idle</div>
            </div>
            <div class="server-row">
              <div class="server-main">
                <div class="server-name">Server concentration</div>
                <div class="server-meta"><div class="server-bar" id="serverSpreadBar"></div></div>
              </div>
              <div class="server-count" id="serverSpreadText">0%</div>
            </div>
          </div>
        </article>
      </aside>
    </section>
  </main>
  <script>
    const messagesEl = document.getElementById("messages");
    const usersEl = document.getElementById("users");
    const serversEl = document.getElementById("servers");
    const activityEl = document.getElementById("activity");
    const statusEl = document.getElementById("status");
    const statusText = document.getElementById("statusText");
    const generatedAtEl = document.getElementById("generatedAt");
    const messages = new Map();
    let onlineUsers = [];
    let stats = {};
    let ws;

    function connect() {
      const scheme = location.protocol === "https:" ? "wss:" : "ws:";
      ws = new WebSocket(scheme + "//" + location.host + "/chat");

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
        } else if (payload.type === "stats") {
          updateStats(payload.stats || {});
        }
      });
    }

    function setStatus(text, state) {
      statusText.textContent = text;
      statusEl.className = "status " + state;
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
      renderActivity();
    }

    function renderUsers(users) {
      onlineUsers = users;
      usersEl.replaceChildren();
      document.getElementById("onlineChip").textContent = users.length + " online";
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
      updateStats(stats);
    }

    function updateStats(nextStats) {
      stats = Object.assign({}, stats, nextStats);
      const onlinePlayers = number(stats.onlinePlayers, onlineUsers.length);
      const onlineServers = number(stats.onlineServers, new Set(onlineUsers.map(user => user.serverAddress)).size);
      const uniquePlayers = number(stats.uniquePlayers);
      const uniqueServers = number(stats.uniqueServers);
      const totalSessions = number(stats.totalSessions);
      const totalMessages = number(stats.totalMessages, messages.size);
      const messagesLast15m = number(stats.messagesLast15m);
      const messagesLastHour = number(stats.messagesLastHour);
      const totalSightings = number(stats.totalSightings);
      const sightingsLastHour = number(stats.sightingsLastHour);
      const uniqueSeenPlayers = number(stats.uniqueSeenPlayers);
      const joinsLastHour = number(stats.joinsLastHour);
      const viewersOnline = number(stats.viewersOnline);
      const clientsConnected = number(stats.clientsConnected, onlinePlayers);
      const averageSessionMinutes = number(stats.averageSessionMinutes);

      setText("onlinePlayers", formatNumber(onlinePlayers));
      setText("onlineServers", formatNumber(onlineServers));
      setText("uniquePlayers", formatNumber(uniquePlayers));
      setText("uniqueServers", formatNumber(uniqueServers));
      setText("totalSessions", formatNumber(totalSessions));
      setText("totalMessages", formatNumber(totalMessages));
      setText("messagesLast15m", formatNumber(messagesLast15m));
      setText("messagesLastHour", formatNumber(messagesLastHour));
      setText("totalSightings", formatNumber(totalSightings));
      setText("sightingsLastHour", formatNumber(sightingsLastHour));
      setText("uniqueSeenPlayers", formatNumber(uniqueSeenPlayers));
      setText("joinsLastHour", formatNumber(joinsLastHour));
      setText("viewersOnline", formatNumber(viewersOnline));
      setText("clientsConnected", formatNumber(clientsConnected));
      setText("busiestServer", stats.busiestServer || "None yet");
      setText("onlineNote", onlinePlayers ? onlineServers + " live server" + plural(onlineServers) : "Waiting for verified clients");
      setText("messageNote", stats.latestMessageAt ? "Last message " + relativeTime(stats.latestMessageAt) : "No chat yet");
      setText("sightingNote", stats.latestSightingAt ? "Last report " + relativeTime(stats.latestSightingAt) : "No sightings yet");
      setText("sessionNote", "Average " + formatMinutes(averageSessionMinutes));
      setText("lastActivity", stats.latestMessageAt ? "Latest chat " + formatTime(stats.latestMessageAt) : "No activity");
      setText("avgSessionValue", formatMinutes(averageSessionMinutes));
      setText("latestMessageMetric", stats.latestMessageAt ? relativeTime(stats.latestMessageAt) : "None");
      setText("generatedMetric", stats.generatedAt ? formatTime(stats.generatedAt) : "Pending");
      setText("serverChip", onlineServers + " live");
      setText("activityChip", messages.size + " msgs");
      if (generatedAtEl) generatedAtEl.textContent = stats.generatedAt ? "Updated " + formatTime(stats.generatedAt) : "Metrics pending";

      renderServers(stats.topServers || []);
      renderSnapshot(stats);
      renderActivity();
    }

    function renderServers(servers) {
      serversEl.replaceChildren();
      if (!servers.length) {
        const empty = document.createElement("div");
        empty.className = "empty";
        empty.textContent = "No server sessions yet.";
        serversEl.append(empty);
        return;
      }

      const maxSessions = Math.max(1, ...servers.map(server => number(server.sessions)));
      for (const server of servers) {
        const row = document.createElement("div");
        row.className = "server-row";
        row.title = server.serverAddress || "unknown";

        const main = document.createElement("div");
        main.className = "server-main";
        const name = document.createElement("div");
        name.className = "server-name";
        name.textContent = server.serverAddress || "unknown";
        const meta = document.createElement("div");
        meta.className = "server-meta";
        const bar = document.createElement("div");
        bar.className = "server-bar";
        bar.style.width = Math.max(6, Math.round(number(server.sessions) / maxSessions * 100)) + "%";
        meta.append(bar);
        main.append(name, meta);

        const count = document.createElement("div");
        count.className = "server-count";
        count.textContent = formatNumber(number(server.sessions)) + " ses";
        if (number(server.online)) count.textContent += " / " + server.online + " live";
        row.append(main, count);
        serversEl.append(row);
      }
    }

    function renderActivity() {
      activityEl.replaceChildren();
      const now = Date.now();
      const bucketMs = 15 * 60 * 1000;
      const buckets = Array.from({ length: 8 }, (_, index) => ({
        start: now - (8 - index) * bucketMs,
        end: now - (7 - index) * bucketMs,
        count: 0
      }));

      for (const message of messages.values()) {
        const timestamp = number(message.timestamp);
        const bucket = buckets.find(item => timestamp >= item.start && timestamp < item.end);
        if (bucket) bucket.count++;
      }

      const max = Math.max(1, ...buckets.map(bucket => bucket.count));
      for (const bucket of buckets) {
        const row = document.createElement("div");
        row.className = "bar";
        const fill = document.createElement("div");
        fill.className = "bar-fill";
        fill.style.height = Math.max(8, Math.round(bucket.count / max * 132)) + "px";
        const label = document.createElement("span");
        label.textContent = bucket.count.toString();
        row.append(fill, label);
        activityEl.append(row);
      }
    }

    function renderSnapshot(currentStats) {
      const online = number(currentStats.onlinePlayers, onlineUsers.length);
      const uniquePlayers = Math.max(1, number(currentStats.uniquePlayers, online));
      const density = Math.min(100, Math.round(online / uniquePlayers * 100));
      setBar("densityBar", density);
      setText("densityText", density + "%");

      const latest = number(currentStats.latestMessageAt);
      const minutesSince = latest ? Math.max(0, (Date.now() - latest) / 60000) : 999;
      const recency = latest ? Math.max(4, Math.round(100 - Math.min(100, minutesSince * 8))) : 0;
      setBar("recencyBar", recency);
      setText("recencyText", latest ? relativeTime(latest) : "idle");

      const uniqueServers = Math.max(1, number(currentStats.uniqueServers));
      const onlineServers = number(currentStats.onlineServers);
      const spread = Math.min(100, Math.round(onlineServers / uniqueServers * 100));
      setBar("serverSpreadBar", spread);
      setText("serverSpreadText", spread + "%");
    }

    let presenceTimer = 0;
    function requestPresenceSoon() {
      clearTimeout(presenceTimer);
      presenceTimer = setTimeout(() => {
        if (ws?.readyState === WebSocket.OPEN) ws.send(JSON.stringify({ type: "viewer" }));
      }, 120);
    }

    function syncEmpty() {
      const emptyEl = document.getElementById("empty");
      if (emptyEl) emptyEl.remove();
      if (!messages.size) {
        const empty = document.createElement("div");
        empty.className = "empty";
        empty.id = "empty";
        empty.textContent = "No messages yet.";
        messagesEl.append(empty);
      }
    }

    function setText(id, value) {
      const element = document.getElementById(id);
      if (element) element.textContent = value;
    }

    function setBar(id, value) {
      const element = document.getElementById(id);
      if (element) element.style.width = Math.max(0, Math.min(100, value)) + "%";
    }

    function number(value, fallback = 0) {
      const parsed = Number(value);
      return Number.isFinite(parsed) ? parsed : fallback;
    }

    function formatNumber(value) {
      return new Intl.NumberFormat().format(Math.round(number(value)));
    }

    function formatMinutes(value) {
      const minutes = Math.max(0, Math.round(number(value)));
      if (minutes < 60) return minutes + "m";
      const hours = Math.floor(minutes / 60);
      const rest = minutes % 60;
      return hours + "h " + rest + "m";
    }

    function relativeTime(timestamp) {
      if (!timestamp) return "never";
      const seconds = Math.max(0, Math.round((Date.now() - timestamp) / 1000));
      if (seconds < 60) return "just now";
      const minutes = Math.round(seconds / 60);
      if (minutes < 60) return minutes + "m ago";
      const hours = Math.round(minutes / 60);
      if (hours < 24) return hours + "h ago";
      return Math.round(hours / 24) + "d ago";
    }

    function plural(value) {
      return value === 1 ? "" : "s";
    }

    function formatTime(timestamp) {
      if (!timestamp) return "";
      return new Intl.DateTimeFormat(undefined, { hour: "2-digit", minute: "2-digit" }).format(new Date(timestamp));
    }

    updateStats({});
    renderActivity();
    connect();
  </script>
</body>
</html>`;
