package pl.eastwestfm.eaststone.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

@Slf4j
public class MySQLData {
    public HikariDataSource dataSource;

    public MySQLData(String mysqlHost, Integer mysqlPort, String mysqlUser, String mysqlName, String mysqlPassword) {
        this.dataSource = new HikariDataSource();
        int poolSize = 5;
        this.dataSource.setMaximumPoolSize(poolSize);
        this.dataSource.setConnectionTimeout(30000L);
        this.dataSource.setJdbcUrl("jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlName + "?useSSL=" + false);
        this.dataSource.setUsername(mysqlUser);
        this.dataSource.setPassword(mysqlPassword);
        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);
        this.executeUpdate("CREATE TABLE IF NOT EXISTS spigot_stones (" +
                "id int(11) not null primary key auto_increment, " +
                "world varchar(255) not null, " +
                "x int(11) not null, " +
                "y int(11) not null, " +
                "z int(11) not null, " +
                "durability int(11));"
        );
    }


    public void executeQuery(final String query, final Consumer<ResultSet> action) {
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(query);
             final ResultSet result = statement.executeQuery()) {
            action.accept(result);
        } catch (Exception exception) {
            log.error("Wystąpił błąd", exception);
        }
    }

    public void executeUpdate(final String query) {
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(query)) {
            if (statement == null) {
                return;
            }
            statement.executeUpdate();
        } catch (Exception exception) {
            log.error("Wystąpił błąd", exception);
        }
    }
}