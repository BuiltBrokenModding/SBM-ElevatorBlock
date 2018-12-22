package p455w0rd.sbm.elevators.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSounds {

	private static final ResourceLocation REGISTRY_NAME = new ResourceLocation(ModGlobals.MODID, "teleport");
	public static final SoundEvent TELEPORT = new SoundEvent(REGISTRY_NAME).setRegistryName(REGISTRY_NAME);

}
