package app.morphe.extension.instagram.utils.core;

import app.morphe.extension.shared.settings.BooleanSetting;

public final class InstagramSettings {
    private InstagramSettings() {
    }

    public static final BooleanSetting DEV_ENABLED =
            new BooleanSetting("morphe_instagram_dev_enabled", false);

    public static final BooleanSetting GHOST_SEEN =
            new BooleanSetting("morphe_instagram_ghost_seen", false);
    public static final BooleanSetting GHOST_TYPING =
            new BooleanSetting("morphe_instagram_ghost_typing", false);
    public static final BooleanSetting GHOST_SCREENSHOT =
            new BooleanSetting("morphe_instagram_ghost_screenshot", false);
    public static final BooleanSetting GHOST_VIEW_ONCE =
            new BooleanSetting("morphe_instagram_ghost_view_once", false);
    public static final BooleanSetting GHOST_STORY =
            new BooleanSetting("morphe_instagram_ghost_story", false);
    public static final BooleanSetting GHOST_LIVE =
            new BooleanSetting("morphe_instagram_ghost_live", false);

    public static final BooleanSetting AD_BLOCK_ENABLED =
            new BooleanSetting("morphe_instagram_ad_block_enabled", false);
    public static final BooleanSetting ANALYTICS_BLOCKED =
            new BooleanSetting("morphe_instagram_analytics_blocked", false);
    public static final BooleanSetting TRACKING_LINKS_DISABLED =
            new BooleanSetting("morphe_instagram_tracking_links_disabled", false);

    public static final BooleanSetting SHOW_FOLLOWER_TOAST =
            new BooleanSetting("morphe_instagram_show_follower_toast", false);
}
