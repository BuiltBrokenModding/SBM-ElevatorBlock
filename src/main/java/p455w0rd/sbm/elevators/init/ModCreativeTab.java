package p455w0rd.sbm.elevators.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs {

	public static final CreativeTabs TAB = new ModCreativeTab();

	public ModCreativeTab() {
		super(ModGlobals.MODID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		return new ItemStack(ModBlocks.ELEVATOR_BLOCK, 1, 0);
	}

	@Override
	public boolean hasSearchBar() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return ModGlobals.NAME;
	}

}
