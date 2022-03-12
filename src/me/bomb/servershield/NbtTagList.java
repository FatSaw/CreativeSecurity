package me.bomb.servershield;

import net.minecraft.server.v1_15_R1.NBTTagList;

class NbtTagList {

    private final NBTTagList handle;

    protected NbtTagList(NBTTagList handle) {
        this.handle = handle;
    }

    protected NbtTagCompound getCompound(int index) {
        return new NbtTagCompound(handle.getCompound(index));
    }

    protected String getString(int index) {
        return handle.getString(index);
    }

    protected int size() {
        return handle.size();
    }

}
