package de.bizik.kai.dicesimulator.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public class Skilllevel {

	@XmlElement(name = "level")
	private final int level;
	@XmlElement(name = "difficulties")
	private final List<Difficulty> difficulties = new ArrayList<Difficulty>(INITIALDIFFICULTIES);
	
	private static final int INITIALDIFFICULTIES = 30;
	
	// number of rolls on this skill level
	@XmlElement(name = "numRolls")
	private BigInteger numRolls = BigInteger.ZERO;
	
	private Skilllevel(int level) {
		this.level = level;
		for (int i = 0; i < INITIALDIFFICULTIES + 1; i++)
			difficulties.add(new Difficulty(i, level));
	}
	
	protected Skilllevel() {
		this(3);
	}

	public static Skilllevel newInstance(int level) {
		if (level == 0)
			return SkilllevelZero.newInstance(level);
		if (level > 0)
			return new Skilllevel(level);
		return null;
	}
	
	public Difficulty getDifficulty(int difficulty) {
		for (int i = difficulties.size(); i < difficulty + 2; i++)
			difficulties.add(new Difficulty(i, level));
		return difficulties.get(difficulty);
	}
	
	public void addNewResults(int[] randomRolls) {
		if (randomRolls == null || randomRolls.length < 1 || level == 0)
			return;
		
		// we can use randomRolls.length / level partitions of the array for new results
		numRolls = numRolls.add(BigInteger.valueOf(randomRolls.length / level));
		
		// get the highest result of this set
		int highest = Arrays.stream(randomRolls).max().getAsInt();
		
		// make sure, all difficulties up to highest are in the list (highest-1 would suffice)
		getDifficulty(highest);
		
		// add the results to all difficulties
		difficulties
			.stream()
			.forEach(diff -> diff.addNewResults(randomRolls));
	}

	public void updateStatistics() {
		for (Difficulty d : difficulties)
			d.updateStatistics(numRolls);
		updateOwnStatistics();
	}
	
	private void updateOwnStatistics() {
		updateAverage();
		numRollsProperty.set(numRolls);
	}
	
	private final DoubleProperty averageProperty = new SimpleDoubleProperty(0.0d);
	public ReadOnlyDoubleProperty averageProperty() {
		return averageProperty;
	}
	
	private void updateAverage() {
		if (numRolls.equals(BigInteger.ZERO))
			return;
		BigInteger sum = difficulties
				.stream()
				.map(d -> d.totalOfSuccessfulThrowsProperty().get())
				.reduce(BigInteger.ZERO, BigInteger::add);
		BigDecimal favorables = new BigDecimal(sum);
		BigDecimal possibles = new BigDecimal(numRolls);
		averageProperty.set(favorables.divide(possibles, 10, RoundingMode.DOWN).doubleValue());
	}

	private ObjectProperty<BigInteger> numRollsProperty = new SimpleObjectProperty<BigInteger>(BigInteger.ZERO);
	public ReadOnlyObjectProperty<BigInteger> numRollsProperty() {
		return numRollsProperty;
	}

}
