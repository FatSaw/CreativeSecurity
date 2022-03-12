package gasha.creativesecurity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import gasha.creativesecurity.Config;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.Message;
import gasha.creativesecurity.PlayerData;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

public class DataListener
implements Listener {
    private static final boolean LOG_TIMINGS = false;
    private static final boolean LOG_TIMINGS_EXT = false;
    private static final String EXT = ".creativesecurity.inv.yml.gz";
    private final File playerDataFolder = new File(((World)Bukkit.getWorlds().get(0)).getWorldFolder(), "playerdata");
    private static final String KEY_PLAYER_DATA = "inventory_data";
    private final LoadingCache<UUID, Object> playerLock = CacheBuilder.newBuilder().expireAfterAccess(2L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<UUID, Object>(){

        public Object load(UUID key) throws Exception {
            return new Object();
        }
    });
    private final LoadingCache<UUID, Future<Optional<PlayerData>>> loadingData = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build(new CacheLoader<UUID, Future<Optional<PlayerData>>>(){

        public Future<Optional<PlayerData>> load(UUID key) throws Exception {
        	return CreativeSecurityPlugin.getInstance().dataLoadingExecutor.submit(() -> {
                synchronized (DataListener.this.playerLock.get(key)) {
                    if (Config.jdbc) {
                        return PlayerData.loadFromDatabase(key);
                    }
                    else {
                    	File playerFile = DataListener.this.playerFile(key);
                        if (playerFile.isFile()) {
                            try {
                            	FileInputStream fis = new FileInputStream(playerFile);
                                try {
                                	BufferedInputStream bis = new BufferedInputStream(fis);
                                    try {
                                        return Optional.of(PlayerData.loadFromInputStream(bis));
                                    }
                                    catch (Throwable t) {
                                        throw t;
                                    }
                                    finally {
                                        if (bis != null) {
                                        	Throwable t2 = new Throwable();
                                            if (t2 != null) {
                                                try {
                                                    bis.close();
                                                }
                                                catch (Throwable exception) {
                                                    t2.addSuppressed(exception);
                                                }
                                            }
                                        }
                                    }
                                }
                                catch (Throwable t3) {
                                    throw t3;
                                }
                                finally {
                                    if (fis != null) {
                                    	Throwable t4 = new Throwable();
                                        if (t4 != null) {
                                            try {
                                                fis.close();
                                            }
                                            catch (Throwable exception2) {
                                                t4.addSuppressed(exception2);
                                            }
                                        }
                                    }
                                }
                            }
                            catch (Exception e) {
                                File backup = new File(playerFile.getParent(), playerFile.getName() + '.' + System.currentTimeMillis() + ".bak");
                                Logger logger = CreativeSecurityPlugin.getInstance().getLogger();
                                logger.log(Level.SEVERE, "Failed to load the data for player " + key, e);
                                logger.severe("The player data will be backed up to: " + backup);
                                if (!playerFile.renameTo(backup)) {
                                    logger.severe("Failed to rename " + playerFile + " to " + backup);
                                }
                            }
                        }
                        return Optional.empty();
                    }
                }
            });
        }
    });

    File playerFile(UUID playerId) {
        return new File(this.playerDataFolder, playerId.toString().concat(EXT));
    }

    private Optional<PlayerData> getDataFromFuture(Player player) {
        Object value;
        MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)player, KEY_PLAYER_DATA, null);
        Object object = value = metadata == null ? null : metadata.value();
        if (value instanceof PlayerData) {
            return Optional.of((PlayerData)value);
        }
        Future futureData = (Future)this.loadingData.getIfPresent((Object)player.getUniqueId());
        if (futureData == null) {
            return null;
        }
        try {
            return (Optional)futureData.get(2L, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not load the player data for " + player.getName() + " / " + player.getUniqueId(), e);
            return null;
        }
    }

    public PlayerData getData(Player player) {
        Object value;
        MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)player, KEY_PLAYER_DATA, null);
        Object object = value = metadata == null ? null : metadata.value();
        if (value instanceof PlayerData) {
            return (PlayerData)value;
        }
        CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "The player data for " + player.getUniqueId() + " was not available!", new IllegalStateException());
        return this.load(player);
    }

    public Integer switchPlayerInventory(Player player, boolean creative, int index) {
        PlayerData data = this.getData(player);
        return data.switchPlayerInventory(player, creative ? GameMode.CREATIVE : GameMode.SURVIVAL, index);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onPreLogin(AsyncPlayerPreLoginEvent event) {
        this.loadPlayerData(event.getUniqueId(), ((AsyncPlayerPreLoginEvent)event)::getName);
    }

    Optional<PlayerData> loadPlayerData(UUID playerUUID, Supplier<String> playerName) {
        try {
            return (Optional<PlayerData>)(this.loadingData.get(playerUUID)).get(15L, TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load the data for " + playerName.get() + " / " + playerUUID, e);
            return Optional.empty();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onLogin(PlayerJoinEvent event) {
        PlayerData data;
        Optional<PlayerData> result;
        Player player = event.getPlayer();
        try {
            Future<Optional<PlayerData>> future = this.loadingData.get(player.getUniqueId());
            if (future.isDone()) {
                result = future.get();
            } else {
                try {
                    result = future.get(100L, TimeUnit.MILLISECONDS);
                }
                catch (TimeoutException e) {
                    result = null;
                }
            }
        }
        catch (ExecutionException e) {
            CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load the data for " + player.getName() + " / " + player.getUniqueId(), e);
            player.kickPlayer(Message.ERR_INVENTORY_DATA.applyArgs((HumanEntity)player, new String[0][]).toString());
            this.loadingData.invalidate((Object)player.getUniqueId());
            return;
        }
        catch (InterruptedException e) {
            CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load the data for " + player.getName() + " / " + player.getUniqueId(), e);
            player.kickPlayer(Message.ERR_INVENTORY_DATA.applyArgs((HumanEntity)player, new String[0][]).toString());
            return;
        }
        if (result != null && result.isPresent()) {
            data = (PlayerData)result.get();
            try {
                data.postLoad(player);
            }
            catch (Exception e) {
                CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to execute postLoad to the player data for " + (Object)player, e);
                player.kickPlayer(Message.ERR_INVENTORY_DATA.applyArgs((HumanEntity)player, new String[0][]).toString());
                this.loadingData.invalidate((Object)player.getUniqueId());
                return;
            }
        } else {
            data = new PlayerData(player);
        }
        player.setMetadata(KEY_PLAYER_DATA, (MetadataValue)new FixedMetadataValue((Plugin)CreativeSecurityPlugin.getInstance(), (Object)data));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    PlayerData load(Player player) {
        Object object = this.getPlayerLock(player.getUniqueId());
        synchronized (object) {
            PlayerData data;
            block31: {
                File playerFile = this.playerFile(player.getUniqueId());
                data = null;
                if (playerFile.isFile()) {
                    try (FileInputStream fis = new FileInputStream(playerFile);
                         BufferedInputStream bis = new BufferedInputStream(fis);){
                        data = PlayerData.loadFromInputStream(bis);
                        data.postLoad(player);
                    }
                    catch (Exception e) {
                        File backup = new File(playerFile.getParent(), playerFile.getName() + '.' + System.currentTimeMillis() + ".bak");
                        Logger logger = CreativeSecurityPlugin.getInstance().getLogger();
                        logger.log(Level.SEVERE, "Failed to load the data for player " + player.getName() + " / " + player.getUniqueId(), e);
                        logger.severe("The player data will be backed up to: " + backup);
                        if (playerFile.renameTo(backup)) break block31;
                        logger.severe("Failed to rename " + playerFile + " to " + backup);
                    }
                }
            }
            if (data == null) {
                data = new PlayerData(player);
            }
            player.setMetadata(KEY_PLAYER_DATA, (MetadataValue)new FixedMetadataValue((Plugin)CreativeSecurityPlugin.getInstance(), (Object)data));
            return data;
        }
    }

    private Object getPlayerLock(UUID uuid) {
        try {
            return this.playerLock.get(uuid);
        }
        catch (ExecutionException e) {
            throw new UncheckedExecutionException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void unload(Player player) {
        Object object = this.getPlayerLock(player.getUniqueId());
        synchronized (object) {
            MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)player, KEY_PLAYER_DATA, null);
            if (metadata == null) {
                return;
            }
            player.removeMetadata(KEY_PLAYER_DATA, (Plugin)CreativeSecurityPlugin.getInstance());
            this.saveData(player, (PlayerData)metadata.value());
        }
    }

    void saveData(Player player, PlayerData data) {
        data.preSave(player);
        this.saveDataForOfflinePlayer(player.getUniqueId(), player.getName(), data);
    }

    public void saveDataForOfflinePlayer(UUID playerUUID, String playerName, PlayerData data) {
        if (Config.jdbc) {
            try {
                data.saveToDatabase();
            }
            catch (SQLException e) {
                CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to save the data for player " + playerName + " / " + playerUUID, e);
            }
        } else {
            File playerFile = this.playerFile(playerUUID);
            File playerFileTmp = new File(playerFile.getParent(), playerFile.getName() + ".tmp");
            try (FileOutputStream fos = new FileOutputStream(playerFileTmp);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 GZIPOutputStream gz = new GZIPOutputStream(bos);){
                data.saveToOutputStream(gz);
            }
            catch (IOException e) {
                CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to save the data for player " + playerName + " / " + playerUUID, e);
            }
            if (playerFile.isFile() && !playerFile.delete()) {
                CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to delete: " + playerFile);
            }
            if (!playerFileTmp.renameTo(playerFile)) {
                CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to rename: " + playerFileTmp + " to " + playerFile);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnline()) {
            CreativeSecurityPlugin.getInstance().getLogger().warning("The gamemode of " + player.getName() + " (" + player.getUniqueId() + ") has been changed when the player was not online. \nNo command trigger will be executed and the player inventory will be switched to the appropriated " + (Object)event.getNewGameMode() + " on join.");
            return;
        }
        if (event.getNewGameMode() == GameMode.SPECTATOR && player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        try {
            boolean toCreative;
            boolean fromCreative = player.getGameMode() == GameMode.CREATIVE;
            boolean bl = toCreative = event.getNewGameMode() == GameMode.CREATIVE;
            if (fromCreative != toCreative) {
                Optional<PlayerData> data = this.getDataFromFuture(player);
                if (data == null) {
                    return;
                }
                if (!CreativeSecurityPlugin.getInstance().getConfig().getBoolean("disable-gamemode-changes")) {
                    data.ifPresent(d -> d.gameModeChanged(player.getInventory(), toCreative));
                }
            }
        }
        finally {
            Config.onGameModeChange(player, player.getGameMode(), event.getNewGameMode());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Config.miscChangeToDefaultGameModeOnQuit) {
            GameMode defaultGameMode = Bukkit.getDefaultGameMode();
            player.setGameMode(defaultGameMode);
        } else {
            GameMode gameMode = Config.miscChangeToSpecificGameModeOnQuit;
            if (gameMode != null) {
                player.setGameMode(gameMode);
            }
        }
        MetadataValue metadata = CreativeSecurityPlugin.getMetadata((Metadatable)player, KEY_PLAYER_DATA, null);
        if (metadata == null) {
            return;
        }
        player.removeMetadata(KEY_PLAYER_DATA, (Plugin)CreativeSecurityPlugin.getInstance());
        ListenableFuture<Optional<PlayerData>> myFuture = Futures.immediateFuture(Optional.of((PlayerData)metadata.value()));
        this.loadingData.put(player.getUniqueId(), myFuture);
        CreativeSecurityPlugin.getInstance().dataLoadingExecutor.submit(() -> {
            Object object = this.getPlayerLock(player.getUniqueId());
            synchronized (object) {
                this.saveData(player, (PlayerData)metadata.value());
            }
        });
    }

    static void logTime(StringBuilder prefix, long start, long end) {
        long ns = end - start;
        NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
        CreativeSecurityPlugin.getInstance().getLogger().info(prefix + format.format(ns) + " nanoseconds (" + format.format(TimeUnit.NANOSECONDS.toMillis(ns)) + "ms)");
    }

    static /* synthetic */ LoadingCache access$000(DataListener x0) {
        return x0.playerLock;
    }
}

