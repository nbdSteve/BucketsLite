package dev.nuer.vbuckets.methods;

import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Class contains method to check if a block is inside the world border
 */
public class BlockWorldBorderCheck {

    /**
     * Method to check if a block is inside the world border or not
     *
     * @param b the block being checked
     * @param e the event it is in
     * @return boolean, true if the block is inside the border
     */
    public static boolean isInsideBorder(Block b, PlayerInteractEvent e, Player p) {
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
