package app.morphe.extension.instagram.ad;

import app.morphe.extension.instagram.utils.core.InstagramSettings;

public final class SponsoredContentGate {
    private SponsoredContentGate() {
    }

    public static boolean shouldHideSponsoredContent() {
        return InstagramSettings.AD_BLOCK_ENABLED.get();
    }
}
