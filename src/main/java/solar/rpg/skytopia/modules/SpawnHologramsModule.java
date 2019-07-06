package solar.rpg.skytopia.modules;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.HologramPlugin;
import com.sainttx.holograms.api.line.TextLine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import solar.rpg.skytopia.Main;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * An extension of SpawnSkullsModule that updates spawn holograms.
 * Only applicable if the Holograms plugin exists and is enabled.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.1
 */
class SpawnHologramsModule extends Module {

    /* Reference to Hologram Manager. */
    private final HologramManager hologramManager;

    SpawnHologramsModule(Main plugin) {
        super(plugin);
        hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
    }

    void refreshHoloTopVoters() {
        Hologram top = hologramManager.getHologram("topvoters");
        top.removeLine(top.getLine(3));
        top.addLine(new TextLine(top, ChatColor.GOLD + "#1: " + ChatColor.RED + "No One" + ChatColor.GRAY + " (no votes)"), 3);
        top.removeLine(top.getLine(4));
        top.addLine(new TextLine(top, ChatColor.GRAY + "#2: " + ChatColor.RED + "No One" + ChatColor.GRAY + " (no votes)"), 4);
        top.removeLine(top.getLine(5));
        top.addLine(new TextLine(top, ChatColor.DARK_RED + "#3: " + ChatColor.RED + "No One" + ChatColor.GRAY + " (no votes)"), 5);
    }

    /**
     * Update the hologram to reflect number of votes.
     */
    void refreshVoterHolo(Iterator<Entry<UUID, Integer>> clone) {
        Entry next;
        OfflinePlayer target;
        Hologram top = hologramManager.getHologram("topvoters");

        // Number one voter.
        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer((UUID) next.getKey());
            top.removeLine(top.getLine(3));
            top.addLine(new TextLine(top, ChatColor.GOLD + "#1: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + " votes)"), 3);
        }
        // Number two voter.
        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer((UUID) next.getKey());
            top.removeLine(top.getLine(4));
            top.addLine(new TextLine(top, ChatColor.GRAY + "#2: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + " votes)"), 4);
        }
        // Number three voter.
        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer((UUID) next.getKey());
            top.removeLine(top.getLine(5));
            top.addLine(new TextLine(top, ChatColor.DARK_RED + "#3: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + " votes)"), 5);
        }
    }

    /**
     * Update the hologram to reflect the value of their islands.
     */
    void refreshTopIslandHolo(Iterator<Entry<Integer, Integer>> clone) {
        Entry<Integer, Integer> next;
        OfflinePlayer target;

        Hologram top = hologramManager.getHologram("topislands");

        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            top.removeLine(top.getLine(3));
            top.addLine(new TextLine(top, ChatColor.GOLD + "#1: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + ")"), 3);
        }
        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            top.removeLine(top.getLine(4));
            top.addLine(new TextLine(top, ChatColor.GRAY + "#2: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + ")"), 4);
        }
        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            top.removeLine(top.getLine(5));
            top.addLine(new TextLine(top, ChatColor.DARK_RED + "#3: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + ")"), 5);
        }
        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            top.removeLine(top.getLine(6));
            top.addLine(new TextLine(top, ChatColor.DARK_GRAY + "#4: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + ")"), 6);
        }
        if (clone.hasNext()) {
            next = clone.next();
            target = Bukkit.getOfflinePlayer(plugin.main().islands().getIsland(next.getKey()).members().getOwner());
            top.removeLine(top.getLine(7));
            top.addLine(new TextLine(top, ChatColor.DARK_GRAY + "#5: " + ChatColor.RED + target.getName() + ChatColor.GRAY + " (" + next.getValue() + ")"), 7);
        }
    }
}