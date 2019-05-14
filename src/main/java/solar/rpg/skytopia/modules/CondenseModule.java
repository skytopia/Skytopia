package solar.rpg.skytopia.modules;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import solar.rpg.skytopia.Main;

import java.util.*;

public class CondenseModule extends Module {

    /* Blocks that can be condensed. */
    private static final Material[] CAN_CONDENSE = {Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.COAL_BLOCK, Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.QUARTZ_BLOCK, Material.GLOWSTONE, Material.BONE_BLOCK};

    /* Predefined messages. */
    private static final String SUCCESS = ChatColor.YELLOW + "Blocks condensed.";
    private static final String NONE = ChatColor.YELLOW + "No blocks were condensed.";

    /* Map of items that can be condensed into blocks. */
    private final Map<ItemStack, SimpleRecipe> condenseList;

    public CondenseModule(Main plugin) {
        super(plugin);
        condenseList = new HashMap<>();
    }

    @Command(aliases = {"condense", "blocks"},
            desc = "Condenses materials into their block form.")
    @CommandPermissions({"commandbook.condense"})
    public void condense(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            Player pl = (Player) sender;

            // Get all valid items from the player's inventory.
            List<ItemStack> is = new ArrayList<>();
            for (ItemStack stack : pl.getInventory().getContents())
                if ((stack != null) && (stack.getType() != Material.AIR))
                    is.add(stack);

            // Attempt to condense the found items.
            boolean didConvert = false;
            for (ItemStack itemStack : is)
                if (condenseStack(pl, itemStack))
                    didConvert = true;
            pl.updateInventory();
            if (didConvert) pl.sendMessage(SUCCESS);
            else pl.sendMessage(NONE);
        } else sender.sendMessage(NOT_PLAYER);
    }

    /**
     * Attempts to condense this item stack.
     *
     * @param user  The user who is condensing their items.
     * @param stack The current specified item stack.
     * @return True, if the item was able to be condensed.
     */
    private boolean condenseStack(Player user, ItemStack stack) {
        SimpleRecipe recipe = getRecipe(stack);
        if (recipe != null) {
            ItemStack input = recipe.getInput();
            ItemStack result = recipe.getResult();

            // Get how much of the material is present.
            int amount = 0;
            for (ItemStack contents : user.getInventory().getContents())
                if ((contents != null) && (contents.isSimilar(stack)))
                    amount += contents.getAmount();

            // Calculate the output amount.
            int output = amount / input.getAmount() * result.getAmount();
            amount -= amount % input.getAmount();
            if (amount > 0) {
                input.setAmount(amount);
                result.setAmount(output);

                // Take away input, give output.
                user.getInventory().removeItem(input);
                user.getInventory().addItem(result);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this item stack can be condensed.
     *
     * @param stack The item to check.
     * @return Recipe to condense, if applicable.
     */
    private SimpleRecipe getRecipe(ItemStack stack) {
        if (condenseList.containsKey(stack))
            return condenseList.get(stack);

        System.out.println(stack);

        Iterator<Recipe> iter = plugin.getServer().recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            if (Arrays.stream(CAN_CONDENSE).noneMatch(condensible -> condensible.equals(recipe.getResult().getType()))) continue;

            Collection<ItemStack> recipeItems = getIngredients(recipe, stack);
            if (recipeItems == null || (recipeItems.size() != 4 && recipeItems.size() != 9)) continue;

            // Check that this recipe fits the condensing criteria.
            if (recipeItems.size() >= recipe.getResult().getAmount()) {
                System.out.println(recipeItems);
                System.out.println(recipe.getResult().getType());
                ItemStack input = stack.clone();
                input.setAmount(recipeItems.size());

                // Save this recipe for future reference.
                SimpleRecipe newRecipe = new SimpleRecipe(recipe.getResult(), input);
                condenseList.put(stack, newRecipe);
                return newRecipe;
            }
        }

        // This item can't be condensed. Map to null for future reference.
        condenseList.put(stack, null);
        return null;
    }

    /**
     * Checks if an itemstack matches a recipe's ingredients.
     *
     * @param recipe The recipe.
     * @param stack  The itemstack.
     * @return List of recipe ingredients if the item matches it.
     */
    private Collection<ItemStack> getIngredients(Recipe recipe, ItemStack stack) {
        // Get recipe ingredient list.
        Collection<ItemStack> inputList;
        if ((recipe instanceof ShapelessRecipe)) {
            ShapelessRecipe slRecipe = (ShapelessRecipe) recipe;
            inputList = slRecipe.getIngredientList();
        } else if ((recipe instanceof ShapedRecipe)) {
            ShapedRecipe sRecipe = (ShapedRecipe) recipe;
            inputList = sRecipe.getIngredientMap().values();
        } else return null;

        // Check if the itemstack still matches the recipe.
        boolean match = true;
        Iterator<ItemStack> iter = inputList.iterator();
        while (iter.hasNext()) {
            ItemStack inputSlot = iter.next();
            if (inputSlot == null)
                iter.remove();
            else {
                if (inputSlot.getDurability() == Short.MAX_VALUE)
                    inputSlot.setDurability((short) 0);
                if (!inputSlot.isSimilar(stack))
                    match = false;
            }
        }
        if (match) return inputList;
        else return null;
    }

    /**
     * Recipe that results in a condensed item.
     * Useful for caching and condensing in the future.
     */
    private class SimpleRecipe implements Recipe {
        private ItemStack result;
        private ItemStack input;

        private SimpleRecipe(ItemStack result, ItemStack input) {
            this.result = result;
            this.input = input;
        }

        public ItemStack getResult() {
            return result.clone();
        }

        ItemStack getInput() {
            return input.clone();
        }
    }
}