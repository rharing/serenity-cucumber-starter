package starter.helpers;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SnapshotManager {
    private static final Logger log = LoggerFactory.getLogger(SnapshotManager.class);
    private final String SNAPSHOT_PREFIX = "SNAPSHOT_";
    private final String SCHEMA = "citest_database";
    private List<String> tables = new ArrayList();
    private boolean snapShotTablesFound = false;

    public SnapshotManager() {
    }

    private void discoverTables(final Connection connection) throws SQLException {
        if (this.tables.isEmpty()) {
            ResultSet resultSet = connection.getMetaData().getTables((String)null, "citest_database", (String)null, new String[]{"TABLE"});

            while(resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                if (!tableName.startsWith("SNAPSHOT_")) {
                    this.tables.add(tableName);
                } else {
                    this.snapShotTablesFound = true;
                }
            }
        }

    }

    public void writeSnapshot(final DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        this.discoverTables(connection);

        try {
            log.info(String.format("Will snapshot the following tables:\n%s\n", this.tables));
            Iterator var3 = this.tables.iterator();

            while(var3.hasNext()) {
                String tableName = (String)var3.next();
                this.createSnapshotTable(connection, tableName);
            }

            this.snapShotTablesFound = true;
        } finally {
            this.unlockTables(connection);
            connection.close();
        }
    }

    public void restoreFromSnapshot(final DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        this.discoverTables(connection);
        if (!this.snapShotTablesFound) {
            log.warn("No snapshot found, nothing will be restored");
            connection.close();
        } else {
            log.info(String.format("Will restore the following tables:\n%s\n", this.tables));

            try {
                this.lockTables(connection, this.allTablesAndSnapshots(this.tables));
                this.execute(connection, "SET FOREIGN_KEY_CHECKS=0");
                Iterator var3 = this.tables.iterator();

                while(var3.hasNext()) {
                    String tableName = (String)var3.next();
                    this.restoreTableFromSnapshot(connection, tableName);
                }
            } finally {
                this.unlockTables(connection);
                this.execute(connection, "SET FOREIGN_KEY_CHECKS=1");
                connection.close();
            }

        }
    }

    private List<String> allTablesAndSnapshots(final List<String> dataTables) {
        List<String> combinedList = new ArrayList(dataTables);
        combinedList.addAll((Collection)dataTables.stream().map((tableName) -> {
            return "SNAPSHOT_" + tableName;
        }).collect(Collectors.toList()));
        return combinedList;
    }

    public void writeSnapshot(final DataSource dataSource, final String tableName) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            this.createSnapshotTable(connection, tableName);
        } finally {
            this.unlockTables(connection);
            connection.close();
        }

    }

    public void restoreFromSnapshot(final DataSource dataSource, final String tableName) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            this.lockTables(connection, Arrays.asList(tableName, this.snapshotTableFor(tableName)));
            this.disableReferentialIntegrity(connection, tableName);
            this.restoreTableFromSnapshot(connection, tableName);
            this.enableReferentialIntegrity(connection, tableName);
        } finally {
            this.unlockTables(connection);
            connection.close();
        }

    }

    private String snapshotTableFor(final String sourceTableName) {
        return "SNAPSHOT_" + sourceTableName;
    }

    private void createSnapshotTable(final Connection connection, final String sourceTable) throws SQLException {
        String dropStatement = String.format("drop table if exists %s", this.snapshotTableFor(sourceTable));
        String copyStatement = String.format("create table %s as select * from %s", this.snapshotTableFor(sourceTable), sourceTable);
        this.execute(connection, dropStatement);
        this.execute(connection, copyStatement);
        Logger var10000 = log;
        Long var10001 = this.getCount(connection, sourceTable);
        var10000.info("copied " + var10001 + " records from " + sourceTable);
    }

    private void restoreTableFromSnapshot(final Connection connection, final String sourceTable) throws SQLException {
        String deleteStatement = String.format("delete from %s", sourceTable);
        String copyStatement = String.format("insert into %s select * from %s", sourceTable, this.snapshotTableFor(sourceTable));
        this.execute(connection, deleteStatement);
        this.execute(connection, copyStatement);
    }

    private Long getCount(final Connection connection, final String tableName) throws SQLException {
        String countStatement = String.format("select count(*) from %s", tableName);
        Statement statement = connection.createStatement();

        Long var7;
        try {
            ResultSet resultSet = statement.executeQuery(countStatement);
            Long result;
            if (resultSet.next()) {
                result = resultSet.getLong(1);
            } else {
                result = 0L;
            }

            var7 = result;
        } catch (Throwable var9) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }
            }

            throw var9;
        }

        if (statement != null) {
            statement.close();
        }

        return var7;
    }

    private void disableReferentialIntegrity(final Connection connection, final String tableName) throws SQLException {
        String disableKeysStatement = String.format("ALTER TABLE `%s` DISABLE KEYS", tableName);
        this.execute(connection, disableKeysStatement);
    }

    private void enableReferentialIntegrity(final Connection connection, final String tableName) throws SQLException {
        String enableKeysStatement = String.format("ALTER TABLE `%s` ENABLE KEYS", tableName);
        this.execute(connection, enableKeysStatement);
    }

    private void lockTables(final Connection connection, final List<String> tablesToLock) throws SQLException {
        String lockStatement = String.format("lock tables %s", tablesToLock.stream().map((tableName) -> {
            return "`" + tableName + "` write";
        }).collect(Collectors.joining(",")));
        this.execute(connection, lockStatement);
    }

    private void unlockTables(final Connection connection) throws SQLException {
        String unlockStatement = "unlock tables";
        this.execute(connection, "unlock tables");
    }

    private void execute(final Connection connection, final String sql) throws SQLException {
        Statement statement = connection.createStatement();

        try {
            statement.execute(sql);
        } catch (Throwable var7) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }

            throw var7;
        }

        if (statement != null) {
            statement.close();
        }

    }
}
