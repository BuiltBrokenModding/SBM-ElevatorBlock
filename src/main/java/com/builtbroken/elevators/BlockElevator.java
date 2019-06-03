package com.builtbroken.elevators;

import com.builtbroken.elevators.config.ConfigMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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

import java.util.List;

/**
 * Created by Dark(DarkGuardsman, Robert) on 6/2/2019.
 */
class BlockElevator extends BlockColored
{

    public BlockElevator()
    {
        super(Material.CLOTH);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if (heldItem.getItem() == Items.DYE)
        {
            return recolorBlock(worldIn, pos, facing, EnumDyeColor.byDyeDamage(heldItem.getItemDamage()));
        }
        return false;
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

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type)
    {
        return ConfigMain.allowMobSpawning && super.canCreatureSpawn(state, world, pos, type);
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, net.minecraft.item.EnumDyeColor color)
    {
        IBlockState state = world.getBlockState(pos);
        for (IProperty prop : state.getProperties().keySet())
        {
            if (prop.getName().equals("color") && prop.getValueClass() == net.minecraft.item.EnumDyeColor.class)
            {
                net.minecraft.item.EnumDyeColor current = (net.minecraft.item.EnumDyeColor) state.getValue(prop);
                if (current != color && prop.getAllowedValues().contains(color))
                {
                    world.setBlockState(pos, state.withProperty(prop, color));
                    return true;
                }
            }
        }
        return false;
    }
}
