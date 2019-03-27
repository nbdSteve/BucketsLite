package dev.nuer.vbuckets.event;

import dev.nuer.vbuckets.file.LoadProvidedFiles;
import dev.nuer.vbuckets.methods.BlockGenerators;
import dev.nuer.vbuckets.methods.BlockWorldBorderCheck;
import dev.nuer.vbuckets.support.Factions;
import dev.nuer.vbuckets.support.WorldGuard;
import dev.nuer.vbuckets.vBuckets;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    //Hashmap for players placing buckets
    private HashMap<UUID, Long> playersUsingBuckets = new HashMap<>();
    //Store the amount that the player is going to pay
    private double totalBucketCost;
    //Store the players active message task
    private BukkitTask playerMessageTask;

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
                        String bucket = "bucket-" + x;
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
                                "vertical") || lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase("scaffold")) {
                            //Start the generation from this height
                            int y = lpf.getBuckets().getInt(bucketType + ".generation-start-height") - 1;
                            //While the block being replaced is air, add these blocks to be changed
                            while (p.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR) && y > 0) {
                                if (wg && !WorldGuard.allowsBreak(p.getWorld().getBlockAt(x, y, z).getLocation())) {
                                    //Do nothing
                                } else if (fac && !Factions.canBreakBlock(p, p.getWorld().getBlockAt(x, y,
                                        z))) {
                                    //Do nothing
                                } else if (BlockWorldBorderCheck.isInsideBorder(p.getWorld().getBlockAt(x, y,
                                        z), e, p)) {
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
                                } else if (BlockWorldBorderCheck.isInsideBorder(p.getWorld().getBlockAt(start,
                                        y, z), e, p)) {
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
                        //Code for generating the actual blocks, this references the methods from the
                        // BlockGenerators class
                        //Figure out what kind of bucket it is
                        if (lpf.getBuckets().getString(bucketType + ".type").equalsIgnoreCase("sand") ||
                                lpf.getBuckets().getString(bucketType + ".type").equalsIgnoreCase("gravel")) {
                            if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                    "scaffold")) {
                                //Generate blocks relevant to that gen style
                                BlockGenerators.scaffoldGen(bucketType, blocks, pl, lpf);
                            } else {
                                BlockGenerators.gravityGen(bucketType, blocks, pl, lpf);
                            }
                        } else if (lpf.getBuckets().getString(bucketType + ".type").equalsIgnoreCase("mixed"
                        )) {
                            if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                    "scaffold")) {
                                BlockGenerators.scaffoldMixedGen(bucketType, blocks, pl, lpf);
                            } else {
                                BlockGenerators.mixedGen(bucketType, blocks, pl, lpf);
                            }
                        } else {
                            BlockGenerators.genericGen(bucketType, blocks, pl, lpf);
                        }
                        //Withdraw the money from the players account
                        if (blocks.size() > 0) {
                            updateCooldown(p, price);
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

    private void updateCooldown(Player player, double genPrice) {
        //Withdraw the money from the players account
        econ.withdrawPlayer(player, genPrice);
        //Increment the total withdrawal amount
        totalBucketCost += genPrice;
        if (playersUsingBuckets.containsKey(player.getUniqueId())) {
            playersUsingBuckets.remove(player.getUniqueId());
            playerMessageTask.cancel();
            playerMessageTask = null;
        } else {
            for (String line : lpf.getMessages().getStringList("start-generation")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        line).replace("%price%", String.valueOf(genPrice)));
            }
        }
        playersUsingBuckets.put(player.getUniqueId(),
                System.currentTimeMillis() + (lpf.getConfig().getInt("message-delay") * 10));
        delayedMessage(player, genPrice);
    }

    private void delayedMessage(Player player, double price) {
        playerMessageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (playersUsingBuckets.containsKey(player.getUniqueId())) {
                    if (playersUsingBuckets.get(player.getUniqueId()) - System.currentTimeMillis() <= 0) {
                        for (String line : lpf.getMessages().getStringList("bucket-use")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)
                                    .replace("%price%", String.valueOf(totalBucketCost))
                                    .replace("%bucketsUsed%",
                                            String.valueOf((int) (totalBucketCost / price))));
                        }
                        resetTotalBucketCost();
                        playersUsingBuckets.remove(player.getUniqueId());
                    }
                }
            }
        }.runTaskLater(pl, lpf.getConfig().getInt("message-delay"));
    }

    private void resetTotalBucketCost() {
        this.totalBucketCost = 0;
    }
}
//    private boolean isInsideBorder(Block b, PlayerInteractEvent e, Player p) {
//        //Get the worldborder
//        WorldBorder wb = p.getWorld().getWorldBorder();
//        //Store the blocks location
//        int blockX = b.getX();
//        int blockZ = b.getZ();
//        //Get the actual worldborder size
//        double size = wb.getSize() / 2;
//        //Check if the block is inside, return true if it is
//        if (blockX > 0 && blockX > size - 1) {
//            return true;
//        } else if (blockX < 0 && blockX < (size * -1)) {
//            return true;
//        } else if (blockZ > 0 && blockZ > size - 1) {
//            return true;
//        } else if (blockZ < 0 && blockZ < (size * -1)) {
//            return true;
//        }
//        return false;
//    }
//}