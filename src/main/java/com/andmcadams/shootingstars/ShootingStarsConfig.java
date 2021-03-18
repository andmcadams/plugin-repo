package com.andmcadams.shootingstars;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(ShootingStarsPlugin.CONFIG_GROUP_KEY)
public interface ShootingStarsConfig extends Config
{
	String SHOOTING_STAR_POST_ENDPOINT_KEYNAME = "post endpoint";
	String SHOOTING_STAR_GET_ENDPOINT_KEYNAME = "get endpoint";

	@ConfigItem(keyName = SHOOTING_STAR_POST_ENDPOINT_KEYNAME, name = "POST endpoint", description = "Web endpoint to post star data to")
	default String shootingStarPostEndpointConfig()
	{
		return "";
	}

	@ConfigItem(keyName = SHOOTING_STAR_GET_ENDPOINT_KEYNAME, name = "GET endpoint", description = "Web endpoint to get star data from")
	default String shootingStarGetEndpointConfig()
	{
		return "";
	}
}