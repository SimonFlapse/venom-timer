package com.simonflapse.osrs.venom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.simonflapse.osrs.venom.VenomTimerConfig.CONFIG_GROUP;

@ConfigGroup(CONFIG_GROUP)
public interface VenomTimerConfig extends Config {
    String CONFIG_GROUP = "venomtimer";
    String OVERLAY_ENABLED = "venomTimerOverlayEnabled";
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
            keyName = TIME_TO_DEATH_ENABLED,
            name = "Time to die",
            description = "Displays an additional timer that indicates when the target will die from venom",
            position = 1
    )
    default boolean timeToDeathEnabled() {
        return false;
    }
}
