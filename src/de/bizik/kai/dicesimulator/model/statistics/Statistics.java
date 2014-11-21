package de.bizik.kai.dicesimulator.model.statistics;

import java.math.BigInteger;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import de.bizik.kai.dicesimulator.model.Difficulty;
import de.bizik.kai.dicesimulator.model.Model;
import de.bizik.kai.dicesimulator.model.Skilllevel;
import de.bizik.kai.dicesimulator.sim.Simulator;

public class Statistics {

	private final Model model;
	private final Observable simulationStepsCountProperty = Simulator.INSTANCE.simulationIterationsCountProperty();
	
	public Statistics(Model model) {
		this.model = model;
	}
	
	public ObservableValue<Double> getAverageNumberOfSummarizedDots(int numberOfDieSides, int level) {
		AverageNumberOfSummarizedDots average = new AverageNumberOfSummarizedDots(model.getDieKind(numberOfDieSides).getLevel(level));
		simulationStepsCountProperty.addListener((observable) -> average.recalculate());
		return average.getProperty();
	}
	
	public ObservableValue<BigInteger> getNumberOfDieRolls(int numberOfDieSides, int numberOfDies) {
		NumberOfDieRolls numRolls = new NumberOfDieRolls(model.getDieKind(numberOfDieSides).getLevel(numberOfDies));
		simulationStepsCountProperty.addListener((observable) -> numRolls.recalculate());
		return numRolls.getProperty();
	}

	public ObservableValue<Double> getCumulativeSuccessProbability(int numberOfDieSides, int numberOfDies, int difficulty, int minNumberOfSuccesses) {
		Skilllevel level = model.getDieKind(numberOfDieSides).getLevel(numberOfDies);
		Difficulty diff = level.getDifficulty(difficulty);
		CumulativeSuccessProbability probability = new CumulativeSuccessProbability(level, diff, minNumberOfSuccesses);
		simulationStepsCountProperty.addListener((observable) -> probability.recalculate());
		return probability.getProperty();
	}
	
	public ObservableValue<Double> getAverageNumberOfSuccesses(int numberOfDieSides, int numberOfDies, int difficulty) {
		Difficulty diff = model.getDieKind(numberOfDieSides).getLevel(numberOfDies).getDifficulty(difficulty);
		AverageNumberOfSuccesses average = new AverageNumberOfSuccesses(diff);
		simulationStepsCountProperty.addListener((observable) -> average.recalculate());
		return average.getProperty();
	}
	
	public ObservableValue<Double> getExactSuccessProbability(int numberOfDieSides, int numberOfDies, int difficulty, int numberOfSuccesses) {
		Skilllevel level = model.getDieKind(numberOfDieSides).getLevel(numberOfDies);
		Difficulty diff = level.getDifficulty(difficulty);
		ExactSuccessProbability probability = new ExactSuccessProbability(level, diff, numberOfSuccesses);
		simulationStepsCountProperty.addListener((observable) -> probability.recalculate());
		return probability.getProperty();
	}
}
