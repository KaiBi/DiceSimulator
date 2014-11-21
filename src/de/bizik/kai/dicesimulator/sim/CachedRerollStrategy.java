package de.bizik.kai.dicesimulator.sim;


public class CachedRerollStrategy implements RerollStrategy {

	private static final int CACHE_SIZE = 512;
	
	private final boolean[] decisionCache = new boolean[CACHE_SIZE];
	private final RerollStrategy wrappedRerollStrategy;

	@Override
	public boolean needsToBeRerolled(int dieResult) {
		if (dieResult < decisionCache.length)
			return decisionCache[dieResult];
		return wrappedRerollStrategy.needsToBeRerolled(dieResult);
	}
	
	public CachedRerollStrategy(RerollStrategy wrappedRerollStrategy) {
		this.wrappedRerollStrategy = wrappedRerollStrategy;
		initializeCache();
	}
	
	private void initializeCache() {
		for (int i = 0; i < decisionCache.length; i++)
			decisionCache[i] = wrappedRerollStrategy.needsToBeRerolled(i);
	}

}
