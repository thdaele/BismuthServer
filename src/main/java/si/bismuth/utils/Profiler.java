package si.bismuth.utils;

import net.minecraft.server.MinecraftServer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Profiler {
	private static final HashMap<String, Long> time_repo = new HashMap<>();
	public static int tick_health_requested = 0;
	private static int tick_health_elapsed = 0;
	private static int test_type = 0; //1 for ticks, 2 for entities;
	private static String current_section = null;
	private static long current_section_start = 0;
	private static long current_tick_start = 0;
	private static final Map<String, String> notchToMcp = new HashMap<>();
	private static boolean isDev = false;

	public static void prepare_tick_report(int ticks) {
		//maybe add so it only spams the sending player, but honestly - all may want to see it
		time_repo.clear();
		test_type = 1;
		time_repo.put("tick", 0L);
		time_repo.put("network", 0L);
		time_repo.put("autosave", 0L);

		time_repo.put("overworld.spawning", 0L);
		time_repo.put("overworld.tickupdates", 0L);
		time_repo.put("overworld.chunkmap", 0L);
		time_repo.put("overworld.villages", 0L);
		time_repo.put("overworld.portals", 0L);
		time_repo.put("overworld.blockevents", 0L);
		time_repo.put("overworld.checknextlight", 0L);
		time_repo.put("overworld.tickchunk", 0L);
		time_repo.put("overworld.thunder", 0L);
		time_repo.put("overworld.iceandsnow", 0L);
		time_repo.put("overworld.randomticks", 0L);
		time_repo.put("overworld.entities", 0L);
		time_repo.put("overworld.tileentities", 0L);

		time_repo.put("the_nether.spawning", 0L);
		time_repo.put("the_nether.tickupdates", 0L);
		time_repo.put("the_nether.chunkmap", 0L);
		time_repo.put("the_nether.villages", 0L);
		time_repo.put("the_nether.portals", 0L);
		time_repo.put("the_nether.blockevents", 0L);
		time_repo.put("the_nether.checknextlight", 0L);
		time_repo.put("the_nether.tickchunk", 0L);
		time_repo.put("the_nether.thunder", 0L);
		time_repo.put("the_nether.iceandsnow", 0L);
		time_repo.put("the_nether.randomticks", 0L);
		time_repo.put("the_nether.entities", 0L);
		time_repo.put("the_nether.tileentities", 0L);

		time_repo.put("the_end.spawning", 0L);
		time_repo.put("the_end.tickupdates", 0L);
		time_repo.put("the_end.chunkmap", 0L);
		time_repo.put("the_end.villages", 0L);
		time_repo.put("the_end.portals", 0L);
		time_repo.put("the_end.blockevents", 0L);
		time_repo.put("the_end.checknextlight", 0L);
		time_repo.put("the_end.tickchunk", 0L);
		time_repo.put("the_end.thunder", 0L);
		time_repo.put("the_end.iceandsnow", 0L);
		time_repo.put("the_end.randomticks", 0L);
		time_repo.put("the_end.entities", 0L);
		time_repo.put("the_end.tileentities", 0L);

		tick_health_elapsed = ticks;
		tick_health_requested = ticks;
		current_tick_start = 0L;
		current_section_start = 0L;
		current_section = null;
	}

	public static void start_section(String dimension, String name) {
		if (tick_health_requested == 0L || test_type != 1) {
			return;
		}
		if (current_tick_start == 0L) {
			return;
		}
		if (current_section != null) {
			end_current_section();
		}
		String key = name;
		if (dimension != null) {
			key = dimension + "." + name;
		}
		current_section = key;
		current_section_start = System.nanoTime();
	}

	public static void start_entity_section(String dimension, Object e) {
		if (tick_health_requested == 0L || test_type != 2) {
			return;
		}
		if (current_tick_start == 0L) {
			return;
		}
		if (current_section != null) {
			end_current_section();
		}
		current_section = dimension + "." + getMCPName(e.getClass().getSimpleName());
		current_section_start = System.nanoTime();
	}

	public static void end_current_section() {
		if (tick_health_requested == 0L || test_type != 1) {
			return;
		}
		long end_time = System.nanoTime();
		if (current_tick_start == 0L) {
			return;
		}
		time_repo.put(current_section, time_repo.get(current_section) + end_time - current_section_start);
		current_section = null;
		current_section_start = 0;
	}

	public static void end_current_entity_section() {
		if (tick_health_requested == 0L || test_type != 2) {
			return;
		}
		long end_time = System.nanoTime();
		if (current_tick_start == 0L) {
			return;
		}
		String time_section = "t." + current_section;
		String count_section = "c." + current_section;
		time_repo.put(time_section, time_repo.getOrDefault(time_section, 0L) + end_time - current_section_start);
		time_repo.put(count_section, time_repo.getOrDefault(count_section, 0L) + 1);
		current_section = null;
		current_section_start = 0;
	}

	public static void start_tick_profiling() {
		current_tick_start = System.nanoTime();
	}

	public static void end_tick_profiling(MinecraftServer server) {
		if (current_tick_start == 0L) {
			return;
		}
		time_repo.put("tick", time_repo.get("tick") + System.nanoTime() - current_tick_start);
		tick_health_elapsed--;
		if (tick_health_elapsed <= 0) {
			finalize_tick_report(server);
		}
	}

	private static void finalize_tick_report(MinecraftServer server) {
		if (test_type == 1) {
			finalize_tick_report_for_time(server);
		}
		if (test_type == 2) {
			finalize_tick_report_for_entities(server);
		}
		cleanup_tick_report();
	}

	private static void cleanup_tick_report() {
		time_repo.clear();
		time_repo.put("tick", 0L);
		test_type = 0;
		tick_health_elapsed = 0;
		tick_health_requested = 0;
		current_tick_start = 0L;
		current_section_start = 0L;
		current_section = null;
	}

	private static void finalize_tick_report_for_time(MinecraftServer server) {
		//print stats
		long total_tick_time = time_repo.get("tick");
		double divider = 1.0D / tick_health_requested / 1000000;
		Messenger.print_server_message(server, String.format("Average tick time: %.3fms", divider * total_tick_time));
		long accumulated = 0L;

		accumulated += time_repo.get("autosave");
		Messenger.print_server_message(server, String.format("Autosave: %.3fms", divider * time_repo.get("autosave")));

		accumulated += time_repo.get("network");
		Messenger.print_server_message(server, String.format("Network: %.3fms", divider * time_repo.get("network")));

		Messenger.print_server_message(server, "Overworld:");

		accumulated += time_repo.get("overworld.entities");
		Messenger.print_server_message(server, String.format(" - Entities: %.3fms", divider * time_repo.get("overworld.entities")));

		accumulated += time_repo.get("overworld.tileentities");
		Messenger.print_server_message(server, String.format(" - Tile Entities: %.3fms", divider * time_repo.get("overworld.tileentities")));

		accumulated += time_repo.get("overworld.tickupdates");
		Messenger.print_server_message(server, String.format(" - Tick Updates: %.3fms", divider * time_repo.get("overworld.tickupdates")));

		accumulated += time_repo.get("overworld.spawning");
		Messenger.print_server_message(server, String.format(" - Spawning: %.3fms", divider * time_repo.get("overworld.spawning")));

		accumulated += time_repo.get("overworld.chunkmap");
		Messenger.print_server_message(server, String.format(" - Chunk Map: %.3fms", divider * time_repo.get("overworld.chunkmap")));

		accumulated += time_repo.get("overworld.villages");
		Messenger.print_server_message(server, String.format(" - Villages: %.3fms", divider * time_repo.get("overworld.villages")));

		accumulated += time_repo.get("overworld.portals");
		Messenger.print_server_message(server, String.format(" - Portalcaching: %.3fms", divider * time_repo.get("overworld.portals")));

		accumulated += time_repo.get("overworld.blockevents");
		Messenger.print_server_message(server, String.format(" - Queued Block Events: %.3fms", divider * time_repo.get("overworld.blockevents")));

		accumulated += time_repo.get("overworld.checknextlight");
		Messenger.print_server_message(server, String.format(" - Check Next Light: %.3fms", divider * time_repo.get("overworld.checknextlight")));

		accumulated += time_repo.get("overworld.tickchunk");
		Messenger.print_server_message(server, String.format(" - Tick Chunk: %.3fms", divider * time_repo.get("overworld.tickchunk")));

		accumulated += time_repo.get("overworld.thunder");
		Messenger.print_server_message(server, String.format(" - Thunder: %.3fms", divider * time_repo.get("overworld.thunder")));

		accumulated += time_repo.get("overworld.iceandsnow");
		Messenger.print_server_message(server, String.format(" - Ice&Snow: %.3fms", divider * time_repo.get("overworld.iceandsnow")));

		accumulated += time_repo.get("overworld.randomticks");
		Messenger.print_server_message(server, String.format(" - Random Ticks: %.3fms", divider * time_repo.get("overworld.randomticks")));

		Messenger.print_server_message(server, "Nether:");

		accumulated += time_repo.get("the_nether.entities");
		Messenger.print_server_message(server, String.format(" - Entities: %.3fms", divider * time_repo.get("the_nether.entities")));

		accumulated += time_repo.get("the_nether.tileentities");
		Messenger.print_server_message(server, String.format(" - Tile Entities: %.3fms", divider * time_repo.get("the_nether.tileentities")));

		accumulated += time_repo.get("the_nether.tickupdates");
		Messenger.print_server_message(server, String.format(" - Tick Updates: %.3fms", divider * time_repo.get("the_nether.tickupdates")));

		accumulated += time_repo.get("the_nether.spawning");
		Messenger.print_server_message(server, String.format(" - Spawning: %.3fms", divider * time_repo.get("the_nether.spawning")));

		accumulated += time_repo.get("the_nether.chunkmap");
		Messenger.print_server_message(server, String.format(" - Chunk Map: %.3fms", divider * time_repo.get("the_nether.chunkmap")));

		accumulated += time_repo.get("the_nether.villages");
		Messenger.print_server_message(server, String.format(" - Villages: %.3fms", divider * time_repo.get("the_nether.villages")));

		accumulated += time_repo.get("the_nether.portals");
		Messenger.print_server_message(server, String.format(" - Portalcaching: %.3fms", divider * time_repo.get("the_nether.portals")));

		accumulated += time_repo.get("the_nether.blockevents");
		Messenger.print_server_message(server, String.format(" - Queued Block Events: %.3fms", divider * time_repo.get("the_nether.blockevents")));

		accumulated += time_repo.get("the_nether.checknextlight");
		Messenger.print_server_message(server, String.format(" - Check Next Light: %.3fms", divider * time_repo.get("the_nether.checknextlight")));

		accumulated += time_repo.get("the_nether.tickchunk");
		Messenger.print_server_message(server, String.format(" - Tick Chunk: %.3fms", divider * time_repo.get("the_nether.tickchunk")));

		accumulated += time_repo.get("the_nether.thunder");
		Messenger.print_server_message(server, String.format(" - Thunder: %.3fms", divider * time_repo.get("the_nether.thunder")));

		accumulated += time_repo.get("the_nether.iceandsnow");
		Messenger.print_server_message(server, String.format(" - Ice&Snow: %.3fms", divider * time_repo.get("the_nether.iceandsnow")));

		accumulated += time_repo.get("the_nether.randomticks");
		Messenger.print_server_message(server, String.format(" - Random Ticks: %.3fms", divider * time_repo.get("the_nether.randomticks")));

		Messenger.print_server_message(server, "End:");

		accumulated += time_repo.get("the_end.entities");
		Messenger.print_server_message(server, String.format(" - Entities: %.3fms", divider * time_repo.get("the_end.entities")));

		accumulated += time_repo.get("the_end.tileentities");
		Messenger.print_server_message(server, String.format(" - Tile Entities: %.3fms", divider * time_repo.get("the_end.tileentities")));

		accumulated += time_repo.get("the_end.tickupdates");
		Messenger.print_server_message(server, String.format(" - Tick Updates: %.3fms", divider * time_repo.get("the_end.tickupdates")));

		accumulated += time_repo.get("the_end.spawning");
		Messenger.print_server_message(server, String.format(" - Spawning: %.3fms", divider * time_repo.get("the_end.spawning")));

		accumulated += time_repo.get("the_end.chunkmap");
		Messenger.print_server_message(server, String.format(" - Chunk Map: %.3fms", divider * time_repo.get("the_end.chunkmap")));

		accumulated += time_repo.get("the_end.villages");
		Messenger.print_server_message(server, String.format(" - Villages: %.3fms", divider * time_repo.get("the_end.villages")));

		accumulated += time_repo.get("the_end.portals");
		Messenger.print_server_message(server, String.format(" - Portalcaching: %.3fms", divider * time_repo.get("the_end.portals")));

		accumulated += time_repo.get("the_end.blockevents");
		Messenger.print_server_message(server, String.format(" - Queued Block Events: %.3fms", divider * time_repo.get("the_end.blockevents")));

		accumulated += time_repo.get("the_end.checknextlight");
		Messenger.print_server_message(server, String.format(" - Check Next Light: %.3fms", divider * time_repo.get("the_end.checknextlight")));

		accumulated += time_repo.get("the_end.tickchunk");
		Messenger.print_server_message(server, String.format(" - Tick Chunk: %.3fms", divider * time_repo.get("the_end.tickchunk")));

		accumulated += time_repo.get("the_end.thunder");
		Messenger.print_server_message(server, String.format(" - Thunder: %.3fms", divider * time_repo.get("the_end.thunder")));

		accumulated += time_repo.get("the_end.iceandsnow");
		Messenger.print_server_message(server, String.format(" - Ice&Snow: %.3fms", divider * time_repo.get("the_end.iceandsnow")));

		accumulated += time_repo.get("the_end.randomticks");
		Messenger.print_server_message(server, String.format(" - Random Ticks: %.3fms", divider * time_repo.get("the_end.randomticks")));

		long rest = total_tick_time - accumulated;

		Messenger.print_server_message(server, String.format("Rest: %.3fms", divider * rest));
	}

	private static void finalize_tick_report_for_entities(MinecraftServer server) {
		//print stats
		long total_tick_time = time_repo.get("tick");
		double divider = 1.0D / tick_health_requested / 1000000;
		Messenger.print_server_message(server, String.format("Average tick time: %.3fms", divider * total_tick_time));
		time_repo.remove("tick");
		Messenger.print_server_message(server, "Top 10 counts:");
		int total = 0;
		for (Map.Entry<String, Long> entry : time_repo.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList())) {
			if (entry.getKey().startsWith("t.")) {
				continue;
			}
			total++;
			if (total > 10) {
				continue;
			}
			String[] parts = entry.getKey().split("\\.");
			String dim = parts[1];
			String name = parts[2];
			Messenger.print_server_message(server, String.format(" - %s in %s: %.3f", name, dim, 1.0D * entry.getValue() / tick_health_requested));
		}
		Messenger.print_server_message(server, "Top 10 grossing:");
		total = 0;
		for (Map.Entry<String, Long> entry : time_repo.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList())) {
			if (entry.getKey().startsWith("c.")) {
				continue;
			}
			total++;
			if (total > 10) {
				continue;
			}
			String[] parts = entry.getKey().split("\\.");
			String dim = parts[1];
			String name = parts[2];
			Messenger.print_server_message(server, String.format(" - %s in %s: %.3fms", name, dim, divider * entry.getValue()));
		}
	}

	public static void prepare_entity_report(int ticks) {
		//maybe add so it only spams the sending player, but honestly - all may want to see it
		time_repo.clear();
		time_repo.put("tick", 0L);
		test_type = 2;
		tick_health_elapsed = ticks;
		tick_health_requested = ticks;
		current_tick_start = 0L;
		current_section_start = 0L;
		current_section = null;
	}

	private static String getMCPName(String notchName) {
		if (isDev) {
			return notchName;
		} else {
			return notchToMcp.getOrDefault(notchName, "Unknown");
		}
	}

	static {
		try {
			Class.forName("net.minecraft.world.World");
			isDev = true;
		} catch (Exception ignored) {
		}

		notchToMcp.put("aaa", "Mooshroom");
		notchToMcp.put("aab", "Ocelot");
		notchToMcp.put("aac", "Parrot");
		notchToMcp.put("aad", "Pig");
		notchToMcp.put("aae", "Polar Bear");
		notchToMcp.put("aaf", "Rabbit");
		notchToMcp.put("aag", "Sheep");
		notchToMcp.put("aai", "Snowman");
		notchToMcp.put("aaj", "Squid");
		notchToMcp.put("aak", "Iron Golem");
		notchToMcp.put("aam", "Wolf");
		notchToMcp.put("aap", "Donkey");
		notchToMcp.put("aaq", "Horse");
		notchToMcp.put("aas", "Llama");
		notchToMcp.put("aat", "Mule");
		notchToMcp.put("aau", "Skeleton Horse");
		notchToMcp.put("aaw", "Zombie Horse");
		notchToMcp.put("abc", "Ender Crystal");
		notchToMcp.put("abd", "Dragon");
		notchToMcp.put("abx", "Wither");
		notchToMcp.put("abz", "Armor Stand");
		notchToMcp.put("acb", "Item Frame");
		notchToMcp.put("acc", "Leash Knot");
		notchToMcp.put("acd", "Painting");
		notchToMcp.put("acf", "Fish Hook");
		notchToMcp.put("aci", "Lightning Bolt");
		notchToMcp.put("ack", "Falling Block");
		notchToMcp.put("acl", "Item");
		notchToMcp.put("acm", "Primed TNT");
		notchToMcp.put("acq", "Blaze");
		notchToMcp.put("acr", "Cave Spider");
		notchToMcp.put("acs", "Creeper");
		notchToMcp.put("act", "Elder Guardian");
		notchToMcp.put("acu", "Enderman");
		notchToMcp.put("acv", "Endermite");
		notchToMcp.put("acx", "Evoker");
		notchToMcp.put("acy", "Ghast");
		notchToMcp.put("acz", "Giant Zombie");
		notchToMcp.put("ada", "Guardian");
		notchToMcp.put("adb", "Husk");
		notchToMcp.put("adc", "Illusioner");
		notchToMcp.put("add", "Magma Cube");
		notchToMcp.put("adf", "Zombie Pigman");
		notchToMcp.put("adi", "Shulker");
		notchToMcp.put("adj", "Silverfish");
		notchToMcp.put("adk", "Skeleton");
		notchToMcp.put("adl", "Slime");
		notchToMcp.put("adn", "Spider");
		notchToMcp.put("ado", "Stray");
		notchToMcp.put("adp", "Vex");
		notchToMcp.put("adq", "Vindicator");
		notchToMcp.put("adr", "Witch");
		notchToMcp.put("ads", "Wither Skeleton");
		notchToMcp.put("adt", "Zombie");
		notchToMcp.put("adu", "Zombie Villager");
		notchToMcp.put("ady", "Villager");
		notchToMcp.put("aeh", "Arrow");
		notchToMcp.put("aei", "Dragon Fireball");
		notchToMcp.put("aej", "Evoker Fangs");
		notchToMcp.put("aek", "EnderEye");
		notchToMcp.put("ael", "Fireball");
		notchToMcp.put("aem", "Firework Rocket");
		notchToMcp.put("aen", "Large Fireball");
		notchToMcp.put("aeo", "LlamaSpit");
		notchToMcp.put("aer", "Shulker Bullet");
		notchToMcp.put("aes", "Small Fireball");
		notchToMcp.put("aet", "Snowball");
		notchToMcp.put("aeu", "Spectral Arrow");
		notchToMcp.put("aew", "Egg");
		notchToMcp.put("aex", "Ender Pearl");
		notchToMcp.put("aey", "Exp Bottle");
		notchToMcp.put("aez", "Potion");
		notchToMcp.put("afa", "Tipped Arrow");
		notchToMcp.put("afb", "Wither Skull");
		notchToMcp.put("afd", "Boat");
		notchToMcp.put("aff", "Chest Minecart");
		notchToMcp.put("afg", "CommandBlock Minecart");
		notchToMcp.put("afh", "Container Minecart");
		notchToMcp.put("afi", "Furnace Minecart");
		notchToMcp.put("afj", "Hopper Minecart");
		notchToMcp.put("afk", "EmptyMinecart");
		notchToMcp.put("afl", "Mob Spawner Minecart");
		notchToMcp.put("afm", "TNT Minecart");
		notchToMcp.put("avf", "Banner");
		notchToMcp.put("avh", "Beacon");
		notchToMcp.put("avi", "Bed");
		notchToMcp.put("avk", "Brewing Stand");
		notchToMcp.put("avl", "Chest");
		notchToMcp.put("avm", "Command Block");
		notchToMcp.put("avn", "Comparator");
		notchToMcp.put("avo", "Daylight Sensor");
		notchToMcp.put("avp", "Dispenser");
		notchToMcp.put("avq", "Dropper");
		notchToMcp.put("avr", "Enchantment Table");
		notchToMcp.put("avs", "Ender Chest");
		notchToMcp.put("avt", "Flower Pot");
		notchToMcp.put("avu", "Furnace");
		notchToMcp.put("avw", "Hopper");
		notchToMcp.put("avy", "Mob Spawner");
		notchToMcp.put("avz", "Noteblock");
		notchToMcp.put("awb", "Shulker Box");
		notchToMcp.put("awc", "Sign");
		notchToMcp.put("awd", "Skull");
		notchToMcp.put("awe", "Structure");
		notchToMcp.put("awf", "End Gateway");
		notchToMcp.put("awg", "End Portal");
		notchToMcp.put("awm", "Piston");
		notchToMcp.put("oq", "Player");
		notchToMcp.put("ve", "Area Effect Cloud");
		notchToMcp.put("vm", "XP Orb");
		notchToMcp.put("zt", "Bat");
		notchToMcp.put("zw", "Chicken");
		notchToMcp.put("zx", "Cow");
	}
}
