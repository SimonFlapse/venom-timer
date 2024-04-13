package com.simonflapse.osrs.venom;


import com.simonflapse.osrs.venom.events.OnConfigChanged;
import com.simonflapse.osrs.venom.events.OnHitsplatApplied;
import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import com.simonflapse.osrs.venom.ui.VenomTimerOverlay;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.*;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class VenomTimerPluginTest {
    @InjectMocks
    VenomTimerPlugin plugin = new VenomTimerPlugin();

    @Spy
    VenomTimerConfig config = new VenomTimerConfig() {
        @Override
        public boolean timeToDeathEnabled() {
            return true;
        }
    };

    OverlayManager overlayManager = mock(OverlayManager.class);

    NPCManager npcManager = mock(NPCManager.class);

    @Spy
    private OverlayOrchestrator overlayOrchestrator = new OverlayOrchestrator(overlayManager, config, npcManager);

    @Spy
    private OnConfigChanged onConfigChanged = new OnConfigChanged(overlayOrchestrator);

    @Spy
    private OnHitsplatApplied onHitsplatApplied = new OnHitsplatApplied(config, overlayOrchestrator);

    @BeforeEach
    void setUp() {}

    @Nested
    class onHitsplatApplied {
        @Test
        void should() {
            HitsplatApplied hitsplatApplied = new HitsplatApplied();
            hitsplatApplied.setHitsplat(new Hitsplat(HitsplatID.VENOM, 6, 0));

            NPC npc = mock(NPC.class);
            when(npc.getHealthRatio()).thenReturn(28);
            when(npc.getHealthScale()).thenReturn(30);
            when(npcManager.getHealth(anyInt())).thenReturn(30);
            hitsplatApplied.setActor(npc);

            plugin.onHitsplatApplied(hitsplatApplied);
            ArgumentCaptor<VenomTimerOverlay> argumentCaptor = ArgumentCaptor.forClass(VenomTimerOverlay.class);
            verify(overlayManager).add(argumentCaptor.capture());

            VenomTimerOverlay overlay = argumentCaptor.getValue();
            overlay.render(mock(Graphics2D.class));
        }
    }
}