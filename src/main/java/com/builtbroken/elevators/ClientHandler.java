package com.builtbroken.elevators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
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

    @SubscribeEvent
    public static void onInput(InputUpdateEvent inputEvent)
    {
        //TODO add a delay to prevent macro spam
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (!player.isSpectator())
        {
            if (wasCrouching != player.isSneaking())
            {
                wasCrouching = player.isSneaking();
                if (wasCrouching)
                {
                    Elevators.NETWORK.sendToServer(new PacketTryMovement(MoveDirection.DOWN));
                }
            }
            boolean jumping = TeleportHelper.isJumping(player);
            if (wasJumping != jumping)
            {
                wasJumping = jumping;
                if (wasJumping)
                {
                    Elevators.NETWORK.sendToServer(new PacketTryMovement(MoveDirection.UP));
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerModels(final ModelRegistryEvent event)
    {
        for (EnumDyeColor color : EnumDyeColor.values())
        {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Elevators.ELEVATOR_BLOCK), color.ordinal(), new ModelResourceLocation(Elevators.ELEVATOR_BLOCK.getRegistryName(), "color=" + color.getName()));
        }
    }
}
