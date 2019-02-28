package p455w0rd.sbm.elevators;

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
