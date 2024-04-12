package com.simonflapse.osrs.venom.events;

import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import net.runelite.api.*;
import net.runelite.api.events.HitsplatApplied;

import javax.inject.Inject;

public class OnHitsplatApplied {
    private final Client client;
    private final OverlayOrchestrator overlayOrchestrator;

    @Inject
    public OnHitsplatApplied(Client client, OverlayOrchestrator overlayOrchestrator) {
        this.client = client;
        this.overlayOrchestrator = overlayOrchestrator;
    }

    public void onEvent(HitsplatApplied hitsplatApplied) {
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        Actor target = hitsplatApplied.getActor();

        if (target.isDead()) {
            return;
        }

        if (hitsplat.getAmount() == 0) {
            return;
        }

        if (hitsplat.getHitsplatType() != HitsplatID.VENOM) {
            return;
        }

        String message = getVenomMessage(hitsplat, target);
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Venom Timer Plugin", message, null);
        overlayOrchestrator.updateDamage(target, hitsplat.getAmount());
    }

    private String getVenomMessage(Hitsplat hitsplat, Actor target) {
        int damage = hitsplat.getAmount();
        String targetName = target.getName();
        String aggressorName = target.getInteracting() != null ? target.getInteracting().getName() : null;
        return String.format("Venom Detected for: %s HP - Damage on: %s - Interacting with: %s", damage, targetName, aggressorName);
    }
}
