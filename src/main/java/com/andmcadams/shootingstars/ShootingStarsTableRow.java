/*
 * Copyright (c) 2021, Cyborger1, Psikoi <https://github.com/Psikoi> (Basis)
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
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

public class ShootingStarsTableRow extends JPanel
{
	private static final int WORLD_COLUMN_WIDTH = 35;
	private static final int TIME_COLUMN_WIDTH = 45;
	private static final int TYPE_COLUMN_WIDTH = 35;

	private static final Color CURRENT_WORLD = new Color(66, 227, 17);
	private static final Color DANGEROUS_WORLD = new Color(251, 62, 62);
	private static final Color TOURNAMENT_WORLD = new Color(79, 145, 255);
	private static final Color MEMBERS_WORLD = new Color(210, 193, 53);
	private static final Color FREE_WORLD = new Color(200, 200, 200);
	private static final Color LEAGUE_WORLD = new Color(133, 177, 178);


	private static final Color COLOR_NEGATIVE = new Color(255, 80, 80);

	private JLabel worldField;
	private JLabel minTimeField;
	private JLabel maxTimeField;
	private JLabel locationField;
	private JLabel worldTypeField;

	@Getter
	private final World world;

	@Getter
	private Instant minTime;

	@Getter
	private boolean minPast;

	@Getter
	private Instant maxTime;

	@Getter
	private boolean maxPast;

	@Getter
	private ShootingStarsLocation starLocation;

	private Color lastBackground;

	ShootingStarsTableRow(World world, boolean current, Instant minTime, Instant maxTime, ShootingStarsLocation location, Consumer<World> onSelect)
	{
		this.world = world;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.starLocation = location;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(2, 0, 2, 0));

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					if (onSelect != null)
					{
						onSelect.accept(world);
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setBackground(getBackground().brighter());
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setBackground(getBackground().darker());
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				ShootingStarsTableRow.this.lastBackground = getBackground();
				setBackground(getBackground().brighter());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setBackground(lastBackground);
			}
		});

		JPanel leftSide = new JPanel(new BorderLayout());
		JPanel rightSide = new JPanel(new BorderLayout());
		leftSide.setOpaque(false);
		rightSide.setOpaque(false);

		JPanel worldField = buildWorldField();
		worldField.setPreferredSize(new Dimension(WORLD_COLUMN_WIDTH, 0));
		worldField.setOpaque(false);

		JPanel minTimeField = buildMinTimeField();
		minTimeField.setPreferredSize(new Dimension(TIME_COLUMN_WIDTH, 0));
		minTimeField.setOpaque(false);

		JPanel maxTimeField = buildMaxTimeField();
		maxTimeField.setPreferredSize(new Dimension(TIME_COLUMN_WIDTH, 0));
		maxTimeField.setOpaque(false);

		JPanel locationField = buildLocationField();
		locationField.setBorder(new EmptyBorder(5, 5, 5, 5));
		locationField.setOpaque(false);

		JPanel typeField = buildTypeField();
		typeField.setPreferredSize(new Dimension(TYPE_COLUMN_WIDTH, 0));
		typeField.setOpaque(false);

		updateStatus(current);

		leftSide.add(worldField, BorderLayout.WEST);
		leftSide.add(minTimeField, BorderLayout.CENTER);
		leftSide.add(maxTimeField, BorderLayout.EAST);
		rightSide.add(locationField, BorderLayout.CENTER);
		rightSide.add(typeField, BorderLayout.EAST);

		add(leftSide, BorderLayout.WEST);
		add(rightSide, BorderLayout.CENTER);
	}

	void updateStatus(boolean current)
	{
		StringBool min = timeString(minTime);
		StringBool max = timeString(maxTime);
		minTimeField.setText(min.getString());
		maxTimeField.setText(max.getString());
		locationField.setText(starLocation.getShortName());
		if (!starLocation.getName().equals(starLocation.getShortName()))
		{
			locationField.setToolTipText(starLocation.getName());
		}

		minPast = min.isBoolValue();
		maxPast = max.isBoolValue();
		if (minPast && maxPast)
		{
			minTimeField.setForeground(COLOR_NEGATIVE);
			maxTimeField.setForeground(COLOR_NEGATIVE);
			locationField.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
		}
		else if (minPast)
		{
			minTimeField.setForeground(COLOR_NEGATIVE);
			maxTimeField.setForeground(Color.WHITE);
			locationField.setForeground(Color.YELLOW);
		}
		else
		{
			minTimeField.setForeground(Color.WHITE);
			maxTimeField.setForeground(Color.WHITE);
			locationField.setForeground(ColorScheme.PROGRESS_INPROGRESS_COLOR);
		}

		recolour(current);
	}

	void updateInfo(Instant minTime, Instant maxTime, ShootingStarsLocation location, boolean current)
	{
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.starLocation = location;
		updateStatus(current);
	}

	private static StringBool timeString(Instant time)
	{
		long s = Duration.between(Instant.now(), time).getSeconds();
		boolean negative = false;
		if (s < 0)
		{
			s *= -1;
			negative = true;
		}

		String str;
		if (s / 60 > 99)
		{
			str = String.format("%dm", s / 60);
		}
		else
		{
			str = String.format("%02d:%02d", s / 60, s % 60);
		}

		return new StringBool(str, negative);
	}

	public void recolour(boolean current)
	{
		if (current)
		{
			worldTypeField.setForeground(CURRENT_WORLD);
			worldField.setForeground(CURRENT_WORLD);
			return;
		}
		else if (world.getTypes().contains(WorldType.PVP)
			|| world.getTypes().contains(WorldType.HIGH_RISK)
			|| world.getTypes().contains(WorldType.DEADMAN))
		{
			worldTypeField.setForeground(DANGEROUS_WORLD);
		}
		else if (world.getTypes().contains(WorldType.LEAGUE))
		{
			worldTypeField.setForeground(LEAGUE_WORLD);
		}
		else if (world.getTypes().contains(WorldType.TOURNAMENT))
		{
			worldTypeField.setForeground(TOURNAMENT_WORLD);
		}
		else
		{
			worldTypeField.setForeground(Color.WHITE);
		}

		worldField.setForeground(world.getTypes().contains(WorldType.MEMBERS) ? MEMBERS_WORLD : FREE_WORLD);
	}

	/**
	 * Builds the world list field (containing the country's flag and the world index).
	 */
	private JPanel buildWorldField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		worldField = new JLabel(world.getId() + "");
		column.add(worldField, BorderLayout.WEST);

		return column;
	}

	private JPanel buildMinTimeField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		minTimeField = new JLabel();
		column.add(minTimeField, BorderLayout.CENTER);

		return column;
	}

	private JPanel buildMaxTimeField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		maxTimeField = new JLabel();
		column.add(maxTimeField, BorderLayout.CENTER);

		return column;
	}

	private JPanel buildLocationField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		locationField = new JLabel();
		locationField.setFont(FontManager.getRunescapeSmallFont());

		column.add(locationField, BorderLayout.CENTER);

		return column;
	}

	private JPanel buildTypeField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		String act = world.getActivity();

		StringBuilder sb = new StringBuilder();
		EnumSet<WorldType> types = world.getTypes();

		if (types.contains(WorldType.SKILL_TOTAL))
		{
			sb.append(act.split(" ")[0]);
		}
		if (types.contains(WorldType.PVP))
		{
			sb.append("P");
		}
		if (types.contains(WorldType.DEADMAN))
		{
			sb.append("D");
		}
		if (types.contains(WorldType.TOURNAMENT) || types.contains(WorldType.DEADMAN_TOURNAMENT))
		{
			sb.append("T");
		}
		if (types.contains(WorldType.LEAGUE))
		{
			sb.append("L");
		}
		if (types.contains(WorldType.HIGH_RISK))
		{
			sb.append("H");
		}

		worldTypeField = new JLabel(sb.toString());
		worldTypeField.setFont(FontManager.getRunescapeSmallFont());

		if (act != null && !act.equals(""))
		{
			worldTypeField.setToolTipText(act);
		}

		column.add(worldTypeField, BorderLayout.EAST);
		return column;
	}

	public String getWorldType()
	{
		return worldTypeField.getText();
	}

	@Value
	@AllArgsConstructor
	private static class StringBool
	{
		String string;
		boolean boolValue;
	}
}
