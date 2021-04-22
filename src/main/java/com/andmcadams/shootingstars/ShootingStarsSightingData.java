package com.andmcadams.shootingstars;

import lombok.Data;

@Data
public class ShootingStarsSightingData
{
	private final int world;
	private final int x;
	private final int y;
	private final int tier;
	private final long time;
	private final boolean isSpawn;
}
