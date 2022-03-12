package gasha.commandguard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import gasha.commandguard.listeners.CommandListener;
import gasha.commandguard.listeners.TeleportListener;
import gasha.commandguard.manager.Category;
import gasha.commandguard.manager.CategoryExecution;
import gasha.commandguard.manager.FileManager;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.hook.WorldGuardHook;
import gasha.creativesecurity.regionevent.EventManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class CommandGuard {
    private CreativeSecurityPlugin main;
    private WorldGuardHook worldGuardHook;
    private static final Set<String> blockedCommands = Sets.newHashSet();
    private FileManager config;
    private FileManager teleport;
    private ProtocolManager manager;
    private Set<Category> categories;
    private Set<CategoryExecution> execution;

    public CommandGuard(CreativeSecurityPlugin main) {
        this.main = main;
    }

    public void initialize() {
        this.main.getLogger().info("Initializing CommandGuard...");
        if (this.main.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.manager = ProtocolLibrary.getProtocolManager();
            this.manager.addPacketListener((PacketListener)new PacketAdapter((Plugin)this.main, ListenerPriority.NORMAL, new PacketType[]{PacketType.Play.Client.TAB_COMPLETE}){

                public void onPacketReceiving(PacketEvent event) {
                    if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
                        String check = ((String)event.getPacket().getStrings().read(0)).toLowerCase();
                        if (check.contains(" ")) {
                            check = check.split(" ")[0];
                        }
                        try {
                            if (!event.getPlayer().hasPermission("creativesecurity.commandguard.bypass.tab") && (blockedCommands.contains(check) || check.equals("/"))) {
                                event.setCancelled(true);
                            }
                        }
                        catch (UnsupportedOperationException ex) {
                            CommandGuard.this.main.getLogger().info("A temporary player tab-completed something");
                        }
                    }
                }
            });
        }
        this.worldGuardHook = this.main.getWorldGuardHook();
        this.main.getServer().getPluginManager().registerEvents((Listener)new CommandListener(this), (Plugin)this.main);
        this.main.getServer().getPluginManager().registerEvents((Listener)new TeleportListener(this), (Plugin)this.main);
        this.main.getLogger().info("CommandGuard initialized!");
    }

    public CreativeSecurityPlugin getCreativeSecurityInstance() {
        return this.main;
    }

    public void reloadPlugin() {
        if (this.config != null) {
            this.config.save();
        }
        this.config = new FileManager(this.main, "commandguard-config.yml", 10);
        if (this.teleport != null) {
            this.teleport.save();
        }
        this.teleport = new FileManager(this.main, "execution-on-player.yml", 10);
        if (this.categories != null) {
            this.categories.clear();
            this.execution.clear();
        } else {
            this.categories = Sets.newHashSet();
            this.execution = Sets.newHashSet();
        }
        this.config.getSection("category").getKeys(false).forEach(name -> this.categories.add(new Category(this, (String)name)));
        this.teleport.getSection("execution").getKeys(false).forEach(name -> this.execution.add(new CategoryExecution(this, (String)name)));
    }

    public Set<Category> getCatagories() {
        return this.categories;
    }

    public Set<CategoryExecution> getExecution() {
        return this.execution;
    }

    public void disable() {
        if (this.manager != null) {
            this.manager.removePacketListeners((Plugin)this.main);
        }
        this.categories.clear();
        this.config.save();
    }

    public FileManager getTeleport() {
        return this.teleport;
    }

    public FileManager getConf() {
        return this.config;
    }

    public boolean isWithinWorldGuardRegion(Location loc, List<String> regions) {
        ArrayList<String> regionsAtLocation = new ArrayList<String>();
        ApplicableRegionSet applicableRegionSet = EventManager.getWGRegions(loc);
        applicableRegionSet.forEach(reg -> regionsAtLocation.add(reg.getId()));
        return !Collections.disjoint(regionsAtLocation, regions);
    }

    public boolean isWithinResidenceRegion(Location location, List<String> regions) {
        String region = this.getResidenceRegion(location);
        if (region != null) {
            return regions.contains(region);
        }
        return false;
    }

    private String getResidenceRegion(Location loc) {
        return EventManager.getResidenceRegion(loc);
    }

    static {
        blockedCommands.add("/");
        blockedCommands.add("/pl ");
        blockedCommands.add("/plugins ");
        blockedCommands.add("/about ");
        blockedCommands.add("/ver ");
        blockedCommands.add("/version ");
        blockedCommands.add("/help ");
        blockedCommands.add("/ehelp ");
        blockedCommands.add("/bukkit:pl ");
        blockedCommands.add("/bukkit:plugins ");
        blockedCommands.add("/bukkit:about ");
        blockedCommands.add("/bukkit:ver ");
        blockedCommands.add("/bukkit:version ");
        blockedCommands.add("/bukkit:help ");
        blockedCommands.add("/pl");
        blockedCommands.add("/plugins");
        blockedCommands.add("/about");
        blockedCommands.add("/ver");
        blockedCommands.add("/version");
        blockedCommands.add("/help");
        blockedCommands.add("/ehelp");
        blockedCommands.add("/bukkit:pl");
        blockedCommands.add("/bukkit:plugins");
        blockedCommands.add("/bukkit:about");
        blockedCommands.add("/bukkit:ver");
        blockedCommands.add("/bukkit:version");
        blockedCommands.add("/bukkit:help");
        blockedCommands.add("/essentials:ehelp ");
        blockedCommands.add("/essentials:ehelp");
        blockedCommands.add("/essentials:help ");
        blockedCommands.add("/essentials:help");
    }
}

