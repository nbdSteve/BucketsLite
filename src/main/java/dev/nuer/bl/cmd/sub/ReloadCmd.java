package dev.nuer.bl.cmd.sub;

import dev.nuer.bl.BucketsLite;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class that handles the reload argument of the main command
 */
public class ReloadCmd {

    /**
     * Reloads all internal maps and managers configurations for the plugin
     *
     * @param sender CommandSender, person sending the command
     */
    public static void onCmd(CommandSender sender) {
        if (sender instanceof Player) {
            if (sender.hasPermission("buckets-lite.admin")) {
                //Reload and instantiate all configuration sections
                FileManager.reload();
                MessageUtil.message("messages", "reload", (Player) sender);
            } else {
                MessageUtil.message("messages", "no-permission", (Player) sender);
            }
        } else {
            //Reload and instantiate all configuration sections
            FileManager.reload();
            BucketsLite.LOGGER.info("Reloaded all tool maps and configuration files.");
        }
    }
}