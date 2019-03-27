package dev.nuer.vbuckets.event.gui;

import dev.nuer.vbuckets.file.LoadProvidedFiles;
import dev.nuer.vbuckets.vBuckets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Class to handle the gui click method
 */
public class GuiClick implements Listener {
    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();
    //Get the cooldown hashmap
    private HashMap<UUID, Long> GuiCDT = ((vBuckets) pl).getGuiCDT();

    /**
     * Event to handle getting gen buckets from the menu GUI
     *
     * @param e event, cannot be null
     */
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        //Store the player
        Player p = (Player) e.getWhoClicked();
        //Store the inventory
        Inventory inven = e.getClickedInventory();
        //Check that the inventory clicked was this inventory
        if (inven != null) {
            if (inven.getName().equals(ChatColor.translateAlternateColorCodes('&',
                    lpf.getConfig().getString("gui.name")))) {
                e.setCancelled(true);
                //Store the details about the clicked bucket
                ItemMeta toolMeta = e.getCurrentItem().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String bucketType = null;
                String perm = null;
                NumberFormat df = new DecimalFormat("#,###");
                //Check to see if it is a valid tool
                if (!toolMeta.getDisplayName().equalsIgnoreCase(" ")) {
                    for (int i = 1; i <= 54; i++) {
                        String bucket = "bucket-" + String.valueOf(i);
                        try {
                            lpf.getBuckets().getString(bucket + ".unique");
                            if (toolLore.contains(ChatColor.translateAlternateColorCodes('&',
                                    lpf.getBuckets().getString(bucket + ".unique")))) {
                                perm = String.valueOf(i);
                                bucketType = bucket;
                            }
                        } catch (Exception ex) {
                            //Do nothing, this tool isn't active or doesn't exist
                        }
                    }
                    if (bucketType == null) {
                        return;
                    }
                    //Check that the player has permission to buy that tool
                    if (p.hasPermission("vbucket.gui." + perm)) {
                        if (p.getInventory().firstEmpty() != -1) {
                            //Check that the player has enough money to buy the tool
                            int CDT = lpf.getConfig().getInt("gui.cooldown-per-bucket");
                            //Check to see if the player is currently on cooldown
                            if (CDT != -1 && CDT >= 0) {
                                if (GuiCDT.containsKey(p.getUniqueId())) {
                                    long cooldown =
                                            ((GuiCDT.get(p.getUniqueId()) / 1000) + CDT) - (System.currentTimeMillis() / 1000);
                                    if (cooldown > 0) {
                                        for (String line : lpf.getMessages().getStringList("cooldown")) {
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("%cooldown%", String.valueOf(cooldown)));
                                        }
                                    } else {
                                        GuiCDT.remove(p.getUniqueId());
                                    }
                                    return;
                                } else {
                                    GuiCDT.put(p.getUniqueId(), System.currentTimeMillis());
                                }
                            }
                            //Create the bucket being bought
                            ItemStack bucket =
                                    new ItemStack(Material.valueOf(lpf.getBuckets().getString(bucketType +
                                            ".item").toUpperCase()));
                            ItemMeta bucketMeta = bucket.getItemMeta();
                            List<String> bucketLore = new ArrayList<>();
                            bucketMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                    lpf.getBuckets().getString(bucketType + ".name")));
                            for (String lore : lpf.getBuckets().getStringList(bucketType + ".lore")) {
                                bucketLore.add(ChatColor.translateAlternateColorCodes('&', lore));
                            }
                            if (lpf.getBuckets().getBoolean(bucketType + ".glowing")) {
                                bucketMeta.addEnchant(Enchantment.LURE, 1, true);
                                bucketMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            }
                            bucketMeta.setLore(bucketLore);
                            bucket.setItemMeta(bucketMeta);
                            //Give the player the bucket
                            p.getInventory().addItem(bucket);
                            for (String line : lpf.getMessages().getStringList("purchase")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            p.closeInventory();
                        } else {
                            for (String line : lpf.getMessages().getStringList("inventory-full")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            p.closeInventory();
                        }
                    } else {
                        for (String line : lpf.getMessages().getStringList("no-buy-permission")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                        p.closeInventory();
                    }
                }
            }
        }
    }
}