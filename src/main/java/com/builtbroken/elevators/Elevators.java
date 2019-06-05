package com.builtbroken.elevators;

import com.builtbroken.elevators.config.ConfigContent;
import com.builtbroken.elevators.content.BlockElevator;
import com.builtbroken.elevators.content.BlockElevatorRedstone;
import com.builtbroken.elevators.logic.PacketTryMovement;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Elevators.DOMAIN, name = "[SBM] Elevators", version = Elevators.VERSION)
@Mod.EventBusSubscriber(modid = Elevators.DOMAIN)
public class Elevators
{

    //References
    public static final String DOMAIN = "sbmelevators";

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String MC_VERSION = "@MC@";
    public static final String VERSION = MC_VERSION + "-" + MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    //Block
    @GameRegistry.ObjectHolder(value = DOMAIN + ":elevator")
    public static Block ELEVATOR_BLOCK;

    @GameRegistry.ObjectHolder(value = DOMAIN + ":elevator_redstone")
    public static Block ELEVATOR_BLOCK_REDSTONE;

    //Audio
    public static final ResourceLocation SOUND_ID = new ResourceLocation(Elevators.DOMAIN, "teleport");
    public static final SoundEvent SOUND_EVENT = new SoundEvent(SOUND_ID).setRegistryName(SOUND_ID);

    //Networking
    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(Elevators.DOMAIN);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NETWORK.registerMessage(PacketTryMovement.Handler.class, PacketTryMovement.class, 0, Side.SERVER);
    }

    @SubscribeEvent
    public static void onSoundRegistryReady(RegistryEvent.Register<SoundEvent> event)
    {
        event.getRegistry().register(SOUND_EVENT);
    }

    @SubscribeEvent
    public static void onBlockRegistryReady(final RegistryEvent.Register<Block> event)
    {
        if (ConfigContent.enableBasicLift)
        {
            ELEVATOR_BLOCK = new BlockElevator()
                    .setRegistryName(new ResourceLocation(Elevators.DOMAIN, "elevator"))
                    .setTranslationKey(Elevators.DOMAIN + ":elevator")
                    .setCreativeTab(CreativeTabs.TRANSPORTATION)
                    .setHardness(0.8F);
            event.getRegistry().register(ELEVATOR_BLOCK);
        }

        if (ConfigContent.enableRedstoneLift)
        {
            ELEVATOR_BLOCK_REDSTONE = new BlockElevatorRedstone()
                    .setRegistryName(new ResourceLocation(Elevators.DOMAIN, "elevator_redstone"))
                    .setTranslationKey(Elevators.DOMAIN + ":elevator_redstone")
                    .setCreativeTab(CreativeTabs.TRANSPORTATION)
                    .setHardness(0.8F);
            event.getRegistry().register(ELEVATOR_BLOCK_REDSTONE);
        }
    }

    @SubscribeEvent
    public static void onItemRegistryReady(final RegistryEvent.Register<Item> event)
    {
        if (ConfigContent.enableBasicLift)
        {
            event.getRegistry().register(new ItemCloth(ELEVATOR_BLOCK).setRegistryName(Elevators.ELEVATOR_BLOCK.getRegistryName()).setHasSubtypes(true));
        }
        if (ConfigContent.enableRedstoneLift)
        {
            event.getRegistry().register(new ItemCloth(ELEVATOR_BLOCK_REDSTONE).setRegistryName(Elevators.ELEVATOR_BLOCK_REDSTONE.getRegistryName()).setHasSubtypes(true));
        }
    }
}
