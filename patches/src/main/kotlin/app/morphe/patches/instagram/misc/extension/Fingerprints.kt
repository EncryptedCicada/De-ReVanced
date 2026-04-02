package app.morphe.patches.instagram.misc.extension

import app.morphe.patcher.Fingerprint
import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.shared.misc.extension.sharedExtensionPatch

internal object InstagramApplicationOnCreateFingerprint : Fingerprint(
    custom = { method, classDef ->
        method.name == "onCreate" && classDef.endsWith("/InstagramMainActivity;")
    },
)

internal val instagramApplicationOnCreateHook = ExtensionHook(
    fingerprint = InstagramApplicationOnCreateFingerprint,
)

val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "instagram",
    isYouTubeOrYouTubeMusic = false,
    instagramApplicationOnCreateHook,
)
