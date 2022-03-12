package gasha.commandguard.manager;

import gasha.commandguard.CommandGuard;
import java.util.List;

public class CategoryExecution {
    private List<String> worldGuardRegions;
    private List<String> residenceRegions;
    private List<String> commands;
    private List<String> messages;
    private List<String> sounds;
    private String name;

    public CategoryExecution(CommandGuard commandGuard, String name) {
        this.name = name;
        String path = "execution." + name + ".";
        this.worldGuardRegions = commandGuard.getTeleport().getStringList(path + "blocked_worldguard_regions");
        this.residenceRegions = commandGuard.getTeleport().getStringList(path + "blocked_residence_regions");
        this.commands = commandGuard.getTeleport().getStringList(path + "blocked_commands");
        this.messages = commandGuard.getTeleport().getStringList(path + "messages");
        this.sounds = commandGuard.getTeleport().getStringList(path + "sounds");
    }

    public String getName() {
        return this.name;
    }

    public List<String> getWorldGuardRegions() {
        return this.worldGuardRegions;
    }

    public List<String> getResidenceRegions() {
        return this.residenceRegions;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public List<String> getSounds() {
        return this.sounds;
    }
}

