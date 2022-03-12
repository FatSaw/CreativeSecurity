package gasha.creativesecurity;

import gasha.creativesecurity.AdditionalInventory;
import gasha.creativesecurity.Config;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.SqlConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerData
implements ConfigurationSerializable {
    private final UUID playerId;
    private final AdditionalInventory[] survivalInventories = new AdditionalInventory[10];
    private final AdditionalInventory[] creativeInventories = new AdditionalInventory[10];
    private int currentSurvival = 0;
    private int currentCreative = 0;
    private boolean lastCreative = false;
    private boolean switchingInventory = false;

    private void saveToDatabase(Connection connection) throws SQLException {
        try (CallableStatement cs = connection.prepareCall("{call creativesecurity_save_playerdata(?,?,?,?,?,?)}");){
            cs.setBytes(1, SqlConfig.uuid(this.playerId));
            cs.setInt(2, this.currentSurvival);
            cs.setInt(3, this.currentCreative);
            cs.setBoolean(4, this.lastCreative);
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("survival-inventories", Arrays.asList(this.survivalInventories));
            String serialized = yaml.saveToString();
            cs.setString(5, serialized);
            yaml = new YamlConfiguration();
            yaml.set("creative-inventories", Arrays.asList(this.creativeInventories));
            serialized = yaml.saveToString();
            cs.setString(6, serialized);
            cs.executeUpdate();
        }
    }

    static Optional<PlayerData> loadFromDatabase(UUID playerId) throws SQLException {
        try (PreparedStatement pst = SqlConfig.getConnection().prepareStatement("SELECT current_survival, current_creative, last_creative, creative_inventories, survival_inventories FROM creativesecurity_playerdata WHERE player_id=?");){
            pst.setBytes(1, SqlConfig.uuid(playerId));
            ResultSet result = pst.executeQuery();
            if (!result.next()) {
                Optional<PlayerData> optional = Optional.empty();
                return optional;
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration((Reader)new StringReader(result.getString("survival_inventories")));
            List survivalInventories = (List)yaml.get("survival-inventories");
            yaml = YamlConfiguration.loadConfiguration((Reader)new StringReader(result.getString("creative_inventories")));
            List creativeInventories = (List)yaml.get("creative-inventories");
            PlayerData playerData = new PlayerData(playerId, survivalInventories, creativeInventories);
            playerData.currentSurvival = result.getInt("current_survival");
            playerData.currentCreative = result.getInt("current_creative");
            playerData.lastCreative = result.getBoolean("last_creative");
            Optional<PlayerData> optional = Optional.of(playerData);
            return optional;
        }
    }

    void saveToDatabase() throws SQLException {
        Connection connection = SqlConfig.getConnection();
        boolean before = connection.getAutoCommit();
        connection.setAutoCommit(false);
        Savepoint savepoint = connection.setSavepoint();
        try {
            this.saveToDatabase(connection);
        }
        catch (Throwable e) {
            try {
                connection.rollback(savepoint);
            }
            catch (Throwable e2) {
                e.addSuppressed(e2);
            }
            throw e;
        }
        finally {
            connection.releaseSavepoint(savepoint);
            connection.setAutoCommit(before);
        }
    }

    private boolean isSameInventory(boolean isCreative, int index) {
        return this.lastCreative == isCreative && (isCreative ? this.currentCreative == index : this.currentSurvival == index);
    }

    public static String getName(GameMode gamemode) {
        if (gamemode == GameMode.CREATIVE) {
            return "creative";
        }
        return "survival";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer switchPlayerInventory(Player player, GameMode gameMode, int index) {
        boolean isCreative;
        if (player.hasPermission("creativesecurity.cmd.keep.*") || player.hasPermission("creativesecurity.cmd.keep." + PlayerData.getName(gameMode))) {
            return 2;
        }
        boolean bl = isCreative = gameMode == GameMode.CREATIVE;
        if (this.isSameInventory(isCreative, index) && !Config.miscAllowForcingAlreadyUsedInv) {
            return 0;
        }
        PlayerInventory inventory = player.getInventory();
        if (!this.checkCreativeStatus(player)) {
            this.getCurrent().read(inventory);
        }
        if (isCreative) {
            this.lastCreative = true;
            this.currentCreative = index;
        } else {
            this.lastCreative = false;
            this.currentSurvival = index;
        }
        try {
            this.switchingInventory = true;
            player.setGameMode(gameMode);
            try {
                List<String> commands = CreativeSecurityPlugin.getInstance().getConfig().getStringList("execute-on-inventory-join." + gameMode.name().toLowerCase() + "-" + (index + 1));
                if (!commands.isEmpty()) {
                    commands.forEach(cmd -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), cmd.replace("{player_name}", player.getName()).replace("{player_uuid}", player.getUniqueId().toString())));
                }
            }
            catch (NullPointerException nullPointerException) {
                // empty catch block
            }
        }
        finally {
            this.switchingInventory = false;
        }
        this.getCurrent().apply(player.getInventory());
        return 1;
    }

    void gameModeChanged(PlayerInventory inventory, boolean creative) {
        if (this.switchingInventory) {
            return;
        }
        this.getCurrent().read(inventory);
        this.lastCreative = creative;
        this.getCurrent().apply(inventory);
    }

    private boolean checkCreativeStatus(Player player) {
        boolean isCreative;
        boolean bl = isCreative = player.getGameMode() == GameMode.CREATIVE;
        if (isCreative != this.lastCreative) {
            Logger log = CreativeSecurityPlugin.getInstance().getLogger();
            if (isCreative) {
                log.info("Applying the creative inventory #" + this.currentCreative + " to the player");
                this.getCreative(this.currentCreative).apply(player.getInventory());
                this.lastCreative = true;
            } else {
                log.info("Applying the survival inventory #" + this.currentSurvival + " to the player");
                this.getSurvival(this.currentSurvival).apply(player.getInventory());
                this.lastCreative = false;
            }
            return true;
        }
        return false;
    }

    void preSave(Player player) {
        this.getCurrent().read(player.getInventory());
        this.checkCreativeStatus(player);
    }

    void postLoad(Player player) {
        if (!this.playerId.equals(player.getUniqueId())) {
            throw new IllegalStateException("The loaded data is for a different player. PlayerData: " + this.playerId + " Player: " + player.getUniqueId());
        }
        if (this.checkCreativeStatus(player)) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        if (player.getGameMode() == GameMode.CREATIVE) {
            this.getCreative(this.currentCreative).apply(inventory);
        } else {
            this.getSurvival(this.currentSurvival).apply(inventory);
        }
    }

    public PlayerData(Player player) {
        this.playerId = player.getUniqueId();
        if (player.getGameMode() == GameMode.CREATIVE) {
            this.creativeInventories[0] = new AdditionalInventory(player);
            this.lastCreative = true;
        } else {
            this.survivalInventories[0] = new AdditionalInventory(player);
            this.lastCreative = false;
        }
    }

    public PlayerData(UUID playerId, List<AdditionalInventory> survivalInventories, List<AdditionalInventory> creativeInventories) {
        int i;
        if (survivalInventories.size() > this.survivalInventories.length) {
            throw new IllegalArgumentException("Too many survival inventories. Expected up to 10, got " + survivalInventories.size());
        }
        if (creativeInventories.size() > this.creativeInventories.length) {
            throw new IllegalArgumentException("Too many creative inventories. Expected up to 10, got " + creativeInventories.size());
        }
        this.playerId = playerId;
        for (i = 0; i < 10; ++i) {
            this.survivalInventories[i] = survivalInventories.get(i);
        }
        for (i = 0; i < 10; ++i) {
            this.creativeInventories[i] = creativeInventories.get(i);
        }
    }

    private static byte[] compress(byte[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gz = new GZIPOutputStream(bos);){
            gz.write(data);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    private static byte[] decompress(byte[] data) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPInputStream gz = new GZIPInputStream(bis);){
            int read;
            byte[] buff = new byte[1024];
            while ((read = gz.read(buff)) >= 0) {
                bos.write(buff, 0, read);
            }
        }
        return bos.toByteArray();
    }

    YamlConfiguration saveToYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("file-version", (Object)1);
        yaml.set("data", (Object)this);
        return yaml;
    }

    byte[] saveToUncompressedBytes() {
        YamlConfiguration yaml = this.saveToYaml();
        String str = yaml.saveToString();
        return str.getBytes(StandardCharsets.UTF_8);
    }

    String saveToBase64() {
        return Base64.getEncoder().encodeToString(PlayerData.compress(this.saveToUncompressedBytes()));
    }

    ItemStack saveToItem() {
        String base64 = this.saveToBase64();
        if (base64.length() > 16000) {
            throw new UnsupportedOperationException("The compressed data is too big. It exceeds the 16KB limit by Minecraft. Data length: " + base64.length());
        }
        CharSequence[] parts = new String[(int)Math.ceil((double)base64.length() / 320.0)];
        for (int i = 0; i < parts.length; ++i) {
            parts[i] = base64.substring(i * 320, Math.min(base64.length(), (i + 1) * 320));
        }
        assert (String.join((CharSequence)"", parts).equals(base64));
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta itemMeta = (BookMeta)stack.getItemMeta();
        itemMeta.addPage((String[])parts);
        stack.setItemMeta((ItemMeta)itemMeta);
        assert (String.join((CharSequence)"", ((BookMeta)stack.getItemMeta()).getPages()).equals(base64));
        return stack;
    }

    static PlayerData loadFromBase64(String base64) throws InvalidConfigurationException, IOException {
        return PlayerData.loadFromUncompressedBytes(PlayerData.decompress(Base64.getDecoder().decode(base64)));
    }

    void saveToOutputStream(GZIPOutputStream out) throws IOException {
        YamlConfiguration yaml = this.saveToYaml();
        OutputStreamWriter writer = new OutputStreamWriter((OutputStream)out, StandardCharsets.UTF_8);
        writer.write(yaml.saveToString());
        writer.flush();
    }

    static PlayerData loadFromInputStream(InputStream is) throws IOException {
        return PlayerData.loadFromYaml(YamlConfiguration.loadConfiguration((Reader)new InputStreamReader((InputStream)new GZIPInputStream(is), StandardCharsets.UTF_8)));
    }

    static PlayerData loadFromUncompressedBytes(byte[] data) throws InvalidConfigurationException {
        String str = new String(data, StandardCharsets.UTF_8);
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.loadFromString(str);
        return PlayerData.loadFromYaml(yaml);
    }

    static PlayerData loadFromYaml(YamlConfiguration yaml) {
        int version = yaml.getInt("file-version", -100);
        if (version != 1) {
            throw new UnsupportedOperationException("Expected file-version to be 1 but got " + version + ", this file might have been generated by a future version and cannot be loaded by this version, please update the plugin.");
        }
        return (PlayerData)yaml.get("data");
    }

    static PlayerData loadFromItem(ItemStack stack) throws InvalidConfigurationException, IOException {
        BookMeta itemMeta = (BookMeta)stack.getItemMeta();
        String base64 = String.join((CharSequence)"", itemMeta.getPages());
        return PlayerData.loadFromBase64(base64);
    }

    public AdditionalInventory getCreative(int index) {
        AdditionalInventory inventory = this.creativeInventories[index];
        if (inventory != null) {
            return inventory;
        }
        this.creativeInventories[index] = inventory = new AdditionalInventory(true);
        return inventory;
    }

    public AdditionalInventory getSurvival(int index) {
        AdditionalInventory inventory = this.survivalInventories[index];
        if (inventory != null) {
            return inventory;
        }
        this.survivalInventories[index] = inventory = new AdditionalInventory(false);
        return inventory;
    }

    public AdditionalInventory getCurrent() {
        if (this.lastCreative) {
            return this.getCreative(this.currentCreative);
        }
        return this.getSurvival(this.currentSurvival);
    }

    public int getCurrentIndex() {
        if (this.lastCreative) {
            return this.getCurrentCreative();
        }
        return this.getCurrentSurvival();
    }

    public int getCurrentCreative() {
        return this.currentCreative;
    }

    public int getCurrentSurvival() {
        return this.currentSurvival;
    }

    public AdditionalInventory getAdditionalInventory(GameMode gameMode, int number) {
        if (gameMode == GameMode.CREATIVE) {
            return this.getCreative(number);
        }
        if (gameMode == GameMode.SURVIVAL) {
            return this.getSurvival(number);
        }
        return null;
    }

    public static PlayerData deserialize(Map<String, Object> data) {
        PlayerData playerData = new PlayerData(UUID.fromString((String)data.get("UUID")), (List)data.get("survival"), (List)data.get("creative"));
        playerData.lastCreative = (Boolean)data.get("last-creative");
        playerData.currentSurvival = (Integer)data.get("current-survival");
        playerData.currentCreative = (Integer)data.get("current-creative");
        return playerData;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("UUID", this.playerId.toString());
        result.put("current-survival", this.currentSurvival);
        result.put("current-creative", this.currentCreative);
        result.put("last-creative", this.lastCreative);
        result.put("survival", Arrays.asList(this.survivalInventories));
        result.put("creative", Arrays.asList(this.creativeInventories));
        return result;
    }

    public boolean isCreativeMode() {
        return this.lastCreative;
    }
}

