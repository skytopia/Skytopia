package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.Utility;
import solar.rpg.skytopia.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Keeps track of the server's top voters and islands.
 * Modifies holograms and skulls in spawn to show the top 3 of each.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class SpawnSkullsModule extends Module {

    /* Reference to Holograms module. */
    private final SpawnHologramsModule holograms;

    /* Top island value and top voter player skulls. */
    private Skull TOP_1, TOP_2, TOP_3, VOTE_1, VOTE_2, VOTE_3;

    /* Maps players to their amount of votes. */
    private final LinkedHashMap<UUID, Integer> voted;

    public SpawnSkullsModule(Main plugin) {
        super(plugin);
        holograms = plugin.getServer().getPluginManager().getPlugin("Holograms") == null ? null : new SpawnHologramsModule(plugin);
        voted = new LinkedHashMap<>();

        World spawnWorld = plugin.getServer().getWorld("world");
        VOTE_1 = (Skull) spawnWorld.getBlockAt(-2, 9, -34).getState();
        VOTE_2 = (Skull) spawnWorld.getBlockAt(-3, 9, -33).getState();
        VOTE_3 = (Skull) spawnWorld.getBlockAt(-1, 9, -35).getState();
        TOP_1 = (Skull) spawnWorld.getBlockAt(8, 9, -34).getState();
        TOP_2 = (Skull) spawnWorld.getBlockAt(7, 9, -35).getState();
        TOP_3 = (Skull) spawnWorld.getBlockAt(9, 9, -33).getState();

        try {
            Main.log(Level.INFO, "Fetching existing player votes");
            // Fetch votes upon startup.
            PreparedStatement pstmt = plugin.main().sql().prepare("SELECT * FROM `PlayerVotes`");
            ResultSet votes = pstmt.executeQuery();
            while (votes.next())
                voted.put(UUID.fromString(votes.getString("player_uuid")), votes.getInt("votes"));
            votes.close();
            pstmt.close();
            Main.log(Level.INFO, String.format("Fecthed %s voting players", voted.size()));
        } catch (SQLException e) {
            Main.log(Level.SEVERE, "Unable to fetch player votes from database");
            e.printStackTrace();
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::refreshTopIslandHeads, 20 * 300L, 20 * 300L);
    }

    @Command(aliases = {"resetvotes", "rvotes"},
            desc = "Resets top voters for this month")
    @CommandPermissions({"skytopia.staff"})
    public void resetVotes(CommandContext args, CommandSender sender) {
        voted.clear();
        VOTE_1.setOwner("Steve");
        VOTE_2.setOwner("Steve");
        VOTE_3.setOwner("Steve");
        VOTE_1.update();
        VOTE_2.update();
        VOTE_3.update();
        plugin.main().sql().queue("DELETE FROM `PlayerVotes` WHERE 1");

        if (holograms != null)
            holograms.refreshHoloTopVoters();
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(event.getVote().getUsername());
        if (target.hasPlayedBefore()) {
            voted.put(target.getUniqueId(), voted.getOrDefault(target.getUniqueId(), 0) + 1);
            plugin.main().sql().queue("INSERT INTO `PlayerVotes` (`player_uuid`) VALUES (?) ON DUPLICATE KEY UPDATE `votes`=`votes`+1", target.getUniqueId());
            refreshVoterHeads();
        }
    }

    /**
     * Update top voter heads to show current top 3 voters.
     */
    private void refreshVoterHeads() {
        Entry next;
        OfflinePlayer target;

        List<Entry<UUID, Integer>> sorted = Utility.entriesSortedByValues(voted);
        Iterator<Entry<UUID, Integer>> iterator = sorted.iterator();

        // Number one voter.
        if (iterator.hasNext()) {
            next = iterator.next();
            target = Bukkit.getOfflinePlayer((UUID) next.getKey());
            VOTE_1.setOwningPlayer(target);
            VOTE_1.update(true);
        }
        // Number two voter.
        if (iterator.hasNext()) {
            next = iterator.next();
            target = Bukkit.getOfflinePlayer((UUID) next.getKey());
            VOTE_2.setOwningPlayer(target);
            VOTE_2.update(true);
        }
        // Number three voter.
        if (iterator.hasNext()) {
            next = iterator.next();
            target = Bukkit.getOfflinePlayer((UUID) next.getKey());
            VOTE_3.setOwningPlayer(target);
            VOTE_3.update(true);
        }

        // Update holograms if applicable.
        if (holograms != null)
            holograms.refreshVoterHolo(sorted.iterator());
    }

    /**
     * Update top island value heads to show owners of the 3 most valuable islands.
     */
    private void refreshTopIslandHeads() {
        // Grab all islands and sort by island value.
        LinkedHashMap<Integer, Integer> values = new LinkedHashMap<>();
        for (Island island : plugin.main().islands().getIslands())
            values.put(island.getID(), island.getValue());

        Entry<Integer, Integer> next;
        OfflinePlayer target;

        List<Entry<Integer, Integer>> sorted = Utility.entriesSortedByValues(values);
        Iterator<Entry<Integer, Integer>> iterator = sorted.iterator();

        if (iterator.hasNext()) {
            next = iterator.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            TOP_1.setOwningPlayer(target);
            TOP_1.update(true);
        }
        if (iterator.hasNext()) {
            next = iterator.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            TOP_2.setOwningPlayer(target);
            TOP_2.update(true);
        }
        if (iterator.hasNext()) {
            next = iterator.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            TOP_3.setOwningPlayer(target);
            TOP_3.update(true);
        }

        // Update holograms if applicable.
        if (holograms != null)
            holograms.refreshTopIslandHolo(sorted.iterator());
    }
}