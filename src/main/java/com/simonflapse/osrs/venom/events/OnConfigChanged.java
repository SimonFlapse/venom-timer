package com.simonflapse.osrs.venom.events;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class OnConfigChanged {
    private final Map<String, Consumer<String>> configurationConsumers = new HashMap<>();

    private final OverlayOrchestrator overlayOrchestrator;

    @Inject
    public OnConfigChanged(OverlayOrchestrator overlayOrchestrator) {
        initializeConsumersMap();

        this.overlayOrchestrator = overlayOrchestrator;
    }

    private void initializeConsumersMap() {
        configurationConsumers.put(VenomTimerConfig.OVERLAY_ENABLED, this::overlayConfigChanged);
    }

    public void onEvent(ConfigChanged configChanged) {
        if (!isVenomTimerConfig(configChanged)) {
            return;
        }

        configurationChanged(configChanged);
    }

    private void configurationChanged(ConfigChanged configChanged) {
        String configurationName = configChanged.getKey();
        String configurationValue = configChanged.getNewValue();
        Consumer<String> consumer = configurationConsumers.get(configurationName);
        if (consumer != null) {
            consumer.accept(configurationValue);
        }
    }

    private static boolean isVenomTimerConfig(ConfigChanged configChanged) {
        return configChanged.getGroup().equals(VenomTimerConfig.CONFIG_GROUP);
    }

    private void removeOverlays() {
        overlayOrchestrator.shutDown();
    }

    private void overlayConfigChanged(String newValue) {
        boolean enabled = Boolean.parseBoolean(newValue);
        if (!enabled) {
            removeOverlays();
        }
    }
}
