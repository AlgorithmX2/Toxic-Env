package toxicenv.item;

import toxicenv.core.ToxicConfig.ToxicCategory;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public enum ToxicItems
{

	Breather(ItemBreather.class),

	AirTank(ItemAirTank.class);

	private Class<? extends ItemBase> itemClass;
	private ItemBase item;

	private ToxicItems(Class<? extends ItemBase> base) {
		itemClass = base;
	}

	public ItemStack getStack(int stackSize)
	{
		return new ItemStack( item, stackSize );
	}

	public ItemBase getItem()
	{
		return item;
	}

	public boolean register(ToxicCategory config)
	{
		try
		{
			item = itemClass.newInstance();
		}
		catch (Throwable t)
		{
			throw new RuntimeException( t );
		}

		item.configure( config );

		item.setUnlocalizedName( "toxicenv." + name() );
		item.setTextureName( "toxicenv:" + name() );

		if ( item.isEnabled )
			GameRegistry.registerItem( item, name() );

		return item.isEnabled;
	}

	public boolean isItem(ItemStack itemStack)
	{
		if ( itemStack == null )
			return false;

		return itemStack.getItem() == item;
	}

}
