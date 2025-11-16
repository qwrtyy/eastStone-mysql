package pl.eastwestfm.eaststone.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import pl.eastwestfm.eaststone.StonePlugin;
import pl.eastwestfm.eaststone.data.StoneLocal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class StoneData implements Data {

    private final List<StoneLocal> stones = new ArrayList<>();

    @Getter
    public HikariDataSource dataSource;

    public StoneData(String mysqlHost, Integer mysqlPort, String mysqlUser, String mysqlName, String mysqlPassword) {
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


    @Override
    public void createStone(Location loc) {
        StoneLocal stone = new StoneLocal(loc);
        StonePlugin.getStoneLocals().add(stone);

        try (PreparedStatement ps = StonePlugin.getMySQLService().getDataSource().getConnection()
                .prepareStatement("INSERT INTO spigot_stones (world, x, y, z) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, loc.getWorld().getName());
            ps.setDouble(2, loc.getX());
            ps.setDouble(3, loc.getY());
            ps.setDouble(4, loc.getZ());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStone(StoneLocal stone) {
        try (PreparedStatement ps = StonePlugin.getMySQLService().getDataSource().getConnection()
                .prepareStatement("DELETE FROM spigot_stones WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            ps.setString(1, stone.getWorld());
            ps.setDouble(2, stone.getX());
            ps.setDouble(3, stone.getY());
            ps.setDouble(4, stone.getZ());
            ps.executeUpdate();

            StonePlugin.getStoneLocals().remove(stone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public StoneLocal getStone(Location loc) {
        Optional<StoneLocal> optional = StonePlugin.getStoneLocals().stream()
                .filter(s -> s.getLocation().equals(loc))
                .findFirst();
        return optional.orElse(null);
    }

    @Override
    public boolean isStone(Location loc) {
        return StonePlugin.getStoneLocals().stream()
                .anyMatch(s -> s.getLocation().equals(loc));
    }

    @Override
    public void saveStone(StoneLocal s) {
        // Nieużywane, bo zapis od razu do bazy
    }

    @Override
    public void saveStones() {
        // Nieużywane, bo zapis od razu do bazy
    }

    @Override
    public List<StoneLocal> loadStones() {
        stones.clear();
        StonePlugin.getMySQLService().executeQuery("SELECT * FROM spigot_stones", rs -> {
            try {
                while (rs.next()) {
                    StoneLocal stone = new StoneLocal(rs);
                    stones.add(stone);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { rs.close(); } catch (Exception ignored) {}
            }
        });
        return new ArrayList<>(stones);
    }
}
