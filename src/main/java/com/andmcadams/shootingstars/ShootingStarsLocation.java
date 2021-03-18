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
