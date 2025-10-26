package pl.eastwestfm.eaststone.database;

import org.bukkit.Location;
import pl.eastwestfm.eaststone.StonePlugin;
import pl.eastwestfm.eaststone.data.StoneLocal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoneData implements Data {
    private static final List<StoneLocal> stones = new ArrayList<>();


    @Override
    public void createStone(Location loc) {
        StoneLocal s = new StoneLocal(loc);
        StonePlugin.getStoneLocals().add(s);
        StonePlugin.getInst().mySQLService.executeUpdate("INSERT INTO `spigot_stones` (`id`, `world`, `x`, `y`, `z`) VALUES (NULL, '" + loc.getWorld() + "', '" + loc.getX() + "', '" + loc.getY() + "', '" + loc.getZ() + "');");
    }

    @Override
    public void deleteStone(StoneLocal s) {
        try {
            StonePlugin.getInst().mySQLService.executeUpdate("DELETE FROM `spigot_stones` WHERE `world`='" + s.getWorld() + "' AND `x`='" + s.getX() + "' AND `y`='" + s.getY() + "' AND `z`='" + s.getZ() + "'");
            StonePlugin.getStoneLocals().remove(s);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    @Override
    public StoneLocal getStone(Location loc) {
        for (StoneLocal s : StonePlugin.getStoneLocals())
            if (s.getLocation().equals(loc))
                return s;
        return null;
    }

    @Override
    public boolean isStone(Location loc) {
        for (StoneLocal s : StonePlugin.getStoneLocals())
            if (s.getLocation().equals(loc))
                return true;
        return false;
    }


    // nie uzywam bo odrazu dodaje do bazy.
    @Override
    public void saveStone(StoneLocal s) {
    }

    @Override
    public void saveStones() {
    }

    @Override
    public List<StoneLocal> loadStones() {
        StonePlugin.getInst().getMySQLService().executeQuery("SELECT * FROM `spigot_stones`", rs -> {
            try {
                while (rs.next()) {
                    StoneLocal stonesData = new StoneLocal(rs);
                    stones.add(stonesData);
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return Collections.emptyList();
    }
}