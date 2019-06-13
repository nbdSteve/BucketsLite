package dev.nuer.bl.utils;

import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles creating and item
 */
public class ItemUtil {

    /**
     * Creates an item from the specified arguments
     *
     * @param typeOfBucket String, the type of bucket the player is using
     * @param material     String, the item material
     * @param bucketID     String, the id of the bucket being placed
     * @param player       Player, the player to give the item to
     */
    public static ItemStack create(String typeOfBucket, String material, String bucketID, Player player) {
        String[] materialParts = material.split(":");
        ItemStack item = new ItemStack(Material.getMaterial(materialParts[0].toUpperCase()), 1, Byte.parseByte(materialParts[1]));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ColorUtil.colorize(FileManager.get(typeOfBucket).getString(bucketID + ".name")));
        List<String> itemLore = new ArrayList<>();
        for (String line : FileManager.get(typeOfBucket).getStringList(bucketID + ".lore")) {
            itemLore.add(ColorUtil.colorize(line));
        }
        itemMeta.setLore(itemLore);
        for (String enchantment : FileManager.get(typeOfBucket).getStringList(bucketID + ".enchantments")) {
            String[] enchantmentParts = enchantment.split(":");
            itemMeta.addEnchant(Enchantment.getByName(enchantmentParts[0].toUpperCase()),
                    Integer.parseInt(enchantmentParts[1]), true);
        }
        for (String flag : FileManager.get(typeOfBucket).getStringList(bucketID + ".item-flags")) {
            itemMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
        }
        item.setItemMeta(itemMeta);
        //If the player is not null give them a gen bucket
        if (player != null) {
            //Set all of the NBT data for the item
            NBTItem nbtItem = new NBTItem(item);
            nbtItem.setBoolean("buckets-lite.gen-bucket", true);
            nbtItem.setString("buckets-lite.type", typeOfBucket);
            nbtItem.setString("buckets-lite.material", FileManager.get(typeOfBucket).getString(bucketID + ".material"));
            nbtItem.setInteger("buckets-lite.delay", FileManager.get(typeOfBucket).getInt(bucketID + ".delay"));
            nbtItem.setInteger("buckets-lite.generation-length", FileManager.get(typeOfBucket).getInt(bucketID + ".generation-length"));
            nbtItem.setDouble("buckets-lite.price", FileManager.get(typeOfBucket).getDouble(bucketID + ".price"));
            nbtItem.setBoolean("buckets-lite.pseudo", FileManager.get(typeOfBucket).getBoolean(bucketID + ".pseudo"));
            //Add the item to their inventory
            player.getInventory().addItem(nbtItem.getItem());
            return nbtItem.getItem();
        }
        return item;
    }

    /**
     * Alternate method to creating an item stack for the player
     *
     * @param material         String, the material of the item
     * @param name_placeholder String, the names placeholder
     * @param name_replacement String, the replacement for the placeholder in the name
     * @param placeholder_1    String, lore placeholder 1
     * @param replacement_1    String, lore replacement 1
     * @param placeholder_2    String, lore placeholder 2
     * @param replacement_2    String, lore replacement 2
     * @param placeholder_3    String, lore placeholder 3
     * @param replacement_3    String, lore replacement 3
     * @param placeholder_4    String, lore placeholder 4
     * @param replacement_4    String, lore relpacement 4
     * @return
     */
    public static ItemStack createPlayerGuiItem(String material,
                                                String name_placeholder, String name_replacement,
                                                String placeholder_1, String replacement_1,
                                                String placeholder_2, String replacement_2,
                                                String placeholder_3, String replacement_3,
                                                String placeholder_4, String replacement_4) {
        String[] materialParts = material.split(":");
        ItemStack item = new ItemStack(Material.getMaterial(materialParts[0].toUpperCase()), 1, Byte.parseByte(materialParts[1]));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ColorUtil.colorize(FileManager.get("config").getString("player-gui.buckets.name").replace(name_placeholder, name_replacement)));
        List<String> itemLore = new ArrayList<>();
        for (String line : FileManager.get("config").getStringList("player-gui.buckets.lore")) {
            itemLore.add(ColorUtil.colorize(line)
                    .replace(placeholder_1, replacement_1)
                    .replace(placeholder_2, replacement_2)
                    .replace(placeholder_3, replacement_3)
                    .replace(placeholder_4, replacement_4));
        }
        itemMeta.setLore(itemLore);
        for (String enchantment : FileManager.get("config").getStringList("player-gui.buckets.enchantments")) {
            String[] enchantmentParts = enchantment.split(":");
            itemMeta.addEnchant(Enchantment.getByName(enchantmentParts[0].toUpperCase()),
                    Integer.parseInt(enchantmentParts[1]), true);
        }
        for (String flag : FileManager.get("config").getStringList("player-gui.buckets.item-flags")) {
            itemMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}
