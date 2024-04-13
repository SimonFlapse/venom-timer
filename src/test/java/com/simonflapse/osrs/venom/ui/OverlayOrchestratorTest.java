package com.simonflapse.osrs.venom.ui;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import net.runelite.api.Actor;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class OverlayOrchestratorTest {
    OverlayOrchestrator overlayOrchestrator;
    OverlayManager overlayManager;
    VenomTimerConfig config;
    NPCManager npcManager;

    @BeforeEach
    void setUp() {
        overlayManager = mock(OverlayManager.class);
        config = mock(VenomTimerConfig.class);
        npcManager = mock(NPCManager.class);
        overlayOrchestrator = new OverlayOrchestrator(overlayManager, config, npcManager);
    }

    @Nested
    class updateDamage {
        @Test
        void should_not_throw_exceptions() {
            overlayOrchestrator.updateDamage(mock(Actor.class), 6);
        }
    }
}