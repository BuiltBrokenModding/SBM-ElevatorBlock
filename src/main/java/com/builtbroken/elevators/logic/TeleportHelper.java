package com.builtbroken.elevators.logic;

import com.builtbroken.elevators.Elevators;
import com.builtbroken.elevators.config.ConfigMain;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dark(DarkGuardsman, Robert) on 2/27/2019.
 */
public class TeleportHelper
{

    public static final String NBT_LAST_TIME = Elevators.DOMAIN + ":last_teleport_time";

    public static boolean isJumping(EntityPlayer player) //TODO replace with AT
    {
        return ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, player, "field_70703_bu", "isJumping");
    }

    /**
     * Tries to teleport the player up or down
     *
     * @param entity       - entity to teleport
     * @param direction    - direction to teleport
     * @param blockToForce - optional block to force teleportation with
     */
    public static boolean tryToTeleport(Entity entity, MoveDirection direction, @Nullable Block blockToForce)
    {
        final World world = entity.getEntityWorld();
        final BlockPos fromPos = getPosUnderEntity(entity);

        //Validate we are using the right block, used to limit different types of functionality
        if (blockToForce != null && world.getBlockState(fromPos).getBlock() != blockToForce) //TODO find a better way to manage this
        {
            return false;
        }

        //Get last teleport time
        long lastTeleportTime = 0;
        if (entity.getEntityData().hasKey(NBT_LAST_TIME)) //TODO consider using world time instead
        {
            lastTeleportTime = entity.getEntityData().getLong(NBT_LAST_TIME);
        }

        //Limit last teleport time
        if (System.currentTimeMillis() - lastTeleportTime > ConfigMain.tp_delay)
        {
            final IBlockState fromState = world.getBlockState(fromPos);
            if (isElevator(fromState))
            {
                final BlockPos toPos = getNearestElevator(world, fromState, fromPos, direction, entity);

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

                    //Save last teleport time
                    entity.getEntityData().setLong(NBT_LAST_TIME, System.currentTimeMillis());

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
        }
        else
        {
            //TODO tell the player why it failed
            world.playSound(null, fromPos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);

        }
        return false;
    }

    public static boolean checkTeleport(Entity entity, World world, BlockPos fromPos, int xp_cost)
    {
        if (entity instanceof EntityPlayer)
        {
            if (!((EntityPlayer) entity).capabilities.isCreativeMode && !hasEnoughXP((EntityPlayer) entity, xp_cost))
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
        if (xp_cost > 0 && entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
        {
            consumeXP((EntityPlayer) entity, xp_cost);
            if (entity instanceof EntityPlayerMP)
            {
                ((EntityPlayerMP) entity).connection.sendPacket(new SPacketSetExperience(((EntityPlayer) entity).experience, ((EntityPlayer) entity).experienceTotal, ((EntityPlayer) entity).experienceLevel));
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
                if (player.experienceLevel > 0)
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


    public static BlockPos getNearestElevator(World world, IBlockState fromState, BlockPos elevatorPos, MoveDirection direction, Entity entity)
    {
        final BlockPos startPos = direction == MoveDirection.UP ? elevatorPos.up() : elevatorPos.down();
        int spaceBetweenElevators = 1;

        //Move up or down until we find another elevator
        for (int y = startPos.getY(); direction.limit(world, y); y += direction.delta.getAsInt())
        {
            //Check that we have a valid match
            final BlockPos currentPos = new BlockPos(elevatorPos.getX(), y, elevatorPos.getZ());
            final IBlockState currentState = world.getBlockState(currentPos);

            //Line of sight check
            if (checkLineMovement(world, currentPos, currentState, fromState, entity))
            {
                return null;
            }

            //Check if elevator is valid
            if (currentState.getBlock() == fromState.getBlock() && isSameType(currentState, fromState))
            {
                if (!isDistanceValid(spaceBetweenElevators) || !isBlockSafeToTeleportTo(world, currentPos, entity))
                {
                    //TODO error to player to tell them the pad is blocked or spacing is bad
                    return null;
                }

                return currentPos;
            }
            spaceBetweenElevators++;
        }
        return null;
    }

    public static boolean checkLineMovement(World world, BlockPos currentPos, IBlockState currentState, IBlockState fromState, Entity entity)
    {
        return !isSameType(currentState, fromState) && ConfigMain.requireClearLineOfSight
                && !isBlockPassable(world, currentState, currentPos, entity);

    }

    public static boolean isDistanceValid(int spaceBetweenElevators)
    {
        return spaceBetweenElevators >= ConfigMain.min_spacing
                && (spaceBetweenElevators <= ConfigMain.max_spacing || ConfigMain.max_spacing < ConfigMain.min_spacing);

    }

    public static BlockPos getPosUnderEntity(Entity entity)
    {
        int x = MathHelper.floor(entity.posX);
        int y = MathHelper.floor(entity.getEntityBoundingBox().minY) - 1;
        int z = MathHelper.floor(entity.posZ);
        return new BlockPos(x, y, z);
    }

    public static boolean isBlockSafeToTeleportTo(World world, BlockPos pos, Entity entity)
    {
        return isNotColliding(world, pos.up(), entity);
        //TODO change to collision check to allow buttons and signs
    }

    public static boolean isNotColliding(World world, BlockPos pos, Entity entity)
    {
        int h = (int) Math.ceil(entity.height);
        for (int i = 0; i < h; i++)
        {
            final BlockPos currentPos = pos.up(i);
            if (!isBlockPassable(world, world.getBlockState(currentPos), currentPos, entity))
            {
                return false;
            }
        }
        return true;
    }

    public static AxisAlignedBB getBoundingBoxAtPosition(Entity entity, BlockPos pos)
    {
        return getBoundingBoxAtPosition(entity, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public static AxisAlignedBB getBoundingBoxAtPosition(Entity entity, double x, double y, double z)
    {
        float f = entity.width / 2.0F;
        float f1 = entity.height;
        return new AxisAlignedBB(
                x - (double) f, y, z - (double) f,
                x + (double) f, y + (double) f1, z + (double) f);
    }

    public static boolean isBlockPassable(World world, IBlockState blockState, BlockPos pos, Entity entity)
    {
        final Block block = blockState.getBlock();
        if (!block.isCollidable() || block.isAir(blockState, world, pos))
        {
            return true;
        }
        //Check for empty bounding box
        else if (blockState.getCollisionBoundingBox(world, pos) == Block.NULL_AABB)
        {
            //Attempt to get sub boxes, some blocks will do null aabb in order to have a custom shape (fence)
            final List<AxisAlignedBB> boxes = new ArrayList();
            blockState.addCollisionBoxToList(world, pos, getBoundingBoxAtPosition(entity, pos), boxes, entity, false);

            //If empty then we have no collisions
            if (boxes.isEmpty())
            {
                return true;
            }
        }
        return false;
        //TODO change to collision check to allow buttons and signs
    }

    public static boolean isSameType(IBlockState fromState, IBlockState toState)
    {
        return ConfigMain.mustBeSameColor ? fromState == toState : fromState.getBlock() == toState.getBlock();
    }

    public static boolean isElevator(IBlockState blockState)
    {
        return blockState.getBlock() == Elevators.ELEVATOR_BLOCK || blockState.getBlock() == Elevators.ELEVATOR_BLOCK_REDSTONE;
    }

}
