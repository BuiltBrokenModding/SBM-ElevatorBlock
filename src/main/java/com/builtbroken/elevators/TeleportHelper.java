package com.builtbroken.elevators;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Created by Dark(DarkGuardsman, Robert) on 2/27/2019.
 */
public class TeleportHelper
{

    public static boolean isJumping(EntityPlayer player) //TODO replace with AT
    {
        return ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, player, "field_70703_bu", "isJumping");
    }

    /**
     * Tries to teleport the player up or down
     *
     * @param player
     * @param direction
     */
    public static void tryToTeleport(EntityPlayer player, MoveDirection direction)
    {
        final World world = player.getEntityWorld();
        final BlockPos fromPos = getPosUnderPlayer(player);
        final IBlockState fromState = world.getBlockState(fromPos);
        final BlockPos toPos = getNearestElevator(world, fromState, fromPos, direction);

        if (toPos != null)
        {
            //Set position TODO fire event to hook and block
            player.setPositionAndUpdate(toPos.getX() + 0.5f, toPos.getY() + 1, toPos.getZ() + 0.5f);
            player.motionY = 0;

            //Trigger audio
            world.playSound(null, toPos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        else
        {
            //TODO play failed audio with message
        }
    }

    public static BlockPos getNearestElevator(World world, IBlockState fromState, BlockPos elevatorPos, MoveDirection direction)
    {
        BlockPos startPos = direction == MoveDirection.UP ? elevatorPos.up(3) : elevatorPos.down(3);
        int spaceBetweenElevators = 2;
        for (int i = startPos.getY(); direction == MoveDirection.UP ? i < 256 : i > 0; i += (direction == MoveDirection.UP ? 1 : -1))
        {
            BlockPos currentPos = new BlockPos(elevatorPos.getX(), i, elevatorPos.getZ());
            IBlockState currentState = world.getBlockState(currentPos);
            if (!isSameType(currentState, fromState) && ConfigMain.requireClearLineOfSight && !isBlockPassable(world, currentState, currentPos))
            {
                return null;
            }
            if (isElevator(currentState) && isSameType(currentState, fromState) && isBlockSafeToTeleportTo(world, currentPos) && spaceBetweenElevators >= ConfigMain.spaceBetweenElevators)
            {
                return currentPos;
            }
            spaceBetweenElevators++;
        }
        return null;
    }

    private static BlockPos getPosUnderPlayer(EntityPlayer player)
    {
        int x = MathHelper.floor(player.posX);
        int y = MathHelper.floor(player.getEntityBoundingBox().minY) - 1;
        int z = MathHelper.floor(player.posZ);
        return new BlockPos(x, y, z);
    }

    private static boolean isBlockSafeToTeleportTo(World world, BlockPos pos)
    {
        return world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2));
    }

    private static boolean isBlockPassable(World world, IBlockState blockState, BlockPos pos)
    {
        return world.isAirBlock(pos);
    }

    private static boolean isSameType(IBlockState fromState, IBlockState toState)
    {
        return ConfigMain.mustBeSameColor ? fromState == toState : fromState.getBlock() == toState.getBlock();
    }

    private static boolean isElevator(IBlockState blockState)
    {
        return blockState.getBlock() == Elevators.ELEVATOR_BLOCK;
    }

}
