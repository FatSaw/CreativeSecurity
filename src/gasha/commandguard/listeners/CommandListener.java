package gasha.commandguard.listeners;

import gasha.commandguard.CommandGuard;
import gasha.commandguard.manager.Category;
import gasha.commandguard.manager.SoundManager;
import gasha.creativesecurity.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener
implements Listener {
    private CommandGuard commandGuard;

    public CommandListener(CommandGuard commandGuard) {
        this.commandGuard = commandGuard;
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (!this.canDo(player, e.getMessage(), true)) {
            e.setCancelled(true);
        }
    }

    private boolean canDo(Player player, String message, boolean sendMessagePlaySounds) {
        if (player.hasPermission("creativesecurity.commandguard.bypass.*")) {
            return true;
        }
        String executedCommand = message.substring(1).toLowerCase();
        for (Category ca : this.commandGuard.getCatagories()) {
            boolean inRegion = false;
            if (player.hasPermission("creativesecurity.commandguard.bypass." + ca.getName()) || !this.restrictedCommandUsage(executedCommand, ca)) continue;
            boolean blacklist = ca.isBlacklist();
            boolean bl = this.commandGuard.getCreativeSecurityInstance().isResidenceIntegrationLoaded() ? this.commandGuard.isWithinWorldGuardRegion(player.getLocation(), ca.getWorldGuardRegions()) || this.commandGuard.isWithinResidenceRegion(player.getLocation(), ca.getResidenceRegions()) : (inRegion = this.commandGuard.isWithinWorldGuardRegion(player.getLocation(), ca.getWorldGuardRegions()));
            if ((!blacklist || !inRegion) && (blacklist || inRegion)) continue;
            if (sendMessagePlaySounds) {
                ca.getMessages().forEach(((Player)player)::sendMessage);
                ca.getSounds().forEach(sound -> SoundManager.playSound(player, sound.toUpperCase()));
            }
            return false;
        }
        return true;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void sudoListener(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("/sudo")) {
            String to = e.getMessage().toLowerCase().replace("/sudo ", "");
            String[] split = to.split(" ");
            StringBuilder f = new StringBuilder("/");
            for (int i = 1; i < split.length; ++i) {
                if (f.toString().equals("/")) {
                    f = new StringBuilder(split[i]);
                    continue;
                }
                f.append(" ").append(split[i]);
            }
            if (f.toString().equals("/")) {
                return;
            }
            String name = split[0];
            Player p = Bukkit.getPlayer((String)name);
            if (!(p == null || e.getPlayer().hasPermission("creativesecurity.commandguard.bypass.sudo") && this.canDo(e.getPlayer(), f.toString(), false))) {
                e.setCancelled(true);
                Message.COMMAND_GUARD_NO_SUDO.sendInfo((CommandSender)p);
                p.sendMessage(this.commandGuard.getConf().getString("message_sudo_not_allowed"));
            }
        }
    }

    private boolean restrictedCommandUsage(String command, Category category) {
        if (category.getCommandsStartsWith().stream().anyMatch(command::startsWith)) {
            return true;
        }
        String cmdNoArgs = command;
        if (command.contains(" ")) {
            if (category.getCommandsExact().contains(command)) {
                return true;
            }
            cmdNoArgs = command.split(" ")[0];
        }
        return category.getCommandsIgnoreArgs().contains(cmdNoArgs);
    }
}

