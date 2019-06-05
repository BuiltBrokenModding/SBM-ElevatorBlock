package com.builtbroken.elevators.config;

import com.builtbroken.elevators.Elevators;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Elevators.DOMAIN, name = "sbm/elevators/content")
@Config.LangKey("config." + Elevators.DOMAIN + ":content.title")
@Mod.EventBusSubscriber(modid = Elevators.DOMAIN)
public class ConfigContent
{
    @Config.Name("enable_basic_lift")
    @Config.LangKey("config." + Elevators.DOMAIN + ":elevator.basic.enable.title")
    @Config.Comment("Enables Basic Elevator Block")
    public static boolean enableBasicLift = true;

    @Config.Name("enable_redstone_lift")
    @Config.LangKey("config." + Elevators.DOMAIN + ":elevator.redstone.enable.title")
    @Config.Comment("Enables Redstone Elevator Block")
    public static boolean enableRedstoneLift = true;
}
