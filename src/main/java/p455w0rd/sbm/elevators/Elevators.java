package p455w0rd.sbm.elevators;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(Elevators.DOMAIN)
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(Elevators.ELEVATOR_BLOCK, 1, 0);
        }
    };

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
        ELEVATOR_BLOCK = new BlockColored(Material.CLOTH)
        {
            @Override
            public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type)
            {
                return ConfigMain.allowMobSpawning && super.canCreatureSpawn(state, world, pos, type);
            }
        }
                .setRegistryName(new ResourceLocation(Elevators.DOMAIN, "elevator"))
                .setTranslationKey(Elevators.DOMAIN + ":elevator")
                .setCreativeTab(CREATIVE_TAB)
                .setHardness(0.8F);
        event.getRegistry().register(ELEVATOR_BLOCK);
    }

    @SubscribeEvent
    public static void onItemRegistryReady(final RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemCloth(ELEVATOR_BLOCK).setRegistryName(Elevators.ELEVATOR_BLOCK.getRegistryName()).setHasSubtypes(true));
    }
}
