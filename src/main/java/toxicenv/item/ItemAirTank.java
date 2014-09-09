package toxicenv.item;

import toxicenv.core.ToxicConfig.ToxicCategory;

public class ItemAirTank extends ItemBase
{

	private int maxAirSupply = 20 * 60 * 24;

	@Override
	public void configure(ToxicCategory config)
	{
		super.configure( config );
		maxAirSupply = config.getInt( "maxAirSupply", "How many ticks can the air supply in the tank last, default is 24 minutes.", maxAirSupply ).getInt();
	}

}
