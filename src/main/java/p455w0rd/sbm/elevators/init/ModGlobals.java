package p455w0rd.sbm.elevators.init;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import p455w0rd.sbm.elevators.blocks.BlockElevator;
import p455w0rd.sbm.elevators.init.ModConfig.Options;
import p455w0rd.sbm.elevators.network.PacketTeleport;

public class ModGlobals {

	public static final String MODID = "elevators";
	public static final String NAME = "Elevators";
	public static final String VERSION = "1.0.0";
	public static final String GUI_FACTORY = "p455w0rd.sbm.elevators.init.ModGuiFactory";
	public static final String CONFIG_FILE = "config/" + NAME + "_SBM.cfg";

	public static class TeleportHelper {

		public static boolean isJumping(EntityPlayer player) {
			return ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, player, "field_70703_bu", "isJumping");
		}

		public static void tryToTeleport(EntityPlayer player, TeleportDirection direction) {
			World world = player.getEntityWorld();
			BlockPos fromPos = getPosUnderPlayer(player);
			IBlockState fromState = world.getBlockState(fromPos);
			BlockPos toPos = getNearestElevator(world, fromState, fromPos, direction);
			if (toPos != null) {
				ModNetworking.getInstance().sendToServer(new PacketTeleport(fromPos, toPos));
			}
		}

		public static BlockPos getNearestElevator(World world, IBlockState fromState, BlockPos elevatorPos, TeleportDirection direction) {
			BlockPos startPos = direction == TeleportDirection.UP ? elevatorPos.up(3) : elevatorPos.down(3);
			int spaceBetweenElevators = 2;
			for (int i = startPos.getY(); direction == TeleportDirection.UP ? i < 256 : i > 0; i += (direction == TeleportDirection.UP ? 1 : -1)) {
				BlockPos currentPos = new BlockPos(elevatorPos.getX(), i, elevatorPos.getZ());
				IBlockState currentState = world.getBlockState(currentPos);
				if (!isSameType(currentState, fromState) && Options.requireClearLineOfSight && !isBlockPassable(world, currentState, currentPos)) {
					break;
				}
				if (isElevator(currentState) && isSameType(currentState, fromState) && isBlockSafeToTeleportTo(world, currentPos) && spaceBetweenElevators >= Options.spaceBetweenElevators) {
					return currentPos;
				}
				spaceBetweenElevators++;
			}
			return null;
		}

		private static BlockPos getPosUnderPlayer(EntityPlayer player) {
			int x = MathHelper.floor(player.posX);
			int y = MathHelper.floor(player.getEntityBoundingBox().minY) - 1;
			int z = MathHelper.floor(player.posZ);
			return new BlockPos(x, y, z);
		}

		private static boolean isBlockSafeToTeleportTo(World world, BlockPos pos) {
			return world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2));
		}

		private static boolean isBlockPassable(World world, IBlockState blockState, BlockPos pos) {
			return world.isAirBlock(pos);
		}

		private static boolean isSameType(IBlockState fromState, IBlockState toState) {
			return Options.mustBeSameColor ? fromState == toState : fromState.getBlock() == toState.getBlock();
		}

		private static boolean isElevator(IBlockState blockState) {
			return blockState.getBlock() instanceof BlockElevator;
		}

		public static enum TeleportDirection {

				UP, DOWN;

		}

	}

}
