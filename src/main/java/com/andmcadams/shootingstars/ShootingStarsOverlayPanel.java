package com.andmcadams.shootingstars;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

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

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();

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
		if (plugin.isKeyError())
		{
			LineComponent l = LineComponent.builder().left("Key error: Please set a valid key in the plugin config. Valid keys are 1-10 alpha characters").build();
			panelComponent.getChildren().add(l);
		}
		return super.render(graphics);
	}
}
