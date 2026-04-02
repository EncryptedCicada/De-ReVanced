package app.morphe.extension.instagram.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import app.morphe.extension.instagram.utils.dialog.DialogUtils;
import app.morphe.extension.shared.Utils;

public final class MorpheSettings {

    private MorpheSettings() {
    }

    public static boolean handleHomeLongPress(
            Object listener,
            View anchor
    ) {
        if (anchor == null) {
            return false;
        }

        Context context = resolveMenuContext(anchor);
        if (context == null) {
            return false;
        }

        try {
            DialogUtils.showMorpheSettingsDialog(context);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Context resolveMenuContext(View anchor) {
        Context anchorContext = anchor.getContext();
        if (anchorContext instanceof Activity) {
            return anchorContext;
        }

        Activity currentActivity = Utils.getActivity();
        if (currentActivity != null) {
            return currentActivity;
        }

        return anchorContext;
    }
}
