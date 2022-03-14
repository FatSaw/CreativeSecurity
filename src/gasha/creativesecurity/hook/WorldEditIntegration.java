package gasha.creativesecurity.hook;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;

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
                    event.setExtent(new Level1(stage, actor, world, event.getExtent()));
                    break;
                }
                case 2: {
                    event.setExtent(new Level2(stage, actor, world, event.getExtent()));
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
        


		abstract void setMarkBlock(BlockPosition var1, RegionPosition var2, RegionData var3,BlockVector3 position, BaseBlock var5);

        abstract void setMarkBlock(BlockPosition var1, RegionPosition var2, RegionData var3,BlockVector3 position, BlockState var5);
        
        public final <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
            BlockPosition pos = new BlockPosition(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            RegionPosition regionPosition = new RegionPosition(pos);
            RegionData regionData = this.regionCache.computeIfAbsent(regionPosition, regPos -> CreativeListener.getRegionData(this.world.getWorld(), regPos));
            if (regionData == null) {
                return false;
            }
            if(block instanceof BaseBlock) {
            	setMarkBlock(pos, regionPosition, regionData,position, (BaseBlock)block);
            } else if(block instanceof BlockState) {
            	setMarkBlock(pos, regionPosition, regionData,position, (BlockState)block);
            }
        	return extent.setBlock(position.getX(), position.getY(), position.getZ(), block);
        }
            
        public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y,int z, T block) throws WorldEditException {
            BlockPosition pos = new BlockPosition(x, y, z);
            RegionPosition regionPosition = new RegionPosition(pos);
            RegionData regionData = this.regionCache.computeIfAbsent(regionPosition, regPos -> CreativeListener.getRegionData(this.world.getWorld(), regPos));
            if (regionData == null) {
                return false;
            }
            if(block instanceof BaseBlock) {
            	setMarkBlock(pos, regionPosition, regionData,BlockVector3.at(x, y, z), (BaseBlock)block);
            } else if(block instanceof BlockState) {
            	setMarkBlock(pos, regionPosition, regionData,BlockVector3.at(x, y, z), (BlockState)block);
            }
        	return extent.setBlock(x, y, z, block);
        }
    }

    private class Level2
    extends Level1 {
        private Level2(@Nullable EditSession.Stage stage, @Nonnull Actor actor, BukkitWorld world, Extent extent) {
            super(stage, actor, world, extent);
        }
        @Override
        public void setMarkBlock(BlockPosition pos, RegionPosition regPos, RegionData regData,BlockVector3 vector, BlockState newBlock) {
        	if (this.stage == EditSession.Stage.BEFORE_CHANGE) {
                if (newBlock.getMaterial().isAir()) {
                    regData.unmark(pos);
                } else if (!this.player.hasPermission(PermissionKey.BYPASS_WORLDEDIT_BLOCK.key) && !this.world.getBlock(vector).equals(newBlock)) {
                	regData.mark(pos, (OfflinePlayer)(this.player != null ? this.player : UNKNOWN));
                }
            }
        }
        
        @Override
        public void setMarkBlock(BlockPosition pos, RegionPosition regPos, RegionData regData,BlockVector3 vector, BaseBlock newBlock) {
        	if (this.stage == EditSession.Stage.BEFORE_CHANGE) {
                if (newBlock.getMaterial().isAir()) {
                    regData.unmark(pos);
                } else if (!this.player.hasPermission(PermissionKey.BYPASS_WORLDEDIT_BLOCK.key) && !this.world.getBlock(vector).toBaseBlock().equals(newBlock)) {
                	regData.mark(pos, (OfflinePlayer)(this.player != null ? this.player : UNKNOWN));
                }
            }
        }
    }

    private class Level1 extends AbstractRegionExtent {
        private Level1(@Nullable EditSession.Stage stage, @Nonnull Actor actor, BukkitWorld world, Extent extent) {
            super(stage, actor, world, extent);
        }
        
        @Override
        public void setMarkBlock(BlockPosition pos, RegionPosition regPos, RegionData regData,BlockVector3 vector, BlockState newBlock) {
        	if (this.stage == EditSession.Stage.BEFORE_CHANGE) {
                if (newBlock.getMaterial().isAir()) {
                    regData.unmark(pos);
                } else if (!this.player.hasPermission(PermissionKey.BYPASS_WORLDEDIT_BLOCK.key)) {
                	regData.mark(pos, (OfflinePlayer)(this.player != null ? this.player : UNKNOWN));
                }
            }
        }
        
        @Override
        public void setMarkBlock(BlockPosition pos, RegionPosition regPos, RegionData regData,BlockVector3 vector, BaseBlock newBlock) {
        	if (this.stage == EditSession.Stage.BEFORE_CHANGE) {
                if (newBlock.getMaterial().isAir()) {
                    regData.unmark(pos);
                } else if (!this.player.hasPermission(PermissionKey.BYPASS_WORLDEDIT_BLOCK.key)) {
                	regData.mark(pos, (OfflinePlayer)(this.player != null ? this.player : UNKNOWN));
                }
            }
        }
    }
}

