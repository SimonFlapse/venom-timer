package com.simonflapse.osrs.venom.combat;

public final class HealthUtil {
    private HealthUtil() {}

    /**
     * The servers healthRation calculation is reasoned to be:
     * <code>
     *     {@code healthRatio = (1 + (healthScale - 1) * health) / (maxHealth)}
     * </code>
     * <p>
     * Isolating for the variable `health` gives:
     * </p>
     * <code>
     *     {@code health = (maxHealth * healthRatio - 1) / (healthScale - 1)}
     * </code>
     *
     * @param maxHealth the maximum health of an actor, so that when health ratio = health scale the returned health = maxHealth.
     * @param healthRatio the ration between 0 and healthScale of the actor's health.
     * @param healthScale the scale of an actor's health indicating how many rations the maxHealth can be divided into by the server.
     * @return approximated health or -1 if unable to calculate.
     */
    public static int getApproximateHealth(int maxHealth, int healthRatio, int healthScale) {
        if (!canCalculateHealth(healthRatio, healthScale)) {
            return -1;
        }

        if (isActorDead(healthRatio, healthScale)) {
            return 0;
        }

        if (isHealthBoolean(healthScale)) {
            return maxHealth;
        }

        return calculateHealth(maxHealth, healthRatio, healthScale);
    }

    /**
     * Calculates an approximate integer value of an actor's health based on its max health,
     * the health scale and health ratio.
     *
     * <p>
     *     The calculation is based on this mathematical equation:
     * </p>
     * <code>
     *     {@code health = (maxHealth * healthRatio - 1) / (healthScale - 1)}
     * </code>
     *
     * @param maxHealth the maximum health of an actor, so that when health ratio = health scale the returned health = maxHealth.
     * @param healthRatio the ration between 0 and healthScale of the actor's health.
     * @param healthScale the scale of an actor's health indicating how many rations the maxHealth can be divided into by the server.
     * @return approximated health
     */
    private static int calculateHealth(int maxHealth, int healthRatio, int healthScale) {
        int health = (maxHealth * healthRatio - 1) / (healthScale - 1);
        return Math.min(health, maxHealth);
    }

    /**
     * Checks if the health scale can be considered a boolean.
     * This means that the health ration either indicates full health or dead.
     * @param healthScale the scale of an actor's health indicating how many rations the maxHealth can be divided into by the server.
     * @return true if health ration can only indicate alive or dead, otherwise false.
     */
    private static boolean isHealthBoolean(int healthScale) {
        return healthScale == 1;
    }

    /**
     * Checks if the actor can be considered dead.
     * This means that the actors health is 0.
     * <p>
     *     This happens when the health ratio is 0 or if the health scale is 0.
     * </p>
     * @param healthRatio the ration between 0 and healthScale of the actor's health.
     * @param healthScale the scale of an actor's health indicating how many rations the maxHealth can be divided into by the server.
     * @return true if the actor can be considered dead, otherwise false
     */
    private static boolean isActorDead(int healthRatio, int healthScale) {
        return healthScale == 0 || healthRatio == 0;
    }

    /**
     * Checks if the health ratio and health scale is non-negative.
     * <p>
     *     The health can only be calculated if the health ratio and health scale is 0 or a positive integer
     * </p>
     * @param healthRatio the ration between 0 and healthScale of the actor's health.
     * @param healthScale the scale of an actor's health indicating how many rations the maxHealth can be divided into by the server.
     * @return true if the health ration and health scale is non-negative, otherwise false
     */
    private static boolean canCalculateHealth(int healthRatio, int healthScale) {
        return healthRatio >= 0 && healthScale >= 0;
    }
}
