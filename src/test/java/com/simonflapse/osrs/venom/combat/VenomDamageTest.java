package com.simonflapse.osrs.venom.combat;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.client.game.NPCManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.ParameterizedTest.DEFAULT_DISPLAY_NAME;
import static org.mockito.Mockito.*;

class VenomDamageTest {
    NPCManager npcManager;
    Actor actor;
    VenomDamage venomDamage;

    @BeforeEach
    void setUp() {
        npcManager = Mockito.mock(NPCManager.class);
        actor = Mockito.mock(NPC.class);
        venomDamage = new VenomDamage(npcManager, actor);
    }

    @Nested
    class updateVenom {
        @Test
        void should_update_total_damage() {
            assertEquals(0, venomDamage.getTotalDamage());

            venomDamage.updateVenom(6);

            assertEquals(6, venomDamage.getTotalDamage());
        }

        @Test
        void should_update_append_total_damage() {
            venomDamage.updateVenom(6);
            venomDamage.updateVenom(8);

            assertEquals(14, venomDamage.getTotalDamage());
        }

        @ParameterizedTest(name = DEFAULT_DISPLAY_NAME + " venom damage")
        @ValueSource(ints = {6, 8, 10, 12, 14, 16, 18, 20})
        void should_update_next_damage(int damage) {
            venomDamage.updateVenom(damage);

            int expectedNextDamage = Math.min(20, damage + 2);

            assertEquals(expectedNextDamage, venomDamage.getNextDamage());
        }
    }

    @Nested
    class timeDifference {
        @Test
        void should_return_5_when_14_seconds_have_elapsed_since_updateVenom() {
            Clock fixedClock = Clock.fixed(Instant.EPOCH, Clock.systemUTC().getZone());
            Duration duration = Duration.ofSeconds(14);
            Instant instantZeroSeconds = Instant.now(fixedClock);
            Instant instant14Seconds = instantZeroSeconds.plus(duration);

            try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(Instant::now).thenReturn(instantZeroSeconds);
                venomDamage.updateVenom(6);
                mockedStatic.when(Instant::now).thenReturn(instant14Seconds);
                long timeDifference = venomDamage.timeDifference();
                assertEquals(5, timeDifference);
            }
        }
    }

    @Nested
    class calculateTimeToDeath {
        @Test
        void should_return_41_seconds_for_npc_with_20_hp() {
            mockActorHealth(20, 30, 30);
            venomDamage.updateVenom(6);
            long timeToDeath = venomDamage.calculateTimeToDeath(5);
            assertEquals(5, timeToDeath % 18);
            assertEquals(41, timeToDeath);
        }

        @Test
        void should_return_negative_1_seconds_for_player() {
            actor = mock(Player.class);
            venomDamage = new VenomDamage(npcManager, actor);
            long timeToDeath = venomDamage.calculateTimeToDeath(5);
            assertEquals(-1, timeToDeath);
        }

        @Test
        void should_return_negative_1_seconds_for_npc_with_0_max_health() {
            long timeToDeath = venomDamage.calculateTimeToDeath(5);
            assertEquals(-1, timeToDeath);
        }

        @Test
        void should_return_negative_1_seconds_for_npc_with_null_max_health() {
            when(npcManager.getHealth(ArgumentMatchers.anyInt())).thenReturn(null);
            long timeToDeath = venomDamage.calculateTimeToDeath(5);
            assertEquals(-1, timeToDeath);
        }

        @Test
        void should_return_correct_time_even_if_healthScale_and_healthRation_information_is_lost() {
            mockActorHealth(30, 30, 30);
            venomDamage.updateVenom(6);
            venomDamage.calculateTimeToDeath(5);

            mockActorHealth(30, -1, -1);
            long timeToDeath = venomDamage.calculateTimeToDeath(5);
            assertEquals(5, timeToDeath % 18);
            assertEquals(41, timeToDeath);
        }
    }

    private void mockActorHealth(int maxHealth, int healthScale, int healthRatio) {
        when(npcManager.getHealth(ArgumentMatchers.anyInt())).thenReturn(maxHealth);
        when(actor.getHealthScale()).thenReturn(healthScale);
        when(actor.getHealthRatio()).thenReturn(healthRatio);
    }
}