package com.ruinscraft.panilla;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerShield implements Listener {
	
	private PacketInspector packetinspector;
	private InventoryCleaner inventorycleaner;
	private PlayerInjector playerinjector;
	private JavaPlugin plugin;
	
	public ServerShield(JavaPlugin plugin) {
		packetinspector = new PacketInspector(this);
		inventorycleaner = new InventoryCleaner(this);
		playerinjector = new PlayerInjector();
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, (Plugin)plugin);
		for(Player player : Bukkit.getOnlinePlayers()) {
			try {
				playerinjector.register(this, player);
	        } catch (IOException e) {
	        }
		}
	}
	
	protected PacketInspector getPacketInspector() {
		return packetinspector;
	}
	
	protected InventoryCleaner getInventoryCleaner() {
		return inventorycleaner;
	}
	
	protected void exec(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
	
	public void shutdown() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			try {
				playerinjector.unregister(player);
	        } catch (IOException e) {
	        }
		}
	}
	@EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
        	playerinjector.register(this, event.getPlayer());
        } catch (IOException e) {
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        try {
        	playerinjector.unregister(event.getPlayer());
        } catch (IOException e) {
        }
    }

}
