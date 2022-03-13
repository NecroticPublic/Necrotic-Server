package com.ruse.world.content.grandexchange;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ruse.model.Item;

/**
 * A Grand Exchange offer
 * @author Gabriel Hannason
 */
public class GrandExchangeOffer extends Item {

	private String owner;
	private int index;
	private int pricePerItem;
	private int box;
	private OfferType type;
	
	private int totalCost;
	private int amountFinished;
	private int coinsCollect;
	private int itemCollect;
	private int failedAttempts;
	
	private GrandExchangeSlotState updateState;
	
	public GrandExchangeOffer(int item, int quantity, String owner, int index, int pricePerItem, int box, OfferType type) {
		super(item, quantity);
		this.owner = owner;
		this.index = index;
		this.pricePerItem = pricePerItem;
		this.type = type;
		this.box = box;
		this.totalCost = GrandExchange.calculateTotalCost(pricePerItem, quantity);
	}
	
	public GrandExchangeOffer(int item, int quantity, String owner, int index, int pricePerItem, int box, int amountFinished, int coinsCollect, int itemCollect, int failedAttempts, OfferType type, int updateStateOrdinal) {
		super(item, quantity);
		this.owner = owner;
		this.index = index;
		this.pricePerItem = pricePerItem;
		this.box = box;
		this.amountFinished = amountFinished;
		this.coinsCollect = coinsCollect;
		this.itemCollect = itemCollect;
		this.failedAttempts = failedAttempts;
		this.updateState = updateStateOrdinal == -1 ? null : GrandExchangeSlotState.forId(updateStateOrdinal);
		this.type = type;
		this.totalCost = GrandExchange.calculateTotalCost(pricePerItem, quantity);
	}	
	
	public String getOwner() {
		return owner;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getPricePerItem() {
		return this.pricePerItem;
	}
	
	public int getBox() {
		return box;
	}
	
	public OfferType getType() {
		return this.type;
	}
	
	public int getTotalCost() {
		return totalCost;
	}
	
	public int getPercent() {
		float amt = (float)this.amountFinished;
		float total = (float)this.getAmount();
		return (int) ((amt * 100)/total);
	}
	
	public int getAmountFinished() {
		return amountFinished;
	}
	
	public void incrementAmountFinished(int amountFinished) {
		this.amountFinished += amountFinished;
	}
	
	public void setAmountFinished(int amountFinished) {
		this.amountFinished = amountFinished;
	}
	
	public int getCoinsCollect() {
		return coinsCollect;
	}
	
	public void setCoinsCollect(int coinsCollect) {
		this.coinsCollect = coinsCollect;
	}
	
	public void incrementCoinsCollect(int coinsCollect) {
		this.coinsCollect += coinsCollect;
	}
	
	public int getItemCollect() {
		return itemCollect;
	}
	
	public void setItemCollect(int itemCollect) {
		this.itemCollect = itemCollect;
	}
	
	public void incrementItemCollect(int itemCollect) {
		this.itemCollect += itemCollect;
	}
	
	public int getFailAttempts() {
		return failedAttempts;
	}
	
	public void incrementFailAttempts() {
		this.failedAttempts ++;
	}
	
	public void setFailAttempts(int failAttempts) {
		this.failedAttempts = failAttempts;
	}
	
	public void setUpdateState(GrandExchangeSlotState updateState) {
		this.updateState = updateState;
	}
	
	public GrandExchangeSlotState getUpdateState() {
		return this.updateState;
	}
	
	public int getUpdateStateOrdinal() {
		return updateState == null ? -1 : updateState.ordinal();
	}
	
	public enum OfferType {
		SELLING,
		BUYING;
	}
	
	public void save(DataOutputStream out) throws IOException {
		out.writeInt(getId());
		out.writeInt(getAmount());
		out.writeUTF(getOwner());
		out.writeInt(getIndex());
		out.writeInt(getPricePerItem());
		out.writeInt(getBox());
		out.writeInt(getAmountFinished());
		out.writeInt(getCoinsCollect());
		out.writeInt(getItemCollect());
		out.writeInt(getFailAttempts());
		out.writeInt(getUpdateStateOrdinal());
		out.writeUTF(getType().name());
	}
}
