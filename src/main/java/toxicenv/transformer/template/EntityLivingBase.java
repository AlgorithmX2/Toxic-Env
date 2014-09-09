package toxicenv.transformer.template;

import toxicenv.transformer.OverriddenLogic;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public abstract class EntityLivingBase extends net.minecraft.entity.EntityLivingBase
{

	/**
	 * will be ignored
	 */
	public EntityLivingBase(World p_i1594_1_) {
		super( p_i1594_1_ );
	}

	@ToxicCoreCopy
	public void toxicenv_breathing(int three_hundred)
	{
		OverriddenLogic.breath( this, three_hundred );
	}

	@ToxicCoreCopy
	public boolean toxicenv_renderairbar(Material water)
	{
		return OverriddenLogic.renderAirBar( this, water );
	}

}
