package com.simonflapse.osrs.venom.combat;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.client.game.NPCManager;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class VenomDamage {
    private final NPCManager npcManager;
    private final Actor actor;

    @Getter
    private int totalDamage = 0;
    @Getter
    private int nextDamage;

    private Instant lastHit;
    private int currentHealth = -1;

    public VenomDamage(NPCManager npcManager, Actor actor) {
        this.npcManager = npcManager;
        this.actor = actor;
    }

    public void updateVenom(int damage) {
        this.totalDamage += damage;
        this.nextDamage = getNextVenomDamage(damage);
        lastHit = Instant.now();
    }

    public long timeDifference() {
        Instant now = Instant.now();
        Instant nextExpectedVenom = lastHit.plus(Duration.ofSeconds(18));
        Duration between = Duration.between(now, nextExpectedVenom);
        return between.getSeconds() + 1;
    }

    public long calculateTimeToDeath(long timeToNextVenom) {
        if (this.actor instanceof NPC) {
            return calculateTimeToDeathForNPC((NPC) this.actor, timeToNextVenom);
        }

        return -1;
    }

    private long calculateTimeToDeathForNPC(NPC npc, long timeToNextVenom) {
        Integer maxHealth = npcManager.getHealth(npc.getId());
        if (maxHealth != null && maxHealth != 0) {
            updateCurrentHealth(maxHealth);
            return getTimeToDeath(timeToNextVenom, this.currentHealth);
        }
        return -1;
    }

    private void updateCurrentHealth(int maxHealth) {
        int currentHealth = getCurrentHealth(maxHealth);

        if (currentHealth != this.currentHealth) {
            this.currentHealth = currentHealth;
            log.debug("Current health updated to: {} for NPC: {}", currentHealth, this.actor.getName());
        }
    }

    private int getCurrentHealth(int maxHealth) {
        int currentHealth = HealthUtil.getApproximateHealth(maxHealth, actor.getHealthRatio(), actor.getHealthScale());
        if (currentHealth < 0) {
            return this.currentHealth;
        }
        return currentHealth;
    }

    private long getTimeToDeath(long timeToNextVenom, int currentHealth) {
        int nextDamage = this.nextDamage;
        currentHealth = currentHealth - nextDamage;

        int venomSplatsToDead = getVenomSplatsToDead(currentHealth, nextDamage);

        return timeToNextVenom + (18L * venomSplatsToDead);
    }

    private static int getVenomSplatsToDead(int currentHealth, int nextDamage) {
        int venomSplatsToDead = 0;
        while(currentHealth > 0) {
            venomSplatsToDead++;
            nextDamage = getNextVenomDamage(nextDamage);
            currentHealth -= nextDamage;
        }
        return venomSplatsToDead;
    }


    private static int getNextVenomDamage(int currentDamage) {
        return Math.min(currentDamage + 2, 20);
    }
}
