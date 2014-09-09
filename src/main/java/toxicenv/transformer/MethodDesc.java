package toxicenv.transformer;

import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * A nicer way to handle Dev vs Runtime naming schemes
 */
enum MethodDesc
{
	onEntityUpdate("C", "func_70030_z", "onEntityUpdate", "()V", "()V"),

	setAir("h", "func_70050_g", "setAir", "(I)V", "(I)V"),

	isInsideOfMaterial("a", "func_70055_a", "isInsideOfMaterial", "(Lawt;)Z", "(Lnet/minecraft/block/material/Material;)Z"),

	renderAir("renderAir", "renderAir", "renderAir", "(II)V", "(II)V");

	String notch, srg, dev;
	String desc0, desc1;

	private MethodDesc(String notch, String srg, String dev, String desc0, String desc1) {
		this.notch = notch;
		this.srg = srg;
		this.dev = dev;
		this.desc0 = desc0;
		this.desc1 = desc1;
	}

	boolean isMethod(MethodInsnNode mn)
	{
		return (mn.name.equals( notch ) || mn.name.equals( srg ) || mn.name.equals( dev )) && (mn.desc.equals( desc0 ) || mn.desc.equals( desc1 ));
	}

	boolean isMethod(MethodNode mn)
	{
		return (mn.name.equals( notch ) || mn.name.equals( srg ) || mn.name.equals( dev )) && (mn.desc.equals( desc0 ) || mn.desc.equals( desc1 ));
	}
}