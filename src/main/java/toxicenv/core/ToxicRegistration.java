package toxicenv.core;

import toxicenv.item.ToxicItems;
import toxicenv.transformer.OverriddenLogic;
import toxicenv.transformer.Transformer;
import toxicenv.transformer.OverriddenLogic.BreathLogic;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Register mod components and handle initialization.
 */
public class ToxicRegistration
{

	static public ToxicRegistration instance = new ToxicRegistration();

	private ToxicRegistration() {
		// just prevent construction.
	}

	public void pre(FMLPreInitializationEvent event)
	{
		ToxicConfig.instance.load( event.getModConfigurationDirectory() );

		ItemStack creativeTabItem = null;

		for (ToxicItems i : ToxicItems.values())
		{
			if ( i.register( ToxicConfig.instance.getCategory( i.name() ) ) && creativeTabItem == null )
			{
				creativeTabItem = i.getStack( 1 );
			}
		}

		if ( creativeTabItem != null )
		{
			CreativeTabs creativeTab = new ToxicTab( creativeTabItem );

			for (ToxicItems i : ToxicItems.values())
				i.getItem().setCreativeTab( creativeTab );
		}
	}

	public void init(FMLInitializationEvent event)
	{

	}

	public void post(FMLPostInitializationEvent event)
	{
		OverriddenLogic.defaultAirQualtiy = new BreathLogic( null );
		Transformer.instance.checkStatus();

		ToxicConfig.instance.doneLoading();
	}
}
