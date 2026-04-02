package app.morphe.extension.instagram.ui;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import app.morphe.extension.instagram.utils.dialog.DialogUtils;
import app.morphe.extension.instagram.utils.core.ExtensionContextBridge;
import app.morphe.extension.shared.Utils;

public final class HomeLongPressMenu {
    private static final ThreadLocal<Boolean> BYPASS_NEXT_LONG_CLICK = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    private HomeLongPressMenu() {
    }

    public static boolean handleHomeLongPress(
            Object listener,
            View anchor
    ) {
        if (!(listener instanceof View.OnLongClickListener originalListener) || anchor == null) {
            return false;
        }

        if (consumeBypassLongClick()) {
            return false;
        }

        return showMenu(anchor, originalListener);
    }

    private static boolean showMenu(
            View anchor,
            View.OnLongClickListener originalListener
    ) {
        Context context = resolveMenuContext(anchor);
        if (context == null) {
            return true;
        }

        try {
            boolean isDeveloperOptionsPatchEnabled =
                    ExtensionContextBridge.isDeveloperOptionsPatchEnabled();

            List<CharSequence> options = new ArrayList<>();
            options.add("Morphe Settings");
            if (isDeveloperOptionsPatchEnabled) {
                options.add("Developer Options");
            }

            CharSequence[] optionItems = options.toArray(new CharSequence[0]);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Home long press")
                    .setItems(optionItems, (d, which) -> {
                        if (which == 0) {
                            DialogUtils.showMorpheSettingsDialog(context);
                            return;
                        }

                        if (isDeveloperOptionsPatchEnabled && which == 1) {
                            openInternalSettings(anchor, originalListener);
                        }
                    })
                    .setOnCancelListener(ignored -> {
                    })
                    .create();

            dialog.show();
            return true;
        } catch (Throwable ignored) {
            try {
                DialogUtils.showMorpheSettingsDialog(context);
                return true;
            } catch (Throwable ignoredToo) {
                return true;
            }
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

    private static boolean openInternalSettings(View anchor, View.OnLongClickListener originalListener) {
        if (originalListener == null) {
            return false;
        }

        try {
            BYPASS_NEXT_LONG_CLICK.set(Boolean.TRUE);
            return originalListener.onLongClick(anchor);
        } catch (Throwable ignored) {
            return false;
        } finally {
            BYPASS_NEXT_LONG_CLICK.set(Boolean.FALSE);
        }
    }

    private static boolean consumeBypassLongClick() {
        Boolean bypass = BYPASS_NEXT_LONG_CLICK.get();
        if (!Boolean.TRUE.equals(bypass)) {
            return false;
        }

        BYPASS_NEXT_LONG_CLICK.set(Boolean.FALSE);
        return true;
    }
}
