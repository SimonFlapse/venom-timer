package com.simonflapse.osrs.venom.events;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import net.runelite.client.events.ConfigChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OnConfigChangedTest {

    OnConfigChanged onConfigChanged;
    OverlayOrchestrator overlayOrchestrator;

    @BeforeEach
    void setUp() {
        this.overlayOrchestrator = Mockito.mock(OverlayOrchestrator.class);
        this.onConfigChanged = new OnConfigChanged(overlayOrchestrator);
    }

    @Nested
    class onEvent {
        @Test
        void should_shutdown_overlays_when_overlay_enabled_is_turned_off() {
            ConfigChanged configChanged = getConfigChanged("false");
            onConfigChanged.onEvent(configChanged);

            Mockito.verify(overlayOrchestrator).shutDown();
        }

        @Test
        void should_not_shutdown_overlays_when_overlay_enabled_is_turned_on() {
            ConfigChanged configChanged = getConfigChanged("true");
            onConfigChanged.onEvent(configChanged);

            Mockito.verify(overlayOrchestrator, Mockito.times(0)).shutDown();
        }

        private ConfigChanged getConfigChanged(String newValue) {
            ConfigChanged configChanged = new ConfigChanged();
            configChanged.setGroup(VenomTimerConfig.CONFIG_GROUP);
            configChanged.setKey(VenomTimerConfig.OVERLAY_ENABLED);
            configChanged.setNewValue(newValue);
            return configChanged;
        }
    }
}