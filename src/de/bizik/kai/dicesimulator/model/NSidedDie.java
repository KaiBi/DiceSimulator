package de.bizik.kai.dicesimulator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public class NSidedDie {

	@XmlElement(name="numSides")
	private final int numSides;
	@XmlElement(name="skilllevels")
	private final List<Skilllevel> skilllevels = new ArrayList<Skilllevel>();
	
	public static final int MAX_LEVEL = 50;

	public NSidedDie() {
		this(6);
	}
	
	public NSidedDie(int numSides) {
		this.numSides = numSides;
		for (int i = 0; i < MAX_LEVEL + 1; i++)
			skilllevels.add(Skilllevel.newInstance(i));
	}
	
	public Skilllevel getLevel(int level) {
		if (level > MAX_LEVEL)
			throw new IllegalArgumentException();
		return skilllevels.get(level);
	}
	
	public int getNumSides() {
		return numSides;
	}
	
	public void addNewResults(int[] randomRolls) {
		skilllevels.parallelStream().forEach(level -> level.addNewResults(randomRolls));
	}

	public void updateStatistics() {
		for (Skilllevel l : skilllevels)
			l.updateStatistics();
	}
}
