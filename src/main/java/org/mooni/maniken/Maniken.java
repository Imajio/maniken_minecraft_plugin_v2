package org.mooni.maniken;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.RecipeChoice;

public class Maniken extends JavaPlugin implements TabExecutor {

    @Override
    public void onEnable() {
        // Регистрация крафта
        registerRecipes();

        // Регистрация команды
        this.getCommand("changehead").setExecutor(this);
    }

    private void registerRecipes() {
        ItemStack steveHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta meta = steveHead.getItemMeta();
        meta.setDisplayName("Манекен");

        // Добавляем кастомное мета-данное для идентификации головы, созданной через плагин
        meta.getPersistentDataContainer().set(new NamespacedKey(this, "crafted_by_plugin"), PersistentDataType.BYTE, (byte) 1);

        steveHead.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(this, "manikin_head");
        ShapedRecipe recipe = new ShapedRecipe(key, steveHead);

        recipe.shape("KKK", "KPK", "KKK");
        recipe.setIngredient('K', Material.LEATHER);
        recipe.setIngredient('P', new RecipeChoice.MaterialChoice(
                Material.OAK_PLANKS,
                Material.SPRUCE_PLANKS,
                Material.BIRCH_PLANKS,
                Material.JUNGLE_PLANKS,
                Material.ACACIA_PLANKS,
                Material.DARK_OAK_PLANKS,
                Material.CRIMSON_PLANKS,
                Material.WARPED_PLANKS,
                Material.MANGROVE_PLANKS,
                Material.BAMBOO_PLANKS,
                Material.CHERRY_PLANKS
        ));

        Bukkit.addRecipe(recipe);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может выполнить только игрок.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("maniken.changehead")) {
            player.sendMessage("У вас нет прав на использование этой команды.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Используйте: /changehead <ник>");
            return true;
        }

        String targetSkin = args[0];
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() != Material.PLAYER_HEAD) {
            player.sendMessage("Вы должны держать голову игрока в руке.");
            return true;
        }

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta == null || !skullMeta.getPersistentDataContainer().has(new NamespacedKey(this, "crafted_by_plugin"), PersistentDataType.BYTE)) {
            player.sendMessage("Вы можете изменить скин только головы, созданной через этот плагин.");
            return true;
        }

        skullMeta.setOwner(targetSkin);
        item.setItemMeta(skullMeta);

        player.sendMessage("Скин головы изменен на " + targetSkin);
        return true;
    }

}