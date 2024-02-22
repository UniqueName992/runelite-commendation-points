package com.pestPoints;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Pest Control Points",
	description = "Shows the number of Pest Control Commendation Points during Pest Control Games",
	tags = {"pest", "control", "commendation", "points", "currency"}
)
public class pestPointsPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private pestPointsOverlay overlay;
	@Inject
	private pestPointsConfig config;
	@Override
	protected void startUp() throws Exception {
		overlayManager.add(overlay);
	}
	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
	}
	@Provides
	pestPointsConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(pestPointsConfig.class);
	}
}
