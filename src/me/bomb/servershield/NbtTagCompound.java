package me.bomb.servershield;

import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagList;

import java.io.UnsupportedEncodingException;
import java.util.Set;

class NbtTagCompound {

    private final NBTTagCompound handle;

    protected NbtTagCompound(NBTTagCompound handle) {
        this.handle = handle;
    }

    protected Object getHandle() {
        return handle;
    }

    protected boolean hasKey(String key) {
        return handle.hasKey(key);
    }

    protected boolean hasKeyOfType(String key, NbtDataType nbtDataType) {
        return handle.hasKeyOfType(key, nbtDataType.id);
    }

    protected Set<String> getKeys() {
        return handle.getKeys();
    }

    protected int getInt(String key) {
        return handle.getInt(key);
    }

    protected short getShort(String key) {
        return handle.getShort(key);
    }

    protected String getString(String key) {
        return handle.getString(key);
    }

    protected int[] getIntArray(String key) {
        return handle.getIntArray(key);
    }

    protected NbtTagList getList(String key, NbtDataType nbtDataType) {
        return new NbtTagList(handle.getList(key, nbtDataType.id));
    }

    protected NbtTagList getList(String key) {
        NBTBase base = handle.get(key);

        if (base instanceof NBTTagList) {
            NBTTagList list = (NBTTagList) base;
            return new NbtTagList(list);
        }

        return null;
    }

    protected NbtTagCompound getCompound(String key) {
        return new NbtTagCompound(handle.getCompound(key));
    }
    
    protected int getStringSizeBytes() {
        try {
            return getHandle().toString().getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
