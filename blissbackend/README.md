# Bliss Backend

Cloudflare Workers backend for Bliss Chat.

## Run locally

```sh
npm install
npm run dev
```

For local backend testing, set the BlissChat module `backend-url` setting to `ws://localhost:8787/chat`. Deploy with:

```sh
npm run deploy
```

The Minecraft client defaults to `wss://blissclientbackend.hogridersupercell123.workers.dev/chat`.

## Auth model

Minecraft clients cannot send chat until they pass Mojang online-mode verification:

1. The Worker Durable Object sends a random `challenge`.
2. The client calls Minecraft's session service `joinServer(profileId, accessToken, challenge)`.
3. The Worker checks Mojang `hasJoined?username=<name>&serverId=<challenge>`.
4. Only verified profiles can publish messages.

The backend displays the server address the player reports from their current multiplayer connection. It does not expose the player's network client IP.
