package toxicenv.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class ItemBreather extends ItemBaseDamageable
{

	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
	{
		return armorType == 0;
	}

}
