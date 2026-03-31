package app.morphe.patches.instagram.ad

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object SponsoredContentMethodFingerprint : Fingerprint(
    returnType = "Z",
    accessFlags = listOf(AccessFlags.PRIVATE),
    strings = listOf(
        "SponsoredContentController.insertItem",
    ),
)
