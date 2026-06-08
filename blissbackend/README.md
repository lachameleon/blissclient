# Bliss Backend

Cloudflare Workers backend for Bliss Client tracker stats.

## Run Locally

```sh
npm install
npm run dev
```

Deploy with:

```sh
npm run deploy
```

## API

- `POST /sightings` accepts player tracker reports from the Minecraft client.
- `GET /stats` returns the dashboard summary JSON.
- `/` serves the dashboard and polls `/stats`.

The Minecraft client posts to `https://blissclientbackend.hogridersupercell123.workers.dev/sightings`.
