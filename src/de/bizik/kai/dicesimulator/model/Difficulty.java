package de.bizik.kai.dicesimulator.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
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
public class Difficulty {

	@XmlElement(name = "difficulty")
	private final int difficulty;
	@XmlElement(name = "maxPossibleSuccesses")
	private final int maxPossibleSuccesses;
	@XmlElement(name = "successes")
	private final List<BigInteger> successes;
	
	public Difficulty(int difficulty, int maxPossibleSuccesses) {
		this.difficulty = difficulty;
		this.maxPossibleSuccesses = maxPossibleSuccesses;
		this.successes = new ArrayList<BigInteger>(maxPossibleSuccesses + 1);
		successProbabilitiesProperty = new ArrayList<DoubleProperty>(maxPossibleSuccesses + 1);
		exactSuccessProbabilitiesProperty = new ArrayList<DoubleProperty>(maxPossibleSuccesses + 1);
		for (int i = 0; i < maxPossibleSuccesses + 2; i++) {
			successes.add(BigInteger.ZERO);
			successProbabilitiesProperty.add(new SimpleDoubleProperty(0.0d));
			exactSuccessProbabilitiesProperty.add(new SimpleDoubleProperty(0.0d));
		}
	}

	public Difficulty() {
		this(0, 0);
	}
	
	public void addNewResults(int[] randomRolls) {
		int numberOfNewResults = randomRolls.length / maxPossibleSuccesses;
		
		// count the number of successes per partition
		int[] succ = new int[maxPossibleSuccesses + 1];
		for (int resultIndex = 0; resultIndex < numberOfNewResults; resultIndex++) {
			int sum = 0;
			for (int i = 0; i < maxPossibleSuccesses; i++) {
				if (randomRolls[resultIndex * maxPossibleSuccesses + i] > difficulty)
					sum++;
			}
			succ[sum]++;
		}
		
		for (int i = 0; i < succ.length; i++)
			successes.set(i, successes.get(i).add(BigInteger.valueOf(succ[i])));
	}

	private final List<DoubleProperty> successProbabilitiesProperty;
	public ReadOnlyDoubleProperty successProbabilityProperty(int numSuccesses) {
		return successProbabilitiesProperty.get(numSuccesses);
	}
	
	private void updateSuccessProbability(BigInteger numRolls) {
		for (int i = successProbabilitiesProperty.size(); i < maxPossibleSuccesses + 2; i++)
			successProbabilitiesProperty.add(new SimpleDoubleProperty(0.0d));
		if (numRolls.equals(BigInteger.ZERO))
			return;
		successProbabilitiesProperty.get(0).set(1.0d);
		for (int i = 1; i < successProbabilitiesProperty.size(); i++) {
			BigDecimal possibles = new BigDecimal(numRolls);
			BigDecimal favorables = new BigDecimal(successes.get(i));
			successProbabilitiesProperty.get(i).set(favorables.divide(possibles, 10, RoundingMode.DOWN).doubleValue());
		}
	}
	
	private ObjectProperty<BigInteger> totalOfSuccessfulThrows = new SimpleObjectProperty<BigInteger>(BigInteger.ZERO);
	public ReadOnlyObjectProperty<BigInteger> totalOfSuccessfulThrowsProperty() {
		return totalOfSuccessfulThrows;
	}
	
	private void updateTotalOfSuccessfulThrows() {
		totalOfSuccessfulThrows.set(successes.stream().skip(1).reduce(BigInteger.ZERO, BigInteger::add));
	}
	
	private DoubleProperty averageNumberOfSuccessesProperty = new SimpleDoubleProperty(0.0d);
	public ReadOnlyDoubleProperty averageNumberOfSuccessesProperty() {
		return averageNumberOfSuccessesProperty;
	}
	
	private void updateAverageNumberOfSuccesses() {
		BigInteger numSuccs = successes.get(1);
		if (numSuccs.equals(BigInteger.ZERO))
			return;
		BigInteger allThrows = successes.stream().skip(1).reduce(BigInteger.ZERO, BigInteger::add);
		BigDecimal allThrowsWeightedSum = new BigDecimal(allThrows);
		BigDecimal numberSuccesses = new BigDecimal(numSuccs);
		averageNumberOfSuccessesProperty.set(allThrowsWeightedSum.divide(numberSuccesses, 10, RoundingMode.DOWN).doubleValue());
	}
	
	private void updateExactSuccessProbability(BigInteger numRolls) {
		for (int i = exactSuccessProbabilitiesProperty.size(); i < maxPossibleSuccesses + 2; i++)
			exactSuccessProbabilitiesProperty.add(new SimpleDoubleProperty(0.0d));
		if (numRolls.equals(BigInteger.ZERO))
			return;
		exactSuccessProbabilitiesProperty.get(0).set(0.0d);
		for (int i = 1; i < exactSuccessProbabilitiesProperty.size(); i++) {
			BigDecimal possibles = new BigDecimal(numRolls);
			BigInteger favs = successes.get(i);
			if (i < successes.size() - 1)
				favs = favs.subtract(successes.get(i + 1));
			BigDecimal favorables = new BigDecimal(favs);
			exactSuccessProbabilitiesProperty.get(i).set(favorables.divide(possibles, 10, RoundingMode.DOWN).doubleValue());
		}
	}
	
	private final List<DoubleProperty> exactSuccessProbabilitiesProperty;
	public ReadOnlyDoubleProperty exactSuccessProbability(int numSuccesses) {
		return exactSuccessProbabilitiesProperty.get(numSuccesses);
	}
	
	public void updateStatistics(BigInteger numRolls) {
		updateOwnStatistics(numRolls);
	}

	private void updateOwnStatistics(BigInteger numRolls) {
		updateTotalOfSuccessfulThrows();
		updateSuccessProbability(numRolls);
		updateExactSuccessProbability(numRolls);
		updateAverageNumberOfSuccesses();
	}
	
}
