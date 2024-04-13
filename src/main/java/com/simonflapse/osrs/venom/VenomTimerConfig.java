package com.simonflapse.osrs.venom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.simonflapse.osrs.venom.VenomTimerConfig.CONFIG_GROUP;

@ConfigGroup(CONFIG_GROUP)
public interface VenomTimerConfig extends Config {
    String CONFIG_GROUP = "venomtimer";
    String OVERLAY_ENABLED = "venomTimerOverlayEnabled";
    String TIME_TO_NEXT_VENOM_ENABLED = "timeToNextVenomEnabled";
    String TOTAL_DAMAGE_ENABLED = "totalDamageEnabled";
    String NEXT_DAMAGE_ENABLED = "nextDamageEnabled";
    String TIME_TO_DEATH_ENABLED = "timeToDeathEnabled";

    @ConfigItem(
            keyName = OVERLAY_ENABLED,
            name = "Venom Timer Overlay",
            description = "Displays venom information above Players and NPCs that are affected by venom",
            position = 0
    )
    default boolean overlayEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = TIME_TO_NEXT_VENOM_ENABLED,
            name = "Time to next venom splat",
            description = "Displays a timer that indicates when the target next will take damage from venom",
            position = 1
    )
    default boolean timeToNextVenomEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = NEXT_DAMAGE_ENABLED,
            name = "Next venom damage",
            description = "Displays how much the next venom splat will damage the target",
            position = 2
    )
    default boolean nextDamageEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = TOTAL_DAMAGE_ENABLED,
            name = "Total venom damage",
            description = "Displays the total amount of venom damage on the target",
            position = 3
    )
    default boolean totalDamageEnabled() {
        return true;
    }

    @ConfigItem(
        keyName = TIME_TO_DEATH_ENABLED,
        name = "Time to die",
        description = "Displays an additional timer that indicates when the target will die from venom",
        position = 4
    )
    default boolean timeToDeathEnabled() {
        return true;
    }
}
