package de.bizik.kai.dicesimulator.sim;

public class BasicRerollStrategy implements RerollStrategy {

	private final int numberOfDieSides;

	public BasicRerollStrategy(int numberOfDieSides) {
		this.numberOfDieSides = numberOfDieSides;
	}
	
	@Override
	public boolean needsToBeRerolled(int dieResult) {
		return dieResult % numberOfDieSides == 0;
	}

}
