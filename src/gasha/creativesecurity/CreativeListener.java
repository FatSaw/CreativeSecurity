package gasha.creativesecurity;

import gasha.creativesecurity.BlockLocation;
import gasha.creativesecurity.BlockPosition;
import gasha.creativesecurity.Config;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.Lazy;
import gasha.creativesecurity.Message;
import gasha.creativesecurity.Pair;
import gasha.creativesecurity.PermissionKey;
import gasha.creativesecurity.RegionData;
import gasha.creativesecurity.RegionPosition;
import gasha.creativesecurity.SoundManager;
import gasha.creativesecurity.XMaterial;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EntityArrow;
import net.minecraft.server.v1_16_R3.EntityArrow.PickupStatus;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Lectern;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Smoker;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArrow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Dispenser;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.material.Wood;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class CreativeListener implements Listener {
    private static CreativeSecurityPlugin main = CreativeSecurityPlugin.getInstance();
    private static final boolean LOG_CHUNK_TIMINGS = false;
    private static final BlockFace[] BLOCK_SIDES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final String KEY_LEASHED_ENTITIES = "leashed-entities";
    private static final String KEY_CREATIVE = "creative";
    private static final String KEY_DELAY_ENDERPEARL_SURVIVAL = "delay-pearl-survival";
    private static final String KEY_DELAY_ENDERPEARL_CREATIVE = "delay-pearl-creative";
    private static final String KEY_REGION_DATA = "region-data";
    //private static final String CREATIVE_MARK = "" + ChatColor.AQUA + ChatColor.AQUA + ChatColor.AQUA + ChatColor.BOLD + ChatColor.RED + ChatColor.RESET;
    private static final String CREATIVE_MARK = "§4 ";
    //private static final String CREATIVE_MARK = "" + ChatColor.of("#abcdef");
    Set<UUID> logBlockBreak = new HashSet<UUID>(1);
    private WeakReference<Player> armorStandPlacer = null;
    private WeakReference<Egg> eggHit = null;
    private WeakReference<EnderPearl> enderPearlHit = null;
    private static Map<World, Map<RegionPosition, RegionData>> hotRegions = new ConcurrentHashMap<World, Map<RegionPosition, RegionData>>();
    private Map<Pair<World, RegionPosition>, Future<RegionData>> loadingRegions = new ConcurrentHashMap<Pair<World, RegionPosition>, Future<RegionData>>();
    private static EnumSet<Material> wearablearmor;
    private static String dateFormatString;
    private static boolean blockPlacingCooldownEnabled;
    private static int blockPlacingCooldown;
    private List<String> blockedCmdsHold;
    private List<String> blockedCmdsInventory;
    private List<String> blockedCmdsInventoryItems;
    private List<Material> blockCmdsOnMaterial;
    private List<String> blockCmdsOnSkullOwner;
    private List<String> blockCmdsOnPotion;
    private boolean disableBlockPhysicsEventChecks;
    private static Set<Material> doublePlant;
    private static Set<Material> woodenPlates;
    private static Set<Material> redRoses;
    private Set<String> cooldownMessage = new HashSet<String>();
    private EnderPearl pearlCache;
    private static EnumSet<Material> fishBuckets;
    private Set<String> blockPlacingCooldownPlayersSet = new HashSet<String>();
    private static final Set<Integer> CRAFTING_ARMOR_SLOTS;
    private Map<Material, Map<UUID, Long>> consumeItemCooldown = new HashMap<Material, Map<UUID, Long>>();
    

    void reload() {
        dateFormatString = main.getConfig().getString("infoitemDateFormat");
        blockPlacingCooldownEnabled = main.getConfig().getBoolean("block-place-cooldown.enabled");
        blockPlacingCooldown = main.getConfig().getInt("block-place-cooldown.cooldown");
        this.blockedCmdsHold = main.getConfig().getStringList("block-commands-hold.used-for-commands");
        this.blockedCmdsInventory = main.getConfig().getStringList("block-commands-inventory.used-for-commands");
        this.blockedCmdsInventoryItems = main.getConfig().getStringList("block-commands-inventory-items.used-for-commands");
        this.disableBlockPhysicsEventChecks = main.getConfig().getBoolean("disable-BlockPhysicsEvent-checks");
        this.blockCmdsOnMaterial = new ArrayList<Material>();
        this.blockCmdsOnSkullOwner = new ArrayList<String>();
        this.blockCmdsOnPotion = new ArrayList<String>();
        ConfigurationSection blockedCmdsInvItems = main.getConfig().getConfigurationSection("block-commands-inventory-items.items");
        block10: for (String key : blockedCmdsInvItems.getKeys(false)) {
            String type;
            ConfigurationSection processedSection = blockedCmdsInvItems.getConfigurationSection(key);
            switch (type = processedSection.getString("type").toUpperCase()) {
                case "ITEM": {
                    this.blockCmdsOnMaterial.add(Material.valueOf((String)processedSection.getString("material")));
                    continue block10;
                }
                case "POTION": {
                    this.blockCmdsOnPotion.add(processedSection.getString("effect").toUpperCase());
                    continue block10;
                }
                case "PLAYER_SKULL": {
                    this.blockCmdsOnSkullOwner.add(processedSection.getString("owner").toLowerCase());
                    continue block10;
                }
            }
            main.getLogger().severe("Invalid 'block-commands-inventory-items' type \"" + type + "\"");
        }
    }

    static void load() {
        try {
            wearablearmor = EnumSet.of(Material.LEATHER_HELMET, new Material[]{Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, XMaterial.GOLDEN_HELMET.parseMaterial(), XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), XMaterial.GOLDEN_LEGGINGS.parseMaterial(), XMaterial.GOLDEN_BOOTS.parseMaterial(), Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.valueOf((String)"ELYTRA"), Material.PUMPKIN, XMaterial.SKELETON_SKULL.parseMaterial(), XMaterial.WITHER_SKELETON_SKULL.parseMaterial()});
        }
        catch (Exception ex) {
            wearablearmor = EnumSet.of(Material.LEATHER_HELMET, new Material[]{Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, XMaterial.GOLDEN_HELMET.parseMaterial(), XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), XMaterial.GOLDEN_LEGGINGS.parseMaterial(), XMaterial.GOLDEN_BOOTS.parseMaterial(), Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.PUMPKIN, XMaterial.SKELETON_SKULL.parseMaterial(), XMaterial.WITHER_SKELETON_SKULL.parseMaterial()});
        }
    }

    void forceLoad(World world, RegionPosition pos) {
        if (Config.dataLogEnabled) {
            Future<RegionData> loading = this.loadingRegions.get(new Pair<World, RegionPosition>(world, pos));
            Map<RegionPosition, RegionData> worldData = hotRegions.get((Object)world);
            RegionData regionData = worldData != null ? worldData.get(pos) : null;
            CreativeSecurityPlugin.dataLogger.severe("Data loading debug: " + world.getName() + " - " + pos + " - Scheduled? " + String.valueOf(loading != null) + " Done? " + String.valueOf(loading != null ? Boolean.valueOf(loading.isDone()) : null) + " Cancelled? " + String.valueOf(loading != null ? Boolean.valueOf(loading.isCancelled()) : null) + " World? " + String.valueOf(worldData != null) + " Region? " + String.valueOf(regionData != null));
        }
    }

    boolean checkCreative(HumanEntity player, PermissionKey permissionKey) {
        return player.getGameMode() == GameMode.CREATIVE && CreativeListener.check(player, permissionKey);
    }

    boolean checkCreative(HumanEntity player, PermissionKey permissionKey, boolean hoverableNoPermsMessage) {
        return player.getGameMode() == GameMode.CREATIVE && CreativeListener.check(player, permissionKey, hoverableNoPermsMessage);
    }

    static boolean check(HumanEntity player, PermissionKey permissionKey) {
        if (player.hasPermission(Objects.requireNonNull(permissionKey, (String)"permissionKey was null").key)) {
            return false;
        }
        permissionKey.denial.sendDenial((CommandSender)player, new String[0][]);
        return true;
    }

    static boolean check(HumanEntity player, PermissionKey permissionKey, boolean hoverableMessage) {
        if (player.hasPermission(Objects.requireNonNull(permissionKey, (String)"permissionKey was null").key)) {
            return false;
        }
        String chatMessage = Message.getNormalPrefix().concat(permissionKey.denial.getText());
        TextComponent textComponent = new TextComponent(chatMessage);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.NO_PERMISSION_HOVER.applyArgs(player, new String[][]{{"permission", permissionKey.key}}).toString()).create()));
        try {
            player.spigot().sendMessage((BaseComponent)textComponent);
        }
        catch (NoSuchMethodError tacoSpigotSupport) {
            player.sendMessage(chatMessage);
        }
        return true;
    }

    private boolean check(HumanEntity player, PermissionKey permissionKey, String[] ... extraArgs) {
        if (player.hasPermission(Objects.requireNonNull(permissionKey, (String)"permissionKey was null").key)) {
            return false;
        }
        permissionKey.denial.sendDenial((CommandSender)player, extraArgs);
        return true;
    }

    boolean isCreative(Block block) {
        RegionData regionData = CreativeListener.getRegionData(block);
        return regionData != null && regionData.isCreative(new BlockPosition(block));
    }

    private boolean isCreative(Entity entity) {
        return entity != null && CreativeSecurityPlugin.getMetadata((Metadatable)entity, KEY_CREATIVE, null) != null;
    }

    boolean isCreative(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return false;
        }
        ItemMeta itemMeta = stack.getItemMeta();
        if (!itemMeta.hasLore()) {
            return false;
        }
        List<String> lore = itemMeta.getLore();
        return lore.size() != 0 && (lore.get(0)).startsWith(CREATIVE_MARK);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onHopperPickupItem(InventoryPickupItemEvent event) {
        if (Config.disableEarlyHopperPickup && event.getItem().getTicksLived() < 3) {
            event.setCancelled(true);
            return;
        }
        if (!Config.disableHoppersPickingUpCreativeItems) {
            return;
        }
        if (this.isCreative(event.getItem().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onEntityResurrect(EntityResurrectEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player)event.getEntity();
        PlayerInventory inventory = player.getInventory();
        ItemStack main = inventory.getItemInMainHand();
        try {
            if (main != null && main.getType() == XMaterial.TOTEM_OF_UNDYING.parseMaterial()) {
                if (!this.isCreative(main)) {
                    return;
                }
                ItemStack off = inventory.getItemInOffHand();
                if (off != null && off.getType() == XMaterial.TOTEM_OF_UNDYING.parseMaterial() && !this.isCreative(off)) {
                    inventory.setItemInMainHand(null);
                    inventory.setItemInOffHand(main);
                    return;
                }
                event.setCancelled(true);
                return;
            }
            ItemStack off = inventory.getItemInOffHand();
            if (off != null && off.getType() == XMaterial.TOTEM_OF_UNDYING.parseMaterial() && this.isCreative(off)) {
                event.setCancelled(true);
            }
        }
        finally {
            if (event.isCancelled()) {
                if (player.hasPermission(PermissionKey.BYPASS_USE_TOTEM.key)) {
                    event.setCancelled(false);
                } else {
                    player.sendMessage(PermissionKey.BYPASS_USE_TOTEM.denial.applyArgs((HumanEntity)player, new String[0][]).toString());
                }
            }
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onAnvil(PrepareAnvilEvent event) {
        boolean checkCreative = Config.disableUsageOfCreativeItemsOnAnvil;
        EnumSet<Material> blockedMaterialsIntoAnvil = Config.blockedItemsIntoAnvil;
        ItemStack result = event.getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        boolean marked = CreativeSecurityPlugin.getInstance().creativeListener.isCreative(result);
        HumanEntity creativeViewer = event.getViewers().stream().filter(human -> human.getGameMode() == GameMode.CREATIVE).findFirst().orElse(null);
        AnvilInventory anvil = event.getInventory();
        for (ItemStack stack : anvil.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) continue;
            if (blockedMaterialsIntoAnvil.contains((Object)stack.getType()) && event.getViewers().stream().anyMatch(human -> CreativeListener.check(human, PermissionKey.BYPASS_USE_BLOCKEDANVILITEM))) {
                event.setResult(null);
                return;
            }
            if (!checkCreative || creativeViewer == null && !CreativeSecurityPlugin.getInstance().creativeListener.isCreative(stack)) continue;
            if (event.getViewers().stream().anyMatch(human -> CreativeListener.check(human, PermissionKey.BYPASS_CRAFT_ANVIL))) {
                event.setResult(null);
                return;
            }
            if (marked || !event.getViewers().stream().anyMatch(viewer -> !viewer.hasPermission("creativesecurity.bypass.mark"))) continue;
            marked = true;
            if (CreativeSecurityPlugin.getInstance().creativeListener.isCreative(stack)) {
                ItemMeta itemMeta = result.getItemMeta();
                List<String> lore = itemMeta.getLore();
                lore = lore == null ? new ArrayList<String>(1) : new ArrayList<String>(lore);
                lore.add(0, stack.getItemMeta().getLore().get(0));
                itemMeta.setLore(lore);
                result.setItemMeta(itemMeta);
            } else {
                CreativeListener.mark(result, creativeViewer);
            }
            event.setResult(result);
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onHopperMoveItem(InventoryMoveItemEvent event) {
        if (!Config.disableHoppersMovingCreativeItems) {
            return;
        }
        if (this.isCreative(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onPrepareRecipe(PrepareItemCraftEvent event) {
        if (!Config.disableCreativeCrafting) {
            return;
        }
        CraftingInventory matrix = event.getInventory();
        ItemStack result = matrix.getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        boolean marked = this.isCreative(result);
        for (ItemStack stack : matrix.getMatrix()) {
            if (stack == null || !this.isCreative(stack)) continue;
            if (event.getViewers().stream().anyMatch(human -> CreativeListener.check(human, PermissionKey.BYPASS_CRAFT_MATRIX))) {
                matrix.setResult(null);
                return;
            }
            if (marked) continue;
            marked = true;
            ItemMeta itemMeta = result.getItemMeta();
            List<String> lore = itemMeta.getLore();
            lore = lore == null ? new ArrayList(1) : new ArrayList(lore);
            lore.add(0, stack.getItemMeta().getLore().get(0));
            itemMeta.setLore(lore);
            result.setItemMeta(itemMeta);
            matrix.setResult(result);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onCraft(CraftItemEvent event) {
        if (!Config.disableCreativeCrafting) {
            return;
        }
        for (ItemStack stack : event.getInventory().getMatrix()) {
            if (!this.isCreative(stack) || !CreativeListener.check(event.getWhoClicked(), PermissionKey.BYPASS_CRAFT_MATRIX)) continue;
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onPrepareEnchantment(PrepareItemEnchantEvent event) {
        if (!Config.disableCreativeEnchantment) {
            return;
        }
        if (Arrays.stream(event.getInventory().getContents()).anyMatch(this::isCreative) && CreativeListener.check((HumanEntity)event.getEnchanter(), PermissionKey.BYPASS_CRAFT_ENCHANT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onEnchant(EnchantItemEvent event) {
        if (!Config.disableCreativeEnchantment) {
            return;
        }
        if (this.isCreative(event.getItem()) && CreativeListener.check((HumanEntity)event.getEnchanter(), PermissionKey.BYPASS_CRAFT_ENCHANT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (!Config.disableCreativeSmelting) {
            return;
        }
        if (this.isCreative(event.getSource())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onFurnaceBurn(FurnaceBurnEvent event) {
        if (!Config.disableCreativeFuel) {
            return;
        }
        if (this.isCreative(event.getFuel())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    void onBlockDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.DISPENSER && event.getItem() != null) {
            UUID owner;
            Lazy<List<Block>> blocks;
            Map<BlockFace, Map<BlockFace, Set<Block>>> possible;
            Block front;
            Dispenser dispenser;
            Material itemMaterial = event.getItem().getType();
            if (itemMaterial == Material.PUMPKIN) {
                dispenser = (Dispenser)block.getState().getData();
                front = block.getRelative(dispenser.getFacing());
                possible = this.scanPossiblePumpkinHead(front.getState());
                blocks = this.getBlocks(possible);
                owner = blocks.get().stream().map(this::getOwnerId).filter(Objects::nonNull).findFirst().orElse(null);
                if (owner == null) {
                    return;
                }
                if (Config.disableDispensersFormingEntityFromCreativeBlocks && !possible.isEmpty()) {
                    event.setCancelled(true);
                }
                this.trackEntityFormedFromBlocks(null, Bukkit.getOfflinePlayer((UUID)owner), front, true, blocks);
            } else if (itemMaterial == XMaterial.WITHER_SKELETON_SKULL.parseMaterial()) {
                if (block.getWorld().getDifficulty() == Difficulty.PEACEFUL || event.getItem().getDurability() != 1) {
                    return;
                }
                dispenser = (Dispenser)block.getState().getData();
                front = block.getRelative(dispenser.getFacing());
                possible = this.scanPossibleWitherForms(front.getState());
                blocks = this.getBlocks(possible);
                owner = blocks.get().stream().map(this::getOwnerId).filter(Objects::nonNull).findFirst().orElse(null);
                if (owner == null) {
                    return;
                }
                if (Config.disableDispensersFormingEntityFromCreativeBlocks && !possible.isEmpty()) {
                    event.setCancelled(true);
                }
                this.trackEntityFormedFromBlocks(null, Bukkit.getOfflinePlayer((UUID)owner), front, false, blocks);
            } else if (itemMaterial == Material.BUCKET) {
                dispenser = (Dispenser)block.getState().getData();
                front = block.getRelative(dispenser.getFacing());
                Material frontMat = front.getType();
                if (!this.isCreative(block) && (frontMat == Material.WATER || frontMat == Material.LAVA) && this.isCreative(front)) {
                    event.setCancelled(true);
                    block.getLocation().getWorld().getNearbyEntities(block.getLocation(), 3.0, 2.0, 3.0).stream().filter(ent -> ent instanceof Player).map(ent -> (Player)ent).findFirst().ifPresent(player -> Message.LIQUID_CREATIVE_DISPENSE.sendDenial((CommandSender)player, new String[][]{{"liquid", front.getType().toString().toLowerCase()}}));
                    return;
                }
            }
        }
        if (Config.disableCreativeDispensing) {
            if (this.isCreative(event.getItem())) {
                event.setCancelled(true);
            }
        } else if (Config.disableDispensingToCreativeEntities && block.getType() == Material.DISPENSER) {
            World world = block.getWorld();
            BlockState state = block.getState();
            if (state == null) {
                return;
            }
            MaterialData data = state.getData();
            if (!(data instanceof org.bukkit.block.Dispenser)) {
                return;
            }
            Location location = state.getLocation();
            BlockFace facing = ((Dispenser)data).getFacing();
            location.add((double)(facing.getModX() / 2), (double)(facing.getModY() / 2), (double)(facing.getModZ() / 2));
            Collection<Entity> nearbyEntities = world.getNearbyEntities(location, 0.5, 0.5, 0.5);
            if (nearbyEntities.stream().anyMatch(this::isCreative)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        ItemStack itemInHand;
        Set<String> blockedCommands = Config.blockedCommands.get((Object)event.getPlayer().getGameMode());
        if (blockedCommands != null && !blockedCommands.isEmpty()) {
            String fullCmd = event.getMessage().toLowerCase().substring(1);
            for (String blockedCmd : blockedCommands) {
                boolean pass = false;
                if (!blockedCmd.contains(" ")) {
                    String ceb = fullCmd.toLowerCase().split(" ")[0];
                    if (ceb.equals(blockedCmd)) {
                        pass = true;
                    }
                } else if (fullCmd.startsWith(blockedCmd)) {
                    pass = true;
                }
                if (!pass) continue;
                Player player = event.getPlayer();
                if (player.hasPermission(PermissionKey.BYPASS_ACTION_COMMAND.key)) break;
                event.setCancelled(true);
                Message.BLOCKED_COMMAND_GAMEMODE.sendInfo((CommandSender)player, new String[]{"command", event.getMessage().toLowerCase()}, new String[]{"gamemode", player.getGameMode().name().toLowerCase()});
                break;
            }
        }
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase().replace("/", "").split(" ")[0];
        if (this.blockedCmdsHold.contains(command) && (itemInHand = player.getInventory().getItemInHand()) != null && itemInHand.hasItemMeta()) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta.hasDisplayName() && !player.hasPermission("creativesecurity.bypass.hold.name") && main.getConfig().getStringList("block-commands-hold.blocked-display-names").contains(meta.getDisplayName())) {
                event.setCancelled(true);
                Message.BLOCKED_COMMAND_ITEM_HOLD.sendInfo((CommandSender)player);
                return;
            }
            if (meta.hasLore() && !player.hasPermission("creativesecurity.bypass.hold.lore") && this.areLoreBlocked(main.getConfig().getStringList("block-commands-hold.blocked-lores"), meta.getLore(), player.getName())) {
                event.setCancelled(true);
                Message.BLOCKED_COMMAND_ITEM_HOLD.sendInfo((CommandSender)player);
                return;
            }
        }
        if (this.blockedCmdsInventory.contains(command)) {
            Set<ItemMeta> inventoryItemsMeta = Arrays.stream(player.getInventory().getContents()).filter(itemStack -> itemStack != null && itemStack.hasItemMeta()).map(ItemStack::getItemMeta).collect(Collectors.toSet());
            for (ItemMeta itemMeta : inventoryItemsMeta) {
                if (!itemMeta.hasDisplayName() || player.hasPermission("creativesecurity.bypass.inventory.name") || !main.getConfig().getStringList("block-commands-inventory.blocked-display-names").contains(itemMeta.getDisplayName())) continue;
                event.setCancelled(true);
                Message.BLOCKED_COMMAND_ITEM_INVENTORY.sendInfo((CommandSender)player);
                return;
            }
            if (!player.hasPermission("creativesecurity.bypass.inventory.name")) {
                List<String> allItemsLore = inventoryItemsMeta.stream().filter(ItemMeta::hasLore).map(ItemMeta::getLore).flatMap(Collection::stream).collect(Collectors.toList());
                if (this.areLoreBlocked(main.getConfig().getStringList("block-commands-inventory.blocked-lores"), allItemsLore, player.getName())) {
                    event.setCancelled(true);
                    Message.BLOCKED_COMMAND_ITEM_INVENTORY.sendInfo((CommandSender)player);
                    return;
                }
            }
        }
        if (this.blockedCmdsInventoryItems.contains(command)) {
            List<ItemStack> inventory = Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).filter(i -> i.getType() != Material.AIR).collect(Collectors.toList());
            for (ItemStack item : inventory) {
                SkullMeta skullMeta;
                Material type = item.getType();
                if (this.blockCmdsOnMaterial.contains((Object)type)) {
                    Message.BLOCKED_COMMAND_INV_MATERIAL.sendDenial((CommandSender)player, new String[0][]);
                    event.setCancelled(true);
                    return;
                }
                if (!item.hasItemMeta()) continue;
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof PotionMeta) {
                    PotionMeta potMeta = (PotionMeta)meta;
                    String effType = potMeta.getBasePotionData().getType().getEffectType().getName();
                    if (!this.blockCmdsOnPotion.contains(effType)) continue;
                    event.setCancelled(true);
                    Message.BLOCKED_COMMAND_INV_POTION.sendDenial((CommandSender)player, new String[0][]);
                    return;
                }
                if (!(meta instanceof SkullMeta) || !this.blockCmdsOnSkullOwner.contains((skullMeta = (SkullMeta)meta).getOwner())) continue;
                event.setCancelled(true);
                Message.BLOCKED_COMMAND_INV_SKULL.sendDenial((CommandSender)player, new String[0][]);
                return;
            }
        }
    }

    private boolean areLoreBlocked(List<String> blockedLore, List<String> itemLore, String playerName) {
        for (String blockedLoreString : blockedLore) {
            for (String itemLoreString : itemLore) {
                if (!itemLoreString.equals(blockedLoreString.replace("%player%", playerName))) continue;
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onGiveItemsCommand(PlayerCommandPreprocessEvent event) {
        Player player;
        String cmd = event.getMessage().toLowerCase().replace("/", "").split(" ")[0];
        String[] args = event.getMessage().split(" ");
        if (args.length < 2) {
            return;
        }
        switch (cmd) {
            case "give": {
                if (args.length < 3) {
                    return;
                }
                player = Bukkit.getPlayerExact((String)args[1]);
                break;
            }
            case "item": 
            case "i": {
                player = event.getPlayer();
                break;
            }
            default: {
                return;
            }
        }
        if (player == null || player.getGameMode() != GameMode.CREATIVE) {
            return;
        }
        Player target = player;
        Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
            for (ItemStack itemStack : target.getInventory().getContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                if (Config.blockedItemsFromCreativeInventoryActions.contains((Object)itemStack.getType()) && CreativeListener.check((HumanEntity)target, PermissionKey.BYPASS_ACTION_SPAWNCREATIVEBLOCKED)) {
                    target.getInventory().remove(itemStack);
                    target.updateInventory();
                    continue;
                }
                if (this.isCreative(itemStack) || player.hasPermission("creativesecurity.bypass.mark")) continue;
                CreativeListener.mark(itemStack, (HumanEntity)target);
            }
        }, 3L);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.PHYSICAL) {
            Block clickedBlock = event.getClickedBlock();
            if (Config.disableFarmlandTrampling && clickedBlock != null && clickedBlock.getType() == XMaterial.FARMLAND.parseMaterial() && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_BREAK_FARMLAND)) {
                event.setCancelled(true);
            }
            return;
        }
        GameMode gameMode = player.getGameMode();
        if (Config.disableUsageOfCreativeItems && event.hasItem() && event.useItemInHand() != Event.Result.DENY && this.isCreative(event.getItem()) && gameMode != GameMode.CREATIVE && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_MARKED)) {
            event.setUseItemInHand(Event.Result.DENY);
            player.updateInventory();
        }
        if (gameMode == GameMode.CREATIVE && event.hasItem() && event.useItemInHand() != Event.Result.DENY && (Config.blockedItemsFromCreativeInventoryActions.contains((Object)event.getItem().getType()) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_SPAWNCREATIVEBLOCKED) || Config.blockedItemsCreativeUsage.contains((Object)event.getItem().getType()) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_BLOCKEDCREATIVE))) {
            event.setUseItemInHand(Event.Result.DENY);
            player.updateInventory();
        }
        if (gameMode == GameMode.CREATIVE && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.useItemInHand() != Event.Result.DENY && this.checkUseItem(player, event.getItem(), event.getClickedBlock())) {
            event.setUseItemInHand(Event.Result.DENY);
            player.updateInventory();
        }
        if (action == Action.RIGHT_CLICK_BLOCK) {
            Material blockType;
            Block clickedBlock;
            block31: {
                clickedBlock = event.getClickedBlock();
                Material clickedBlockMat = clickedBlock.getType();
                RegionData.BlockMark mark = this.getBlockMark(clickedBlock);
                if (mark == null) {
                    if (player.getGameMode() == GameMode.CREATIVE && event.hasItem() && clickedBlockMat == Material.FLOWER_POT) {
                        event.setCancelled(true);
                        Message.FLOWERPOT_SURVIVAL.sendDenial((CommandSender)player, new String[0][]);
                        player.updateInventory();
                    }
                    return;
                }
                try {
                    if (!event.getHand().equals(EquipmentSlot.HAND)) {
                        return;
                    }
                }
                catch (Exception oldmethod) {
                    // empty catch block
                }
                if (player.getGameMode() != GameMode.CREATIVE && (clickedBlockMat.toString().startsWith("POTTED_") || event.hasItem() && clickedBlockMat == Material.FLOWER_POT)) {
                    Message.USE_MARKED.sendDenial((CommandSender)player, new String[0][]);
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }
                try {
                    if (event.hasItem() && event.getItem().getType() == XMaterial.valueOf(main.getConfig().getString("infoitem").toUpperCase()).parseMaterial()) {
                        if (player.isSneaking() && event.hasItem() && player.hasPermission(PermissionKey.BLOCKINFO.key)) {
                            if (main.getConfig().getBoolean("cooldown-on-checking-placed")) {
                                if (this.cooldownMessage.contains(player.getName())) {
                                    return;
                                }
                                this.cooldownMessage.add(player.getName());
                                main.getServer().getScheduler().runTaskLater((Plugin)main, () -> this.cooldownMessage.remove(player.getName()), 20L);
                            }
                            Message.BLOCK_INFO.sendInfo((CommandSender)player, new String[]{"date", new SimpleDateFormat(dateFormatString).format(new Date(mark.date.toUnix()))}, new String[]{"player", Objects.toString(Bukkit.getOfflinePlayer((UUID)mark.playerId).getName(), mark.playerId.toString())});
                        }
                        return;
                    }
                }
                catch (Exception ex) {
                    if (!event.hasItem() || event.getItem().getType() == Material.FEATHER) break block31;
                    return;
                }
            }
            if ((blockType = clickedBlock.getType()) == Material.JUKEBOX) {
                if (gameMode == GameMode.CREATIVE) {
                    if (CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_JUKEBOX)) {
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
                } else if (Config.disableUsageOfCreativeBlocks && this.isCreative(clickedBlock) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_MARKEDBLOCK)) {
                    event.setUseInteractedBlock(Event.Result.DENY);
                }
            } else if (blockType == XMaterial.SPAWNER.parseMaterial()) {
                if (Config.checkMobSpawnerChanging && event.hasItem() && (event.getItem().getType().toString().equals("MONSTER_EGG") || event.getItem().getType().toString().contains("_SPAWN_EGG")) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_MODIFY_MOBSPAWNER)) {
                    event.setCancelled(true);
                }
            } else if (blockType == Material.TNT) {
                this.checkTnt(clickedBlock.getState(), 1);
            } else if ((blockType == Material.DROPPER || blockType == Material.DISPENSER || blockType == XMaterial.CRAFTING_TABLE.parseMaterial() || blockType == Material.ANVIL || blockType == Material.BEACON || blockType == Material.ENDER_CHEST || blockType == XMaterial.END_PORTAL_FRAME.parseMaterial() || blockType == XMaterial.ENCHANTING_TABLE.parseMaterial() || blockType == Material.FURNACE || blockType == Material.CHEST || blockType == Material.TRAPPED_CHEST || blockType.toString().contains("_BED") || blockType == Material.BREWING_STAND || blockType == Material.CAULDRON) && Config.disableUsageOfCreativeBlocks && gameMode != GameMode.CREATIVE && this.isCreative(clickedBlock) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_MARKEDBLOCK)) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    void onUseEnderPearl(PlayerInteractEvent event) {
        String metadataKey;
        PermissionKey delayBypass;
        long delay;
        if (!event.hasItem() || event.useItemInHand() == Event.Result.DENY || event.getItem().getType() != Material.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            delay = Config.miscCreativeEnderPearlDelay;
            delayBypass = PermissionKey.BYPASS_DELAY_CREATIVE_ENDERPEARL;
            metadataKey = KEY_DELAY_ENDERPEARL_CREATIVE;
        } else {
            delay = Config.miscSurvivalEnderPearlDelay;
            delayBypass = PermissionKey.BYPASS_DELAY_SURVIVAL_ENDERPEARL;
            metadataKey = KEY_DELAY_ENDERPEARL_SURVIVAL;
        }
        if (delay <= 0L) {
            return;
        }
        long now = System.currentTimeMillis();
        MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)player, metadataKey, null);
        if (metadata != null) {
            long limit = metadata.asLong();
            if (limit <= now) {
                player.removeMetadata(metadataKey, (Plugin)main);
            } else if (this.check((HumanEntity)player, delayBypass, new String[][]{{"remaining", Long.toString((limit - now) / 1000L)}})) {
                event.setUseItemInHand(Event.Result.DENY);
                Bukkit.getScheduler().runTask((Plugin)main, ((Player)player)::updateInventory);
                return;
            }
        }
        player.setMetadata(metadataKey, (MetadataValue)new FixedMetadataValue((Plugin)main, (Object)(now + delay)));
    }

    private boolean checkUseItem(Player player, ItemStack item, Block block) {
        Material type = item.getType();
        if (type == Material.FISHING_ROD) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_FISHINGROD);
        }
        if (type == Material.ENDER_PEARL) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_ENDERPEARL, true);
        }
        if (type == XMaterial.ENDER_EYE.parseMaterial()) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_EYEOFENDER, true);
        }
        if (type == XMaterial.SNOWBALL.parseMaterial()) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_SNOWBALL);
        }
        if (type.toString().equals("MONSTER_EGG") || type.toString().contains("_SPAWN_EGG")) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_MONSTEREGG, true);
        }
        if (type == Material.FLINT_AND_STEEL || type == XMaterial.FIRE_CHARGE.parseMaterial()) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_FLINTSTEAL);
        }
        if (type == XMaterial.EXPERIENCE_BOTTLE.parseMaterial()) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_EXPBOTTLE);
        }
        if (type == XMaterial.EGG.parseMaterial()) {
            return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_EGG);
        }
        if (type.toString().contains("POTION")) {
            return Config.disableCreativePotions && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_POTION);
        }
        if (type == XMaterial.WHEAT_SEEDS.parseMaterial() || type == XMaterial.BEETROOT_SEEDS.parseMaterial() || type == Material.MELON_SEEDS || type == Material.PUMPKIN_SEEDS || type == XMaterial.CARROT.parseMaterial() || type == XMaterial.POTATO.parseMaterial()) {
            return block != null && block.getType() == XMaterial.FARMLAND.parseMaterial() && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == XMaterial.NETHER_WART.parseMaterial()) {
            return block != null && block.getType() == Material.SOUL_SAND && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == Material.CACTUS) {
            return block != null && block.getType() == Material.CACTUS && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == XMaterial.ACACIA_SAPLING.parseMaterial() || type == XMaterial.BIRCH_SAPLING.parseMaterial() || type == XMaterial.DARK_OAK_SAPLING.parseMaterial() || type == XMaterial.JUNGLE_SAPLING.parseMaterial() || type == XMaterial.OAK_SAPLING.parseMaterial() || type == XMaterial.SPRUCE_SAPLING.parseMaterial()) {
            return block != null && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == Material.BROWN_MUSHROOM || type == Material.RED_MUSHROOM) {
            return Config.miscTreatMushroomAsPlant && block != null && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == Material.VINE) {
            return Config.miscTreatVineAsPlant && block != null && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (this.isDoublePlant(type)) {
            return Config.miscTreatDoublePlantAsPlant && block != null && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == XMaterial.CHORUS_FLOWER.parseMaterial()) {
            return block != null && block.getType() == XMaterial.END_STONE.parseMaterial() && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == Material.GRASS) {
            return Config.miscTreatGrassAsPlant && block != null && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == XMaterial.MYCELIUM.parseMaterial()) {
            return Config.miscTreatMyceliumAsPlant && block != null && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
        }
        if (type == XMaterial.INK_SAC.parseMaterial()) {
            Dye dye = (Dye)item.getData();
            if (dye.getColor() == DyeColor.WHITE) {
                if (block != null) {
                    if (CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_BONEMEAL)) {
                        return true;
                    }
                    if (block.getType() == XMaterial.SUNFLOWER.parseMaterial() || block.getType() == XMaterial.TALL_GRASS.parseMaterial()) {
                        Bukkit.getScheduler().runTask((Plugin)main, () -> block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() == 1).filter(it -> ((Item)it).getItemStack().getType() == XMaterial.SUNFLOWER.parseMaterial() || ((Item)it).getItemStack().getType() == XMaterial.TALL_GRASS.parseMaterial()).forEachOrdered(it -> CreativeListener.mark(it, player)));
                    }
                }
            } else if (dye.getColor() == DyeColor.BROWN) {
                return block != null && (block.getType() == XMaterial.BIRCH_LOG.parseMaterial() || block.getType() == XMaterial.BIRCH_WOOD.parseMaterial() || block.getType() == XMaterial.JUNGLE_LOG.parseMaterial() || block.getType() == XMaterial.JUNGLE_WOOD.parseMaterial() || block.getType() == XMaterial.OAK_LOG.parseMaterial() || block.getType() == XMaterial.OAK_WOOD.parseMaterial() || block.getType() == XMaterial.SPRUCE_LOG.parseMaterial() || block.getType() == XMaterial.SPRUCE_WOOD.parseMaterial()) && ((Wood)block.getState().getData()).getSpecies() == TreeSpecies.JUNGLE && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLANT);
            }
            return false;
        }
        return false;
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    void onCreatureSpawn(CreatureSpawnEvent event) {
        EntityType entityType = event.getEntityType();
        if (entityType == EntityType.CHICKEN) {
            if (Config.disableChickenSpawningFromCreativeEggs && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
                WeakReference<Egg> reference = this.eggHit;
                if (reference == null) {
                    return;
                }
                this.eggHit = null;
                Egg egg = (Egg)reference.get();
                if (egg == null || this.isCreative((Entity)egg)) {
                    event.setCancelled(true);
                }
            }
        } else if (entityType == EntityType.ENDERMITE) {
            try {
                if (Config.disableEndermiteSpawningFromCreativeEnderPearl && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.ENDER_PEARL) {
                    EnderPearl pearl = this.pearlCache;
                    if (pearl == null) {
                        return;
                    }
                    if (this.isCreative((Entity)pearl)) {
                        event.setCancelled(true);
                    }
                    this.enderPearlHit = null;
                    this.pearlCache = null;
                }
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    void onProjectileHitHit(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        switch (entity.getType()) {
            case EGG: {
                if (!this.isCreative((Entity)entity)) {
                    this.eggHit = null;
                    return;
                }
                this.eggHit = new WeakReference<Egg>((Egg)entity);
                break;
            }
            case ENDER_PEARL: {
                if (!this.isCreative((Entity)entity)) {
                    return;
                }
                this.enderPearlHit = new WeakReference<EnderPearl>((EnderPearl)entity);
                this.pearlCache = (EnderPearl)entity;
                break;
            }
        }
    }

    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {
        ArrayList<Location> circleBlocks = new ArrayList<Location>();
        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();
        for (int x = bx - radius; x <= bx + radius; ++x) {
            for (int y = by - radius; y <= by + radius; ++y) {
                for (int z = bz - radius; z <= bz + radius; ++z) {
                    double distance = (bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y);
                    if (!(distance < (double)(radius * radius)) || hollow && distance < (double)((radius - 1) * (radius - 1))) continue;
                    Location l = new Location(centerBlock.getWorld(), (double)x, (double)y, (double)z);
                    circleBlocks.add(l);
                }
            }
        }
        return circleBlocks;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onCreativeTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE && main.getConfig().getBoolean("misc.prevent-spectator-teleport")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onShoot(ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        Entity root = this.getOwner((Entity)entity);
        if (!(root instanceof Player)) {
            return;
        }
        Player player = (Player)root;
        if (entity.getType() == EntityType.FISHING_HOOK) {
            if (Config.trackFishingHook) {
                try {
                    PlayerInventory inventory = player.getInventory();
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        if (Stream.of(new ItemStack[]{inventory.getItemInMainHand(), inventory.getItemInOffHand()}).filter(Objects::nonNull).filter(it -> it.getType() == Material.FISHING_ROD).noneMatch(this::isCreative)) {
                            return;
                        }
                    }
                }
                catch (NoSuchMethodError noSuchMethodError) {}
            }
        } else if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }
        try {
            if (Config.trackTrident && entity.getType() == EntityType.TRIDENT && !player.hasPermission("creativesecurity.bypass.use.trident")) {
                event.setCancelled(true);
                Message.TRIDENT_DENY.sendDenial((CommandSender)player, new String[0][]);
            }
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        if (entity instanceof Arrow) {
            if (!Config.trackArrows) {
                return;
            }
            EntityArrow arrow = ((CraftArrow)entity).getHandle();
        	arrow.fromPlayer = PickupStatus.CREATIVE_ONLY;
        }
        switch (event.getEntityType()) {
            case ARROW: 
            case SPECTRAL_ARROW: {
                if (Config.trackArrows && !player.hasPermission("creativesecurity.bypass.mark")) {
                    CreativeListener.mark((Entity)entity, player);
                }
                if (!Config.checkCreativeBowShots || !CreativeListener.check((HumanEntity)((Player)root), PermissionKey.BYPASS_USE_BOW)) break;
                event.setCancelled(true);
                break;
            }
            case SPLASH_POTION: {
                if (Config.trackThrownPotions && !player.hasPermission("creativesecurity.bypass.mark")) {
                    CreativeListener.mark((Entity)entity, player);
                }
                if (!CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_POTION)) break;
                event.setCancelled(true);
                break;
            }
            case THROWN_EXP_BOTTLE: {
                if (Config.trackExpBottle && !player.hasPermission("creativesecurity.bypass.mark")) {
                    CreativeListener.mark((Entity)entity, player);
                }
                if (!this.checkCreative((HumanEntity)((Player)root), PermissionKey.BYPASS_USE_EXPBOTTLE)) break;
                event.setCancelled(true);
                break;
            }
            case EGG: {
                if (Config.trackEgg && !player.hasPermission("creativesecurity.bypass.mark")) {
                    CreativeListener.mark((Entity)entity, player);
                }
                if (!this.checkCreative((HumanEntity)((Player)root), PermissionKey.BYPASS_USE_EGG)) break;
                event.setCancelled(true);
                break;
            }
            case ENDER_SIGNAL: {
                if (Config.trackEnderEye && !player.hasPermission("creativesecurity.bypass.mark")) {
                    CreativeListener.mark((Entity)entity, player);
                }
                if (!this.checkCreative((HumanEntity)((Player)root), PermissionKey.BYPASS_USE_EYEOFENDER)) break;
                event.setCancelled(true);
                break;
            }
            case ENDER_PEARL: {
                if (Config.trackEnderPearl && !player.hasPermission("creativesecurity.bypass.mark")) {
                    CreativeListener.mark((Entity)entity, player);
                }
                if (!this.checkCreative((HumanEntity)((Player)root), PermissionKey.BYPASS_USE_ENDERPEARL, true)) break;
                event.setCancelled(true);
                break;
            }
            case SNOWBALL: {
                if (Config.trackSnowball && !player.hasPermission("creativesecurity.bypass.mark")) {
                    CreativeListener.mark((Entity)entity, player);
                }
                if (!this.checkCreative((HumanEntity)((Player)root), PermissionKey.BYPASS_USE_SNOWBALL)) break;
                event.setCancelled(true);
                break;
            }
            default: {
                if (player.hasPermission("creativesecurity.bypass.mark")) break;
                CreativeListener.mark((Entity)entity, player);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=false)
    void onBlockBreakLowest(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " LOWEST cancelled=" + event.isCancelled() + " type=" + (Object)block.getType());
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=false)
    void onBlockBreakLow(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " LOWEST cancelled=" + event.isCancelled() + " type=" + (Object)block.getType());
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=false)
    void onBlockBreakNormal(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " NORMAL cancelled=" + event.isCancelled() + " type=" + (Object)block.getType());
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=false)
    void onBlockBreakHigh(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " HIGH cancelled=" + event.isCancelled() + " type=" + (Object)block.getType());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    void onBlockBreakHighest2(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " HIGHEST cancelled=" + event.isCancelled() + " type=" + (Object)block.getType());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
    void onBlockBreakMonitor(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " MONITOR cancelled=" + event.isCancelled() + " type=" + (Object)block.getType());
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " actual handler, type=" + (Object)block.getType() + " checkBreakingBedRock=" + Config.checkBreakingBedRock + " checkCreative=" + this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_BREAK_BEDROCK) + " BYPASS_BREAK_BEDROCK=" + player.hasPermission(PermissionKey.BYPASS_BREAK_BEDROCK.key));
        }
        if (Config.checkBreakingBedRock && block.getType() == Material.BEDROCK) {
            if (this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_BREAK_BEDROCK)) {
                event.setCancelled(true);
            }
            return;
        }
        Material blockType = block.getType();
        if (blockType == XMaterial.WHEAT.parseMaterial()) {
            this.checkPlant(block.getState(), 1, EnumSet.of(XMaterial.WHEAT_SEEDS.parseMaterial(), Material.WHEAT));
        } else if (blockType == XMaterial.POTATOES.parseMaterial()) {
            this.checkPlant(block.getState(), 1, EnumSet.of(XMaterial.POTATO.parseMaterial(), Material.POISONOUS_POTATO));
        } else if (blockType == XMaterial.CARROTS.parseMaterial()) {
            this.checkPlant(block.getState(), 1, EnumSet.of(XMaterial.CARROT.parseMaterial()));
        } else if (blockType == Material.MELON_STEM) {
            this.checkPlant(block.getState(), 1, EnumSet.of(Material.MELON_SEEDS));
        } else if (blockType == XMaterial.MELON.parseMaterial()) {
            this.checkPlant(block.getState(), 1, EnumSet.of(XMaterial.MELON_SLICE.parseMaterial()));
        } else if (blockType == Material.PUMPKIN_STEM) {
            this.checkPlant(block.getState(), 1, EnumSet.of(Material.PUMPKIN_SEEDS));
        } else if (blockType == XMaterial.BEETROOT.parseMaterial()) {
            this.checkPlant(block.getState(), 1, EnumSet.of(XMaterial.BEETROOTS.parseMaterial(), Material.BEETROOT_SEEDS));
        } else if (blockType == XMaterial.NETHER_WART_BLOCK.parseMaterial()) {
            this.checkPlant(block.getState(), 1, EnumSet.of(XMaterial.NETHER_WART.parseMaterial()));
        } else if (blockType == XMaterial.CHORUS_PLANT.parseMaterial()) {
            this.checkPlant(block.getState(), 1, EnumSet.of(Material.CHORUS_FRUIT), 1.5);
        } else if (blockType == Material.COCOA) {
            this.checkPlant(block.getState(), 1, EnumSet.of(XMaterial.COCOA_BEANS.parseMaterial()));
        } else if (blockType.toString().equals("MONSTER_EGGS") || blockType.toString().contains("INFESTED_")) {
            this.checkSilverfish(block.getState(), 1);
        } else if (player.getGameMode() == GameMode.CREATIVE && block.getType().toString().contains("SHULKER_BOX")) {
        	Location location = block.getLocation().add(0.5, 0.5, 0.5);
            Bukkit.getScheduler().runTask((Plugin)CreativeSecurityPlugin.getInstance(), () -> {
                World world = location.getWorld();
                Entity drop = world.getNearbyEntities(location, 0.5, 0.5, 0.5).stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() == 1).filter(entity -> ((Item)entity).getItemStack().getType().toString().contains("SHULKER_BOX")).findFirst().orElse(null);
                if (drop != null) {
                    drop.remove();
                }
            });
            return;
        } else if (player.getGameMode() == GameMode.CREATIVE) {
        	return;
        }
        RegionData regionData = CreativeListener.getRegionData(block);
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " actual handler #2, type=" + (Object)block.getType() + " regionData=" + Config.checkBreakingBedRock + " checkCreative(BYPASS_BREAK_BEDROCK)=" + this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_BREAK_BEDROCK));
        }
        if (regionData == null) {
            event.setCancelled(true);
            Message.ERR_CHUNK_DATA_NOT_LOADED.sendError((CommandSender)event.getPlayer());
            if (Config.dataLogEnabled) {
                try {
                    CreativeSecurityPlugin.dataLogger.severe("Chunk data not loaded: " + block.getWorld().getName() + " " + (Object)block.getLocation().toVector());
                    this.forceLoad(block.getWorld(), new RegionPosition(block));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " actual handler #3, type=" + (Object)block.getType() + " blockpos=" + new BlockPosition(block) + " isCreative=" + regionData.isCreative(new BlockPosition(block)) + " mark=" + regionData.getMark(new BlockPosition(block)) + " BYPASS_BREAK_BlOCK=" + player.hasPermission(PermissionKey.BYPASS_BREAK_BlOCK.key) + " check(BYPASS_BREAK_BlOCK)=" + CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_BREAK_BlOCK));
        }
        if (!regionData.isCreative(new BlockPosition(block))) {
            return;
        }
        if (CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_BREAK_BlOCK)) {
        	event.setDropItems(false);
            event.setExpToDrop(0);
        } else {
            event.setExpToDrop(0);
            Location location = block.getLocation().add(0.5, 0.5, 0.5);
            if (blockType == XMaterial.PISTON_HEAD.parseMaterial()) {
                PistonExtensionMaterial extension = (PistonExtensionMaterial)block.getState().getData();
                BlockFace attachedFace = extension.getAttachedFace();
                location.add((double)attachedFace.getModX(), (double)attachedFace.getModY(), (double)attachedFace.getModZ());
            } else if (this.isDoublePlant(blockType) && this.isDoublePlant(block.getRelative(BlockFace.DOWN).getType())) {
                location.add(0.0, -1.0, 0.0);
            }
            Bukkit.getScheduler().runTask((Plugin)main, () -> {
                World world = location.getWorld();
                world.getNearbyEntities(location, 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() == 1).forEachOrdered(drop -> CreativeListener.mark(drop, player));
            });
        }
        if(block.getBlockData()instanceof Waterlogged && ((Waterlogged)block.getBlockData()).isWaterlogged()) {
        	event.getBlock().setType(Material.AIR);
        }
    }

    private boolean isDoublePlant(Material material) {
        return doublePlant.contains((Object)material);
    }

    public static RegionData getRegionData(World world, RegionPosition pos) {
        Map<RegionPosition, RegionData> worldData = hotRegions.get((Object)world);
        if (worldData == null) {
            if (Config.dataLogEnabled) {
                // empty if block
            }
            return null;
        }
        RegionData regionData = worldData.get(pos);
        if (regionData == null) {
            if (Config.dataLogEnabled) {
                // empty if block
            }
            return null;
        }
        return regionData;
    }

    static RegionData getRegionData(Block block) {
        return CreativeListener.getRegionData(block.getWorld(), new RegionPosition(block));
    }

    RegionData getRegionData(Chunk chunk) {
        return CreativeListener.getRegionData(chunk.getWorld(), new RegionPosition(chunk));
    }

    private RegionData markDirty(World world, RegionData data, Consumer<RegionData> task) {
        if (data.isDirty()) {
            task.accept(data);
            return data;
        }
        task.accept(data);
        if (data.isDirty()) {
            RegionPosition pos = new RegionPosition(data.regionX, data.regionZ);
            hotRegions.computeIfAbsent(world, w -> new ConcurrentHashMap()).putIfAbsent(pos, data);
        }
        return data;
    }

    RegionData getRegionData(Player notifier, Block block) {
        RegionData regionData = CreativeListener.getRegionData(block);
        if (regionData == null) {
            if (notifier != null) {
                Message.ERR_CHUNK_DATA_NOT_LOADED.sendError((CommandSender)notifier);
            }
            if (Config.dataLogEnabled) {
                try {
                    CreativeSecurityPlugin.dataLogger.severe("Chunk data not loaded: " + block.getWorld().getName() + " " + (Object)block.getLocation().toVector());
                    this.forceLoad(block.getWorld(), new RegionPosition(block));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        return regionData;
    }

    private boolean mark(Player notifier, Block block, Consumer<RegionData> marker) {
        RegionData regionData = this.getRegionData(notifier, block);
        if (regionData == null) {
            return true;
        }
        this.markDirty(block.getWorld(), regionData, marker);
        return false;
    }

    static UUID getOwnerId(Entity entity) {
        MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)entity, KEY_CREATIVE, null);
        if (metadata == null) {
            return null;
        }
        Object value = metadata.value();
        if (value instanceof UUID) {
            return (UUID)value;
        }
        return new UUID(0L, 0L);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onEntityBlockFrom(EntityBlockFormEvent event) {
        UUID owner = CreativeListener.getOwnerId(event.getEntity());
        Block block = event.getBlock();
        if (this.mark(null, block, r -> {
            if (!Config.trackSnowmanTrail || owner == null || Config.untrackedMaterials.contains((Object)event.getNewState().getType())) {
                r.unmark(block);
            } else {
                r.mark(block, Bukkit.getOfflinePlayer((UUID)owner));
            }
        })) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onEntityModifyBlock(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        EntityType entityType = event.getEntityType();
        if (entityType == EntityType.SHEEP) {
            return;
        }
        if (event.getTo() == Material.AIR) {
            UUID ownerId;
            Material type = block.getType();
            if (type == Material.AIR) {
                return;
            }
            if (type == Material.TNT && this.isCreative(block)) {
                this.checkTnt(block.getState(), 1);
            }
            if (Config.trackSilverfishHatching && (type.toString().equals("MONSTER_EGGS") || type.toString().contains("INFESTED_")) && entityType == EntityType.SILVERFISH && (ownerId = this.getOwnerId(block)) != null) {
                Bukkit.getScheduler().runTask((Plugin)main, () -> block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.SILVERFISH).filter(it -> it.getTicksLived() == 1).findFirst().ifPresent(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)ownerId))));
            }
            if (this.mark(null, block, r -> r.unmark(block))) {
                event.setCancelled(true);
            }
            return;
        }
        UUID owner = CreativeListener.getOwnerId(event.getEntity());
        if (entityType == EntityType.SILVERFISH) {
            if (this.mark(null, block, r -> {
                if (Config.trackSilverfishHiding && owner != null) {
                    r.mark(block, Bukkit.getOfflinePlayer((UUID)owner));
                } else {
                    r.unmark(block);
                }
            })) {
                event.setCancelled(true);
            }
            return;
        }
        if (Config.disableEndermanGettingCreativeBlocks && entityType == EntityType.ENDERMAN && this.isCreative(block)) {
            event.setCancelled(true);
            return;
        }
        if (owner == null) {
            return;
        }
        if (this.mark(null, block, r -> r.mark(block, Bukkit.getOfflinePlayer((UUID)owner)))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onBlockBreakHighest(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean debug = this.logBlockBreak.contains(player.getUniqueId());
        if (debug) {
            CreativeSecurityPlugin.dataLogger.info(player.getName() + " HIGHEST ##unmarker##, type=" + (Object)block.getType() + " regionData=" + Config.checkBreakingBedRock + " checkCreative(BYPASS_BREAK_BEDROCK)=" + this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_BREAK_BEDROCK));
        }
        Material relativeType = event.getBlock().getRelative(BlockFace.UP).getType();
        if (this.mark(event.getPlayer(), block, d -> d.unmark(block))) {
            event.setCancelled(true);
        } else if (relativeType.toString().equals("STANDING_BANNER") || relativeType.toString().contains("_BANNER")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)CreativeListener.main.messages.getString("cantdestroy-onbanner")));
        }
    }

    private void onBlockSpread(Cancellable event, Block from, BlockState to) {
        if (!Config.trackCreativeSpread || to.getLocation().equals((Object)from.getLocation())) {
            return;
        }
        UUID ownerId = this.getOwnerId(from);
        if (ownerId == null) {
            return;
        }
        if (this.mark(null, to.getBlock(), r -> {
            if (Config.untrackedMaterials.contains((Object)from.getType()) || Config.untrackedMaterials.contains((Object)to.getType())) {
                r.unmark(to.getBlock());
            } else {
                r.mark(to.getBlock(), Bukkit.getOfflinePlayer((UUID)ownerId));
            }
        })) {
            event.setCancelled(true);
        }
    }

    private void onMelonLikeGrow(BlockGrowEvent event, Block block, Material stem) {
        List<Block> stems = Arrays.stream(BLOCK_SIDES).map(((Block)block)::getRelative).filter(rel -> rel.getType() == Material.MELON_STEM).collect(Collectors.toList());
        int size = stems.size();
        if (size == 0) {
            this.onBlockSpread((Cancellable)event, block, event.getNewState());
        } else if (size == 1) {
            this.onBlockSpread((Cancellable)event, (Block)stems.get(0), event.getNewState());
        } else {
            Optional<Block> creative = stems.stream().filter(this::isCreative).findFirst();
            if (creative.isPresent()) {
                this.onBlockSpread((Cancellable)event, creative.get(), event.getNewState());
            } else {
                this.onBlockSpread((Cancellable)event, (Block)stems.get(new Random().nextInt(stems.size())), event.getNewState());
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        BlockState newState = event.getNewState();
        Material newStateType = newState.getType();
        if (newStateType == XMaterial.SUGAR_CANE.parseMaterial()) {
            this.onBlockSpread((Cancellable)event, block.getRelative(BlockFace.DOWN), newState);
        } else if (newStateType == XMaterial.MELON.parseMaterial()) {
            this.onMelonLikeGrow(event, block, Material.MELON_STEM);
        } else if (newStateType == Material.PUMPKIN) {
            this.onMelonLikeGrow(event, block, Material.PUMPKIN_STEM);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    void onBlockSpread(BlockSpreadEvent event) {
        Block block = event.getSource();
        BlockState newState = event.getNewState();
        if (newState.getType() == Material.FIRE) {
            return;
        }
        this.onBlockSpread((Cancellable)event, block, newState);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    void onBlockFromTo(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();
        if (to.getLocation().equals((Object)from.getLocation())) {
            System.out.println("equals locations");
            return;
        }
        UUID ownerId = this.getOwnerId(to);
        if (ownerId != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    void onBucketEmpty(PlayerBucketEmptyEvent event) {
        
        Block block = event.getBlock();
        Material bucket = event.getBucket();
        Player player = event.getPlayer();
        ItemStack bucketitem;
        PlayerInventory playerinv = player.getInventory();
        if (playerinv.getItemInMainHand().getType() == bucket) {
        	bucketitem = playerinv.getItemInMainHand();
        } else if (playerinv.getItemInOffHand().getType() == bucket) {
        	bucketitem = playerinv.getItemInOffHand();
        } else {
            throw new IllegalArgumentException(String.format("The player %s emptied a %s without holding it is one of his hands", new Object[]{player.getName(), bucket}));
        }
        if(!bucket.equals(Material.LAVA_BUCKET) && block.getBlockData()instanceof Waterlogged) {
        	if(!((Waterlogged)block.getBlockData()).isWaterlogged() && isCreative(bucketitem)!=isCreative(block)) {
    			event.setCancelled(true);
    			Message.WATERLOGGED_EMPTY.sendDenial(player, new String[0][]);
                return;
    		}
        } else {
        	if (player.getGameMode() == GameMode.CREATIVE && isCreative(bucketitem)) {
        		
        		if (Config.trackCreativeFishBucket && fishBuckets.contains(bucket)) {
                    main.getServer().getScheduler().runTaskLater((Plugin)main, () -> block.getWorld().getNearbyEntities(block.getLocation(), 0.5, 0.5, 0.5).stream().filter(ent -> ent instanceof Fish).filter(it -> it.getTicksLived() == 1).findFirst().ifPresent(entity -> CreativeListener.mark(entity, player)), 1L);
                }
        		
        		if (!Config.untrackedMaterials.contains(bucket)) {
                    this.mark(player, block, regionData -> regionData.mark(block, player));
                }
        	}
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(player.getGameMode()==GameMode.CREATIVE) {
        	if(block.getType().equals(Material.LAVA) || block.getType().equals(Material.WATER)) {
                this.mark(null, block, regionData -> regionData.unmark(block));
        	}
        } else if(isCreative(block)) {
        	if(block.getType().equals(Material.LAVA) || block.getType().equals(Material.WATER)) {
        		Message.LIQUID_CREATIVE.sendDenial(player, new String[0][]);
                event.setCancelled(true);
                this.mark(null, block, regionData -> regionData.unmark(block));
        		block.breakNaturally();
        	} else if(block.getBlockData()instanceof Waterlogged && ((Waterlogged)block.getBlockData()).isWaterlogged()) {
        		Message.LIQUID_CREATIVE.sendDenial(player, new String[0][]);
                event.setCancelled(true);
        		//block.getState().update();
            }
        }
        
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    void onStructureGrow(StructureGrowEvent event) {
        Block block = event.getLocation().getBlock();
        UUID ownerId = this.getOwnerId(block);
        if (ownerId == null) {
            return;
        }
        OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID)ownerId);
        if (this.multiMarkb(event.getPlayer(), event.getBlocks().stream().map(s -> new Pair<BlockLocation, BlockState>(new BlockLocation((BlockState)s), (BlockState)s)), (r, s) -> {
            Block b = s;
            Material bt = b.getType();
            Material st = s.getType();
            if (st == bt) {
                return;
            }
            if ((bt == Material.GRASS || bt == XMaterial.MYCELIUM.parseMaterial()) && st == Material.DIRT) {
                return;
            }
            if (!Config.trackCreativeStructures || Config.untrackedMaterials.contains((Object)s.getType())) {
                r.unmark(b);
            } else {
                r.mark(b, owner);
            }
        })) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onLeaveDecay(LeavesDecayEvent event) {
        Block block = event.getBlock();
        if (this.isCreative(block) && this.mark(null, block, r -> r.unmark(block))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (event.getNewState().getType() == Material.AIR && this.isCreative(block) && this.mark(null, block, r -> r.unmark(block))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (this.isCreative(block) && this.mark(null, block, r -> r.unmark(block))) {
            event.setCancelled(true);
        }
    }

    private RegionData.BlockMark getBlockMark(Block block) {
        RegionData regionData = CreativeListener.getRegionData(block);
        if (regionData == null) {
            return null;
        }
        return regionData.getMark(new BlockPosition(block));
    }

    private UUID getOwnerId(BlockState block) {
        return this.getOwnerId(block.getBlock());
    }

    private UUID getOwnerId(Block block) {
        RegionData.BlockMark blockMark = this.getBlockMark(block);
        if (blockMark == null) {
            return null;
        }
        return blockMark.playerId;
    }

    private void checkTnt(BlockState block, int delay) {
        Material original = block.getType();
        UUID ownerId = this.getOwnerId(block);
        if (ownerId != null) {
            Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                Block b = block.getBlock();
                if (b.getType() != original) {
                    this.mark(null, b, d -> d.unmark(b));
                }
                if (Config.trackTnt) {
                    block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.PRIMED_TNT).filter(it -> it.getTicksLived() == 1).findFirst().ifPresent(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)ownerId)));
                }
            }, (long)delay);
        }
    }

    private void checkGravityBlock(BlockState block, int delay) {
        Material originalBlockType = block.getType();
        UUID ownerId = this.getOwnerId(block);
        if (ownerId != null) {
            main.getServer().getScheduler().runTaskLater((Plugin)main, () -> {
                Block newBlock = block.getBlock();
                Material newBlockType = newBlock.getType();
                if (newBlockType != originalBlockType) {
                    if (!newBlockType.toString().endsWith("_CONCRETE")) {
                        this.mark(null, newBlock, d -> d.unmark(newBlock));
                    }
                } else if (delay < 2) {
                    this.checkGravityBlock(block, 2);
                }
                block.getWorld().getNearbyEntities(block.getLocation(), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.FALLING_BLOCK).filter(it -> it.getTicksLived() <= 2).findFirst().ifPresent(entity -> {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)ownerId);
                    this.mark((Entity)entity, offlinePlayer);
                    new ItemDropTracker((Entity)entity, offlinePlayer, originalBlockType);
                });
            }, (long)delay);
        }
    }

    private void checkVanishing(BlockState block, int delay) {
        Material original = block.getType();
        UUID ownerId = this.getOwnerId(block);
        if (ownerId != null) {
            Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                Block b = block.getBlock();
                if (b.getType() != original) {
                    this.mark(null, b, d -> d.unmark(b));
                }
            }, (long)delay);
        }
    }

    private void checkAttachableBlock(BlockState blockState, long delay) {
        Material originalMaterial = blockState.getType();
        UUID ownerId = this.getOwnerId(blockState);
        if (ownerId != null) {
            Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                Block block = blockState.getBlock();
                if (block.getType() != originalMaterial) {
                    this.mark(null, block, d -> d.unmark(block));
                }
                if (Config.trackAttachedDrops) {
                    blockState.getWorld().getNearbyEntities(blockState.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> (long)it.getTicksLived() <= delay).filter(it -> !this.isCreative((Entity)it)).forEachOrdered(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)ownerId)));
                }
            }, delay);
        }
    }

    private void checkPlant(BlockState block, int delay, EnumSet<Material> drops) {
        this.checkPlant(block, delay, drops, 0.5);
    }

    private void checkPlant(BlockState block, int delay, EnumSet<Material> drops, double radius) {
        Material original = block.getType();
        UUID ownerId = this.getOwnerId(block);
        if (ownerId != null) {
            Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                Block b = block.getBlock();
                if (b.getType() != original) {
                    this.mark(null, b, d -> d.unmark(b));
                }
                if (Config.trackCreativePlantLoot) {
                    block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), radius, radius, radius).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() == 1).filter(it -> drops.contains((Object)((Item)it).getItemStack().getType())).forEachOrdered(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)ownerId)));
                }
            }, (long)delay);
        }
    }

    private void checkSilverfish(BlockState block, int delay) {
        Material original = block.getType();
        UUID ownerId = this.getOwnerId(block);
        if (ownerId != null) {
            Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                Block b = block.getBlock();
                if (b.getType() != original) {
                    this.mark(null, b, d -> d.unmark(b));
                }
                if (Config.trackCreativePlantLoot) {
                    block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.SILVERFISH).filter(it -> it.getTicksLived() == 1).forEachOrdered(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)ownerId)));
                }
            }, (long)delay);
        }
    }

    private void checkPhysics(BlockState block, int delay) {
        String typeStr;
        Material type = block.getType();
        if (type.hasGravity()) {
            this.checkGravityBlock(block, delay);
            return;
        }
        switch (type) {
            case TNT: {
                this.checkTnt(block, delay);
                return;
            }
            case MELON_STEM: {
                this.checkPlant(block, delay, EnumSet.of(Material.MELON_SEEDS));
                return;
            }
            case PUMPKIN_STEM: {
                this.checkPlant(block, delay, EnumSet.of(Material.PUMPKIN_SEEDS));
                return;
            }
            case COCOA: {
                this.checkPlant(block, delay, EnumSet.of(XMaterial.COCOA_BEANS.parseMaterial()));
                return;
            }
            case NETHER_WART: {
                this.checkPlant(block, delay, EnumSet.of(XMaterial.NETHER_WART.parseMaterial()));
                return;
            }
            case LEVER: 
            case TORCH: 
            case ACACIA_DOOR: 
            case BIRCH_DOOR: 
            case IRON_DOOR: 
            case JUNGLE_DOOR: 
            case SPRUCE_DOOR: 
            case LADDER: 
            case REDSTONE_WIRE: 
            case RED_MUSHROOM: 
            case BROWN_MUSHROOM: 
            case ACTIVATOR_RAIL: 
            case DETECTOR_RAIL: 
            case POWERED_RAIL: 
            case TRIPWIRE: 
            case TRIPWIRE_HOOK: 
            case FLOWER_POT: 
            case CACTUS: 
            case PUMPKIN: 
            case JACK_O_LANTERN: 
            case DAYLIGHT_DETECTOR: {
                this.checkAttachableBlock(block, delay);
                return;
            }
        }
        switch (typeStr = type.toString()) {
            case "CHORUS_PLANT": {
                UUID ownerId = this.getOwnerId(block);
                if (ownerId != null) {
                    Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                        Block b = block.getBlock();
                        if (b.getType() != type) {
                            this.mark(null, b, d -> d.unmark(b));
                        }
                        if (Config.trackCreativePlantLoot) {
                            block.getWorld().getNearbyEntities(block.getLocation(), 1.5, 1.5, 1.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() <= delay + 3).filter(it -> ((Item)it).getItemStack().getType() == Material.CHORUS_FRUIT).forEachOrdered(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)ownerId)));
                        }
                    }, (long)(delay + 3));
                }
                return;
            }
            case "CHORUS_FLOWER": {
                if (this.getOwnerId(block) != null) {
                    Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                        Block b = block.getBlock();
                        Material current = b.getType();
                        if (current != Material.CHORUS_FLOWER && current != Material.CHORUS_PLANT) {
                            this.mark(null, b, d -> d.unmark(b));
                        }
                    }, (long)delay);
                }
                return;
            }
            case "SAPLING": 
            case "REDSTONE_TORCH_OFF": 
            case "REDSTONE_TORCH_ON": 
            case "CARPET": 
            case "BANNER": 
            case "TRAP_DOOR": 
            case "REDSTONE_COMPARATOR_OFF": 
            case "REDSTONE_COMPARATOR_ON": 
            case "DIODE_BLOCK_OFF": 
            case "DIODE_BLOCK_ON": 
            case "FERN": {
                this.checkAttachableBlock(block, delay);
                return;
            }
        }
        XMaterial xMaterial = XMaterial.fromMaterial(type);
        if (xMaterial != null) {
            switch (xMaterial) {
                case POTATOES: {
                    this.checkPlant(block, delay, EnumSet.of(XMaterial.POTATO.parseMaterial(), Material.POISONOUS_POTATO));
                    return;
                }
                case CARROTS: {
                    this.checkPlant(block, delay, EnumSet.of(XMaterial.CARROT.parseMaterial()));
                    return;
                }
                case MELON: {
                    this.checkPlant(block, delay, EnumSet.of(Material.MELON));
                    return;
                }
                case BEETROOTS: {
                    this.checkPlant(block, delay, EnumSet.of(Material.BEETROOT, Material.BEETROOT_SEEDS));
                    return;
                }
                case KELP_PLANT: {
                    UUID ownerId = this.getOwnerId(block);
                    if (ownerId != null) {
                        Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                            Block b = block.getBlock();
                            if (b.getType() != type) {
                                this.mark(null, b, d -> d.unmark(b));
                            }
                            block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 1.5, 1.5, 1.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).peek(System.out::println).filter(it -> it.getTicksLived() == 1).filter(it -> ((Item)it).getItemStack().getType() == Material.KELP).forEachOrdered(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)ownerId)));
                        }, (long)delay);
                    }
                    return;
                }
                case RAIL: 
                case REDSTONE_TORCH: 
                case REDSTONE_WALL_TORCH: 
                case WALL_TORCH: 
                case COMPARATOR: 
                case REPEATER: 
                case DANDELION: 
                case LIGHT_WEIGHTED_PRESSURE_PLATE: 
                case HEAVY_WEIGHTED_PRESSURE_PLATE: 
                case STONE_PRESSURE_PLATE: 
                case SUGAR_CANE: 
                case LILY_PAD: 
                case OAK_DOOR: 
                case COBWEB: 
                case SEA_PICKLE: {
                    this.checkAttachableBlock(block, delay);
                    return;
                }
            }
        }
        if (typeStr.contains("_SAPLING") || typeStr.contains("SIGN") || typeStr.contains("_TRAPDOOR") || typeStr.contains("_CARPET") || typeStr.contains("BUTTON") || typeStr.contains("_BANNER") || this.isRedRose(type) || this.isWoodPlate(type) || this.isDoublePlant(type)) {
            this.checkAttachableBlock(block, delay);
            return;
        }
        if (type == XMaterial.WHEAT.parseMaterial()) {
            this.checkPlant(block, delay, EnumSet.of(XMaterial.WHEAT_SEEDS.parseMaterial(), Material.WHEAT));
        }
    }

    private boolean isRedRose(Material type) {
        return redRoses.contains((Object)type);
    }

    private boolean isWoodPlate(Material type) {
        return woodenPlates.contains((Object)type);
    }

    private void checkPistonPhysics(BlockState block, int delay) {
        Material type = block.getType();
        if (type == XMaterial.SKELETON_SKULL.parseMaterial() || type == XMaterial.WITHER_SKELETON_SKULL.parseMaterial() || type.toString().contains("_HEAD") || type == Material.PUMPKIN || type == XMaterial.CARVED_PUMPKIN.parseMaterial() || type == Material.STRING) {
            this.checkAttachableBlock(block, delay);
        } else {
            this.checkPhysics(block, delay);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onFish(PlayerFishEvent event) {
        try {
            if (Config.trackFish && event.getState() == PlayerFishEvent.State.CAUGHT_FISH && this.isCreative((Entity)event.getHook())) {
                CreativeListener.mark(event.getCaught(), event.getPlayer());
            }
        }
        catch (NoSuchMethodError noSuchMethodError) {
            // empty catch block
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onBlockPhysics(BlockPhysicsEvent event) {
        if (!this.disableBlockPhysicsEventChecks) {
            this.checkPhysics(event.getBlock().getState(), 1);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onBlockExplode(BlockExplodeEvent event) {
        this.onExplode((Cancellable)event, event.blockList());
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onEntityExplode(EntityExplodeEvent event) {
        this.onExplode((Cancellable)event, event.blockList());
    }

    private void onExplode(Cancellable event, List<Block> blocks) {
        HashMap<Block,RegionData.BlockMark> creativeBlocks = new HashMap(blocks.size());
        blocks.forEach(block -> {
            RegionData.BlockMark mark = this.getBlockMark((Block)block);
            if (mark == null) {
                return;
            }
            creativeBlocks.put(block, mark);
        });
        Function<Block, Pair> pairCreator = b -> new Pair<BlockLocation, Block>(new BlockLocation((Block)b), (Block)b);
        if (this.multiMark(null, blocks.stream().map(pairCreator), RegionData::unmark)) {
            event.setCancelled(true);
        } else {
            Bukkit.getScheduler().runTask((Plugin)main, () -> {
                if (!event.isCancelled()) {
                    creativeBlocks.forEach((block, mark) -> block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM || it.getType() == EntityType.SILVERFISH).filter(it -> it.getTicksLived() == 1).forEach(entity -> this.mark((Entity)entity, Bukkit.getOfflinePlayer((UUID)mark.playerId))));
                }
            });
        }
    }

    private boolean multiMarkb(Player notifier, Stream<Pair<BlockLocation, BlockState>> stream, BiConsumer<RegionData, Block> marker) {
    	Map<Pair, List<Pair>> regions = stream.collect(Collectors.groupingBy(pair -> new Pair<World, RegionPosition>(((BlockLocation)pair.getFirst()).getWorld(), new RegionPosition((BlockLocation)pair.getFirst()))));
        Map<Pair,RegionData> regionsToData = regions.keySet().stream().collect(Collectors.toMap(Function.identity(), pair -> CreativeListener.getRegionData((World)pair.getFirst(), (RegionPosition)pair.getSecond())));
        if (regionsToData.values().stream().anyMatch(Objects::isNull)) {
            if (notifier != null) {
                Message.ERR_CHUNK_DATA_NOT_LOADED.sendError((CommandSender)notifier);
            }
            if (Config.dataLogEnabled) {
                try {
                    Pair key = (Pair)regionsToData.entrySet().stream().filter(e -> e.getValue() == null).findFirst().get().getKey();
                    CreativeSecurityPlugin.dataLogger.severe("Chunk data not loaded: " + ((World)key.getFirst()).getName() + " " + key.getSecond());
                    this.forceLoad((World)key.getFirst(), (RegionPosition)key.getSecond());
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return true;
        }
        regions.forEach((regPos, blocks) -> {
            RegionData data = regionsToData.get(regPos);
            blocks.forEach(block -> this.markDirty((World)regPos.getFirst(), data, d -> marker.accept((RegionData)d, (Block) block.getSecond())));
        });
        return false;
	}


	private boolean multiMark(Player notifier, Stream<Pair<BlockLocation, Block>> stream, BiConsumer<RegionData, Block> marker) {
        Map<Pair, List<Pair>> regions = stream.collect(Collectors.groupingBy(pair -> new Pair<World, RegionPosition>(((BlockLocation)pair.getFirst()).getWorld(), new RegionPosition((BlockLocation)pair.getFirst()))));
        Map<Pair,RegionData> regionsToData = regions.keySet().stream().collect(Collectors.toMap(Function.identity(), pair -> CreativeListener.getRegionData((World)pair.getFirst(), (RegionPosition)pair.getSecond())));
        if (regionsToData.values().stream().anyMatch(Objects::isNull)) {
            if (notifier != null) {
                Message.ERR_CHUNK_DATA_NOT_LOADED.sendError((CommandSender)notifier);
            }
            if (Config.dataLogEnabled) {
                try {
                    Pair key = (Pair)regionsToData.entrySet().stream().filter(e -> e.getValue() == null).findFirst().get().getKey();
                    CreativeSecurityPlugin.dataLogger.severe("Chunk data not loaded: " + ((World)key.getFirst()).getName() + " " + key.getSecond());
                    this.forceLoad((World)key.getFirst(), (RegionPosition)key.getSecond());
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return true;
        }
        regions.forEach((regPos, blocks) -> {
            RegionData data = regionsToData.get(regPos);
            blocks.forEach(block -> this.markDirty((World)regPos.getFirst(), data, d -> marker.accept((RegionData)d, (Block) block.getSecond())));
        });
        return false;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onPlaceBannedBlock(BlockPlaceEvent event) {
        if (!Config.checkMobSpawnerPlacement) {
            return;
        }
        Player player = event.getPlayer();
        Material type = event.getBlock().getType();
        if (type == XMaterial.SPAWNER.parseMaterial()) {
            if (this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLACEMOBSPAWNER)) {
                event.setCancelled(true);
            }
        } else if ((type.toString().equals("MONSTER_EGGS") || type.toString().contains("INFESTED_")) && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_PLACESILVERFISHBLOCK)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    void onBlockPlaceOnItemFrame(BlockPlaceEvent event) {
        Block blockPlaced;
        Location blockLoc;
        Player player = event.getPlayer();
        if (!player.hasPermission("creativesecurity.bypass.placeonframes") && (blockLoc = (blockPlaced = event.getBlockPlaced()).getLocation()).getWorld().getNearbyEntities(blockLoc, 1.0, 1.0, 1.0).stream().filter(entity -> entity instanceof ItemFrame).map(entity -> (ItemFrame)entity).anyMatch(frame -> frame.getLocation().getBlock().equals((Object)blockPlaced))) {
            event.setCancelled(true);
            Message.ITEMFRAME_ATTACHED.sendDenial((CommandSender)player, new String[0][]);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (blockPlacingCooldownEnabled && !player.hasPermission("creativesecurity.bypass.placecooldown")) {
            if (this.blockPlacingCooldownPlayersSet.contains(playerName)) {
                event.setCancelled(true);
                return;
            }
            this.blockPlacingCooldownPlayersSet.add(playerName);
            main.getServer().getScheduler().runTaskLater((Plugin)main, () -> this.blockPlacingCooldownPlayersSet.remove(playerName), (long)blockPlacingCooldown);
        }
        if (!Config.trackPlaceCreative) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() == Material.FIRE) {
            return;
        }
        if (player.getGameMode() != GameMode.CREATIVE || !this.isCreative(event.getItemInHand()) || player.hasPermission("creativesecurity.bypass.placemark")) {
            BlockState blockReplacedState = event.getBlockReplacedState();
            Material blockReplacedType = event.getBlockReplacedState().getType();
            if (blockReplacedType == Material.WATER || blockReplacedType == Material.LAVA) {
                Block blockReplaced = blockReplacedState.getBlock();
                if (this.isCreative(block)) {
                    this.mark(player, block, regionData -> regionData.unmark(blockReplaced));
                }
            }
            return;
        }
        boolean untrackedMaterials = Config.untrackedMaterials.contains((Object)block.getType());
        if (this.mark(player, block, d -> {
            if (!untrackedMaterials) {
                d.mark(block, (OfflinePlayer)player);
            } else {
                d.unmark(block);
            }
        })) {
            event.setCancelled(true);
            return;
        }
        if (!untrackedMaterials) {
            this.checkPhysics(event.getBlock().getState(), 2);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onMultiPlace(BlockMultiPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && !this.isCreative(event.getItemInHand())) {
            return;
        }
        if (this.multiMark(player, event.getReplacedBlockStates().stream().map(s -> new Pair<BlockLocation, Block>(new BlockLocation((BlockState)s), s.getBlock())), (data, block) -> {
            Material type = block.getType();
            if (Config.untrackedMaterials.contains((Object)type)) {
                data.unmark((Block)block);
            } else {
                data.mark((Block)block, (OfflinePlayer)player);
            }
        })) {
            event.setCancelled(true);
        } else {
            event.getReplacedBlockStates().stream().map(it -> it.getWorld().getBlockAt(it.getLocation()).getState()).forEach(state -> this.checkPhysics((BlockState)state, 2));
        }
    }

    private boolean onPistonMove(Cancellable event, BlockFace direction, List<Block> blocks) {
        Comparator<Block> comparator;
        if (blocks.stream().peek(b -> this.checkPistonPhysics(b.getState(), 1)).map(Block::getType).anyMatch(it -> Config.immovableBlocks.contains(it))) {
            event.setCancelled(true);
            return true;
        }
        switch (direction) {
            case NORTH: {
                comparator = Comparator.comparingInt(Block::getZ).thenComparing(Block::getY).thenComparing(Block::getX);
                break;
            }
            case SOUTH: {
                comparator = Comparator.comparingInt(Block::getZ).reversed().thenComparing(Block::getY).thenComparing(Block::getX);
                break;
            }
            case EAST: {
                comparator = Comparator.comparingInt(Block::getX).reversed().thenComparing(Block::getY).thenComparing(Block::getZ);
                break;
            }
            case WEST: {
                comparator = Comparator.comparingInt(Block::getX).thenComparing(Block::getY).thenComparing(Block::getZ);
                break;
            }
            case UP: {
                comparator = Comparator.comparingInt(Block::getY).reversed().thenComparing(Block::getX).thenComparing(Block::getZ);
                break;
            }
            case DOWN: {
                comparator = Comparator.comparingInt(Block::getY).thenComparing(Block::getX).thenComparing(Block::getZ);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unexpected direction: " + (Object)direction);
            }
        }
        if (this.multiMark(null, blocks.stream().sorted(comparator).map(b -> new Pair<BlockLocation, Block>(new BlockLocation((Block)b), (Block)b)), (r, b) -> {
            BlockPosition fromPos = new BlockPosition((Block)b);
            BlockPosition toPos = new BlockPosition(b.getX() + direction.getModX(), b.getY() + direction.getModY(), b.getZ() + direction.getModZ());
            RegionData.BlockMark mark = r.getMark(fromPos);
            if (mark != null) {
                r.setMark(toPos, mark);
                r.unmark(fromPos);
            } else {
                r.unmark(toPos);
            }
        })) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onPiston(BlockPistonExtendEvent event) {
        if (this.onPistonMove((Cancellable)event, event.getDirection(), event.getBlocks())) {
            return;
        }
        Block block = event.getBlock();
        RegionData.BlockMark pistonMark = this.getBlockMark(block);
        if (pistonMark != null) {
            Block moving = block.getRelative(event.getDirection());
            CreativeListener.getRegionData(moving).setMark(new BlockPosition(moving), pistonMark);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onPiston(BlockPistonRetractEvent event) {
        this.onPistonMove((Cancellable)event, event.getDirection(), event.getBlocks());
    }

    @SafeVarargs
    private final Map<BlockFace, Map<BlockFace, Set<Block>>> scanBlockPattern(Block first, BlockState placed, int lineAdjust, List<Predicate<BlockState>> ... pattern) {
        World world = first.getWorld();
        EnumMap<BlockFace, Map<BlockFace, Set<Block>>> results = new EnumMap<BlockFace, Map<BlockFace, Set<Block>>>(BlockFace.class);
        for (BlockFace vertical : new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            EnumMap subResults = new EnumMap(BlockFace.class);
            block45: for (BlockFace horizon : new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                if (horizon == vertical || horizon.getOppositeFace() == vertical) continue;
                HashSet<Block> matches = new HashSet<Block>();
                boolean single = pattern.length == 1;
                for (int line = 0; line < pattern.length; ++line) {
                    List<Predicate<BlockState>> predicates = pattern[line];
                    for (int colRaw = 0; colRaw < predicates.size(); ++colRaw) {
                        BlockState state;
                        int col = lineAdjust + colRaw;
                        Predicate<BlockState> condition = predicates.get(colRaw);
                        int offsetX = 0;
                        int offsetY = 0;
                        int offsetZ = 0;
                        block0 : switch (vertical) {
                            case DOWN: {
                                offsetY = -1 * line;
                                switch (horizon) {
                                    case NORTH: {
                                        offsetZ = -1 * col;
                                        break block0;
                                    }
                                    case SOUTH: {
                                        offsetZ = 1 * col;
                                        break block0;
                                    }
                                    case WEST: {
                                        offsetX = -1 * col;
                                        break block0;
                                    }
                                    case EAST: {
                                        offsetX = 1 * col;
                                        break block0;
                                    }
                                }
                                throw new IllegalStateException();
                            }
                            case UP: {
                                offsetY = 1 * line;
                                switch (horizon) {
                                    case NORTH: {
                                        offsetZ = -1 * col;
                                        break block0;
                                    }
                                    case SOUTH: {
                                        offsetZ = 1 * col;
                                        break block0;
                                    }
                                    case WEST: {
                                        offsetX = -1 * col;
                                        break block0;
                                    }
                                    case EAST: {
                                        offsetX = 1 * col;
                                        break block0;
                                    }
                                }
                                throw new IllegalStateException();
                            }
                            case NORTH: {
                                offsetZ = -1 * line;
                                switch (horizon) {
                                    case DOWN: {
                                        offsetY = -1 * col;
                                        break block0;
                                    }
                                    case UP: {
                                        offsetY = 1 * col;
                                        break block0;
                                    }
                                    case WEST: {
                                        offsetX = -1 * col;
                                        break block0;
                                    }
                                    case EAST: {
                                        offsetX = 1 * col;
                                        break block0;
                                    }
                                }
                                throw new IllegalStateException();
                            }
                            case SOUTH: {
                                offsetZ = 1 * line;
                                switch (horizon) {
                                    case DOWN: {
                                        offsetY = -1 * col;
                                        break block0;
                                    }
                                    case UP: {
                                        offsetY = 1 * col;
                                        break block0;
                                    }
                                    case WEST: {
                                        offsetX = -1 * col;
                                        break block0;
                                    }
                                    case EAST: {
                                        offsetX = 1 * col;
                                        break block0;
                                    }
                                }
                                throw new IllegalStateException();
                            }
                            case WEST: {
                                offsetX = 1 * line;
                                switch (horizon) {
                                    case DOWN: {
                                        offsetY = -1 * col;
                                        break block0;
                                    }
                                    case UP: {
                                        offsetY = 1 * col;
                                        break block0;
                                    }
                                    case NORTH: {
                                        offsetZ = -1 * col;
                                        break block0;
                                    }
                                    case SOUTH: {
                                        offsetZ = 1 * col;
                                        break block0;
                                    }
                                }
                                throw new IllegalStateException();
                            }
                            case EAST: {
                                offsetX = -1 * line;
                                switch (horizon) {
                                    case DOWN: {
                                        offsetY = -1 * col;
                                        break block0;
                                    }
                                    case UP: {
                                        offsetY = 1 * col;
                                        break block0;
                                    }
                                    case NORTH: {
                                        offsetZ = -1 * col;
                                        break block0;
                                    }
                                    case SOUTH: {
                                        offsetZ = 1 * col;
                                        break block0;
                                    }
                                }
                                throw new IllegalStateException();
                            }
                            default: {
                                throw new IllegalStateException();
                            }
                        }
                        int x = first.getX() + offsetX;
                        int y = first.getY() + offsetY;
                        int z = first.getZ() + offsetZ;
                        if (x == placed.getX() && y == placed.getY() && z == placed.getZ()) {
                            state = placed;
                        } else {
                            state = world.getBlockAt(x, y, z).getState();
                            if (!condition.test(state)) {
                                if (!single) continue block45;
                                break block45;
                            }
                        }
                        matches.add(state.getBlock());
                    }
                }
                if (single) {
                    Stream.of(new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}).filter(it -> it != vertical && it != vertical.getOppositeFace()).forEachOrdered(h -> subResults.put((BlockFace)h, matches));
                    break;
                }
                subResults.put(horizon, matches);
            }
            if (subResults.isEmpty()) continue;
            results.put(vertical, subResults);
        }
        return results;
    }

    private Map<BlockFace, Map<BlockFace, Set<Block>>> scanPossibleSnowmanForms(BlockState pumpkinHead) {
        Predicate<BlockState> snow = block -> block.getType() == Material.SNOW_BLOCK;
        Predicate<BlockState> pumpkin = block -> block.getType() == Material.PUMPKIN;
        return this.scanBlockPattern(pumpkinHead.getBlock(), pumpkinHead, 0, Collections.singletonList(pumpkin), Collections.singletonList(snow), Collections.singletonList(snow));
    }

    private Map<BlockFace, Map<BlockFace, Set<Block>>> scanPossibleIronGolemForms(BlockState pumpkinHead) {
        Predicate<BlockState> air = block -> block.getType() == Material.AIR;
        Predicate<BlockState> pumpkin = block -> block.getType() == Material.PUMPKIN;
        Predicate<BlockState> iron = block -> block.getType() == Material.IRON_BLOCK;
        return this.scanBlockPattern(pumpkinHead.getBlock(), pumpkinHead, -1, Arrays.asList(air, pumpkin, air), Arrays.asList(iron, iron, iron), Arrays.asList(air, iron, air));
    }

    @SafeVarargs
    private final Map<BlockFace, Map<BlockFace, Set<Block>>> scanBlockWitherPattern(BlockState placed, List<Predicate<BlockState>> ... pattern) {
        Block block = placed.getBlock();
        return Stream.concat(Stream.of(block), Stream.of(new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}).flatMap(face -> Stream.of(new Block[]{block.getRelative(face, -2), block.getRelative(face, -1), block.getRelative(face, 1), block.getRelative(face, 2)}))).map(first -> this.scanBlockPattern((Block)first, placed, -1, pattern)).reduce(CreativeListener::combine).orElseGet(() -> new EnumMap(BlockFace.class));
    }

    private static Map<BlockFace, Map<BlockFace, Set<Block>>> combine(Map<BlockFace, Map<BlockFace, Set<Block>>> a, Map<BlockFace, Map<BlockFace, Set<Block>>> b) {
        b.forEach((vertical, bMap) -> a.compute((BlockFace)vertical, (av, aMap) -> {
            if (aMap == null) {
                aMap = new EnumMap(BlockFace.class);
            }
            Map<BlockFace, Set<Block>> aMap1 = aMap;
            bMap.forEach((horizon, bSet) -> aMap1.compute((BlockFace)horizon, (ah, aSet) -> {
                if (aSet == null) {
                    aSet = new LinkedHashSet();
                }
                aSet.addAll(bSet);
                return aSet;
            }));
            return aMap;
        }));
        return a;
    }

    private Map<BlockFace, Map<BlockFace, Set<Block>>> scanPossiblePumpkinHead(BlockState placedPumpkin) {
        return Stream.of(this.scanPossibleSnowmanForms(placedPumpkin), this.scanPossibleIronGolemForms(placedPumpkin)).reduce(CreativeListener::combine).orElseGet(() -> new EnumMap(BlockFace.class));
    }

    private Map<BlockFace, Map<BlockFace, Set<Block>>> scanPossibleWitherForms(BlockState placedSkull) {
        Predicate<BlockState> air = block -> block.getType() == Material.AIR;
        Predicate<BlockState> skull = block -> {
            Material type = block.getType();
            return type == XMaterial.WITHER_SKELETON_SKULL.parseMaterial();
        };
        Predicate<BlockState> soul = block -> block.getType() == Material.SOUL_SAND;
        return this.scanBlockWitherPattern(placedSkull, Arrays.asList(skull, skull, skull), Arrays.asList(soul, soul, soul), Arrays.asList(air, soul, air));
    }

    private boolean anyCreative(Map<BlockFace, Map<BlockFace, Set<Block>>> possibility) {
        return possibility.values().stream().flatMap(sub -> sub.values().stream().flatMap(Collection::stream)).distinct().anyMatch(this::isCreative);
    }

    private Stream<Block> getBlockStream(Map<BlockFace, Map<BlockFace, Set<Block>>> possible) {
        return possible.values().stream().flatMap(sub -> sub.values().stream().flatMap(Collection::stream));
    }

    private Lazy<List<Block>> getBlocks(Map<BlockFace, Map<BlockFace, Set<Block>>> possible) {
        return new Lazy<List<Block>>(() -> this.getBlockStream(possible).distinct().collect(Collectors.toList()));
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onFormEntity(BlockPlaceEvent event) {
        if (!Config.trackEntityForming) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();
        if (type == Material.PUMPKIN || type == Material.WITHER_SKELETON_SKULL) {
            boolean creative = player.getGameMode() == GameMode.CREATIVE || this.isCreative(event.getItemInHand());
            boolean pumpkin = type == Material.PUMPKIN;
            Map<BlockFace, Map<BlockFace, Set<Block>>> possible = pumpkin ? this.scanPossiblePumpkinHead(block.getState()) : (block.getWorld().getDifficulty() != Difficulty.PEACEFUL ? this.scanPossibleWitherForms(block.getState()) : Collections.emptyMap());
            if (!possible.isEmpty()) {
                Lazy<List<Block>> blocks = this.getBlocks(possible);
                if (creative || blocks.get().stream().anyMatch(this::isCreative)) {
                    boolean cancel = false;
                    if (CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_CREATEMOB)) {
                        event.setCancelled(true);
                        cancel = true;
                    }
                    this.trackEntityFormedFromBlocks(cancel ? null : player, (OfflinePlayer)player, block, pumpkin, blocks);
                }
            }
        }
    }

    private void trackEntityFormedFromBlocks(Player notifier, OfflinePlayer owner, Block head, boolean pumpkin, Lazy<List<Block>> blocks) {
        Objects.requireNonNull(owner, "owner was null");
        HashMap regions = new HashMap(blocks.get().size());
        Stream stream = blocks.get().stream().map(it -> new Pair<BlockLocation, Block>(new BlockLocation((Block)it), (Block)it));
        this.multiMark(notifier, stream, (r, b) -> regions.put(b, r));
        Bukkit.getScheduler().runTask((Plugin)main, () -> {
            head.getWorld().getNearbyEntities(head.getLocation().add(0.5, 0.5, 0.5), 2.0, 2.0, 2.0).stream().filter(it -> it.getTicksLived() == 1).filter(it -> {
                switch (it.getType()) {
                    case SNOWMAN: 
                    case IRON_GOLEM: {
                        return pumpkin;
                    }
                    case WITHER: {
                        return !pumpkin;
                    }
                }
                return false;
            }).findFirst().ifPresent(entity -> this.mark((Entity)entity, owner));
            blocks.get().stream().filter(it -> it.getType() == Material.AIR).forEach(b -> {
                BlockPosition pos;
                RegionData region = (RegionData)regions.get(b);
                if (region.isCreative(pos = new BlockPosition((Block)b))) {
                    region.unmark(pos);
                }
            });
        });
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    void onSnowmanPlaceSnow(EntityBlockFormEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SNOWMAN && entity.getTicksLived() < 3) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onVehicleDestroyed(VehicleDestroyEvent event) {
        Entity root = this.getOwner(event.getAttacker());
        if (!(root instanceof Player)) {
            return;
        }
        Player player = (Player)root;
        if (player.getGameMode() != GameMode.CREATIVE && this.isCreative((Entity)event.getVehicle()) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_BREAK_VEHICLE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onVehicleDestroyedMonitor(VehicleDestroyEvent event) {
        UUID ownerId = CreativeListener.getOwnerId((Entity)event.getVehicle());
        if (ownerId == null) {
            return;
        }
        event.getVehicle().setCustomName("");
        EntityType type = event.getVehicle().getType();
        Location location = event.getVehicle().getLocation();
        Bukkit.getScheduler().runTask((Plugin)main, () -> {
            World world = location.getWorld();
            world.getNearbyEntities(location, 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() == 1).filter(it -> {
                Material itemType = ((Item)it).getItemStack().getType();
                switch (type) {
                    case BOAT: {
                        return itemType == XMaterial.ACACIA_BOAT.parseMaterial() || itemType == XMaterial.BIRCH_BOAT.parseMaterial() || itemType == XMaterial.DARK_OAK_BOAT.parseMaterial() || itemType == XMaterial.JUNGLE_BOAT.parseMaterial() || itemType == XMaterial.OAK_BOAT.parseMaterial() || itemType == XMaterial.SPRUCE_BOAT.parseMaterial();
                    }
                    case MINECART: 
                    case MINECART_COMMAND: 
                    case MINECART_MOB_SPAWNER: {
                        return itemType == Material.MINECART;
                    }
                    case MINECART_CHEST: {
                        return itemType == Material.MINECART || itemType == Material.CHEST;
                    }
                    case MINECART_TNT: {
                        return itemType == Material.MINECART || itemType == Material.TNT;
                    }
                    case MINECART_FURNACE: {
                        return itemType == Material.MINECART || itemType == Material.FURNACE;
                    }
                    case MINECART_HOPPER: {
                        return itemType == Material.MINECART || itemType == Material.HOPPER;
                    }
                }
                return false;
            }).forEach(item -> this.mark((Entity)item, Bukkit.getOfflinePlayer((UUID)ownerId)));
        });
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onHangingBreak(HangingBreakByEntityEvent event) {
        boolean creative;
        Entity root = this.getOwner(event.getRemover());
        Hanging entity = event.getEntity();
        UUID owner = CreativeListener.getOwnerId((Entity)entity);
        boolean bl = creative = owner != null;
        if (entity.getType() == EntityType.ITEM_FRAME) {
            if (root instanceof Player) {
                Player player = (Player)root;
                if (player.getGameMode() == GameMode.CREATIVE) {
                    if (!this.isCreative((Entity)entity) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_BREAK_HANGINGSURVIVAL)) {
                        event.setCancelled(true);
                    }
                } else if (this.isCreative((Entity)entity) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_BREAK_HANGING)) {
                    event.setCancelled(true);
                }
            }
        } else if (root instanceof Player) {
            Player player = (Player)root;
            if (player.getGameMode() != GameMode.CREATIVE && this.isCreative((Entity)entity) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_BREAK_HANGING)) {
                event.setCancelled(true);
            }
        } else if (creative) {
            event.setCancelled(true);
        }
        if (creative) {
            Bukkit.getScheduler().runTask((Plugin)main, () -> entity.getWorld().getNearbyEntities(entity.getLocation(), 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() == 1).findFirst().ifPresent(it -> this.mark((Entity)it, Bukkit.getOfflinePlayer((UUID)owner))));
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onHangingBreak(HangingBreakEvent event) {
        if (Config.disableCreativeHangingFromBreaking && event.getCause() != HangingBreakEvent.RemoveCause.ENTITY && this.isCreative((Entity)event.getEntity())) {
            event.setCancelled(true);
        }
    }

    private Entity getOwner(Entity entity) {
        ProjectileSource shooter;
        if (entity instanceof Projectile && (shooter = ((Projectile)entity).getShooter()) instanceof Entity) {
            return this.getOwner((Entity)shooter);
        }
        return entity;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    void onItemFrameDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ITEM_FRAME) {
            return;
        }
        ItemFrame frame = (ItemFrame)event.getEntity();
        UUID creator = CreativeListener.getOwnerId((Entity)frame);
        if (creator == null) {
            return;
        }
        ItemStack item = frame.getItem();
        if (item != null) {
            if (item.getType() == Config.miscEmptyCreativeItemFrameMarker && Optional.ofNullable(item.getItemMeta()).map(ItemMeta::getDisplayName).map(it -> it.startsWith(CREATIVE_MARK)).orElse(false).booleanValue()) {
                HangingBreakByEntityEvent breakEvent = new HangingBreakByEntityEvent((Hanging)frame, event.getDamager());
                Bukkit.getPluginManager().callEvent((Event)breakEvent);
                if (breakEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                World world = frame.getWorld();
                Location location = frame.getLocation();
                SoundManager.playSound((Player)event.getEntity(), "ENTITY_ITEMFRAME_BREAK");
                frame.remove();
                Entity owner = this.getOwner(event.getDamager());
                if (!(owner instanceof Player) || ((Player)owner).getGameMode() != GameMode.CREATIVE) {
                    BlockFace facing = frame.getFacing();
                    Item drop = world.dropItemNaturally(location.add((double)(facing.getModX() / 3), (double)(facing.getModY() / 3), (double)(facing.getModZ() / 3)), new ItemStack(Material.ITEM_FRAME));
                    this.mark((Entity)drop, Bukkit.getOfflinePlayer((UUID)creator));
                }
            } else {
                this.scheduleItemFrameUpdate(frame);
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setDisplayName(null);
                    item.setItemMeta(itemMeta);
                    frame.setItem(item);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    void onPlayerDeath(PlayerDeathEvent event) {
        Player killer;
        Player victim = event.getEntity();
        String victimName = victim.getName();
        if (!Config.checkPlayerDeath) {
            return;
        }
        if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
            if (Config.deathCreativeKeepInventory) {
                event.setKeepInventory(true);
            }
            if (Config.deathCreativeKeepLevel) {
                event.setKeepLevel(true);
            }
            if (Config.deathCreativeDestroysCreativeDrops) {
                event.getDrops().removeIf(this::isCreative);
            }
        } else {
            if (Config.deathSurvivalKeepInventory) {
                event.setKeepInventory(true);
            }
            if (Config.deathSurvivalKeepLevel) {
                event.setKeepLevel(true);
            }
            if (Config.deathSurvivalDestroysCreativeDrops) {
                event.getDrops().removeIf(this::isCreative);
            }
        }
    }

    private boolean sameAddress(Player player1, Player player2) {
        return player1.getAddress().getHostName().equals(player2.getAddress().getHostName());
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    void onEntityDeath(EntityDeathEvent event) {
        if (!Config.trackCreativeCreatureLoot) {
            return;
        }
        UUID ownerId = CreativeListener.getOwnerId((Entity)event.getEntity());
        if (ownerId != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)ownerId);
            event.setDroppedExp(0);
            event.getDrops().forEach(drop -> CreativeListener.mark(drop, Objects.toString(offlinePlayer.getName(), offlinePlayer.getUniqueId().toString())));
        }
    }

    private void handleUnmarkedItem(Item entity) {
        if (this.isCreative((Entity)entity) && this.isCreative(entity.getItemStack())) {
            if (Config.entityMarkApplyName) {
                entity.setCustomName(((String)entity.getItemStack().getItemMeta().getLore().get(0)).substring(CREATIVE_MARK.length()));
                if (Config.entityMarkSetNameVisible) {
                    entity.setCustomNameVisible(true);
                }
            }
            if (Config.entityMarkHideNameFor.contains((Object)entity.getType())) {
                entity.setCustomNameVisible(false);
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    void onItemSpawn(ItemSpawnEvent event) {
        this.handleUnmarkedItem(event.getEntity());
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        ItemStack hand;
        Entity root = this.getOwner(event.getDamager());
        Entity entity = event.getEntity();
        if (!(root instanceof Player)) {
            switch (entity.getType()) {
                case ARMOR_STAND: {
                    if (!Config.disableCreativeArmorStandGettingHitByMonster || !this.isCreative(entity)) break;
                    event.setCancelled(true);
                    break;
                }
                case ITEM_FRAME: {
                    if (!Config.disableCreativeItemFrameGettingHitByMonster || !this.isCreative(entity)) break;
                    event.setCancelled(true);
                }
            }
            return;
        }
        Player player = (Player)root;
        switch (event.getEntity().getType()) {
            case ARMOR_STAND: {
                if (!this.checkCreativeEntity(entity, player, PermissionKey.BYPASS_INVENTORY_ARMORSTAND, PermissionKey.BYPASS_BREAK_ARMORSTAND)) break;
                event.setCancelled(true);
                return;
            }
            case ITEM_FRAME: {
                if (!this.checkCreativeEntity(entity, player, PermissionKey.BYPASS_INVENTORY_ITEMFRAME_SURVIVAL, PermissionKey.BYPASS_INVENTORY_ITEMFRAME_CREATIVE)) break;
                event.setCancelled(true);
                return;
            }
        }
        if (Config.disableUsageOfCreativeItems && player.getGameMode() != GameMode.CREATIVE && this.isCreative(hand = CreativeListener.getHand(player)) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_MARKED)) {
            event.setCancelled(true);
        }
        if (player.getGameMode() == GameMode.CREATIVE && this.checkDamageEntity(player, entity)) {
            event.setCancelled(true);
        }
    }

    private boolean checkDamageEntity(Player player, Entity entity) {
        if (this.isCreative(entity) && player.hasPermission(PermissionKey.CREATIVE_ATTACK_CREATIVE.key)) {
            return false;
        }
        EntityType type = entity.getType();
        switch (type) {
            case GIANT: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_BOSS_GIANT);
            }
            case SHULKER_BULLET: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SHULKER_BULLET);
            }
            case SHULKER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SHULKER);
            }
            case ARMOR_STAND: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_STRUCTURE_ARMORSTAND);
            }
            case COW: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_COW);
            }
            case MUSHROOM_COW: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_COW_MUSHROOM);
            }
            case CREEPER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_CREEPER);
            }
            case SPIDER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SPIDER);
            }
            case SLIME: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SLIME);
            }
            case ENDERMAN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ENDERMAN);
            }
            case CAVE_SPIDER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SPIDER_CAVE);
            }
            case SILVERFISH: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SILVERFISH);
            }
            case WITHER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_BOSS_WITHER);
            }
            case BAT: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_BAT);
            }
            case WITCH: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_WITCH);
            }
            case ENDERMITE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ENDERMITE);
            }
            case PIG: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_PIG);
            }
            case SHEEP: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_SHEEP);
            }
            case CHICKEN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_CHICKEN);
            }
            case SQUID: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_WATERANIMAL_SQUID);
            }
            case WOLF: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_WOLF);
            }
            case SNOWMAN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_GOLEM_SNOWMAN);
            }
            case OCELOT: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_OCELOT);
            }
            case IRON_GOLEM: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_GOLEM_IRON);
            }
            case RABBIT: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_RABBIT);
            }
            case POLAR_BEAR: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_BEAR);
            }
            case VILLAGER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_VILLAGER);
            }
            case ENDER_CRYSTAL: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_STRUCTURE_ENDERCRYSTAL);
            }
            case PLAYER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_PLAYER);
            }
            case ENDER_DRAGON: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_BOSS_DRAGON);
            }
            case ELDER_GUARDIAN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_BOSS_ELDERGUARDIAN);
            }
            case GUARDIAN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_GUARDIAN);
            }
            case SKELETON: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SKELETON);
            }
            case STRAY: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SKELETON_STRAY);
            }
            case ZOMBIE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ZOMBIE);
            }
            case HUSK: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ZOMBIE_HUSK);
            }
            case ZOMBIE_VILLAGER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ZOMBIE_VILLAGER);
            }
            case DONKEY: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_HORSE_DONKEY);
            }
            case MULE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_HORSE_MULE);
            }
            case HORSE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_HORSE);
            }
            case SKELETON_HORSE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_HORSE_SKELETON);
            }
            case ZOMBIE_HORSE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_HORSE_ZOMBIE);
            }
            case LLAMA: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_LLAMA);
            }
            case EVOKER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_BOSS_EVOKER);
            }
            case VEX: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_VEX);
            }
            case VINDICATOR: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_VINDICATOR);
            }
            case ILLUSIONER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_BOSS_ILLUSIONER);
            }
            case PARROT: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_PARROT);
            }
            case BEE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_BEE);
            }
            case CAT: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_CAT);
            }
            case COD: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_WATERANIMAL_COD);
            }
            case DOLPHIN: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_WATERANIMAL_DOLPHIN);
            }
            case DROWNED: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ZOMBIE_DROWNED);
            }
            case FOX: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_FOX);
            }
            case PANDA: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_PANDA);
            }
            case STRIDER: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_STRIDER);
            }
            case PHANTOM: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_PHANTOM);
            }
            case PILLAGER: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_PILLAGER);
            }
            case PUFFERFISH: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_PUFFERFISH);
            }
            case TROPICAL_FISH: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_WATERANIMAL_TROPICAL_FISH);
            }
            case RAVAGER: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_RAVAGER);
            }
            case SALMON: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_WATERANIMAL_SALMON);
            }
            case TURTLE: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_WATERANIMAL_TURTLE);
            }
            case TRADER_LLAMA: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_ANIMAL_TRADER_LLAMA);
            }
            case WANDERING_TRADER: {
            	return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_WANDERING_TRADER);
            }
            case WITHER_SKELETON: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_SKELETON_WITHER);
            }
            case BLAZE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_BLAZE);
            }
            case MAGMA_CUBE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_MAGMACUBE);
            }
            case GHAST: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_GHAST);
            }
            case HOGLIN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_HOGLIN);
            }
            case PIGLIN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_PIGLIN);
            }
            case PIGLIN_BRUTE: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_PIGLIN_BRUTE);
            }
            case ZOGLIN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ZOGLIN);
            }
            case ZOMBIFIED_PIGLIN: {
                return CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_DAMAGE_MOB_ZOMBIFIED_PIGLIN);
            }
            default:
            case UNKNOWN: {
                return false;
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        switch (player.getGameMode()) {
            case CREATIVE: {
                if (!Config.checkCreativeDrop) break;
                if (Config.trackCreativeDrop || this.isCreative(event.getItemDrop().getItemStack())) {
                    if (!player.hasPermission("creativesecurity.bypass.drop.marked")) {
                        CreativeListener.mark((Entity)event.getItemDrop(), player);
                    } else {
                        ItemStack drop = event.getItemDrop().getItemStack().clone();
                        if (this.isCreative(drop)) {
                            ItemMeta meta = drop.getItemMeta();
                            ArrayList lore = new ArrayList(meta.getLore());
                            lore.remove(0);
                            meta.setLore(lore);
                            drop.setItemMeta(meta);
                            event.getItemDrop().setItemStack(drop);
                        }
                    }
                }
                if (!CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_DROP)) break;
                event.setCancelled(true);
                break;
            }
            case ADVENTURE: {
                if (!main.getConfig().getBoolean("adventure-mode-restrictions.disable-dropping-items") || player.hasPermission("creativesecurity.bypass.adventure.drop")) break;
                event.setCancelled(true);
                break;
            }
            default: {
                if (!Config.checkSurvivalDrop || !this.isCreative(event.getItemDrop().getItemStack()) || player.hasPermission(PermissionKey.BYPASS_ACTION_DROP.key)) break;
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (Config.checkItemPickup) {
            if (this.isCreative(event.getItem().getItemStack())) {
                if (player.hasPermission("creativesecurity.bypass.pick.marked")) {
                    return;
                }
                if (player.getGameMode() != GameMode.CREATIVE && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_ACTION_PICKUPSURVIVAL)) {
                    event.setCancelled(true);
                    return;
                }
            } else if (this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_PICKUP)) {
                event.setCancelled(true);
                return;
            }
        }
        if (player.getGameMode() == GameMode.ADVENTURE && main.getConfig().getBoolean("adventure-mode-restrictions.disable-pickup-items") && !player.hasPermission(" creativesecurity.bypass.adventure.pickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onOpenInventory(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();
        if (!(player instanceof Player)) {
            return;
        }
        if (this.checkInventory(event.getInventory(), (Player)player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (!(player instanceof Player)) {
            return;
        }
        if (this.checkInventory(event.getInventory(), (Player)player)) {
            event.setCancelled(true);
        } else {
            switch (event.getAction()) {
                case PLACE_ALL: 
                case PLACE_ONE: 
                case PLACE_SOME: 
                case SWAP_WITH_CURSOR: {
                    if (event.getSlotType() == InventoryType.SlotType.ARMOR && (event.getCurrentItem().getType().toString().equals("ELYTRA") || event.getCursor().getType().toString().equals("ELYTRA")) && player.hasPermission("creativesecurity.bypass.use.elytra") || event.getSlotType() != InventoryType.SlotType.ARMOR && (event.getInventory().getType() != InventoryType.BEACON || event.getSlotType() != InventoryType.SlotType.CRAFTING) || !this.isCreative(event.getCursor()) && event.getInventory().getType() != InventoryType.CREATIVE || !CreativeListener.check(player, PermissionKey.BYPASS_USE_MARKED)) break;
                    event.setCancelled(true);
                    break;
                }
                case HOTBAR_SWAP: {
                    if (event.getSlotType() != InventoryType.SlotType.ARMOR && (event.getInventory().getType() != InventoryType.BEACON || event.getSlotType() != InventoryType.SlotType.CRAFTING)) break;
                    int hotbarButton = event.getHotbarButton();
                    ItemStack itemStack = player.getInventory().getItem(hotbarButton);
                    if (!this.isCreative(itemStack) || !CreativeListener.check(player, PermissionKey.BYPASS_USE_MARKED)) break;
                    event.setCancelled(true);
                    break;
                }
                case MOVE_TO_OTHER_INVENTORY: {
                    ItemStack currentItem;
                    InventoryType inventoryType = event.getInventory().getType();
                    if (inventoryType != InventoryType.CRAFTING && event.getInventory().getType() != InventoryType.BEACON || !wearablearmor.contains((Object)(currentItem = event.getCurrentItem()).getType()) || !this.isCreative(currentItem) || !CreativeListener.check(player, PermissionKey.BYPASS_USE_MARKED)) break;
                    event.setCancelled(true);
                    break;
                }
                case UNKNOWN: {
                    if (event.getInventory().getType() != InventoryType.CREATIVE && event.getInventory().getType() != InventoryType.BEACON && event.getSlotType() != InventoryType.SlotType.ARMOR && event.getSlotType() != InventoryType.SlotType.CRAFTING && !this.isCreative(event.getCurrentItem()) && !this.isCreative(event.getCursor()) || !CreativeListener.check(player, PermissionKey.BYPASS_USE_MARKED)) break;
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onInventoryDrag(InventoryDragEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (!(player instanceof Player)) {
            return;
        }
        if (this.checkInventory(event.getInventory(), (Player)player)) {
            event.setCancelled(true);
        } else {
            Set<Integer> checkedSlots;
            InventoryType inventoryType = event.getInventory().getType();
            if (inventoryType == InventoryType.CRAFTING) {
                checkedSlots = CRAFTING_ARMOR_SLOTS;
            } else if (inventoryType == InventoryType.BEACON) {
                checkedSlots = Collections.singleton(0);
            } else {
                return;
            }
            if (this.isCreative(event.getOldCursor())) {
                if (event.getRawSlots().stream().anyMatch(checkedSlots::contains) && CreativeListener.check(player, PermissionKey.BYPASS_USE_MARKED)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean checkInventory(Inventory inventory, Player player) {
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Mule) {
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_MULE);
        }
        if (holder instanceof Donkey) {
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_DONKEY);
        }
        if (holder instanceof Horse || holder instanceof Llama) {
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_HORSE);
        }
        if (holder instanceof StorageMinecart) {
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_STORAGEMINECART);
        }
        if (holder instanceof HopperMinecart) {
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_HOPPERMINECART);
        }
        InventoryType type = inventory.getType();
        switch (type) {
        case CHEST: 
            if (holder != null) {
                PermissionKey permission;
                if (holder instanceof DoubleChest) {
                    permission = PermissionKey.BYPASS_INVENTORY_DOUBLECHEST;
                } else if (holder instanceof Chest) {
                    permission = PermissionKey.BYPASS_INVENTORY_CHEST;
                } else {
                    return false;
                }
                return this.checkCreative((HumanEntity)player, permission);
            }
            return false;
        case DISPENSER: 
            return holder instanceof org.bukkit.block.Dispenser && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_DISPENSER);
        
        case DROPPER: 
            return holder instanceof Dropper && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_DROPPER);
        
        case FURNACE: 
            return holder instanceof Furnace && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_FURNACE);
        
        case WORKBENCH: 
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_WORKBENCH);
        
        case CRAFTING: 
            return false;
        
        case ENCHANTING: 
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_ENCHANTING);
        
        case BREWING: 
            return holder instanceof BrewingStand && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_BREWING);
        
        case PLAYER: 
            return false;
        
        case CREATIVE: 
            return false;
            
        case MERCHANT: {
        	if (holder instanceof Villager) {
        		return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_VILLAGER);
        	} else if(holder instanceof WanderingTrader) {
        		return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_WANDERING_TRADER);
        	}
        }
        case ENDER_CHEST: 
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_ENDERCHEST);
        
        case ANVIL: 
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_ANVIL);
        
        case BEACON: 
            return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_BEACON);
        
        case SHULKER_BOX: 
            return holder instanceof ShulkerBox && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_SHULKERBOX);
        
        case HOPPER: 
            return holder instanceof Hopper && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_HOPPER);
        
        case BARREL: 
        	return holder instanceof Barrel && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_BARREL);
        
        case BLAST_FURNACE: 
        	return holder instanceof BlastFurnace && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_BLAST_FURNACE);
        
        case SMOKER: 
        	return holder instanceof Smoker && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_SMOKER);
        
        case CARTOGRAPHY: 
        	return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_CARTOGRAPHY);
        
        case GRINDSTONE: 
        	return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_GRINDSTONE);
        
        case LECTERN: 
        	return holder instanceof Lectern && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_LECTERN);
        
        case LOOM: 
        	return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_LOOM);
        
        case STONECUTTER: 
        	return this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_INVENTORY_STONECUTER);
        default: return false;
    }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        switch (entity.getType()) {
            case ARMOR_STAND: {
                if (!this.checkCreativeEntity(entity, player, PermissionKey.BYPASS_INVENTORY_ARMORSTAND, PermissionKey.BYPASS_BREAK_ARMORSTAND)) break;
                event.setCancelled(true);
            }
        }
    }

    private boolean checkCreativeEntity(Entity entity, Player player, PermissionKey survivalPermission, PermissionKey creativePermission) {
        return player.getGameMode() == GameMode.CREATIVE ? !this.isCreative(entity) && CreativeListener.check((HumanEntity)player, survivalPermission) : this.isCreative(entity) && CreativeListener.check((HumanEntity)player, creativePermission);
    }

    private void markItemFromEntity(Entity entity, ItemStack current, Object creator) {
        if (!this.isCreative(current)) {
            if (creator == null) {
                creator = Objects.requireNonNull(CreativeSecurityPlugin.getMetadata((Metadatable)entity, KEY_CREATIVE, null)).value();
            }
            String name = creator instanceof UUID ? Objects.toString(Bukkit.getOfflinePlayer((UUID)((UUID)creator)).getName(), creator.toString()) : "???";
            CreativeListener.mark(current, name);
        }
    }

    private void executeItemFrameUpdate(ItemFrame frame, Object creator) {
        ItemStack current = frame.getItem();
        if (!Config.entityMarkSetNameVisible || Config.entityMarkHideNameFor.contains((Object)frame.getType())) {
            String displayName;
            ItemMeta itemMeta;
            if (current != null && current.getType() == Config.miscEmptyCreativeItemFrameMarker && (itemMeta = current.getItemMeta()) != null && (displayName = itemMeta.getDisplayName()) != null && displayName.startsWith(CREATIVE_MARK)) {
                current = new ItemStack(Material.AIR);
            }
            this.markItemFromEntity((Entity)frame, current, creator);
            frame.setItem(current);
            return;
        }
        if (current == null || current.getType() == Material.AIR) {
            current = new ItemStack(Config.miscEmptyCreativeItemFrameMarker);
        }
        ItemMeta itemMeta = current.getItemMeta();
        String customName = frame.getCustomName();
        if (Objects.equals(customName, itemMeta.getDisplayName())) {
            this.markItemFromEntity((Entity)frame, current, creator);
            frame.setItem(current);
            return;
        }
        itemMeta.setDisplayName(frame.getCustomName());
        current.setItemMeta(itemMeta);
        this.markItemFromEntity((Entity)frame, current, creator);
        frame.setItem(current);
    }

    private void scheduleItemFrameUpdate(final ItemFrame frame) {
        if (this.isCreative((Entity)frame)) {
            new BukkitRunnable(){

                public void run() {
                    if (frame.isValid()) {
                        CreativeListener.this.executeItemFrameUpdate(frame, null);
                    }
                }
            }.runTask((Plugin)main);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onGameModeChangeMonitor(PlayerGameModeChangeEvent event) {
        MetadataValue metadata;
        Player player = event.getPlayer();
        if (event.getNewGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.CREATIVE) {
            return;
        }
        if (Config.whenGameModeChangesRemoveCreativeArmor && event.getNewGameMode() != GameMode.CREATIVE) {
            boolean checked = false;
            PlayerInventory inventory = player.getInventory();
            ItemStack[] armorContents = inventory.getArmorContents();
            boolean changed = false;
            for (int i = 0; i < armorContents.length; ++i) {
                ItemStack armor = armorContents[i];
                if (!this.isCreative(armor)) continue;
                if (!checked) {
                    if (!CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_MARKED)) break;
                    checked = true;
                }
                changed = true;
                armorContents[i] = null;
                inventory.addItem(new ItemStack[]{armor}).values().forEach(stack -> player.getWorld().dropItemNaturally(player.getLocation(), stack));
            }
            if (changed) {
                inventory.setArmorContents(armorContents);
            }
        }
        if (Config.whenGameModeChangesUnleashEntities && (metadata = CreativeSecurityPlugin.getMetadata((Metadatable)player, KEY_LEASHED_ENTITIES, null)) != null) {
            Set<LivingEntity> leashedEntities = (Set<LivingEntity>)metadata.value();
            leashedEntities.forEach(entity -> entity.setLeashHolder(null));
            player.removeMetadata(KEY_LEASHED_ENTITIES, (Plugin)main);
            if (player.getGameMode() != GameMode.CREATIVE) {
                ItemStack stack2 = XMaterial.LEAD.parseItem();
                leashedEntities.forEach(living -> living.getWorld().dropItemNaturally(living.getLocation(), stack2));
            }
        }
        if (Config.whenGameModeChangesCloseInventory) {
            player.closeInventory();
        }
        if (Config.whenGameModeChangesRemovePotions) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onLeashEntity(PlayerLeashEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        Set entities = (Set)CreativeSecurityPlugin.getMetadata((Metadatable)event.getLeashHolder(), KEY_LEASHED_ENTITIES, () -> new HashSet(2)).value();
        entities.add((LivingEntity)entity);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    void onUnleashEntity(EntityUnleashEvent event) {
        try {
            Entity entity = event.getEntity();
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            LivingEntity livingEntity = (LivingEntity)entity;
            if (livingEntity.isLeashed()) {
                Entity holder = livingEntity.getLeashHolder();
                MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)holder, KEY_LEASHED_ENTITIES, null);
                if (metadata == null) {
                    return;
                }
                Set entities = (Set)metadata.value();
                entities.remove((Object)event.getEntity());
                if (entities.isEmpty()) {
                    holder.removeMetadata(KEY_LEASHED_ENTITIES, (Plugin)main);
                }
                if (holder instanceof Player && ((Player)holder).getGameMode() == GameMode.CREATIVE) {
                    Player player = (Player)holder;
                    Location location = livingEntity.getLocation();
                    Bukkit.getScheduler().runTask((Plugin)main, () -> location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).map(it -> (Item)it).filter(it -> it.getItemStack().getType() == XMaterial.LEAD.parseMaterial()).min(Comparator.comparingInt(Entity::getTicksLived)).ifPresent(item -> {
                        CreativeListener.mark((Entity)item, player);
                        item.remove();
                    }));
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    void onInteractEntity(PlayerInteractEntityEvent event) {
        EntityType type;
        Player player = event.getPlayer();
        ItemStack hand = null;
        try {
            switch (event.getHand()) {
                case HAND: {
                    hand = player.getInventory().getItemInMainHand();
                    break;
                }
                case OFF_HAND: {
                    hand = player.getInventory().getItemInOffHand();
                }
            }
        }
        catch (NoSuchMethodError ex) {
            hand = player.getItemInHand();
        }
        Material handType = hand == null ? Material.AIR : hand.getType();
        Entity entity = event.getRightClicked();
        if (player.getGameMode() == GameMode.CREATIVE && Config.blockedItemsCreativeUsage.contains((Object)handType) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_BLOCKEDCREATIVE)) {
            event.setCancelled(true);
            return;
        }
        if (handType == XMaterial.LEAD.parseMaterial()) {
            if (this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_LEASH)) {
                event.setCancelled(true);
                return;
            }
        } else if (handType == Material.NAME_TAG) {
            if (this.isCreative(entity)) {
                event.setCancelled(true);
                Message.NAME_TAG_CREATIVE_ENTITY.sendDenial((CommandSender)player, new String[0][]);
                return;
            }
            if (this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_APPLYNAMETAG)) {
                event.setCancelled(true);
                return;
            }
        }
        if ((type = entity.getType()) != EntityType.ARMOR_STAND && type != EntityType.ITEM_FRAME && this.isCreative(hand) && player.getGameMode() != GameMode.CREATIVE && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_MARKED)) {
            event.setCancelled(true);
            return;
        }
        if (type == EntityType.ITEM_FRAME) {
            String displayName;
            ItemMeta itemMeta;
            ItemFrame frame = (ItemFrame)entity;
            this.scheduleItemFrameUpdate(frame);
            ItemStack item = frame.getItem();
            if (this.checkCreativeEntity(entity, player, PermissionKey.BYPASS_INVENTORY_ITEMFRAME_SURVIVAL, PermissionKey.BYPASS_INVENTORY_ITEMFRAME_CREATIVE)) {
                event.setCancelled(true);
            } else if (handType != Material.AIR && item != null && item.getType() == Config.miscEmptyCreativeItemFrameMarker && (itemMeta = item.getItemMeta()) != null && (displayName = itemMeta.getDisplayName()) != null && displayName.startsWith(CREATIVE_MARK)) {
                frame.setItem(null);
            }
            return;
        }
        if (player.getGameMode() != GameMode.CREATIVE && this.isCreative(entity) && CreativeListener.check((HumanEntity)player, PermissionKey.BYPASS_USE_CREATIVEENTITY)) {
            event.setCancelled(true);
            return;
        }
        block5 : switch (type) {
            case PIG: {
                if (handType == XMaterial.CARROT.parseMaterial() || handType == XMaterial.POTATO.parseMaterial() || handType == Material.BEETROOT) {
                    if (!this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                    event.setCancelled(true);
                    break;
                }
                if (handType != Material.SADDLE || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_SADDLE)) break;
                event.setCancelled(true);
                break;
            }
            case CHICKEN: {
                if (handType != XMaterial.WHEAT_SEEDS.parseMaterial() && handType != Material.PUMPKIN_SEEDS && handType != Material.MELON_SEEDS && handType != Material.BEETROOT_SEEDS || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                event.setCancelled(true);
                break;
            }
            case OCELOT: {
                if (handType != XMaterial.COD.parseMaterial() && handType != XMaterial.PUFFERFISH.parseMaterial() && handType != XMaterial.SALMON.parseMaterial() && handType != XMaterial.TROPICAL_FISH.parseMaterial() || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                event.setCancelled(true);
                break;
            }
            case RABBIT: {
                if (handType != XMaterial.DANDELION.parseMaterial() && handType != XMaterial.CARROT.parseMaterial() && handType != Material.GOLDEN_CARROT || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                event.setCancelled(true);
                break;
            }
            case MINECART_FURNACE: {
                if (!this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_USE_FURNACEMINECART)) break;
                event.setCancelled(true);
                break;
            }
            case WOLF: {
                if (handType != XMaterial.PORKCHOP.parseMaterial() && handType != XMaterial.BEEF.parseMaterial() && handType != XMaterial.CHICKEN.parseMaterial() && handType != Material.RABBIT && handType != Material.MUTTON && handType != Material.ROTTEN_FLESH && handType != XMaterial.COOKED_PORKCHOP.parseMaterial() && handType != Material.COOKED_BEEF && handType != Material.COOKED_CHICKEN && handType != Material.COOKED_RABBIT && handType != Material.COOKED_MUTTON || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                event.setCancelled(true);
                break;
            }
            case SHEEP: {
                if (handType == Material.WHEAT) {
                    if (!this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                    event.setCancelled(true);
                    break;
                }
                if (handType == Material.SHEARS) {
                    if (!this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_SHEARSHEEP)) break;
                    event.setCancelled(true);
                    break;
                }
                if (handType != XMaterial.INK_SAC.parseMaterial() || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_DYESHEEP)) break;
                event.setCancelled(true);
                break;
            }
            case COW: 
            case MUSHROOM_COW: {
                switch (handType) {
                    case WHEAT: {
                        if (!this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                        event.setCancelled(true);
                        break block5;
                    }
                    case BUCKET: {
                        if (!this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_MILK)) break;
                        event.setCancelled(true);
                        break block5;
                    }
                    case BOWL: {
                        if (type != EntityType.MUSHROOM_COW || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_MOOSHROOM)) break;
                        event.setCancelled(true);
                    }
                }
                break;
            }
            case SNOWMAN: {
                if (handType != Material.SHEARS || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_SHEARSNOWMAN)) break;
                event.setCancelled(true);
                break;
            }

            case ZOMBIE_HORSE: 
            case SKELETON_HORSE:
            case HORSE: {
                switch (handType) {
                    case GOLDEN_APPLE: 
                    case GOLDEN_CARROT: 
                    case APPLE: 
                    case HAY_BLOCK: 
                    case WHEAT: 
                    case SUGAR: {
                        if (!this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                        event.setCancelled(true);
                    }
                }
                break;
            }
            case ZOMBIE_VILLAGER: 
            case HUSK:
            case ZOMBIE: {
                if (handType != Material.GOLDEN_APPLE || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEEDZOMBIE)) break;
                event.setCancelled(true);
            }
            case LLAMA: {
                if (handType != Material.HAY_BLOCK || !this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) break;
                event.setCancelled(true);
                break;
            }
            
            case PARROT: {
            	if (handType == Material.COOKIE || handType == XMaterial.WHEAT_SEEDS.parseMaterial() && this.checkCreative((HumanEntity)player, PermissionKey.BYPASS_ACTION_FEED)) {
                    event.setCancelled(true);
                }
            }
            
            //default: {
            //    CreativeSecurityPlugin.future.onInteractEntity(this, player, entity, type, event, handType);
            //}
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onOpenEnderchest(PlayerInteractEvent event) {
        if (!event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.hasBlock() && event.getClickedBlock().getType() == Material.ENDER_CHEST && event.useInteractedBlock() != Event.Result.DENY && this.checkInventory(event.getPlayer().getEnderChest(), event.getPlayer())) {
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    void onPlaceEntityMonitor(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (event.useItemInHand() != Event.Result.DENY && event.hasItem() && (player.getGameMode() == GameMode.CREATIVE || this.isCreative(event.getItem()))) {
            Material type = event.getItem().getType();
            if (type == Material.ARMOR_STAND) {
                if (Config.trackArmorStand && action == Action.RIGHT_CLICK_BLOCK) {
                    this.armorStandPlacer = new WeakReference<Player>(player);
                }
            } else if (type == XMaterial.ENDER_EYE.parseMaterial()) {
                if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
                    return;
                }
                Location location = player.getEyeLocation();
                Bukkit.getScheduler().runTask((Plugin)main, () -> {
                    World world = location.getWorld();
                    Entity eyeOfEnder = world.getNearbyEntities(location, 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.ENDER_SIGNAL).min(Comparator.comparingInt(Entity::getTicksLived)).orElse(null);
                    if (eyeOfEnder == null) {
                        return;
                    }
                    CreativeListener.mark(eyeOfEnder, player);
                    this.preventEyeOfEnderFromDropping(eyeOfEnder);
                });
            } else if (type.toString().contains("BOAT")) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    Location location = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
                    Bukkit.getScheduler().runTask((Plugin)main, () -> {
                        World world = location.getWorld();
                        Entity boat = world.getNearbyEntities(location, 1.5, 1.5, 1.5).stream().filter(it -> it.getType() == EntityType.BOAT).filter(it -> it.getTicksLived() == 1).findFirst().orElse(null);
                        if (boat == null) {
                            return;
                        }
                        CreativeListener.mark(boat, player);
                    });
                }
            } else if (type == Material.MINECART || type == XMaterial.COMMAND_BLOCK_MINECART.parseMaterial() || type == XMaterial.TNT_MINECART.parseMaterial() || type == Material.HOPPER_MINECART || type == XMaterial.FURNACE_MINECART.parseMaterial() || type == XMaterial.CHEST_MINECART.parseMaterial()) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    Location location = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
                    Bukkit.getScheduler().runTask((Plugin)main, () -> {
                        World world = location.getWorld();
                        Entity cart = world.getNearbyEntities(location, 1.5, 1.5, 1.5).stream().filter(it -> it instanceof Minecart).filter(it -> it.getTicksLived() == 1).findFirst().orElse(null);
                        if (cart == null) {
                            return;
                        }
                        CreativeListener.mark(cart, player);
                    });
                }
            } else if (type.toString().equals("END_CRYSTAL")) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    Location location = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
                    Bukkit.getScheduler().runTask((Plugin)main, () -> {
                        World world = location.getWorld();
                        Entity crystal = world.getNearbyEntities(location, 1.5, 1.5, 1.5).stream().filter(it -> it.getType() == EntityType.ENDER_CRYSTAL).filter(it -> it.getTicksLived() == 1).findFirst().orElse(null);
                        if (crystal == null) {
                            return;
                        }
                        CreativeListener.mark(crystal, player);
                    });
                }
            } else if ((type.toString().equals("MONSTER_EGG") || type.toString().contains("_SPAWN_EGG")) && Config.trackEntitySpawnByEgg && action == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
                Bukkit.getScheduler().runTask((Plugin)main, () -> {
                    World world = location.getWorld();
                    Entity entity = world.getNearbyEntities(location, 1.5, 1.5, 1.5).stream().filter(it -> it instanceof LivingEntity).filter(it -> it.getTicksLived() == 1).findFirst().orElse(null);
                    if (entity == null) {
                        return;
                    }
                    CreativeListener.mark(entity, player);
                });
            }
        }
    }

    private void preventEyeOfEnderFromDropping(Entity eyeOfEnder) {
        if (!Config.disableCreativeEnderEyeDropping) {
            return;
        }
        Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
            if (eyeOfEnder.isValid()) {
                eyeOfEnder.remove();
                Location effectLocation = eyeOfEnder.getLocation();
                eyeOfEnder.getWorld().playEffect(effectLocation, Effect.ENDER_SIGNAL, 0);
                effectLocation.getWorld().playSound(effectLocation, Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
            }
        }, (long)(80 - eyeOfEnder.getTicksLived()));
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onCreatureSpawnMonitor(CreatureSpawnEvent event) {
        Player player;
        UUID uid;
        LivingEntity entity = event.getEntity();
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT && CreativeSecurityPlugin.getMetadata((Metadatable)entity, CREATIVE_MARK, null) == null) {
            this.entityLoaded((Entity)entity);
        }
        if (!(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_WITHER || (uid = CreativeListener.getRegionData(entity.getLocation().getBlock()).getOwnerId(new BlockPosition(event.getLocation()))) == null || (player = Bukkit.getPlayer((UUID)uid)).hasPermission("creativesecurity.bypass.mark"))) {
            CreativeListener.mark((Entity)entity, player);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    void onPlayerQuit(PlayerQuitEvent event) {
        Entity vehicle = event.getPlayer().getVehicle();
        if (this.isCreative(vehicle)) {
            this.entityUnloaded(vehicle);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    void onArmorStandSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.ARMOR_STAND) {
            WeakReference<Player> reference = this.armorStandPlacer;
            this.armorStandPlacer = null;
            if (reference == null) {
                return;
            }
            Player player = (Player)reference.get();
            if (player == null) {
                return;
            }
            if (!player.hasPermission("creativesecurity.bypass.mark")) {
                CreativeListener.mark((Entity)event.getEntity(), player);
            }
            this.entityUnloaded((Entity)event.getEntity());
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    void onPlaceHanging(HangingPlaceEvent event) {
        block12: {
            Player player;
            block11: {
                Predicate<Material> filter;
                player = event.getPlayer();
                PlayerInventory inventory = event.getPlayer().getInventory();
                switch (event.getEntity().getType()) {
                    case PAINTING: {
                        if (!Config.trackPainting) {
                            return;
                        }
                        filter = it -> it == Material.PAINTING;
                        break;
                    }
                    case ITEM_FRAME: {
                        if (!Config.trackItemFrame) {
                            return;
                        }
                        filter = it -> it == Material.ITEM_FRAME;
                        break;
                    }
                    default: {
                        filter = it -> true;
                    }
                }
                if (player.getGameMode() == GameMode.CREATIVE) break block11;
                if (!Stream.of(new ItemStack[]{CreativeListener.getHand(player), CreativeListener.getOffHand(player)}).filter(Objects::nonNull).filter(this::isCreative).map(ItemStack::getType).anyMatch(filter)) break block12;
            }
            Hanging entity = event.getEntity();
            CreativeListener.mark((Entity)entity, player);
            if (Config.entityMarkSetNameVisible && entity.getType() == EntityType.ITEM_FRAME && !Config.entityMarkHideNameFor.contains((Object)EntityType.ITEM_FRAME)) {
                ItemFrame frame = (ItemFrame)entity;
                ItemStack item = frame.getItem();
                if (item == null || item.getType() == Material.AIR) {
                    item = new ItemStack(Config.miscEmptyCreativeItemFrameMarker);
                }
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(frame.getCustomName());
                item.setItemMeta(itemMeta);
                CreativeListener.mark(item, (HumanEntity)player);
                frame.setItem(item);
            }
        }
    }

    static ItemStack getHand(Player p) {
        try {
            p.getInventory().getItemInMainHand();
            return p.getInventory().getItemInMainHand();
        }
        catch (NoSuchMethodError e) {
            return p.getItemInHand();
        }
    }

    static ItemStack getOffHand(Player p) {
        try {
            return p.getInventory().getItemInOffHand();
        }
        catch (Exception ex) {
            return p.getItemInHand();
        }
    }

    private Object updateMark(Entity entity) {
        Comparable result;
        String name = Objects.toString(entity.getCustomName(), "");
        if (!name.startsWith(CREATIVE_MARK)) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                this.handleUnmarkedItem((Item)entity);
            }
            return null;
        }
        String uuidString = name.substring(CREATIVE_MARK.length());
        UUID uuid = null;
        if (!uuidString.isEmpty()) {
            try {
                uuid = UUID.fromString(uuidString);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if (uuid == null) {
            if (Config.entityMarkApplyName) {
                entity.setCustomName(CreativeListener.entityMark(Message.ENTITY_MARK.applyArgs((HumanEntity)((Player)null), new String[][]{{"player", "???"}})));
            } else {
                entity.setCustomName(CREATIVE_MARK);
                entity.setCustomNameVisible(false);
            }
            result = true;
        } else {
            if (Config.entityMarkApplyName) {
                String currentName = Objects.toString(Bukkit.getOfflinePlayer((UUID)uuid).getName(), uuid.toString());
                entity.setCustomName(CreativeListener.entityMark(Message.ENTITY_MARK.applyArgs((HumanEntity)((Player)null), new String[][]{{"player", currentName}})));
                entity.setCustomNameVisible(Config.entityMarkSetNameVisible && !Config.entityMarkHideNameFor.contains((Object)entity.getType()));
            } else {
                entity.setCustomName(CREATIVE_MARK);
                entity.setCustomNameVisible(false);
            }
            result = uuid;
        }
        EntityType type = entity.getType();
        if (type == EntityType.ITEM_FRAME) {
            this.executeItemFrameUpdate((ItemFrame)entity, result);
        } else if (type == EntityType.ENDER_SIGNAL) {
            this.preventEyeOfEnderFromDropping(entity);
        } else if (type == EntityType.FALLING_BLOCK) {
            new ItemDropTracker(entity, Bukkit.getOfflinePlayer((UUID)(result instanceof UUID ? result : new UUID(0L, 0L))), ((FallingBlock)entity).getMaterial());
        }
        return result;
    }

    void loadRegionData(World world, RegionPosition reg) {
        long start = 0L;
        Map<RegionPosition, RegionData> regions = hotRegions.computeIfAbsent(world, w -> new ConcurrentHashMap());
        if (regions.containsKey(reg)) {
            return;
        }
        AtomicBoolean computed = new AtomicBoolean(false);
        this.loadingRegions.computeIfAbsent(new Pair<World, RegionPosition>(world, reg), pair -> {
            if (Config.dataLogEnabled) {
                CreativeSecurityPlugin.dataLogger.fine("Scheduling: " + world.getName() + " " + reg);
            }
            AtomicReference<Future<RegionData>> future = new AtomicReference<Future<RegionData>>();
            Future<RegionData> result = CreativeListener.main.dataLoadingExecutor.submit(() -> regions.computeIfAbsent(reg, pos -> {
                long compStart = System.nanoTime();
                try {
                    if (Config.dataLogEnabled) {
                        CreativeSecurityPlugin.dataLogger.fine("Loading: " + world.getName() + " " + reg);
                    }
                    RegionData regionData = RegionData.load(world, pos.regionX, pos.regionZ);
                    return regionData;
                }
                catch (IOException | SQLException e) {
                    main.getLogger().log(Level.SEVERE, "Failed to load the region data for: World: " + world.getName() + " Reg: " + reg, e);
                    if (Config.dataLogEnabled) {
                        CreativeSecurityPlugin.dataLogger.log(Level.SEVERE, "Failed: " + world.getName() + " " + reg);
                    }
                    RegionData regionData = null;
                    return regionData;
                }
                finally {
                    Bukkit.getScheduler().runTask((Plugin)main, () -> {
                        Object present;
                        Future value = (Future)future.get();
                        try {
                            present = value.get();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            present = e;
                        }
                        if (Config.dataLogEnabled) {
                            CreativeSecurityPlugin.dataLogger.fine("Finished: " + world.getName() + " " + reg + " value: " + String.valueOf(value) + " present: " + String.valueOf(present));
                        }
                        if (value != null) {
                            this.loadingRegions.remove(pair, value);
                        } else {
                            try {
                                Thread.currentThread().wait(2L);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            value = (Future)future.get();
                            if (value != null) {
                                this.loadingRegions.remove(pair, value);
                            } else {
                                this.loadingRegions.remove(pair);
                            }
                        }
                    });
                }
            }));
            future.set(result);
            return result;
        });
    }

    void entityLoaded(Entity entity) {
        Object mark = this.updateMark(entity);
        if (mark == null) {
            return;
        }
        entity.setMetadata(KEY_CREATIVE, (MetadataValue)new FixedMetadataValue((Plugin)main, mark));
    }

    void chunkLoaded(Chunk chunk, boolean isNew) {
        for (Entity entity : chunk.getEntities()) {
            this.entityLoaded(entity);
        }
        this.loadRegionData(chunk.getWorld(), new RegionPosition(chunk));
    }

    void entityUnloaded(Entity entity) {
        MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)entity, KEY_CREATIVE, null);
        if (metadata != null) {
            Object value = metadata.value();
            if (value instanceof UUID) {
                entity.setCustomName(CREATIVE_MARK + value);
            } else {
                entity.setCustomName(CREATIVE_MARK);
            }
        }
    }

    void chunkUnloaded(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            this.entityUnloaded(entity);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    void onWorldSave(WorldSaveEvent event) {
        CreativeSecurityPlugin instance = main;
        World world = event.getWorld();
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.config("The world " + world.getName() + " is being saved");
        }
        Chunk[] loadedChunks = world.getLoadedChunks();
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.config("Faking a chunk unload task for all " + loadedChunks.length + " loaded chunks");
        }
        (Arrays.stream(loadedChunks).parallel()).forEach(this::chunkUnloaded);
        Bukkit.getScheduler().runTask((Plugin)instance, () -> {
            if (Config.dataLogEnabled) {
                CreativeSecurityPlugin.dataLogger.config("Reloading all " + loadedChunks.length + " chunks for " + world.getName() + " if they are still loaded");
            }
            (Arrays.stream(loadedChunks).parallel()).filter(Chunk::isLoaded).forEach(c -> this.chunkLoaded((Chunk)c, false));
        });
        this.saveRegion(world);
    }

    void saveRegions() {
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.fine("Saving all regions for all worlds...");
        }
        for (World world : new ArrayList<World>(hotRegions.keySet())) {
            this.saveRegion(world);
        }
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.fine("All regions have been saved");
        }
    }

    private void saveRegion(World world) {
        Map<RegionPosition, RegionData> regions;
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.fine("Saving regions for world: " + world.getName());
        }
        if ((regions = hotRegions.get((Object)world)) == null) {
            if (Config.dataLogEnabled) {
                CreativeSecurityPlugin.dataLogger.fine("No region was loaded");
            }
            return;
        }
        regions.values().forEach(regionData -> {
            block2: {
                try {
                    regionData.save(world);
                }
                catch (IOException | SQLException e) {
                    main.getLogger().severe("Failed to save the region data for " + world.getName() + "," + regionData.regionX + "," + regionData.regionZ);
                    if (!Config.dataLogEnabled) break block2;
                    CreativeSecurityPlugin.dataLogger.severe("Failed to save the region data for " + world.getName() + "," + regionData.regionX + "," + regionData.regionZ);
                }
            }
        });
        Set loadedRegions = (Arrays.stream(world.getLoadedChunks()).parallel()).map(RegionPosition::new).distinct().collect(Collectors.toSet());
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.fine("Removing unloaded regions from the world: " + world.getName() + " Current size: " + regions.size() + ", The world has " + loadedRegions.size() + " regions loaded");
        }
        regions.keySet().retainAll(loadedRegions);
        hotRegions.compute(world, (w, l) -> l.isEmpty() ? null : l);
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.fine("New hot region size: " + regions.size());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onWorldUnload(WorldUnloadEvent event) {
        if (Config.dataLogEnabled) {
            CreativeSecurityPlugin.dataLogger.fine("Unloading the WORLD: " + event.getWorld().getName());
        }
        hotRegions.remove((Object)event.getWorld());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    void onChunkLoad(ChunkLoadEvent event) {
        this.chunkLoaded(event.getChunk(), event.isNewChunk());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onChunkUnload(ChunkUnloadEvent event) {
        this.chunkUnloaded(event.getChunk());
    }

    static String entityMark(StringBuilder builder) {
        builder.insert(0, CREATIVE_MARK);
        return builder.toString();
    }

    void mark(Entity entity, OfflinePlayer player) {
        CreativeSecurityPlugin.getMetadata((Metadatable)entity, KEY_CREATIVE, ((OfflinePlayer)player)::getUniqueId);
        if (entity instanceof Item) {
            Item item = (Item)entity;
            ItemStack itemStack = item.getItemStack();
            if (Config.untrackedMaterials.contains((Object)itemStack.getType())) {
                return;
            }
            CreativeListener.mark(itemStack, null, new String[][]{{"player", Objects.toString(player.getName(), "???")}});
            item.setItemStack(itemStack);
        }
        if (Config.entityMarkApplyName) {
            entity.setCustomName(CreativeListener.entityMark(Message.ENTITY_MARK.applyArgs((HumanEntity)((Player)null), new String[][]{{"player", Objects.toString(player.getName(), "???")}})));
            if (Config.entityMarkSetNameVisible) {
                entity.setCustomNameVisible(true);
            }
            if (Config.entityMarkHideNameFor.contains((Object)entity.getType())) {
                entity.setCustomNameVisible(false);
            }
        } else {
            entity.setCustomName(CREATIVE_MARK);
        }
    }

    static void mark(Entity entity, Player player) {
        if (player.hasPermission("creativesecurity.bypass.mark")) {
            return;
        }
        CreativeSecurityPlugin.getMetadata((Metadatable)entity, KEY_CREATIVE, ((Player)player)::getUniqueId);
        if (entity instanceof Item) {
            Item item = (Item)entity;
            ItemStack itemStack = item.getItemStack();
            if (Config.untrackedMaterials.contains((Object)itemStack.getType())) {
                return;
            }
            CreativeListener.mark(itemStack, (HumanEntity)player);
            item.setItemStack(itemStack);
        }
        if (Config.entityMarkApplyName) {
            entity.setCustomName(CreativeListener.entityMark(Message.ENTITY_MARK.applyArgs((HumanEntity)player, new String[0][])));
            if (Config.entityMarkSetNameVisible) {
                entity.setCustomNameVisible(true);
            }
            if (Config.entityMarkHideNameFor.contains((Object)entity.getType())) {
                entity.setCustomNameVisible(false);
            }
        } else {
            entity.setCustomName(CREATIVE_MARK);
        }
    }

    static void mark(ItemStack stack, HumanEntity player) {
        CreativeListener.mark(stack, player, new String[0][]);
    }

    static void mark(ItemStack stack, String playerName) {
        CreativeListener.mark(stack, null, new String[][]{{"player", Objects.toString(playerName, "???")}});
    }

    static void mark(ItemStack stack, HumanEntity player, String[] ... extraArgs) {
        if (Config.untrackedMaterials.contains(stack.getType())) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        List<String> previousLore = meta.hasLore() ? meta.getLore() : Collections.emptyList();
        List<String> newLore = new ArrayList<String>(previousLore.size() + 1);
        newLore.add(CREATIVE_MARK + Message.ITEM_MARK.applyArgs(player, extraArgs));
        for(String lorestring : previousLore) {
        	if(lorestring.length()<CREATIVE_MARK.length() || !lorestring.substring(0,CREATIVE_MARK.length()).equals(CREATIVE_MARK)) {
        		newLore.add(lorestring);
        	}
        }
        meta.setLore(newLore);
        stack.setItemMeta(meta);
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    void onInventoryCreative(InventoryCreativeEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked.getGameMode() != GameMode.CREATIVE) {
            return;
        }
        if (event.getClick() == ClickType.CREATIVE && !whoClicked.hasPermission("creativesecurity.bypass.nbtclone")) {
            Bukkit.getScheduler().runTaskLater((Plugin)main, () -> {
                for (int i = 0; i < whoClicked.getInventory().getSize(); ++i) {
                    ItemStack item = whoClicked.getInventory().getItem(i);
                    if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasLore() || !item.getItemMeta().getLore().stream().filter(obj -> obj.contains("(+NBT")).findFirst().isPresent()) continue;
                    ItemStack itemto = new ItemStack(item.getType());
                    ItemMeta meta = itemto.getItemMeta();
                    List lore = item.getItemMeta().getLore();
                    lore.remove(lore.size() - 1);
                    meta.setLore(lore);
                    meta.setDisplayName(item.getItemMeta().getDisplayName());
                    itemto.setItemMeta(meta);
                    whoClicked.getInventory().setItem(i, itemto);
                    break;
                }
            }, 1L);
        }
        ItemStack cursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        if (!(whoClicked instanceof Player)) {
            return;
        }
        Player player = (Player)whoClicked;
        switch (event.getAction()) {
            case PLACE_ALL: {
                PotionMeta potionMeta;
                ItemMeta itemMeta = cursor.getItemMeta();
                if (Config.trackCreativeItemSpawn && cursor.getType() != Material.AIR && !player.hasPermission("creativesecurity.bypass.mark")) {
                    String displayName;
                    if (itemMeta != null && (displayName = itemMeta.getDisplayName()) != null && displayName.startsWith(CREATIVE_MARK)) {
                        itemMeta.setDisplayName(null);
                        cursor.setItemMeta(itemMeta);
                        if (cursor.getType() == Config.miscEmptyCreativeItemFrameMarker) {
                            cursor.setType(Material.ITEM_FRAME);
                        }
                    }
                    CreativeListener.mark(cursor, whoClicked);
                }
                if (itemMeta instanceof PotionMeta && !player.hasPermission("creativesecurity.bypass.weirdpotions") && (potionMeta = (PotionMeta)itemMeta).hasCustomEffects()) {
                    if (potionMeta.getCustomEffects().size() > 4) {
                        event.setCancelled(true);
                        return;
                    }
                    for (PotionEffect effect : potionMeta.getCustomEffects()) {
                        if (effect.getAmplifier() <= 3) continue;
                        event.setCancelled(true);
                        return;
                    }
                }
                if (itemMeta != null && !player.hasPermission("creativesecurity.bypass.openchants")) {
                    for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                        if (enchantment.canEnchantItem(cursor) && itemMeta.getEnchantLevel(enchantment) <= enchantment.getMaxLevel()) continue;
                        event.setCancelled(true);
                        PermissionKey.BYPASS_ACTION_SPAWNCREATIVEBLOCKED.denial.sendDenial((CommandSender)player, new String[0][]);
                        return;
                    }
                }
                if ((cursor == null || cursor.getType() == Material.AIR || !Config.blockedItemsFromCreativeInventoryActions.contains((Object)cursor.getType())) && (currentItem == null || currentItem.getType() == Material.AIR || !Config.blockedItemsFromCreativeInventoryActions.contains((Object)currentItem.getType())) || !CreativeListener.check(whoClicked, PermissionKey.BYPASS_ACTION_SPAWNCREATIVEBLOCKED)) break;
                event.setCancelled(true);
                player.updateInventory();
                break;
            }
            default: {
                main.getLogger().severe("Unexpected creative inventory action: " + (Object)event.getAction());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack handItem;
        String sectionName;
        Material consumedItemMaterial = event.getItem().getType();
        if (consumedItemMaterial == XMaterial.GOLDEN_APPLE.parseMaterial()) {
            sectionName = "golden-apple";
        } else if (consumedItemMaterial == XMaterial.ENCHANTED_GOLDEN_APPLE.parseMaterial()) {
            sectionName = "enchanted-golden-apple";
        } else {
            return;
        }
        ConfigurationSection configSection = main.getConfig().getConfigurationSection("player-item-consume-control." + sectionName);
        if (!configSection.getBoolean("enabled")) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (this.consumeItemCooldown.containsKey((Object)consumedItemMaterial)) {
            if (this.consumeItemCooldown.get((Object)consumedItemMaterial).containsKey(player.getUniqueId())) {
                player.sendMessage(configSection.getString("cooldown-message").replace("{amount}", String.valueOf((this.consumeItemCooldown.get((Object)consumedItemMaterial).get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L)));
                return;
            }
        } else {
            this.consumeItemCooldown.put(consumedItemMaterial, new HashMap());
        }
        int cooldown = configSection.getInt("cooldown");
        this.consumeItemCooldown.get((Object)consumedItemMaterial).put(player.getUniqueId(), System.currentTimeMillis() + (long)(cooldown * 1000));
        main.getServer().getScheduler().runTaskLater((Plugin)main, () -> this.consumeItemCooldown.get((Object)consumedItemMaterial).remove(player.getUniqueId()), (long)(cooldown * 20));
        int foodLevel = configSection.getInt("food-level");
        if ((float)player.getFoodLevel() < 20.0f && (float)(player.getFoodLevel() + foodLevel) < 20.0f) {
            player.setFoodLevel(player.getFoodLevel() + foodLevel);
        } else {
            player.setFoodLevel(20);
        }
        float saturation = Double.valueOf(configSection.getDouble("saturation")).floatValue();
        if (player.getSaturation() < 20.0f && player.getSaturation() + saturation < 20.0f) {
            player.setSaturation(player.getSaturation() + saturation);
        } else {
            player.setSaturation(20.0f);
        }
        boolean mainHand = true;
        try {
            if (player.getInventory().getItemInMainHand().getType() == consumedItemMaterial) {
                handItem = player.getInventory().getItemInMainHand();
            } else {
                mainHand = false;
                handItem = player.getInventory().getItemInOffHand();
            }
        }
        catch (NoSuchMethodError ex) {
            handItem = player.getItemInHand();
        }
        if (handItem.getAmount() > 1) {
            handItem.setAmount(handItem.getAmount() - 1);
        } else {
            ItemStack air = new ItemStack(Material.AIR);
            try {
                if (mainHand) {
                    player.getInventory().setItemInMainHand(air);
                } else {
                    player.getInventory().setItemInOffHand(air);
                }
            }
            catch (NoSuchMethodError ex) {
                player.getInventory().setItemInHand(air);
            }
        }
        List<String> configEffects = configSection.getStringList("effects");
        for (String effectInput : configEffects) {
            String[] input = effectInput.split(":");
            PotionEffectType potionEffectType = PotionEffectType.getByName((String)input[0]);
            int duration = Integer.parseInt(input[1]);
            int amplifier = Integer.parseInt(input[2]);
            if (player.hasPotionEffect(potionEffectType)) {
                player.removePotionEffect(potionEffectType);
            }
            player.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier));
        }
    }

    static {
        doublePlant = EnumSet.of(XMaterial.LARGE_FERN.parseMaterial(), new Material[]{XMaterial.LILAC.parseMaterial(), XMaterial.PEONY.parseMaterial(), XMaterial.ROSE_BUSH.parseMaterial(), XMaterial.SUNFLOWER.parseMaterial(), XMaterial.TALL_GRASS.parseMaterial()});
        woodenPlates = EnumSet.of(XMaterial.ACACIA_PRESSURE_PLATE.parseMaterial(), new Material[]{XMaterial.BIRCH_PRESSURE_PLATE.parseMaterial(), XMaterial.DARK_OAK_PRESSURE_PLATE.parseMaterial(), XMaterial.JUNGLE_PRESSURE_PLATE.parseMaterial(), XMaterial.OAK_PRESSURE_PLATE.parseMaterial(), XMaterial.SPRUCE_PRESSURE_PLATE.parseMaterial()});
        redRoses = EnumSet.of(XMaterial.AZURE_BLUET.parseMaterial(), new Material[]{XMaterial.BLUE_ORCHID.parseMaterial(), XMaterial.ORANGE_TULIP.parseMaterial(), XMaterial.OXEYE_DAISY.parseMaterial(), XMaterial.PINK_TULIP.parseMaterial(), XMaterial.POPPY.parseMaterial(), XMaterial.RED_TULIP.parseMaterial(), XMaterial.WHITE_TULIP.parseMaterial()});
        try {
            fishBuckets = EnumSet.of(Material.COD_BUCKET, Material.PUFFERFISH_BUCKET, Material.SALMON_BUCKET, Material.TROPICAL_FISH_BUCKET);
        }
        catch (NoSuchFieldError err) {
            fishBuckets = EnumSet.noneOf(Material.class);
        }
        CRAFTING_ARMOR_SLOTS = new HashSet<Integer>(Arrays.asList(5, 6, 7, 8));
    }

    class ItemDropTracker
    extends Tracker {
        final OfflinePlayer offlinePlayer;
        final Material filter;

        ItemDropTracker(Entity entity, OfflinePlayer offlinePlayer, Material filter) {
            super(entity);
            this.offlinePlayer = offlinePlayer;
            this.filter = filter;
        }

        @Override
        void died() {
            new Tracker(this.entity){

                @Override
                void died() {
                    Location location = this.entity.getLocation();
                    location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5).stream().filter(it -> it.getType() == EntityType.DROPPED_ITEM).filter(it -> it.getTicksLived() == 1).filter(it -> ((Item)it).getItemStack().getType() == ItemDropTracker.this.filter).findFirst().ifPresent(itemDrop -> CreativeListener.this.mark((Entity)itemDrop, ItemDropTracker.this.offlinePlayer));
                }
            };
        }
    }

    abstract class Tracker
    extends BukkitRunnable {
        Entity entity;

        Tracker(Entity entity) {
            this.entity = entity;
            this.runTaskTimer((Plugin)main, 0L, 0L);
        }

        public void run() {
            if (this.entity.isDead()) {
                this.cancel();
                this.died();
            } else if (!this.entity.isValid()) {
                this.cancel();
            }
        }

        abstract void died();
    }
}

