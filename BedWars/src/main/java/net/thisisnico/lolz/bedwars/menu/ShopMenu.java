package net.thisisnico.lolz.bedwars.menu;

import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bukkit.utils.InventoryMenu;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Objects;

public class ShopMenu extends InventoryMenu {
    Player player;

    private boolean hasEnough(Player player, Material material, int amount) {
        return player.getInventory().contains(material, amount);
    }

    private void removeItems(Player player, Material material, int amount) {
        player.getInventory().removeItem(new ItemStack(material, amount));
    }

    private void addCategoriesItems() {
        addItem(ItemUtil.generate(Material.SANDSTONE, 1, "§aБлоки"), _p -> {
            getInventory().clear();
            showBlockMenu();
        });

        addItem(ItemUtil.generate(Material.IRON_CHESTPLATE, 1, "§aБроня"), _p -> {
            getInventory().clear();
            showArmorMenu();
        });

        addItem(ItemUtil.generate(Material.IRON_SWORD, 1, "§aОружие"), _p -> {
            getInventory().clear();
            showWeaponMenu();
        });

        addItem(ItemUtil.generate(Material.IRON_PICKAXE, 1, "§aИнструменты"), _p -> {
            getInventory().clear();
            showToolsMenu();
        });

        addItem(ItemUtil.generate(Material.PORKCHOP, 1, "§cЕда"), _p -> {
            getInventory().clear();
            showFoodMenu();
        });

        addItem(ItemUtil.generate(Material.TNT, 1, "§cПлюшки"), _p -> {
            getInventory().clear();
            showExtrasMenu();
        });
    }

    private static final Material COPPER = Material.BRICK;

    private void showExtrasMenu() {
        addCategoriesItems();

        setItem(9, ItemUtil.generate(Material.COBWEB, 1, "§aПаутина", "Цена: 3 Железа"), _p -> {
            if (hasEnough(player, Material.IRON_INGOT, 3)) {
                removeItems(player, Material.IRON_INGOT, 3);
                player.getInventory().addItem(ItemUtil.generate(Material.COBWEB, 1, true, "§aПаутина"));
            }
        });

        setItem(10, ItemUtil.generate(Material.FLINT_AND_STEEL, 1, "§aЗажигалка", "Цена: 10 Бронзы"), _p -> {
            if (hasEnough(player, COPPER, 10)) {
                removeItems(player, COPPER, 10);
                player.getInventory().addItem(ItemUtil.generate(Material.FLINT_AND_STEEL, 1, true, "§aЗажигалка"));
            }
        });

        setItem(11, ItemUtil.generate(Material.TNT, 1, "§aТНТ", "Цена: 4 Железа"), _p -> {
            if (hasEnough(player, Material.IRON_INGOT, 4)) {
                removeItems(player, Material.IRON_INGOT, 4);
                player.getInventory().addItem(ItemUtil.generate(Material.TNT, 1, true, "§aТНТ"));
            }
        });
    }

    private void showFoodMenu() {
        addCategoriesItems();

        setItem(9, ItemUtil.generate(Material.BREAD, 4, "§aХлеб", "Цена: 10 Бронзы"), _p -> {
            if (hasEnough(player, COPPER, 10)) {
                removeItems(player, COPPER, 10);
                player.getInventory().addItem(ItemUtil.generate(Material.BREAD, 4, "§aХлеб"));
            }
        });

        setItem(10, ItemUtil.generate(Material.COOKED_PORKCHOP, 4, "§aСвинина", "Цена: 10 Бронзы"), _p -> {
            if (hasEnough(player, COPPER, 10)) {
                removeItems(player, COPPER, 10);
                player.getInventory().addItem(ItemUtil.generate(Material.COOKED_PORKCHOP, 4, "§aСвинина"));
            }
        });

        setItem(11, ItemUtil.generate(Material.GOLDEN_APPLE, 1, "§aЗолотое яблоко", "Цена: 10 Железа"), _p -> {
            if (hasEnough(player, Material.IRON_INGOT, 10)) {
                removeItems(player, Material.IRON_INGOT, 10);
                player.getInventory().addItem(ItemUtil.generate(Material.GOLDEN_APPLE, 1, "§aЗолотое яблоко"));
            }
        });
    }

    private void showToolsMenu() {
        addCategoriesItems();

        setItem(9, ItemUtil.generate(Material.WOODEN_PICKAXE, 1, "§aДеревянная кирка", "Цена: 10 Бронзы"), _p -> {
            if (hasEnough(player, COPPER, 10)) {
                removeItems(player, COPPER, 10);
                player.getInventory().addItem(ItemUtil.generate(Material.WOODEN_PICKAXE, 1, true, "§aДеревянная кирка"));
            }
        });

        setItem(10, ItemUtil.generate(Material.STONE_PICKAXE, 1, "§aКаменная кирка", "Цена: 20 Бронзы"), _p -> {
            if (hasEnough(player, COPPER, 20)) {
                removeItems(player, COPPER, 20);
                player.getInventory().addItem(ItemUtil.generate(Material.STONE_PICKAXE, 1, true, "§aКаменная кирка"));
            }
        });

        setItem(11, ItemUtil.generate(Material.IRON_PICKAXE, 1, "§aЖелезная кирка", "Цена: 30 Бронзы"), _p -> {
            if (hasEnough(player, COPPER, 30)) {
                removeItems(player, COPPER, 30);
                player.getInventory().addItem(ItemUtil.generate(Material.IRON_PICKAXE, 1, true, "§aЖелезная кирка"));
            }
        });

        setItem(12, ItemUtil.generate(Material.DIAMOND_PICKAXE, 1, "§aАлмазная кирка", "Цена: 10 Железа"), _p -> {
            if (hasEnough(player, Material.IRON_INGOT, 10)) {
                removeItems(player, Material.IRON_INGOT, 10);
                player.getInventory().addItem(ItemUtil.generate(Material.DIAMOND_PICKAXE, 1, true, "§aАлмазная кирка"));
            }
        });
    }

    private void showWeaponMenu() {
        addCategoriesItems();

        setItem(9, ItemUtil.generate(Material.STICK, 1, "§aПалка ударялка", "Цена: 16 Бронзы"), _p -> {
            if (hasEnough(_p, COPPER, 16)) {
                removeItems(_p, COPPER, 16);
                _p.getInventory().addItem(ItemUtil.generate(Material.STICK, 1, true, "§aПалка ударялка", Enchantment.KNOCKBACK, 1));
            }
        });

        setItem(10, ItemUtil.generate(Material.WOODEN_SWORD, 1, "§aДеревянный меч", "Цена: 7 Бронзы"), _p -> {
            if (hasEnough(_p, COPPER, 7)) {
                removeItems(_p, COPPER, 7);
                _p.getInventory().addItem(ItemUtil.generate(Material.WOODEN_SWORD, 1, "§aДеревянный меч"));
            }
        });

        setItem(11, ItemUtil.generate(Material.STONE_SWORD, 1, "§aКаменный меч", "Цена: 16 Бронзы"), _p -> {
            if (hasEnough(_p, COPPER, 16)) {
                removeItems(_p, COPPER, 16);
                _p.getInventory().addItem(ItemUtil.generate(Material.STONE_SWORD, 1, "§aКаменный меч"));
            }
        });

        setItem(12, ItemUtil.generate(Material.IRON_SWORD, 1, "§aЖелезный меч", "Цена: 32 Бронзы"), _p -> {
            if (hasEnough(_p, COPPER, 32)) {
                removeItems(_p, COPPER, 32);
                _p.getInventory().addItem(ItemUtil.generate(Material.IRON_SWORD, 1, "§aЖелезный меч"));
            }
        });

        setItem(14, ItemUtil.generate(Material.BOW, 1, "§aЛук", "Цена: 7 Железа"), _p -> {
            if (hasEnough(_p, Material.IRON_INGOT, 7)) {
                removeItems(_p, Material.IRON_INGOT, 7);
                _p.getInventory().addItem(ItemUtil.generate(Material.BOW, 1, "§aЛук"));
            }
        });

        setItem(15, ItemUtil.generate(Material.ARROW, 1, "§aСтрела", "Цена: 10 Бронзы"), _p -> {
            if (hasEnough(_p, COPPER, 10)) {
                removeItems(_p, COPPER, 10);
                _p.getInventory().addItem(ItemUtil.generate(Material.ARROW, 3, "§aСтрела"));
            }
        });
    }

    private void showArmorMenu() {
        addCategoriesItems();

        setItem(9, ItemUtil.generate(Material.LEATHER_CHESTPLATE, 1, "§aКожаная броня", "Цена: 16 Бронзы"), _p -> {
            if (_p.getInventory().getChestplate() != null && _p.getInventory().getChestplate().getType().toString().contains("CHESTPLATE")) {
                _p.sendMessage("§cУ вас уже есть броня!");
                return;
            }

            if (hasEnough(_p, COPPER, 16)) {
                removeItems(_p, COPPER, 16);

                var team = Game.getTeam(_p);
                var color = Color.fromRGB(team.getColor().getColor().red(), team.getColor().getColor().green(), team.getColor().getColor().blue());

                var is = new ItemStack(Material.LEATHER_HELMET);
                var meta = (LeatherArmorMeta) is.getItemMeta();
                meta.setColor(color);
                is.setItemMeta(meta);
                player.getInventory().setHelmet(is);

                is = new ItemStack(Material.LEATHER_CHESTPLATE);
                meta = (LeatherArmorMeta) is.getItemMeta();
                meta.setColor(color);
                is.setItemMeta(meta);
                player.getInventory().setChestplate(is);

                is = new ItemStack(Material.LEATHER_LEGGINGS);
                meta = (LeatherArmorMeta) is.getItemMeta();
                meta.setColor(color);
                is.setItemMeta(meta);
                player.getInventory().setLeggings(is);

                is = new ItemStack(Material.LEATHER_BOOTS);
                meta = (LeatherArmorMeta) is.getItemMeta();
                meta.setColor(color);
                is.setItemMeta(meta);
                player.getInventory().setBoots(is);
            }
        });

        setItem(10, ItemUtil.generate(Material.CHAINMAIL_CHESTPLATE, 1, "§aКольчужная броня", "Цена: 32 Бронзы"), _p -> {
            var chestplate = _p.getInventory().getChestplate();
            if (chestplate != null &&
                    (chestplate.getType() == Material.CHAINMAIL_CHESTPLATE || chestplate.getType() == Material.IRON_CHESTPLATE)) {
                _p.sendMessage("§cУ вас уже есть броня!");
                return;
            }

            if (hasEnough(_p, COPPER, 32)) {
                removeItems(_p, COPPER, 32);

                var is = new ItemStack(Material.CHAINMAIL_HELMET);
                player.getInventory().setHelmet(is);

                is = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                player.getInventory().setChestplate(is);

                is = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                player.getInventory().setLeggings(is);

                is = new ItemStack(Material.CHAINMAIL_BOOTS);
                player.getInventory().setBoots(is);
            }
        });

        // Iron armor
        setItem(11, ItemUtil.generate(Material.IRON_CHESTPLATE, 1, "§aЖелезная броня", "Цена: 16 Железа"), _p -> {
            var chestplate = _p.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.IRON_CHESTPLATE) {
                _p.sendMessage("§cУ вас уже есть броня!");
                return;
            }

            if (hasEnough(_p, Material.IRON_INGOT, 16)) {
                removeItems(_p, Material.IRON_INGOT, 16);

                var is = new ItemStack(Material.IRON_HELMET);
                player.getInventory().setHelmet(is);

                is = new ItemStack(Material.IRON_CHESTPLATE);
                player.getInventory().setChestplate(is);

                is = new ItemStack(Material.IRON_LEGGINGS);
                player.getInventory().setLeggings(is);

                is = new ItemStack(Material.IRON_BOOTS);
                player.getInventory().setBoots(is);
            }
        });
    }

    private void showBlockMenu() {
        addCategoriesItems();

        setItem(9, ItemUtil.generate(Material.SANDSTONE, 16, "§aПесчаник", "Цена: 4 Бронзы"), _p -> {
            if (hasEnough(_p, COPPER, 4)) {
                removeItems(_p, COPPER, 4);
                _p.getInventory().addItem(ItemUtil.generate(Material.SANDSTONE, 16, "§aПесчаник"));
            }
        });

        Material concreteMaterial = Material.valueOf(Objects.requireNonNull(Game.getTeam(player)).getColor().name()
                .replaceAll("AQUA", "LIGHT_BLUE").toUpperCase() + "_CONCRETE");
        setItem(10, ItemUtil.generate(Material.GRAY_CONCRETE, 8, "§aБетон", "Цена: 8 Бронзы"), _p -> {
            if (hasEnough(_p, COPPER, 8)) {
                removeItems(_p, COPPER, 8);

                _p.getInventory().addItem(ItemUtil.generate(concreteMaterial, 8, "§aБетон"));
            }
        });

        setItem(11, ItemUtil.generate(Material.OBSIDIAN, 8, "§aОбсидиан", "Цена: 8 Железных слитков"), _p -> {
            if (hasEnough(_p, Material.IRON_INGOT, 8)) {
                removeItems(_p, Material.IRON_INGOT, 8);

                _p.getInventory().addItem(ItemUtil.generate(Material.OBSIDIAN, 8, "§aОбсидиан"));
            }
        });
    }

    public ShopMenu(Player p) {
        super("magazin", 2, true);

        this.player = p;

        showBlockMenu();

        open(player);
    }
}