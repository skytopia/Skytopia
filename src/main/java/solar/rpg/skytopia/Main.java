package solar.rpg.skytopia;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import solar.rpg.skyblock.Cloud;
import solar.rpg.skyblock.stored.Database;
import solar.rpg.skytopia.modules.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main acts as the plugin class and entry point for the program.
 * Registers modules and does database stuff.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class Main extends JavaPlugin {
    /* sk89q-command-framework stuff. */
    private CommandsManager<CommandSender> commands;
    private ArrayList<Class> cmdClasses;
    /* Reference to Skyblock plugin. */
    private solar.rpg.skyblock.Cloud cloud;
    /* Keep a static instance of the logger after enabling so all classes can log. */
    private static Logger logger;

    /**
     * Global logging method. Prints out to console with Shoptopia prefix.
     *
     * @param level Logging level.
     * @param msg   Message to log.
     */
    public static void log(Level level, String msg) {
        logger.log(level, String.format("[Skytopia] %s", msg));
    }

    public void onEnable() {
        logger = getLogger();
        log(Level.FINE, String.format("Enabling Skytopia v%s!", getDescription().getVersion()));
        saveDefaultConfig();
        reloadConfig();

        cloud = solar.rpg.skyblock.Main.instance.main();

        try {
            Database db = cloud.sql().db;
            db.regenerateTable("Players",
                    "CREATE TABLE `Players` (" +
                            "player_uuid CHAR(36) NOT NULL," +
                            "last_ign VARCHAR(16) NOT NULL," +
                            "last_login TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                            "PRIMARY KEY (player_uuid))");

            db.regenerateTable("PlayerVotes",
                    "CREATE TABLE `PlayerVotes` (" +
                            "player_uuid CHAR(36) NOT NULL," +
                            "votes INT NOT NULL DEFAULT 1," +
                            "PRIMARY KEY (player_uuid)," +
                            "FOREIGN KEY (player_uuid) REFERENCES Players(player_uuid))");
        } catch (SQLException ex) {
            log(Level.SEVERE, "Unable to regenerate database tables!");
            ex.printStackTrace();
        }

        registerCommandClass(SpawnModule.class);
        registerCommandClass(SkullModule.class);
        registerCommandClass(CondenseModule.class);
        registerCommandClass(CraftModule.class);
        registerCommandClass(SpawnSkullsModule.class);
        registerCommandClass(GCModule.class);
        registerCommandClass(LoginsModule.class);
        registerCommandClass(NearbyModule.class);
        registerCommandClass(InvseeModule.class);
        registerCommands();
    }

    public void onDisable() {
    }

    /**
     * @return Instance of Skyblock central controller.
     */
    public Cloud main() {
        return cloud;
    }

    /**
     * Registers the commands after all module classes have been registered.
     */
    private void registerCommands() {
        if (cmdClasses == null || cmdClasses.size() < 1) {
            solar.rpg.skyblock.Main.log("Could not register commands! Perhaps you registered no classes?");
            return;
        }

        // Register the modules that we want to use.
        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender player, String perm) {
                return Main.this.hasPermission(player, perm); // Check player permissions using our own method.
            }
        };
        commands.setInjector(new SimpleInjector(this));
        final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);

        for (Class cmdClass : cmdClasses)
            cmdRegister.register(cmdClass);
    }

    /**
     * Registers a class containing sk89q Commands.
     *
     * @param cmdClass The command class.
     * @see com.sk89q.minecraft.util.commands.Command
     */
    private void registerCommandClass(Class cmdClass) {
        if (cmdClasses == null)
            cmdClasses = new ArrayList<>();
        cmdClasses.add(cmdClass);
    }

    /**
     * Checks whether the player has permission depending on their rank.
     * This should be used if a certain permission involves the Console.
     *
     * @param sender The person who sent the command.
     * @param perm   The permission string, if any.
     * @return Whether the player has permission.
     */
    public boolean hasPermission(CommandSender sender, String perm) {
        return ((sender instanceof ConsoleCommandSender)) || (sender.hasPermission(perm));
    }

    /**
     * The onCommand method found in Bukkit's CommandExecutor class.
     * Re-written to handle modules much more smoothly.
     *
     * @param sender The sender.
     * @param cmd    Bukkit's "Command" instance.
     * @param label  The command label.
     * @param args   Any arguments included in the command.
     * @return Always true because we don't want it to return false.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if ((e.getCause() instanceof NumberFormatException)) {
                sender.sendMessage(ChatColor.RED + "You need to enter a number!");
            } else {
                sender.sendMessage(ChatColor.RED + "Error occurred, contact developer.");
                sender.sendMessage(ChatColor.RED + "Message: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }
}