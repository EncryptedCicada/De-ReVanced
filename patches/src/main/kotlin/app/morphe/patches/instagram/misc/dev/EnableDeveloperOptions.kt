package app.morphe.patches.instagram.misc.dev

import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.getMutableMethod
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstStringInstructionOrThrow
import app.morphe.util.indexOfFirstInstructionReversedOrThrow
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val USER_SESSION_TYPE = "Lcom/instagram/common/session/UserSession;"

@Suppress("unused")
val enableDeveloperOptionsPatch = bytecodePatch(
    name = "Enable Developer Options",
    description = "Enable developer options by default.",
) {
    compatibleWith("com.instagram.android")

    execute {
        val clearNotificationReceiverOnReceiveMethod = ClearNotificationReceiverOnReceiveFingerprint.method
        val notificationDismissedStringIndex =
            clearNotificationReceiverOnReceiveMethod.indexOfFirstStringInstructionOrThrow("NOTIFICATION_DISMISSED")

        val invokeTargetMethodIndex = clearNotificationReceiverOnReceiveMethod.indexOfFirstInstructionReversedOrThrow(
            notificationDismissedStringIndex
        ) {
            if (opcode != Opcode.INVOKE_STATIC && opcode != Opcode.INVOKE_STATIC_RANGE) {
                return@indexOfFirstInstructionReversedOrThrow false
            }

            val methodReference = getReference<MethodReference>() ?: return@indexOfFirstInstructionReversedOrThrow false
            methodReference.returnType == "Z" &&
                    methodReference.parameterTypes.size == 1 &&
                    methodReference.parameterTypes[0] == USER_SESSION_TYPE
        }

        val invokeTargetMethodReference = clearNotificationReceiverOnReceiveMethod
            .getInstruction(invokeTargetMethodIndex)
            .getReference<MethodReference>()
            ?: throw Exception("Expected method reference at invoke index: $invokeTargetMethodIndex")

        invokeTargetMethodReference.getMutableMethod().returnEarly(true)
    }
}
