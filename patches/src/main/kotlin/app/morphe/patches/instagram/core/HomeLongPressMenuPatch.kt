package app.morphe.patches.instagram.core

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.findMutableMethodOf
import app.morphe.util.getReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference

private const val HOME_LONG_PRESS_MENU_EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/instagram/ui/HomeLongPressMenu;"
private const val HOME_LONG_PRESS_MENU_EXTENSION_METHOD_DESCRIPTOR = "handleHomeLongPress"
private const val LONG_CLICK_LISTENER_TYPE = "Landroid/view/View" + "$" + "OnLongClickListener;"
private const val INSTAGRAM_MAIN_ACTIVITY_TYPE = "Lcom/instagram/mainactivity/InstagramMainActivity;"
private const val USER_SESSION_TYPE = "Lcom/instagram/common/session/UserSession;"

@Suppress("unused")
val homeLongPressMenuPatch = bytecodePatch(
    name = "Long Press Morphe Settings",
    description = "Open Morphe Settings on home button long press.",
    use = true
) {
    compatibleWith("com.instagram.android")

    dependsOn(initCorePatch)

    execute {
        val candidateMethods = mutableListOf<Pair<String, com.android.tools.smali.dexlib2.iface.Method>>()

        classDefForEach { classDef ->
            if (!classDef.interfaces.contains(LONG_CLICK_LISTENER_TYPE)) return@classDefForEach

            val hasMainActivityField = classDef.fields.any { it.type == INSTAGRAM_MAIN_ACTIVITY_TYPE }
            val hasUserSessionField = classDef.fields.any { it.type == USER_SESSION_TYPE }
            if (!hasMainActivityField || !hasUserSessionField) return@classDefForEach

            val hasClickAndActivityAnchors = classDef.methods.any { method ->
                val strings = method.implementation?.instructions
                    ?.mapNotNull { instruction -> instruction.getReference<StringReference>() }
                    ?.map { it.string }
                    ?: emptyList()

                strings.any { it == "click" } && strings.any { it == "activity" }
            }
            if (!hasClickAndActivityAnchors) return@classDefForEach

            val handlerMethod = classDef.methods.firstOrNull { method ->
                method.name == "onLongClick" &&
                        method.returnType == "Z" &&
                        method.parameterTypes == listOf("Landroid/view/View;")
            } ?: return@classDefForEach

            candidateMethods += classDef.type to handlerMethod
        }

        if (candidateMethods.isEmpty()) {
            throw Exception("No dynamic home long-click handler candidate found")
        }

        if (candidateMethods.size > 1) {
            println("[Home Long Press Menu] Multiple candidates found, selecting first:")
            candidateMethods.forEach { (className, _) -> println("[Home Long Press Menu] candidate=$className") }
        }

        val (targetClass, targetMethod) = candidateMethods.first()
        val mutableMethod = mutableClassDefBy(targetClass).findMutableMethodOf(targetMethod)

        mutableMethod.addInstructions(
            0,
            """
                invoke-static {p0, p1}, $HOME_LONG_PRESS_MENU_EXTENSION_CLASS_DESCRIPTOR->$HOME_LONG_PRESS_MENU_EXTENSION_METHOD_DESCRIPTOR(Ljava/lang/Object;Landroid/view/View;)Z
                move-result v0
                if-eqz v0, :morphe_continue
                return v0
                :morphe_continue
            """.trimIndent()
        )
    }
}
