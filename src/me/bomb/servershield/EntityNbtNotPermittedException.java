package me.bomb.servershield;

import java.util.UUID;

class EntityNbtNotPermittedException extends PacketException {

	private final UUID entityId;
    private final String world;

    protected EntityNbtNotPermittedException(String nmsClass, boolean from, FailedNbt failedNbt, UUID entityId, String world) {
        super(nmsClass, from, failedNbt);
        this.entityId = entityId;
        this.world = world;
    }

    protected UUID getEntityId() {
        return entityId;
    }

    protected String getWorld() {
        return world;
    }

}
