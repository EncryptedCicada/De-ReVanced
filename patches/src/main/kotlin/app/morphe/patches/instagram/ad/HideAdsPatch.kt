package app.morphe.patches.instagram.ad

import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import app.morphe.patches.instagram.misc.extension.sharedExtensionPatch
import app.morphe.patches.instagram.misc.settings.settingsPatch
import app.morphe.util.getFreeRegisterProvider
import app.morphe.util.setExtensionIsPatchIncluded

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/instagram/patches/HideAdsPatch;"

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide Ads",
    description = "Adds an option to hide sponsored content in the app.",
    use = true
) {
    compatibleWith("com.instagram.android")
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
    )

    execute {
        SponsoredContentFingerprint.method.apply {
            val shouldHideRegister = getFreeRegisterProvider(
                index = 1,
                numberOfFreeRegistersNeeded = 1
            ).getFreeRegister()

            addInstructionsWithLabels(
                0,
                """
                    invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->hideAds()Z
                    move-result v$shouldHideRegister
                    if-eqz v$shouldHideRegister, :morphe_continue
                    const/4 v$shouldHideRegister, 0x0
                    return v$shouldHideRegister
                """.trimIndent(),
                ExternalLabel("morphe_continue", getInstruction(0))
            )
        }

        setExtensionIsPatchIncluded(EXTENSION_CLASS_DESCRIPTOR)
    }
}