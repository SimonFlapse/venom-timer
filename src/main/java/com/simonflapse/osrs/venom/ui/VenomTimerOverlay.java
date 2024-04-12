package com.simonflapse.osrs.venom.ui;

import com.google.common.base.Strings;
import com.simonflapse.osrs.venom.VenomTimerConfig;
import com.simonflapse.osrs.venom.ui.utils.StringGraphics;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class VenomTimerOverlay extends Overlay {

    private final VenomTimerConfig config;
    private final NPCManager npcManager;
    private final Actor actor;
    private final ActorOverlayRemover overlayRemover;
    private final AtomicBoolean activated = new AtomicBoolean(false);

    private Instant lastHit;
    private int totalDamage = 0;
    private int nextDamage;
    private int currentHealth = -1;

    public VenomTimerOverlay(VenomTimerConfig config, NPCManager npcManager, Actor actor, ActorOverlayRemover overlayRemover) {
        this.config = config;
        this.npcManager = npcManager;
        this.actor = actor;
        this.overlayRemover = overlayRemover;
    }

    public void updateVenom(int damage) {
        this.totalDamage += damage;
        this.nextDamage = getNextVenomDamage(damage);
        lastHit = Instant.now();
        activated.set(true);
    }

    private int getNextVenomDamage(int currentDamage) {
        return Math.min(currentDamage + 2, 20);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!activated.get()) {
            return null;
        }

        if (!renderActorOverlay(graphics)) {
            overlayRemover.removeActorOverlay(this.actor, this);
        }
        return null;
    }

    private boolean renderActorOverlay(Graphics2D graphics) {
        long timeToNextVenom = timeDifference();
        long timeToDead = calculateTimeToDeath(timeToNextVenom);


        if (!isActorOverlayRelevant(timeToNextVenom)) {
            return false;
        }

        if (timeToNextVenom <= 0) {
            return true;
        }

        drawText(graphics, timeToNextVenom, timeToDead);
        return true;
    }

    private long calculateTimeToDeath(long timeToNextVenom) {
        if (!config.timeToDeathEnabled()) {
            this.currentHealth = -1;
            return -1;
        }

        if (this.actor instanceof NPC) {
            NPC npc = (NPC) this.actor;
            Integer npcManagerHealth = npcManager.getHealth(npc.getId());
            if (npcManagerHealth != null) {
                updateCurrentHealth(npcManagerHealth);

                return getTimeToDeath(timeToNextVenom, this.currentHealth);
            }
        }
        return -1;
    }

    private void updateCurrentHealth(Integer npcManagerHealth) {
        int currentHealth = getCurrentHealth(npcManagerHealth, actor.getHealthRatio(), actor.getHealthScale());
        if (currentHealth == -1) {
            currentHealth = this.currentHealth;
        }

        if (currentHealth != this.currentHealth) {
            this.currentHealth = currentHealth;
            log.debug("Current health updated to: {} for NPC: {}", currentHealth, this.actor.getName());
        }
    }

    private long getTimeToDeath(long timeToNextVenom, int currentHealth) {
        int nextDamage = this.nextDamage;
        int healthLeft = currentHealth - nextDamage;
        int iterator = 0;
        while(healthLeft > 0) {
            iterator++;
            nextDamage = getNextVenomDamage(nextDamage);
            healthLeft -= nextDamage;
        }

        return timeToNextVenom + (18L * iterator);
    }

    private void drawText(Graphics2D graphics, long timeToNextVenom, long timeToDeath) {
        int yOffset = 0;

        Color lightVenomColor = new Color(73,151,126);
        Color darkVenomColor = new Color(22,48,40).brighter().brighter();

        yOffset = drawSingleLineText(new StringGraphics("Total damage: " + totalDamage, darkVenomColor), graphics, yOffset);
        yOffset = drawSingleLineText(new StringGraphics("Next damage: " + nextDamage, darkVenomColor), graphics, yOffset);
        yOffset = drawSingleLineText(new StringGraphics("Venom in: " + timeToNextVenom + "s", lightVenomColor), graphics, yOffset);

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
            renderTextLocation(graphics, textLocation, stringGraphics);
        }
    }

    private long timeDifference() {
        return Duration.between(Instant.now(), lastHit.plus(Duration.ofSeconds(18))).getSeconds() + 1;
    }

    private static String getUnformattedString(ArrayList<StringGraphics> texts) {
        StringBuilder unformattedString = new StringBuilder();
        for (StringGraphics text : texts) {
            unformattedString.append(text.getString());
        }
        return unformattedString.toString();
    }

    private static void renderTextLocation(Graphics2D graphics, Point txtLoc, ArrayList<StringGraphics> texts) {
        int x = txtLoc.getX();
        int y = txtLoc.getY();

        for (StringGraphics text : texts){
            if (Strings.isNullOrEmpty(text.getString())) {
                return;
            }

            renderTextShadow(graphics, x, y, text.getString());

            graphics.setColor(ColorUtil.colorWithAlpha(text.getColor(), 0xFF));
            graphics.drawString(text.getString(), x, y);
            x += text.width(graphics);
        }
    }

    private static void renderTextShadow(Graphics2D graphics, int x, int y, String text) {
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);

        graphics.setColor(ColorUtil.colorWithAlpha(Color.BLACK,  50));
        graphics.drawString(text, x + 2, y + 2);
    }

    private int getCurrentHealth(int lastMaxHealth, int lastRatio, int lastHealthScale) {
        if (lastRatio >= 0 && lastHealthScale > 0) {
            // This is the reverse of the calculation of healthRatio done by the server
            // which is: healthRatio = 1 + (healthScale - 1) * health / maxHealth (if health > 0, 0 otherwise)
            // It's able to recover the exact health if maxHealth <= healthScale.
            int health = 0;
            if (lastRatio > 0) {
                int minHealth = 1;
                int maxHealth;
                if (lastHealthScale > 1) {
                    if (lastRatio > 1) {
                        // This doesn't apply if healthRatio = 1, because of the special case in the server calculation that
                        // health = 0 forces healthRatio = 0 instead of the expected healthRatio = 1
                        minHealth = (lastMaxHealth * (lastRatio - 1) + lastHealthScale - 2) / (lastHealthScale - 1);
                    }
                    maxHealth = (lastMaxHealth * lastRatio - 1) / (lastHealthScale - 1);
                    if (maxHealth > lastMaxHealth) {
                        maxHealth = lastMaxHealth;
                    }
                } else {
                    // If healthScale is 1, healthRatio will always be 1 unless health = 0
                    // so we know nothing about the upper limit except that it can't be higher than maxHealth
                    maxHealth = lastMaxHealth;
                }
                // Take the average of min and max possible healths
                health = (minHealth + maxHealth + 1) / 2;
                return health;
            }
        }
        return -1;
    }
}
