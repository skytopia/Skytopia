package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import solar.rpg.skyblock.util.StringUtility;
import solar.rpg.skytopia.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the sender a list of players within 50 blocks of them.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class NearbyModule extends Module {

    /* Predefined message. */
    private static final String NEARBY = ChatColor.YELLOW + "Nearby players ({0}):";

    public NearbyModule(Main plugin) {
        super(plugin);
    }

    @Command(aliases = {"nearby", "nrby", "nrb"}, desc = "Shows nearby players")
    @CommandPermissions({"commandbook.nearby"})
    public void nearby(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            List<String> nearby = new ArrayList<>();

            // Find nearby players.
            for (Entity near : ((Player) sender).getNearbyEntities(50.0D, 50.0D, 50.0D))
                if ((near instanceof Player))
                    nearby.add(((Player) near).getDisplayName() + ChatColor.RED + " (" + (int) Math.floor(near.getLocation().distance(((Player) sender).getLocation())) + "m)");

            sender.sendMessage(NEARBY.replace("{0}", String.valueOf(nearby.size())));
            sender.sendMessage(StringUtility.sentenceFormat(nearby, ""));
        } else sender.sendMessage(NOT_PLAYER);
    }
}