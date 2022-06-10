package com.andmcadams.giantsfoundry;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GiantsFoundryPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GiantsFoundryPlugin.class);
		RuneLite.main(args);
	}
}