package de.bizik.kai.dicesimulator.sim;

public interface RerollStrategy {

	public boolean needsToBeRerolled(int dieResult);
	
}
