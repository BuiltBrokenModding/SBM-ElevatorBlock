package com.builtbroken.elevators;

import com.builtbroken.elevators.config.ConfigContent;
import com.builtbroken.elevators.logic.MoveDirection;
import com.builtbroken.elevators.logic.PacketTryMovement;
import com.builtbroken.elevators.logic.TeleportHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 */
@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = Elevators.DOMAIN, value = Side.CLIENT)
public class ClientHandler
{

    private static boolean wasCrouching;
    private static boolean wasJumping;

    private static long lastClickTime = 0;
    private static long minClickTime = 50;

    @SubscribeEvent
    public static void onInput(InputUpdateEvent inputEvent)
    {
        //validate the game is running
        if (Minecraft.getMinecraft() != null)
        {
            //Validate the player
            final EntityPlayer player = Minecraft.getMinecraft().player;
            if (player != null && !player.isSpectator())
            {
                //Validate the block is an elevator
                final BlockPos fromPos = TeleportHelper.getPosUnderEntity(player);
                final IBlockState fromState = player.world.getBlockState(fromPos);
                if (TeleportHelper.isElevator(fromState))
                {
                    //Handle down movement
                    if (wasCrouching != player.isSneaking())
                    {
                        wasCrouching = player.isSneaking();
                        if (wasCrouching && checkClickTime())
                        {
                            Elevators.NETWORK.sendToServer(new PacketTryMovement(MoveDirection.DOWN));
                        }
                    }

                    //Handle up movement
                    boolean jumping = TeleportHelper.isJumping(player);
                    if (wasJumping != jumping)
                    {
                        wasJumping = jumping;
                        if (wasJumping && checkClickTime())
                        {
                            Elevators.NETWORK.sendToServer(new PacketTryMovement(MoveDirection.UP));
                        }
                    }
                }
            }
        }
    }

    //Controls delay between clicks to prevent macro spam
    private static boolean checkClickTime()
    {
        if (System.currentTimeMillis() - lastClickTime >= minClickTime)
        {
            lastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void registerModels(final ModelRegistryEvent event)
    {
        if (ConfigContent.enableBasicLift)
        {
            for (EnumDyeColor color : EnumDyeColor.values())
            {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Elevators.ELEVATOR_BLOCK), color.ordinal(), new ModelResourceLocation(Elevators.ELEVATOR_BLOCK.getRegistryName(), "color=" + color.getName()));
            }
        }
        if (ConfigContent.enableRedstoneLift)
        {
            for (EnumDyeColor color : EnumDyeColor.values())
            {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Elevators.ELEVATOR_BLOCK_REDSTONE), color.ordinal(), new ModelResourceLocation(Elevators.ELEVATOR_BLOCK_REDSTONE.getRegistryName(), "color=" + color.getName()));
            }
        }
    }
}
