package com.pestPoints;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface pestPointsConfig extends Config
{
	@ConfigItem(
			keyName = "targetEnable",
			name = "Enable Points Target",
			description = "Option for calculating the number of games needed to achieve a number of points."
	) default boolean targetEnabled() { return false; }

	@ConfigItem(
			keyName = "target",
			name = "Points Goal",
			description = "How many points you intend to grind"
	) default int target()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "showGain",
			name = "Show Points per Game",
			description = "Display the number of points that each game will award"
	) default boolean showGain() { return false; }
}
