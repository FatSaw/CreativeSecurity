package gasha.commandguard.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FileManager {
    private JavaPlugin javaPlugin;
    private File file;
    private YamlConfiguration configuration;
    private Map<String, Object> cache;
    private int unsavedChanges = 0;
    private int maxUnsavedChanges = 10;

    @Deprecated
    public FileManager(JavaPlugin javaPlugin, String name) {
        new FileManager(javaPlugin, name, 10);
    }

    public FileManager(JavaPlugin javaPlugin, String name, int maxUnsavedChanges) {
        Validate.notNull((Object)javaPlugin, (String)"JavaPlugin cannot be null");
        Validate.notNull((Object)name, (String)"Name cannot be null!");
        this.maxUnsavedChanges = maxUnsavedChanges;
        if (!name.endsWith(".yml")) {
            name = name + ".yml";
        }
        this.javaPlugin = javaPlugin;
        this.file = new File(this.javaPlugin.getDataFolder(), name);
        this.cache = new HashMap<String, Object>();
        if (!this.file.exists()) {
            this.javaPlugin.saveResource(name, false);
        }
        this.configuration = YamlConfiguration.loadConfiguration((File)this.file);
    }

    public List<String> getStringList(String path) {
        List<String> stringList = this.configuration.getStringList(path);
        for (int i = 0; i < stringList.size(); ++i) {
            stringList.set(i, ChatColor.translateAlternateColorCodes((char)'&', (String)((String)stringList.get(i))));
        }
        return stringList;
    }

    public void set(String path, Object obj) {
        this.set(path, obj, false);
    }

    public void set(String path, Object obj, boolean save) {
        this.configuration.set(path, obj);
        if (save) {
            if (this.save()) {
                this.unsavedChanges = 0;
            } else {
                this.javaPlugin.getLogger().log(Level.WARNING, "Config could not be saved for plugin: " + this.javaPlugin.getName() + "!");
                this.javaPlugin.getLogger().log(Level.WARNING, "Please contact plugin owner to fix this! ");
                this.javaPlugin.getLogger().log(Level.WARNING, "Also include the stacktrace shown above!");
            }
        } else {
            ++this.unsavedChanges;
            if (this.unsavedChanges >= this.maxUnsavedChanges) {
                if (this.save()) {
                    this.unsavedChanges = 0;
                } else {
                    this.javaPlugin.getLogger().log(Level.WARNING, "Config could not be saved for plugin: " + this.javaPlugin.getDescription().getName() + "! (Version: " + this.javaPlugin.getDescription().getVersion() + ")");
                    this.javaPlugin.getLogger().log(Level.WARNING, "Please contact plugin owner (" + this.javaPlugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "") + ") to fix this! ");
                    this.javaPlugin.getLogger().log(Level.WARNING, "Also include the stacktrace shown above!");
                }
            }
        }
    }

    public String getString(String path) {
        return this.getString(path, false);
    }

    public String getString(String path, boolean load) {
        String def;
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            def = this.configuration.getString(path);
            this.cache.put(path, def);
        } else {
            Object obj = this.cache.get(path);
            def = obj instanceof String ? (String)obj : this.getString(path, true);
        }
        return def == null ? def : ChatColor.translateAlternateColorCodes((char)'&', (String)def);
    }

    public Location getLocation(String path) {
        return this.getLocation(path, false);
    }

    public Location getLocation(String path, boolean load) {
        Validate.notNull((Object)path, (String)"Path cannot be null");
        Location loc = null;
        if (load) {
            Object obj = this.get(path);
            if (obj instanceof Location) {
                this.cache.put(path, obj);
                loc = (Location)obj;
            }
        } else {
            Object obj = this.cache.get(path);
            if (obj instanceof Location) {
                loc = (Location)obj;
            } else {
                obj = this.get(path);
                if (obj instanceof Location) {
                    this.cache.put(path, obj);
                    loc = (Location)obj;
                }
            }
        }
        return loc;
    }

    public int getInteger(String path) {
        return this.getInteger(path, false, -1);
    }

    public int getInteger(String path, boolean load) {
        return this.getInteger(path, load, -1);
    }

    public int getInteger(String path, boolean load, int def) {
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            def = this.configuration.getInt(path, -1);
            this.cache.put(path, def);
        } else {
            Object obj = this.cache.get(path);
            def = obj instanceof Integer ? ((Integer)obj).intValue() : this.getInteger(path, true);
        }
        return def;
    }

    public double getDouble(String path) {
        return this.getDouble(path, false, -1.0);
    }

    public double getDouble(String path, boolean load) {
        return this.getDouble(path, load, -1.0);
    }

    public double getDouble(String path, boolean load, double def) {
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            def = this.configuration.getDouble(path, -1.0);
            this.cache.put(path, def);
        } else {
            Object obj = this.cache.get(path);
            def = obj instanceof Double ? ((Double)obj).doubleValue() : this.getDouble(path, true);
        }
        return def;
    }

    public Object get(String path) {
        return this.get(path, false);
    }

    public Object get(String path, boolean load) {
        Object obj;
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            obj = this.configuration.get(path);
        } else {
            obj = this.cache.get(path);
            if (obj == null) {
                obj = this.get(path, true);
                this.cache.put(path, obj);
            }
        }
        return obj;
    }

    public List<?> getList(String path) {
        return this.getList(path, false, null);
    }

    public List<?> getList(String path, boolean load) {
        return this.getList(path, load, null);
    }

    public List<?> getList(String path, boolean load, List<?> def) {
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            def = this.configuration.getList(path);
            this.cache.put(path, def);
        } else {
            Object obj = this.cache.get(path);
            def = obj instanceof List ? (List<?>)obj : this.getList(path, true);
        }
        return def;
    }

    public boolean getBoolean(String path) {
        return this.getBoolean(path, false, false);
    }

    public boolean getBoolean(String path, boolean load) {
        return this.getBoolean(path, load, false);
    }

    public boolean getBoolean(String path, boolean load, boolean def) {
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            def = this.configuration.getBoolean(path);
            this.cache.put(path, def);
        } else {
            Object obj = this.cache.get(path);
            def = obj instanceof Boolean ? ((Boolean)obj).booleanValue() : this.getBoolean(path, true);
        }
        return def;
    }

    public ItemStack getItemStack(String path) {
        return this.getItemStack(path, false);
    }

    public ItemStack getItemStack(String path, boolean load) {
        ItemStack itemStack;
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            itemStack = this.configuration.getItemStack(path);
            this.cache.put(path, (Object)itemStack);
        } else {
            Object obj = this.cache.get(path);
            itemStack = obj instanceof ItemStack ? (ItemStack)obj : this.getItemStack(path, true);
        }
        return itemStack;
    }

    public ConfigurationSection getSection(String path) {
        return this.getSection(path, false);
    }

    public ConfigurationSection getSection(String path, boolean load) {
        ConfigurationSection section;
        Validate.notNull((Object)path, (String)"Path cannot be null");
        if (load) {
            section = this.configuration.getConfigurationSection(path);
            this.cache.put(path, (Object)section);
        } else {
            Object obj = this.cache.get(path);
            section = obj instanceof ConfigurationSection ? (ConfigurationSection)obj : this.getSection(path, true);
        }
        return section;
    }

    public YamlConfiguration getConfig() {
        return this.configuration;
    }

    public void resetCache() {
        this.cache.clear();
    }

    public boolean save() {
        if (this.unsavedChanges > 0) {
            try {
                this.configuration.save(this.file);
                this.unsavedChanges = 0;
                return true;
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
                return false;
            }
        }
        return true;
    }
}

