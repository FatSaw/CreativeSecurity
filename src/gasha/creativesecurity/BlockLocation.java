package gasha.creativesecurity;

import java.util.Objects;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

class BlockLocation {
    private final World world;
    private final int x;
    private final int y;
    private final int z;

    BlockLocation(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    BlockLocation(BlockState block) {
        this(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    BlockLocation(Block block) {
        this(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    World getWorld() {
        return this.world;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    int getZ() {
        return this.z;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockLocation that = (BlockLocation)o;
        return this.x == that.x && this.y == that.y && this.z == that.z && Objects.equals((Object)this.world, (Object)that.world);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.world, this.x, this.y, this.z});
    }

    public String toString() {
        return "BlockLocation{world=" + (Object)this.world + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }
}

