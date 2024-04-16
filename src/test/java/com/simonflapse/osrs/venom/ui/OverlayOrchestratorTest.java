package com.simonflapse.osrs.venom.ui;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import net.runelite.api.Actor;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.awt.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        @Test
        void should_reuse_active_overlay() {
            Actor actor = mock(Actor.class);
            overlayOrchestrator.updateDamage(actor, 6);
            overlayOrchestrator.updateDamage(actor, 6);

            verify(overlayManager, times(1)).add(any());
        }

        @Test
        void should_support_multiple_overlays() {
            overlayOrchestrator.updateDamage(mock(Actor.class), 6);
            overlayOrchestrator.updateDamage(mock(Actor.class), 6);

            verify(overlayManager, times(2)).add(any());
        }

        @Test
        void should_remove_irrelevant_overlay() {
            Actor actor = mock(Actor.class);
            when(actor.isDead()).thenReturn(true);
            overlayOrchestrator.updateDamage(actor, 6);

            ArgumentCaptor<VenomTimerOverlay> captor = ArgumentCaptor.forClass(VenomTimerOverlay.class);
            verify(overlayManager).add(captor.capture());

            captor.getValue().render(mock(Graphics2D.class));

            verify(overlayManager).remove(any());
        }
    }

    @Nested
    class shutdown {
        @Test
        void should_remove_all_active_overlays() {
            overlayOrchestrator.updateDamage(mock(Actor.class), 6);
            overlayOrchestrator.updateDamage(mock(Actor.class), 6);

            overlayOrchestrator.shutDown();

            verify(overlayManager, times(2)).remove(any());
        }
    }
}