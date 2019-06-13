package dev.nuer.bl.gui.purchase;

import dev.nuer.bl.gui.AbstractGui;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.ColorUtil;
import dev.nuer.bl.utils.ItemUtil;

/**
 * Class that handles the vertical gen bucket menu gui
 */
public class VerticalBucketsGui extends AbstractGui {

    /**
     * Constructor to create the Gui, add all items with their respective listeners
     */
    public VerticalBucketsGui() {
        super(FileManager.get("vertical").getInt("gui.size"),
                ColorUtil.colorize(FileManager.get("vertical").getString("gui.name")));

        if (FileManager.get("vertical").getBoolean("gui.exit-button.shown")) {
            setItemInSlot(FileManager.get("vertical").getInt("gui.exit-button.slot"),
                    ItemUtil.create("vertical",
                            FileManager.get("vertical").getString("gui.exit-button.material"),
                            "gui.exit-button", null), player -> {
                        player.closeInventory();
                    });
        }

        for (int i = 1; i <= 54; i++) {
            final int configItem = i;
            try {
                if (!FileManager.get("vertical").getBoolean(configItem + ".gui.shown")) continue;
                setItemInSlot(FileManager.get("vertical").getInt(configItem + ".gui.slot"),
                        ItemUtil.create("vertical",
                                FileManager.get("vertical").getString(configItem + ".gui.item"),
                                String.valueOf(configItem), null), player -> {
                            ItemUtil.create("vertical",
                                    FileManager.get("vertical").getString(configItem + ".gui.item"),
                                    String.valueOf(configItem), player);
                            player.closeInventory();
                        });
            } catch (NullPointerException e) {
                //Item does not exist, do nothing
            }
        }
    }
}
