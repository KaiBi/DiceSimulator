package de.bizik.kai.dicesimulator.model.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import de.bizik.kai.dicesimulator.model.Skilllevel;

public class AverageNumberOfSummarizedDots implements StatisticalProperty<Double> {

	private final Skilllevel level;
	private final ReadOnlyObjectWrapper<Double> averageNumberOfSummarizedDots = new ReadOnlyObjectWrapper<Double>();
	
	public AverageNumberOfSummarizedDots(Skilllevel level) {
		this.level = level;
		recalculate();
	}
	
	@Override
	public ObservableValue<Double> getProperty() {
		return averageNumberOfSummarizedDots.getReadOnlyProperty();
	}

	@Override
	public void recalculate() {
		BigInteger numberOfDiceSetRolls = level.getNumberOfDiceSetRolls();
		if (numberOfDiceSetRolls.equals(BigInteger.ZERO)) {
			averageNumberOfSummarizedDots.set(0.0d);
			return;
		}
		BigInteger sumOfSuccesses = level
				.getDifficulties()
				.stream()
				.map(d -> d.getSuccessCounters().stream().skip(1).reduce(BigInteger.ZERO, BigInteger::add))
				.reduce(BigInteger.ZERO, BigInteger::add);
		BigDecimal favorable = new BigDecimal(sumOfSuccesses);
		BigDecimal possible = new BigDecimal(numberOfDiceSetRolls);
		averageNumberOfSummarizedDots.set(favorable.divide(possible, 10, RoundingMode.DOWN).doubleValue());
	}

}
