package com.simonflapse.osrs.venom;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class VenomTimerPluginTest
{
	@SuppressWarnings({"unchecked", "varargs"})
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(VenomTimerPlugin.class);
		RuneLite.main(args);
	}
}