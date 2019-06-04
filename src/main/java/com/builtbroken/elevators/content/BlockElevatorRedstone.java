package com.builtbroken.elevators.content;

import com.builtbroken.elevators.config.ConfigMain;
import com.builtbroken.elevators.logic.MoveDirection;
import com.builtbroken.elevators.logic.TeleportHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            int redstone = worldIn.getRedstonePowerFromNeighbors(pos);
            if (redstone > 0)
            {
                final List<Entity> entityList = worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up()));
                if (redstone < 7)
                {
                    entityList.forEach(entity -> TeleportHelper.tryToTeleport(entity, MoveDirection.DOWN));
                }
                else
                {
                    entityList.forEach(entity -> TeleportHelper.tryToTeleport(entity, MoveDirection.UP));
                }
            }
        }
    }
}
