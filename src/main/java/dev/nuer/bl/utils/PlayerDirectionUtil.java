package dev.nuer.bl.utils;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class PlayerDirectionUtil {

    public static BlockFace checkDirection(Player player) {
        double facing = player.getLocation().getYaw();
        facing = (facing %= 360) >= 0 ? facing : facing + 360;
        if (facing <= 45 || facing > 315) return BlockFace.SOUTH;
        if (facing > 45 && facing <= 135) return BlockFace.WEST;
        if (facing >135 && facing <= 225) return BlockFace.NORTH;
        if (facing > 225 && facing <= 315) return BlockFace.EAST;
        return BlockFace.NORTH;
    }
}