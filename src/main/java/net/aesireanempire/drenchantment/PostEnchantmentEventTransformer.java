package net.aesireanempire.drenchantment;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class PostEnchantmentEventTransformer implements IClassTransformer
{
	private static final String[] classesBeingTransformed =
			{
					"net.minecraft.inventory.ContainerEnchantment",
					"mcjty.deepresonance.items.armor.ItemRadiationSuit"
			};
	
	@Override
	public byte[] transform( String name, String transformedName, byte[] classBeingTransformed )
	{
		boolean isObfuscated = !name.equals( transformedName );
		int index = Arrays.asList( classesBeingTransformed ).indexOf( transformedName );
		return index != -1 ? transform( index, classBeingTransformed, isObfuscated ) : classBeingTransformed;
	}
	
	private byte[] transform( int index, byte[] classBeingTransformed, boolean isObfuscated )
	{
		System.out.println( "Transforming: " + classesBeingTransformed[ index ] );
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader( classBeingTransformed );
			classReader.accept( classNode, 0 );
			
			switch (index)
			{
				case 0:
					transformContainerEnchantment( classNode, isObfuscated );
					break;
				case 1:
					transformCountRadiationPieces( classNode, isObfuscated );
					break;
			}
			
			ClassWriter classWriter = new ClassWriter( ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES );
			classNode.accept( classWriter );
			return classWriter.toByteArray();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return classBeingTransformed;
	}
	
	
	private void transformCountRadiationPieces( ClassNode classNode, boolean isObfuscated )
	{
		final String COUNT_ITEMS = "countSuitPieces";
		final String COUNT_ITEMS_DESC = "(Lnet/minecraft/entity/EntityLivingBase;)I";
		
		MethodNode method = findMethod( classNode, COUNT_ITEMS, COUNT_ITEMS_DESC );
		
		if ( method == null ) return;
		
		
		// Adds label for Else If Addition
		for (AbstractInsnNode node : method.instructions.toArray())
		{
			if ( node.getOpcode() == INVOKESTATIC )
			{
				MethodInsnNode methodInsnNode = (MethodInsnNode) node;
				if ( methodInsnNode.owner.equals( "mcjty/lib/tools/ItemStackTools" ) && methodInsnNode.name.equals( "isValid" ) )
				{
					AbstractInsnNode next = methodInsnNode.getNext();
					if ( next.getOpcode() == IFEQ )
					{
						Label label = new Label();
						method.instructions.insert( next, new LabelNode( label ) );
					}
				}
			}
		}
		
		//Adds check for TagComponent "AntiRadiationArmor"
		for (AbstractInsnNode node : method.instructions.toArray())
		{
			if ( node.getOpcode() == INSTANCEOF )
			{
				TypeInsnNode typeInsnNode = (TypeInsnNode) node;
				if ( typeInsnNode.desc.equals( "mcjty/deepresonance/api/IRadiationArmor" ) )
				{
					Label label = new Label();
					LabelNode labelNode = new LabelNode( label );
					
					InsnList list = new InsnList();
					list.add( new JumpInsnNode( IFNE, labelNode ) );
					list.add( new VarInsnNode( ALOAD, 6 ) );
					list.add( new MethodInsnNode( INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "hasTagCompound", "()Z", false ) );
					
					AbstractInsnNode nodeNext = node.getNext();
					method.instructions.insert( node, list );
					
					if ( nodeNext.getOpcode() == IFEQ )
					{
						JumpInsnNode jumpInsnNode = (JumpInsnNode) nodeNext;
						
						list = new InsnList();
						list.add( new VarInsnNode( ALOAD, 6 ) );
						list.add( new MethodInsnNode( INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "getTagCompound", "()Lnet/minecraft/nbt/NBTTagCompound;", false ) );
						list.add( new LdcInsnNode( "AntiRadiationArmor" ) );
						list.add( new MethodInsnNode( INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "hasKey", "(Ljava/lang/String;)Z", false ) );
						list.add( new JumpInsnNode( IFEQ, jumpInsnNode.label ) );
						list.add( labelNode );
						
						list.add( new FrameNode( F_APPEND, 2, new Object[]{"net/minecraft/inventory/EntityEquipmentSlot", "net/minecraft/item/ItemStack"}, 0, null ) );
						list.add( new IincInsnNode( 1, 1 ) );
						
						method.instructions.insert( nodeNext, list );
					}
				}
			}
		}
	}
	
	// Adds Event for after enchantment has happened
	// Allows adding custom tags to an item stack during enchantment.
	private void transformContainerEnchantment( ClassNode classNode, boolean isObfuscated )
	{
		final String ENCHANT_ITEM = "enchantItem";
		final String ENCHANT_ITEM_DESC = "(Lnet/minecraft/entity/player/EntityPlayer;I)Z";
		
		
		MethodNode method = findMethod( classNode, ENCHANT_ITEM, ENCHANT_ITEM_DESC );
		
		if ( method == null ) return;
		
		for (AbstractInsnNode node : method.instructions.toArray())
		{
			if ( node.getOpcode() == INVOKEVIRTUAL )
			{
				if ( ( (MethodInsnNode) node ).name.equals( "addEnchantment" ) )
				{
					InsnList list = new InsnList();
					list.add( new VarInsnNode( ALOAD, 3 ) ); // ItemStack
					list.add( new VarInsnNode( ALOAD, 1 ) ); // Player
					list.add( new VarInsnNode( ALOAD, 9 ) ); // EnchantmentData
					list.add( new MethodInsnNode( INVOKESTATIC, Type.getInternalName( ModContainer.class ), "onPostEnchant", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/enchantment/EnchantmentData;)V", false ) );
					method.instructions.insert( node, list );
				}
			}
		}
	}
	
	private MethodNode findMethod( ClassNode classNode, String methodName, String methodDesc )
	{
		for (MethodNode method : classNode.methods)
		{
			if ( method.name.equals( methodName ) && method.desc.equals( methodDesc ) )
			{
				return method;
			}
		}
		return null;
	}
	
}
