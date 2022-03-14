package com.ruinscraft.panilla;

class PacketException extends Exception {

    private final String nmsClass;
    private final boolean from;
    private final FailedNbt failedNbt;

    protected PacketException(String nmsClass, boolean from, FailedNbt failedNbt) {
        this.nmsClass = nmsClass;
        this.from = from;
        this.failedNbt = failedNbt;
    }

    protected String getNmsClass() {
        return nmsClass;
    }

    // is from client
    protected boolean isFrom() {
        return from;
    }

    protected FailedNbt getFailedNbt() {
        return failedNbt;
    }

}
