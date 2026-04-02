package app.morphe.patches.instagram.misc.extension

import app.morphe.patches.shared.misc.extension.sharedExtensionPatch

internal val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "instagram",
    isYouTubeOrYouTubeMusic = false,
    instagramApplicationOnCreateHook,
)