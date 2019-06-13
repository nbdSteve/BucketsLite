package dev.nuer.bl.gui;

import dev.nuer.bl.bucket.GenBucket;
import dev.nuer.bl.gui.player.GenerationPageGui;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class that handles Gui creation and has methods to check which Gui is open for a player
 */
public abstract class AbstractGui {
    //Store the map of inventories that are created, static so it can be accessed and modified outside the
    // class
    public static HashMap<UUID, AbstractGui> inventoriesByID = new HashMap<>();
    //Store the map of inventories that are open for players, static so it can be accessed and modified
    // outside the class
    public static HashMap<UUID, UUID> openInventories = new HashMap<>();

    //Store the id of the inventory
    private UUID inventoryID;
    //Store the inventory being created
    private Inventory inventory;
    //Store the map of actions to be called in the InventoryClickEvent
    private HashMap<Integer, inventoryClickActions> clickActions;

    /**
     * Constructor the create a new Gui
     *
     * @param inventorySize Integer, size of the Gui
     * @param inventoryName String, name of the Gui
     */
    public AbstractGui(int inventorySize, String inventoryName) {
        inventoryID = UUID.randomUUID();
        inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
        clickActions = new HashMap<>();
        inventoriesByID.put(getInventoryID(), this);
    }

    /**
     * Get the inventory map
     *
     * @return
     */
    public static HashMap<UUID, AbstractGui> getInventoriesByID() {
        return inventoriesByID;
    }

    /**
     * Get the open inventories map
     *
     * @return
     */
    public static HashMap<UUID, UUID> getOpenInventories() {
        return openInventories;
    }

    /**
     * Will the the given item to the respective slot in the Gui
     *
     * @param guiSlot     Integer, slot in the Gui
     * @param item        ItemStack, the item to place there
     * @param clickAction the event called when the player clicks this item
     */
    public void setItemInSlot(int guiSlot, ItemStack item, inventoryClickActions clickAction) {
        inventory.setItem(guiSlot, item);
        if (clickAction != null) {
            clickActions.put(guiSlot, clickAction);
        }
    }

    /**
     * Void method that will open the inventory for the player
     *
     * @param player Player, the player to open the inventory for
     */
    public void open(Player player) {
        player.openInventory(inventory);
        openInventories.put(player.getUniqueId(), getInventoryID());
    }

    /**
     * Void method to delete all of the open inventories
     */
    public void delete() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (openInventories.get(player.getUniqueId()).equals(getInventoryID())) {
                player.closeInventory();
            }
        }
        inventoriesByID.remove(getInventoryID());
    }

    /**
     * Getter for the inventory
     *
     * @return
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Getter for the inventory ID
     *
     * @return
     */
    public UUID getInventoryID() {
        return inventoryID;
    }

    /**
     * Getter for the inventory actions map
     *
     * @return
     */
    public HashMap<Integer, inventoryClickActions> getClickActions() {
        return clickActions;
    }

    /**
     * Interface to create a click action
     */
    public interface inventoryClickActions {
        void itemClick(Player player);
    }

    public void pageItems(Player player, int startingSlot, int endSlot, HashMap<GenBucket, Integer> buckets, int page, boolean first) {
        if (!first) {
            setItemInSlot(48,
                    ItemUtil.create("config",
                            FileManager.get("config").getString("player-gui.page.previous.material"),
                            "player-gui.page.previous", null),
                    player1 -> {
                        new GenerationPageGui(player, startingSlot-45, startingSlot, buckets, page - 1).open(player);
                    });
        }
        setItemInSlot(49,
                ItemUtil.create("config",
                        FileManager.get("config").getString("player-gui.page.exit.material"),
                        "player-gui.page.exit", null),
                player1 -> {
            player.closeInventory();
        });
        setItemInSlot(50,
                ItemUtil.create("config",
                        FileManager.get("config").getString("player-gui.page.next.material"),
                "player-gui.page.next", null),
                player1 -> {
            new GenerationPageGui(player, endSlot, endSlot+45, buckets, page + 1).open(player);
        });
    }
}
