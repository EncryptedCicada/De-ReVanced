package app.morphe.extension.instagram.ui;

import android.app.Activity;

import app.morphe.extension.shared.Utils;

public class UIHookManager {
    private static Activity currentActivity;

    private UIHookManager() {
    }

    public static Activity getCurrentActivity() {
        if (currentActivity != null) {
            return currentActivity;
        }

        return Utils.getActivity();
    }

    public static void setupHooks(Activity activity) {
        setCurrentActivity(activity);
    }

    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
        if (activity != null) {
            Utils.setActivity(activity);
        }
    }
}