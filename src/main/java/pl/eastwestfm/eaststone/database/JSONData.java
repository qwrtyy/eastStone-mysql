package pl.eastwestfm.eaststone.database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pl.eastwestfm.eaststone.StonePlugin;
import pl.eastwestfm.eaststone.data.StoneLocal;

public class JSONData implements Data {

	private static JSONArray tempData = new JSONArray();
	
	private File jsonFile;
	private JSONParser jsonParser;
		
	public JSONData(String filename) {
		this.jsonFile = new File(StonePlugin.getInst().getDataFolder(), filename + ".json");
		this.jsonParser = new JSONParser();
		if(!this.jsonFile.exists()) {
			try {
				this.jsonFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void saveStone(StoneLocal s) {
		tempData.add(s.toJSONObject());
	}

	@Override
	public void saveStones() {
		for (StoneLocal s : StonePlugin.getStoneLocals())
			saveStone(s);
		saveData();
		tempData.clear();
	}

	@Override
	public List<StoneLocal> loadStones() {
		List<StoneLocal> stoneLocals = new ArrayList<StoneLocal>();
		try {
			Object o = jsonParser.parse(new FileReader(jsonFile.getAbsolutePath()));
			JSONArray jsonArray = (JSONArray) o;
			for(Object ob : jsonArray) {
				JSONObject jsonO = (JSONObject) ob;
				StoneLocal s = new StoneLocal(jsonO);
				stoneLocals.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stoneLocals;
	}
	
	public void saveData() {
		FileWriter f;
		try {
			f = new FileWriter(this.jsonFile.getAbsolutePath());
			f.write(tempData.toJSONString());
			f.flush();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
