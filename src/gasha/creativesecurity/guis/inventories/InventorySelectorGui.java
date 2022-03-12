package gasha.creativesecurity.guis.inventories;

import gasha.creativesecurity.DataListener;
import gasha.creativesecurity.PlayerData;
import gasha.creativesecurity.guis.GuiUtil;
import gasha.creativesecurity.guis.inventories.GuiParent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventorySelectorGui extends GuiParent {
    private PlayerData playerData;
    private String type;
    private int openedInventoryIndex;

    public InventorySelectorGui(Player player, String type, DataListener dataListener) {
        super(player, dataListener, "InventorySelector", "title-" + type, 18);
        this.type = type;
        this.playerData = this.getDataListener().getData(player);
        this.openedInventoryIndex = this.getOpenInvIndex();
        for (int i = 0; i < 10; ++i) {
            ItemStack skull = GuiUtil.getNumberedSkull(i);
            if (this.openedInventoryIndex == i) {
                skull = skull.clone();
                ItemMeta updatedLore = skull.getItemMeta();
                updatedLore.setLore(this.getStringList("lore-selected"));
                skull.setItemMeta(updatedLore);
            }
            this.inventory.setItem(i, skull);
        }
        this.inventory.setItem(17, GuiUtil.getBackArrowSkull());
    }

    private int getOpenInvIndex() {
        if (this.getPlayer().getGameMode().toString().toLowerCase().equals(this.type)) {
            return this.playerData.getCurrentIndex();
        }
        return 100;
    }
}

