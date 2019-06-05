package com.builtbroken.elevators.content;

import com.builtbroken.elevators.logic.MoveDirection;
import com.builtbroken.elevators.logic.TeleportHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Dark(DarkGuardsman, Robert) on 6/2/2019.
 */
public class BlockElevatorRedstone extends BlockElevator
{
    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
    {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            int redstone = getRedstone(worldIn, pos);
            System.out.println(redstone);
            if (redstone > 0)
            {
                final List<Entity> entityList = worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up()));
                if (redstone <= 7)
                {
                    entityList.forEach(entity -> TeleportHelper.tryToTeleport(entity, MoveDirection.DOWN, this));
                }
                else
                {
                    entityList.forEach(entity -> TeleportHelper.tryToTeleport(entity, MoveDirection.UP, this));
                }
            }
        }
    }

    public int getRedstonePower(World world, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        int power = Math.max(world.getStrongPower(pos), iblockstate.getWeakPower(world, pos, facing));
        if(iblockstate.getBlock() == Blocks.REDSTONE_WIRE)
        {
            return Math.max(power, ((Integer)iblockstate.getValue(BlockRedstoneWire.POWER)).intValue());
        }
        return power;
    }

    public int getRedstone(World world, BlockPos pos)
    {
        int max = 0;
        for (EnumFacing facing : EnumFacing.values())
        {
            max = Math.max(max, this.getRedstonePower(world, pos.offset(facing), facing));
        }
        return max;
    }
}
