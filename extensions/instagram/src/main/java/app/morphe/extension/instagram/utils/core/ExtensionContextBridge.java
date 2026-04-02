package app.morphe.extension.instagram.utils.core;

import android.content.Context;

import app.morphe.extension.shared.Utils;

public class ExtensionContextBridge {
    private static volatile boolean developerOptionsPatchEnabled;

    private ExtensionContextBridge() {
    }

    public static void setContext(Context context) {
        if (context == null) {
            return;
        }

        Utils.setContext(context);
    }

    public static void setDeveloperOptionsPatchEnabled() {
        developerOptionsPatchEnabled = true;
    }

    public static boolean isDeveloperOptionsPatchEnabled() {
        return developerOptionsPatchEnabled;
    }
}