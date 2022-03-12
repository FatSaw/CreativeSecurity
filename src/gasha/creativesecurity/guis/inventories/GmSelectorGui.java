package gasha.creativesecurity.guis.inventories;

import gasha.creativesecurity.DataListener;
import gasha.creativesecurity.guis.GuiUtil;
import gasha.creativesecurity.guis.inventories.GuiParent;
import java.util.ArrayList;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GmSelectorGui extends GuiParent {
    public GmSelectorGui(Player player, DataListener dataListener) {
        super(player, dataListener, "GmSelector", "title", 9);
        this.inventory.setItem(0, GuiUtil.getGamemodeSkull(GameMode.SURVIVAL));
        this.inventory.setItem(2, GuiUtil.getGamemodeSkull(GameMode.CREATIVE));
        this.inventory.setItem(4, GuiUtil.getGamemodeSkull(GameMode.SPECTATOR));
        this.inventory.setItem(6, GuiUtil.getGamemodeSkull(GameMode.ADVENTURE));
        int currentIndex = this.getDataListener().getData(player).getCurrentIndex() + 1;
        ArrayList<String> infoItemLore = new ArrayList<String>();
        for (String lore : this.getStringList("info.lore")) {
            infoItemLore.add(lore.replace("{gamemode}", player.getGameMode().toString().toLowerCase()).replace("{index}", String.valueOf(currentIndex)));
        }
        ItemStack playerSkull = GuiUtil.createSkull(player, this.getString("info.name").replace("{player_name}", player.getName()), infoItemLore);
        this.inventory.setItem(8, playerSkull);
    }
}

