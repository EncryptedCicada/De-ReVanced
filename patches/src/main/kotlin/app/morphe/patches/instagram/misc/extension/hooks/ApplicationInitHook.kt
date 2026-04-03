package app.morphe.patches.instagram.misc.extension.hooks

import app.morphe.patches.shared.misc.extension.activityOnCreateExtensionHook
import app.morphe.patches.instagram.shared.INSTAGRAM_MAIN_ACTIVITY_CLASS_TYPE

internal val instagramActivityOnCreateHook = activityOnCreateExtensionHook(
    activityClassType = INSTAGRAM_MAIN_ACTIVITY_CLASS_TYPE,
    targetBundleMethod = true
)