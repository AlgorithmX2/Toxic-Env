package toxicenv.item;

import toxicenv.core.ToxicConfig.ToxicCategory;
import net.minecraft.item.Item;

public class ItemBase extends Item
{

	boolean isEnabled = false;

	public void configure(ToxicCategory config)
	{
		isEnabled = config.getBoolean( "enabled", "Is the item enabled, disabled items will not be added to the game world!", true ).getBoolean();
	}

}
