package com.ruse.world.content.grandexchange;

/**
 * A Grand Exchange slot's attributes
 * @author Gabriel Hannason
 *
 */
public class GrandExchangeSlot {

	private GrandExchangeSlotState state = GrandExchangeSlotState.EMPTY;
	private GrandExchangeOffer offer;
	
	public GrandExchangeSlotState getState() {
		return state;
	}
	
	public void setState(GrandExchangeSlotState state) {
		this.state = state;
	}
	
	public GrandExchangeOffer getOffer() {
		return offer;
	}
	
	public void setOffer(GrandExchangeOffer offer) {
		this.offer = offer;
	}
}
