package toxicenv.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ToxicTab extends CreativeTabs
{

	final ItemStack whichItem;

	public ToxicTab(ItemStack whichItem) {
		super( "mod.toxicenv" );
		this.whichItem = whichItem;
	}

	@Override
	public Item getTabIconItem()
	{
		return whichItem.getItem();
	}

}
