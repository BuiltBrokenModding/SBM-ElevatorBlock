package com.builtbroken.elevators;

import net.minecraftforge.common.config.Config;

@Config(modid = Elevators.DOMAIN)
public class ConfigMain
{

    public static boolean allowMobSpawning;
    public static boolean requireClearLineOfSight;
    public static boolean mustBeSameColor;
    public static int spaceBetweenElevators;
}
