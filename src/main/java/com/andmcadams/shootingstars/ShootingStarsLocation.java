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
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShootingStarsLocation
{
	ASGARNIA(0, "Asgarnia", "Asgarnia", ShootingStarsConfig::shootingStarShowAsgarniaWorlds),
	KARAMJA(1, "Crandor or Karamja", "Cran/Karam", ShootingStarsConfig::shootingStarShowKaramjaWorlds),
	FELDIP_HILLS(2, "Feldip Hills or on the Isle of Souls", "Feldip/Souls", ShootingStarsConfig::shootingStarShowFeldipWorlds),
	FOSSIL_ISLAND(3, "Fossil Island or on Mos Le'Harmless", "Fossil/Mos", ShootingStarsConfig::shootingStarShowFossilIslandWorlds),
	FREMENNIK(4, "Fremennik Lands or on Lunar Isle", "Frem/Lunar", ShootingStarsConfig::shootingStarShowFremennikWorlds),
	KOUREND(5, "Great Kourend", "Kourend", ShootingStarsConfig::shootingStarShowKourendWorlds),
	KANDARIN(6, "Kandarin", "Kandarin", ShootingStarsConfig::shootingStarShowKandarinWorlds),
	KEBOS(7, "Kebos Lowlands", "Lowlands", ShootingStarsConfig::shootingStarShowKebosWorlds),
	KHARIDIAN_DESERT(8, "Kharidian Desert", "Desert", ShootingStarsConfig::shootingStarShowDesertWorlds),
	MISTHALIN(9, "Misthalin", "Misthalin", ShootingStarsConfig::shootingStarShowMisthalinWorlds),
	MORYTANIA(10, "Morytania", "Morytania", ShootingStarsConfig::shootingStarShowMorytaniaWorlds),
	PISCATORIS(11, "Piscatoris or the Gnome Stronghold", "Pisc/Gnome", ShootingStarsConfig::shootingStarShowPiscatorisWorlds),
	TIRANNWN(12, "Tirannwn", "Tirannwn", ShootingStarsConfig::shootingStarShowTirannwnWorlds),
	WILDERNESS(13, "Wilderness", "Wilderness", ShootingStarsConfig::shootingStarShowWildernessWorlds),
	UNKNOWN(14, "Unknown", "Unknown", c -> false);

	private int id;
	private String name;
	private String shortName;
	private Function<ShootingStarsConfig, Boolean> configFunction;

	public static ShootingStarsLocation determineLocation(String text)
	{
		text = text.replace("<br>", " ");
		for (ShootingStarsLocation l : values())
		{
			if (text.contains(l.name))
			{
				return l;
			}
		}
		return ShootingStarsLocation.UNKNOWN;
	}

	public static ShootingStarsLocation getLocation(int id)
	{
		for (ShootingStarsLocation l : values())
		{
			if (l.getId() == id)
			{
				return l;
			}
		}

		return ShootingStarsLocation.UNKNOWN;
	}
}
