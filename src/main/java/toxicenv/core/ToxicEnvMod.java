package toxicenv.core;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ToxicEnv.MODID, version = ToxicEnv.VERSION, name = ToxicEnv.NAME, dependencies = "required-after:" + ToxicEnv.COREMODID)
public class ToxicEnvMod
{

	@EventHandler
	public void pre(FMLPreInitializationEvent event)
	{
		Stopwatch sw = Stopwatch.createStarted();
		ToxicLog.setLogger( event.getModLog() );
		ToxicLog.info( "Pre-Init" );

		ToxicRegistration.instance.pre( event );

		ToxicLog.info( "Pre-Init Done ( %s ms )", sw.elapsed( TimeUnit.MILLISECONDS ) );
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		Stopwatch sw = Stopwatch.createStarted();
		ToxicLog.info( "Init" );

		ToxicRegistration.instance.init( event );

		ToxicLog.info( "Init Done ( %s ms )", sw.elapsed( TimeUnit.MILLISECONDS ) );
	}

	@EventHandler
	public void post(FMLPostInitializationEvent event)
	{
		Stopwatch sw = Stopwatch.createStarted();
		ToxicLog.info( "Post-Init" );

		ToxicRegistration.instance.post( event );

		ToxicLog.info( "Post-Init Done ( %s ms )", sw.elapsed( TimeUnit.MILLISECONDS ) );
	}
}
