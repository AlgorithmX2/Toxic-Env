package toxicenv.transformer;

import java.util.WeakHashMap;

import toxicenv.core.ToxicConfig;
import toxicenv.core.ToxicConfig.ToxicCategory;
import toxicenv.item.ToxicItems;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class OverriddenLogic
{

	public static class BreathLogic
	{

		boolean doesSunLightBurn = false;

		boolean doesRainHurt = false;
		boolean isPrecipitationPoisoned = false;

		boolean isAirBreathable = true;
		boolean isAirPoisoned = false;

		int airRestoreQuantity = 0;
		int poisonIntensity = 0;

		public BreathLogic(World w) {
			ToxicCategory cat = ToxicConfig.instance.getCategory( w == null ? "default-world" : w.provider.dimensionId + "-world" );

			BreathLogic myDefault = this;
			if ( w != null )
				myDefault = defaultAirQualtiy;

			doesSunLightBurn = cat.getBoolean( "doesSunLightBurn", "When standing in sunlight do you burn?", myDefault.doesSunLightBurn ).getBoolean();

			doesRainHurt = cat.getBoolean( "doesRainHurt", "When standing in the rain does it hurt you?", myDefault.doesRainHurt ).getBoolean();

			isPrecipitationPoisoned = cat.getBoolean( "isPrecipitationPoisoned", "Does standing in the rain or snow poison you?",
					myDefault.isPrecipitationPoisoned ).getBoolean();

			airRestoreQuantity = cat.getInt( "airRestoreQuantity",
					"Amount of air to restore when sufficating, defaults to 0, range 0 - 300; higher numbers will let you limp on as you sufficate.",
					myDefault.airRestoreQuantity ).getInt();

			isAirBreathable = cat.getBoolean( "isAirBreathable", "Can the air be breathed? - when your out of air you will drown outside of water.",
					myDefault.isAirBreathable ).getBoolean();

			isAirPoisoned = cat.getBoolean( "isAirPoisoned",
					"if true when breated air will poison you, you can use this with breathable air to require a breather.", myDefault.isAirPoisoned )
					.getBoolean();

			poisonIntensity = cat.getInt( "poisonIntensity",
					"Only valid when isPrecipitationPoisoned or isAirPoisoned is true; The Level of poison to apply, 0 is poison, 1 is poison 2, etc...",
					myDefault.poisonIntensity ).getInt();
		}

	};

	public static WeakHashMap<World, BreathLogic> airQualtiy = new WeakHashMap<World, BreathLogic>();
	public static BreathLogic defaultAirQualtiy;

	public static boolean renderAirBar(EntityLivingBase entity, Material water)
	{
		assert (water == Material.water);

		BreathLogic localQualtiy = getAirQuality( entity.worldObj );

		if ( !localQualtiy.isAirBreathable )
			return true;

		return Minecraft.getMinecraft().thePlayer.isInsideOfMaterial( water );
	}

	private static BreathLogic getAirQuality(World worldObj)
	{
		BreathLogic bl = airQualtiy.get( worldObj );

		if ( bl == null )
		{
			airQualtiy.put( worldObj, bl = new BreathLogic( worldObj ) );
		}

		return bl;
	}

	public static void breath(EntityLivingBase entity, int three_hundred)
	{
		assert (three_hundred == 300);

		if ( entity instanceof EntityPlayer )
		{
			BreathLogic localQualtiy = getAirQuality( entity.worldObj );

			EntityPlayer myPlayer = (EntityPlayer) entity;

			if ( myPlayer.capabilities.isCreativeMode )
			{
				entity.setAir( three_hundred );
				return;
			}

			World w = entity.worldObj;

			int eX = (int) Math.round( entity.posX );
			int eY = (int) Math.round( entity.posY );
			int eZ = (int) Math.round( entity.posZ );

			boolean openToTheSky = w.canBlockSeeTheSky( eX, eY, eZ );

			if ( openToTheSky && localQualtiy.doesSunLightBurn )
			{
				float brightness = w.getSunBrightness( 1.0F );
				if ( brightness > 0.3 )
				{
					entity.setFire( 2 );
				}
			}

			if ( w.isRaining() && (localQualtiy.doesRainHurt || localQualtiy.isPrecipitationPoisoned) )
			{
				if ( openToTheSky )
				{
					BiomeGenBase biome = w.getBiomeGenForCoords( eX, eZ );
					if ( biome != null )
					{
						if ( biome.canSpawnLightningBolt() || biome.getEnableSnow() )
						{
							if ( localQualtiy.isPrecipitationPoisoned )
								poisonEntity( entity, localQualtiy.poisonIntensity );
						}

						if ( biome.canSpawnLightningBolt() && localQualtiy.doesRainHurt )
						{
							if ( !w.canSnowAtBody( eX, eY, eZ, false ) )
								entity.attackEntityFrom( DamageSource.inFire, 0.5f );
						}
					}
				}
			}

			if ( localQualtiy.isAirBreathable )
			{
				if ( localQualtiy.isAirPoisoned && !hasBreather( myPlayer ) )
					poisonEntity( entity, localQualtiy.poisonIntensity );

				entity.setAir( three_hundred );
			}
			else
			{
				int newAir = entity.getAir() - 1;
				entity.setAir( newAir );

				if ( newAir <= -20 )
				{
					entity.setAir( 0 );
					entity.attackEntityFrom( DamageSource.drown, 2.0F );

					if ( localQualtiy.isAirPoisoned && !hasBreather( myPlayer ) )
						poisonEntity( entity, localQualtiy.poisonIntensity );
				}
			}
		}
		else
			entity.setAir( three_hundred );
	}

	private static void poisonEntity(EntityLivingBase entity, int severity)
	{
		if ( !entity.isPotionActive( Potion.poison.id ) )
			entity.addPotionEffect( new PotionEffect( Potion.poison.id, 300, severity ) );
	}

	private static boolean hasBreather(EntityPlayer entity)
	{
		return ToxicItems.Breather.isItem( entity.inventory.armorInventory[3] );
	}

}
