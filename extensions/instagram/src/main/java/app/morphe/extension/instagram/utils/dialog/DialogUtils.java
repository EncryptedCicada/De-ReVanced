package app.morphe.extension.instagram.utils.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Pair;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import app.morphe.extension.instagram.ui.UIHookManager;
import app.morphe.extension.instagram.utils.core.ExtensionContextBridge;
import app.morphe.extension.instagram.utils.core.InstagramSettings;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.shared.ui.CustomDialog;
import app.morphe.extension.shared.ui.Dim;

public class DialogUtils {
    private static Dialog currentDialog;

    private DialogUtils() {
    }

    public static void showEclipseOptionsDialog(Context context) {
        showMorpheSettingsDialog(context);
    }

    public static void showMorpheSettingsDialog(Context context) {
        if (context == null) {
            return;
        }

        ExtensionContextBridge.setContext(context);
        dismissCurrentDialog();

        Pair<Dialog, LinearLayout> pair = CustomDialog.create(
                context,
                "Morphe Settings",
                "Instagram options",
                null,
                "Close",
                DialogUtils::dismissCurrentDialog,
                null,
                null,
                null,
                true
        );

        addMenuButton(context, pair.second, "👻 Ghost mode", () -> showGhostOptions(context));
        addMenuButton(context, pair.second, "🛡 Ads & Analytics", () -> showAdOptions(context));
        addMenuButton(context, pair.second, "⚙ Misc", () -> showMiscOptions(context));

        showDialog(context, pair.first);
    }

    public static void showSimpleDialog(Context context, String title, String message) {
        if (context == null) {
            return;
        }

        dismissCurrentDialog();

        Pair<Dialog, LinearLayout> pair = CustomDialog.create(
                context,
                title,
                message,
                null,
                "OK",
                DialogUtils::dismissCurrentDialog,
                null,
                null,
                null,
                true
        );

        showDialog(context, pair.first);
    }

    private static void showGhostOptions(Context context) {
        showToggleDialog(
                context,
                "Ghost mode",
                new String[]{
                        "Hide Seen",
                        "Hide Typing",
                        "Disable Screenshot Detection",
                        "Hide View Once",
                        "Hide Story Seen",
                        "Hide Live Seen"
                },
                new boolean[]{
                        InstagramSettings.GHOST_SEEN.get(),
                        InstagramSettings.GHOST_TYPING.get(),
                        InstagramSettings.GHOST_SCREENSHOT.get(),
                        InstagramSettings.GHOST_VIEW_ONCE.get(),
                        InstagramSettings.GHOST_STORY.get(),
                        InstagramSettings.GHOST_LIVE.get()
                },
                values -> {
                    InstagramSettings.GHOST_SEEN.save(values[0]);
                    InstagramSettings.GHOST_TYPING.save(values[1]);
                    InstagramSettings.GHOST_SCREENSHOT.save(values[2]);
                    InstagramSettings.GHOST_VIEW_ONCE.save(values[3]);
                    InstagramSettings.GHOST_STORY.save(values[4]);
                    InstagramSettings.GHOST_LIVE.save(values[5]);
                }
        );
    }

    private static void showAdOptions(Context context) {
        showToggleDialog(
                context,
                "Ads & Analytics",
                new String[]{
                        "Block Ads",
                        "Block Analytics",
                        "Disable Tracking Links"
                },
                new boolean[]{
                        InstagramSettings.AD_BLOCK_ENABLED.get(),
                        InstagramSettings.ANALYTICS_BLOCKED.get(),
                        InstagramSettings.TRACKING_LINKS_DISABLED.get()
                },
                values -> {
                    InstagramSettings.AD_BLOCK_ENABLED.save(values[0]);
                    InstagramSettings.ANALYTICS_BLOCKED.save(values[1]);
                    InstagramSettings.TRACKING_LINKS_DISABLED.save(values[2]);
                }
        );
    }

    private static void showMiscOptions(Context context) {
        showToggleDialog(
                context,
                "Misc",
                new String[]{
                        "Show Follower Toast",
                        "Enable Developer Mode"
                },
                new boolean[]{
                        InstagramSettings.SHOW_FOLLOWER_TOAST.get(),
                        InstagramSettings.DEV_ENABLED.get()
                },
                values -> {
                    InstagramSettings.SHOW_FOLLOWER_TOAST.save(values[0]);
                    InstagramSettings.DEV_ENABLED.save(values[1]);
                }
        );
    }

    private static void showToggleDialog(
            Context context,
            String title,
            String[] labels,
            boolean[] values,
            ToggleSaveAction saveAction
    ) {
        dismissCurrentDialog();

        Pair<Dialog, LinearLayout> pair = CustomDialog.create(
                context,
                title,
                null,
                null,
                "Save",
                () -> {
                    saveAction.onSave(values);
                    showMorpheSettingsDialog(context);
                },
                () -> showMorpheSettingsDialog(context),
                null,
                null,
                true
        );

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, Dim.dp8, 0, Dim.dp8);

        for (int index = 0; index < labels.length; index++) {
            final int toggleIndex = index;
            Switch toggle = new Switch(context);
            toggle.setText(labels[index]);
            toggle.setChecked(values[index]);
            toggle.setOnCheckedChangeListener((buttonView, isChecked) -> values[toggleIndex] = isChecked);
            content.addView(toggle);
        }

        int insertAt = Math.max(pair.second.getChildCount() - 1, 0);
        pair.second.addView(content, insertAt);

        showDialog(context, pair.first);
    }

    private static void addMenuButton(Context context, LinearLayout root, String text, Runnable clickAction) {
        Button button = new Button(context);
        button.setAllCaps(false);
        button.setText(text);
        button.setOnClickListener(v -> {
            dismissCurrentDialog();
            clickAction.run();
        });

        int insertAt = Math.max(root.getChildCount() - 1, 0);
        root.addView(button, insertAt);
    }

    private static void showDialog(Context context, Dialog dialog) {
        currentDialog = dialog;
        Activity activity = resolveActivity(context);
        if (activity != null) {
            Utils.showDialog(activity, dialog);
            return;
        }

        dialog.show();
    }

    private static Activity resolveActivity(Context context) {
        if (context instanceof Activity activity) {
            UIHookManager.setCurrentActivity(activity);
            return activity;
        }

        Activity current = UIHookManager.getCurrentActivity();
        if (current != null) {
            return current;
        }

        return Utils.getActivity();
    }

    private static void dismissCurrentDialog() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        currentDialog = null;
    }

    private interface ToggleSaveAction {
        void onSave(boolean[] values);
    }
}
