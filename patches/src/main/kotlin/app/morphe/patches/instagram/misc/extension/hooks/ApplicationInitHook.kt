package app.morphe.patches.instagram.misc.extension.hooks

import app.morphe.patches.shared.misc.extension.activityOnCreateExtensionHook

internal val instagramActivityOnCreateHook = activityOnCreateExtensionHook(
    activityClassType = "Lcom/instagram/mainactivity/InstagramMainActivity;",
    targetBundleMethod = true
)