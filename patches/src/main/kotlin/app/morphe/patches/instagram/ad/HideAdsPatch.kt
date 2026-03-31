package app.morphe.patches.instagram.ad

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

@Suppress("unused")
val adSuppressionPatch = bytecodePatch(
    name = "Hide Ads",
    description = "Hide ads in the app.",
) {
    compatibleWith("com.instagram.android")

    execute {
        SponsoredContentMethodFingerprint.method.returnEarly(false)
    }
}