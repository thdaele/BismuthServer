package carpet.bismuth.utils;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.Tuple;

import java.util.HashMap;

public class SpawnReporter {
	public static final HashMap<Integer, HashMap<EnumCreatureType, Tuple<Integer, Integer>>> mobcaps = new HashMap<>();

	static {
		reset_spawn_stats();
	}

	private static void reset_spawn_stats() {
		mobcaps.put(-1, new HashMap<>());
		mobcaps.put(0, new HashMap<>());
		mobcaps.put(1, new HashMap<>());
	}
}
