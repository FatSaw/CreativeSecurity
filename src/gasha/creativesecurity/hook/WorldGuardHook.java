package gasha.creativesecurity.hook;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook {
    private WorldGuardPlugin worldGuardPlugin;
    private WorldGuardPlatform worldGuard;
    private boolean installed;

    public WorldGuardHook() {
    	this.worldGuard = WorldGuard.getInstance().getPlatform();
        this.worldGuardPlugin = (WorldGuardPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        this.installed = this.worldGuardPlugin != null;
    }

    public boolean isInstalled() {
        return this.installed;
    }

    public RegionManager getRegionManager(org.bukkit.World world) {
        return this.worldGuard.getRegionContainer().get((World)new BukkitWorld(world));
    }

    public boolean canBuild(Player player, Location location) {
        if (!player.isOp() && !player.hasPermission("*")) {
        	com.sk89q.worldedit.util.Location worldEditLocation = new com.sk89q.worldedit.util.Location(new BukkitWorld(location.getWorld()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            return this.worldGuard.getRegionContainer().createQuery().testState(worldEditLocation, this.worldGuardPlugin.wrapPlayer(player), new StateFlag[]{Flags.BUILD});
        }
        return true;
    }
}

