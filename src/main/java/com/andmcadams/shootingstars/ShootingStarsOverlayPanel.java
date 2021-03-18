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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class ShootingStarsOverlayPanel extends OverlayPanel
{
	private final ShootingStarsPlugin plugin;

	@Inject
	private ShootingStarsOverlayPanel(ShootingStarsPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;

		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.HIGH);
	}

	private String prettyPrintTime(Duration d)
	{
		long hours = d.toHours();
		StringBuilder timeStringBuilder = new StringBuilder();
		if (hours == 1)
			timeStringBuilder.append(hours).append(" hour and ");
		else if(hours == 2)
			timeStringBuilder.append(hours).append(" hours and ");
		long minutes = 1 + (d.toMinutes() % 60);
		timeStringBuilder.append(String.format("%d minutes", minutes));

		return timeStringBuilder.toString();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();

		if (plugin.isPostError() || plugin.isGetError())
		{
			if (plugin.isPostError())
			{
				LineComponent l = LineComponent.builder().left("Shooting Stars Error: Failed to post, check POST endpoint in config").build();
				panelComponent.getChildren().add(l);
			}
			if (plugin.isGetError())
			{
				LineComponent l = LineComponent.builder().left("Shooting Stars Error: Failed to get, check GET endpoint in config").build();
				panelComponent.getChildren().add(l);
			}
			return super.render(graphics);
		}
		panelComponent.getChildren().add(TitleComponent.builder().text("Next Star").build());
		if (plugin.getStarData() != null && plugin.getStarData().size() > 0)
		{
			ShootingStarsData soonest = plugin.getStarData().get(0);
			LineComponent l = LineComponent.builder().left("World: " + soonest.getWorld() + ": " + soonest.getLocation().getName()).build();
			panelComponent.getChildren().add(l);
			String timeLeftMin = prettyPrintTime(Duration.between(Instant.now(), Instant.ofEpochMilli(soonest.getMinTime() * 1000)));
			LineComponent l2 = LineComponent.builder().left(timeLeftMin).build();
			panelComponent.getChildren().add(l2);
			String timeLeftMax = prettyPrintTime(Duration.between(Instant.now(), Instant.ofEpochMilli(soonest.getMaxTime() * 1000)));
			LineComponent l3 = LineComponent.builder().left(timeLeftMax).build();
			panelComponent.getChildren().add(l3);
		}
		else {
			LineComponent l = LineComponent.builder().left("Unknown").build();
			panelComponent.getChildren().add(l);
		}
		return super.render(graphics);
	}
}

