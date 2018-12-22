package p455w0rd.sbm.elevators;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import p455w0rd.sbm.elevators.init.*;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, acceptedMinecraftVersions = "[1.12.2]", guiFactory = ModGlobals.GUI_FACTORY)
public class Elevators {

	@Instance
	public static Elevators instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModConfig.getInstance().init();
		ModNetworking.init();
	}

}
