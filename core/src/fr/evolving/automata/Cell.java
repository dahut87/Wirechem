package fr.evolving.automata;

import java.io.Serializable;

public class Cell implements Serializable,Cloneable {
	public boolean Fiber;
	public boolean Copper;
	public Transmuter Transmuter;
	public boolean Locked;
	public boolean Free;

	public transient int Fiber_state;
	public transient int Copper_calc;
	public transient int Transmuter_calc;
	public transient int Transmuter_movex;
	public transient int Transmuter_movey;

	public Cell() {
		this.Fiber = false;
		this.Copper = false;
		this.Locked = false;
		this.Free = false;
		this.Transmuter = null;
		this.Transmuter_calc = 0;
		this.Transmuter_movex = 0;
		this.Transmuter_movey = 0;
	}
	
	public Object clone() {
		Cell result = new Cell();
		result.Locked=this.Locked;
		result.Free=this.Free;
		result.Copper=this.Copper;
		result.Fiber=this.Fiber;
		if (this.Transmuter!=null)
			result.Transmuter=(Transmuter)this.Transmuter.clone();
		return result;
	}
}
