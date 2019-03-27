package dev.nuer.vbuckets.gui;

import dev.nuer.vbuckets.file.LoadProvidedFiles;
import dev.nuer.vbuckets.vBuckets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Merchant Gui class, this creates and adds the gen buckets to the Merchant Gui.
 */
public class BucketGui {
    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();

    /**
     * Method to create the gui whenever the player runs the /vb command
     *
     * @param p player, cannot be null
     */
    public void gui(Player p) {
        // Creating the inventory with the name & size from the config.yml.
        Inventory i = pl.getServer().createInventory(null, lpf.getConfig().getInt("gui.size"),
                ChatColor.translateAlternateColorCodes('&', lpf.getConfig().getString("gui.name")));
        String[] parts = lpf.getConfig().getString("gui.fill-item").split("-");
        int b = Integer.parseInt(parts[1]);
        // Creating the item that will fill all of the free slots in the GUI.
        ItemStack f1 = new ItemStack(Material.valueOf(parts[0].toUpperCase()),
                lpf.getConfig().getInt("gui.fill-item-amount"), (byte) b);
        ItemMeta f1M = f1.getItemMeta();
        f1M.setDisplayName(" ");
        // Setting if the fill item will be glowing or not.
        if (lpf.getConfig().getBoolean("gui.fill-item-glowing")) {
            f1M.addEnchant(Enchantment.LURE, 1, true);
            f1M.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        f1.setItemMeta(f1M);
        //Fill the rest of the GUI with the filler item
        for (int z = 0; z < lpf.getConfig().getInt("gui.size"); z++) {
            i.setItem(z, f1);
        }
        //Add all of the buckets to the inventory
        for (int x = 1; x <= 54; x++) {
            String bucket = "bucket-" + String.valueOf(x);
            if (lpf.getBuckets().getBoolean(bucket + ".in-gui")) {
                //Create the bucket
                ItemStack vBucket = new ItemStack(
                        Material.valueOf(lpf.getBuckets().getString(bucket + ".item").toUpperCase()), 1);
                ItemMeta vBucketMeta = vBucket.getItemMeta();
                List<String> vBucketLore = new ArrayList<String>();
                vBucketMeta.setDisplayName(
                        ChatColor.translateAlternateColorCodes('&', lpf.getBuckets().getString(bucket +
                                ".name")));
                for (String lore : lpf.getBuckets().getStringList(bucket + ".lore")) {
                    vBucketLore.add(ChatColor.translateAlternateColorCodes('&', lore));
                }
                if (lpf.getBuckets().getBoolean(bucket + ".glowing")) {
                    vBucketMeta.addEnchant(Enchantment.LURE, 1, true);
                    vBucketMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                vBucketMeta.setLore(vBucketLore);
                vBucket.setItemMeta(vBucketMeta);
                //Add it to the gui
                i.setItem(lpf.getBuckets().getInt(bucket + ".gui-slot"), vBucket);
            } else {

            }
        }
        p.openInventory(i);
    }
}
