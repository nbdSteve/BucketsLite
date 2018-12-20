package com.nbdsteve.vbuckets.support;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Support class for the plugin Factions
 */
public class Factions {
    public static boolean canBreakBlock(Player player, Block block) {
        if (FPlayers.getInstance().getByPlayer(player).getFaction() != null) {
            Faction P = FPlayers.getInstance().getByPlayer(player).getFaction();
            FLocation loc = new FLocation(block.getLocation());
            Faction B = Board.getInstance().getFactionAt(loc);
            if (ChatColor.stripColor(B.getTag()).equalsIgnoreCase("Wilderness") || P == B) {
                return true;
            }
        }
        return false;
    }
}
