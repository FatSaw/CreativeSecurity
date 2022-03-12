package gasha.creativesecurity;

import com.ibatis.common.jdbc.ScriptRunner;
import gasha.creativesecurity.CreativeSecurityPlugin;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.World;

class SqlConfig {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static Connection connection;

    SqlConfig() {
    }

    static void load(String url, String username, String password) {
        URL = url;
        USERNAME = username;
        PASSWORD = password;
        try {
            SqlConfig.openConnection();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            SqlConfig.update();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return false;
        }
        CreativeSecurityPlugin creativeSecurityPlugin = CreativeSecurityPlugin.getInstance();
        synchronized (creativeSecurityPlugin) {
            if (connection != null && !connection.isClosed()) {
                return false;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return true;
    }

    private static void update() throws SQLException, IOException {
        boolean installed;
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet results = metaData.getTables(null, null, "creativesecurity_version", new String[]{"TABLE"});){
            installed = results.next();
        }
        if (!installed) {
            System.out.println("Creating tables....");
            boolean before = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (InputStream in = Objects.requireNonNull(SqlConfig.class.getResourceAsStream("/sql/setup.sql"), "Missing setup.sql");){
                SqlConfig.run(connection, new InputStreamReader((InputStream)new BufferedInputStream(in), StandardCharsets.UTF_8));
                try (Statement stm = connection.createStatement();){
                    stm.execute("CREATE PROCEDURE `creativesecurity_save_playerdata`(IN `pid` BINARY(16),IN `cs` TINYINT(2),IN `cc` TINYINT(2),IN `lc` TINYINT(1),IN `si` TEXT,IN `ci` TEXT)\n  BEGIN\n    INSERT INTO creativesecurity_playerdata(player_id, current_survival, current_creative, last_creative, survival_inventories, creative_inventories)\n        VALUES (pid,cs,cc,lc,si,ci)\n        ON DUPLICATE KEY UPDATE current_survival=cs, current_creative=cc, last_creative=lc, survival_inventories=si, creative_inventories=ci;\n  END;");
                }
                connection.commit();
                System.out.println("Tables created!");
            }
            catch (Throwable e) {
                try {
                    connection.rollback();
                }
                catch (Throwable e2) {
                    e.addSuppressed(e2);
                }
                throw e;
            }
            finally {
                connection.setAutoCommit(before);
            }
            return;
        }
        try (Statement stm = connection.createStatement();){
            ResultSet result = stm.executeQuery("SELECT `value` FROM creativesecurity_version WHERE property = 'version'");
            if (!result.next()) {
                throw new SQLException("Missing version property from the creativesecurity_version database");
            }
            int version = result.getInt(1);
            if (version != 1) {
                throw new UnsupportedOperationException("Expected the database version to be 1 but got " + version + ". It means that the database was updated by a higher version of this plugin. Please update the CreativeSecurity plugin.");
            }
        }
    }

    static Connection getConnection() {
        return connection;
    }

    static void closeConnection() {
        try {
            if (connection != null && connection.isClosed()) {
                connection.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static byte[] uuid(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        byte[] bytes = new byte[16];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return bytes;
    }

    static UUID uuid(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            long firstLong = bb.getLong();
            long secondLong = bb.getLong();
            return new UUID(firstLong, secondLong);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Bad UUID", e);
        }
    }

    static void register(Connection connection, byte[] uuid, String worldFolder) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("INSERT IGNORE creativesecurity_worlds(world_id, world_folder) VALUES (?, ?)");){
            pst.setBytes(1, uuid);
            pst.setString(2, worldFolder);
            pst.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void register(Connection connection, World world) throws SQLException {
        SqlConfig.register(connection, SqlConfig.uuid(world.getUID()), world.getWorldFolder().toString());
    }

    private static void run(Connection connection, Reader reader) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(connection, false, true);
        runner.runScript(reader);
    }

    static {
        connection = null;
    }
}

