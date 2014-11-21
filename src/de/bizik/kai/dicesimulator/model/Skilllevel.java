package de.bizik.kai.dicesimulator.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public class Skilllevel implements ModelElement {

	private static final int INITIALDIFFICULTIES = 30;

	@XmlID
	private final String xmlUID;
	@XmlIDREF
	private final DieKind dieKind;
	private final int numberOfDies;
	private final List<Difficulty> difficulties = new ArrayList<Difficulty>(INITIALDIFFICULTIES);
	private BigInteger numberOfDiceSetRolls = BigInteger.ZERO;
	
	public Skilllevel(DieKind dieKind, int numberOfDies) {
		if (dieKind != null)
			xmlUID = this.getClass().getCanonicalName() + dieKind.getNumberOfDieSides() + "::" + numberOfDies;
		else
			xmlUID = this.getClass().getCanonicalName() + "null::" + numberOfDies;
		this.dieKind = dieKind;
		this.numberOfDies = numberOfDies;
		for (int i = 0; i < INITIALDIFFICULTIES + 1; i++)
			difficulties.add(new Difficulty(this, i));
	}
	
	@SuppressWarnings("unused") // needed by JAXB for unmarshalling
	private Skilllevel() {
		this(null, 3);
	}

	public List<Difficulty> getDifficulties() {
		return Collections.unmodifiableList(difficulties);
	}
	
	public BigInteger getNumberOfDiceSetRolls() {
		return numberOfDiceSetRolls;
	}
	
	public void setNumberOfDiceSetRolls(BigInteger numberOfDiceSetRolls) {
		this.numberOfDiceSetRolls = numberOfDiceSetRolls;
	}
	
	public int getNumberOfDice() {
		return numberOfDies;
	}
	
	public DieKind getDieKind() {
		return dieKind;
	}
	
	@Override
	public Collection<? extends ModelElement> getChildren() {
		return getDifficulties();
	}

	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

	public Difficulty getDifficulty(int difficulty) {
		return difficulties.get(difficulty);
	}
	
	public void setHighestAvailableDifficulty(int difficulty) {
		int currentHighest = getHighestAvailableDifficulty();
		for (int i = currentHighest; i < difficulty + 1; i++)
			difficulties.add(new Difficulty(this, i));		
	}
	
	public int getHighestAvailableDifficulty() {
		return difficulties.size() - 1;
	}
}
