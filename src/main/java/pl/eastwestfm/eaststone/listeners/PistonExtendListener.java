package pl.eastwestfm.eaststone.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import pl.eastwestfm.eaststone.StonePlugin;
import pl.eastwestfm.eaststone.data.StoneLocal;

public class PistonExtendListener implements Listener {

	@EventHandler
	public void onExplode(BlockPistonExtendEvent e) {
		List<Block> blocks = e.getBlocks();
		if (!(blocks.isEmpty())) {
			for (Block b : blocks) {
				StoneLocal s = StonePlugin.getData().getStone(b.getLocation());
				if (s != null)
					StonePlugin.getData().deleteStone(s);
			}
		}
	}

}
