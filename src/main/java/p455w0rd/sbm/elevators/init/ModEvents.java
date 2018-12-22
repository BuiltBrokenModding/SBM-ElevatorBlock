package p455w0rd.sbm.elevators.init;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModEvents {

	@SubscribeEvent
	public static void onSoundRegistryReady(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(ModSounds.TELEPORT);
	}

	@SubscribeEvent
	public static void onBlockRegistryReady(final RegistryEvent.Register<Block> event) {
		event.getRegistry().register(ModBlocks.ELEVATOR_BLOCK);
	}

	@SubscribeEvent
	public static void onItemRegistryReady(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(ModBlocks.ELEVATOR_BLOCK) {

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				String colorName = EnumDyeColor.values()[stack.getItemDamage()].getUnlocalizedName();
				colorName = colorName.substring(0, 1).toUpperCase() + colorName.substring(1);
				return colorName.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2") + " " + I18n.translateToLocal(getUnlocalizedName() + ".name");
			}

			@Override
			public int getMetadata(int metadata) {
				return metadata;
			}

		}.setRegistryName(ModBlocks.ELEVATOR_BLOCK.getRegistryName()).setHasSubtypes(true));
	}

	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent event) {
		if (event.getModID().equals(ModGlobals.MODID)) {
			ModConfig.getInstance().sync();
		}
	}

}
