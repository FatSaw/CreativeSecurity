package gasha.creativesecurity.guis.invsee;

import gasha.creativesecurity.AdditionalInventory;
import gasha.creativesecurity.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;

public class EditSessionWrapper {
    private PlayerData playerData;
    private AdditionalInventory additionalInventory;
    private int invNumber;
    private GameMode gameMode;
    private OfflinePlayer targetOfflinePlayer;

    public EditSessionWrapper(PlayerData playerData, AdditionalInventory additionalInventory, int invNumber, GameMode gameMode, OfflinePlayer targetOfflinePlayer) {
        this.playerData = playerData;
        this.additionalInventory = additionalInventory;
        this.invNumber = invNumber;
        this.gameMode = gameMode;
        this.targetOfflinePlayer = targetOfflinePlayer;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    public AdditionalInventory getAdditionalInventory() {
        return this.additionalInventory;
    }

    public int getInvNumber() {
        return this.invNumber;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public OfflinePlayer getTargetOfflinePlayer() {
        return this.targetOfflinePlayer;
    }
}

