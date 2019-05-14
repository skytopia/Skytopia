package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skytopia.Main;

/**
 * Adds a specified player's skull to the sender's inventory.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class SkullModule extends Module {

    /* Predefined message. */
    private static final String SKULL_GIVEN = ChatColor.YELLOW + "Given the skull of {0}.";

    public SkullModule(Main plugin) {
        super(plugin);
    }

    @Command(aliases = {"skull", "giveskull"},
            desc = "Gives you the skull of a player",
            usage = "<player>",
            max = 1)
    @CommandPermissions({"commandbook.skull"})
    public void skull(CommandContext args, CommandSender sender) {
        if ((sender instanceof Player)) {
            OfflinePlayer found = Bukkit.getOfflinePlayer(args.argsLength() == 1 ? args.getString(0) : sender.getName());
            ((Player) sender).getInventory().addItem(ItemUtility.giveSkull(found.getName()));
            sender.sendMessage(SKULL_GIVEN.replace("{0}", found.getName()));
        } else sender.sendMessage(NOT_PLAYER);
    }
}