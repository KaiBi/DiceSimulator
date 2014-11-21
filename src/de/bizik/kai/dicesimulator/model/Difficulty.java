package de.bizik.kai.dicesimulator.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public class Difficulty implements ModelElement {

	@XmlIDREF
	private final Skilllevel skilllevel;
	private final int difficulty;
	private final List<BigInteger> successCounters;
	
	public Difficulty(Skilllevel level, int difficulty) {
		this.skilllevel = level;
		this.difficulty = difficulty;
		int maxPossibleNumberOfSuccesses = level != null? level.getNumberOfDice() : 0;
		this.successCounters = new ArrayList<BigInteger>(maxPossibleNumberOfSuccesses + 1);
		for (int i = 0; i < maxPossibleNumberOfSuccesses + 2; i++) {
			successCounters.add(BigInteger.ZERO);
		}
	}

	public Difficulty() {
		this(null, 0);
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
	public List<BigInteger> getSuccessCounters() {
		return Collections.unmodifiableList(successCounters);
	}
	
	public BigInteger getSuccessCounter(int difficulty) {
		return successCounters.get(difficulty);
	}
	
	public void setSuccessCounter(int difficulty, BigInteger value) {
		successCounters.set(difficulty, value);
	}

	public Skilllevel getSkilllevel() {
		return skilllevel;
	}
	
	@Override
	public Collection<ModelElement> getChildren() {
		return new ArrayList<ModelElement>();
	}

	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

}
