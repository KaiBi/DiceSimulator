package de.bizik.kai.dicesimulator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public class DieKind implements ModelElement {

	public static final int MAX_LEVEL = 50;

	@XmlID
	private final String xmlUID;
	private final int numberOfSides;
	private final List<Skilllevel> skilllevels = new ArrayList<Skilllevel>();

	@SuppressWarnings("unused")	// needed by JAXB for unmarshalling
	private DieKind() {
		this(6);
	}
	
	public DieKind(int numberOfSides) {
		this.numberOfSides = numberOfSides;
		for (int i = 0; i < MAX_LEVEL + 1; i++)
			skilllevels.add(new Skilllevel(this, i));
		xmlUID = this.getClass().getCanonicalName() + numberOfSides;
	}
	
	public Skilllevel getLevel(int level) {
		return skilllevels.get(level);
	}
	
	public int getNumberOfDieSides() {
		return numberOfSides;
	}
	
	@Override
	public Collection<ModelElement> getChildren() {
		return Collections.unmodifiableCollection(skilllevels);
	}

	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

}
