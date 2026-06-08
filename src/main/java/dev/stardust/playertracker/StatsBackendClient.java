package dev.stardust.playertracker;

import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;

import java.util.LinkedHashMap;
import java.util.Map;

final class StatsBackendClient {
    private static final String SIGHTINGS_URL = "https://blissclientbackend.hogridersupercell123.workers.dev/sightings";

    private StatsBackendClient() {
    }

    static void report(PlayerTracker.SightingReport report) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("reporterUsername", report.reporterUsername());
        payload.put("reporterUuid", report.reporterUuid());
        payload.put("reporterServerAddress", report.reporterServerAddress());
        payload.put("seenUsername", report.seenUsername());
        payload.put("seenUuid", report.seenUuid());
        payload.put("serverAddress", report.serverAddress());
        payload.put("dimension", report.dimension());
        payload.put("x", report.x());
        payload.put("y", report.y());
        payload.put("z", report.z());
        payload.put("distance", report.distance());
        payload.put("health", report.health());
        payload.put("maxHealth", report.maxHealth());
        payload.put("ping", report.ping());
        payload.put("gameMode", report.gameMode());
        payload.put("sightingCount", report.sightingCount());
        payload.put("totalVisibleMs", report.totalVisibleMs());
        payload.put("visibleMs", report.visibleMs());
        payload.put("averageDistance", report.averageDistance());
        payload.put("speed", report.speed());
        payload.put("heatmapX", report.heatmapX());
        payload.put("heatmapZ", report.heatmapZ());

        MeteorExecutor.execute(() -> Http.post(SIGHTINGS_URL).ignoreExceptions().bodyJson(payload).sendResponse());
    }
}
