/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.morphe.extension.instagram.patches;

import java.util.ArrayList;
import java.util.List;

import app.morphe.extension.instagram.settings.Settings;

@SuppressWarnings("unused")
public final class HideAdsPatch {
    private static final boolean HIDE_ADS = Settings.ENABLE_AD_BLOCK.get();

    /**
     * @return If this patch was included during patching.
     */
    public static boolean isPatchIncluded() {
        return false;  // Modified during patching.
    }

    /**
     * Injection point.
     */
    public static boolean hideAds() {
        return HIDE_ADS;
    }

    /**
     * Injection point.
     */
    public static boolean hideAds(boolean original) {
        return HIDE_ADS || original;
    }
}
