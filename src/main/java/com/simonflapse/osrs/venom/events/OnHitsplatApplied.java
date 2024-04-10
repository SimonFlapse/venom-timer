package com.simonflapse.osrs.venom.events;

import net.runelite.api.*;
import net.runelite.api.events.HitsplatApplied;

public class OnHitsplatApplied {
    private final Client client;

    public OnHitsplatApplied(Client client) {
        this.client = client;
    }

    public void onEvent(HitsplatApplied hitsplatApplied) {
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        Actor target = hitsplatApplied.getActor();

        if (hitsplat.getHitsplatType() == HitsplatID.VENOM) {
            String message = getVenomMessage(hitsplat, target);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Venom Timer Plugin", message, null);
        }
    }

    private String getVenomMessage(Hitsplat hitsplat, Actor target) {
        return String.format("Venom Detected for: %s HP - Damage on: %s", hitsplat.getAmount(), target.getName());
    }
}
