package me.bomb.servershield;

import net.minecraft.server.v1_15_R1.Container;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

class InventoryCleaner {

    private final ServerShield panilla;

    protected InventoryCleaner(ServerShield panilla) {
        this.panilla = panilla;
    }

    protected void clean(Player player) {
        Container container = ((CraftPlayer)player).getHandle().activeContainer;

        for (int slot = 0; slot < container.slots.size(); slot++) {
            ItemStack itemStack = container.getSlot(slot).getItem();

            if (itemStack == null || !itemStack.hasTag()) {
                continue;
            }

            NBTTagCompound nmsTag = itemStack.getTag();
            NbtTagCompound tag = new NbtTagCompound(nmsTag);
            String itemName = itemStack.getItem().getName();

            if (nmsTag == null || itemName == null) {
                continue;
            }

            FailedNbt failedNbt = NbtChecks.checkAll(tag, itemName, panilla);

            if (FailedNbt.failsThreshold(failedNbt)) {
                container.getSlot(slot).getItem().setTag(null);
            } else if (FailedNbt.fails(failedNbt)) {
                nmsTag.remove(failedNbt.key);
                container.getSlot(slot).getItem().setTag(nmsTag);
            }
        }
    }

}
