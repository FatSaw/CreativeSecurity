package gasha.creativesecurity;

import gasha.creativesecurity.ChangeGameModeCommands;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.Lazy;
import gasha.creativesecurity.SqlConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Config {
    static boolean jdbc = false;
    static boolean validateEnumMessages = true;
    static Locale language = Locale.ENGLISH;
    static boolean enableMessageCooldown = true;
    static int messageCooldown = 3;
    public static int worldEditIntegration = 3;
    static boolean dataLogEnabled = false;
    static boolean disableservershield = false;
    static EnumSet<Material> untrackedMaterials;
    static EnumSet<Material> immovableBlocks;
    static boolean arrowmelon;
    static boolean dropmelon;
    static boolean wgmelon;
    static List<String> blacklistpearl;
    static String melonsound;
    static boolean checkItemPickup;
    static boolean checkCreativeDrop;
    static boolean checkSurvivalDrop;
    static boolean checkPlayerDeath;
    static boolean checkMobSpawnerPlacement;
    static boolean checkMobSpawnerChanging;
    static boolean checkCreativeBowShots;
    static boolean checkApplyNameTag;
    static boolean checkBreakingBedRock;
    static boolean deathCreativeKeepInventory;
    static boolean deathCreativeKeepLevel;
    static boolean deathCreativeDestroysCreativeDrops;
    static boolean deathSurvivalKeepInventory;
    static boolean deathSurvivalKeepLevel;
    static boolean deathSurvivalDestroysCreativeDrops;
    static boolean whenGameModeChangesRemovePotions;
    static boolean whenGameModeChangesUnleashEntities;
    static boolean whenGameModeChangesCloseInventory;
    static boolean whenGameModeChangesRemoveCreativeArmor;
    static boolean trackCreativeItemSpawn;
    static boolean trackPlaceCreative;
    static boolean trackCreativeDrop;
    static boolean trackEntitySpawnByEgg;
    static boolean trackFishingHook;
    static boolean trackFish;
    static boolean trackCreativeFishBucket;
    static boolean trackArrows;
    static boolean trackTrident;
    static boolean trackThrownPotions;
    static boolean trackExpBottle;
    static boolean trackEgg;
    static boolean trackEnderEye;
    static boolean trackSnowball;
    static boolean trackEnderPearl;
    static boolean trackCreativeCreatureLoot;
    static boolean trackSnowmanTrail;
    static boolean trackAttachedDrops;
    static boolean trackTnt;
    static boolean trackCreativeStructures;
    static boolean trackCreativeSpread;
    static boolean trackCreativePlantLoot;
    static boolean trackEntityForming;
    static boolean trackItemFrame;
    static boolean trackArmorStand;
    static boolean trackPainting;
    static boolean trackSilverfishHatching;
    static boolean trackSilverfishHiding;
    static boolean entityMarkApplyName;
    static boolean entityMarkSetNameVisible;
    static EnumSet<EntityType> entityMarkHideNameFor;
    static boolean disableEarlyHopperPickup;
    static boolean disableUsageOfCreativeItems;
    static boolean disableUsageOfCreativeBlocks;
    static boolean disableUsageOfCreativeItemsOnAnvil;
    static boolean disableCreativeArmor;
    static boolean disableCreativeEnchantment;
    static boolean disableCreativeSmelting;
    static boolean disableCreativeFuel;
    static boolean disableCreativeDispensing;
    static boolean disableCreativeTridentUsage;
    static boolean disableDispensingToCreativeEntities;
    static boolean disableDispensersFormingEntityFromCreativeBlocks;
    static boolean disableChickenSpawningFromCreativeEggs;
    static boolean disableEndermiteSpawningFromCreativeEnderPearl;
    static boolean disableCreativeEnderEyeDropping;
    static boolean disableCreativeCrafting;
    static boolean disableCreativePotions;
    static boolean disableFarmlandTrampling;
    static boolean disableEndermanGettingCreativeBlocks;
    static boolean disableCreativeItemFrameGettingHitByMonster;
    static boolean disableCreativeArmorStandGettingHitByMonster;
    static boolean disableCreativeHangingFromBreaking;
    static boolean disableHoppersPickingUpCreativeItems;
    static boolean disableHoppersMovingCreativeItems;
    static Material miscEmptyCreativeItemFrameMarker;
    static long miscSurvivalEnderPearlDelay;
    static long miscCreativeEnderPearlDelay;
    static boolean miscTreatGrassAsPlant;
    static boolean miscTreatMyceliumAsPlant;
    static boolean miscTreatDoublePlantAsPlant;
    static boolean miscTreatMushroomAsPlant;
    static boolean miscTreatVineAsPlant;
    static boolean miscChangeToDefaultGameModeOnQuit;
    static GameMode miscChangeToSpecificGameModeOnQuit;
    static boolean miscRestoreEmptyInventories;
    static boolean miscAllowForcingAlreadyUsedInv;
    static EnumMap<GameMode, Set<String>> blockedCommands;
    static EnumSet<Material> blockedItemsIntoAnvil;
    static EnumSet<Material> blockedItemsCreativeSpawn;
    static EnumSet<Material> blockedItemsFromCreativeInventoryActions;
    static EnumSet<Material> blockedItemsCreativeUsage;
    
    private static Map<GameMode, Map<GameMode, ChangeGameModeCommands>> changeGameModeHooks;

    private static void onGameModeChange(ChangeGameModeCommands commands, Player player, Lazy<List<String[]>> args) {
        if (commands != null) {
            commands.onGameModeChange(player, args.get());
        }
    }

    static void onGameModeChange(Player player, GameMode from, GameMode to) {
        Lazy<List<String[]>> args = new Lazy<List<String[]>>(() -> ChangeGameModeCommands.buildArgs(player, from, to));
        Map<GameMode, ChangeGameModeCommands> toAny = changeGameModeHooks.getOrDefault(null, Collections.emptyMap());
        Config.onGameModeChange((ChangeGameModeCommands)toAny.get(null), player, args);
        Config.onGameModeChange((ChangeGameModeCommands)toAny.get(from), player, args);
        Map<GameMode, ChangeGameModeCommands> toSpecific = changeGameModeHooks.getOrDefault(to, Collections.emptyMap());
        Config.onGameModeChange((ChangeGameModeCommands)toSpecific.get(null), player, args);
        Config.onGameModeChange((ChangeGameModeCommands)toSpecific.get(from), player, args);
    }

    private static Material material(Logger logger, String line) {
        Material material = Material.matchMaterial((String)line);
        if (material == null && validateEnumMessages) {
            logger.info("Skipping unknown material: " + line);
        }
        return material;
    }

    private static EntityType entity(Logger logger, String line) {
        EntityType entity;
        block5: {
            try {
                int entityId = Integer.parseInt(line);
                entity = EntityType.fromId(entityId);
            }
            catch (NumberFormatException ignored) {
                entity = EntityType.fromName(line);
                if (entity != null) break block5;
                try {
                    entity = EntityType.valueOf(line.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    entity = null;
                }
            }
        }
        if (entity == null && validateEnumMessages) {
            logger.info("Skipping unknown entity: " + line);
        }
        return entity;
    }

    private static EnumSet<EntityType> entitySet(Logger logger, List<String> list) {
        return list.stream().map(String::trim).filter(line -> !line.isEmpty()).map(line -> Config.entity(logger, line)).filter(Objects::nonNull).collect(Collectors.toCollection(() -> EnumSet.noneOf(EntityType.class)));
    }

    private static EnumSet<Material> materialSet(Logger logger, List<String> list) {
        return list.stream().map(String::trim).filter(line -> !line.isEmpty()).map(line -> Config.material(logger, line)).filter(Objects::nonNull).collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
    }

    private static Set<String> nonEmptySet(List<String> list) {
        return list.stream().map(String::trim).filter(line -> !line.isEmpty()).collect(Collectors.toSet());
    }

    private static ConfigurationSection getOrCreateSection(ConfigurationSection base, String path) {
        ConfigurationSection sec = base.getConfigurationSection(path);
        if (sec != null) {
            return sec;
        }
        return base.createSection(path);
    }

    static void load() {
        if (miscEmptyCreativeItemFrameMarker == null) {
            try {
                miscEmptyCreativeItemFrameMarker = Material.valueOf((String)"STRUCTURE_VOID");
            }
            catch (Exception Ex) {
                miscEmptyCreativeItemFrameMarker = Material.AIR;
            }
        }
        CreativeSecurityPlugin plugin = CreativeSecurityPlugin.getInstance();
        Logger logger = plugin.getLogger();
        logger.info("Loading configurations.");
        FileConfiguration config = plugin.getConfig();
        validateEnumMessages = config.getBoolean("validate-enums-messages", true);
        language = Locale.forLanguageTag(config.getString("language", "ru"));
        messageCooldown = config.getInt("message-cooldown", 3);
        worldEditIntegration = config.getInt("worldedit-integration", 1);
        dataLogEnabled = config.getBoolean("debug-data", false);
        disableservershield = config.getBoolean("disable-servershield", true);
        enableMessageCooldown = messageCooldown > 0;
        untrackedMaterials = Config.materialSet(logger, config.getStringList("untracked-materials"));
        untrackedMaterials.add(Material.FIRE);
        untrackedMaterials.add(Material.AIR);
        immovableBlocks = Config.materialSet(logger, config.getStringList("immovable-blocks"));
        ConfigurationSection sec = Config.getOrCreateSection((ConfigurationSection)config, "check");
        checkItemPickup = sec.getBoolean("item-pickup", true);
        checkCreativeDrop = sec.getBoolean("creative-drop", true);
        checkSurvivalDrop = sec.getBoolean("survival-drop", true);
        checkPlayerDeath = sec.getBoolean("player-death", true);
        checkMobSpawnerPlacement = sec.getBoolean("mob-spawner.placement", true);
        checkMobSpawnerChanging = sec.getBoolean("mob-spawner.changing", true);
        checkCreativeBowShots = sec.getBoolean("creative-bow-shots", true);
        checkApplyNameTag = sec.getBoolean("apply-nametag", true);
        checkBreakingBedRock = sec.getBoolean("breaking-bedrock", true);
        sec = Config.getOrCreateSection((ConfigurationSection)config, "death");
        deathCreativeKeepInventory = sec.getBoolean("creative.keep-inventory", true);
        deathCreativeKeepLevel = sec.getBoolean("creative.keep-level", true);
        deathCreativeDestroysCreativeDrops = sec.getBoolean("creative.destroy-creative-drops", true);
        deathSurvivalKeepInventory = sec.getBoolean("survival.keep-inventory", false);
        deathSurvivalKeepLevel = sec.getBoolean("survival.keep-level", false);
        deathSurvivalDestroysCreativeDrops = sec.getBoolean("survival.destroy-creative-drops", true);
        sec = Config.getOrCreateSection((ConfigurationSection)config, "when-gamemode-changes");
        whenGameModeChangesRemovePotions = sec.getBoolean("remove-potions", true);
        whenGameModeChangesUnleashEntities = sec.getBoolean("unleash-entities", true);
        whenGameModeChangesCloseInventory = sec.getBoolean("close-inventory", true);
        whenGameModeChangesRemoveCreativeArmor = sec.getBoolean("remove-creative-armor", true);
        sec = Config.getOrCreateSection((ConfigurationSection)config, "track");
        trackCreativeItemSpawn = sec.getBoolean("creative-item-spawn", true);
        trackPlaceCreative = sec.getBoolean("place-creative", true);
        trackCreativeDrop = sec.getBoolean("creative-drop", true);
        trackEntitySpawnByEgg = sec.getBoolean("entity-spawn-by-egg", true);
        trackFishingHook = sec.getBoolean("fishing-hook", true);
        trackFish = sec.getBoolean("fish", true);
        trackCreativeFishBucket = sec.getBoolean("creative-fish-bucket", true);
        trackArrows = sec.getBoolean("arrows", true);
        trackThrownPotions = sec.getBoolean("thrown-potions", true);
        trackExpBottle = sec.getBoolean("exp-bottle", true);
        trackEgg = sec.getBoolean("egg", true);
        trackEnderEye = sec.getBoolean("ender-eye", true);
        trackSnowball = sec.getBoolean("snowball", true);
        trackEnderPearl = sec.getBoolean("ender-pearl", true);
        trackCreativeCreatureLoot = sec.getBoolean("creative-creature-loot", true);
        trackSnowmanTrail = sec.getBoolean("snowman-trail", true);
        trackAttachedDrops = sec.getBoolean("attached-drops", true);
        trackTnt = sec.getBoolean("tnt", true);
        trackCreativeStructures = sec.getBoolean("creative-structures", true);
        trackCreativeSpread = sec.getBoolean("creative-spread", true);
        trackCreativePlantLoot = sec.getBoolean("creative-plant-loot", true);
        trackEntityForming = sec.getBoolean("entity-forming", true);
        trackItemFrame = sec.getBoolean("item-frame", true);
        trackArmorStand = sec.getBoolean("armor-stand", true);
        trackPainting = sec.getBoolean("painting", true);
        trackSilverfishHatching = sec.getBoolean("silverfish-hatching", true);
        trackSilverfishHiding = sec.getBoolean("silverfish-hiding", true);
        sec = Config.getOrCreateSection((ConfigurationSection)config, "entity-mark");
        entityMarkApplyName = sec.getBoolean("apply-name", true);
        entityMarkSetNameVisible = sec.getBoolean("set-name-visible", true);
        entityMarkHideNameFor = Config.entitySet(logger, sec.getStringList("hide-name-for"));
        sec = Config.getOrCreateSection((ConfigurationSection)config, "disable");
        disableEarlyHopperPickup = sec.getBoolean("early-hopper-pickup", true);
        disableUsageOfCreativeItems = sec.getBoolean("usage-of-creative-items", true);
        disableUsageOfCreativeBlocks = sec.getBoolean("usage-of-creative-blocks", true);
        disableUsageOfCreativeItemsOnAnvil = sec.getBoolean("usage-of-creative-items-on-anvil", true);
        disableCreativeArmor = sec.getBoolean("usage-of-creative-armor", true);
        disableCreativeEnchantment = sec.getBoolean("creative-enchantment", true);
        disableCreativeSmelting = sec.getBoolean("creative-smelting", true);
        disableCreativeFuel = sec.getBoolean("creative-fuel", true);
        disableCreativeDispensing = sec.getBoolean("creative-dispensing", true);
        disableCreativeTridentUsage = sec.getBoolean("creative-trident-usage", true);
        disableDispensingToCreativeEntities = sec.getBoolean("dispensing-to-creative-entities", true);
        disableDispensersFormingEntityFromCreativeBlocks = sec.getBoolean("dispensers-forming-entities-from-creative-blocks", true);
        disableChickenSpawningFromCreativeEggs = sec.getBoolean("chicken-spawning-from-creative-eggs", true);
        disableEndermiteSpawningFromCreativeEnderPearl = sec.getBoolean("endermite-spawning-from-creative-ender-pearls", true);
        disableCreativeEnderEyeDropping = sec.getBoolean("creative-ender-eye-dropping", true);
        disableCreativeCrafting = sec.getBoolean("creative-crafting", true);
        disableCreativePotions = sec.getBoolean("creative-potion", true);
        disableFarmlandTrampling = sec.getBoolean("trampling-farmlands", true);
        disableEndermanGettingCreativeBlocks = sec.getBoolean("enderman-getting-creative-blocks", true);
        disableCreativeItemFrameGettingHitByMonster = sec.getBoolean("creative-item-frame-getting-hit-by-monsters", true);
        disableCreativeArmorStandGettingHitByMonster = sec.getBoolean("creative-armor-stand-getting-hit-by-monsters", true);
        disableCreativeHangingFromBreaking = sec.getBoolean("creative-hanging-from-breaking", true);
        disableHoppersPickingUpCreativeItems = sec.getBoolean("hoppers-picking-up-creative-items", true);
        disableHoppersMovingCreativeItems = sec.getBoolean("hoppers-moving-creative-items", true);
        sec = Config.getOrCreateSection((ConfigurationSection)config, "misc");
        miscEmptyCreativeItemFrameMarker = Config.material(logger, sec.getString("empty-creative-item-frame-marker", "STRUCTURE_VOID"));
        miscSurvivalEnderPearlDelay = sec.getInt("survival-ender-pearl-cooldown", 20);
        miscSurvivalEnderPearlDelay *= 1000L;
        miscCreativeEnderPearlDelay = sec.getInt("creative-ender-pearl-cooldown", 20);
        miscCreativeEnderPearlDelay *= 1000L;
        miscTreatGrassAsPlant = sec.getBoolean("treat-grass-as-plant", false);
        miscTreatMyceliumAsPlant = sec.getBoolean("treat-mycelium-as-plant", true);
        miscTreatDoublePlantAsPlant = sec.getBoolean("treat-double-pant-as-plant", true);
        miscTreatMushroomAsPlant = sec.getBoolean("treat-mushrooms-as-plant", true);
        miscTreatVineAsPlant = sec.getBoolean("treat-vine-as-plant", true);
        miscChangeToDefaultGameModeOnQuit = sec.getBoolean("change-to-default-gamemode-on-quit", false);
        String string = sec.getString("change-to-specific-gamemode-on-quit", "");
        if (string.isEmpty()) {
            miscChangeToSpecificGameModeOnQuit = null;
        } else {
            try {
                miscChangeToSpecificGameModeOnQuit = GameMode.valueOf((String)string.toUpperCase());
            }
            catch (IllegalArgumentException e2) {
                logger.severe("Invalid gamemode specified on config misc.change-to-specific-gamemode-on-quit. Using SURVIVAL instead!");
                miscChangeToSpecificGameModeOnQuit = GameMode.SURVIVAL;
            }
        }
        miscRestoreEmptyInventories = sec.getBoolean("restore-empty-inventories", false);
        miscAllowForcingAlreadyUsedInv = sec.getBoolean("allow-forcing-already-used-inv", true);
        sec = Config.getOrCreateSection((ConfigurationSection)config, "blocked-commands");
        GameMode[] gameModes = GameMode.values();
        GameMode[] gameModesWithNull = Arrays.copyOf(gameModes, gameModes.length + 1);
        blockedCommands.clear();
        for (GameMode gameMode : gameModes) {
            blockedCommands.put(gameMode, Config.nonEmptySet(sec.getStringList(gameMode.name().toLowerCase())));
        }
        Set<String> nonCreative = Config.nonEmptySet(sec.getStringList("non-creative"));
        blockedCommands.entrySet().stream().filter(e -> e.getKey() != GameMode.CREATIVE).forEachOrdered(e -> ((Set)e.getValue()).addAll(nonCreative));
        sec = Config.getOrCreateSection((ConfigurationSection)config, "blocked-items");
        blockedItemsIntoAnvil = Config.materialSet(logger, sec.getStringList("into-anvil"));
        blockedItemsCreativeSpawn = Config.materialSet(logger, sec.getStringList("creative-spawn"));
        blockedItemsFromCreativeInventoryActions = EnumSet.copyOf(blockedItemsCreativeSpawn);
        blockedItemsCreativeUsage = Config.materialSet(logger, sec.getStringList("creative-usage"));
        sec = Config.getOrCreateSection((ConfigurationSection)config, "change-gamemode-hooks");
        changeGameModeHooks = new HashMap<GameMode, Map<GameMode, ChangeGameModeCommands>>(gameModesWithNull.length);
        for (GameMode toGm : gameModesWithNull) {
            ConfigurationSection toSec = toGm == null ? sec : Config.getOrCreateSection(sec, "to-" + toGm.name().toLowerCase());
            for (GameMode fromGm : gameModesWithNull) {
                ConfigurationSection fromSec = fromGm == null ? toSec : Config.getOrCreateSection(toSec, "from-" + fromGm.name().toLowerCase());
                changeGameModeHooks.computeIfAbsent(toGm, gm -> new HashMap(gameModesWithNull.length)).put(fromGm, new ChangeGameModeCommands(fromSec));
            }
        }
        changeGameModeHooks.values().forEach(sub -> sub.values().removeIf(ChangeGameModeCommands::isEmpty));
        changeGameModeHooks.values().removeIf(Map::isEmpty);
        sec = Config.getOrCreateSection((ConfigurationSection)config, "more");
        arrowmelon = sec.getBoolean("melon-arrow-destroy.enabled", false);
        dropmelon = sec.getBoolean("melon-arrow-destroy.dropmelon", false);
        wgmelon = sec.getBoolean("melon-arrow-destroy.worldguard", false);
        melonsound = sec.getString("melon-arrow-destroy.sound", "nosound");
        blacklistpearl = config.getStringList("prevent-enderpearl-region");
        sec = Config.getOrCreateSection((ConfigurationSection)config, "persistence");
        jdbc = sec.getBoolean("jdbc", false);
        if (jdbc) {
            String host = sec.getString("host", "localhost");
            int port = sec.getInt("port", 3306);
            String user = sec.getString("username", "root");
            String pass = sec.getString("password", "");
            String driver = sec.getString("driver", "mysql");
            String database = sec.getString("database", "creativesecurity");
            String url = sec.getString("url", "default");
            if (url.isEmpty() || url.equals("default")) {
                url = "jdbc:" + driver + "://" + host + ":" + port + "/" + database;
            }
            try {
                SqlConfig.load(url, user, pass);
            }
            catch (Exception e3) {
                logger.log(Level.SEVERE, "Failed to load the MySQL settings! Shutting down for security!", e3);
                Bukkit.shutdown();
                return;
            }
        }
        CreativeSecurityPlugin.dataLogger.setLevel(Level.ALL);
        logger.info("All configurations have been loaded.");
    }

    static {
        immovableBlocks = EnumSet.noneOf(Material.class);
        arrowmelon = false;
        dropmelon = false;
        wgmelon = false;
        blacklistpearl = new ArrayList<String>();
        melonsound = "";
        checkItemPickup = true;
        checkCreativeDrop = true;
        checkSurvivalDrop = true;
        checkPlayerDeath = true;
        checkMobSpawnerPlacement = true;
        checkMobSpawnerChanging = true;
        checkCreativeBowShots = true;
        checkApplyNameTag = true;
        checkBreakingBedRock = true;
        deathCreativeKeepInventory = true;
        deathCreativeKeepLevel = true;
        deathCreativeDestroysCreativeDrops = true;
        deathSurvivalKeepInventory = false;
        deathSurvivalKeepLevel = false;
        deathSurvivalDestroysCreativeDrops = true;
        whenGameModeChangesRemovePotions = false;
        whenGameModeChangesUnleashEntities = true;
        whenGameModeChangesCloseInventory = true;
        whenGameModeChangesRemoveCreativeArmor = true;
        trackCreativeItemSpawn = true;
        trackPlaceCreative = true;
        trackCreativeDrop = true;
        trackEntitySpawnByEgg = true;
        trackFishingHook = true;
        trackFish = true;
        trackCreativeFishBucket = true;
        trackArrows = true;
        trackTrident = true;
        trackThrownPotions = true;
        trackExpBottle = true;
        trackEgg = true;
        trackEnderEye = true;
        trackSnowball = true;
        trackEnderPearl = true;
        trackCreativeCreatureLoot = true;
        trackSnowmanTrail = true;
        trackAttachedDrops = true;
        trackTnt = true;
        trackCreativeStructures = true;
        trackCreativeSpread = true;
        trackCreativePlantLoot = true;
        trackEntityForming = true;
        trackItemFrame = true;
        trackArmorStand = true;
        trackPainting = true;
        trackSilverfishHatching = true;
        trackSilverfishHiding = true;
        entityMarkApplyName = true;
        entityMarkSetNameVisible = true;
        entityMarkHideNameFor = EnumSet.noneOf(EntityType.class);
        disableEarlyHopperPickup = true;
        disableUsageOfCreativeItems = true;
        disableUsageOfCreativeBlocks = true;
        disableUsageOfCreativeItemsOnAnvil = true;
        disableCreativeArmor = true;
        disableCreativeEnchantment = true;
        disableCreativeSmelting = true;
        disableCreativeFuel = true;
        disableCreativeDispensing = true;
        disableCreativeTridentUsage = true;
        disableDispensingToCreativeEntities = true;
        disableDispensersFormingEntityFromCreativeBlocks = true;
        disableChickenSpawningFromCreativeEggs = true;
        disableEndermiteSpawningFromCreativeEnderPearl = true;
        disableCreativeEnderEyeDropping = true;
        disableCreativeCrafting = true;
        disableCreativePotions = true;
        disableFarmlandTrampling = true;
        disableEndermanGettingCreativeBlocks = true;
        disableCreativeItemFrameGettingHitByMonster = true;
        disableCreativeArmorStandGettingHitByMonster = true;
        disableCreativeHangingFromBreaking = true;
        disableHoppersPickingUpCreativeItems = true;
        disableHoppersMovingCreativeItems = true;
        miscEmptyCreativeItemFrameMarker = null;
        miscSurvivalEnderPearlDelay = 20000L;
        miscCreativeEnderPearlDelay = 20000L;
        miscTreatGrassAsPlant = false;
        miscTreatMyceliumAsPlant = true;
        miscTreatDoublePlantAsPlant = true;
        miscTreatMushroomAsPlant = true;
        miscTreatVineAsPlant = true;
        miscChangeToDefaultGameModeOnQuit = false;
        miscChangeToSpecificGameModeOnQuit = null;
        miscRestoreEmptyInventories = false;
        miscAllowForcingAlreadyUsedInv = true;
        blockedCommands = new EnumMap(GameMode.class);
        blockedItemsIntoAnvil = EnumSet.noneOf(Material.class);
        blockedItemsCreativeSpawn = EnumSet.noneOf(Material.class);
        blockedItemsFromCreativeInventoryActions = EnumSet.noneOf(Material.class);
        blockedItemsCreativeUsage = EnumSet.noneOf(Material.class);
        changeGameModeHooks = Collections.emptyMap();
    }
}

