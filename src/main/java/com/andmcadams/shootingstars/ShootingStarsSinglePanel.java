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

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;

public class ShootingStarsSinglePanel extends JPanel
{

	private static final Color INCOMING = Color.YELLOW;
	private static final Color LANDED = Color.GREEN;

	private JTextArea nameLabel;
	private JLabel world;
	private JTextArea time;

	@Getter
	private ShootingStarsData starData;

	public ShootingStarsSinglePanel(ShootingStarsData starData)
	{
		super();
		this.starData = starData;

		setLayout(new DynamicGridLayout(2, 1, 0, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel topPanel = new JPanel(new DynamicGridLayout(1, 2, 10, 0));
		topPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameLabel = new JTextArea();
		nameLabel.setWrapStyleWord(true);
		nameLabel.setLineWrap(true);
		nameLabel.setEditable(false);
		nameLabel.setOpaque(false);
		nameLabel.setFocusable(false);

		world = new JLabel("World " + starData.getWorld());
		nameLabel.setText(starData.getLocation().getName());

		topPanel.add(world);
		topPanel.add(nameLabel);

		time = new JTextArea();
		time.setWrapStyleWord(true);
		time.setLineWrap(true);
		time.setEditable(false);
		time.setOpaque(false);
		time.setFocusable(false);
		updateTime();
		updateLanded();
		add(topPanel);
		add(time);
	}

	public boolean updateLanded()
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

	public void updateTime()
	{
		time.setText(starData.getLandingTime());
	}
}
