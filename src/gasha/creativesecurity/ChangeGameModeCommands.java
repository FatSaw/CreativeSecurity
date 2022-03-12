package gasha.creativesecurity;

import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.Message;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

class ChangeGameModeCommands {
    private List<String> runAsPlayer = Collections.emptyList();
    private List<String> runAsConsole = Collections.emptyList();
    private List<String> permissions = Collections.emptyList();
    private List<String> runWithPermissions = Collections.emptyList();
    private List<String> runAsOP = Collections.emptyList();

    boolean isEmpty() {
        return this.runAsPlayer.isEmpty() && this.runAsConsole.isEmpty() && this.runWithPermissions.isEmpty() && this.runAsOP.isEmpty();
    }

    private static List<String> nonEmptyLines(List<String> list) {
        return list.stream().map(String::trim).filter(line -> !line.isEmpty()).collect(Collectors.toList());
    }

    ChangeGameModeCommands(ConfigurationSection sec) {
        if (sec.contains("run-as-player")) {
            this.runAsPlayer = ChangeGameModeCommands.nonEmptyLines(sec.getStringList("run-as-player"));
        }
        if (sec.contains("run-as-console")) {
            this.runAsConsole = ChangeGameModeCommands.nonEmptyLines(sec.getStringList("run-as-console"));
        }
        if (sec.contains("run-with-permissions")) {
            List<String> permissions = ChangeGameModeCommands.nonEmptyLines(sec.getStringList("run-with-permissions.permissions"));
            List<String> commands = ChangeGameModeCommands.nonEmptyLines(sec.getStringList("run-with-permissions.commands"));
            if (!commands.isEmpty()) {
                this.permissions = permissions;
                this.runWithPermissions = commands;
            }
        }
        if (sec.contains("run-as-op")) {
            this.runAsOP = ChangeGameModeCommands.nonEmptyLines(sec.getStringList("run-as-op"));
        }
    }

    static List<String[]> buildArgs(Player player, GameMode from, GameMode to) {
        return Arrays.asList(new String[]{"player", player.getName()}, new String[]{"uuid", player.getUniqueId().toString()}, new String[]{"from", from.name()}, new String[]{"from-id", Integer.toString(from.getValue())}, new String[]{"to", to.name()}, new String[]{"to-id", Integer.toString(to.getValue())});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void onGameModeChange(Player player, List<String[]> args) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        this.runAsPlayer.forEach(cmd -> player.performCommand(Message.applyArgs(new StringBuilder((String)cmd), args).toString()));
        this.runAsConsole.forEach(cmd -> Bukkit.dispatchCommand((CommandSender)console, (String)Message.applyArgs(new StringBuilder((String)cmd), args).toString()));
        if (!this.runWithPermissions.isEmpty()) {
            PermissionAttachment attachment = player.addAttachment((Plugin)CreativeSecurityPlugin.getInstance(), 1);
            try {
                this.permissions.forEach(perm -> attachment.setPermission(perm, true));
                this.runWithPermissions.forEach(cmd -> player.performCommand(Message.applyArgs(new StringBuilder((String)cmd), args).toString()));
            }
            finally {
                attachment.remove();
            }
        }
        if (!this.runAsOP.isEmpty()) {
            boolean previous = player.isOp();
            try {
                player.setOp(true);
                this.runAsOP.forEach(cmd -> player.performCommand(Message.applyArgs(new StringBuilder((String)cmd), args).toString()));
            }
            finally {
                player.setOp(previous);
            }
        }
    }
}

