package gasha.creativesecurity;

import gasha.creativesecurity.BlockLocation;
import gasha.creativesecurity.BlockPosition;
import java.util.Objects;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

public final class RegionPosition {
    final int regionX;
    final int regionZ;

    RegionPosition(int regionX, int regionZ) {
        this.regionX = regionX;
        this.regionZ = regionZ;
    }

    RegionPosition(Chunk chunk) {
        this(chunk.getX() >> 5, chunk.getZ() >> 5);
    }

    RegionPosition(Block block) {
        this(block.getX() >> 9, block.getZ() >> 9);
    }

    RegionPosition(BlockLocation block) {
        this(block.getX() >> 9, block.getZ() >> 9);
    }

    public RegionPosition(BlockPosition block) {
        this(block.x >> 9, block.z >> 9);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RegionPosition that = (RegionPosition)o;
        return this.regionX == that.regionX && this.regionZ == that.regionZ;
    }

    public int hashCode() {
        return Objects.hash(this.regionX, this.regionZ);
    }

    public String toString() {
        return "RegionPosition{regionX=" + this.regionX + ", regionZ=" + this.regionZ + '}';
    }
}

