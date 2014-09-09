package toxicenv.item;

import toxicenv.core.ToxicConfig.ToxicCategory;

public class ItemBaseDamageable extends ItemBase
{

	private int maxDurability = 120;

	@Override
	public void configure(ToxicCategory config)
	{
		super.configure( config );
		maxDurability = config.getInt( "maxDurability", "Durability of the item, increasing this will let the item last longer.", maxDurability ).getInt();
		setMaxDamage( maxDurability );
	}

}
