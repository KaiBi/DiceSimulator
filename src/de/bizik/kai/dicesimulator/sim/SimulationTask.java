package de.bizik.kai.dicesimulator.sim;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import javafx.beans.property.BooleanProperty;
import de.bizik.kai.dicesimulator.model.Model;

public class SimulationTask implements Runnable {

	private static final int randomRollsPerSimulationStep = 1000000;
	
	public SimulationTask(ExecutorService exService, BooleanProperty isRunning) {
		this.exService = exService;
		this.isRunning = isRunning;
	} 
	
	private final ExecutorService exService;
	private final BooleanProperty isRunning;
	
	// uncomment all line comments for empirical benchmark of optimized vs. unoptimized rerolling
//	private static long numRunsNormal = 0;
//	private static long numRunsOpti = 0;
//	private static long timeNormal = 0;
//	private static long timeOpti = 0;
//	private static boolean toggle = false;
	
	@Override
	public void run() {
		if (!isRunning.get())
			isRunning.set(true);
		Model model = Model.getModel();
//		long starttime = System.currentTimeMillis();
//		if (toggle)
			Arrays.stream(model.allowedDieKindsProperty().get()).forEach((i) -> {
				model.getNSidedDie(i).addNewResults(rollDiceOptimized(randomRollsPerSimulationStep, i));
			});
//		else
//			Arrays.stream(model.allowedDieKindsProperty().get()).forEach((i) -> {
//				model.getNSidedDie(i).addNewResults(rollDice(randomRollsPerSimulationStep, i));
//			});
		model.updateStatistics();
		model.autosaveIfNecessary();
//		long stoptime = System.currentTimeMillis();
//		if (toggle) {
//			numRunsOpti++;
//			timeOpti += stoptime - starttime;
//			double avgNormal = (double) timeNormal / numRunsNormal;
//			double avgOpti = (double) timeOpti / numRunsOpti;
//			double ratio = avgOpti / avgNormal;
//			System.out.println("Normal: " + avgNormal + "\tOpti: " + avgOpti + "\tRatio: " + ratio);
//		} else {
//			numRunsNormal++;
//			timeNormal += stoptime - starttime;
//		}
//		toggle = !toggle;
		try {
			exService.execute(this);
		} catch (RejectedExecutionException e) {
			isRunning.set(false);
		}
	}

//	private static int[] rollDice(int numDice, int numSides) {
//		int[] results = new int[numDice];
//		Arrays.fill(results, 0);
//		for (int i = 0; i < numDice; i++)
//			while (results[i] % numSides == 0)
//				results[i] += ThreadLocalRandom.current().nextInt(1, numSides + 1);
//		return results;
//	}

	private static int[] rollDiceOptimized(int numDice, int numSides) {
		int[] results = new int[numDice];
		Arrays.fill(results, 0);
		if (!rerollTables.containsKey(numDice))
			initRerollTable(numSides);
		boolean[] reroll = rerollTables.get(numSides);
		for (int i = 0; i < numDice; i++) {
			while (results[i] < reroll.length) {
				if (reroll[results[i]])
					results[i] += ThreadLocalRandom.current().nextInt(1, numSides + 1);
				else
					break;
			}
			if (results[i] >= reroll.length) {
				while (results[i] % numSides == 0)
					results[i] += ThreadLocalRandom.current().nextInt(1, numSides + 1);
			}
		}
		return results;
	}
	
	private static final Map<Integer, boolean[]> rerollTables = new HashMap<Integer, boolean[]>();
	private static void initRerollTable(Integer numSides) {
		boolean[] table = new boolean[numSides * 10];
		Arrays.fill(table, false);
		for (int i = 0; i < table.length; i += numSides)
			table[i] = true;
		rerollTables.put(numSides, table);
	}
	
}
