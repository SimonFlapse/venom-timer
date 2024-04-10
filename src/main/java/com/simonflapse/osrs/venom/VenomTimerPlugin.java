package com.simonflapse.osrs.venom;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Venom Timer"
)
public class VenomTimerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private VenomTimerConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Venom Timer started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Venom timer stopped!");
	}

	@Provides
	VenomTimerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VenomTimerConfig.class);
	}
}
