package de.bizik.kai.dicesimulator.model.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import de.bizik.kai.dicesimulator.model.Difficulty;

public class AverageNumberOfSuccesses implements StatisticalProperty<Double> {
	
	private final Difficulty difficulty;
	private final ReadOnlyObjectWrapper<Double> averageNumberOfSuccesses = new ReadOnlyObjectWrapper<Double>(0.0d);
	
	public AverageNumberOfSuccesses(Difficulty difficulty) {
		this.difficulty = difficulty;
		recalculate();
	}
	
	@Override
	public ObservableValue<Double> getProperty() {
		return averageNumberOfSuccesses.getReadOnlyProperty();
	}

	@Override
	public void recalculate() {
		BigInteger successesCounter = difficulty.getSuccessCounters().get(1);
		if (successesCounter.equals(BigInteger.ZERO)) {
			averageNumberOfSuccesses.set(0.0d);
			return;
		}
		BigInteger sumOfAllSuccessCounters = difficulty.getSuccessCounters().stream().skip(1).reduce(BigInteger.ZERO, BigInteger::add);
		BigDecimal sumOfAllSuccessCountersDecimal = new BigDecimal(sumOfAllSuccessCounters);
		BigDecimal successesCounterDecimal = new BigDecimal(successesCounter);
		averageNumberOfSuccesses.set(sumOfAllSuccessCountersDecimal.divide(successesCounterDecimal, 10, RoundingMode.DOWN).doubleValue());
	}

}
