package app.morphe.patches.instagram.misc.dev

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object ClearNotificationReceiverOnReceiveFingerprint : Fingerprint(
    returnType = "V",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    definingClass = "Lcom/instagram/notifications/push/ClearNotificationReceiver;",
    name = "onReceive",
)
