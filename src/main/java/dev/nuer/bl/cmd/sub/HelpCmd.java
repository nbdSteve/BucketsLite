package dev.nuer.bl.cmd.sub;

import dev.nuer.bl.BucketsLite;
import dev.nuer.bl.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class that handles the help argument of the main command
 */
public class HelpCmd {

    /**
     * Sends the help message from the configuration
     *
     * @param sender CommandSender, person sending the command
     */
    public static void onCmd(CommandSender sender) {
        if (sender instanceof Player) {
            if (sender.hasPermission("buckets-lite.help")) {
                MessageUtil.message("messages", "help", (Player) sender);
            } else {
                MessageUtil.message("messages", "no-permission", (Player) sender);
            }
        } else {
            BucketsLite.LOGGER.info("The help message can only be viewed by players.");
        }
    }
}