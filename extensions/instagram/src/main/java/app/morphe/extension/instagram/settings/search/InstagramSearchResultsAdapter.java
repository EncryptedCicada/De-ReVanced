package app.morphe.extension.instagram.settings.search;

import android.content.Context;
import android.preference.PreferenceScreen;

import java.util.List;

/**
 * YouTube-specific search results adapter.
 */
public class InstagramSearchResultsAdapter extends BaseSearchResultsAdapter {

    public InstagramSearchResultsAdapter(Context context, List<BaseSearchResultItem> items,
                                       BaseSearchViewController.BasePreferenceFragment fragment,
                                       BaseSearchViewController searchViewController) {
        super(context, items, fragment, searchViewController);
    }

    @Override
    protected PreferenceScreen getMainPreferenceScreen() {
        return fragment.getPreferenceScreenForSearch();
    }
}
