package app.morphe.patches.instagram.core

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.instagram.misc.extension.hooks.instagramActivityOnCreateHook
import app.morphe.patches.instagram.misc.extension.sharedExtensionPatch

internal const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/shared/Utils;"

@Suppress("unused")
val initCorePatch = bytecodePatch(
    name = "Initialize Core Settings",
    description = "Initialize Morphe shared extension context.",
    use = true
) {
    compatibleWith("com.instagram.android")

    dependsOn(sharedExtensionPatch)

    execute {
        instagramActivityOnCreateHook(EXTENSION_CLASS_DESCRIPTOR)
    }
}