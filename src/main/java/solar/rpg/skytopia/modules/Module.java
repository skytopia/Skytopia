package solar.rpg.skytopia.modules;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import solar.rpg.skytopia.Main;

/**
 * Represents a Skytopia plugin module.
 * Holds common fields and methods for neater code.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.1
 */
public abstract class Module implements Listener {

    /* Predefined general messages. */
    protected static final String NOT_ONLINE = ChatColor.RED + "That player is not online.";
    protected static final String NOT_PLAYER = ChatColor.RED + "Only players can do this!";
    protected static final String NON_EXISTENT = ChatColor.RED + "That player doesn't exist.";

    /* Reference to JavaPlugin. */
    protected final Main plugin;

    public Module(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * @param sender A command sender.
     * @return Most appropriate name identifier.
     */
    public String getSenderName(CommandSender sender) {
        String consoleName = "an administrator";
        String anonymousName = "someone";
        if ((sender instanceof ConsoleCommandSender))
            return consoleName;
        return sender instanceof Player ? ((Player) sender).getDisplayName() : anonymousName;
    }
}
