package app.morphe.extension.instagram.network;

import java.net.URI;

import app.morphe.extension.instagram.utils.core.InstagramSettings;

public class Interceptor {

    public static URI nullifyRequest(URI uri) {
        if (uri == null) {
            return null;
        }

        if (shouldNullify(uri)) {
            return sanitize(uri);
        }

        return uri;
    }

    private static boolean shouldNullify(URI uri) {
        String path = uri.getPath();
        String host = uri.getHost();

        if (path == null) {
            return false;
        }

        if (path.endsWith("/screenshot/") ||
                path.endsWith("/ephemeral_screenshot/")) {
            return InstagramSettings.GHOST_SCREENSHOT.get();
        }

        if (path.endsWith("/item_replayed/") ||
                (path.contains("/direct") && path.endsWith("/item_seen/"))) {
            return InstagramSettings.GHOST_VIEW_ONCE.get();
        }

        if (path.contains("/api/v2/media/seen/")) {
            return InstagramSettings.GHOST_STORY.get();
        }

        if (path.contains("/heartbeat_and_get_viewer_count/")) {
            return InstagramSettings.GHOST_LIVE.get();
        }

        if (path.contains("profile_ads/get_profile_ads/") ||
                path.contains("/async_ads/") ||
                path.equals("/api/v1/ads/graphql/")) {
            return InstagramSettings.AD_BLOCK_ENABLED.get();
        }

        if (host != null &&
                (host.contains("graph.instagram.com") ||
                        host.contains("graph.facebook.com"))) {
            return InstagramSettings.ANALYTICS_BLOCKED.get();
        }

        if (path.contains("/logging_client_events")) {
            return InstagramSettings.ANALYTICS_BLOCKED.get();
        }

        return false;
    }

    private static URI sanitize(URI original) {
        try {
            return new URI(
                    "https",
                    "127.0.0.1",
                    "/404",
                    null
            );
        } catch (Exception e) {
            return original;
        }
    }
}