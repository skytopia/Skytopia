package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import solar.rpg.skytopia.Main;

/**
 * This module handles spawnpoint-related functions.
 * This includes teleporting players to the spawnpoint when necessary.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class SpawnModule extends Module {

    /* Predefined messages. */
    private static final String SPAWN_TELEPORT = ChatColor.YELLOW + "Teleported to spawn.";
    private static final String SPAWN_TELEPORT_BY = ChatColor.YELLOW + "Teleported to spawn by {0}.";
    private static final String SPAWN_LOCATION_SET = ChatColor.YELLOW + "Spawn location set.";
    private static final String TELEPORT_CONFIRMATION = ChatColor.YELLOW + "Teleported.";

    /* Instance of spawn world. */
    private final World spawnWorld;

    public SpawnModule(Main plugin) {
        super(plugin);
        spawnWorld = plugin.getServer().getWorld("world");
    }

    @Command(aliases = {"spawn", "spwn", "sp"},
            desc = "Teleports you back to the spawn",
            usage = "<player>",
            max = 1,
            flags = "s")
    @CommandPermissions({"commandbook.spawn"})
    public void spawn(CommandContext args, CommandSender sender)
            throws CommandPermissionsException {
        if (args.argsLength() == 0) {
            if ((sender instanceof Entity)) {
                ((Entity) sender).teleport(getProperSpawn());
                sender.sendMessage(SPAWN_TELEPORT);
            } else sender.sendMessage(NOT_PLAYER);
        } else {
            if (!plugin.hasPermission(sender, "commandbook.spawn.others"))
                throw new CommandPermissionsException();
            Player pl = Bukkit.getPlayer(args.getString(0));
            if (pl != null) {
                if (pl.equals(sender)) {
                    pl.sendMessage(SPAWN_TELEPORT);
                    pl.teleport(getProperSpawn());
                } else {
                    sender.sendMessage(TELEPORT_CONFIRMATION);
                    if (args.hasFlag('s')) pl.sendMessage(SPAWN_TELEPORT);
                    else pl.sendMessage(SPAWN_TELEPORT_BY.replace("{0}", getSenderName(sender)));
                    pl.teleport(getProperSpawn());
                }
            } else sender.sendMessage(NOT_ONLINE);
        }
    }

    @Command(aliases = {"setspawnloc", "sspwn", "ssp"},
            desc = "Sets the spawnpoint")
    @CommandPermissions({"commandbook.setspawn"})
    public void setSpawn(CommandContext args, CommandSender sender) {
        if ((sender instanceof Entity)) {
            Location loc = ((Entity) sender).getLocation();
            spawnWorld.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            sender.sendMessage(SPAWN_LOCATION_SET);
        } else sender.sendMessage(NOT_PLAYER);
    }

    /**
     * @return Proper spawn location.
     */
    private Location getProperSpawn() {
        Location spawn = spawnWorld.getSpawnLocation().add(0.5D, 0.5D, 0.5D);
        spawn.setYaw(180.0F);
        return spawn;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Gives players starter kit and teleports them to spawn location on first join.
        if (!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().teleport(getProperSpawn());
            event.getPlayer().getInventory().clear();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kit starter " + event.getPlayer().getName());
        }

        // Players logging in while in a portal block are teleported to spawn.
        if (event.getPlayer().getLocation().getBlock().getType() == Material.NETHER_PORTAL)
            event.getPlayer().teleport(getProperSpawn());
    }

    @EventHandler()
    public void onRespawn(PlayerRespawnEvent event) {
        // Players respawn at the spawn location unless otherwise set.
        event.setRespawnLocation(getProperSpawn());
    }
}