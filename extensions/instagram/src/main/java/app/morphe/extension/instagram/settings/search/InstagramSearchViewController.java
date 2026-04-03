package app.morphe.extension.instagram.settings.search;

import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Toolbar;

import app.morphe.extension.instagram.settings.search.BaseSearchResultItem;
import app.morphe.extension.instagram.settings.search.BaseSearchResultsAdapter;
import app.morphe.extension.instagram.settings.search.BaseSearchViewController;
import app.morphe.extension.instagram.settings.preference.InstagramPreferenceFragment;

/**
 * Instagram-specific search view controller implementation.
 */
@SuppressWarnings("deprecation")
public class InstagramSearchViewController extends BaseSearchViewController {

    public static InstagramSearchViewController addSearchViewComponents(Activity activity, Toolbar toolbar,
                                                                      InstagramPreferenceFragment fragment) {
        return new InstagramSearchViewController(activity, toolbar, fragment);
    }

    private InstagramSearchViewController(Activity activity, Toolbar toolbar, InstagramPreferenceFragment fragment) {
        super(activity, toolbar, new PreferenceFragmentAdapter(fragment));
    }

    @Override
    protected BaseSearchResultsAdapter createSearchResultsAdapter() {
        return new InstagramSearchResultsAdapter(activity, filteredSearchItems, fragment, this);
    }

   @Override
   protected boolean isSpecialPreferenceGroup(Preference preference) {
       return false; // No special handling for any groups in Instagram settings.
   }

    @Override
    protected void setupSpecialPreferenceListeners(BaseSearchResultItem item) {
    }

    // Static method for Activity finish.
    public static boolean handleFinish(InstagramSearchViewController searchViewController) {
        if (searchViewController != null && searchViewController.isSearchActive()) {
            searchViewController.closeSearch();
            return true;
        }
        return false;
    }

    // Adapter to wrap InstagramPreferenceFragment to BasePreferenceFragment interface.
    private record PreferenceFragmentAdapter(InstagramPreferenceFragment fragment) implements BasePreferenceFragment {
        @Override
        public PreferenceScreen getPreferenceScreenForSearch() {
            return fragment.getPreferenceScreenForSearch();
        }

        @Override
        public View getView() {
            return fragment.getView();
        }

        @Override
        public Activity getActivity() {
            return fragment.getActivity();
        }
    }
}
