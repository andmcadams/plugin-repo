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
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

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
	private ShootingStarsConfig config;

	@Inject
	private ShootingStarsOverlayPanel overlayPanel;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ClientThread clientThread;

	private final int SECONDS_BETWEEN_UPLOADS = 10;
	private final int SECONDS_BETWEEN_GET = 5;
	private final int SECONDS_BETWEEN_PANEL_REFRESH = 5;
	private boolean canRefresh;

	private ShootingStarsLocation lastLoc;
	private int lastWorld;

	static final String CONFIG_GROUP_KEY = "shootingstar";

	@Getter
	private String shootingStarPostEndpoint;

	@Getter
	private String shootingStarGetEndpoint;

	@Getter
	private String shootingStarsSharedKey;

	@Getter
	@Setter
	private ArrayList<ShootingStarsData> starData = new ArrayList<>();

	@Getter
	@Setter
	private boolean postError = false;

	@Getter
	@Setter
	private boolean getError = false;

	@Getter
	private boolean keyError = false;

	private ShootingStarsPanel shootingStarsPanel;
	private NavigationButton navButton;

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
		canRefresh = true;

		// Set up config variables
		shootingStarPostEndpoint = config.shootingStarPostEndpointConfig();
		shootingStarGetEndpoint = config.shootingStarGetEndpointConfig();
		shootingStarsSharedKey = config.shootingStarSharedKeyConfig();
		keyError = isInvalidKey(shootingStarsSharedKey);

		// Add the overlay to the OverlayManager
		overlayManager.add(overlayPanel);

		// Set up the sidebar panel
		shootingStarsPanel = new ShootingStarsPanel(this);
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/shooting_stars_icon.png");
		navButton = NavigationButton.builder().tooltip("Shooting Stars").icon(icon).priority(7).panel(shootingStarsPanel).build();
		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		// Reset non-config vars
		lastLoc = null;
		lastWorld = -1;

		// Remove the overlay from the OverlayManager
		overlayManager.remove(overlayPanel);

		// Remove sidebar panel button
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		switch (event.getKey())
		{
			case ShootingStarsConfig.SHOOTING_STAR_POST_ENDPOINT_KEYNAME:
				shootingStarPostEndpoint = config.shootingStarPostEndpointConfig();
				manager.makePostRequest(new ArrayList<Object>());
				break;
			case ShootingStarsConfig.SHOOTING_STAR_GET_ENDPOINT_KEYNAME:
				shootingStarGetEndpoint = config.shootingStarGetEndpointConfig();
				manager.makeGetRequest();
				break;
			case ShootingStarsConfig.SHOOTING_STAR_SHARED_KEY_KEYNAME:
				shootingStarsSharedKey = config.shootingStarSharedKeyConfig();
				keyError = isInvalidKey(shootingStarsSharedKey);
			default:
				break;
		}
	}

	private final Pattern firstMinThenHour = Pattern.compile(".* next (\\d+) minutes to (\\d+) hours? (\\d+) .*");
	private final Pattern hourRegex = Pattern.compile(".* next (\\d+) hours? (\\d+) minutes? to (\\d+) hours? (\\d+) .*");
	private final Pattern minutes = Pattern.compile(".* (\\d+) to (\\d+) .*");
	private static final int MAX_TIME_ADJ = 59;

	private void recordEvent(ShootingStarsLocation loc, int world, int minTime, int maxTime)
	{
		long currentTime = Instant.now().toEpochMilli();
		long lminTime = currentTime / 1000 + (minTime * 60);
		long lmaxTime = currentTime / 1000 + (maxTime * 60) + MAX_TIME_ADJ;
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

	public void updatePanelList()
	{
		log.info("Update panel list");
		SwingUtilities.invokeLater(() -> shootingStarsPanel.reloadListPanel());
	}

	@Schedule(
		period = SECONDS_BETWEEN_PANEL_REFRESH,
		unit = ChronoUnit.SECONDS,
		asynchronous = true
	)
	public void updatePanels()
	{
		log.info("Update panels");
		if (shootingStarsPanel.isOpen())
		{
			SwingUtilities.invokeLater(() -> shootingStarsPanel.refreshPanels());
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
	public void attemptGetRequest()
	{
		log.info("Attempt get request");
		hitAPI();
	}

	private final Pattern validKeyRegex = Pattern.compile("^[a-zA-Z]{1,10}$");

	private boolean isInvalidKey(String sharedKey)
	{
		log.info("key is valid: " + validKeyRegex.matcher(sharedKey).find());
		return !validKeyRegex.matcher(sharedKey).find();
	}

	public void hitAPI()
	{
		if (canRefresh)
		{
			if ((client.getGameState() == GameState.LOGGED_IN || client.getGameState() == GameState.HOPPING) &&
				!keyError && shootingStarsPanel.isOpen())
			{
				canRefresh = false;
				manager.makeGetRequest();
				Timer t = new Timer();
				t.schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						log.info("Resetting canRefresh");
						canRefresh = true;
					}
				}, 60 * 1000);
			}
		}
	}

}
