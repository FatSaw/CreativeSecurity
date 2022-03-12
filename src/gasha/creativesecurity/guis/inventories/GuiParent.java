package gasha.creativesecurity.guis.inventories;

import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.DataListener;
import gasha.creativesecurity.guis.GuiConfig;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

class GuiParent {
    private GuiConfig config;
    private DataListener dataListener;
    private Player player;
    private String path;
    protected Inventory inventory;
    private static String inventoryTitle;

    GuiParent(Player player, DataListener dataListener, String path, String inventoryTitlePath, int inventorySize) {
        this.player = player;
        this.dataListener = dataListener;
        this.path = path + ".";
        this.config = CreativeSecurityPlugin.getInstance().getGuiConfig();
        inventoryTitle = this.getString(inventoryTitlePath);
        this.inventory = Bukkit.createInventory(null, (int)inventorySize, (String)this.getTitle());
    }

    public void open() {
        this.player.closeInventory();
        this.player.openInventory(this.inventory);
    }

    public String getTitle() {
        return inventoryTitle;
    }

    protected Player getPlayer() {
        return this.player;
    }

    DataListener getDataListener() {
        return this.dataListener;
    }

    protected GuiConfig getConfig() {
        return this.config;
    }

    protected String getString(String configPath) {
        return this.config.getString(this.path + configPath);
    }

    protected List<String> getStringList(String configPath) {
        return this.config.getStringList(this.path + configPath);
    }
}

