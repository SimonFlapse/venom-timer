package com.simonflapse.osrs.venom;

import com.google.inject.Provides;
import com.simonflapse.osrs.venom.events.OnConfigChanged;
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

@Slf4j
@PluginDescriptor(
		name = "Venom Timer",
		description = "Visual indication of venom damage",
		tags = {"combat, venom"}
)
public class VenomTimerPlugin extends Plugin {

	@Inject
	private OnConfigChanged onConfigChanged;

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
	public VenomTimerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(VenomTimerConfig.class);
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
		this.onHitsplatApplied.onEvent(hitsplatApplied);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		this.onConfigChanged.onEvent(configChanged);
	}
}
