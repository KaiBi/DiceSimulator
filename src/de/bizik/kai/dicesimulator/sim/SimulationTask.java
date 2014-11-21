package de.bizik.kai.dicesimulator.sim;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import de.bizik.kai.dicesimulator.model.DieKind;
import de.bizik.kai.dicesimulator.model.Model;
import de.bizik.kai.dicesimulator.model.ModelVisitor;

public class SimulationTask implements Runnable {

	private static final int rollsPerIteration = 1000000;
	private static final Map<DieKind, RerollStrategy> rerollStrategies = new HashMap<DieKind, RerollStrategy>();

	private final ExecutorService executorService;
	private final BooleanProperty isRunning;
	private final IntegerProperty iterationsCounter;
	private final Model model;

	public SimulationTask(ExecutorService exexutorService, BooleanProperty isRunning,
			IntegerProperty iterationsCounter) {
		this.executorService = exexutorService;
		this.isRunning = isRunning;
		this.iterationsCounter = iterationsCounter;
		this.model = Model.getModel();
	}

	@Override
	public void run() {
		isRunning.set(true);
		Map<DieKind, int[]> simulatedRolls = simulateDieRolls();
		insertNewRollsIntoModel(simulatedRolls);
		model.autosaveIfIntervalPassed();
		iterationsCounter.set(iterationsCounter.get() + 1);
		scheduleNextSimulationTaskOrStop();
	}

	private Map<DieKind, int[]> simulateDieRolls() {
		Map<DieKind, int[]> rollsForAllDieKinds = new HashMap<DieKind, int[]>();
		for (DieKind dk : model.getDieKinds()) {
			RerollStrategy strategy = getRerollStrategy(dk);
			int[] rolls = simulateDieRolls(rollsPerIteration, dk.getNumberOfDieSides(), strategy);
			rollsForAllDieKinds.put(dk, rolls);
		}
		return rollsForAllDieKinds;
	}

	private void insertNewRollsIntoModel(Map<DieKind, int[]> simulatedRolls) {
		simulatedRolls.keySet().stream().parallel().forEach((dieKind) -> {
			ModelVisitor insertNewRollsVisitor = new InsertNewRollsVisitor(simulatedRolls.get(dieKind));
			dieKind.accept(insertNewRollsVisitor);
		});
	}

	private void scheduleNextSimulationTaskOrStop() {
		try {
			executorService.execute(this);
		} catch (RejectedExecutionException e) {
			isRunning.set(false);
		}
	}

	private static int[] simulateDieRolls(int numberOfDiceToRoll, int numberOfDieSides, RerollStrategy rerollStrategy) {
		int[] results = new int[numberOfDiceToRoll];
		int startingRoll = 0;
		for (int i = 0; i < numberOfDiceToRoll; i++) {
			results[i] = calculateRollResult(startingRoll, numberOfDieSides, rerollStrategy);
		}
		return results;
	}

	private static RerollStrategy getRerollStrategy(DieKind dieKind) {
		if (!rerollStrategies.containsKey(dieKind)) {
			int dieSides = dieKind.getNumberOfDieSides();
			RerollStrategy strategy = new CachedRerollStrategy(new BasicRerollStrategy(dieSides));
			rerollStrategies.put(dieKind, strategy);
		}
		return rerollStrategies.get(dieKind);
	}

	private static int calculateRollResult(int currentResult, int numberOfDieSides, RerollStrategy rerollStrategy) {
		if (rerollStrategy.needsToBeRerolled(currentResult)) {
			int newResult = currentResult + rollSingleDieOnce(numberOfDieSides);
			return calculateRollResult(newResult, numberOfDieSides, rerollStrategy);
		}
		return currentResult;
	}

	private static int rollSingleDieOnce(int numberOfDieSides) {
		return ThreadLocalRandom.current().nextInt(1, numberOfDieSides + 1);
	}

}
