package net.aesireanempire.drenchantment;

import com.google.common.eventbus.EventBus;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Collections;

public class ModContainer extends DummyModContainer
{
	public ModContainer()
	{
		super( new ModMetadata() );
		
		ModMetadata md = getMetadata();
		md.description = "Coremod to add custom nbttags for radiation suits";
		md.name = "DR Enchnantment CoreMod";
		md.modId = "dr_enchnantment-core-mod";
		md.version = "0.0.1";
		md.credits = "Freyja";
		md.authorList = Collections.singletonList( "Freyja" );
		md.url = "";
	}
	
	@Override
	public boolean registerBus( EventBus bus, LoadController controller )
	{
		bus.register( this );
		return true;
	}
	
	public static void onPostEnchant( ItemStack itemStack, EntityPlayer player, EnchantmentData enchantmentData )
	{
		PostEnchantmentEvent event = new PostEnchantmentEvent( itemStack, enchantmentData, player );
		
		System.out.println( "Sending Enchantment Event" );
		MinecraftForge.EVENT_BUS.post( event );
	}
}
