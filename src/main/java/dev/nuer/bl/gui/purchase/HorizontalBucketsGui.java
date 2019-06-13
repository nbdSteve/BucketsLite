package dev.nuer.bl.gui.purchase;

import dev.nuer.bl.gui.AbstractGui;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.ColorUtil;
import dev.nuer.bl.utils.ItemUtil;

/**
 * Class that handles the horizontal gen bucket menu gui
 */
public class HorizontalBucketsGui extends AbstractGui {

    /**
     * Constructor to create the Gui, add all items with their respective listeners
     */
    public HorizontalBucketsGui() {
        super(FileManager.get("horizontal").getInt("gui.size"),
                ColorUtil.colorize(FileManager.get("horizontal").getString("gui.name")));

        if (FileManager.get("horizontal").getBoolean("gui.exit-button.shown")) {
            setItemInSlot(FileManager.get("horizontal").getInt("gui.exit-button.slot"),
                    ItemUtil.create("horizontal",
                            FileManager.get("horizontal").getString("gui.exit-button.material"),
                            "gui.exit-button", null), player -> {
                        player.closeInventory();
                    });
        }

        for (int i = 1; i <= 54; i++) {
            final int configItem = i;
            try {
                if (!FileManager.get("horizontal").getBoolean(configItem + ".gui.shown")) continue;
                setItemInSlot(FileManager.get("horizontal").getInt(configItem + ".gui.slot"),
                        ItemUtil.create("horizontal",
                                FileManager.get("horizontal").getString(configItem + ".gui.item"),
                                String.valueOf(configItem), null), player -> {
                            if (!FileManager.get("horizontal").getBoolean("gui.exit-button" +
                                    ".exit-menu-button")) {
                                ItemUtil.create("horizontal",
                                        FileManager.get("horizontal").getString(configItem + ".gui.item"),
                                        String.valueOf(configItem), player);
                                player.closeInventory();
                            }
                        });
            } catch (NullPointerException e) {
                //Item does not exist, do nothing
            }
        }
    }
}