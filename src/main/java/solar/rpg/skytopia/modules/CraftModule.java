package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import solar.rpg.skytopia.Main;

/**
 * Opens a bunch of useful inventories for the sender.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class CraftModule extends Module {

    /* Predefined messages. */
    private static final String VIEWING = ChatColor.YELLOW + "Workbench opened.";
    private static final String VIEWING_2 = ChatColor.YELLOW + "Ender Chest opened.";

    public CraftModule(Main plugin) {
        super(plugin);
    }

    @Command(aliases = {"workbench", "craft"}, desc = "Opens a workbench for you")
    @CommandPermissions({"commandbook.workbench"})
    public void workbench(CommandContext args, CommandSender sender) {
        if ((sender instanceof Player)) {
            ((Player) sender).closeInventory();
            ((Player) sender).openWorkbench(null, true);
            sender.sendMessage(VIEWING);
        }
    }

    @Command(aliases = {"enderchest", "echest", "ec"}, desc = "Opens your ender chest for you")
    @CommandPermissions({"commandbook.enderchest"})
    public void echest(CommandContext args, CommandSender sender) {
        if ((sender instanceof Player)) {
            ((Player) sender).closeInventory();
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
            ((Player) sender).openInventory(((Player) sender).getEnderChest());
            sender.sendMessage(VIEWING_2);
        }
    }
}