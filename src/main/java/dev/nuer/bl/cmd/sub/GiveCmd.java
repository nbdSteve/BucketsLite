package dev.nuer.bl.cmd.sub;

import dev.nuer.bl.BucketsLite;
import dev.nuer.bl.utils.ItemUtil;
import dev.nuer.bl.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class that handles the give argument of the main command
 */
public class GiveCmd {

    /**
     * Gives the player the specified tool
     *
     * @param sender CommandSender, person sending the command
     * @param args   String[], list of command arguments
     */
    public static void onCmd(CommandSender sender, String[] args) {
        if (sender.hasPermission("buckets-lite.admin")) {
            if (!(args[2].equalsIgnoreCase("vertical") || args[2].equalsIgnoreCase("horizontal"))) {
                if (sender instanceof Player) {
                    MessageUtil.message("messages", "invalid-command", (Player) sender,
                            "{reason}", "The bucket type you entered is not valid");
                } else {
                    BucketsLite.LOGGER.info("Invalid command, check the in game help command for command help.");
                }
                return;
            }
            try {
                Player target = Bukkit.getPlayer(args[1]);
                if (!Bukkit.getOnlinePlayers().contains(target)) {
                    if (sender instanceof Player) {
                        MessageUtil.message("messages", "invalid-command", (Player) sender,
                                "{reason}", "The player you are trying to give that tool to is not online");
                    } else {
                        BucketsLite.LOGGER.info("Invalid command, check the in game help command for command help.");
                    }
                    return;
                }
                ItemUtil.create(args[2], args[3], args[4], target);
                if (sender instanceof Player) {
                    MessageUtil.message("messages", "give", (Player) sender, "{player}", target.getName());
                }
                BucketsLite.LOGGER.info("Success! " + target.getName() + " has received a Gen Bucket. The bucket was given to them by: " + sender.getName());
            } catch (Exception e) {
                e.printStackTrace();
                if (sender instanceof Player) {
                    MessageUtil.message("messages", "invalid-command", (Player) sender,
                            "{reason}", "An error occurred. Please check your command syntax, then your configuration (stack trace console)");
                } else {
                    BucketsLite.LOGGER.info("Invalid command, check the in game help command for command help.");
                }
            }
        } else {
            if (sender instanceof Player) {
                MessageUtil.message("messages", "no-permission", (Player) sender);
            }
        }
    }
}