package app.morphe.extension.instagram.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.morphe.extension.shared.settings.Setting.parent;
import app.morphe.extension.shared.settings.BaseSettings;
import app.morphe.extension.shared.settings.BooleanSetting;
import app.morphe.extension.shared.settings.EnumSetting;
import app.morphe.extension.shared.settings.FloatSetting;
import app.morphe.extension.shared.settings.IntegerSetting;
import app.morphe.extension.shared.settings.LongSetting;
import app.morphe.extension.shared.settings.StringSetting;
import app.morphe.extension.shared.settings.Setting;

public class Settings extends BaseSettings {
    public static final BooleanSetting SETTINGS_INITIALIZED = new BooleanSetting("morphe_settings_initialized", FALSE, false, false);

    public static final BooleanSetting ENABLE_DEVELOPER_OPTIONS = new BooleanSetting("morphe_enable_developer_options", FALSE, true, true);

    public static final BooleanSetting ENABLE_GHOST_MODE = new BooleanSetting("morphe_enable_ghost_mode", FALSE, false, true);
    public static final BooleanSetting DISABLE_READ_RECEIPTS = new BooleanSetting("morphe_disable_read_receipts", FALSE, false, parent(ENABLE_GHOST_MODE));
    public static final BooleanSetting DISABLE_TYPING_STATUS = new BooleanSetting("morphe_disable_typing_status", FALSE, false, parent(ENABLE_GHOST_MODE));
    public static final BooleanSetting ENABLE_GHOST_SCREENSHOT = new BooleanSetting("morphe_enable_ghost_screenshot", FALSE, false, parent(ENABLE_GHOST_MODE));
    public static final BooleanSetting ENABLE_GHOST_VIEW_ONCE = new BooleanSetting("morphe_enable_ghost_view_once", FALSE, false, parent(ENABLE_GHOST_MODE));
    public static final BooleanSetting ENABLE_GHOST_STORY_VIEW = new BooleanSetting("morphe_enable_ghost_story_view", FALSE, false, parent(ENABLE_GHOST_MODE));
    public static final BooleanSetting ENABLE_GHOST_LIVE_VIEW = new BooleanSetting("morphe_enable_ghost_live_view", FALSE, false, parent(ENABLE_GHOST_MODE));

    public static final BooleanSetting ENABLE_PRIVACY_OPTIONS = new BooleanSetting("morphe_enable_privacy_options", TRUE, false, true);
    public static final BooleanSetting ENABLE_AD_BLOCK = new BooleanSetting("morphe_enable_ad_block", TRUE, false, parent(ENABLE_PRIVACY_OPTIONS));
    public static final BooleanSetting DISABLE_ANALYTICS = new BooleanSetting("morphe_disable_analytics", TRUE, false, parent(ENABLE_PRIVACY_OPTIONS));
    public static final BooleanSetting SANITIZE_SHARING_LINKS = new BooleanSetting("morphe_sanitize_sharing_links", TRUE, false, parent(ENABLE_PRIVACY_OPTIONS));

    public static final BooleanSetting SHOW_FOLLOWER_TOAST = new BooleanSetting("morphe_show_follower_toast", FALSE, false, true);
}