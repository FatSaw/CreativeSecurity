package gasha.creativesecurity.guis.invsee;

import gasha.creativesecurity.AdditionalInventory;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.guis.GuiConfig;
import gasha.creativesecurity.guis.GuiUtil;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvseeGui {
    private CreativeSecurityPlugin main;
    private GuiConfig guiConfig;
    private OfflinePlayer inventoryOwner;
    private Inventory inventory;
    private AdditionalInventory additionalInventory;
    private int additionalInventoryNumber;

    public InvseeGui(CreativeSecurityPlugin main, AdditionalInventory additionalInventory, OfflinePlayer inventoryOwner, int additionalInventoryNumber, String title) {
        this.main = main;
        this.guiConfig = main.getGuiConfig();
        this.inventoryOwner = inventoryOwner;
        this.additionalInventory = additionalInventory;
        this.additionalInventoryNumber = additionalInventoryNumber;
        this.inventory = Bukkit.createInventory(null, (int)54, (String)title);
        this.insertInInventory(true);
    }

    public InvseeGui(CreativeSecurityPlugin main, Player inventoryOwner, String title) {
        this.main = main;
        this.guiConfig = main.getGuiConfig();
        this.inventoryOwner = inventoryOwner;
        this.additionalInventory = new AdditionalInventory(inventoryOwner);
        this.inventory = Bukkit.createInventory(null, (int)54, (String)title);
        this.insertInInventory(false);
    }

    private void insertInInventory(boolean insertInfoItem) {
        IntStream.rangeClosed(0, 3).forEach(index -> {
            try {
                this.inventory.setItem(index, this.additionalInventory.getArmorContents()[index]);
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                // empty catch block
            }
        });
        ItemStack glassPane = GuiUtil.getInvseeSeparator();
        this.inventory.setItem(4, glassPane);
        IntStream.rangeClosed(9, 17).forEach(index -> this.inventory.setItem(index, glassPane));
        ItemStack infoItemLocation = insertInfoItem ? this.createInfoItem() : glassPane;
        this.inventory.setItem(8, infoItemLocation);
        this.inventory.setItem(6, glassPane);
        this.inventory.setItem(7, glassPane);
        int index2 = 5;
        for (ItemStack itemStack : this.additionalInventory.getExtraContents()) {
            this.inventory.setItem(index2, itemStack);
            ++index2;
        }
        index2 = 18;
        for (ItemStack itemStack : this.additionalInventory.getStorageContents()) {
            int location = index2;
            location = index2 <= 26 ? (location += 27) : (location -= 9);
            this.inventory.setItem(location, itemStack);
            ++index2;
        }
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    private ItemStack createInfoItem() {
        ItemStack infoItem = GuiUtil.getInvseeInfoItem().clone();
        ItemMeta itemMeta = infoItem.getItemMeta();
        String path = "Invsee.info";
        String displayName = this.guiConfig.getString(path + ".name").replace("{gamemode}", this.additionalInventory.isCreative() ? "Creative" : "Survival").replace("{inv_number}", String.valueOf(this.additionalInventoryNumber));
        List<String> lore = this.guiConfig.getStringList(path + ".lore").stream().map(line -> line.replace("{name}", this.inventoryOwner.getName()).replace("{uuid}", this.inventoryOwner.getUniqueId().toString())).collect(Collectors.toList());
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        infoItem.setItemMeta(itemMeta);
        return infoItem;
    }
}

