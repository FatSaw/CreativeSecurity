package gasha.creativesecurity.regionevent;

import gasha.creativesecurity.CreativeSecurityPlugin;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBar {
    public static boolean works = true;
    static CreativeSecurityPlugin plugin;

    public static void setup() {
        plugin = CreativeSecurityPlugin.getInstance();
    }

    public static void sendActionBar(Player player, String message) {
        if (!player.isOnline()) {
            return;
        }
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), ChatMessageType.GAME_INFO);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendActionBar(final Player player, final String message, int duration) {
        ActionBar.sendActionBar(player, message);
        if (duration >= 0) {
            new BukkitRunnable() {
                public void run() {
                    ActionBar.sendActionBar(player, "");
                }
            }.runTaskLater(plugin, (duration + 1));
        }
        while (duration > 60) {
            int sched = (duration -= 60) % 60;
            new BukkitRunnable() {
                public void run() {
                    ActionBar.sendActionBar(player, message);
                }
            }.runTaskLater(plugin, sched);
        }
    }

    public static void sendActionBarToAllPlayers(String message) {
        ActionBar.sendActionBarToAllPlayers(message, -1);
    }

    public static void sendActionBarToAllPlayers(String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ActionBar.sendActionBar(p, message, duration);
        }
    }
}

