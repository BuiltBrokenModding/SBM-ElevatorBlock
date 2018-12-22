package p455w0rd.sbm.elevators.init;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ModConfig extends Configuration {

	private static final ModConfig INSTANCE = new ModConfig(new File(ModGlobals.CONFIG_FILE));

	private ModConfig(File file) {
		super(file);
	}

	public static ModConfig getInstance() {
		return INSTANCE;
	}

	public void init() {
		load();
		sync();
	}

	public void sync() {
		Options.allowMobSpawning = getInstance().getBoolean("allowMobSpawning", CATEGORY_GENERAL, true, "Setting to true will allow mobs to spawn on Elevator blocks");
		Options.requireClearLineOfSight = getInstance().getBoolean("requireClearLineOfSight", CATEGORY_GENERAL, true, "Setting to true will force there to only be air blocks between Elevators");
		Options.mustBeSameColor = getInstance().getBoolean("mustBeSameColor", CATEGORY_GENERAL, true, "If true you can only teleport between elevators of the same color");
		Options.spaceBetweenElevators = getInstance().getInt("sapceBetweenElevators", CATEGORY_GENERAL, 2, 2, 252, "Number of blocks between Elevator blocks for them to function");
		if (getInstance().hasChanged()) {
			getInstance().save();
		}
	}

	public static class Options {

		public static boolean allowMobSpawning;
		public static boolean requireClearLineOfSight;
		public static boolean mustBeSameColor;
		public static int spaceBetweenElevators;

	}
}
