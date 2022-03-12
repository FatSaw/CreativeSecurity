package gasha.creativesecurity;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

public class BlockPosition {
    final int x;
    final int y;
    final int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    BlockPosition(Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    BlockPosition(BlockState block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    BlockPosition(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    BlockPosition(Vector vector) {
        this(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    Block getBlock(World world) {
        return world.getBlockAt(this.x, this.y, this.z);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockPosition that = (BlockPosition)o;
        return this.x == that.x && this.y == that.y && this.z == that.z;
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }

    public String toString() {
        return "BlockPosition{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }
}

