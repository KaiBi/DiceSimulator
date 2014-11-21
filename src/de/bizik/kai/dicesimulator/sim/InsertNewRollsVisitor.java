package de.bizik.kai.dicesimulator.sim;

import java.math.BigInteger;
import java.util.Arrays;

import de.bizik.kai.dicesimulator.model.DieKind;
import de.bizik.kai.dicesimulator.model.Difficulty;
import de.bizik.kai.dicesimulator.model.Model;
import de.bizik.kai.dicesimulator.model.ModelElement;
import de.bizik.kai.dicesimulator.model.ModelVisitor;
import de.bizik.kai.dicesimulator.model.Skilllevel;

public class InsertNewRollsVisitor implements ModelVisitor {

	private final int[] newRolls;
	
	public InsertNewRollsVisitor(int[] newRolls) {
		this.newRolls = newRolls;
	}
	
	@Override
	public void visit(Model model) {
	}

	@Override
	public void visit(DieKind dieKind) {
		for (ModelElement level : dieKind.getChildren())
			level.accept(this);
	}

	@Override
	public void visit(Skilllevel level) {
		for (ModelElement difficulty : level.getChildren())
			difficulty.accept(this);
		updateSkilllevel(level);
	}

	@Override
	public void visit(Difficulty difficulty) {
		updateDifficulty(difficulty);
	}

	private void updateSkilllevel(Skilllevel level) {
		if (level.getNumberOfDice() == 0)
			return;
		
		BigInteger oldNumberOfDiceSetRolls = level.getNumberOfDiceSetRolls();
		BigInteger additionalDiceSetRolls = BigInteger.valueOf(newRolls.length / level.getNumberOfDice());
		BigInteger newNumberOfDiceSetRolls = oldNumberOfDiceSetRolls.add(additionalDiceSetRolls);
		level.setNumberOfDiceSetRolls(newNumberOfDiceSetRolls);
		
		int highest = Arrays.stream(newRolls).max().getAsInt();
		level.setHighestAvailableDifficulty(highest);
	}
	
	private void updateDifficulty(Difficulty difficulty) {
		int maxPossibleNumberOfSuccesses = difficulty.getSkilllevel().getNumberOfDice();
		if (maxPossibleNumberOfSuccesses == 0)
			return;
		
		int numberOfNewDieSetRolls = newRolls.length / maxPossibleNumberOfSuccesses;
		
		// successCounters[x] == j means we rolled j successes against difficulty x
		int[] successCounters = new int[maxPossibleNumberOfSuccesses + 1];
		for (int partitionIndex = 0; partitionIndex < numberOfNewDieSetRolls; partitionIndex++) {
			int sum = 0;
			for (int i = 0; i < maxPossibleNumberOfSuccesses; i++) {
				if (newRolls[partitionIndex * maxPossibleNumberOfSuccesses + i] > difficulty.getDifficulty())
					sum++;
			}
			successCounters[sum]++;
		}
		
		for (int i = 0; i < successCounters.length; i++) {
			BigInteger oldCounter = difficulty.getSuccessCounter(i);
			BigInteger newCounter = oldCounter.add(BigInteger.valueOf(successCounters[i]));
			difficulty.setSuccessCounter(i, newCounter);
		}
	}
}
