package dev.nuer.vbuckets.support;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

/**
 * Support class for the plugin WorldGuard
 */
public class WorldGuard {
    public static boolean allowsBreak(int x, int y, int z, Player player) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        com.sk89q.worldedit.util.Location loc = new Location(localPlayer.getWorld(), x, y, z);
        RegionContainer container =
                com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        if (query.testState(loc, localPlayer, Flags.BUILD)) {
            return true;
        }
        return false;
    }
}
