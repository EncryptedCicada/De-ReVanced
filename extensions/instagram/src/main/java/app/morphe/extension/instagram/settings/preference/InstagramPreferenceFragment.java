package app.morphe.extension.instagram.settings.preference;

import android.app.Dialog;
import android.preference.PreferenceScreen;
import android.widget.Toolbar;

import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.instagram.settings.preference.ToolbarPreferenceFragment;
import app.morphe.extension.instagram.settings.InstagramActivityHook;

/**
 * Preference fragment for Morphe settings.
 */
@SuppressWarnings("deprecation")
public class InstagramPreferenceFragment extends ToolbarPreferenceFragment {
    /**
     * The main PreferenceScreen used to display the current set of preferences.
     */
    private PreferenceScreen preferenceScreen;

    /**
     * Initializes the preference fragment.
     */
    @Override
    protected void initialize() {
        super.initialize();

        try {
            preferenceScreen = getPreferenceScreen();
            Utils.sortPreferenceGroups(preferenceScreen);
            setPreferenceScreenToolbar(preferenceScreen);

        } catch (Exception ex) {
            Logger.printException(() -> "initialize failure", ex);
        }
    }

    /**
     * Called when the fragment starts.
     */
    @Override
    public void onStart() {
        super.onStart();
        try {
            // Initialize search controller if needed.
            if (InstagramActivityHook.searchViewController != null) {
                // Trigger search data collection after fragment is ready.
                InstagramActivityHook.searchViewController.initializeSearchData();
            }
        } catch (Exception ex) {
            Logger.printException(() -> "onStart failure", ex);
        }
    }

    /**
     * Sets toolbar for all nested preference screens.
     */
    @Override
    protected void customizeToolbar(Toolbar toolbar) {
        InstagramActivityHook.setToolbarLayoutParams(toolbar);
    }

    /**
     * Perform actions after toolbar setup.
     */
    @Override
    protected void onPostToolbarSetup(Toolbar toolbar, Dialog preferenceScreenDialog) {
        if (InstagramActivityHook.searchViewController != null
                && InstagramActivityHook.searchViewController.isSearchActive()) {
            toolbar.post(() -> InstagramActivityHook.searchViewController.closeSearch());
        }
    }

    /**
     * Returns the preference screen for external access by SearchViewController.
     */
    public PreferenceScreen getPreferenceScreenForSearch() {
        return preferenceScreen;
    }
}
