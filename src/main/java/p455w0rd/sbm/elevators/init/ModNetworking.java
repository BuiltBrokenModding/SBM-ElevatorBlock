package p455w0rd.sbm.elevators.init;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.sbm.elevators.network.PacketTeleport;

public class ModNetworking {

	private static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ModGlobals.MODID);

	public static SimpleNetworkWrapper getInstance() {
		return INSTANCE;
	}

	public static void init() {
		getInstance().registerMessage(PacketTeleport.Handler.class, PacketTeleport.class, 0, Side.SERVER);
	}
}
