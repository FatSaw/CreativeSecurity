package gasha.creativesecurity;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AdditionalInventory
implements ConfigurationSerializable {
    private final boolean creative;
    private ItemStack[] armorContents;
    private ItemStack[] extraContents;
    private ItemStack[] storageContents;

    public AdditionalInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        boolean bl = this.creative = player.getGameMode() == GameMode.CREATIVE;
        this.extraContents = inventory.getExtraContents();
        this.storageContents = inventory.getStorageContents();
        this.armorContents = inventory.getArmorContents();
    }

    public AdditionalInventory(PlayerInventory playerInventory) {
        this.creative = false;
        this.extraContents = playerInventory.getExtraContents();
        this.storageContents = playerInventory.getStorageContents();
        this.armorContents = playerInventory.getArmorContents();
    }

    AdditionalInventory(boolean creative) {
        this(creative, new ItemStack[0], new ItemStack[0], new ItemStack[0]);
    }

    private AdditionalInventory(boolean creative, ItemStack[] armorContents, ItemStack[] extraContents, ItemStack[] storageContents) {
        this.creative = creative;
        this.armorContents = armorContents;
        this.extraContents = extraContents;
        this.storageContents = storageContents;
    }

    boolean matches(PlayerInventory inventory) {
        return Arrays.equals((Object[])this.armorContents, (Object[])inventory.getArmorContents()) && Arrays.equals((Object[])this.extraContents, (Object[])inventory.getExtraContents()) && Arrays.equals((Object[])this.storageContents, (Object[])inventory.getStorageContents());
    }

    void read(PlayerInventory inventory) {
        this.extraContents = inventory.getExtraContents();
        this.storageContents = inventory.getStorageContents();
        this.armorContents = inventory.getArmorContents();
    }

    public void apply(PlayerInventory inventory) {
        inventory.setArmorContents(this.armorContents);
        inventory.setExtraContents(this.extraContents);
        inventory.setStorageContents(this.storageContents);
    }

    void clear() {
        this.armorContents = new ItemStack[0];
        this.extraContents = new ItemStack[0];
        this.storageContents = new ItemStack[0];
    }

    public static AdditionalInventory deserialize(Map<String, Object> m) {
        return new AdditionalInventory((Boolean)m.get("creative"), ((List<ItemStack>)m.get("armor")).toArray(new ItemStack[4]), ((List<ItemStack>)m.get("extra")).toArray(new ItemStack[1]), ((List<ItemStack>)m.get("storage")).toArray(new ItemStack[36]));
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("creative", this.creative);
        result.put("armor", Arrays.asList(this.armorContents));
        result.put("extra", Arrays.asList(this.extraContents));
        result.put("storage", Arrays.asList(this.storageContents));
        return result;
    }

    public boolean isCreative() {
        return this.creative;
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }

    public void setExtraContents(ItemStack[] extraContents) {
        this.extraContents = extraContents;
    }

    public void setStorageContents(ItemStack[] storageContents) {
        this.storageContents = storageContents;
    }

    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }

    public ItemStack[] getExtraContents() {
        return this.extraContents;
    }

    public ItemStack[] getStorageContents() {
        return this.storageContents;
    }
}

