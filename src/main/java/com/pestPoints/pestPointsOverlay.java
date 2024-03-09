package com.pestPoints;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Map;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.LineComponent;

public class pestPointsOverlay extends Overlay {
    private final Client client;
    private final ConfigManager configMan;
    private final PanelComponent panelComponent = new PanelComponent();
    private static final int PC_REWARDS_BAR = 15925253;
    private static final int PC_POINTS_VARP = 261;
    private static final int PC_BOAT_POINTS = 26673157;
    private static final int PC_BOAT_DIFFICULTY=26673172;
    private static final int PC_Chat = 15138822;
    Widget PC_SUCCESS=null;
    private final Map<String, Integer> points_map = Map.of(
        "Novice",       3,
        "Intermediate", 4,
        "Veteran",      5
    );
    @Inject
    private pestPointsConfig config;
    private boolean fired = false;
    @Inject
    private pestPointsOverlay(Client client, ConfigManager configMan, ConfigManager configMan1, pestPointsConfig config) {
        this.configMan = configMan;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.config = config;
    }

    private void putConf(String key, Object val) {
        configMan.setConfiguration("pest-control-points", key, val);
    }

    private Object getConf (String key) {
        return configMan.getConfiguration("pest-control-points", key);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        // FOR DEBUG
//        configMan.unsetConfiguration("pest-control-points", "Points");
//        configMan.unsetConfiguration("pest-control-points", "Difficulty");
//        configMan.unsetConfiguration("pest-control-points", "CA");

        // If we aren't in a game and not on the void island, don't draw
        if (client.getWidget(ComponentID.PEST_CONTROL_BLUE_SHIELD) == null && client.getLocalPlayer().getWorldLocation().getRegionID() != 10537) { return null; }

        // If Boat interface shown, scrape commendation points
        if (client.getWidget(PC_BOAT_POINTS) != null) { putConf("Points", client.getWidget(PC_BOAT_POINTS).getText().replaceAll("\\D+","")); }

        // If Store Window is open, populate commendation points from varp (only time it updates)
        if (client.getWidget(PC_REWARDS_BAR) != null) { putConf("Points", Integer.toString(client.getVarpValue(PC_POINTS_VARP))); }

        // if on boat, scrape difficulty
        if (client.getWidget(PC_BOAT_DIFFICULTY) != null) { putConf("Difficulty", points_map.get(client.getWidget(PC_BOAT_DIFFICULTY).getText())); }

        // If PestPoints unknown, default to 0
        if (getConf("Points") == null) { putConf("Points", 0); }

        // If no difficulty then default Novice
        if (getConf("Difficulty") == null) { putConf("Difficulty", 3); }

        // If no CA Factor, Default to none
        if (getConf("CA") == null) { putConf("CA", 0); }

        // If success message, scrape points gained, correct CA factor and recalc points
        PC_SUCCESS = client.getWidget(PC_Chat);
        if(PC_SUCCESS != null) {
            if (PC_SUCCESS.getText().contains("Congratulations! You managed to destroy all the portals!")) {
                if (!fired) {
                    putConf("CA", Integer.parseInt(client.getWidget(PC_Chat).getText().replaceAll("[^0-9]", "")) - Integer.parseInt(getConf("Difficulty").toString()));
                    putConf("Points", Integer.parseInt(getConf("Points").toString()) + Integer.parseInt(client.getWidget(PC_Chat).getText().replaceAll("[^0-9]", "")));
                    fired = true;
                }
            }
        } else { fired = false; }

        // Calculate Points per Game
        String pestPointsPerGame = String.valueOf(Integer.parseInt(getConf("Difficulty").toString()) + Integer.parseInt(getConf("CA").toString()));

        // prep point display string
        String pestPointsDisplay = getConf("Points").toString();

        // if showing point gains, concat them to the string
        if (config.showGain()) { pestPointsDisplay = pestPointsDisplay + " (+" + pestPointsPerGame + ")"; }

        panelComponent.getChildren().clear();
        String overlayTitle = "Pest Points";

        // Add a line on the overlay for pestPoints
        panelComponent.getChildren().add(LineComponent.builder()
                .left(overlayTitle)
                .right(pestPointsDisplay)
                .build());

        // If showing point estimate, calculate and show
        if (config.targetEnabled()) {
            String overlaySub   = "Games Left";
            int gamesLeft = (int) Math.ceil((double) (config.target() - Double.parseDouble(getConf("Points").toString())) / Double.parseDouble(pestPointsPerGame));
            if (gamesLeft < 0) { gamesLeft = 0; }
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(overlaySub)
                    .right(String.valueOf(gamesLeft))
                    .build());
        }

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(overlayTitle) + 30 + graphics.getFontMetrics().stringWidth(pestPointsDisplay),
                0));

        return panelComponent.render(graphics);
    }
}
