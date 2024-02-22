package com.pestPoints;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class pestPointsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(pestPointsPlugin.class);
		RuneLite.main(args);
	}
}