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

import java.time.Duration;
import java.time.Instant;
import lombok.Data;
import lombok.Getter;

@Data
public class ShootingStarsData
{
	@Getter
	private final int location;

	@Getter
	private final int world;

	@Getter
	private final long minTime;

	@Getter
	private final long maxTime;

	private static final String NOW_STRING = "Now";

	public ShootingStarsData(ShootingStarsLocation loc, int world, long minTime, long maxTime)
	{
		this.location = loc.getId();
		this.world = world;
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	public ShootingStarsLocation getShootingStarsLocation()
	{
		return ShootingStarsLocation.getLocation(this.location);
	}

	public boolean hasLanded()
	{
		Duration timeUntil = Duration.between(Instant.now(), Instant.ofEpochMilli(this.minTime * 1000));
		return NOW_STRING.equals(prettyPrintTime(timeUntil));
	}

	private String prettyPrintTime(Duration d)
	{
		long hours = d.toHours();
		StringBuilder timeStringBuilder = new StringBuilder();
		if (hours != 0)
			timeStringBuilder.append(hours).append(" hr ");

		long minutes = d.toMinutes() % 60;
		if (hours == 0 && minutes <= 0)
		{
			long seconds = d.getSeconds() % 60;
			if (seconds > 0)
				timeStringBuilder.append(String.format("%d sec", seconds));
			else
				timeStringBuilder.append(NOW_STRING);
		}
		else
			timeStringBuilder.append(String.format("%d min", minutes));

		return timeStringBuilder.toString();
	}

	public String getLandingTime()
	{
		String minTimeString = prettyPrintTime(Duration.between(Instant.now(), Instant.ofEpochMilli(this.minTime * 1000)));
		String maxTimeString = prettyPrintTime(Duration.between(Instant.now(), Instant.ofEpochMilli(this.maxTime * 1000)));

		// If the star has definitely landed, just return "Now"
		if (NOW_STRING.equals(maxTimeString))
			return NOW_STRING;

		return minTimeString + " - " + maxTimeString;
	}
}
