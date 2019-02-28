package com.builtbroken.elevators.config;

import com.builtbroken.elevators.Elevators;
import net.minecraftforge.common.config.Config;

@Config(modid = Elevators.DOMAIN, name = "sbm/elevators/main")
@Config.LangKey("config." + Elevators.DOMAIN + ":main.title")
public class ConfigMain
{
    @Config.Name("allow_mob_spawning")
    @Config.LangKey("config." + Elevators.DOMAIN + ":mob.spawning.title")
    @Config.Comment("Toggles mobs spawning on the block on/off")
    public static boolean allowMobSpawning = false;

    @Config.Name("require_sight")
    @Config.LangKey("config." + Elevators.DOMAIN + ":require.sight.title")
    @Config.Comment("Toggle to require line of sight (no blocks between) each pad to allow movement")
    public static boolean requireClearLineOfSight = false;

    @Config.Name("require_color")
    @Config.LangKey("config." + Elevators.DOMAIN + ":require.color.title")
    @Config.Comment("Toggles switch between forcing pads to match color or allowing any color")
    public static boolean mustBeSameColor = true;

    @Config.Name("xp_cost")
    @Config.LangKey("config." + Elevators.DOMAIN + ":xp.cost.title")
    @Config.Comment("XP required to use the pad, set to -1 to disable")
    public static int xp_consumed = -1;

    @Config.Name("xp_cost_distance")
    @Config.LangKey("config." + Elevators.DOMAIN + ":xp.cost.distance.title")
    @Config.Comment("Toggle to make XP cost be based on the distance traveled.")
    public static boolean xp_per_distance = false;

    @Config.Name("min_spacing")
    @Config.LangKey("config." + Elevators.DOMAIN + ":spacing.min.title")
    @Config.Comment("Minimal distance allowed between pads")
    public static int min_spacing = 3;

    @Config.Name("max_spacing")
    @Config.LangKey("config." + Elevators.DOMAIN + ":spacing.min.title")
    @Config.Comment("Max distance allowed between pads, set to -1 to not care")
    public static int max_spacing = -1;
}
