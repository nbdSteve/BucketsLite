package dev.nuer.bl.gui.purchase;

import dev.nuer.bl.gui.AbstractGui;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.ColorUtil;
import dev.nuer.bl.utils.ItemUtil;
import dev.nuer.bl.utils.MessageUtil;

/**
 * class that handles the main gui menu for the player
 */
public class MenuGui extends AbstractGui {

    /**
     * Constructor to create the Gui, add all items with their respective listeners
     */
    public MenuGui() {
        super(FileManager.get("config").getInt("menu-gui.size"),
                ColorUtil.colorize(FileManager.get("config").getString("menu-gui.name")));

        for (int i = 1; i <= 54; i++) {
            final int configItem = i;
            try {
                setItemInSlot(FileManager.get("config").getInt("menu-gui." + configItem + ".slot"),
                        ItemUtil.create("config",
                                FileManager.get("config").getString("menu-gui." + configItem + ".material"),
                                "menu-gui." + configItem, null), player -> {
                            try {
                                if (FileManager.get("config").getBoolean("menu-gui." + configItem + ".open" +
                                        "-vertical-menu")) {
                                    new VerticalBucketsGui().open(player);
                                }
                                if (FileManager.get("config").getBoolean("menu-gui." + configItem + ".open" +
                                        "-horizontal-menu")) {
                                    new HorizontalBucketsGui().open(player);
                                }
                                if (FileManager.get("config").getBoolean("menu-gui." + configItem + ".exit" +
                                        "-menu-button")) {
                                    player.closeInventory();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                player.closeInventory();
                                MessageUtil.message("messages", "invalid-configuration", player, "{reason}",
                                        "problem relating to BucketsLite menu gui, located in buckets-lite.yml");
                            }
                        });
            } catch (NullPointerException e) {
                //Do nothing this was not found
            }
        }
    }
}