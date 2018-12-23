package com.nbdsteve.vbuckets.event;

import com.nbdsteve.vbuckets.file.LoadProvidedFiles;
import com.nbdsteve.vbuckets.support.Factions;
import com.nbdsteve.vbuckets.support.WorldGuard;
import com.nbdsteve.vbuckets.vBuckets;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Interact event class
 */
public class InteractEvent implements Listener {
    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();
    //Get the server economy
    private Economy econ = vBuckets.getEconomy();
    //Private variable for the runnable in the loop
    private int taskid;

    /**
     * All code for the interact event is handled in this method
     *
     * @param e the event, cannot be null
     */
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        //Get the player
        Player p = e.getPlayer();
        //Get the action
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //Check that the player has a vBucket in their hand
            if (p.getItemInHand().hasItemMeta()) {
                if (p.getItemInHand().getItemMeta().hasLore()) {
                    //Store the information about the vBucket
                    ItemMeta bucketMeta = p.getItemInHand().getItemMeta();
                    List<String> bucketLore = bucketMeta.getLore();
                    String bucketType = null;
                    for (int x = 1; x <= 54; x++) {
                        String bucket = "bucket-" + String.valueOf(x);
                        try {
                            lpf.getBuckets().getString(bucket + ".unique");
                            if (bucketLore.contains(ChatColor.translateAlternateColorCodes('&',
                                    lpf.getBuckets().getString(bucket + ".unique")))) {
                                bucketType = bucket;
                            }
                        } catch (Exception ex) {
                            //Do nothing, this bucket just doesn't exist.
                        }
                    }
                    if (bucketType == null) {
                        return;
                    }
                    e.setCancelled(true);
                    boolean wg = false;
                    boolean fac = false;
                    //Figure out which plugins are being used and what to support
                    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
                        wg = true;
                    }
                    if (Bukkit.getPluginManager().getPlugin("MassiveCore") != null) {
                        fac = true;
                    } else if (Bukkit.getServer().getPluginManager().getPlugin("Factions") != null) {
                        fac = true;
                    }
                    //Store the price for the gen bucket
                    double price = lpf.getBuckets().getDouble(bucketType + ".price-per-use");
                    if (econ.getBalance(p) >= price) {
                        //Store the blocks that are going to be changed by the gen bucket
                        ArrayList<Block> blocks = new ArrayList<>();
                        //Store the bucket type as a final variable
                        final String bt = bucketType;
                        //Store the initial blocks coordinates
                        int x = e.getClickedBlock().getX();
                        int z = e.getClickedBlock().getZ();
                        //Code for the vertical bucket
                        if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                "vertical")) {
                            //Start the generation from this height
                            int y = lpf.getBuckets().getInt(bucketType + ".generation-start-height") - 1;
                            //While the block being replaced is air, add these blocks to be changed
                            while (p.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR) && y > 0) {
                                if (wg && !WorldGuard.allowsBreak(p.getWorld().getBlockAt(x, y, z).getLocation())) {
                                    //Do nothing
                                } else if (fac && !Factions.canBreakBlock(p, p.getWorld().getBlockAt(x, y,
                                        z))) {
                                    //Do nothing
                                } else if (isInsideBorder(p.getWorld().getBlockAt(x, y, z), e, p)) {
                                    //Do nothing
                                } else {
                                    blocks.add(p.getWorld().getBlockAt(x, y, z));
                                }
                                y--;
                            }
                        } else if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                "horizontal")) {
                            //Store the coordinates for the starting block
                            int y = e.getClickedBlock().getY();
                            int end = x + (lpf.getBuckets().getInt(bucketType + ".horizontal-gen-length"));
                            int start = x + 1;
                            //Check that the block being replaced is air
                            while (p.getWorld().getBlockAt(start, y, z).getType().equals(Material.AIR) && start < end) {
                                if (wg && !WorldGuard.allowsBreak(p.getWorld().getBlockAt(x, y, z).getLocation())) {
                                    //Do nothing
                                } else if (fac && !Factions.canBreakBlock(p, p.getWorld().getBlockAt(x, y,
                                        z))) {
                                    //Do nothing
                                } else if (isInsideBorder(p.getWorld().getBlockAt(start, y, z), e, p)) {
                                    //Do nothing
                                } else {
                                    //Get the current location in the gen buckets generation and set that
                                    // block
                                    // to the desired type
                                    blocks.add(p.getWorld().getBlockAt(start, y, z));
                                }
                                start++;
                            }
                        }
                        //Runnable to do the block generation
                        if (lpf.getBuckets().getString(bucketType + ".type").equalsIgnoreCase("sand") ||
                                lpf.getBuckets().getString(bucketType + ".type").equalsIgnoreCase("gravel")) {
                            new BukkitRunnable() {
                                int index = 0;

                                @Override
                                public void run() {
                                    if (index < blocks.size()) {
                                        if (blocks.get(0).getType().equals(Material.AIR)) {
                                            blocks.get(0).setType(Material.valueOf(lpf.getBuckets().getString(bt + ".type").toUpperCase()));
                                            index++;
                                        } else {
                                            this.cancel();
                                        }
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(pl, 0L, 20L);
                        } else if (lpf.getBuckets().getString(bucketType + ".type").equalsIgnoreCase("mixed")) {
                            new BukkitRunnable() {
                                int index = 0;

                                @Override
                                public void run() {
                                    int random = (int)(Math.random() * 2 + 1);
                                    if (index < blocks.size()) {
                                        if (blocks.get(0).getType().equals(Material.AIR)) {
                                            if (random == 1) {
                                                blocks.get(0).setType(Material.SAND);
                                            } else if (random == 2) {
                                                blocks.get(0).setType(Material.GRAVEL);
                                            }
                                            index++;
                                        } else {
                                            this.cancel();
                                        }
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(pl, 0L, 20L);
                        } else {
                            new BukkitRunnable() {
                                int index = 0;

                                @Override
                                public void run() {
                                    if (index < blocks.size()) {
                                        //If the block isn't air then stop generating, do this check twice
                                        if (blocks.get(index).getType().equals(Material.AIR)) {
                                            blocks.get(index).setType(Material.valueOf(lpf.getBuckets().getString(bt + ".type").toUpperCase()));
                                            index++;
                                        } else {
                                            //If the block isn't air cancel the task
                                            this.cancel();
                                        }
                                    } else {
                                        //When the generation is scheduled to finish, cancel the task
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(pl, 0L, lpf.getConfig().getInt("delay"));
                        }
                        //Withdraw the money from the players account
                        if (blocks.size() > 0) {
                            for (String line : lpf.getMessages().getStringList("bucket-use")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace(
                                        "%price%", String.valueOf(price)));
                            }
                            econ.withdrawPlayer(p, price);
                        }
                    } else {
                        for (String line : lpf.getMessages().getStringList("insufficient-funds")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("%bal%"
                                    , String.valueOf(econ.getBalance(p))).replace("%cost%",
                                    String.valueOf(price)));
                        }
                    }
                }
            }
        }
    }

    /**
     * Method to check if a block is inside the world border or not
     *
     * @param b the block being checked
     * @param e the event it is in
     * @return boolean, true if the block is inside the border
     */
    private boolean isInsideBorder(Block b, PlayerInteractEvent e, Player p) {
        //Get the worldborder
        WorldBorder wb = p.getWorld().getWorldBorder();
        //Store the blocks location
        int blockX = b.getX();
        int blockZ = b.getZ();
        //Get the actual worldborder size
        double size = wb.getSize() / 2;
        //Check if the block is inside, return true if it is
        if (blockX > 0 && blockX > size - 1) {
            return true;
        } else if (blockX < 0 && blockX < (size * -1)) {
            return true;
        } else if (blockZ > 0 && blockZ > size - 1) {
            return true;
        } else if (blockZ < 0 && blockZ < (size * -1)) {
            return true;
        }
        return false;
    }
}
