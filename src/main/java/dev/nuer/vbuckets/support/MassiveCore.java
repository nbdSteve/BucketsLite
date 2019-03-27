package dev.nuer.vbuckets.support;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Support class for the plugin MassiveCore
 */
public class MassiveCore {
    public static boolean canBreakBlock(Player player, Block block) {
        Faction P = MPlayer.get(player).getFaction();
        Faction B = BoardColl.get().getFactionAt(PS.valueOf(block.getLocation()));
        if (ChatColor.stripColor(B.getName()).equalsIgnoreCase("Wilderness") || P == B) {
            return true;
        }
        return false;
    }
}
