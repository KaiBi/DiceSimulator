package de.bizik.kai.dicesimulator.model.statistics;

import javafx.beans.value.ObservableValue;

public interface StatisticalProperty<T> {

	public ObservableValue<T> getProperty();
	public void recalculate();
	
}
