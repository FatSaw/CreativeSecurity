package gasha.creativesecurity.guis;

import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.guis.GuiUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class GuiConfig {
    private File file;
    private FileConfiguration config;

    public GuiConfig() {
        CreativeSecurityPlugin main = CreativeSecurityPlugin.getInstance();
        this.file = new File(main.getDataFolder(), "guis.yml");
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdir();
        }
        if (!this.file.exists()) {
            CreativeSecurityPlugin.getInstance().saveResource("guis.yml", false);
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration((File)this.file);
        GuiUtil.loadStuff();
    }

    public String getString(String path) {
        return this.color(this.config.getString(path));
    }

    public List<String> getStringList(String path) {
        ArrayList<String> colored = new ArrayList<String>();
        this.config.getStringList(path).forEach(str -> colored.add(this.color((String)str)));
        return colored;
    }

    private String color(String str) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)str);
    }
}

