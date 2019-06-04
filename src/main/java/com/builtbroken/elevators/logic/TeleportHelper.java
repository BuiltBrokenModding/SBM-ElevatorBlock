package com.builtbroken.elevators.logic;

import com.builtbroken.elevators.Elevators;
import com.builtbroken.elevators.config.ConfigMain;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSetExperience;
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
     * @param entity
     * @param direction
     */
    public static boolean tryToTeleport(Entity entity, MoveDirection direction)
    {
        final World world = entity.getEntityWorld();
        final BlockPos fromPos = getPosUnderEntity(entity);
        final IBlockState fromState = world.getBlockState(fromPos);
        if(isElevator(fromState))
        {
            final BlockPos toPos = getNearestElevator(world, fromState, fromPos, direction);

            if (toPos != null)
            {
                //Check XP cost
                final int xp_cost = getXPCost(Math.abs(fromPos.getY() - toPos.getY()));
                if (!checkTeleport(entity, world, fromPos, xp_cost))
                {
                    return false;
                }

                //Set position TODO fire event to hook and block
                entity.setPositionAndUpdate(toPos.getX() + 0.5f, toPos.getY() + 1, toPos.getZ() + 0.5f);
                entity.motionY = 0;

                //Trigger audio
                world.playSound(null, toPos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);

                //Consume XP
                consumeXP(entity, xp_cost);

                return true;
            }
            else
            {
                //TODO tell the player why it failed
                world.playSound(null, fromPos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
        return false;
    }

    public static boolean checkTeleport(Entity entity, World world, BlockPos fromPos, int xp_cost)
    {
        if (entity instanceof EntityPlayer)
        {
            if(!((EntityPlayer)entity).capabilities.isCreativeMode && !hasEnoughXP((EntityPlayer)entity, xp_cost))
            {
                //TODO tell the player why it failed
                world.playSound(null, fromPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return false;
            }
            return true;
        }
        return ConfigMain.allowAnyEntity;
    }

    public static void consumeXP(Entity entity, int xp_cost)
    {
        if (xp_cost > 0 && entity instanceof EntityPlayer && !((EntityPlayer)entity).capabilities.isCreativeMode)
        {
            consumeXP((EntityPlayer)entity, xp_cost);
            if (entity instanceof EntityPlayerMP)
            {
                ((EntityPlayerMP) entity).connection.sendPacket(new SPacketSetExperience(((EntityPlayer)entity).experience, ((EntityPlayer)entity).experienceTotal, ((EntityPlayer)entity).experienceLevel));
            }
        }
    }

    public static boolean hasEnoughXP(EntityPlayer player, int amount)
    {
        //Cache values
        final float experience = player.experience;
        final int levels = player.experienceLevel;

        //Run consume, this will edit the player
        int re = consumeXP(player, amount);

        //Restore from cache
        player.experience = experience;
        player.experienceLevel = levels;

        //Return true if we consumed amount
        if (re <= 0)
        {
            return true;
        }
        return false;
    }

    public static int consumeXP(EntityPlayer player, int amount)
    {
        int xpInBar = (int) (player.experience * player.xpBarCap());
        do
        {
            if (xpInBar >= amount)
            {
                xpInBar -= amount;
                player.experience = Math.min(1, Math.max(0, (float) xpInBar / (float) player.xpBarCap()));

                return 0;
            }
            else
            {
                amount -= xpInBar;
                player.experience = 0;
                if(player.experienceLevel > 0)
                {
                    player.experienceLevel -= 1;
                    xpInBar = player.xpBarCap();
                }
            }
        } while (amount > 0 && (player.experienceLevel > 0 || xpInBar > 0));

        return amount;
    }

    public static int getXPCost(int distance)
    {
        if (ConfigMain.xp_consumed > 0)
        {
            return ConfigMain.xp_per_distance ? ConfigMain.xp_consumed * distance : ConfigMain.xp_consumed;
        }
        return 0;
    }


    public static BlockPos getNearestElevator(World world, IBlockState fromState, BlockPos elevatorPos, MoveDirection direction)
    {
        final BlockPos startPos = direction == MoveDirection.UP ? elevatorPos.up(3) : elevatorPos.down(3);
        int spaceBetweenElevators = 2;

        for (int i = startPos.getY(); direction == MoveDirection.UP ? i < 256 : i > 0; i += (direction == MoveDirection.UP ? 1 : -1))
        {
            //Check that we have a valid match
            BlockPos currentPos = new BlockPos(elevatorPos.getX(), i, elevatorPos.getZ());
            IBlockState currentState = world.getBlockState(currentPos);
            if (!isSameType(currentState, fromState) && ConfigMain.requireClearLineOfSight && !isBlockPassable(world, currentState, currentPos))
            {
                return null;
            }

            //Check if elevator is valid
            if (isElevator(currentState) && isSameType(currentState, fromState) && isBlockSafeToTeleportTo(world, currentPos))
            {
                //Check that spacing is good
                if (spaceBetweenElevators >= ConfigMain.min_spacing && (spaceBetweenElevators <= ConfigMain.max_spacing || ConfigMain.max_spacing < ConfigMain.min_spacing))
                {
                    return currentPos;
                }
                else
                {
                    //TODO error to player to tell them the pad is setup wrong
                    return null;
                }
            }
            else
            {
                //TODO error to player to tell them the pad is setup wrong
            }
            spaceBetweenElevators++;
        }
        return null;
    }

    public static BlockPos getPosUnderEntity(Entity entity)
    {
        int x = MathHelper.floor(entity.posX);
        int y = MathHelper.floor(entity.getEntityBoundingBox().minY) - 1;
        int z = MathHelper.floor(entity.posZ);
        return new BlockPos(x, y, z);
    }

    public static boolean isBlockSafeToTeleportTo(World world, BlockPos pos)
    {
        return world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2));
        //TODO change to collision check to allow buttons and signs
    }

    public static boolean isBlockPassable(World world, IBlockState blockState, BlockPos pos)
    {
        return world.isAirBlock(pos);
        //TODO change to collision check to allow buttons and signs
    }

    public static boolean isSameType(IBlockState fromState, IBlockState toState)
    {
        return ConfigMain.mustBeSameColor ? fromState == toState : fromState.getBlock() == toState.getBlock();
    }

    public static boolean isElevator(IBlockState blockState)
    {
        return blockState.getBlock() == Elevators.ELEVATOR_BLOCK;
    }

}
