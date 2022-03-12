package gasha.creativesecurity;

import gasha.creativesecurity.CreativeSecurityPlugin;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class AntiCommandsSuggestion
implements Listener {
    private CreativeSecurityPlugin main;
    private boolean enabled;
    private Map<String, List<String>> whitelistedCommands;
    private Set<String> whitelistsKeys;

    AntiCommandsSuggestion(CreativeSecurityPlugin main) {
        this.main = main;
    }

    boolean doesCommandsSuggestSystemExist() {
        try {
            Class.forName("org.bukkit.event.player.PlayerCommandSendEvent");
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @EventHandler
    public void onSuggest(PlayerCommandSendEvent event) {
        if (!this.enabled) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("creativesecurity.bypass.tabsuggest")) {
            List extraSuggestions = this.whitelistsKeys.stream().filter(whitelistKey -> player.hasPermission("creativesecurity.tabsuggestwhitelist." + whitelistKey.toLowerCase())).flatMap(whitelistKey -> this.whitelistedCommands.get(whitelistKey).stream()).collect(Collectors.toList());
            if (extraSuggestions.isEmpty()) {
                if (this.getDefaultWhitelist().isEmpty()) {
                    event.getCommands().clear();
                } else {
                    event.getCommands().removeIf(line -> !this.getDefaultWhitelist().contains(line));
                }
            } else {
                extraSuggestions.addAll(this.getDefaultWhitelist());
                event.getCommands().removeIf(line -> !extraSuggestions.contains(line));
            }
        }
    }

    public void reload() {
        ConfigurationSection config = this.main.getConfig().getConfigurationSection("disable-commands-suggestions");
        this.enabled = config.getBoolean("enabled");
        ConfigurationSection whitelists = config.getConfigurationSection("whitelists");
        this.whitelistedCommands = whitelists.getKeys(false).stream().collect(Collectors.toMap(Function.identity(), ((ConfigurationSection)whitelists)::getStringList));
        this.whitelistsKeys = new HashSet<String>(this.whitelistedCommands.keySet());
        this.whitelistsKeys.remove("default");
    }

    private List<String> getDefaultWhitelist() {
        return this.whitelistedCommands.get("default");
    }
}

