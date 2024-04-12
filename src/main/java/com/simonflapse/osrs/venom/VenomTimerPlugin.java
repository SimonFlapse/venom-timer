package com.simonflapse.osrs.venom;

import com.google.inject.Provides;
import com.simonflapse.osrs.venom.events.OnHitsplatApplied;
import com.simonflapse.osrs.venom.ui.OverlayOrchestrator;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Venom Timer"
)
public class VenomTimerPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private VenomTimerConfig config;

	@Inject
	private OnHitsplatApplied onHitsplatApplied;

	@Inject
	private OverlayOrchestrator overlayOrchestrator;

	@Override
	protected void startUp() {
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
		this.onHitsplatApplied.onEvent(hitsplatApplied);
	}
}
