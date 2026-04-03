/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.morphe.patches.instagram.misc.settings

import app.morphe.patches.all.misc.packagename.setOrGetFallbackPackageName

//import app.morphe.patches.shared.misc.checks.experimentalAppNoticePatch
//import app.morphe.patches.instagram.shared.Constants.COMPATIBILITY_YOUTUBE
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.morphe.patches.all.misc.resources.addAppResources
import app.morphe.patches.all.misc.resources.addResourcesPatch
import app.morphe.patches.shared.layout.branding.addLicensePatch
import app.morphe.patches.instagram.initialization.initializationPatch
import app.morphe.patches.shared.misc.mapping.resourceMappingPatch
import app.morphe.patches.shared.misc.settings.MORPHE_SETTINGS_INTENT
import app.morphe.patches.shared.misc.settings.overrideThemeColors
import app.morphe.patches.shared.misc.settings.preference.BasePreference
import app.morphe.patches.shared.misc.settings.preference.BasePreferenceScreen
import app.morphe.patches.shared.misc.settings.preference.InputType
import app.morphe.patches.shared.misc.settings.preference.IntentPreference
import app.morphe.patches.shared.misc.settings.preference.ListPreference
import app.morphe.patches.shared.misc.settings.preference.NonInteractivePreference
import app.morphe.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.morphe.patches.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import app.morphe.patches.shared.misc.settings.preference.SwitchPreference
import app.morphe.patches.shared.misc.settings.preference.TextPreference
import app.morphe.patches.shared.misc.settings.settingsPatch
import app.morphe.patches.instagram.misc.extension.sharedExtensionPatch
import app.morphe.patches.instagram.shared.InstagramActivityOnCreateFingerprint
import app.morphe.util.ResourceGroup
import app.morphe.util.addInstructionsAtControlFlowLabel
import app.morphe.util.copyResources
import app.morphe.util.findElementByAttributeValueOrThrow
import app.morphe.util.findInstructionIndicesReversedOrThrow
import app.morphe.util.insertLiteralOverride
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter
import com.android.tools.smali.dexlib2.util.MethodUtil

private const val BASE_ACTIVITY_HOOK_CLASS_DESCRIPTOR = "Lapp/morphe/extension/instagram/settings/BaseActivityHook;"
private const val INSTAGRAM_ACTIVITY_HOOK_CLASS_DESCRIPTOR = "Lapp/morphe/extension/instagram/settings/InstagramActivityHook;"

private val preferences = mutableSetOf<BasePreference>()

private val settingsResourcePatch = resourcePatch {
    dependsOn(
        resourceMappingPatch,
        settingsPatch(
            rootPreferences = listOf(
                IntentPreference(
                    titleKey = "morphe_settings_title",
                    summaryKey = null,
                    intent = newIntent(MORPHE_SETTINGS_INTENT)
                ) to "settings_fragment",
            ),
            preferences = preferences
        )
    )

    execute {
        // Use same colors as stock YouTube.
        overrideThemeColors("@color/bds_white", "@color/bds_black")

        copyResources(
            "settings",
            ResourceGroup("drawable",
//                "morphe_settings_icon_dynamic.xml",
                "morphe_settings_icon_light.xml",
                "morphe_settings_icon_dark.xml",
                "morphe_settings_screen_00_about.xml",
                "morphe_settings_screen_01_ads.xml",
                "morphe_settings_screen_02_ghost_mode.xml",
                "morphe_settings_screen_03_feed.xml",
                "morphe_settings_screen_04_general.xml",
                "morphe_settings_screen_05_privacy.xml",
                "morphe_settings_screen_06_misc.xml",
                "morphe_settings_screen_07_quality.xml",
            )
        )

        // Remove horizontal divider from the settings Preferences
        // To better match the appearance of the stock YouTube settings.
        document("res/values/styles.xml").use { document ->
            val childNodes = document.childNodes

            arrayOf(
                "Theme.Instagram",
            ).forEach { value ->
                val listDividerNode = document.createElement("item")
                listDividerNode.setAttribute("name", "android:listDivider")
                listDividerNode.appendChild(document.createTextNode("@null"))

                childNodes.findElementByAttributeValueOrThrow(
                    "name",
                    value,
                ).appendChild(listDividerNode)
            }
        }

        // Modify the manifest to enhance LicenseActivity behavior
        document("AndroidManifest.xml").use { document ->
            val apiActivity = document.childNodes.findElementByAttributeValueOrThrow(
                "android:name",
                "com.google.android.gms.common.api.GoogleApiActivity",
            )

            // Prevents the activity from being recreated on configuration changes
            // (e.g., screen rotation), preserving its current state and fragment.
            apiActivity.setAttribute(
                "android:configChanges",
                "orientation|screenSize|keyboardHidden"
            )

            // Use same theme as other activities. The exiting theme causes the Morphe
            // settings to fade in and not use a transition animation. A custom theme
            // could be used to not show any transition to match the original YT settings
            // submenu behavior.
            apiActivity.setAttribute(
                "android:theme",
                "@style/Theme.AppCompat.DayNight.NoActionBar"
            )

            val mimeType = document.createElement("data")
            mimeType.setAttribute("android:mimeType", "text/plain")

            // Add a data intent filter with MIME type "text/plain".
            // Some devices crash if undeclared data is passed to an intent,
            // and this change appears to fix the issue.
            val intentFilter = document.createElement("intent-filter")
            intentFilter.appendChild(mimeType)

            apiActivity.appendChild(intentFilter)
        }
    }
}

val settingsPatch = bytecodePatch(
    description = "Adds settings for Morphe to YouTube.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsResourcePatch,
        addResourcesPatch,
        addLicensePatch,
        // TODO: Enable later when repo is upstreamed?
        // experimentalAppNoticePatch(
        //    mainActivityFingerprint = InstagramActivityOnCreateFingerprint,
        //    recommendedAppVersion = COMPATIBILITY_INSTAGRAM.targets!!.first { !it.isExperimental }.version!!
        //),
        initializationPatch(
            mainActivityFingerprint = InstagramActivityOnCreateFingerprint
        )
    )

    execute {
        addAppResources("instagram")

        // Add an "About" preference to the top.
        preferences += NonInteractivePreference(
            key = "morphe_settings_screen_00_about",
            icon = "@drawable/morphe_settings_screen_00_about",
            layout = "@layout/preference_with_icon",
            summaryKey = null,
            tag = "app.morphe.extension.shared.settings.preference.about.MorpheAboutPreference",
            selectable = true,
        )

        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("morphe_settings_search_history"),
        )

        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("morphe_show_menu_icons")
        )

        PreferenceScreen.MISC.addPreferences(
            TextPreference(
                key = null,
                titleKey = "morphe_pref_import_export_title",
                summaryKey = "morphe_pref_import_export_summary",
                inputType = InputType.TEXT_MULTI_LINE,
                tag = "app.morphe.extension.shared.settings.preference.ImportExportPreference",
            ),
            ListPreference(
                key = "morphe_language",
                tag = "app.morphe.extension.shared.settings.preference.SortedListPreference"
            )
        )

        // Update shared dark mode status based on Instagram theme.
        // This is needed because YT allows forcing light/dark mode
        // which then differs from the system dark mode status.
        GetThemeModeFingerprint.method.apply {
            findInstructionIndicesReversedOrThrow(Opcode.RETURN).forEach { index ->
                val register = getInstruction<OneRegisterInstruction>(index).registerA
                addInstructionsAtControlFlowLabel(
                    index,
                    "invoke-static { v$register }, $INSTAGRAM_ACTIVITY_HOOK_CLASS_DESCRIPTOR->updateLightDarkModeStatus(I)V",
                )
            }
        }

        modifyActivityForSettingsInjection(
            GoogleApiActivityOnCreateFingerprint,
            INSTAGRAM_ACTIVITY_HOOK_CLASS_DESCRIPTOR,
            true
        )
    }

    finalize {
        PreferenceScreen.close()
    }
}

/**
 * Modifies the activity to show Morphe settings instead of its original purpose.
 */
context(BytecodePatchContext)
internal fun modifyActivityForSettingsInjection(
    activityOnCreateFingerprint: Fingerprint,
    extensionClassType: String,
    isInstagram: Boolean
) {
    val activityOnCreateClass = activityOnCreateFingerprint.classDef
    val activityOnCreateMethod = activityOnCreateFingerprint.method

    // Modify Activity and remove all existing layout code.
    // Must modify an existing activity and cannot add a new activity to the manifest,
    // as that fails for root installations.
    activityOnCreateMethod.addInstructions(
        0,
        """
            invoke-super { p0, p1 }, ${activityOnCreateClass.superclass}->onCreate(Landroid/os/Bundle;)V
            invoke-static { p0 }, $extensionClassType->initialize(Landroid/app/Activity;)V
            return-void
        """
    )

    // Remove other methods as they will break as the onCreate method is modified above.
    activityOnCreateClass.apply {
        methods.removeIf { it != activityOnCreateMethod && !MethodUtil.isConstructor(it) }
    }

    // Override base context to allow using Morphe specific settings.
    ImmutableMethod(
        activityOnCreateClass.type,
        "attachBaseContext",
        listOf(ImmutableMethodParameter("Landroid/content/Context;", null, null)),
        "V",
        AccessFlags.PROTECTED.value,
        null,
        null,
        MutableMethodImplementation(3),
    ).toMutable().apply {
        addInstructions(
            """
                invoke-static { p1 }, $BASE_ACTIVITY_HOOK_CLASS_DESCRIPTOR->getAttachBaseContext(Landroid/content/Context;)Landroid/content/Context;
                move-result-object p1
                invoke-super { p0, p1 }, ${activityOnCreateClass.superclass}->attachBaseContext(Landroid/content/Context;)V
                return-void
            """
        )
    }.let(activityOnCreateClass.methods::add)

    // Override onBackPressed() to intercept back gesture.
    ImmutableMethod(
        activityOnCreateClass.type,
        if (isInstagram) "finish" else "onBackPressed",
        emptyList(),
        "V",
        AccessFlags.PUBLIC.value,
        null,
        null,
        MutableMethodImplementation(3),
    ).toMutable().apply {
        // Slightly different hooks are needed, otherwise the back button can behave wrong.
        val extensionMethodName = if (isInstagram) "handleFinish" else "handleBackPress"
        val invokeFinishOpcode = if (isInstagram) "invoke-super" else "invoke-virtual"

        addInstructions(
            """
                invoke-static {}, $extensionClassType->$extensionMethodName()Z
                move-result v0
                if-nez v0, :search_handled
                $invokeFinishOpcode { p0 }, Landroid/app/Activity;->finish()V
                :search_handled
                return-void
            """
        )
    }.let(activityOnCreateClass.methods::add)
}

/**
 * Creates an intent to open Morphe settings.
 */
fun newIntent(settingsName: String) = IntentPreference.Intent(
    data = settingsName,
    targetClass = "com.google.android.gms.common.api.GoogleApiActivity",
) {
    // The package name change has to be reflected in the intent.
    setOrGetFallbackPackageName("com.android.instagram")
}

object PreferenceScreen : BasePreferenceScreen() {
    // Sort screens in the root menu by key, to not scatter related items apart
    // (sorting key is set in morphe_prefs.xml).
    // If no preferences are added to a screen, the screen will not be added to the settings.
    val ADS = Screen(
        key = "morphe_settings_screen_01_ads",
        summaryKey = null,
        icon = "@drawable/morphe_settings_screen_01_ads",
        layout = "@layout/preference_with_icon",
    )
    val GHOST_MODE = Screen(
        key = "morphe_settings_screen_02_ghost_mode",
        summaryKey = null,
        icon = "@drawable/morphe_settings_screen_02_ghost_mode",
        layout = "@layout/preference_with_icon",
        sorting = Sorting.UNSORTED,
    )
    val FEED = Screen(
        key = "morphe_settings_screen_03_feed",
        summaryKey = null,
        icon = "@drawable/morphe_settings_screen_03_feed",
        layout = "@layout/preference_with_icon",
    )
    val GENERAL = Screen(
        key = "morphe_settings_screen_04_general",
        summaryKey = null,
        icon = "@drawable/morphe_settings_screen_04_general",
        layout = "@layout/preference_with_icon",
    )
    val PRIVACY = Screen(
        key = "morphe_settings_screen_05_privacy",
        summaryKey = null,
        icon = "@drawable/morphe_settings_screen_05_privacy",
        layout = "@layout/preference_with_icon",
    )
    val MISC = Screen(
        key = "morphe_settings_screen_06_misc",
        summaryKey = null,
        icon = "@drawable/morphe_settings_screen_06_misc",
        layout = "@layout/preference_with_icon",
    )
    val QUALITY = Screen(
        key = "morphe_settings_screen_07_quality",
        summaryKey = null,
        icon = "@drawable/morphe_settings_screen_07_quality",
        layout = "@layout/preference_with_icon",
    )

    override fun commit(screen: PreferenceScreenPreference) {
        preferences += screen
    }
}
