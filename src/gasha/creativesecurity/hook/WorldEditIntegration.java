package gasha.creativesecurity.hook;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.adapter.impl.fawe.BlockMaterial_1_15_2;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BaseBlock;

import gasha.creativesecurity.BlockPosition;
import gasha.creativesecurity.Config;
import gasha.creativesecurity.CreativeListener;
import gasha.creativesecurity.PermissionKey;
import gasha.creativesecurity.RegionData;
import gasha.creativesecurity.RegionPosition;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class WorldEditIntegration {
    private static final OfflinePlayer UNKNOWN = Bukkit.getOfflinePlayer((UUID)new UUID(0L, 0L));

    @Subscribe
    public void onEditSession(EditSessionEvent event) {
        String rawWorld = event.getWorld().getName();
        World bWorld = Bukkit.getWorld((String)rawWorld);
        BukkitWorld world = new BukkitWorld(bWorld);
        EditSession.Stage stage = event.getStage();
        Actor actor = event.getActor();
        try {
            switch (Config.worldEditIntegration) {
                case 1: {
                    event.setExtent((Extent)new Level1(stage, actor, world, event.getExtent()));
                    break;
                }
                case 2: {
                    event.setExtent((Extent)new Level2(stage, actor, world, event.getExtent()));
                    break;
                }
                case 3: {
                    event.setExtent((Extent)new Level3(stage, actor, world, event.getExtent()));
                    break;
                }
            }
        }
        catch (IllegalAccessError illegalAccessError) {
            // empty catch block
        }
    }

    private abstract class AbstractRegionExtent
    extends AbstractDelegateExtent {
        @Nullable
        final org.bukkit.entity.Player player;
        @Nonnull
        final BukkitWorld world;
        @Nonnull
        final EditSession.Stage stage;
        final Map<RegionPosition, RegionData> regionCache;

        private AbstractRegionExtent(@Nullable EditSession.Stage stage, @Nonnull Actor actor, BukkitWorld world, Extent extent) {
            super(extent);
            this.regionCache = new ConcurrentHashMap<RegionPosition, RegionData>();
            this.stage = stage;
            this.world = world;
            this.player = actor instanceof BukkitPlayer ? ((BukkitPlayer)actor).getPlayer() : (actor instanceof Player ? Bukkit.getPlayer((UUID)actor.getUniqueId()) : null);
        }

        abstract boolean setBlock(BlockPosition var1, RegionPosition var2, RegionData var3, BlockVector3 var4, BaseBlock var5);

        public final boolean setBlock(BlockVector3 vector, BaseBlock newBlock) throws WorldEditException {
            BlockPosition pos = new BlockPosition(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            RegionPosition regionPosition = new RegionPosition(pos);
            RegionData regionData = this.regionCache.computeIfAbsent(regionPosition, regPos -> CreativeListener.getRegionData(this.world.getWorld(), regPos));
            if (regionData == null) {
                return false;
            }
            return this.setBlock(pos, regionPosition, regionData, vector, newBlock) && super.setBlock(vector, newBlock);
        }
    }

    private class Level3
    extends Level2 {
        private Level3(@Nullable EditSession.Stage stage, @Nonnull Actor actor, BukkitWorld world, Extent extent) {
            super(stage, actor, world, extent);
        }

        @Override
        boolean setBlock(BlockPosition pos, RegionPosition regPos, RegionData regData, BlockVector3 vector, BaseBlock newBlock) {
            return this.world.getBlock(vector).equals(newBlock) || super.setBlock(pos, regPos, regData, vector, newBlock);
        }
    }

    private class Level2
    extends Level1 {
        private Level2(@Nullable EditSession.Stage stage, @Nonnull Actor actor, BukkitWorld world, Extent extent) {
            super(stage, actor, world, extent);
        }

        @Override
        boolean setBlock(BlockPosition pos, RegionPosition regPos, RegionData regData, BlockVector3 vector, BaseBlock newBlock) {
            return this.world.getBlock(vector).equals(newBlock) || super.setBlock(pos, regPos, regData, vector, newBlock);
        }
    }

    private class Level1
    extends AbstractRegionExtent {
        private Level1(@Nullable EditSession.Stage stage, @Nonnull Actor actor, BukkitWorld world, Extent extent) {
            super(stage, actor, world, extent);
        }

        @Override
        boolean setBlock(BlockPosition pos, RegionPosition regPos, RegionData regData, BlockVector3 vector, BaseBlock newBlock) {
            if (this.stage == EditSession.Stage.BEFORE_CHANGE) {
                if (newBlock.getMaterial().isAir()) {
                    regData.unmark(pos);
                } else if (!this.player.hasPermission(PermissionKey.BYPASS_WORLDEDIT_BLOCK.key)) {
                    regData.mark(pos, (OfflinePlayer)(this.player != null ? this.player : UNKNOWN));
                }
            }
            return true;
        }
    }
}

