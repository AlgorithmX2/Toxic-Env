package toxicenv.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ToxicConfig
{

	public static class ToxicCategory
	{

		String category;

		public ToxicCategory(String name) {
			category = name;
		}

		public void setComment(String desc)
		{
			instance.config.getCategory( category ).setComment( desc );
		}

		public Property getString(String name, String desc, String value)
		{
			Property prop = instance.config.get( category, name, value );
			prop.comment = desc;

			instance.save();

			return prop;
		}

		public Property getBoolean(String name, String desc, boolean value)
		{
			Property prop = instance.config.get( category, name, value );
			prop.comment = desc;

			instance.save();

			return prop;
		}

		public Property getInt(String name, String desc, int value)
		{
			Property prop = instance.config.get( category, name, value );
			prop.comment = desc;

			instance.save();

			return prop;
		}

		public Property getDouble(String name, String desc, double value)
		{
			Property prop = instance.config.get( category, name, value );
			prop.comment = desc;

			instance.save();

			return prop;
		}

	};

	final public static ToxicConfig instance = new ToxicConfig();

	private Configuration config;
	private boolean doneLoading = false;

	public void load(File modConfigurationDirectory)
	{
		config = new Configuration( new File( modConfigurationDirectory, "ToxicEnv.cfg" ) );
	}

	public void doneLoading()
	{
		doneLoading = true;
		save();
	}

	public void save()
	{
		if ( doneLoading && config.hasChanged() )
			config.save();
	}

	public ToxicCategory getCategory(String name)
	{
		return new ToxicCategory( name );
	}
}
