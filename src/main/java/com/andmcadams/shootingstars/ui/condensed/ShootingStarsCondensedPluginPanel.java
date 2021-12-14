/*
 * Copyright (c) 2021, Andrew McAdams, Cyborger1, Psikoi <https://github.com/Psikoi> (Basis)
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
package com.andmcadams.shootingstars.ui.condensed;

import com.andmcadams.shootingstars.ShootingStarsData;
import com.andmcadams.shootingstars.ShootingStarsLocation;
import com.andmcadams.shootingstars.ShootingStarsPlugin;
import com.andmcadams.shootingstars.ui.ShootingStarsPluginPanelBase;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.Comparator.naturalOrder;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

public class ShootingStarsCondensedPluginPanel extends ShootingStarsPluginPanelBase
{
	private static final Color ODD_ROW = new Color(44, 44, 44);

	private static final int WORLD_COLUMN_WIDTH = 35;
	private static final int TIME_COLUMN_WIDTH = 45;
	private static final int TYPE_COLUMN_WIDTH = 35;

	private final JPanel listContainer = new JPanel();

	private ShootingStarsPanelHeader worldHeader;
	private ShootingStarsPanelHeader minTimeHeader;
	private ShootingStarsPanelHeader maxTimeHeader;
	private ShootingStarsPanelHeader locationHeader;
	private ShootingStarsPanelHeader worldTypeHeader;

	private ShootingStarsOrder orderIndex = ShootingStarsOrder.MAX_TIME;
	private boolean ascendingOrder = true;

	private final ArrayList<ShootingStarsTableRow> rows = new ArrayList<>();

	public ShootingStarsCondensedPluginPanel(ShootingStarsPlugin plugin)
	{
		super(plugin);

		setBorder(null);
		setLayout(new DynamicGridLayout(0, 1));

		JPanel headerContainer = buildHeader();

		listContainer.setLayout(new GridLayout(0, 1));

		add(headerContainer);
		add(listContainer);
	}

	/**
	 * Builds the entire table header.
	 */
	private JPanel buildHeader()
	{
		JPanel header = new JPanel(new BorderLayout());
		JPanel leftSide = new JPanel(new BorderLayout());
		JPanel rightSide = new JPanel(new BorderLayout());

		worldHeader = new ShootingStarsPanelHeader("W", orderIndex == ShootingStarsOrder.WORLD, ascendingOrder);
		worldHeader.setPreferredSize(new Dimension(WORLD_COLUMN_WIDTH, 0));
		worldHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = orderIndex != ShootingStarsOrder.WORLD || !ascendingOrder;
				orderBy(ShootingStarsOrder.WORLD);
			}
		});

		minTimeHeader = new ShootingStarsPanelHeader("Min", orderIndex == ShootingStarsOrder.MIN_TIME, ascendingOrder);
		minTimeHeader.setPreferredSize(new Dimension(TIME_COLUMN_WIDTH, 0));
		minTimeHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = orderIndex != ShootingStarsOrder.MIN_TIME || !ascendingOrder;
				orderBy(ShootingStarsOrder.MIN_TIME);
			}
		});

		maxTimeHeader = new ShootingStarsPanelHeader("Max", orderIndex == ShootingStarsOrder.MAX_TIME, ascendingOrder);
		maxTimeHeader.setPreferredSize(new Dimension(TIME_COLUMN_WIDTH, 0));
		maxTimeHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = orderIndex != ShootingStarsOrder.MAX_TIME || !ascendingOrder;
				orderBy(ShootingStarsOrder.MAX_TIME);
			}
		});

		locationHeader = new ShootingStarsPanelHeader("Location", orderIndex == ShootingStarsOrder.LOCATION, ascendingOrder);
		locationHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = orderIndex != ShootingStarsOrder.LOCATION || !ascendingOrder;
				orderBy(ShootingStarsOrder.LOCATION);
			}
		});

		worldTypeHeader = new ShootingStarsPanelHeader("T", orderIndex == ShootingStarsOrder.TYPE, ascendingOrder);
		worldTypeHeader.setPreferredSize(new Dimension(TYPE_COLUMN_WIDTH, 0));
		worldTypeHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = orderIndex != ShootingStarsOrder.TYPE || !ascendingOrder;
				orderBy(ShootingStarsOrder.TYPE);
			}
		});

		leftSide.add(worldHeader, BorderLayout.WEST);
		leftSide.add(minTimeHeader, BorderLayout.CENTER);
		leftSide.add(maxTimeHeader, BorderLayout.EAST);
		rightSide.add(locationHeader, BorderLayout.CENTER);
		rightSide.add(worldTypeHeader, BorderLayout.EAST);

		header.add(leftSide, BorderLayout.WEST);
		header.add(rightSide, BorderLayout.CENTER);

		return header;
	}

	private void orderBy(ShootingStarsOrder order)
	{
		worldHeader.highlight(false, ascendingOrder);
		minTimeHeader.highlight(false, ascendingOrder);
		maxTimeHeader.highlight(false, ascendingOrder);
		locationHeader.highlight(false, ascendingOrder);
		worldTypeHeader.highlight(false, ascendingOrder);

		switch (order)
		{
			case WORLD:
				worldHeader.highlight(true, ascendingOrder);
				break;
			case MIN_TIME:
				minTimeHeader.highlight(true, ascendingOrder);
				break;
			case MAX_TIME:
				maxTimeHeader.highlight(true, ascendingOrder);
				break;
			case LOCATION:
				locationHeader.highlight(true, ascendingOrder);
				break;
			case TYPE:
				worldTypeHeader.highlight(true, ascendingOrder);
				break;
		}

		orderIndex = order;
		updateList();
	}

	@Override
	public void updateList()
	{
		rows.sort((r1, r2) ->
		{
			switch (orderIndex)
			{
				case WORLD:
					return getCompareValue(r1, r2, row -> row.getWorld().getId());
				case MIN_TIME:
					return getCompareValue(r1, r2, ShootingStarsTableRow::getMinTime);
				case MAX_TIME:
					return getCompareValue(r1, r2, ShootingStarsTableRow::getMaxTime);
				case LOCATION:
					return getCompareValue(r1, r2, row -> row.getStarLocation().getShortName());
				case TYPE:
					return getCompareValue(r1, r2, ShootingStarsTableRow::getWorldType);
				default:
					return 0;
			}
		});

		listContainer.removeAll();

		int currentWorld = plugin.getCurrentWorld();

		int i = 0;
		for (ShootingStarsTableRow row : rows)
		{
			// Disallow old stars from being displayed
			Duration timeSinceLanded = Duration.between(row.getMaxTime(), Instant.now());
			if (timeSinceLanded.toMinutes() < plugin.getConfig().shootingStarExpirationLength())
			{
				row.updateStatus(row.getWorld().getId() == currentWorld);
				setColorOnRow(row, i++ % 2 == 0);
				listContainer.add(row);
			}
		}

		listContainer.revalidate();
		listContainer.repaint();
	}

	@SuppressWarnings("rawtypes")
	private int getCompareValue(ShootingStarsTableRow row1, ShootingStarsTableRow row2,
								Function<ShootingStarsTableRow, Comparable> compareByFn)
	{
		Comparator<ShootingStarsTableRow> c = ascendingOrder ?
			comparing(compareByFn, naturalOrder()) : comparing(compareByFn, reverseOrder());
		// Always default to ordering by Max time for the second sort pass
		return c.thenComparing(ShootingStarsTableRow::getMaxTime, naturalOrder()).compare(row1, row2);
	}

	@Override
	public void populate(List<ShootingStarsData> stars)
	{
		rows.clear();

		for (int i = 0; i < stars.size(); i++)
		{
			ShootingStarsData star = stars.get(i);
			rows.add(buildRow(star, i % 2 == 0));
		}

		updateList();
	}

	private ShootingStarsTableRow buildRow(ShootingStarsData star, boolean stripe)
	{
		World world = plugin.getWorldService().getWorlds().findWorld(star.getWorld());
		boolean current = plugin.getCurrentWorld() == star.getWorld();
		ShootingStarsTableRow row = new ShootingStarsTableRow(
			world, current,
			Instant.ofEpochSecond(star.getMinTime()),
			Instant.ofEpochSecond(star.getMaxTime()),
			star.getShootingStarsLocation(), plugin::hopTo);

		// Create a right click menu that can hide the world
		row.createRightClickMenu(this);

		setColorOnRow(row, stripe);
		return row;
	}

	private void setColorOnRow(ShootingStarsTableRow row, boolean stripe)
	{
		EnumSet<WorldType> types = row.getWorld().getTypes();
		Color c = stripe ? ODD_ROW : ColorScheme.DARK_GRAY_COLOR;
		if (row.getStarLocation() == ShootingStarsLocation.WILDERNESS
			|| types.contains(WorldType.PVP)
			|| types.contains(WorldType.DEADMAN)
			|| types.contains(WorldType.TOURNAMENT))
		{
			c = new Color(
				c.getRed(),
				c.getGreen() / 2,
				c.getBlue() / 2,
				c.getAlpha()
			);
		}

		row.setBackground(c);
	}

}
