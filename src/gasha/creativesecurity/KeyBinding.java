package gasha.creativesecurity;

import gasha.creativesecurity.CreativeSecurityPlugin;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class KeyBinding
implements Listener {
    private CreativeSecurityPlugin main;
    private ConsoleCommandSender consoleSender;

    KeyBinding(CreativeSecurityPlugin main) {
        this.main = main;
        this.consoleSender = main.getServer().getConsoleSender();
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        String key = String.valueOf(event.getNewSlot() + 1);
        String configPath = "key-binding.";
        if (player.isSneaking()) {
            configPath = configPath + "shift+";
        }
        configPath = configPath + key;
        ConfigurationSection configSection = this.main.getConfig().getConfigurationSection(configPath);
        if (configSection == null || configSection.getKeys(false).isEmpty()) {
            return;
        }
        String permission = "creativesecurity.keybind.";
        if (player.isSneaking()) {
            permission = permission + "shift+";
        }
        permission = permission + key + ".";
        this.iterateCommands(configSection, player, permission);
    }

    @EventHandler
    public void onPlayerSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        String configPath = "key-binding.";
        if (player.isSneaking()) {
            configPath = configPath + "shift+";
        }
        configPath = configPath + "F";
        ConfigurationSection configSection = this.main.getConfig().getConfigurationSection(configPath);
        if (configSection == null || configSection.getKeys(false).isEmpty()) {
            return;
        }
        String permission = "creativesecurity.keybind.";
        if (player.isSneaking()) {
            permission = permission + "shift+";
        }
        permission = permission + "F.";
        this.iterateCommands(configSection, player, permission);
    }

    private void iterateCommands(ConfigurationSection configSection, Player player, String permission) {
        List<String> allCommands = configSection.getKeys(false).stream().filter(key -> player.hasPermission(permission + key)).flatMap(key -> configSection.getStringList(key).stream()).collect(Collectors.toList());
        if (!allCommands.isEmpty()) {
            allCommands.stream().filter(cmd -> cmd.startsWith("CONSOLE:")).map(cmd -> cmd.replace("CONSOLE:", "")).map(cmd -> cmd.replace("%player%", player.getName())).forEach(cmd -> Bukkit.dispatchCommand((CommandSender)this.consoleSender, (String)cmd));
            allCommands.stream().filter(cmd -> !cmd.startsWith("CONSOLE:")).forEach(((Player)player)::performCommand);
        }
    }
}

