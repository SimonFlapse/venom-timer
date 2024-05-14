package com.simonflapse.osrs.venom.ui;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import com.simonflapse.osrs.venom.ui.utils.DrawStringTest;
import com.simonflapse.osrs.venom.ui.utils.GraphicsUtil;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.game.NPCManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VenomTimerOverlayTest {

    VenomTimerOverlay overlay;

    VenomTimerConfig config;
    NPCManager npcManager;
    NPC npc;
    ActorOverlayRemover actorOverlayRemover;
    Graphics2D graphics2D;

    @BeforeEach
    void setUp() {
        config = spy(new VenomTimerConfig() {});
        npcManager = mock(NPCManager.class);
        npc = mock(NPC.class);
        actorOverlayRemover = mock(ActorOverlayRemover.class);

        overlay = new VenomTimerOverlay(config, npcManager, npc, actorOverlayRemover);

        graphics2D = GraphicsUtil.getSpyGraphics2D();
    }

    @Nested
    class updateVenom {
        @Test
        void should_update_total_damage() {
            setRenderFlags(false, true, false, false);

            when(npc.getCanvasTextLocation(eq(graphics2D), anyString(), anyInt()))
                    .thenAnswer((invocation) -> new Point(0, invocation.getArgument(2)));

            overlay.updateVenom(6);
            overlay.updateVenom(8);
            overlay.render(graphics2D);

            Color expectedColor = new Color(22,48,40).brighter().brighter();

            verifyStringRenderedWithColor(expectedColor, "Total damage: 14", 0, 100);
        }
    }

    @Nested
    class render {
        @Nested
        class should_not_render_if {
            @Test
            void venom_not_updated_yet() {
                overlay.render(graphics2D);
                verifyNoInteractions(graphics2D);
            }

            @Test
            void canvas_text_location_is_null() {
                when(npc.getCanvasTextLocation(eq(graphics2D), anyString(), anyInt())).thenReturn(null);

                updateVenomAndRender(6);

                verifyNoInteractions(graphics2D);
            }

            @Test
            void time_to_next_venom_is_less_than_1() {
                Clock fixedClock = Clock.fixed(Instant.EPOCH, Clock.systemUTC().getZone());
                Duration duration = Duration.ofSeconds(19);
                Instant instantZeroSeconds = Instant.now(fixedClock);
                Instant instant19Seconds = instantZeroSeconds.plus(duration);

                try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS)) {
                    mockedStatic.when(Instant::now).thenReturn(instantZeroSeconds);

                    overlay.updateVenom(6);
                    mockedStatic.when(Instant::now).thenReturn(instant19Seconds);
                    overlay.render(graphics2D);

                    verifyNoInteractions(actorOverlayRemover);
                    verifyNoInteractions(graphics2D);
                }
            }
        }

        @Nested
        class should_remove_overlay_if {
            @Test
            void actor_is_null() {
                overlay = new VenomTimerOverlay(config, npcManager, null, actorOverlayRemover);
                overlay.updateVenom(6);
                verifyOverlayRemovedOnRender(null);
            }

            @Test
            void actor_is_dead() {
                when(npc.isDead()).thenReturn(true);
                overlay.updateVenom(6);
                verifyOverlayRemovedOnRender(npc);
            }

            @Test
            void time_to_next_venom_was_over_9_seconds_ago() {
                Clock fixedClock = Clock.fixed(Instant.EPOCH, Clock.systemUTC().getZone());
                Duration duration = Duration.ofSeconds(30);
                Instant instantZeroSeconds = Instant.now(fixedClock);
                Instant instant30Seconds = instantZeroSeconds.plus(duration);

                try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS)) {
                    mockedStatic.when(Instant::now).thenReturn(instantZeroSeconds);

                    overlay.updateVenom(6);

                    mockedStatic.when(Instant::now).thenReturn(instant30Seconds);
                    verifyOverlayRemovedOnRender(npc);
                }
            }

            private void verifyOverlayRemovedOnRender(NPC npc) {
                overlay.render(graphics2D);

                verify(actorOverlayRemover).removeActorOverlay(npc, overlay);
                verifyNoInteractions(graphics2D);
            }
        }


        @Nested
        class should_draw_string {
            static final int EXPECTED_X = 0;
            static final int EXPECTED_Y = 100;

            @Test
            void next_damage() {
                setRenderFlags(true, false, false, false);

                when(npc.getCanvasTextLocation(eq(graphics2D), anyString(), anyInt()))
                        .thenAnswer((invocation) -> new Point(0, invocation.getArgument(2)));

                updateVenomAndRender(6);

                Color expectedColor = new Color(22,48,40).brighter().brighter();

                verifyStringRenderedWithColor(expectedColor, "Next damage: 8", EXPECTED_X, EXPECTED_Y);
            }

            @Test
            void total_damage() {
                setRenderFlags(false, true, false, false);

                when(npc.getCanvasTextLocation(eq(graphics2D), anyString(), anyInt()))
                        .thenAnswer((invocation) -> new Point(0, invocation.getArgument(2)));

                updateVenomAndRender(6);

                Color expectedColor = new Color(22,48,40).brighter().brighter();

                verifyStringRenderedWithColor(expectedColor, "Total damage: 6", EXPECTED_X, EXPECTED_Y);
            }

            @Test
            void venom_in() {
                setRenderFlags(false, false, true, false);

                when(npc.getCanvasTextLocation(eq(graphics2D), anyString(), anyInt()))
                        .thenAnswer((invocation) -> new Point(0, invocation.getArgument(2)));

                updateVenomAndRender(6);

                Color expectedColor = new Color(73,151,126);

                verifyStringRenderedWithColor(expectedColor, "Venom in:", EXPECTED_X, EXPECTED_Y);
            }

            @Test
            void death_in() {
                setRenderFlags(false, false, false, true);

                when(npc.getCanvasTextLocation(eq(graphics2D), anyString(), anyInt()))
                        .thenAnswer((invocation) -> new Point(0, invocation.getArgument(2)));

                when(npc.getHealthRatio()).thenReturn(30);
                when(npc.getHealthScale()).thenReturn(30);
                when(npcManager.getHealth(anyInt())).thenReturn(30);

                updateVenomAndRender(6);

                Color expectedColor = new Color(73,151,126);

                verifyStringRenderedWithColor(expectedColor, "Dead in:", EXPECTED_X, EXPECTED_Y);
            }
        }

        private void updateVenomAndRender(int damage) {
            overlay.updateVenom(damage);
            overlay.render(graphics2D);
        }
    }

    private void setRenderFlags(boolean nextDamage, boolean totalDamage, boolean nextVenom, boolean deathTime) {
        when(config.nextDamageEnabled()).thenReturn(nextDamage);
        when(config.totalDamageEnabled()).thenReturn(totalDamage);
        when(config.timeToNextVenomEnabled()).thenReturn(nextVenom);
        when(config.timeToDeathEnabled()).thenReturn(deathTime);
    }

    private void verifyStringRenderedWithColor(Color expectedColor, String string, int expectedX, int expectedY) {
        DrawStringTest.verifyStringRenderedWithColor(graphics2D, expectedColor, string, expectedX, expectedY);
    }
}