package gasha.creativesecurity.files;

import gasha.creativesecurity.CreativeSecurityPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLDataStorage {
    private File configFile;
    private YamlConfiguration config;

    public YAMLDataStorage(String fileName) {
        fileName = fileName + ".yml";
        this.configFile = new File(CreativeSecurityPlugin.getInstance().getDataFolder() + File.separator + "internal" + File.separator + fileName);
        this.load();
    }

    protected void load() {
        this.config = YamlConfiguration.loadConfiguration((File)this.configFile);
    }

    public void set(String path, Object value) {
        this.setWithoutSaving(path, value);
        this.save();
    }

    public void setWithoutSaving(String path, Object value) {
        this.config.set(path, value);
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public String getString(String path) {
        return this.getConfig().getString(path);
    }

    public List<String> getStringList(String path) {
        return getConfig().getStringList(path);
    }

    public void addToList(String path, String value) {
        List<String> configList;
        try {
            configList = this.getConfig().getStringList(path);
        }
        catch (NullPointerException ex) {
            configList = new ArrayList<String>();
        }
        configList.add(value);
        this.config.set(path, configList);
        this.save();
    }

    public void removeFromList(String path, String value) {
        List<String> configList;
        try {
            configList = this.getConfig().getStringList(path);
        }
        catch (NullPointerException ex) {
            return;
        }
        configList.remove(value);
        this.config.set(path, (Object)configList);
        this.save();
    }

    public File getFile() {
        return this.configFile;
    }

    public void resetFile() {
        if (!this.getConfig().getKeys(false).isEmpty()) {
            this.getConfig().getKeys(false).forEach(section -> this.getConfig().set(section, null));
        }
    }

    public void save() {
        try {
            this.config.save(this.configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

