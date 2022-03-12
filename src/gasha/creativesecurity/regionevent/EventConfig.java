package gasha.creativesecurity.regionevent;

import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.regionevent.MessageUT;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EventConfig {
    public FileConfiguration config = null;
    private final File file;

    public EventConfig(JavaPlugin plugin, String filen) {
        this.file = new File(plugin.getDataFolder(), filen + ".yml");
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdir();
        }
        if (!this.file.exists()) {
            CreativeSecurityPlugin.getInstance().saveResource(filen + ".yml", false);
        }
        this.reload();
    }

    public void reload() {
        try {
            this.config = YamlConfiguration.loadConfiguration((File)this.file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.config.save(this.file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getStrList(String path, List<String> def) {
        return this.getStrList(path, def, this.config, this.config, this.file);
    }

    public List<String> getStrList(String path) {
        return this.getStrList(path, this.config);
    }

    public String getStr(String path, String def) {
        return this.getStr(path, def, this.config, this.config, this.file);
    }

    public String getStr(String path) {
        return this.getStr(path, this.config);
    }

    public double getDouble(String path, double v) {
        return this.getDouble(path, v, this.config, this.file);
    }

    public int getInt(String path, int def) {
        return this.getInt(path, def, this.config, this.file);
    }

    public Boolean getBool(String path, boolean def) {
        return this.getBool(path, def, this.config, this.file);
    }

    public Boolean getBool(String path) {
        return this.getBool(path, this.config);
    }

    public String getStr(String path, String def, FileConfiguration config, FileConfiguration defaultc, File file) {
        if (config.getString(path, def = MessageUT.t(def)).equalsIgnoreCase(def)) {
            if (defaultc.get(path) == null) {
                config.set(path, (Object)MessageUT.u(def));
            } else {
                config.set(path, defaultc.get(path));
                def = (String)defaultc.get(path);
            }
            try {
                config.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return def;
        }
        return config.getString(path, def);
    }

    public List<String> getStrList(String path, FileConfiguration config) {
        return config.getStringList(path);
    }

    public List<String> getStrList(String path, List<String> def, FileConfiguration config, FileConfiguration defaultc, File file) {
        if (config.getStringList(path).isEmpty()) {
            if (defaultc.getStringList(path).isEmpty()) {
                config.set(path, def);
            } else {
                config.set(path, (Object)defaultc.getStringList(path));
                def = defaultc.getStringList(path);
            }
            try {
                config.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return def;
        }
        return config.getStringList(path);
    }

    public int getInt(String path, int def, FileConfiguration config, File file) {
        if (config.get(path) == null) {
            config.set(path, (Object)def);
            try {
                config.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return def;
        }
        return config.getInt(path, def);
    }

    public String getStr(String path, FileConfiguration config) {
        return MessageUT.t(config.getString(path));
    }

    public int getInt(String path, FileConfiguration config) {
        return config.getInt(path);
    }

    public double getDouble(String path, double def, FileConfiguration config, File file) {
        if (config.get(path) == null) {
            config.set(path, (Object)def);
            try {
                config.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return def;
        }
        return config.getDouble(path, def);
    }

    public Boolean getBool(String path, FileConfiguration config) {
        return config.getBoolean(path);
    }

    public Boolean getBool(String path, Boolean def, FileConfiguration config, File file) {
        if (config.get(path) == null) {
            config.set(path, (Object)def);
            try {
                config.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return def;
        }
        return config.getBoolean(path, def.booleanValue());
    }
}

