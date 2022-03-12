package gasha.commandguard.listeners;

import gasha.commandguard.CommandGuard;
import gasha.commandguard.manager.CategoryExecution;
import gasha.commandguard.manager.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TeleportListener
implements Listener {
    private CommandGuard commandGuard;

    public TeleportListener(CommandGuard commandGuard) {
        this.commandGuard = commandGuard;
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("creativesecurity.commandguard.bypass.execution.*")) {
            return;
        }
        String cmd = e.getMessage().split(" ")[0];
        String[] cargs = e.getMessage().split(" ");
        for (CategoryExecution ce : this.commandGuard.getExecution()) {
            if (p.hasPermission("creativesecurity.commandguard.bypass.execution." + ce.getName())) continue;
            block5: for (String cmdToCheck : ce.getCommands()) {
                if (!cmdToCheck.split(" ")[0].toLowerCase().equalsIgnoreCase(cmd)) continue;
                String[] args = cmdToCheck.split(" ");
                for (int i = 1; i < args.length; ++i) {
                    String word = args[i];
                    if (word.equalsIgnoreCase("%playername%")) {
                        try {
                            Player target = Bukkit.getPlayer((String)cargs[i]);
                            if (target != null && (this.commandGuard.isWithinWorldGuardRegion(target.getLocation(), ce.getWorldGuardRegions()) || this.commandGuard.isWithinResidenceRegion(target.getLocation(), ce.getResidenceRegions()))) {
                                e.setCancelled(true);
                                ce.getMessages().forEach(msg -> p.sendMessage(msg.replace("%playername%", target.getName())));
                                ce.getSounds().forEach(sound -> SoundManager.playSound(p, sound.toUpperCase()));
                                return;
                            }
                        }
                        catch (Exception exception) {}
                        continue;
                    }
                    try {
                        if (word.toLowerCase().equalsIgnoreCase(cargs[i])) continue;
                        continue block5;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
    }
}

