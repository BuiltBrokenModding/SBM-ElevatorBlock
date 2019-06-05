package com.builtbroken.elevators.logic;

import com.builtbroken.elevators.Elevators;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet used to tell the server that a player is ready to try to teleport
 *
 * @author p455w0rd
 */
public class PacketTryMovement implements IMessage
{
    private MoveDirection direction;

    public PacketTryMovement()
    {
    }

    public PacketTryMovement(MoveDirection direction)
    {
        this.direction = direction;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        direction = MoveDirection.get(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(direction.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketTryMovement, IMessage>
    {
        @Override
        public IMessage onMessage(PacketTryMovement message, MessageContext context)
        {
            //TODO add check to prevent macro spam of server (player -> last try time, with config to change delay)
            FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(
                    () -> TeleportHelper.tryToTeleport(context.getServerHandler().player, message.direction, Elevators.ELEVATOR_BLOCK)
            );
            return null;
        }
    }
}
