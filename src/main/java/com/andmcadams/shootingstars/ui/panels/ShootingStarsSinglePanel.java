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
package com.andmcadams.shootingstars.ui.panels;

import com.andmcadams.shootingstars.ShootingStarsData;
import com.andmcadams.shootingstars.ui.condensed.ShootingStarsCondensedPluginPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;

public class ShootingStarsSinglePanel extends JPanel
{

	private static final Color INCOMING = Color.YELLOW;
	private static final Color LANDED = Color.GREEN;

	private JLabel nameLabel;
	private JLabel world;
	private JLabel time;

	@Getter
	private ShootingStarsData starData;

	private Color lastBackground;

	public ShootingStarsSinglePanel(ShootingStarsData starData, Consumer<Integer> onSelect)
	{
		super();
		this.starData = starData;

		setLayout(new DynamicGridLayout(2, 1, 0, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		world = new JLabel("World " + starData.getWorld());
		nameLabel = new JLabel("<html>" + starData.getShootingStarsLocation().getName() + "</html>");
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nameLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

		topPanel.add(world, BorderLayout.WEST);
		topPanel.add(nameLabel, BorderLayout.CENTER);

		time = new JLabel();

		updateLabels();
		add(topPanel);
		add(time);

		// From WorldHopper/Condensed Stars Panel
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					if (onSelect != null)
					{
						onSelect.accept(starData.getWorld());
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
				ShootingStarsSinglePanel.this.lastBackground = getBackground();
				setBackground(getBackground().brighter());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setBackground(lastBackground);
			}
		});
	}

	private boolean updateLanded()
	{
		Color c;
		boolean hasLanded = starData.hasLanded();
		if (hasLanded)
			c = LANDED;
		else
			c = INCOMING;
		nameLabel.setForeground(c);
		return hasLanded;
	}

	private void updateTime()
	{
		time.setText(starData.getLandingTime());
	}

	public void updateLabels()
	{
		updateLanded();
		updateTime();
		repaint();
		revalidate();
	}

	public void createRightClickMenu(ShootingStarsPluginPanel panel)
	{
		JPopupMenu rightClickMenu = new JPopupMenu();
		rightClickMenu.setBorder(new EmptyBorder(1, 1, 1, 1));
		JMenuItem removeEntryOption = new JMenuItem();
		removeEntryOption.setText("Hide for this wave");
		removeEntryOption.setFont(FontManager.getRunescapeSmallFont());
		removeEntryOption.addActionListener(e -> panel.hideWorld(this.starData.getWorld(), this.starData.getMaxTime()));
		removeEntryOption.setBorder(new EmptyBorder(5, 0, 5, 0));

		JMenuItem resetHiddenWorldsOptions = new JMenuItem();
		resetHiddenWorldsOptions.setText("Unhide all worlds");
		resetHiddenWorldsOptions.setFont(FontManager.getRunescapeSmallFont());
		resetHiddenWorldsOptions.addActionListener(e -> panel.resetHiddenWorlds());
		resetHiddenWorldsOptions.setBorder(new EmptyBorder(5, 0, 5, 0));

		rightClickMenu.add(removeEntryOption);
		rightClickMenu.add(resetHiddenWorldsOptions);
		this.setComponentPopupMenu(rightClickMenu);
	}
}
