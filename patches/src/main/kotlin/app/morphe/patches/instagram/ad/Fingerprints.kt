package app.morphe.patches.instagram.ad

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object SponsoredContentFingerprint : Fingerprint(
    returnType = "Z",
    accessFlags = listOf(AccessFlags.PRIVATE),
    strings = listOf(
        "SponsoredContentController.insertItem",
    ),
)
