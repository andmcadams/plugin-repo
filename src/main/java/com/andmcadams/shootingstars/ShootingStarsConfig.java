/*
 * Copyright (c) 2021, Andrew McAdams
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.andmcadams.shootingstars;

import com.andmcadams.shootingstars.ui.ShootingStarsPanelType;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(ShootingStarsPlugin.CONFIG_GROUP_KEY)
public interface ShootingStarsConfig extends Config
{
	String SHOOTING_STAR_POST_ENDPOINT_KEYNAME = "post endpoint";
	String SHOOTING_STAR_GET_ENDPOINT_KEYNAME = "get endpoint";
	String SHOOTING_STAR_SHARED_KEY_KEYNAME = "password";
	String SHOOTING_STAR_EXPIRATION_LENGTH = "expiration";
	String SHOOTING_STAR_PANEL_CLASS = "panelclass";
	String SHOOTING_STAR_SHOW_PVP_WORLDS_KEYNAME = "pvp worlds";
	String SHOOTING_STAR_SHOW_ASGARNIA_KEYNAME = "asgarnia";
	String SHOOTING_STAR_SHOW_KARAMJA_KEYNAME = "karamja";
	String SHOOTING_STAR_SHOW_FELDIP_HILLS_KEYNAME = "feldip hills";
	String SHOOTING_STAR_SHOW_FOSSIL_ISLAND_KEYNAME = "fossil island";
	String SHOOTING_STAR_SHOW_FREMENNIK_KEYNAME = "fremennik";
	String SHOOTING_STAR_SHOW_KOUREND_KEYNAME = "kourend";
	String SHOOTING_STAR_SHOW_KANDARIN_KEYNAME = "kandarin";
	String SHOOTING_STAR_SHOW_KEBOS_KEYNAME = "kebos";
	String SHOOTING_STAR_SHOW_KHARIDIAN_DESERT_KEYNAME = "desert";
	String SHOOTING_STAR_SHOW_MISTHALIN_KEYNAME = "misthalin";
	String SHOOTING_STAR_SHOW_MORYTANIA_KEYNAME = "morytania";
	String SHOOTING_STAR_SHOW_PISCATORIS_KEYNAME = "piscatoris";
	String SHOOTING_STAR_SHOW_TIRANNWN_KEYNAME = "tirannwn";
	String SHOOTING_STAR_SHOW_WILDERNESS_KEYNAME = "wilderness";
	String SHOOTING_STAR_SHOW_SKILL_TOTAL_KEYNAME = "skill total";

	@ConfigItem(keyName = SHOOTING_STAR_POST_ENDPOINT_KEYNAME, position = 0, name = "POST endpoint", description = "Web endpoint to post star data to")
	default String shootingStarPostEndpointConfig()
	{
		return "https://z9smj03u77.execute-api.us-east-1.amazonaws.com/stars";
	}

	@ConfigItem(keyName = SHOOTING_STAR_GET_ENDPOINT_KEYNAME, position = 1, name = "GET endpoint", description = "Web endpoint to get star data from")
	default String shootingStarGetEndpointConfig()
	{
		return "https://z9smj03u77.execute-api.us-east-1.amazonaws.com/stars";
	}

	@ConfigItem(keyName = SHOOTING_STAR_SHARED_KEY_KEYNAME, position = 2, name = "Key", description = "A keyword to use to share stars with friends. Must be 1-10 alpha characters")
	default String shootingStarSharedKeyConfig()
	{
		return "global";
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_EXPIRATION_LENGTH,
		position = 2,
		name = "Show landed stars for",
		description = "How many minutes landed stars should remain listed in the side panel<br>" +
			"Different APIs may return less than the maximum of 120 minutes"
	)
	@Range(
		max = 120
	)
	@Units(
		value = Units.MINUTES
	)
	default int shootingStarExpirationLength()
	{
		return 5;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_PANEL_CLASS,
		position = 3,
		name = "Stars Panel",
		description = "Choose the shooting stars panel implementation"
	)
	default ShootingStarsPanelType shootingStarsPanelType()
	{
		return ShootingStarsPanelType.CONDENSED;
	}

	@ConfigItem(
		keyName = "worldHopperEnabled",
		position = 4,
		name = "Double click to Hop",
		description = "Enables double clicking worlds in the side view panels to quick-hop to them"
	)
	default boolean isWorldHopperEnabled()
	{
		return true;
	}

	@ConfigSection(
		name = "World Toggles",
		description = "Settings to show and hide certain types of worlds from the list of scouted worlds",
		position = 5
	)
	String toggleSection = "World Toggles";

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_PVP_WORLDS_KEYNAME,
		position = 3,
		name = "PVP worlds",
		description = "Show scouted PVP worlds",
		section = toggleSection
	)
	default boolean shootingStarShowPvpWorlds()
	{
		return false;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_WILDERNESS_KEYNAME,
		position = 4,
		name = "Wilderness",
		description = "Show scouted Wilderness worlds",
		section = toggleSection
	)
	default boolean shootingStarShowWildernessWorlds()
	{
		return false;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_SKILL_TOTAL_KEYNAME,
		position = 5,
		name = "Skill Total worlds",
		description = "Show scouted Skill Total worlds",
		section = toggleSection
	)
	default boolean shootingStarShowSkillTotalWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_ASGARNIA_KEYNAME,
		position = 6,
		name = "Asgarnia",
		description = "Show scouted Asgarnia worlds",
		section = toggleSection
	)
	default boolean shootingStarShowAsgarniaWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_KARAMJA_KEYNAME,
		position = 7,
		name = "Crandor and Karamja",
		description = "Show scouted Crandor and Karamja worlds",
		section = toggleSection
	)
	default boolean shootingStarShowKaramjaWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_FELDIP_HILLS_KEYNAME,
		position = 8,
		name = "Feldip Hills and Isle of Souls",
		description = "Show scouted Feldip Hills and Isle of Souls worlds",
		section = toggleSection
	)
	default boolean shootingStarShowFeldipWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_FOSSIL_ISLAND_KEYNAME,
		position = 9,
		name = "Fossil Island and Mos Le'Harmless",
		description = "Show scouted Fossil Island and Mos Le'Harmless worlds",
		section = toggleSection
	)
	default boolean shootingStarShowFossilIslandWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_FREMENNIK_KEYNAME,
		position = 10,
		name = "Fremennik Lands and Lunar Isle",
		description = "Show scouted Fremennik Lands and Lunar Isle worlds",
		section = toggleSection
	)
	default boolean shootingStarShowFremennikWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_KOUREND_KEYNAME,
		position = 11,
		name = "Great Kourend",
		description = "Show scouted Great Kourend worlds",
		section = toggleSection
	)
	default boolean shootingStarShowKourendWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_KANDARIN_KEYNAME,
		position = 12,
		name = "Kandarin",
		description = "Show scouted Kandarin worlds",
		section = toggleSection
	)
	default boolean shootingStarShowKandarinWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_KEBOS_KEYNAME,
		position = 13,
		name = "Kebos Lowlands",
		description = "Show scouted Kebos Lowlands worlds",
		section = toggleSection
	)
	default boolean shootingStarShowKebosWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_KHARIDIAN_DESERT_KEYNAME,
		position = 14,
		name = "Kharidian Desert",
		description = "Show scouted Kharidian Desert worlds",
		section = toggleSection
	)
	default boolean shootingStarShowDesertWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_MISTHALIN_KEYNAME,
		position = 15,
		name = "Misthalin",
		description = "Show scouted Misthalin worlds",
		section = toggleSection
	)
	default boolean shootingStarShowMisthalinWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_MORYTANIA_KEYNAME,
		position = 16,
		name = "Morytania",
		description = "Show scouted Morytania worlds",
		section = toggleSection
	)
	default boolean shootingStarShowMorytaniaWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_PISCATORIS_KEYNAME,
		position = 17,
		name = "Piscatoris and the Gnome Stronghold",
		description = "Show scouted Piscatoris and Gnome Stronghold worlds",
		section = toggleSection
	)
	default boolean shootingStarShowPiscatorisWorlds()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOOTING_STAR_SHOW_TIRANNWN_KEYNAME,
		position = 18,
		name = "Tirannwn",
		description = "Show scouted Tirannwn worlds",
		section = toggleSection
	)
	default boolean shootingStarShowTirannwnWorlds()
	{
		return true;
	}
}