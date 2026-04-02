package app.morphe.patches.instagram.ad

import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import app.morphe.patches.instagram.core.initCorePatch
import app.morphe.util.getFreeRegisterProvider

internal const val AD_GATE_EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/instagram/ad/SponsoredContentGate;"

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide Ads",
    description = "Adds an option to hide sponsored content in the app.",
) {
    compatibleWith("com.instagram.android")
    dependsOn(initCorePatch)

    execute {
        SponsoredContentFingerprint.method.apply {
            val shouldHideRegister = getFreeRegisterProvider(
                index = 1,
                numberOfFreeRegistersNeeded = 1
            ).getFreeRegister()

            addInstructionsWithLabels(
                0,
                """
                    invoke-static {}, $AD_GATE_EXTENSION_CLASS_DESCRIPTOR->shouldHideSponsoredContent()Z
                    move-result v$shouldHideRegister
                    if-eqz v$shouldHideRegister, :morphe_continue
                    const/4 v$shouldHideRegister, 0x0
                    return v$shouldHideRegister
                """.trimIndent(),
                ExternalLabel("morphe_continue", getInstruction(0))
            )
        }
    }
}