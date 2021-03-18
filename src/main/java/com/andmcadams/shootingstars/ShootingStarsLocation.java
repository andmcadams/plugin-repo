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

import lombok.Getter;

public enum ShootingStarsLocation
{
	ASGARNIA(0, "Asgarnia"),
	KARAMJA(1, "Karamja"),
	FELDIP_HILLS(2, "Feldip Hills"),
	FOSSIL_ISLAND(3, "Fossil Island"),
	FREMENNIK(4, "Fremennik"),
	KOUREND(5, "Kourend"),
	KANDARIN(6, "Kandarin"),
	KEBOS(7, "Kebos Lowlands"),
	KHARIDIAN_DESERT(8, "Kharidian Desert"),
	MISTHALIN(9, "Misthalin"),
	MORYTANIA(10, "Morytania"),
	PISCATORIS(11, "Piscatoris"),
	TIRANNWN(12, "Tirannwn"),
	WILDERNESS(13, "Wilderness"),
	UNKNOWN(14, "Unknown");

	@Getter
	private int id;
	@Getter
	private String name;

	ShootingStarsLocation(int id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public static ShootingStarsLocation determineLocation(String text)
	{
		text = text.replace("<br>", " ");
		for (ShootingStarsLocation l : values())
			if(text.contains(l.name))
				return l;
		return ShootingStarsLocation.UNKNOWN;
	}

	public static ShootingStarsLocation getLocation(int id)
	{
		for (ShootingStarsLocation l : values())
			if(l.getId() == id)
				return l;

		return ShootingStarsLocation.UNKNOWN;
	}
}
