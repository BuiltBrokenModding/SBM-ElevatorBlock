package p455w0rd.sbm.elevators.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.sbm.elevators.init.ModSounds;

/**
 * @author p455w0rd
 *
 */
public class PacketTeleport implements IMessage {

	public BlockPos from, to;

	public PacketTeleport() {
	}

	public PacketTeleport(BlockPos from, BlockPos to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		from = readPos(buffer);
		to = readPos(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		writePos(from, buffer);
		writePos(to, buffer);
	}

	private BlockPos readPos(ByteBuf buffer) {
		return new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	private void writePos(BlockPos pos, ByteBuf buffer) {
		buffer.writeInt(pos.getX());
		buffer.writeInt(pos.getY());
		buffer.writeInt(pos.getZ());
	}

	public static class Handler implements IMessageHandler<PacketTeleport, IMessage> {

		@Override
		public IMessage onMessage(PacketTeleport message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				World world = player.getEntityWorld();
				BlockPos from = message.from, to = message.to;
				if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
					return;
				}
				player.setPositionAndUpdate(to.getX() + 0.5f, to.getY() + 1, to.getZ() + 0.5f);
				player.motionY = 0;
				world.playSound(null, to, ModSounds.TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
			});
			return null;
		}

	}

}
