package com.andmcadams.giantsfoundry;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Giants Foundry"
)
public class GiantsFoundryPlugin extends Plugin
{
	public static final int SWORD_TYPE_1 = 13907; // 4=Broad, 2=Light
	public static final int SWORD_TYPE_2 = 13908; // 3=Flat, 6=Spiked

	public static final int MOULD_TAB_VARBIT = 13909; // 0,1,2 values of the 3 tabs
	public static final int FORTE_MOULD_VARBIT = 13910;
	public static final int BLADES_MOULD_VARBIT = 13911;
	public static final int TIP_MOULD_VARBIT = 13912;

	public static final int UNSURE2 = 13913; // 0->95 when pouring crucible

	/*  1=Mould set
		2=Crucible poured
		3=Picked up preform but then goes to 0 in the same tick
	*/
	public static final int STAGE_VARBIT = 13914;

	public static final int CRUCIBLE_MITHRIL_VARBIT = 13934;
	public static final int CRUCIBLE_ADAMANT_VARBIT = 13935;

	public static final int UNSURE = 13947; // 1 when preformed in storage, 0 when preform equipped

	public static final int TEMPERATURE_VARBIT = 13948; // 0-1000
	public static final int TASK_VARBIT = 13949; // 0-1000

	public static final int CURRENT_QUALITY_VARBIT = 13939;
	public static final int MAX_QUALITY_VARBIT = 13950;
	private enum ActionsEnum {
		HAMMER,
		GRINDSTONE,
		POLISH,
		NULL;

		public static ActionsEnum getActionFromId(int id)
		{
			if (id == 0)
				return HAMMER;
			else if (id == 1)
				return GRINDSTONE;
			else if (id == 2)
				return POLISH;
			return NULL;
		}
	}

	@Inject
	private Client client;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	private int lastGuess = 0;
	private ActionsEnum lastStep = ActionsEnum.NULL;
	@Subscribe
	private void onGameTick(GameTick tick)
	{
		ActionsEnum currentStep = determineCurrentStep();
		if (currentStep != lastStep)
		{
			log.info(String.format("Step change: %s -> %s", lastStep.name(), currentStep.name()));
			lastStep = currentStep;
			if (lastGuess != 1)
			{
				log.error("");
				log.error("Last guess was " + lastGuess);
				log.error("");
			}
		}
		lastGuess = estimateNumberActionsRemaining();
	}

	// Action counter
	// Bar prediction - only depends on bars and not
	// Mould list change
	private int getCurrentHeat()
	{
		return client.getVarbitValue(13948);
	}

	private int script_6119()
	{
		// https://github.com/Joshua-F/cs2-scripts/blob/master/scripts/%5Bproc,script6119%5D.cs2
		int tier = client.getVarbitValue(13938);
		if (tier >= 0 && tier < 20)
			return 3;
		if (tier >= 20 && tier < 60)
			return 4;
		if (tier >= 60 && tier < 90)
			return 5;
		if (tier >= 90 && tier < 120)
			return 6;
		if (tier >= 120)
			return 7;
		return 3;
	}

	private List<ActionsEnum> getSteps()
	{
		// Polish 2, Grind 1, Hammer 0
		int numSteps = script_6119();
		ArrayList<ActionsEnum> l = new ArrayList<>();
		// First one is always hammer
		l.add(ActionsEnum.HAMMER);
		if (numSteps >= 2)
			l.add(ActionsEnum.getActionFromId(client.getVarbitValue(13940)));
		if (numSteps >= 3)
			l.add(ActionsEnum.getActionFromId(client.getVarbitValue(13941)));
		if (numSteps >= 4)
			l.add(ActionsEnum.getActionFromId(client.getVarbitValue(13942)));
		if (numSteps >= 5)
			l.add(ActionsEnum.getActionFromId(client.getVarbitValue(13943)));
		if (numSteps >= 6)
			l.add(ActionsEnum.getActionFromId(client.getVarbitValue(13944)));
		if (numSteps >= 7)
			l.add(ActionsEnum.getActionFromId(client.getVarbitValue(13945)));
		return l;
	}

	private int getCurrentStepIndex()
	{
		// Effectively 6120
		int numSteps = script_6119();
		return numSteps*client.getVarbitValue(13949)/1000;
	}

	private int getProgressForStep(int step)
	{
		// Return the minimum progress required for the given step to be active
		int maxStep = script_6119();
		if (step == maxStep)
			return 1000;
		return (int) Math.ceil(1000.0*((double)step)/((double)maxStep));
	}

	private ActionsEnum determineCurrentStep()
	{
		List<ActionsEnum> l = getSteps();
		int stepIndex = getCurrentStepIndex();
		ActionsEnum currentStep = ActionsEnum.NULL;
		if (stepIndex < l.size())
		{
			currentStep = l.get(stepIndex);
			log.debug(String.format("Current step %s", currentStep.name()));
		}
		else
			log.debug("Current step null");
		return currentStep;
	}

	private int estimateNumberActionsRemaining()
	{
		ActionsEnum currentAction = determineCurrentStep();
		int stepIndex = getCurrentStepIndex();
		int minProgressNextStep = getProgressForStep(stepIndex+1);
		int progressToNextStep = minProgressNextStep - client.getVarbitValue(13949);
		log.info("Progress to next step: " + progressToNextStep + " => " + stepIndex + " -> " + (stepIndex+1) + " (" + minProgressNextStep +  ")");
		int numActions = 0;
		if (currentAction == ActionsEnum.HAMMER)
			numActions = (int) Math.ceil(((double)progressToNextStep)/20.0);
		if (currentAction == ActionsEnum.GRINDSTONE || currentAction == ActionsEnum.POLISH)
			numActions = (int) Math.ceil(((double)progressToNextStep)/10.0);
		log.info("Actions to next step: " + numActions + " => " + stepIndex + " -> " + (stepIndex+1));
		return numActions;
	}

	private int estimateNumberActionsPossible()
	{
		// Grind + 15 heat
		// Polish - 17 heat
		// Hammer - 25 heat
		// Just handle the case where heat is decr now
		ActionsEnum currentAction = determineCurrentStep();
		int ticksUntilNextAction = 5;
		return 0;
	}

}
