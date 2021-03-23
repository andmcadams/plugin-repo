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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class ShootingStarsPanel extends PluginPanel
{

	ShootingStarsPlugin plugin;
	FixedWidthPanel starsListPanel = new FixedWidthPanel();
	ArrayList<ShootingStarsSinglePanel> starsList = new ArrayList<>();

	@Getter
	private boolean open = false;

	private JScrollPane scrollPane;
	private GridBagConstraints c = new GridBagConstraints();

	public ShootingStarsPanel(ShootingStarsPlugin plugin)
	{
		super(false);

		this.plugin = plugin;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());

		// Create the container for the title and refresh task button
		JPanel topContainer = new JPanel();
		topContainer.setLayout(new BorderLayout());

		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		titlePanel.setLayout(new BorderLayout());

		JLabel title = new JLabel();
		title.setText("Shooting Stars");
		title.setForeground(Color.WHITE);
		titlePanel.add(title, BorderLayout.WEST);
		topContainer.add(titlePanel, BorderLayout.NORTH);

		add(topContainer, BorderLayout.NORTH);

		// Create the task list panel
		starsListPanel.setLayout(new GridBagLayout());
		starsListPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		c.insets = new Insets(0, 2, 2, 2);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.anchor = GridBagConstraints.PAGE_START;

		scrollPane = new JScrollPane(starsListPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new EmptyBorder(1, 0, 0, 0));
		scrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		add(scrollPane, BorderLayout.CENTER);
	}

	public void addStar(JPanel shootingStarsPanel, ShootingStarsData data)
	{
		ShootingStarsSinglePanel starsSinglePanel = new ShootingStarsSinglePanel(data);
		shootingStarsPanel.add(starsSinglePanel, c);
		c.gridy += 1;
		starsList.add(starsSinglePanel);
	}

	private boolean isAllowedWorld(ShootingStarsData starData)
	{
	    switch(starData.getLocation())
		{
			case ASGARNIA:
				return plugin.getConfig().shootingStarShowAsgarniaWorlds();
			case KARAMJA:
				return plugin.getConfig().shootingStarShowKaramjaWorlds();
			case FELDIP_HILLS:
				return plugin.getConfig().shootingStarShowFeldipWorlds();
			case FOSSIL_ISLAND:
				return plugin.getConfig().shootingStarShowFossilIslandWorlds();
			case FREMENNIK:
				return plugin.getConfig().shootingStarShowFremennikWorlds();
			case KOUREND:
				return plugin.getConfig().shootingStarShowKourendWorlds();
			case KANDARIN:
				return plugin.getConfig().shootingStarShowKandarinWorlds();
			case KEBOS:
				return plugin.getConfig().shootingStarShowKebosWorlds();
			case KHARIDIAN_DESERT:
				return plugin.getConfig().shootingStarShowDesertWorlds();
			case MISTHALIN:
				return plugin.getConfig().shootingStarShowMisthalinWorlds();
			case MORYTANIA:
				return plugin.getConfig().shootingStarShowMorytaniaWorlds();
			case PISCATORIS:
				return plugin.getConfig().shootingStarShowPiscatorisWorlds();
			case TIRANNWN:
				return plugin.getConfig().shootingStarShowTirannwnWorlds();
			case WILDERNESS:
				return plugin.getConfig().shootingStarShowWildernessWorlds();
		}
	    return true;
	}

	public void reloadListPanel()
	{
		c.gridy = 0;
		c.weighty = 0;
		// Remove all old panels
		for (ShootingStarsSinglePanel starsSinglePanel : starsList)
		{
			starsListPanel.remove(starsSinglePanel);
		}
		starsList.clear();

		// Add new panels. Need to keep track of the last one to give it extra weighty (to put all extra space after it)
		ArrayList<ShootingStarsData> starsData = plugin.getStarData();
		ShootingStarsData lastData = null;
		for (int i = 0; i < starsData.size(); i++)
		{
			ShootingStarsData starData = starsData.get(i);
			// Skip certain worlds based on config
			if (!isAllowedWorld(starData))
                continue;

			if (lastData != null)
				addStar(starsListPanel, lastData);
			lastData = starData;
		}

		// Add the last panel with weighty 1
		c.weighty = 1;
		if (lastData != null)
			addStar(starsListPanel, lastData);

		repaint();
		revalidate();
	}

	public void refreshPanels()
	{
		for (ShootingStarsSinglePanel starsSinglePanel : starsList)
		{
			starsSinglePanel.updateLabels();
		}
	}

	public void onActivate()
	{
		log.info("Activated");
		open = true;
		plugin.hitAPI();
	}

	public void onDeactivate()
	{
		log.info("Deactivated");
		open = false;
	}

}
