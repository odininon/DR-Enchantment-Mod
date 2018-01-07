package net.aesireanempire.drenchantment;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PostEnchantmentEvent extends Event
{
	private final ItemStack itemStack;
	private final EnchantmentData enchantmentData;
	private final EntityPlayer player;
	
	PostEnchantmentEvent( ItemStack itemStack, EnchantmentData enchantmentData, EntityPlayer player )
	{
		
		this.itemStack = itemStack;
		this.enchantmentData = enchantmentData;
		this.player = player;
	}
	
	public ItemStack getItemStack()
	{
		return itemStack;
	}
	
	public EnchantmentData getEnchantment()
	{
		return enchantmentData;
	}
	
	public EntityPlayer getPlayer()
	{
		return player;
	}
	
}
