package de.bizik.kai.dicesimulator.model;

public interface ModelVisitor {

	public void visit(Model model);
	public void visit(DieKind dieKind);
	public void visit(Skilllevel level);
	public void visit(Difficulty difficulty);
	
}
