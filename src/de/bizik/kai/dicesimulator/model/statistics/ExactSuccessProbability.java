package de.bizik.kai.dicesimulator.model.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import de.bizik.kai.dicesimulator.model.Difficulty;
import de.bizik.kai.dicesimulator.model.Skilllevel;

public class ExactSuccessProbability implements StatisticalProperty<Double> {

	private final Skilllevel level;
	private final Difficulty difficulty;
	private final int numberOfSuccesses;
	private final ReadOnlyObjectWrapper<Double> exactSuccessProbability = new ReadOnlyObjectWrapper<Double>(0.0d);
	
	public ExactSuccessProbability(Skilllevel level, Difficulty difficulty, int numberOfSuccesses) {
		this.level = level;
		this.difficulty = difficulty;
		this.numberOfSuccesses = numberOfSuccesses;
		recalculate();
	}

	@Override
	public ObservableValue<Double> getProperty() {
		return exactSuccessProbability.getReadOnlyProperty();
	}

	@Override
	public void recalculate() {
		BigInteger numberOfDiceSetRolls = level.getNumberOfDiceSetRolls();
		if (numberOfDiceSetRolls.equals(BigInteger.ZERO)) {
			exactSuccessProbability.set(0.0d);
			return;
		}
		List<BigInteger> successCounters = difficulty.getSuccessCounters();
		BigDecimal possibles = new BigDecimal(numberOfDiceSetRolls);
		BigInteger favorable = successCounters.get(numberOfSuccesses);
		if (numberOfSuccesses < successCounters.size() - 1)
			favorable = favorable.subtract(successCounters.get(numberOfSuccesses + 1));
		BigDecimal favorables = new BigDecimal(favorable);
		exactSuccessProbability.set(favorables.divide(possibles, 10, RoundingMode.DOWN).doubleValue());
	}

}
