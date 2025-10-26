package pl.eastwestfm.eaststone.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.eastwestfm.eaststone.StonePlugin;
import pl.eastwestfm.eaststone.data.StoneLocal;
import pl.eastwestfm.eaststone.util.Logger;

public class YAMLData implements Data {

	private String fileName;
	private YamlConfiguration data;
	
	private List<String> tempData = new ArrayList<String>();

	public YAMLData(String filename) {
		this.fileName = filename;
		this.data = YamlConfiguration.loadConfiguration(new File(StonePlugin.getInst().getDataFolder(), this.fileName + ".yml"));

		Bukkit.getScheduler().runTaskTimerAsynchronously(StonePlugin.getInst(), new Runnable() {

			@Override
			public void run() {

				saveStones();
			}

		}, 20 * 10, 20 * 60 * StonePlugin.getCfg().databaseAutoSaveTime);
	}

	@Override
	public void createStone(Location loc) {
		StoneLocal s = new StoneLocal(loc);
		StonePlugin.getStoneLocals().add(s);
	}

	@Override
	public void deleteStone(StoneLocal s) {
		StonePlugin.getStoneLocals().remove(s);
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

	@Override
	public void saveStone(StoneLocal s) {
		tempData.add(s.toString());
	}

	@Override
	public void saveStones() {
		for (StoneLocal s : StonePlugin.getStoneLocals())
			saveStone(s);
		data.set("data.stones", tempData);
		saveData();
		tempData.clear();
	}

	@Override
	public List<StoneLocal> loadStones() {
		List<StoneLocal> stoneLocals = new ArrayList<StoneLocal>();
		for (String string : this.data.getStringList("data.stones")) {
			StoneLocal s = new StoneLocal(string);
			StonePlugin.getStoneLocals().add(s);
		}
		return stoneLocals;
	}

	public void saveData() {
		try {
			data.save(new File(StonePlugin.getInst().getDataFolder(), this.fileName + ".yml"));
		} catch (IOException e) {
			Logger.log(Level.SEVERE, "Wystapil blad podczas zapisu pliku " + this.fileName + ".yml", "Blad: " + e.getMessage());
		}
	}

}
