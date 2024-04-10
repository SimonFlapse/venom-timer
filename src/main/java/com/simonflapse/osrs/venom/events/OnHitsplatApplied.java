package com.simonflapse.osrs.venom.events;

import com.simonflapse.osrs.venom.ui.VenomTimerOverlay;
import net.runelite.api.*;
import net.runelite.api.events.HitsplatApplied;

public class OnHitsplatApplied {
    private final Client client;

    public OnHitsplatApplied(Client client) {
        this.client = client;
    }

    public void onEvent(HitsplatApplied hitsplatApplied, VenomTimerOverlay venomTimerOverlay) {
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        Actor target = hitsplatApplied.getActor();

        if (hitsplat.getHitsplatType() == HitsplatID.VENOM) {
            String message = getVenomMessage(hitsplat, target);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Venom Timer Plugin", message, null);
            venomTimerOverlay.updateVenom(target, hitsplat.getAmount());
        }
    }

    private String getVenomMessage(Hitsplat hitsplat, Actor target) {
        return String.format("Venom Detected for: %s HP - Damage on: %s - Interacting with: %s", hitsplat.getAmount(), target.getName(), target.getInteracting().getName());
    }
}
