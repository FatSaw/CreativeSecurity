package me.bomb.servershield;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

class PacketInspector {

    private final ServerShield panilla;

    protected PacketInspector(ServerShield panilla) {
        this.panilla = panilla;
    }

    protected void checkPacketPlayInSetCreativeSlot(Object _packet) throws NbtNotPermittedException {
        if (_packet instanceof PacketPlayInSetCreativeSlot) {
            PacketPlayInSetCreativeSlot packet = (PacketPlayInSetCreativeSlot) _packet;

            int slot = packet.b();
            ItemStack itemStack = packet.getItemStack();

            if (itemStack == null || !itemStack.hasTag()) return;

            NbtTagCompound tag = new NbtTagCompound(itemStack.getTag());
            String itemClass = itemStack.getItem().getClass().getSimpleName();
            String packetClass = packet.getClass().getSimpleName();

            NbtChecks.checkPacketPlayIn(slot, tag, itemClass, packetClass, panilla);
        }
    }

    protected void checkPacketPlayOutSetSlot(Object _packet) throws NbtNotPermittedException {
        if (_packet instanceof PacketPlayOutSetSlot) {
            PacketPlayOutSetSlot packet = (PacketPlayOutSetSlot) _packet;

            try {
                Field windowIdField = PacketPlayOutSetSlot.class.getDeclaredField("a");

                windowIdField.setAccessible(true);

                //int windowId = (int) windowIdField.get(packet);

                // check if window is not player inventory and we are ignoring non-player inventories
                //if (windowId != 0 && panilla.getPConfig().ignoreNonPlayerInventories) {
                //    return;
                //}

                Field slotField = PacketPlayOutSetSlot.class.getDeclaredField("b");
                Field itemStackField = PacketPlayOutSetSlot.class.getDeclaredField("c");

                slotField.setAccessible(true);
                itemStackField.setAccessible(true);

                int slot = (int) slotField.get(packet);
                ItemStack itemStack = (ItemStack) itemStackField.get(packet);

                if (itemStack == null || !itemStack.hasTag()) return;

                NbtTagCompound tag = new NbtTagCompound(itemStack.getTag());
                String itemClass = itemStack.getClass().getSimpleName();
                String packetClass = packet.getClass().getSimpleName();

                NbtChecks.checkPacketPlayOut(slot, tag, itemClass, packetClass, panilla);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected void checkPacketPlayOutWindowItems(Object _packet) throws NbtNotPermittedException {
        if (_packet instanceof PacketPlayOutWindowItems) {
            PacketPlayOutWindowItems packet = (PacketPlayOutWindowItems) _packet;

            try {
                Field windowIdField = PacketPlayOutWindowItems.class.getDeclaredField("a");

                windowIdField.setAccessible(true);

                int windowId = (int) windowIdField.get(packet);

                // check if window is not player inventory
                if (windowId != 0) {
                    return;
                }

                Field itemStacksField = PacketPlayOutWindowItems.class.getDeclaredField("b");

                itemStacksField.setAccessible(true);

                List<ItemStack> itemStacks = (List<ItemStack>) itemStacksField.get(packet);

                for (ItemStack itemStack : itemStacks) {
                    if (itemStack == null || !itemStack.hasTag()) {
                        continue;
                    }

                    NbtTagCompound tag = new NbtTagCompound(itemStack.getTag());
                    String itemClass = itemStack.getClass().getSimpleName();
                    String packetClass = packet.getClass().getSimpleName();

                    NbtChecks.checkPacketPlayOut(0, tag, itemClass, packetClass, panilla); // TODO: set slot?
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected void checkPacketPlayOutSpawnEntity(Object _packet) throws EntityNbtNotPermittedException {
        if (_packet instanceof PacketPlayOutSpawnEntity) {
            PacketPlayOutSpawnEntity packet = (PacketPlayOutSpawnEntity) _packet;

            try {
                Field typeField = PacketPlayOutSpawnEntity.class.getDeclaredField("b");

                typeField.setAccessible(true);

                UUID entityId = (UUID) typeField.get(packet);
                org.bukkit.entity.Entity bukkitEntity = org.bukkit.Bukkit.getEntity(entityId);
                CraftEntity craftEntity = (CraftEntity) bukkitEntity;

                if (craftEntity == null) {
                    return;
                }

                Entity entity = craftEntity.getHandle();

                if (entity != null) {
                    if (entity instanceof EntityItem) {
                        EntityItem item = (EntityItem) entity;

                        if (item.getItemStack() == null) {
                            return;
                        }

                        if (!item.getItemStack().hasTag()) {
                            return;
                        }

                        NbtTagCompound tag = new NbtTagCompound(item.getItemStack().getTag());
                        String itemName = item.getItemStack().getItem().getName();
                        FailedNbt failedNbt = NbtChecks.checkAll(tag, itemName, panilla);

                        if (FailedNbt.fails(failedNbt)) {
                            throw new EntityNbtNotPermittedException(packet.getClass().getSimpleName(), false, failedNbt, entityId, bukkitEntity.getWorld().getName());
                        }
                    }
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected void sendPacketPlayOutSetSlotAir(Player player, int slot) {
    	EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(0, slot, new ItemStack(Blocks.AIR));
        entityPlayer.playerConnection.sendPacket(packet);
    }

    protected void stripNbtFromItemEntity(UUID entityId) {
        org.bukkit.entity.Entity bukkitEntity = Bukkit.getServer().getEntity(entityId);

        if (bukkitEntity instanceof CraftEntity) {
            CraftEntity craftEntity = (CraftEntity) bukkitEntity;
            Entity entity = craftEntity.getHandle();

            if (entity instanceof EntityItem) {
                EntityItem item = (EntityItem) entity;

                if (item.getItemStack() == null) {
                    return;
                }

                if (!item.getItemStack().hasTag()) {
                    return;
                }

                item.getItemStack().setTag(null);
            }
        }
    }

    protected void validateBaseComponentParse(String string) throws Exception {
        IChatBaseComponent.ChatSerializer.a(string);
    }
    
    protected void checkPlayIn(ServerShield panilla, Player player, Object packetHandle) throws PacketException {
        try {
            checkPacketPlayInSetCreativeSlot(packetHandle);
        } catch (NbtNotPermittedException e) {
            sendPacketPlayOutSetSlotAir(player, e.getItemSlot());
            throw e;
        }
    }

    protected void checkPlayOut(ServerShield panilla, Object packetHandle) throws PacketException {
        checkPacketPlayOutSetSlot(packetHandle);
        checkPacketPlayOutWindowItems(packetHandle);

        try {
            checkPacketPlayOutSpawnEntity(packetHandle);
        } catch (EntityNbtNotPermittedException e) {
        	stripNbtFromItemEntity(e.getEntityId());
            throw e;
        }
    }

}
