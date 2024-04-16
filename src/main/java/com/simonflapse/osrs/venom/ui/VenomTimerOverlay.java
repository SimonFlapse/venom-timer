package com.simonflapse.osrs.venom.ui;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import com.simonflapse.osrs.venom.combat.VenomDamage;
import com.simonflapse.osrs.venom.ui.utils.DrawString;
import com.simonflapse.osrs.venom.ui.utils.StringGraphics;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Point;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.Overlay;

import java.awt.*;
import java.util.ArrayList;

@Slf4j
public class VenomTimerOverlay extends Overlay {

    private final VenomTimerConfig config;
    private final NPCManager npcManager;
    private final Actor actor;
    private final ActorOverlayRemover overlayRemover;

    private VenomDamage venomDamage;

    public VenomTimerOverlay(VenomTimerConfig config, NPCManager npcManager, Actor actor, ActorOverlayRemover overlayRemover) {
        this.config = config;
        this.npcManager = npcManager;
        this.actor = actor;
        this.overlayRemover = overlayRemover;
    }

    public void updateVenom(int damage) {
        if (venomDamage == null) {
            this.venomDamage = new VenomDamage(npcManager, actor);
        }
        this.venomDamage.updateVenom(damage);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (venomDamage == null) {
            return null;
        }

        if (!renderActorOverlay(graphics)) {
            overlayRemover.removeActorOverlay(this.actor, this);
        }
        return null;
    }

    private boolean renderActorOverlay(Graphics2D graphics) {
        long timeToNextVenom = this.venomDamage.timeDifference();

        long timeToDead = -1;
        if (config.timeToDeathEnabled()) {
            timeToDead = this.venomDamage.calculateTimeToDeath(timeToNextVenom);
        }

        if (!isActorOverlayRelevant(timeToNextVenom)) {
            return false;
        }

        if (timeToNextVenom <= 0) {
            return true;
        }

        drawText(graphics, timeToNextVenom, timeToDead);
        return true;
    }

    private void drawText(Graphics2D graphics, long timeToNextVenom, long timeToDeath) {
        int yOffset = 0;

        Color lightVenomColor = new Color(73,151,126);
        Color darkVenomColor = new Color(22,48,40).brighter().brighter();

        if (config.totalDamageEnabled()) {
            yOffset = drawSingleLineText(new StringGraphics("Total damage: " + this.venomDamage.getTotalDamage(), darkVenomColor), graphics, yOffset);
        }

        if (config.nextDamageEnabled()) {
            yOffset = drawSingleLineText(new StringGraphics("Next damage: " + this.venomDamage.getNextDamage(), darkVenomColor), graphics, yOffset);
        }

        if (config.timeToNextVenomEnabled()) {
            yOffset = drawSingleLineText(new StringGraphics("Venom in: " + timeToNextVenom + "s", lightVenomColor), graphics, yOffset);
        }

        if (timeToDeath > 0) {
            drawSingleLineText(new StringGraphics("Dead in: " + timeToDeath + "s", lightVenomColor), graphics, yOffset);
        }
    }

    private boolean isActorOverlayRelevant(long timeToNextVenom) {
        if (actor == null || actor.isDead()) {
            return false;
        }

        return timeToNextVenom > -9;
    }

    private int drawSingleLineText(StringGraphics stringGraphic, Graphics2D graphics, int yOffset) {
        ArrayList<StringGraphics> stringGraphics = new ArrayList<>();
        stringGraphics.add(stringGraphic);
        println(stringGraphics, graphics, yOffset);

        return yOffset + 15;
    }

    private void println(ArrayList<StringGraphics> stringGraphics, Graphics2D graphics, int yOffset) {
        Point textLocation = actor.getCanvasTextLocation(graphics, getUnformattedString(stringGraphics), actor.getLogicalHeight() + 100);
        if (textLocation != null)
        {
            textLocation = new Point(textLocation.getX(), textLocation.getY() + yOffset);
            DrawString.renderTextLocation(graphics, textLocation, stringGraphics);
        }
    }

    private static String getUnformattedString(ArrayList<StringGraphics> texts) {
        StringBuilder unformattedString = new StringBuilder();
        for (StringGraphics text : texts) {
            unformattedString.append(text.getString());
        }
        return unformattedString.toString();
    }
}
