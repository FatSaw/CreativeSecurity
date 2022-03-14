package com.ruinscraft.panilla;

class NbtNotPermittedException extends PacketException {

    private final int itemSlot;

    protected NbtNotPermittedException(String nmsClass, boolean from, FailedNbt failedNbt, int itemSlot) {
        super(nmsClass, from, failedNbt);
        this.itemSlot = itemSlot;
    }

    protected int getItemSlot() {
        return itemSlot;
    }

}
