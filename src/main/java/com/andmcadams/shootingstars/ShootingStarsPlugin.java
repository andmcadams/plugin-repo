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

import com.google.inject.Provides;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Shooting Stars",
	description = "Crowdsources the locations of shooting stars",
	tags = {"stars", "mining", "shooting star"}
)
public class ShootingStarsPlugin extends Plugin
{
	@Inject
	Client client;

	@Inject
	private ShootingStarsDataManager manager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ShootingStarsOverlayPanel overlayPanel;

	@Inject
	private ShootingStarsConfig config;

	private final int SECONDS_BETWEEN_UPLOADS = 10;
	private final int SECONDS_BETWEEN_GET = 30;

	private ShootingStarsLocation lastLoc;
	private int lastWorld;

	static final String CONFIG_GROUP_KEY = "shootingstar";

	@Getter
	@Setter
	private List<ShootingStarsData> starData = null;

	@Getter
	private String shootingStarPostEndpoint;

	@Getter
	private String shootingStarGetEndpoint;

	@Getter
	private String shootingStarsSharedKey;

	@Getter
	@Setter
	private boolean postError = false;

	@Getter
	@Setter
	private boolean getError = false;

	@Provides
	ShootingStarsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShootingStarsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		lastLoc = null;
		lastWorld = -1;
		overlayManager.add(overlayPanel);
		shootingStarPostEndpoint = config.shootingStarPostEndpointConfig();
		shootingStarGetEndpoint = config.shootingStarGetEndpointConfig();
		shootingStarsSharedKey = config.shootingStarSharedKeyConfig();
	}

	@Override
	protected void shutDown() throws Exception
	{
		lastLoc = null;
		lastWorld = -1;
		overlayManager.remove(overlayPanel);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		switch (event.getKey())
		{
			case ShootingStarsConfig.SHOOTING_STAR_POST_ENDPOINT_KEYNAME:
				shootingStarPostEndpoint = config.shootingStarPostEndpointConfig();
				break;
			case ShootingStarsConfig.SHOOTING_STAR_GET_ENDPOINT_KEYNAME:
				shootingStarGetEndpoint = config.shootingStarGetEndpointConfig();
				break;
			case ShootingStarsConfig.SHOOTING_STAR_SHARED_KEY_KEYNAME:
				shootingStarsSharedKey = config.shootingStarSharedKeyConfig();
			default:
				break;
		}
	}

	private Pattern firstMinThenHour = Pattern.compile(".* next (\\d+) minutes to (\\d+) hours? (\\d+) .*");
	private Pattern hourRegex = Pattern.compile(".* next (\\d+) hours? (\\d+) minutes? to (\\d+) hours? (\\d+) .*");
	private Pattern minutes = Pattern.compile(".* (\\d+) to (\\d+) .*");


	private void recordEvent(ShootingStarsLocation loc, int world, int minTime, int maxTime)
	{
		long currentTime = Instant.now().toEpochMilli();
		long lminTime = currentTime / 1000 + (minTime * 60);
		long lmaxTime = currentTime / 1000 + (maxTime * 60);
		manager.storeEvent(new ShootingStarsData(loc, world, lminTime, lmaxTime));
		lastWorld = world;
		lastLoc = loc;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		Widget chatbox = client.getWidget(229, 1);
		if (chatbox != null)
		{
			int world = client.getWorld();
			ShootingStarsLocation loc = ShootingStarsLocation.determineLocation(chatbox.getText());
			if (world != lastWorld || (loc != null && loc != lastLoc))
			{
				String text = chatbox.getText();
				text = text.replace("<br>", " ");

				Matcher m = firstMinThenHour.matcher(text);
				if (m.find())
				{
					int minTime = Integer.parseInt(m.group(1));
					int maxTime = 60 * Integer.parseInt(m.group(2)) + Integer.parseInt(m.group(3));
					recordEvent(loc, world, minTime, maxTime);
					return;
				}
				m = hourRegex.matcher(text);
				if (m.find())
				{
					int minTime = 60 * Integer.parseInt(m.group(1)) + Integer.parseInt(m.group(2));
					int maxTime = 60 * Integer.parseInt(m.group(3)) + Integer.parseInt(m.group(4));
					recordEvent(loc, world, minTime, maxTime);
					return;
				}
				m = minutes.matcher(text);
				if (m.find())
				{
					int minTime = Integer.parseInt(m.group(1));
					int maxTime = Integer.parseInt(m.group(2));
					recordEvent(loc, world, minTime, maxTime);
					return;
				}
			}
		}
	}

	@Schedule(
		period = SECONDS_BETWEEN_UPLOADS,
		unit = ChronoUnit.SECONDS,
		asynchronous = true
	)
	public void submitToAPI()
	{
		manager.submitToAPI();
	}

	@Schedule(
		period = SECONDS_BETWEEN_GET,
		unit = ChronoUnit.SECONDS,
		asynchronous = true
	)
	public void hitAPI()
	{
		manager.hitAPI();
	}

}
