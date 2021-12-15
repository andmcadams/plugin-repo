/*
 * Copyright (c) 2021, Cyborger1
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
package com.andmcadams.shootingstars.ui;

import com.andmcadams.shootingstars.ShootingStarsData;
import com.andmcadams.shootingstars.ShootingStarsPlugin;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public abstract class ShootingStarsPluginPanelBase extends PluginPanel
{
	@Getter
	private boolean open = false;

	protected ShootingStarsPlugin plugin;

	protected ShootingStarsPluginPanelBase(ShootingStarsPlugin plugin)
	{
		this(plugin, true);
	}

	protected ShootingStarsPluginPanelBase(ShootingStarsPlugin plugin, boolean wrap)
	{
		super(wrap);
		this.plugin = plugin;
	}

	public abstract void populate(List<ShootingStarsData> stars);
	public abstract void updateList();

	public void onActivate()
	{
		// If the panel is opened, try to run a get request to populate/refresh the panel.
		log.debug("Activated");
		open = true;
		plugin.hitAPI();
	}

	public void onDeactivate()
	{
		log.debug("Deactivated");
		open = false;
	}

	public void hideWorld(int world, long maxTime)
	{
		plugin.hideWorld(world, maxTime);
	}

	public void resetHiddenWorlds()
	{
		plugin.resetHiddenWorlds();
	}
}
