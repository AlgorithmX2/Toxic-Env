package toxicenv.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

/**
 * Simple Logging Class
 */
public class ToxicLog
{

	static private ToxicLog myLog = new ToxicLog();
	private Logger internalLog;

	private ToxicLog() {
		// no external construction
	}

	private Logger getLogger()
	{
		if ( internalLog == null )
			return FMLRelaunchLog.log.getLogger();

		return internalLog;
	}

	public static void setLogger(Logger modLog)
	{
		myLog.internalLog = modLog;
	}

	private static void log(Level level, String format, Object... data)
	{
		myLog.getLogger().log( level, String.format( format, data ) );
	}

	public static void error(Throwable t, String message)
	{
		myLog.getLogger().fatal( message, t );
	}

	public static void severe(String format, Object... data)
	{
		log( Level.ERROR, format, data );
	}

	public static void warning(String format, Object... data)
	{
		log( Level.WARN, format, data );
	}

	public static void info(String format, Object... data)
	{
		log( Level.INFO, format, data );
	}

}
