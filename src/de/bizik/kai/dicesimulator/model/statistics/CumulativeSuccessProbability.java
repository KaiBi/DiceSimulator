package de.bizik.kai.dicesimulator.model.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import de.bizik.kai.dicesimulator.model.Difficulty;
import de.bizik.kai.dicesimulator.model.Skilllevel;

public class CumulativeSuccessProbability implements StatisticalProperty<Double> {

	private final Skilllevel level;
	private final Difficulty difficulty;
	private final int minNumberOfSuccesses;
	private final ReadOnlyObjectWrapper<Double> cumulativeSuccessProbability = new ReadOnlyObjectWrapper<Double>();
	
	public CumulativeSuccessProbability(Skilllevel level, Difficulty difficulty, int minNumberOfSuccesses) {
		this.level = level;
		this.difficulty = difficulty;
		this.minNumberOfSuccesses = minNumberOfSuccesses;
		recalculate();
	}

	@Override
	public ObservableValue<Double> getProperty() {
		return cumulativeSuccessProbability.getReadOnlyProperty();
	}

	@Override
	public void recalculate() {
		if (level.getNumberOfDiceSetRolls().equals(BigInteger.ZERO)) {
			cumulativeSuccessProbability.set(0.0d);
			return;
		}
		if (difficulty.getDifficulty() == 0) {
			cumulativeSuccessProbability.set(1.0d);
			return;
		}
		BigDecimal possible = new BigDecimal(level.getNumberOfDiceSetRolls());
		BigDecimal favorable = new BigDecimal(difficulty.getSuccessCounters().get(minNumberOfSuccesses));
		cumulativeSuccessProbability.set(favorable.divide(possible, 10, RoundingMode.DOWN).doubleValue());
	}

}
