package com.simonflapse.osrs.venom.events;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import net.runelite.api.Actor;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.events.HitsplatApplied;

import javax.inject.Inject;

public class OnHitsplatApplied {
    private final VenomTimerConfig config;
    private final OverlayOrchestrator overlayOrchestrator;

    @Inject
    public OnHitsplatApplied(VenomTimerConfig config, OverlayOrchestrator overlayOrchestrator) {
        this.config = config;
        this.overlayOrchestrator = overlayOrchestrator;
    }

    public void onEvent(HitsplatApplied hitsplatApplied) {
        if (!config.overlayEnabled()) {
            return;
        }

        System.out.println(hitsplatApplied.getActor().getName() + " Health ratio: " + hitsplatApplied.getActor().getHealthRatio());
        System.out.println(hitsplatApplied.getActor().getName() + " Health Scale: " + hitsplatApplied.getActor().getHealthScale());

        updateVenomDamage(hitsplatApplied);
    }

    private void updateVenomDamage(HitsplatApplied hitsplatApplied) {
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        Actor target = hitsplatApplied.getActor();

        if (!isHitsplatRelevant(target, hitsplat)) {
            return;
        }

        overlayOrchestrator.updateDamage(target, hitsplat.getAmount());
    }

    private static boolean isHitsplatRelevant(Actor target, Hitsplat hitsplat) {
        if (target.isDead() || hitsplat.getAmount() <= 0) {
            return false;
        }

        return hitsplat.getHitsplatType() == HitsplatID.VENOM;
    }
}
