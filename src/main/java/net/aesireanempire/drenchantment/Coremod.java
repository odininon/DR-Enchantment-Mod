package net.aesireanempire.drenchantment;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.11.2")
@IFMLLoadingPlugin.TransformerExclusions("net.aesireanempire.")
public class Coremod implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{"net.aesireanempire.drenchantment.PostEnchantmentEventTransformer"};
	}
	
	@Override
	public String getModContainerClass()
	{
		System.out.println( "Testings" );
		return "net.aesireanempire.drenchantment.ModContainer";
	}
	
	@Nullable
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData( Map<String, Object> map )
	{
	
	}
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
