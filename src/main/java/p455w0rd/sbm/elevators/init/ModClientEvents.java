package p455w0rd.sbm.elevators.init;

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
import p455w0rd.sbm.elevators.init.ModGlobals.TeleportHelper;
import p455w0rd.sbm.elevators.init.ModGlobals.TeleportHelper.TeleportDirection;

/**
 * @author p455w0rd
 *
 */
@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModClientEvents {

	private static boolean wasCrouching;
	private static boolean wasJumping;

	@SubscribeEvent
	public static void onPlayerMove(InputUpdateEvent inputEvent) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (!player.isSpectator()) {
			if (wasCrouching != player.isSneaking()) {
				wasCrouching = player.isSneaking();
				if (wasCrouching) {
					TeleportHelper.tryToTeleport(player, TeleportDirection.DOWN);
				}
			}
			boolean jumping = TeleportHelper.isJumping(player);
			if (wasJumping != jumping) {
				wasJumping = jumping;
				if (wasJumping) {
					TeleportHelper.tryToTeleport(player, TeleportDirection.UP);
				}
			}
		}
	}

	@SubscribeEvent
	public static void registerModels(final ModelRegistryEvent event) {
		for (EnumDyeColor color : EnumDyeColor.values()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.ELEVATOR_BLOCK), color.ordinal(), new ModelResourceLocation(ModBlocks.ELEVATOR_BLOCK.getRegistryName(), "color=" + color.getName()));
		}
	}

}
