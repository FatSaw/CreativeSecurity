package gasha.creativesecurity;

import gasha.creativesecurity.BlockPosition;
import gasha.creativesecurity.Config;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.SqlConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;

public class RegionData {
    private static final byte[] HEADER = new byte[]{-18, 18, 104, 88, 19};
    private static final long DATE_START = 1497994581000L;
    private final Map<BlockPosition, BlockMark> blocks;
    private final Set<BlockPosition> unmarked = new HashSet<BlockPosition>(0);
    final int regionX;
    final int regionZ;
    private final int firstBlockX;
    private final int firstBlockZ;
    private boolean isDirty;

    private synchronized void saveJDBC(World world) throws SQLException {
        if (!this.isDirty) {
            return;
        }
        Connection connection = SqlConfig.getConnection();
        boolean before = connection.getAutoCommit();
        connection.setAutoCommit(false);
        Savepoint savepoint = connection.setSavepoint();
        try {
            List<Map.Entry<BlockPosition,RegionData.BlockMark>> marks;
            byte[] worldId = SqlConfig.uuid(world.getUID());
            String worldFolder = world.getWorldFolder().toString();
            SqlConfig.register(connection, worldId, worldFolder);
            if (!this.unmarked.isEmpty()) {
                try (PreparedStatement pst = connection.prepareStatement("DELETE FROM creativesecurity_blocks WHERE world_id=? AND x=? AND y=? AND z=?");){
                    pst.setBytes(1, worldId);
                    for (BlockPosition position : this.unmarked) {
                        pst.setInt(2, position.x);
                        pst.setInt(3, position.y);
                        pst.setInt(4, position.z);
                        pst.addBatch();
                    }
                    pst.executeUpdate();
                }
            }
            if (!(marks = this.blocks.entrySet().stream().filter(entry -> ((BlockMark)entry.getValue()).isNew).collect(Collectors.toList())).isEmpty()) {
                PreparedStatement pst = connection.prepareStatement("INSERT INTO creativesecurity_blocks(world_id, x, y, z, region_x, region_z, uuid, date) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE uuid=?, date=?");
                Object object = null;
                try {
                    pst.setBytes(1, worldId);
                    pst.setInt(5, this.regionX);
                    pst.setInt(6, this.regionZ);
                    for (Map.Entry<BlockPosition,RegionData.BlockMark> entry2 : marks) {
                        BlockPosition pos = (BlockPosition)entry2.getKey();
                        BlockMark mark = (BlockMark)entry2.getValue();
                        pst.setInt(2, pos.x);
                        pst.setInt(3, pos.y);
                        pst.setInt(4, pos.z);
                        byte[] ownerId = SqlConfig.uuid(mark.playerId);
                        pst.setBytes(7, ownerId);
                        pst.setBytes(9, ownerId);
                        Date date = new Date(mark.date.toUnix());
                        pst.setDate(8, date);
                        pst.setDate(10, date);
                        pst.addBatch();
                    }
                    pst.executeBatch();
                }
                catch (Throwable throwable) {
                    object = throwable;
                    throw throwable;
                }
                finally {
                    if (pst != null) {
                        if (object != null) {
                            try {
                                pst.close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                        } else {
                            pst.close();
                        }
                    }
                }
            }
            connection.commit();
        }
        catch (Throwable e) {
            try {
                connection.rollback(savepoint);
            }
            catch (Throwable e2) {
                e.addSuppressed(e2);
            }
            throw e;
        }
        finally {
            connection.releaseSavepoint(savepoint);
            connection.setAutoCommit(before);
        }
        this.postSave();
    }

    private static File subfolder(File worldFolder) {
        return new File(worldFolder, "region/");
    }

    static RegionData load(World world, int regionX, int regionZ) throws IOException, SQLException {
        try {
            if (Config.jdbc) {
                return RegionData.loadJDBC(regionX, regionZ, world);
            }
            return RegionData.load(regionX, regionZ, RegionData.subfolder(world.getWorldFolder()));
        }
        catch (Exception e) {
            CreativeSecurityPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load the region data World:" + world.getName() + " X:" + regionX + " Z:" + regionZ, e);
            CreativeSecurityPlugin.dataLogger.log(Level.SEVERE, "Failed to load the region data World:" + world.getName() + " X:" + regionX + " Z:" + regionZ, e);
            throw e;
        }
    }

    private static RegionData loadJDBC(int regionX, int regionZ, World world) throws SQLException {
        RegionData data = new RegionData(regionX, regionZ);
        try (PreparedStatement pst = SqlConfig.getConnection().prepareStatement("SELECT x, y, z, uuid, `date` FROM creativesecurity_blocks USE INDEX (region) WHERE world_id=? AND region_x=? AND region_z=?");){
            pst.setBytes(1, SqlConfig.uuid(world.getUID()));
            pst.setInt(2, regionX);
            pst.setInt(3, regionZ);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                data.blocks.put(new BlockPosition(result.getInt(1), result.getInt(2), result.getInt(3)), new BlockMark(new ShortDate(result.getDate(5).getTime()), SqlConfig.uuid(result.getBytes(4)), false));
            }
        }
        return data;
    }

    private static RegionData load(int regionX, int regionZ, File dataDir) throws IOException {
        File file = new File(dataDir, RegionData.fileName(regionX, regionZ));
        if (!file.isFile()) {
            return new RegionData(regionX, regionZ);
        }
        try (FileInputStream in = new FileInputStream(file);){
            RegionData regionData = new RegionData(regionX, regionZ, new DataInputStream(new BufferedInputStream(in)));
            return regionData;
        }
    }

    private static String fileName(int regionX, int regionZ) {
        return "r." + regionX + "." + regionZ + ".creativesecurity.dat";
    }

    private RegionData(int regionX, int regionZ) {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.blocks = new HashMap<BlockPosition, BlockMark>(0);
        this.firstBlockX = regionX << 5 << 4;
        this.firstBlockZ = regionZ << 5 << 4;
    }

    private RegionData(int regionX, int regionZ, DataInput in) throws IOException {
        int i;
        int blocksLen;
        int playersLen;
        IntInput playerIndexReader;
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.firstBlockX = regionX << 5 << 4;
        this.firstBlockZ = regionZ << 5 << 4;
        byte[] header = new byte[HEADER.length];
        in.readFully(header);
        if (!Arrays.equals(header, HEADER)) {
            throw new IOException("Invalid file header");
        }
        byte version = in.readByte();
        if (version < 1 || version > 3) {
            throw new UnsupportedOperationException("Expected file-version to be 1 ~ 3 but got " + version + ", this file might have been generated by a future version and cannot be loaded by this version, please update the plugin.");
        }
        if (version < 3) {
            playerIndexReader = DataInput::readUnsignedShort;
            playersLen = in.readUnsignedShort();
            blocksLen = in.readUnsignedShort();
        } else {
            switch (in.readUnsignedByte()) {
                case 1: {
                    playerIndexReader = DataInput::readUnsignedByte;
                    break;
                }
                case 2: {
                    playerIndexReader = DataInput::readUnsignedShort;
                    break;
                }
                case 3: {
                    playerIndexReader = DataInput::readInt;
                    break;
                }
                default: {
                    throw new IOException("Invalid storage flag");
                }
            }
            playersLen = playerIndexReader.read(in);
            blocksLen = in.readInt();
        }
        UUID[] players = new UUID[playersLen];
        for (i = 0; i < playersLen; ++i) {
            players[i] = new UUID(in.readLong(), in.readLong());
        }
        this.blocks = new HashMap<BlockPosition, BlockMark>(blocksLen);
        for (i = 0; i < blocksLen; ++i) {
            BlockPosition pos = this.createBlockPosition(in.readShort(), in.readByte(), in.readShort());
            short shortDate = in.readShort();
            if (version == 1) {
                shortDate = (short)(shortDate + 1);
            }
            ShortDate date = new ShortDate(shortDate);
            UUID player = players[playerIndexReader.read(in)];
            this.blocks.put(pos, new BlockMark(date, player, false));
        }
    }

    synchronized void setMark(BlockPosition pos, BlockMark mark) {
        Objects.requireNonNull(mark, "mark can't be null");
        Objects.requireNonNull(pos, "pos can't be null");
        BlockMark copy = new BlockMark(mark.date, mark.playerId, true);
        this.isDirty = true;
        this.unmarked.remove(pos);
        this.blocks.put(pos, copy);
    }

    synchronized void mark(Block block, OfflinePlayer player) {
        this.mark(new BlockPosition(block), player);
    }

    public synchronized void mark(BlockPosition pos, OfflinePlayer player) {
        this.setMark(pos, new BlockMark(new ShortDate(), player.getUniqueId(), true));
    }

    public synchronized void unmark(BlockPosition pos) {
        BlockMark removed = this.blocks.remove(pos);
        if (removed == null || removed.isNew) {
            return;
        }
        this.isDirty = true;
        this.unmarked.add(pos);
    }

    synchronized void unmark(Block block) {
        this.unmark(new BlockPosition(block));
    }

    @Deprecated
    synchronized void markBlocks(World world) {
        CreativeSecurityPlugin instance = CreativeSecurityPlugin.getInstance();
    }

    boolean isDirty() {
        return this.isDirty;
    }

    synchronized void save(World world) throws IOException, SQLException {
        if (Config.jdbc) {
            this.saveJDBC(world);
        } else {
            this.save(world.getWorldFolder());
        }
    }

    private synchronized void save(File worldFolder) throws IOException {
        boolean updating;
        if (!this.isDirty) {
            return;
        }
        File target = new File(RegionData.subfolder(worldFolder), RegionData.fileName(this.regionX, this.regionZ));
        File safe = new File(target.getParent(), target.getName() + ".new");
        File old = new File(target.getParent(), target.getName() + ".old");
        File parentFile = target.getParentFile();
        if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
            CreativeSecurityPlugin.getInstance().getLogger().severe("Failed to create the folder: " + parentFile);
        }
        if (this.blocks.isEmpty()) {
            if (target.isFile() && !target.delete()) {
                CreativeSecurityPlugin.getInstance().getLogger().severe("Failed to delete the file: " + target);
            }
            this.postSave();
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(safe);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             DataOutputStream dos = new DataOutputStream(bos);){
            this.save(dos);
        }
        if (old.isFile() && !old.delete()) {
            CreativeSecurityPlugin.getInstance().getLogger().severe("Failed to delete the file: " + old);
        }
        if ((updating = target.isFile()) && !target.renameTo(old)) {
            CreativeSecurityPlugin.getInstance().getLogger().severe("Failed to move: " + target + " to: " + old);
        }
        if (!safe.renameTo(target)) {
            CreativeSecurityPlugin.getInstance().getLogger().severe("Failed to move: " + safe + " to: " + target);
        }
        if (updating && !old.delete()) {
            CreativeSecurityPlugin.getInstance().getLogger().severe("Failed to delete: " + old);
        }
    }

    private synchronized void save(DataOutput out) throws IOException {
        IntOutput playerIndexWritter;
        if (!this.isDirty) {
            return;
        }
        out.write(HEADER);
        out.writeByte(3);
        UUID[] players = (UUID[])this.blocks.values().stream().map(mark -> mark.playerId).distinct().toArray(UUID[]::new);
        if (players.length <= 255) {
            out.writeByte(1);
            out.writeByte(players.length);
            playerIndexWritter = DataOutput::writeByte;
        } else if (players.length <= 65535) {
            out.writeByte(2);
            out.writeShort(players.length);
            playerIndexWritter = DataOutput::writeShort;
        } else {
            out.writeByte(3);
            out.writeInt(players.length);
            playerIndexWritter = DataOutput::writeInt;
        }
        out.writeInt(this.blocks.size());
        for (UUID player : players) {
            out.writeLong(player.getMostSignificantBits());
            out.writeLong(player.getLeastSignificantBits());
        }
        HashMap<UUID, Integer> playerIndex = new HashMap<UUID, Integer>(players.length);
        for (int i = 0; i < players.length; ++i) {
            playerIndex.put(players[i], i);
        }
        for (Map.Entry<BlockPosition, BlockMark> entry : this.blocks.entrySet()) {
            BlockPosition pos = entry.getKey();
            out.writeShort(this.regX(pos.x));
            out.writeByte(this.regY(pos.y));
            out.writeShort(this.regZ(pos.z));
            BlockMark mark2 = entry.getValue();
            out.writeShort(mark2.date.date);
            playerIndexWritter.write(out, (Integer)playerIndex.get(mark2.playerId));
        }
        this.postSave();
    }

    private synchronized void postSave() {
        this.isDirty = false;
        this.blocks.values().forEach(mark -> ((BlockMark)mark).isNew = false);
        this.unmarked.clear();
    }

    BlockMark getMark(BlockPosition block) {
        return this.blocks.get(block);
    }

    boolean isCreative(BlockPosition block) {
        return this.blocks.containsKey(block);
    }

    UUID getOwnerId(BlockPosition block) {
        BlockMark mark = this.blocks.get(block);
        if (mark == null) {
            return null;
        }
        return mark.playerId;
    }

    private BlockPosition createBlockPosition(short x, byte y, short z) {
        return new BlockPosition(this.firstBlockX + Short.toUnsignedInt(x), Byte.toUnsignedInt(y), this.firstBlockZ + Short.toUnsignedInt(z));
    }

    private short regX(int x) {
        return (short)(x - this.firstBlockX);
    }

    private byte regY(int y) {
        return (byte)y;
    }

    private short regZ(int z) {
        return (short)(z - this.firstBlockZ);
    }

    @FunctionalInterface
    private static interface IntOutput {
        public void write(DataOutput var1, int var2) throws IOException;
    }

    @FunctionalInterface
    private static interface IntInput {
        public int read(DataInput var1) throws IOException;
    }

    static class BlockMark {
        final ShortDate date;
        final UUID playerId;
        private boolean isNew;

        BlockMark(ShortDate date, UUID playerId, boolean isNew) {
            this.date = date;
            this.playerId = playerId;
            this.isNew = isNew;
        }

        public String toString() {
            return "BlockMark{date=" + this.date + ", playerId=" + this.playerId + ", isNew=" + this.isNew + '}';
        }
    }

    static class ShortDate
    implements Comparable<ShortDate> {
        final short date;

        ShortDate() {
            this(System.currentTimeMillis());
        }

        ShortDate(long unixTime) {
            this.date = (short)(TimeUnit.MILLISECONDS.toDays(unixTime - 1497994581000L) + 1L);
        }

        ShortDate(short date) {
            this.date = date;
        }

        long toUnix() {
            return TimeUnit.DAYS.toMillis(this.date) + 1497994581000L;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ShortDate shortDate = (ShortDate)o;
            return this.date == shortDate.date;
        }

        public int hashCode() {
            return Objects.hash(this.date);
        }

        @Override
        public int compareTo(ShortDate o) {
            return Short.compare(this.date, o.date);
        }

        public String toString() {
            return "ShortDate{date=" + this.date + '}';
        }
    }
}

