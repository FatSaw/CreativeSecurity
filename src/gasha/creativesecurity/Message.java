package gasha.creativesecurity;

import gasha.creativesecurity.Config;
import gasha.creativesecurity.CreativeSecurityPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

public enum Message {
    BLOCK_INFO("block-info"),
    PLACE_MOBSPAWNER("mob_spawner-place"),
    PLACE_SILVERFISH_BLOCK("silverfish-block-place"),
    DROP("drop"),
    PICKUP("pickup"),
    PICKUP_SURVIVAL("pickup-survival"),
    LEASH("leash"),
    MILK("milk"),
    COLLECT_MUSHROOM_SOAP("collect-mushroom-soap"),
    FORM_MOB("mob-create"),
    BONE_MEAL("bone_meal"),
    PLANT("plant"),
    SHEAR_SHEEP("shear-sheep"),
    SHEAR_SNOWMAN("shear-snowman"),
    NAME_TAG("nametag"),
    NAME_TAG_CREATIVE_ENTITY("nametag-creative-entity"),
    SPAWN_IMMOVABLE_ITEM("spawn-creative-blocked-item"),
    CONTAINER_DOUBLE_CHEST("container-chestdouble"),
    CONTAINER_CHEST("container-chest"),
    CONTAINER_DISPENSER("container-dispenser"),
    CONTAINER_DROPPER("container-dropper"),
    CONTAINER_FURNACE("container-furnace"),
    CONTAINER_WORKBENCH("container-crafting"),
    CONTAINER_ENCHANTING_TABLE("container-enchant"),
    CONTAINER_BREWING_STAND("container-brewing"),
    CONTAINER_VILLAGER("container-villager"),
    CONTAINER_WANDERING_TRADER("container-wandering-trader"),
    CONTAINER_ENDER_CHEST("container-enderchest"),
    CONTAINER_ANVIL("container-repair"),
    CONTAINER_BEACON("beacon"),
    CONTAINER_HOPPER("container-hopper"),
    CONTAINER_SHULKER_BOX("container-shulkerbox"),
    CONTAINER_BARREL("container-barrel"),
    CONTAINER_BLAST_FURNACE("container-blast-furnace"),
    CONTAINER_SMOKER("container-smoker"),
    CONTAINER_CARTOGRAPHY("container-cartography"),
    CONTAINER_GRINDSTONE("container-grindstone"),
    CONTAINER_LECTERN("container-lectern"),
    CONTAINER_LOOM("container-loom"),
    CONTAINER_STONECUTER("container-stonecuter"),
    CONTAINER_MULE("container-mule"),
    CONTAINER_DONKEY("container-donkey"),
    CONTAINER_HORSE("container-horse"),
    CONTAINER_STORAGE_MINECART("container-storageminecart"),
    CONTAINER_HOPPER_MINECART("container-hopperminecart"),
    CONTAINER_JUKEBOX("jukebox"),
    CONTAINER_ARMOR_STAND("armor_stand"),
    CONTAINER_ITEM_FRAME_SURVIVAL("survival-item_frame"),
    CONTAINER_ITEM_FRAME_CREATIVE("creative-item_frame"),
    DAMAGE_ELDER_GUARDIAN("damage-elder-guardian"),
    DAMAGE_GUARDIAN("damage-guardian"),
    DAMAGE_WITHER_SKELETON("damage-wither-skeleton"),
    DAMAGE_STRAY("damage-stray"),
    DAMAGE_SKELETON("damage-skeleton"),
    DAMAGE_HUSK("damage-husk"),
    DAMAGE_ZOMBIE("damage-zombie"),
    DAMAGE_ZOMBIE_VILLAGER("damage-zombie-villager"),
    DAMAGE_HOGLIN("damage-hoglin"),
    DAMAGE_PIGLIN("damage-piglin"),
    DAMAGE_PIGLIN_BRUTE("damage-piglin-brute"),
    DAMAGE_STRIDER("damage-strider"),
    DAMAGE_ZOGLIN("damage-zoglin"),
    DAMAGE_ZOMBIFIED_PIGLIN("damage-zombified-piglin"),
    DAMAGE_GIANT("damage-giant"),
    DAMAGE_SHULKER_BULLET("damage-shulker-bullet"),
    DAMAGE_SHULKER("damage-shulker"),
    DAMAGE_ARMOR_STAND("damage-armorstand"),
    DAMAGE_DONKEY("damage-donkey"),
    DAMAGE_MULE("damage-mule"),
    DAMAGE_HORSE("damage-horse"),
    DAMAGE_SKELETON_HORSE("damage-skeleton-horse"),
    DAMAGE_ZOMBIE_HORSE("damage-zombie-horse"),
    DAMAGE_EVOKER("damage-evoker"),
    DAMAGE_VEX("damage-vex"),
    DAMAGE_VINDICATOR("damage-vindicator"),
    DAMAGE_ILLUSIONER("damage-illusioner"),
    DAMAGE_COW("damage-cow"),
    DAMAGE_MUSHROOM_COW("damage-mooshroom"),
    DAMAGE_CREEPER("damage-creeper"),
    DAMAGE_SPIDER("damage-spider"),
    DAMAGE_SLIME("damage-slime"),
    DAMAGE_GHAST("damage-ghast"),
    DAMAGE_ENDERMAN("damage-enderman"),
    DAMAGE_CAVE_SPIDER("damage-cave-spider"),
    DAMAGE_SILVERFISH("damage-silverfish"),
    DAMAGE_BLAZE("damage-blaze"),
    DAMAGE_MAGMA_CUBE("damage-magma-cube"),
    DAMAGE_WITHER("damage-wither"),
    DAMAGE_BAT("damage-bat"),
    DAMAGE_WITCH("damage-witch"),
    DAMAGE_ENDERMITE("damage-endermite"),
    DAMAGE_DROWNED("damage-drowned"),
    DAMAGE_PHANTOM("damage-phantom"),
    DAMAGE_PILLAGER("damage-pillager"),
    DAMAGE_RAVAGER("damage-ravager"),
    DAMAGE_PIG("damage-pig"),
    DAMAGE_SHEEP("damage-sheep"),
    DAMAGE_CHIKEN("damage-chicken"),
    DAMAGE_SQUID("damage-squid"),
    DAMAGE_WOLF("damage-wolf"),
    DAMAGE_SNOWMAN("damage-snow-golem"),
    DAMAGE_OCELOT("damage-ocelot"),
    DAMAGE_BEE("damage-bee"),
    DAMAGE_CAT("damage-cat"),
    DAMAGE_COD("damage-cod"),
    DAMAGE_DOLPHIN("damage-dolphin"),
    DAMAGE_FOX("damage-fox"),
    DAMAGE_PANDA("damage-panda"),
    DAMAGE_PUFFERFISH("damage-pufferfish"),
    DAMAGE_TROPICAL_FISH("damage-tropicalfish"),
    DAMAGE_SALMON("damage-salmon"),
    DAMAGE_TURTLE("damage-turtle"),
    DAMAGE_TRADER_LLAMA("damage-trader-llama"),
    DAMAGE_IRON_GOLEM("damage-iron-golem"),
    DAMAGE_RABBIT("damage-rabbit"),
    DAMAGE_POLAR_BEAR("damage-polar-bear"),
    DAMAGE_LLAMA("damage-llama"),
    DAMAGE_PARROT("damage-parrot"),
    DAMAGE_VILLAGER("damage-villager"),
    DAMAGE_WANDERING_TRADER("damage-wandering-trader"),
    DAMAGE_PLAYER("damage-player"),
    DAMAGE_ENDER_CRYSTAL("damage-ender-crystal"),
    DAMAGE_DRAGON("damage-ender-dragon"),
    BREAK_BEDROCK("block-break-bedrock"),
    BREAK_BLOCK("block-break"),
    BREAK_VEHICLE("vehicle-break"),
    BREAK_HANGING("creative-hanging-break"),
    BREAK_HANGING_SURVIVAL("survival-hanging-break"),
    BREAK_FARMLAND("destroy_farmland"),
    BREAK_ARMOR_STAND("armor_stand-break"),
    USE_ENDER_PEARL("ender_pearl"),
    USE_EYE_OF_ENDER("eye_of_ender"),
    USE_SNOWBALL("snowball"),
    USE_MONSTER_EGG("monster_egg"),
    USE_FLINT_STEAL("ignite"),
    USE_EXP_BOTTLE("exp_bottle"),
    USE_EGG("chicken_egg"),
    USE_POTION("potion"),
    USE_BOW("shooting"),
    USE_MARKED("use-marked"),
    USE_FISHING_ROD("fish"),
    USE_FURNACE_MINECART("container-furnaceminecart"),
    USE_BLOCKEDCREATIVE("disabled-item"),
    MODIFY_MOB_SPAWNER("mob_spawner-modify"),
    ITEM_MARK("item-mark"),
    CRAFT_MARKED("craft-marked"),
    ANVIL_MARKED("anvil-marked"),
    ENCHANT_MARKED("enchant-marked"),
    ENTITY_MARK("entity-mark"),
    FEED("feed"),
    SADDLE("saddle"),
    FEED_ZOMBIE("feed-zombie"),
    BLOCK_MARKED("block-marked"),
    DYE_SHEEP("change-sheep-color"),
    ERR_CHUNK_DATA_NOT_LOADED("err-chunkdata-not-loaded"),
    TOTEM("totem"),
    CREATIVE_SWITCH("creative-switch"),
    SURVIVAL_SWITCH("survival-switch"),
    SPECTATOR_SWITCH("spectator-switch"),
    ADVENTURE_SWITCH("adventure-switch"),
    ERR_ALREADY("err-already"),
    COMMAND("command"),
    BLOCKED_ON_ANVIL("blocked-on-anvil"),
    SURVIVAL_ENDER_PEARL_DELAY("survival-enderpearl-delay"),
    CREATIVE_ENDER_PEARL_DELAY("creative-enderpearl-delay"),
    ERR_INVENTORY_DATA("err-playerdata-loading"),
    MARK_SUCCESS("mark-success"),
    MARK_NOTHING("mark-nothing"),
    UNMARK_SUCCESS("unmark-success"),
    UNMARK_ALREADY("unmark-alread"),
    UNMARK_NOTHING("unmark-nothing"),
    RELOAD_SUCCESS("reload-success"),
    VERSION_INFO("version-info"),
    INTERACT_CREATIVE_ENTITY("creative-entity"),
    PLAYER_ONLY_CMD("player-only-command"),
    NO_PERMISSION("no-permission-command"),
    NO_PERMISSION_HOVER("no-permission-command-hover"),
    NO_PERMISSION_INVENTORY("no-permission-inventory"),
    NO_PERMISSION_SPECTATOR("no-permission-spectator"),
    NO_PERMISSION_ADVENTURE("no-permission-adventure"),
    INVALID_HELP_PAGE_MENU("invalid-help-menu-page"),
    BLOCKED_COMMAND_ITEM_HOLD("blocked-command-item-hold"),
    BLOCKED_COMMAND_ITEM_INVENTORY("blocked-command-item-inventory"),
    BLOCKED_COMMAND_INV_MATERIAL("blocked-command-inv-material"),
    BLOCKED_COMMAND_INV_SKULL("blocked-command-inv-skull"),
    BLOCKED_COMMAND_INV_POTION("blocked-command-inv-potion"),
    BLOCKED_COMMAND_GAMEMODE("blocked-command-gamemode"),
    WORLD_EDIT_NOT_LOADED("world-edit-not-loaded"),
    WORLDEDIT_NOT_LOADED("worldedit-not-loaded"),
    WORLD_EDIT_NO_SELECTION("world-edit-no-selection"),
    WORLD_EDIT_NO_MARKED("world-edit-no-marked"),
    WORLD_EDIT_UNMARKED_SUCCESS("world-edit-unmark-success"),
    NOT_FOUND("not-found"),
    TARGET_NEVER_JOINED("target-never-joined"),
    INVALID_NUMBER("invalid-number"),
    INVALID_INVENTORY_NUMBER("invalid-inventory-number"),
    INVALID_GAMEMODE("invalid-gamemode"),
    CLEAR_USAGE("clear-usage"),
    CLEAR_YOURSELF("clear-yourself"),
    CLEAR_TARGET_INVALID_GAMEMODE("clear-target-invalid-gamemode"),
    CLEAR_SUCCESS_OWN("clear-success-own"),
    CLEAR_SUCCESS_OTHERS("clear-success-others"),
    CLEAR_SUCCESS_BY_OTHERS("clear-success-by-others"),
    INVSEE_USAGE("invsee-usage"),
    INVSEE_YOURSELF("invsee-yourself"),
    INVSEE_TARGET_BYPASS("invsee-target-bypass"),
    INVSEE_TARGET_OFFLINE("invsee-target-offline"),
    INVEDIT_TARGET_BYPASS("invedit-target-bypass"),
    INVEDIT_NO_OPERATION("invedit-no-operation"),
    STATUS_USAGE("status-usage"),
    STATUS_YOURSELF("status-yourself"),
    STATUS_INFO("status-info"),
    STATUS_INFO_OFFLINE("status-info-offline"),
    TRIDENT_DENY("trident-deny"),
    WATERLOGGED_EMPTY("waterlogged-empty"),
    ITEMFRAME_ATTACHED("itemframe-attached"),
    LIQUID_CREATIVE("liquid-creative"),
    LIQUID_CREATIVE_DISPENSE("liquid-creative-dispense"),
    FLOWERPOT_SURVIVAL("flowerpot-survival");

    private static String prefix;
    private static String errorPrefix;
    private static String normalPrefix;
    private final String key;
    private String text;

    static FileConfiguration load() {
        CreativeSecurityPlugin plugin = CreativeSecurityPlugin.getInstance();
        String defaultLangFileName = "messages_ru.yml";
        Locale language = Config.language;
        File file = new File(plugin.getDataFolder(), "messages_" + language + ".yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        if (!file.exists()) {
            CreativeSecurityPlugin.getInstance().saveResource(defaultLangFileName, false);
        }
        YamlConfiguration messages = YamlConfiguration.loadConfiguration((File)file);
        Message.setPrefix(ChatColor.translateAlternateColorCodes((char)'&', (String)messages.getString("denial-prefix")));
        Message.setErrorPrefix(ChatColor.translateAlternateColorCodes((char)'&', (String)messages.getString("err-prefix")));
        Message.setNormalPrefix(ChatColor.translateAlternateColorCodes((char)'&', (String)messages.getString("normal-prefix")));
        YamlConfiguration defaultMessages = null;
        for (Message message : Message.values()) {
            try {
                message.setText(ChatColor.translateAlternateColorCodes((char)'&', (String)messages.getString(message.key)));
            }
            catch (Exception ex) {
                if (defaultMessages == null) {
                    defaultMessages = new YamlConfiguration();
                    try {
                        defaultMessages.loadFromString(Message.stringFromInputStream(CreativeSecurityPlugin.getInstance().getResource(defaultLangFileName)));
                    }
                    catch (InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                }
                messages.set(message.key, (Object)defaultMessages.getString(message.key));
                try {
                    message.setText(ChatColor.translateAlternateColorCodes((char)'&', (String)messages.getString(message.key)));
                    
                }
                catch (NullPointerException npe) {
                    System.out.println("NOTE:                                                 Null messages key: " + message.key);
                }
                try {
                    messages.save(file);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return messages;
    }

    private static String stringFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\A").next();
    }

    private Message(String key) {
        this.key = Objects.requireNonNull(key, "key was null");
    }

    static void setPrefix(String prefix) {
        Message.prefix = Objects.requireNonNull(prefix, "prefix was null");
    }

    static void setNormalPrefix(String normalPrefix) {
        Message.normalPrefix = Objects.requireNonNull(normalPrefix, "normalPrefix was null");
    }

    static void setErrorPrefix(String errorPrefix) {
        Message.errorPrefix = Objects.requireNonNull(prefix, "errorPrefix was null");
    }

    static String getErrorPrefix() {
        return Objects.requireNonNull(errorPrefix, "errorPrefix was null");
    }

    static String getPrefix() {
        return Objects.requireNonNull(prefix, "prefix was null");
    }

    static String getNormalPrefix() {
        return Objects.requireNonNull(normalPrefix, "normalPrefix was null");
    }

    public String getText() {
        return Objects.requireNonNull(this.text, "text was null. Message: " + this.name());
    }

    void setText(String value) {
        this.text = Objects.requireNonNull(value, "value was null. Message: " + this.name());
    }

    String error() {
        return errorPrefix + this.getText();
    }

    static StringBuilder applyArgs(StringBuilder message, String[] ... args) {
        return Message.applyArgs(message, Arrays.asList(args));
    }

    static StringBuilder applyArgs(StringBuilder message, List<String[]> args) {
        Objects.requireNonNull(args, "args was null");
        for (String[] arg : args) {
            int index;
            if (arg.length != 2) continue;
            String key = "{" + Objects.requireNonNull(arg[0], "replacement key was null") + "}";
            String value = Objects.requireNonNull(arg[1], "replacement value was null");
            while ((index = message.indexOf(key)) >= 0) {
                message.replace(index, key.length() + index, value);
            }
        }
        return message;
    }

    StringBuilder applyArgs(HumanEntity player, String[] ... extraArgs) {
        Objects.requireNonNull(extraArgs, "extraArgs was null");
        String text = this.getText();
        if (text.indexOf(123) == -1) {
            return new StringBuilder(text);
        }
        StringBuilder message = new StringBuilder(text);
        ArrayList<String[]> args = new ArrayList<String[]>();
        if (extraArgs.length > 0) {
            args.addAll(Arrays.asList(extraArgs));
        }
        if (player != null) {
            args.add(new String[]{"player", Objects.toString(player.getName(), "???")});
        }
        return Message.applyArgs(message, args);
    }

    private boolean checkCooldown(HumanEntity player) {
        if (Config.enableMessageCooldown) {
            long time = System.currentTimeMillis();
            Map<String,Long> cooldown = (Map<String,Long>)CreativeSecurityPlugin.getMetadata((Metadatable)player, "msg-cooldown", () -> new HashMap(2)).value();
            cooldown.values().removeIf(it -> it < time);
            if (cooldown.computeIfAbsent(this.key, k -> time) != time) {
                return true;
            }
            cooldown.put(this.key, time + TimeUnit.SECONDS.toMillis(Config.messageCooldown));
        }
        return false;
    }

    public void sendInfo(CommandSender commandSender, String[] ... extraArgs) {
        StringBuilder message;
        HumanEntity humanEntity = null;
        if (commandSender instanceof HumanEntity) {
            humanEntity = (HumanEntity)commandSender;
        }
        if ((message = this.applyArgs(humanEntity, extraArgs)).length() > 0) {
            message.insert(0, Message.getNormalPrefix());
            commandSender.sendMessage(message.toString());
        }
    }

    public void sendInfo(CommandSender sender) {
        sender.sendMessage(Message.getNormalPrefix() + this.getText());
    }

    public void sendDenial(CommandSender commandSender, String[] ... extraArgs) {
        HumanEntity humanEntity = null;
        if (commandSender instanceof HumanEntity && this.checkCooldown(humanEntity = (HumanEntity)commandSender)) {
            return;
        }
        StringBuilder message = this.applyArgs(humanEntity, extraArgs);
        if (message.length() > 0) {
            message.insert(0, Message.getPrefix());
            commandSender.sendMessage(message.toString());
        }
    }

    public void sendError(CommandSender commandSender) {
        if (commandSender instanceof HumanEntity && this.checkCooldown((HumanEntity)commandSender)) {
            return;
        }
        String error = this.error();
        if (!error.isEmpty()) {
            commandSender.sendMessage(error);
        }
    }

    public static boolean hasPermission(Player player, Permission permission) {
        return Message.hasPermission((Permissible)player, permission.getName());
    }

    public static boolean hasPermission(Permissible user, String permission) {
        return Message.hasPermission(user, permission, NO_PERMISSION);
    }

    public static boolean hasPermission(Permissible user, String permission, Message message) {
        if (user.hasPermission(permission)) {
            return true;
        }
        if (user instanceof Player) {
            Player player = (Player)user;
            String chatMessage = Message.getNormalPrefix().concat(message.getText());
            TextComponent textComponent = new TextComponent(chatMessage);
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(NO_PERMISSION_HOVER.applyArgs((HumanEntity)player, new String[][]{{"permission", permission}}).toString()).create()));
            try {
                player.spigot().sendMessage((BaseComponent)textComponent);
            }
            catch (NoSuchMethodError tacoSpigotSupport) {
                player.sendMessage(chatMessage);
            }
        }
        return false;
    }
}

