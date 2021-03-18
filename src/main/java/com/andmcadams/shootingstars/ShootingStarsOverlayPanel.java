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

