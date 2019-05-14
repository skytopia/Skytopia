package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import solar.rpg.skytopia.Main;

/**
 * Allows staff to see & modify other players' inventories.
 * Can be choppy as inventories do not update immediately.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class InvseeModule extends Module {

    /* Predefined message. */
    private static final String VIEWING = ChatColor.YELLOW + "Viewing the inventory of {0}.";

    public InvseeModule(Main plugin) {
        super(plugin);
    }

    @Command(aliases = {"invsee", "invs"},
            desc = "Look at the inventory of another player",
            usage = "<player>",
            min = 1,
            max = 1,
            flags = "a")
    @CommandPermissions({"commandbook.invsee"})
    public void invsee(CommandContext args, CommandSender sender) {
        if ((sender instanceof Player)) {
            Player found = Bukkit.getPlayer(args.getString(0));
            if (found == null) {
                sender.sendMessage(NOT_ONLINE);
                return;
            }
            Inventory inv;
            if (args.hasFlag('a')) {
                // Armor inventory cannot be modified.
                inv = plugin.getServer().createInventory(found, 9, "Equipped Armor");
                inv.setContents(found.getInventory().getArmorContents());
            } else inv = found.getInventory();
            ((Player) sender).closeInventory();
            ((Player) sender).openInventory(inv);
            sender.sendMessage(VIEWING.replace("{0}", found.getDisplayName()));
        } else sender.sendMessage(NOT_PLAYER);
    }
}