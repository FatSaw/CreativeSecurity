package gasha.creativesecurity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {
    public static void playSound(Player player, String sound, Location loc, float volume, float pitch) {
        Sound theSound = null;
        try {
            theSound = Sound.valueOf((String)sound);
        }
        catch (IllegalArgumentException ex) {
            String[] splitsound = sound.split("_");
            String newSound = "";
            for (int i = 1; i < splitsound.length; ++i) {
                if (splitsound.length - i <= 1) {
                    newSound = newSound + splitsound[i];
                    continue;
                }
                if (splitsound.length - i <= 1) continue;
                newSound = newSound + splitsound[i] + "_";
            }
            newSound = newSound.replace("ENDERMEN", "ENDERMAN");
            try {
                theSound = Sound.valueOf((String)newSound);
            }
            catch (IllegalArgumentException exb) {
                try {
                    theSound = Sound.valueOf((String)"CLICK");
                }
                catch (IllegalArgumentException exxb) {
                    theSound = Sound.valueOf((String)"UI_BUTTON_CLICK");
                }
            }
        }
        player.playSound(loc, theSound, volume, pitch);
    }

    public static void playSound(Player player, String sound) {
        SoundManager.playSound(player, sound, player.getLocation(), 5.0f, 1.0f);
    }

    public static void playSound(Player player, String sound, Location loc) {
        SoundManager.playSound(player, sound, loc, 5.0f, 1.0f);
    }

    public static void playSound(Player player, String sound, float volume, float pitch) {
        SoundManager.playSound(player, sound, player.getLocation(), volume, pitch);
    }

    public static void playSound(String sound, Location loc, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SoundManager.playSound(player, sound, loc, volume, pitch);
        }
    }
}

