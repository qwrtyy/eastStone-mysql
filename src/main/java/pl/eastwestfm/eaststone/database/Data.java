package pl.eastwestfm.eaststone.database;

import java.util.List;

import org.bukkit.Location;

import pl.eastwestfm.eaststone.data.StoneLocal;

public interface Data {

	public void createStone(Location loc);
	
	public void deleteStone(StoneLocal s);
	
	public StoneLocal getStone(Location loc);
	
	public boolean isStone(Location loc);
	
	public void saveStone(StoneLocal s);

	public void saveStones();
		
	public List<StoneLocal> loadStones();
	
}
