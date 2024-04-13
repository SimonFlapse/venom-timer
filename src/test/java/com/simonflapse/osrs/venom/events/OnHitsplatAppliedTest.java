package com.simonflapse.osrs.venom.events;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import net.runelite.api.Actor;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.annotations.HitsplatType;
import net.runelite.api.events.HitsplatApplied;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class OnHitsplatAppliedTest {

    OnHitsplatApplied onHitsplatApplied;
    VenomTimerConfig config;
    OverlayOrchestrator overlayOrchestrator;

    @BeforeEach
    void setUp() {
        config = new VenomTimerConfig() {};
        overlayOrchestrator = mock(OverlayOrchestrator.class);
        onHitsplatApplied = new OnHitsplatApplied(config, overlayOrchestrator);
    }

    @Nested
    class onEvent {
        @Test
        void should_update_damage() {
            int damage = 6;
            Actor actor = mock(Actor.class);

            HitsplatApplied hitsplatApplied = getHitsplatApplied(damage, actor);

            onHitsplatApplied.onEvent(hitsplatApplied);
            verify(overlayOrchestrator).updateDamage(actor, damage);
        }

        @Test
        void should_not_update_damage_if_actor_is_dead() {
            Actor actor = mock(Actor.class);
            when(actor.isDead()).thenReturn(true);
            HitsplatApplied hitsplatApplied = getHitsplatApplied(6, actor);

            onHitsplatApplied.onEvent(hitsplatApplied);
            verify(overlayOrchestrator, times(0)).updateDamage(any(), anyInt());
        }

        @Test
        void should_not_update_damage_if_not_venom() {
            Actor actor = mock(Actor.class);
            HitsplatApplied hitsplatApplied = getHitsplatApplied(HitsplatID.DAMAGE_ME, 6, actor);

            onHitsplatApplied.onEvent(hitsplatApplied);
            verify(overlayOrchestrator, times(0)).updateDamage(any(), anyInt());
        }

        @Test
        void should_not_update_damage_if_no_damage() {
            Actor actor = mock(Actor.class);
            HitsplatApplied hitsplatApplied = getHitsplatApplied(0, actor);

            onHitsplatApplied.onEvent(hitsplatApplied);
            verify(overlayOrchestrator, times(0)).updateDamage(any(), anyInt());
        }

        @Test
        void should_not_update_damage_if_overlay_disabled() {
            config = new VenomTimerConfig() {
                @Override
                public boolean overlayEnabled() {
                    return false;
                }
            };
            onHitsplatApplied = new OnHitsplatApplied(config, overlayOrchestrator);
            HitsplatApplied hitsplatApplied = new HitsplatApplied();

            onHitsplatApplied.onEvent(hitsplatApplied);
            verify(overlayOrchestrator, times(0)).updateDamage(any(), anyInt());
        }
    }

    private static HitsplatApplied getHitsplatApplied(int damage) {
        return getHitsplatApplied(damage, mock(Actor.class));
    }

    private static HitsplatApplied getHitsplatApplied(int damage, Actor actor) {
        return getHitsplatApplied(HitsplatID.VENOM, damage, actor);
    }

    private static HitsplatApplied getHitsplatApplied(@HitsplatType int type, int damage, Actor actor) {
        HitsplatApplied hitsplatApplied = new HitsplatApplied();
        hitsplatApplied.setHitsplat(new Hitsplat(type, damage, 0));
        hitsplatApplied.setActor(actor);
        return hitsplatApplied;
    }
}