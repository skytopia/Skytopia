package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import solar.rpg.skytopia.Main;
import solar.rpg.skytopia.util.Utility;

import java.util.concurrent.TimeUnit;

/**
 * Displays a bunch of useful information:
 * <ul>
 * <li>JVM RAM allocation.</li>
 * <li>Server uptime.</li>
 * <li>World information: chunks, entities, players.</li>
 * </ul>
 */
public class GCModule extends Module {

    /* Keeps a record of the time that the server went up. */
    private final long UPTIME;

    public GCModule(Main plugin) {
        super(plugin);
        UPTIME = System.currentTimeMillis();
    }

    @Command(aliases = {"gc", "tps", "lag"}, desc = "Shows debug information")
    @CommandPermissions({"commandbook.lag"})
    public void gc(CommandContext args, CommandSender sender) throws CommandPermissionsException {
        sender.sendMessage(ChatColor.YELLOW + "Server uptime is " + ChatColor.RED + Utility.convertTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - UPTIME)) + ".");
        sender.sendMessage(ChatColor.YELLOW + "RAM: " + ChatColor.RED + Runtime.getRuntime().totalMemory() / 1024L / 1024L + "MB/AL " + ChatColor.RED + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L + "MB/US " + ChatColor.RED + Runtime.getRuntime().freeMemory() / 1024L / 1024L + "MB/FR");
        for (World world : plugin.getServer().getWorlds()) {
            sender.sendMessage("  " + ChatColor.YELLOW + world.getName() + ": " + ChatColor.RED + world.getPlayers().size() + " players, " + world.getEntities().size() + " entities, " + world.getLoadedChunks().length + " chunks");
        }
    }
}