package com.builtbroken.elevators.config;

import com.builtbroken.elevators.Elevators;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Elevators.DOMAIN, name = "sbm/elevators/main")
@Config.LangKey("config." + Elevators.DOMAIN + ":main.title")
@Mod.EventBusSubscriber(modid = Elevators.DOMAIN)
public class ConfigMain
{
    @Config.Name("allow_mob_spawning")
    @Config.LangKey("config." + Elevators.DOMAIN + ":mob.spawning.title")
    @Config.Comment("Toggles mobs spawning on the block on/off")
    public static boolean allowMobSpawning = false;

    @Config.Name("allow_any_entity")
    @Config.LangKey("config." + Elevators.DOMAIN + ":allow.any.entity.title")
    @Config.Comment("Toggles allowing entities other than the player to be teleport. Only works for automatic teleporters.")
    public static boolean allowAnyEntity = true;

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

    @Config.Name("teleport_delay")
    @Config.LangKey("config." + Elevators.DOMAIN + ":teleport.delay.title")
    @Config.Comment("Time in miliseconds to wait until teleporting again, set to -1 to ignore")
    public static int tp_delay = 100;

    @Config.Name("xp_cost_distance")
    @Config.LangKey("config." + Elevators.DOMAIN + ":xp.cost.distance.title")
    @Config.Comment("Toggle to make XP cost be based on the distance traveled.")
    public static boolean xp_per_distance = false;

    @Config.Name("min_spacing")
    @Config.LangKey("config." + Elevators.DOMAIN + ":spacing.min.title")
    @Config.Comment("Minimal distance allowed between pads")
    public static int min_spacing = 3;

    @Config.Name("max_spacing")
    @Config.LangKey("config." + Elevators.DOMAIN + ":spacing.max.title")
    @Config.Comment("Max distance allowed between pads, set to -1 to not care")
    public static int max_spacing = -1;

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Elevators.DOMAIN))
        {
            ConfigManager.sync(Elevators.DOMAIN, Config.Type.INSTANCE);
        }
    }
}
