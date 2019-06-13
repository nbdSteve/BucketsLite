package dev.nuer.bl.listeners;

import dev.nuer.bl.bucket.GenBucket;
import dev.nuer.bl.nbtapi.NBTItem;
import dev.nuer.bl.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerClickListener implements Listener {

    @EventHandler
    public void playerPlaceBucket(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        NBTItem item = new NBTItem(event.getPlayer().getItemInHand());
        try {
            if (!item.getBoolean("buckets-lite.gen-bucket")) return;
        } catch (NullPointerException e) {
            //The item is not a gen bucket so stop this function
            return;
        }
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(event.getClickedBlock(), event.getPlayer());
        Bukkit.getPluginManager().callEvent(blockBreakEvent);
        if (blockBreakEvent.isCancelled()) return;
        blockBreakEvent.setCancelled(true);
        GenBucket bucket = new GenBucket(event.getPlayer(), item, event.getClickedBlock());
        //Send all of the admins a message
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("buckets-lite.notify")) {
                MessageUtil.message("messages", "admin-place-notification", player,
                        "{player}", bucket.getOwner().getName(),
                        "{x}", String.valueOf(bucket.getStartingBlock().getX()),
                        "{y}", String.valueOf(bucket.getStartingBlock().getY()),
                        "{z}", String.valueOf(bucket.getStartingBlock().getZ()));
            }
        }
    }
}