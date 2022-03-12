package gasha.commandguard.manager;

import gasha.commandguard.CommandGuard;
import java.util.List;
import java.util.stream.Collectors;

public class Category {
    private boolean blacklist;
    private List<String> worldGuardRegions;
    private List<String> residenceRegions;
    private List<String> commandsExact;
    private List<String> commandsIgnoreArgs;
    private List<String> commandsStartsWith;
    private List<String> messages;
    private List<String> sounds;
    private String name;

    public Category(CommandGuard commandGuard, String name) {
        this.name = name;
        String path = "category." + name + ".";
        this.blacklist = commandGuard.getConf().getBoolean(path + "blacklist", true);
        this.worldGuardRegions = this.toLowerCase(commandGuard.getConf().getStringList(path + "worldguard-regions"));
        this.residenceRegions = commandGuard.getConf().getStringList(path + "residence-regions");
        this.messages = commandGuard.getConf().getStringList(path + "messages");
        this.sounds = commandGuard.getConf().getStringList(path + "sounds");
        path = path + "commands.";
        this.commandsExact = this.toLowerCase(commandGuard.getConf().getStringList(path + "exact"));
        this.commandsIgnoreArgs = this.toLowerCase(commandGuard.getConf().getStringList(path + "ignore-args"));
        this.commandsStartsWith = this.toLowerCase(commandGuard.getConf().getStringList(path + "starts-with"));
    }

    private List<String> toLowerCase(List<String> list) {
        return list.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public String getName() {
        return this.name;
    }

    public boolean isBlacklist() {
        return this.blacklist;
    }

    public List<String> getWorldGuardRegions() {
        return this.worldGuardRegions;
    }

    public List<String> getResidenceRegions() {
        return this.residenceRegions;
    }

    public List<String> getCommandsExact() {
        return this.commandsExact;
    }

    public List<String> getCommandsIgnoreArgs() {
        return this.commandsIgnoreArgs;
    }

    public List<String> getCommandsStartsWith() {
        return this.commandsStartsWith;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public List<String> getSounds() {
        return this.sounds;
    }
}

