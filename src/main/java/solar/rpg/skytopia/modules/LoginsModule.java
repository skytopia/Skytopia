package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import solar.rpg.skytopia.Main;
import solar.rpg.skytopia.util.Utility;

import java.util.HashMap;
import java.util.UUID;

/**
 * This module keeps track of how long users have been online for.
 * It also updates a database table with their most recent information.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class LoginsModule extends Module {

    /* Predefined messages. */
    private static final String ONLINE = ChatColor.RED + "{0} " + ChatColor.YELLOW + "has been {1} since " + ChatColor.RED + "{2}.";

    /* Track how long players have been online for. */
    private final HashMap<UUID, Long> loginTimes;

    public LoginsModule(Main plugin) {
        super(plugin);
        loginTimes = new HashMap<>();
    }

    @Command(aliases = {"seen", "lastlogin"},
            desc = "Shows the last login time of a player",
            usage = "<player>", min = 1, max = 1)
    @CommandPermissions({"commandbook.seen"})
    public void seen(CommandContext args, CommandSender sender) {
        OfflinePlayer found = Bukkit.getOfflinePlayer(args.getString(0));
        if (!found.hasPlayedBefore())
            sender.sendMessage(NON_EXISTENT);
        else {
            String name = found.getName();
            if (found.isOnline())
                name = found.getPlayer().getDisplayName();
            sender.sendMessage(ONLINE.replace("{0}", name)
                    .replace("{1}", (found.isOnline() ? ChatColor.GREEN + "online" : ChatColor.RED + "offline") + ChatColor.YELLOW)
                    .replace("{2}", Utility.prettyTime(found.isOnline() ? loginTimes.get(found.getUniqueId()) : found.getLastPlayed())));
        }
    }

    @EventHandler()
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        plugin.main().sql().queue("INSERT INTO `Players` (`player_uuid`, `last_ign`) VALUES (?,?) ON DUPLICATE KEY UPDATE `last_ign`=?", event.getUniqueId().toString(), event.getName(), event.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        // Format join message.
        event.setJoinMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + event.getPlayer().getName());
        loginTimes.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        // Format quit message.
        event.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + event.getPlayer().getName());
    }

    @EventHandler
    public void onServerList(ServerListPingEvent event) {
        // Format server list message.
        event.setMotd(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("list-message")));
    }
}