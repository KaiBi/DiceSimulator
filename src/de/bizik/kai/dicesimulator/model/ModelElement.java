package de.bizik.kai.dicesimulator.model;

import java.util.Collection;


public interface ModelElement {

	public void accept(ModelVisitor visitor);
	public Collection<? extends ModelElement> getChildren();
	
}
