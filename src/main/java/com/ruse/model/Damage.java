package com.ruse.model;

public class Damage {
	
	public Damage(Hit... hits) {
		if (hits.length > 2 || hits.length <= 0)
			throw new IllegalArgumentException("Hit array length cannot be less than 1 and cannot be greater than 2!");
		this.hits = hits;
	}

	private final Hit[] hits;
	
	public Hit[] getHits() {
		return hits;
	}
	
	private boolean shown;
	
	public boolean hasBeenShown() {
		return shown;
	}
	
	public void setShown(boolean b) {
		this.shown = b;
	}
}