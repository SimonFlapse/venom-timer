package com.simonflapse.osrs.venom;

import com.google.inject.Provides;
import com.simonflapse.osrs.venom.events.OnHitsplatApplied;
import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@PluginDescriptor(
	name = "Venom Timer"
)
public class VenomTimerPlugin extends Plugin {
	@Inject
	private OnHitsplatApplied onHitsplatApplied;

	@Inject
	private OverlayOrchestrator overlayOrchestrator;

	@Inject
	private VenomTimerConfig config;

	private final AtomicBoolean active = new AtomicBoolean(false);

	@Override
	protected void startUp() {
		if (!config.overlayEnabled()) {
			active.set(false);
			return;
		}
		active.set(true);
		log.info("Venom Timer started!");
	}

	@Override
	protected void shutDown() {
		overlayOrchestrator.shutDown();
		log.info("Venom timer stopped!");
	}

	@Provides
	VenomTimerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VenomTimerConfig.class);
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
		if (!active.get()) {
			return;
		}
		this.onHitsplatApplied.onEvent(hitsplatApplied);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (!configChanged.getGroup().equals(VenomTimerConfig.CONFIG_GROUP)) {
			return;
		}

		if (configChanged.getKey().equals(VenomTimerConfig.OVERLAY_ENABLED)) {
            if (configChanged.getNewValue().equals("true")) {
				startUp();
            } else {
				shutDown();
            }
        }

	}
}
