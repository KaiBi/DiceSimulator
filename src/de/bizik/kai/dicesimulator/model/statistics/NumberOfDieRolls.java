package de.bizik.kai.dicesimulator.model.statistics;

import java.math.BigInteger;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import de.bizik.kai.dicesimulator.model.Skilllevel;

public class NumberOfDieRolls implements StatisticalProperty<BigInteger> {

	private final Skilllevel level;
	
	public NumberOfDieRolls(Skilllevel level) {
		this.level = level;
		numberOfDiceSetRolls = new ReadOnlyObjectWrapper<BigInteger>();
		recalculate();
	}
	
	private final ReadOnlyObjectWrapper<BigInteger> numberOfDiceSetRolls;
	
	@Override
	public ObservableValue<BigInteger> getProperty() {
		return numberOfDiceSetRolls.getReadOnlyProperty();
	}

	@Override
	public void recalculate() {
		numberOfDiceSetRolls.set(level.getNumberOfDiceSetRolls());
	}

}
