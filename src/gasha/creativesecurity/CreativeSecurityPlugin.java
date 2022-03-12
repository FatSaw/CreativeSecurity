package gasha.creativesecurity;

import com.sk89q.worldedit.WorldEdit;
import gasha.creativesecurity.AdditionalInventory;
import gasha.creativesecurity.AntiCommandsSuggestion;
import gasha.creativesecurity.Config;
import gasha.creativesecurity.CreativeListener;
import gasha.creativesecurity.CreativeSecurityCommandExecutor;
import gasha.creativesecurity.DataListener;
import gasha.creativesecurity.DataLogFormatter;
import gasha.creativesecurity.KeyBinding;
import gasha.creativesecurity.Message;
import gasha.creativesecurity.PlayerData;
import gasha.creativesecurity.RegionData;
import gasha.creativesecurity.RegionPosition;
import gasha.creativesecurity.SqlConfig;
import gasha.creativesecurity.consolefilter.ConsoleFilter;
import gasha.creativesecurity.guis.GuiConfig;
import gasha.creativesecurity.guis.GuiListener;
import gasha.creativesecurity.guis.inventories.GmSelectorGui;
import gasha.creativesecurity.guis.invsee.InvEditListener;
import gasha.creativesecurity.hook.WorldEditIntegration;
import gasha.creativesecurity.hook.WorldGuardHook;
import gasha.creativesecurity.regionevent.EventManager;
import gasha.creativesecurity.regionevent.MessageUT;
import me.bomb.servershield.ServerShield;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CreativeSecurityPlugin extends JavaPlugin {
    static java.util.logging.Logger dataLogger;
    private static CreativeSecurityPlugin instance;
    ExecutorService dataLoadingExecutor;
    CreativeListener creativeListener;
    private DataListener dataListener;
    private Object worldEditIntegration;
    static List<FallingBlock> FBCHECKS;
    private GuiConfig guiConfig;
    private CreativeSecurityCommandExecutor commandExecutor;
    private WorldGuardHook worldGuardHook;
    private AntiCommandsSuggestion antiCommandsSuggestion;
    public FileConfiguration messages;
    private ServerShield servershield;

    private static boolean test(Callable<?> test) {
        try {
            test.call();
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public static CreativeSecurityPlugin getInstance() {
        return Objects.requireNonNull(instance, "AntiShare plugin is not loaded");
    }

    static MetadataValue getMetadata(Metadatable metadatable, String key, Supplier<Object> defaultSupplier) {
        CreativeSecurityPlugin plugin = CreativeSecurityPlugin.getInstance();
        Optional<MetadataValue> metadata = metadatable.getMetadata(key).stream().filter(it -> it.getOwningPlugin() == plugin).findFirst();
        if (metadata.isPresent()) {
            return metadata.get();
        }
        if (defaultSupplier == null) {
            return null;
        }
        Object value = defaultSupplier.get();
        if (value == null) {
            return null;
        }
        FixedMetadataValue metadataValue = new FixedMetadataValue((Plugin)plugin, value);
        metadatable.setMetadata(key, (MetadataValue)metadataValue);
        return metadataValue;
    }

    private void startCheckFalling() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable(){
            List<FallingBlock> rem = new ArrayList<FallingBlock>();

            @Override
            public void run() {
            	if(FBCHECKS==null) return;
                if (FBCHECKS.size() == 0) {
                    return;
                }
                for (FallingBlock b : FBCHECKS) {
                    if (!b.isDead()) continue;
                    RegionData rd = CreativeListener.getRegionData(b.getWorld(), new RegionPosition(b.getLocation().getBlock()));
                    rd.mark(b.getLocation().getBlock(), (OfflinePlayer)Bukkit.getPlayer(CreativeListener.getOwnerId((Entity)b)));
                    b.remove();
                    this.rem.add(b);
                }
                for (FallingBlock remove : this.rem) {
                    FBCHECKS.remove((Object)remove);
                    remove.remove();
                }
                this.rem.clear();
            }
        }, 0L, 2L);
    }

    public void onEnable() {
        instance = this;
        ((Logger)LogManager.getRootLogger()).addFilter((Filter)new ConsoleFilter(this));
        CreativeListener.load();
        ConfigurationSerialization.registerClass(PlayerData.class);
        ConfigurationSerialization.registerClass(AdditionalInventory.class);
        dataLogger = java.util.logging.Logger.getLogger("CreativeData");
        try {
            FileHandler handler = new FileHandler(new File(this.getDataFolder().getAbsoluteFile(), "data.log").getAbsolutePath(), true);
            handler.setFormatter(new DataLogFormatter());
            handler.setEncoding("UTF-8");
            handler.setLevel(Level.ALL);
            dataLogger.addHandler(handler);
            dataLogger.setLevel(Level.ALL);
        }
        catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Failed to setup the java logger for: data.log");
        }
        dataLogger.config("The plugin is being enabled");
        this.dataLoadingExecutor = Executors.newCachedThreadPool(new ThreadFactory(){
            private volatile int last = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "AntiShareDataLoading#" + ++this.last);
                thread.setDaemon(true);
                thread.setPriority(3);
                return thread;
            }
        });
        this.saveDefaultConfig();
        List<String> dependencies = Arrays.asList("WorldEdit");
        for (String dependency : dependencies) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin(dependency);
            if (plugin != null && plugin.isEnabled()) continue;
            ConsoleCommandSender cs = this.getServer().getConsoleSender();
            cs.sendMessage(ChatColor.AQUA + "[CreativeSecurity] " + ChatColor.RED + "Can't find the plugin \"" + dependency + "\"");
            cs.sendMessage(ChatColor.AQUA + "[CreativeSecurity] " + ChatColor.RED + "CreativeSecurity depends on it.");
            cs.sendMessage(ChatColor.AQUA + "[CreativeSecurity] " + ChatColor.RED + "Disabling plugin...");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.getServer().getPluginManager().registerEvents((Listener)new EventManager(), (Plugin)this);
        this.guiConfig = new GuiConfig();
        this.worldGuardHook = new WorldGuardHook();
        this.creativeListener = new CreativeListener();
        this.dataListener = new DataListener();
        this.getServer().getPluginManager().registerEvents((Listener)this.dataListener, (Plugin)this);
        this.commandExecutor = new CreativeSecurityCommandExecutor(this, this.creativeListener, this.dataListener);
        this.antiCommandsSuggestion = new AntiCommandsSuggestion(this);
        if (this.antiCommandsSuggestion.doesCommandsSuggestSystemExist()) {
            this.getServer().getPluginManager().registerEvents((Listener)this.antiCommandsSuggestion, (Plugin)this);
            this.getLogger().info("Commands suggestions system loaded!");
        } else {
            this.getLogger().info("Can't load anti commands suggestions system. Your server version doesn't support it.");
        }
        this.reloadConfig();
        this.getServer().getPluginManager().registerEvents((Listener)this.creativeListener, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new GuiListener(this, this.dataListener), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new InvEditListener(this.guiConfig, this.dataListener), (Plugin)this);
        if (Bukkit.getBukkitVersion().contains("1.8")) {
            this.getLogger().info("Can't register KeyBinds, you server version doesn't support them");
        } else {
            this.getServer().getPluginManager().registerEvents((Listener)new KeyBinding(this), (Plugin)this);
            this.getLogger().info("KeyBinds loaded");
        }
        this.getCommand("creativesecurity").setExecutor((CommandExecutor)this.commandExecutor);
        if(!Config.disableservershield) {
        	this.servershield = new ServerShield(this);
        	this.getLogger().info("ServerShield loaded");
        }
        if (Config.worldEditIntegration > 0 && this.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            try {
                this.getLogger().info("Loading WorldEdit Integration");
                this.worldEditIntegration = new WorldEditIntegration();
                WorldEdit.getInstance().getEventBus().register(this.worldEditIntegration);
            }
            catch (Throwable e) {
                this.getLogger().log(Level.SEVERE, "Failed to load WorldEdit integration!", e);
            }
        }
        if (this.isResidenceIntegrationLoaded()) {
            this.getLogger().info("Residence Integration Loaded");
        } else {
            this.getLogger().info("Residence not found");
        }
        dataLogger.config("Scanning loaded chunks...");
        this.getServer().getWorlds().parallelStream().flatMap(world -> Arrays.stream(world.getLoadedChunks())).forEach(chunk -> this.creativeListener.chunkLoaded((Chunk)chunk, false));
        dataLogger.config("The loaded chunks have been successfully scanned");
        dataLogger.config("Scanning online players...");
        this.getServer().getOnlinePlayers().forEach(this.dataListener::load);
        dataLogger.config("The online players have been successfully scanned");
        dataLogger.config("The plugin initialization has finished");
        this.startCheckFalling();
    }

    public boolean isResidenceIntegrationLoaded() {
        Plugin residencePlugin = this.getServer().getPluginManager().getPlugin("Residence");
        return residencePlugin != null && residencePlugin.isEnabled();
    }

    boolean worldEditIntegrationLoaded() {
        return this.worldEditIntegration != null;
    }

    public void reloadConfig() {
        super.reloadConfig();
        EventManager.eventconfig.reload();
        ConsoleFilter.fc.reload();
        EventManager.reload();
        ConsoleFilter.setup();
        Config.load();
        this.creativeListener.reload();
        this.messages = Message.load();
        this.getGuiConfig().reload();
        this.commandExecutor.reload();
        this.antiCommandsSuggestion.reload();
    }

    public void onDisable() {
        dataLogger.config("The plugin is being disabled");
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
        if (this.worldEditIntegration != null) {
            try {
                WorldEdit.getInstance().getEventBus().unregister(this.worldEditIntegration);
            }
            catch (Throwable e) {
                this.getLogger().log(Level.SEVERE, "Failed to disable the WorldEdit integration!", e);
            }
        }
        if(servershield!=null) {
        	servershield.shutdown();
        	this.getLogger().info("ServerShield disabled");
        }
        dataLogger.config("Faking a chunk unload for all loaded chunks...");
        try {
            this.getServer().getWorlds().parallelStream().flatMap(world -> Arrays.stream(world.getLoadedChunks())).forEach(this.creativeListener::chunkUnloaded);
        }
        catch (NullPointerException e) {
            // empty catch block
        }
        dataLogger.config("All fake chunk unload tasks have been completed");
        dataLogger.config("Saving all regions...");
        if (this.creativeListener != null) {
            this.creativeListener.saveRegions();
        }
        dataLogger.config("All regions have been saved");
        dataLogger.config("Unloading all player data...");
        if (this.dataListener != null) {
            this.getServer().getOnlinePlayers().forEach(this.dataListener::unload);
        }
        dataLogger.config("All player data have been unloaded");
        dataLogger.config("Shutting down all custom executors...");
        this.dataLoadingExecutor.shutdown();
        try {
            this.dataLoadingExecutor.awaitTermination(5L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Runnable> runnables = this.dataLoadingExecutor.shutdownNow();
        dataLogger.config("All executors have been shut down. " + runnables.size() + " tasks are still running");
        if (!runnables.isEmpty()) {
            this.getLogger().severe("Failed to shutdown all chunkLoadingExecution tasks!");
            runnables.forEach(r -> this.getLogger().severe(" - Still running: " + r));
        }
        this.dataLoadingExecutor = null;
        HandlerList.unregisterAll((Plugin)this);
        SqlConfig.closeConnection();
        dataLogger.config("The plugin is now fully disabled");
    }

    public GuiConfig getGuiConfig() {
        return this.guiConfig;
    }

    public WorldGuardHook getWorldGuardHook() {
        return this.worldGuardHook;
    }

    private Player playerOnly(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.PLAYER_ONLY_CMD.applyArgs(new StringBuilder(), new String[0][]).toString());
            return null;
        }
        return (Player)sender;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName;
        switch (commandName = command.getName().toLowerCase()) {
            case "gmgui": {
                if (sender instanceof Player) {
                    if (!Message.hasPermission((Permissible)((Player)sender), "creativesecurity.cmd." + commandName)) {
                        return false;
                    }
                } else {
                    return false;
                }
                new GmSelectorGui((Player)sender, this.dataListener).open();
                return true;
            }
            case "gmc1": 
            case "gmc2": 
            case "gmc3": 
            case "gmc4": 
            case "gmc5": 
            case "gmc6": 
            case "gmc7": 
            case "gmc8": 
            case "gmc9": 
            case "gmc10": {
                if (sender instanceof Player && !Message.hasPermission((Permissible)((Player)sender), "creativesecurity.cmd.creative." + commandName)) {
                    return true;
                }
                Player player = this.playerOnly(sender);
                if (player == null) {
                    return false;
                }
                int index = Integer.valueOf(commandName.substring(3));
                Integer returns = this.dataListener.switchPlayerInventory(player, true, index - 1);
                if (returns == 2) {
                    String message = ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("keepinginventory"));
                    if (!message.isEmpty()) {
                        player.sendMessage(message);
                    }
                } else if (returns == 1) {
                    Message.CREATIVE_SWITCH.sendInfo((CommandSender)player, new String[][]{{"number", Integer.toString(index)}});
                } else {
                    Message.ERR_ALREADY.sendError((CommandSender)player);
                }
                return true;
            }
            case "gms1": 
            case "gms2": 
            case "gms3": 
            case "gms4": 
            case "gms5": 
            case "gms6": 
            case "gms7": 
            case "gms8": 
            case "gms9": 
            case "gms10": {
                if (sender instanceof Player && !Message.hasPermission((Permissible)((Player)sender), "creativesecurity.cmd.survival." + commandName)) {
                    return true;
                }
                Player player = this.playerOnly(sender);
                if (player == null) {
                    return false;
                }
                int index = Integer.valueOf(commandName.substring(3));
                Integer returns = this.dataListener.switchPlayerInventory(player, false, index - 1);
                if (returns == 2) {
                    String message = ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("keepinginventory"));
                    if (!message.isEmpty()) {
                        player.sendMessage(message);
                    }
                } else if (returns == 1) {
                    Message.SURVIVAL_SWITCH.sendInfo((CommandSender)player, new String[][]{{"number", Integer.toString(index)}});
                } else {
                    Message.ERR_ALREADY.sendError((CommandSender)player);
                }
                return true;
            }
            case "gmset": {
                if (sender instanceof Player && !Message.hasPermission((Permissible)((Player)sender), "creativesecurity.cmd.gmset")) {
                    return true;
                }
                CommandSender player = sender;
                if (args.length > 0) {
                    Player target = Bukkit.getPlayer((String)args[0]);
                    if (target == null) {
                        player.sendMessage(MessageUT.t(this.messages.getString("gmset-target-offline").replace("{target}", args[0])));
                        return false;
                    }
                    if (args.length > 1) {
                        String gmValue = args[1];
                        GameMode gm = null;
                        switch (gmValue.toLowerCase()) {
                            case "creative": {
                                gm = GameMode.CREATIVE;
                                break;
                            }
                            case "survival": {
                                gm = GameMode.SURVIVAL;
                                break;
                            }
                            case "spectator": {
                                gm = GameMode.SPECTATOR;
                                break;
                            }
                            case "adventure": {
                                gm = GameMode.ADVENTURE;
                            }
                        }
                        if (gm == null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("gmset-help")));
                            return false;
                        }
                        int index = 0;
                        if (gm == GameMode.SPECTATOR || gm == GameMode.ADVENTURE) {
                            target.setGameMode(gm);
                        } else if (args.length > 2) {
                            try {
                                Integer.parseInt(args[2]);
                            }
                            catch (NumberFormatException ex) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("gmset-toshort")));
                                return false;
                            }
                            index = Integer.valueOf(args[2]);
                            if (index <= 0 || index > 10) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("gmset-toshort")));
                                return false;
                            }
                            this.dataListener.switchPlayerInventory(target, gm == GameMode.CREATIVE, index - 1);
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("gmset-help")));
                            return false;
                        }
                        player.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("target-inventory-changed").replace("%number%", String.valueOf(index)).replace("%gamemode%", gm.toString().toLowerCase()).replace("%player%", target.getName())));
                        boolean targetMessage = Arrays.stream(args).map(String::toLowerCase).noneMatch("-s"::equals);
                        if (targetMessage) {
                            target.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("other-inventory-changed").replace("%number%", String.valueOf(index)).replace("%gamemode%", gm.toString().toLowerCase()).replace("%player%", player.getName())));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("gmset-help")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)this.messages.getString("gmset-help")));
                }
                return true;
            }
            case "mark": {
                if (sender instanceof Player && !Message.hasPermission((Permissible)((Player)sender), "creativesecurity.cmd.mark")) {
                    return true;
                }
                Player player = this.playerOnly(sender);
                if (player == null) {
                    return false;
                }
                ItemStack stack = CreativeListener.getHand(player);
                if (stack == null || stack.getType() == Material.AIR) {
                    Message.MARK_NOTHING.sendError((CommandSender)player);
                    return true;
                }
                CreativeListener.mark(stack, (HumanEntity)player);
                player.getInventory().setItemInMainHand(stack);
                Message.MARK_SUCCESS.sendInfo((CommandSender)player);
                return true;
            }
            case "unmark": {
                if (sender instanceof Player && !Message.hasPermission((Permissible)((Player)sender), "creativesecurity.cmd.unmark")) {
                    return true;
                }
                Player player = this.playerOnly(sender);
                if (player == null) {
                    return false;
                }
                ItemStack stack = CreativeListener.getHand(player);
                if (stack == null || stack.getType() == Material.AIR) {
                    Message.UNMARK_NOTHING.sendError((CommandSender)player);
                    return true;
                }
                if (!this.creativeListener.isCreative(stack)) {
                    Message.UNMARK_ALREADY.sendError((CommandSender)player);
                    return true;
                }
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<String>(meta.getLore());
                lore.remove(0);
                meta.setLore(lore);
                stack.setItemMeta(meta);
                player.getInventory().setItemInMainHand(stack);
                Message.UNMARK_SUCCESS.sendInfo((CommandSender)player);
                return true;
            }
            case "debugcreativeblocks": {
                if (sender instanceof Player && !Message.hasPermission(sender, "creativesecurity.cmd.debug")) {
                    return true;
                }
                Player player = this.playerOnly(sender);
                if (player == null) {
                    return false;
                }
                boolean removed = this.creativeListener.logBlockBreak.contains(player.getUniqueId());
                if (removed) {
                    this.creativeListener.logBlockBreak.remove(player.getUniqueId());
                } else {
                    this.creativeListener.logBlockBreak.add(player.getUniqueId());
                }
                sender.sendMessage("Debug " + (removed ? "disabled" : "enabled"));
                return true;
            }
        }
        return false;
    }
}

