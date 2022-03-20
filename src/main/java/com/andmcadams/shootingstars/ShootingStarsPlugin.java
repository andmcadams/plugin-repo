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

import com.andmcadams.shootingstars.ui.ShootingStarsPluginPanelBase;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

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
	private ChatMessageManager chatMessageManager;

	@Inject
	private ShootingStarsDataManager manager;

	@Inject
	@Getter
	private ShootingStarsConfig config;

	@Inject
	private ShootingStarsOverlayPanel overlayPanel;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ClientThread clientThread;

	@Inject
	@Getter
	private WorldService worldService;

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

	// Map of World => MaxTime so that we can hide stars for that World with max time < MaxTime (hide this wave's stars)
	private final HashMap<Integer, Long> hiddenWorlds = new HashMap<>();

	@Getter
	@Setter
	private boolean postError = false;

	@Getter
	@Setter
	private boolean getError = false;

	@Getter
	private boolean keyError = false;

	private ShootingStarsPluginPanelBase shootingStarsPanel;
	private NavigationButton navButton = null;
	private final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/shooting_stars_icon.png");

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
		loadPluginPanel();
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
		if (!event.getGroup().equals(CONFIG_GROUP_KEY))
		{
			return;
		}
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
				canRefresh = true;
				break;
			case ShootingStarsConfig.SHOOTING_STAR_PANEL_CLASS:
				loadPluginPanel();
				updatePanelList();
				break;
			default:
				updatePanelList();
				break;
		}
	}

	private void loadPluginPanel()
	{
		if (navButton != null)
		{
			clientToolbar.removeNavigation(navButton);
		}

		Class<? extends ShootingStarsPluginPanelBase> clazz = config.shootingStarsPanelType().getPanelClass();
		try
		{
			shootingStarsPanel = clazz.getDeclaredConstructor(this.getClass()).newInstance(this);
		}
		catch (Exception e)
		{
			log.error("Error loading panel class", e);
			return;
		}

		navButton = NavigationButton.builder().tooltip("Shooting Stars").icon(icon).priority(7).panel(shootingStarsPanel).build();
		clientToolbar.addNavigation(navButton);
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
		handleHop();

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
		log.debug("Update panel list");
		SwingUtilities.invokeLater(() -> shootingStarsPanel.populate(starData.stream().filter(this::isAllowedWorld).collect(Collectors.toList())));
	}

	@Schedule(
		period = SECONDS_BETWEEN_PANEL_REFRESH,
		unit = ChronoUnit.SECONDS,
		asynchronous = true
	)
	public void updatePanels()
	{
		log.debug("Update panels");
		if (shootingStarsPanel.isOpen())
		{
			SwingUtilities.invokeLater(() -> shootingStarsPanel.updateList());
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
		log.debug("Attempt get request");
		hitAPI();
	}

	private final Pattern validKeyRegex = Pattern.compile("^[a-zA-Z]{1,10}$");

	private boolean isInvalidKey(String sharedKey)
	{
		log.debug("key is valid: " + validKeyRegex.matcher(sharedKey).find());
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
						log.debug("Resetting canRefresh");
						canRefresh = true;
					}
				}, 30 * 1000);
			}
		}
	}

	private boolean isAllowedWorld(ShootingStarsData starData)
	{
		// Protect against non-existent world ids
		WorldResult worldResult = worldService.getWorlds();
		if (worldResult == null)
		{
			return false;
		}
		World world = worldResult.findWorld(starData.getWorld());
		if (world == null)
		{
			return false;
		}

		// Disallow old stars from being displayed
		Duration timeSinceLanded = Duration.between(Instant.ofEpochSecond(starData.getMaxTime()), Instant.now());
		if (timeSinceLanded.toMinutes() >= config.shootingStarExpirationLength())
		{
			return false;
		}

		// Disallow hidden stars from being displayed
		if (isWorldHidden(starData))
		{
			return false;
		}

		// Disallow PVP worlds from being displayed (depending on config)
		if (!config.shootingStarShowPvpWorlds() && world.getTypes().contains(WorldType.PVP))
		{
			return false;
		}

		// Disallow Skill Total worlds from being displayed (depending on config)
		if (!config.shootingStarShowSkillTotalWorlds() && world.getTypes().contains(WorldType.SKILL_TOTAL))
		{
			return false;
		}

		// Disallow non-members worlds from showing up
		if (!world.getTypes().contains(WorldType.MEMBERS))
		{
			return false;
		}

		// Disallow various landing sites (depending on config)
		return starData.getShootingStarsLocation().getConfigFunction().apply(config);
	}

	/*
		Stealing from quick hopper plugin from here on, close your eyes...
	*/
	public int getCurrentWorld()
	{
		return client.getWorld();
	}

	private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;
	private net.runelite.api.World quickHopTargetWorld;
	private int displaySwitcherAttempts = 0;

	public void hopTo(World world)
	{
		hopTo(world.getId());
	}

	public void hopTo(int worldId)
	{
		clientThread.invoke(() -> hop(worldId));
	}

	private void hop(int worldId)
	{
		assert client.isClientThread();

		if (!config.isWorldHopperEnabled())
		{
			return;
		}

		WorldResult worldResult = worldService.getWorlds();
		if (worldResult == null)
		{
			return;
		}
		// Don't try to hop if the world doesn't exist
		World world = worldResult.findWorld(worldId);
		if (world == null)
		{
			return;
		}

		final net.runelite.api.World rsWorld = client.createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

		String chatMessage = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append("Quick-hopping to World ")
			.append(ChatColorType.HIGHLIGHT)
			.append(Integer.toString(world.getId()))
			.append(ChatColorType.NORMAL)
			.append("..")
			.build();

		chatMessageManager
			.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(chatMessage)
				.build());

		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			// on the login screen we can just change the world by ourselves
			client.changeWorld(rsWorld);
			return;
		}

		quickHopTargetWorld = rsWorld;
	}

	private void handleHop()
	{
		if (quickHopTargetWorld == null)
		{
			return;
		}

		if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
		{
			client.openWorldHopper();

			if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS)
			{
				String chatMessage = new ChatMessageBuilder()
					.append(ChatColorType.NORMAL)
					.append("Failed to quick-hop after ")
					.append(ChatColorType.HIGHLIGHT)
					.append(Integer.toString(displaySwitcherAttempts))
					.append(ChatColorType.NORMAL)
					.append(" attempts.")
					.build();

				chatMessageManager
					.queue(QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(chatMessage)
						.build());

				resetQuickHopper();
			}
		}
		else
		{
			client.hopToWorld(quickHopTargetWorld);
			resetQuickHopper();
		}
	}

	private void resetQuickHopper()
	{
		quickHopTargetWorld = null;
		displaySwitcherAttempts = 0;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (event.getMessage().equals("Please finish what you're doing before using the World Switcher."))
		{
			resetQuickHopper();
		}
	}

	public void hideWorld(int world, long maxTime)
	{
		hiddenWorlds.put(world, maxTime);
		updatePanelList();
	}

	public boolean isWorldHidden(ShootingStarsData data)
	{
		return hiddenWorlds.containsKey(data.getWorld()) && hiddenWorlds.get(data.getWorld()) >= data.getMaxTime();
	}

	public void resetHiddenWorlds()
	{
		hiddenWorlds.clear();
		updatePanelList();
	}
}
