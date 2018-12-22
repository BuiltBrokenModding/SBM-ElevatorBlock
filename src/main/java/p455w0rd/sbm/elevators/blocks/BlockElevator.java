package p455w0rd.sbm.elevators.blocks;

import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import p455w0rd.sbm.elevators.init.ModConfig.Options;
import p455w0rd.sbm.elevators.init.ModCreativeTab;
import p455w0rd.sbm.elevators.init.ModGlobals;

public class BlockElevator extends BlockColored {

	private static final ResourceLocation REGISTRY_NAME = new ResourceLocation(ModGlobals.MODID, "elevator");

	public BlockElevator() {
		super(Material.CLOTH);
		setRegistryName(REGISTRY_NAME);
		setUnlocalizedName(REGISTRY_NAME.toString());
		setHardness(0.8F);
		setSoundType(SoundType.CLOTH);
		setCreativeTab(ModCreativeTab.TAB);
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return Options.allowMobSpawning && super.canCreatureSpawn(state, world, pos, type);
	}

}
