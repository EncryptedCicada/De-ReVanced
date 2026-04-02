package app.morphe.patches.instagram.misc.extension

import app.morphe.patches.shared.misc.extension.sharedExtensionPatch
import app.morphe.patches.instagram.misc.extension.hooks.instagramActivityOnCreateHook

val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "instagram",
    isYouTubeOrYouTubeMusic = false,
    instagramActivityOnCreateHook,
)