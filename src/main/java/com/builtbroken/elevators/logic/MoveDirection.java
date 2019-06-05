package com.builtbroken.elevators.logic;

import net.minecraft.world.World;

import java.util.function.IntSupplier;

public enum MoveDirection
{
    UP((world, h) -> h < world.getHeight(), () -> 1),
    DOWN((world, h) -> h >= 0, () -> -1);

    public final HeightCheck heightCheck;
    public final IntSupplier delta;

    MoveDirection(HeightCheck heightCheck, IntSupplier delta)
    {
        this.heightCheck = heightCheck;
        this.delta = delta;
    }

    public boolean limit(World world, int height)
    {
        return heightCheck.limit(world, height);
    }


    public static MoveDirection get(int index)
    {
        if (index >= 0 && index < values().length)
        {
            return values()[index];
        }
        return UP;
    }

    public static interface HeightCheck
    {
        boolean limit(World world, int height);
    }
}
