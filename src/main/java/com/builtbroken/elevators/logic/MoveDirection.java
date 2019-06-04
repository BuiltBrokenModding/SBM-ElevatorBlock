package com.builtbroken.elevators.logic;

public enum MoveDirection
{
    UP, DOWN;

    public static MoveDirection get(int index)
    {
        if (index >= 0 && index < values().length)
        {
            return values()[index];
        }
        return UP;
    }
}
