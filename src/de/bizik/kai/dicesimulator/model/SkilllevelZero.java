package de.bizik.kai.dicesimulator.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public class SkilllevelZero extends Skilllevel {

	private SkilllevelZero() {}
	
	public static Skilllevel newInstance(int level) {
		if (level == 0)
			return new SkilllevelZero();
		else
			return Skilllevel.newInstance(level);
	}
	
	private final Difficulty difficulty = new Difficulty(0, 0);
	@Override
	public Difficulty getDifficulty(int difficulty) {
		return this.difficulty;
	}
	
	@Override
	public void addNewResults(int[] randomRolls) {}
	
	@Override
	public void updateStatistics() {}
	
}