package com.andmcadams.shootingstars;

import lombok.Data;
import lombok.Getter;

@Data
public class ShootingStarsData
{
	@Getter
	private final int loc;

	@Getter
	private final int world;

	@Getter
	private final long minTime;

	@Getter
	private final long maxTime;

	public ShootingStarsData(ShootingStarsLocation loc, int world, long minTime, long maxTime)
	{
		this.loc = loc.getId();
		this.world = world;
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	public ShootingStarsLocation getLocation()
	{
		return ShootingStarsLocation.getLocation(this.loc);
	}

}
