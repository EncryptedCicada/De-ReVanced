package app.morphe.extension.instagram.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
//import static app.morphe.extension.shared.patches.CustomBrandingPatch.BrandingTheme;
//import static app.morphe.extension.shared.patches.CustomBrandingPatch.NotificationIconTheme;
//import static app.morphe.extension.shared.settings.Setting.migrateOldSettingToNew;
import static app.morphe.extension.shared.settings.Setting.parent;
import app.morphe.extension.shared.settings.BaseSettings;
import app.morphe.extension.shared.settings.BooleanSetting;
import app.morphe.extension.shared.settings.EnumSetting;
import app.morphe.extension.shared.settings.FloatSetting;
import app.morphe.extension.shared.settings.IntegerSetting;
import app.morphe.extension.shared.settings.LongSetting;
import app.morphe.extension.shared.settings.StringSetting;
import app.morphe.extension.shared.settings.Setting;

//import app.morphe.extension.shared.patches.CustomBrandingPatch;
//import app.morphe.extension.shared.spoof.SpoofVideoStreamsPatch.JavaScriptClientAvailability;
//import app.morphe.extension.shared.spoof.SpoofVideoStreamsPatch.JavaScriptHashAvailability;
//import app.morphe.extension.shared.spoof.js.JavaScriptVariant;

/**
 * Settings shared by YouTube and YouTube Music.
 * <p>
 * To ensure this class is loaded when the UI is created, app specific setting bundles should extend
 * or reference this class.
 */
public class SharedInstagramSettings extends BaseSettings {
    public static final BooleanSetting SETTINGS_INITIALIZED = new BooleanSetting("morphe_settings_initialized", FALSE, false, false);

    public static final BooleanSetting SETTINGS_SEARCH_HISTORY = new BooleanSetting("morphe_settings_search_history", TRUE, true);
    public static final StringSetting SETTINGS_SEARCH_ENTRIES = new StringSetting("morphe_settings_search_entries", "");

    public static final BooleanSetting SANITIZE_SHARING_LINKS = new BooleanSetting("morphe_sanitize_sharing_links", TRUE);
}
