/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.morphe.extension.instagram.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiActivity;

import app.morphe.extension.shared.ResourceType;
import app.morphe.extension.shared.ResourceUtils;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.instagram.settings.BaseActivityHook;
import app.morphe.extension.instagram.settings.preference.InstagramPreferenceFragment;
import app.morphe.extension.instagram.settings.search.InstagramSearchViewController;

/**
 * Hooks {@link GoogleApiActivity} to inject a custom {@link InstagramPreferenceFragment}
 * with a toolbar and search functionality.
 */
@SuppressWarnings("deprecation")
public class InstagramActivityHook extends BaseActivityHook {

    /**
     * How much time has passed since the first launch of the app. Simple check to prevent
     * forcing bold icons on first launch where the settings menu is partially broken
     * due to missing icon resources the client has not yet received.
     */
    private static final long MINIMUM_TIME_AFTER_FIRST_LAUNCH_BEFORE_ALLOWING_BOLD_ICONS = 30 * 1000; // 30 seconds.

    private static final boolean USE_BOLD_ICONS = false;

    static {
        Utils.setAppIsUsingBoldIcons(USE_BOLD_ICONS);
    }

    private static int currentThemeMode = Integer.MIN_VALUE; // Must initially be a non-valid value.

    /**
     * Controller for managing search view components in the toolbar.
     */
    @SuppressLint("StaticFieldLeak")
    public static InstagramSearchViewController searchViewController;

    /**
     * Injection point.
     */
    @SuppressWarnings("unused")
    public static void initialize(Activity parentActivity) {
        BaseActivityHook.initialize(new InstagramActivityHook(), parentActivity);
    }

    /**
     * Customizes the activity theme based on dark/light mode.
     */
    @Override
    protected void customizeActivityTheme(Activity activity) {
        final var theme = Utils.isDarkModeEnabled()
                ? "Theme.Instagram"
                : "Theme.Instagram";
        activity.setTheme(ResourceUtils.getIdentifierOrThrow(ResourceType.STYLE, theme));
    }

    /**
     * Returns the toolbar background color based on dark/light mode.
     */
    @Override
    protected int getToolbarBackgroundColor() {
        final String colorName = Utils.isDarkModeEnabled()
                ? "bds_black"
                : "bds_white";
        return Utils.getColorFromString(colorName);
    }

    /**
     * Returns the navigation icon drawable for the toolbar.
     */
    @Override
    protected Drawable getNavigationIcon() {
        return InstagramPreferenceFragment.getBackButtonDrawable();
    }

    /**
     * Returns the click listener for the navigation icon.
     */
    @Override
    protected View.OnClickListener getNavigationClickListener(Activity activity) {
        return view -> {
            if (searchViewController != null && searchViewController.isSearchActive()) {
                searchViewController.handleBackPress();
            } else {
                activity.finish();
            }
        };
    }

    /**
     * Adds search view components to the toolbar for {@link InstagramPreferenceFragment}.
     *
     * @param activity The activity hosting the toolbar.
     * @param toolbar  The configured toolbar.
     * @param fragment The PreferenceFragment associated with the activity.
     */
    @Override
    protected void onPostToolbarSetup(Activity activity, Toolbar toolbar, PreferenceFragment fragment) {
        if (fragment instanceof InstagramPreferenceFragment) {
            searchViewController = InstagramSearchViewController.addSearchViewComponents(
                    activity, toolbar, (InstagramPreferenceFragment) fragment);
        }
    }

    /**
     * Creates a new {@link InstagramPreferenceFragment} for the activity.
     */
    @Override
    protected PreferenceFragment createPreferenceFragment() {
        return new InstagramPreferenceFragment();
    }

    /**
     * Injection point.
     * <p>
     * Updates dark/light mode since Instagram settings can force light/dark mode
     * which can differ from the global device settings.
     */
    @SuppressWarnings("unused")
    public static void updateLightDarkModeStatus(int mode) {
        if (currentThemeMode != mode) {
            currentThemeMode = mode;
            // Instagram: -1 system, 1 light, 2 dark
            if (mode == -1) {
                Utils.setIsDarkModeEnabled(false); // cannot represent system so default to light. // FIXME
            } else {
                Utils.setIsDarkModeEnabled(mode == 2);
            }
        }
    }

    /**
     * Injection point.
     * <p>
     * Overrides {@link Activity#finish()} of the injection Activity.
     *
     * @return if the original activity finish method should be allowed to run.
     */
    @SuppressWarnings("unused")
    public static boolean handleBackPress() {
        if (searchViewController != null && searchViewController.isSearchActive()) {
            return searchViewController.handleBackPress();
        }
        return false;
    }

    /**
     * Injection point.
     */
    @SuppressWarnings("unused")
    public static boolean useBoldIcons(boolean original) {
        return USE_BOLD_ICONS;
    }
}
