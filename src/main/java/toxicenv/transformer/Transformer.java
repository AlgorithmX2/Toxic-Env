package toxicenv.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.client.GuiIngameForge;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import toxicenv.core.ToxicLog;
import toxicenv.transformer.template.ToxicCoreCopy;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class Transformer implements IClassTransformer
{

	public static EnumSet<TransformerTask> completed = EnumSet.noneOf( TransformerTask.class );
	public static EnumSet<TransformerTask> failed = EnumSet.allOf( TransformerTask.class );

	public static Transformer instance;

	public Transformer() {
		instance = this;
	}

	private void log(String str)
	{
		FMLRelaunchLog.log( "ToxicEnv", Level.INFO, str );
	}

	private void completeTask(TransformerTask which)
	{
		failed.remove( which );
		completed.add( which );
	}

	@Override
	public byte[] transform(String className, String transformedName, byte[] bytes)
	{
		try
		{
			if ( "net.minecraft.entity.EntityLivingBase".equals( transformedName ) )
				bytes = transformLivingBase( bytes );

			if ( "net.minecraftforge.client.GuiIngameForge".equals( transformedName ) )
				bytes = transformIngameGui( bytes );

			return bytes;
		}
		catch (Throwable t)
		{
			throw new RuntimeException( t );
		}
	}

	private byte[] transformIngameGui(byte[] bytes)
	{
		log( "Found GuiIngameForge" );

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader( bytes );
		classReader.accept( classNode, 0 );

		MethodNode renderAir = findMethod( classNode, MethodDesc.renderAir );
		if ( renderAir != null )
		{
			Iterator<AbstractInsnNode> i = renderAir.instructions.iterator();
			while (i.hasNext())
			{
				AbstractInsnNode node = i.next();
				if ( node instanceof MethodInsnNode )
				{
					MethodInsnNode in = (MethodInsnNode) node;
					if ( MethodDesc.isInsideOfMaterial.isMethod( in ) )
					{
						in.name = "toxicenv_renderairbar";
						completeTask( TransformerTask.RenderAirBar );
					}
				}
			}

			ClassWriter writer = new ClassWriter( ClassWriter.COMPUTE_MAXS );
			classNode.accept( writer );
			return writer.toByteArray();
		}

		return bytes;
	}

	private byte[] transformLivingBase(byte[] bytes) throws IOException
	{
		log( "Found EntityLivingBase" );

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader( bytes );
		classReader.accept( classNode, 0 );

		applyTemplate( classNode, "/toxicenv/transformer/template/EntityLivingBase.class" );

		MethodNode onEntityUpdate = findMethod( classNode, MethodDesc.onEntityUpdate );
		if ( onEntityUpdate != null )
		{
			Iterator<AbstractInsnNode> i = onEntityUpdate.instructions.iterator();
			while (i.hasNext())
			{
				AbstractInsnNode node = i.next();
				if ( node instanceof MethodInsnNode )
				{
					MethodInsnNode in = (MethodInsnNode) node;
					if ( MethodDesc.setAir.isMethod( in ) && in.getPrevious() instanceof IntInsnNode )
					{
						IntInsnNode intNode = (IntInsnNode) in.getPrevious();
						if ( intNode.operand == 300 )
						{
							in.name = "toxicenv_breathing";
							completeTask( TransformerTask.PlayerAirBreathing );
						}
					}
				}
			}

			ClassWriter writer = new ClassWriter( ClassWriter.COMPUTE_MAXS );
			classNode.accept( writer );
			return writer.toByteArray();
		}

		return bytes;
	}

	private void applyTemplate(ClassNode classNode, String string) throws IOException
	{
		ClassNode srcNode = new ClassNode();
		InputStream is = getClass().getResourceAsStream( string );
		ClassReader srcReader = new ClassReader( is );
		srcReader.accept( srcNode, 0 );

		for (MethodNode mn : srcNode.methods)
		{
			if ( hasAnnotation( mn.visibleAnnotations, ToxicCoreCopy.class ) )
			{
				log( "Found " + mn.name );
				handleMethod( classNode, srcNode.name, mn );
			}
		}
	}

	private void handleMethod(ClassNode classNode, String from, MethodNode mn)
	{
		Iterator<AbstractInsnNode> i = mn.instructions.iterator();
		while (i.hasNext())
		{
			processNode( i.next(), from, classNode.name );
		}

		for (MethodNode tmn : classNode.methods)
		{
			if ( tmn.name.equals( mn.name ) && tmn.desc.equals( mn.desc ) )
			{
				ToxicLog.info( "Found " + tmn.name + " : Appending" );

				AbstractInsnNode finalReturn = mn.instructions.getLast();
				while (!isReturn( finalReturn.getOpcode() ))
				{
					mn.instructions.remove( finalReturn );
					finalReturn = mn.instructions.getLast();
				}
				mn.instructions.remove( finalReturn );

				tmn.instructions.insert( mn.instructions );
				return;
			}
		}

		log( "No Such Method " + mn.name + " found on " + classNode.name + " : Adding" );
		classNode.methods.add( mn );
	}

	private boolean hasAnnotation(List<AnnotationNode> anns, Class<?> anno)
	{
		if ( anns == null )
			return false;

		for (AnnotationNode ann : anns)
		{
			if ( ann.desc.equals( Type.getDescriptor( anno ) ) )
			{
				return true;
			}
		}

		return false;
	}

	private boolean isReturn(int opcode)
	{
		switch (opcode)
		{
		case Opcodes.ARETURN:
		case Opcodes.DRETURN:
		case Opcodes.FRETURN:
		case Opcodes.LRETURN:
		case Opcodes.IRETURN:
		case Opcodes.RETURN:
			return true;
		}
		return false;
	}

	/**
	 * borrowed from RotatableBlocks Blocks
	 * 
	 * @param next
	 * @param from
	 * @param nePar
	 */
	private void processNode(AbstractInsnNode next, String from, String nePar)
	{
		if ( next instanceof FieldInsnNode )
		{
			FieldInsnNode min = (FieldInsnNode) next;
			if ( min.owner.equals( from ) )
			{
				min.owner = nePar;
			}
		}
		if ( next instanceof MethodInsnNode )
		{
			MethodInsnNode min = (MethodInsnNode) next;
			if ( min.owner.equals( from ) )
			{
				min.owner = nePar;
			}
		}
	}

	private MethodNode findMethod(ClassNode classNode, MethodDesc md)
	{
		for (MethodNode mn : classNode.methods)
		{
			if ( md.isMethod( mn ) )
				return mn;
		}

		return null;
	}

	public void checkStatus()
	{

		try
		{
			// no errors
			GuiIngameForge.class.getClass();
		}
		catch (Throwable t)
		{
			completeTask( TransformerTask.RenderAirBar );
		}

		if ( !Transformer.completed.isEmpty() )
			ToxicLog.info( "Transformer Success: " + Transformer.completed );

		if ( !Transformer.failed.isEmpty() )
			ToxicLog.info( "Transformer Failed: " + Transformer.failed );

		if ( !Transformer.failed.isEmpty() )
			throw new RuntimeException( "Tranformer Failed to Complete: " + Transformer.failed );
	}
}
