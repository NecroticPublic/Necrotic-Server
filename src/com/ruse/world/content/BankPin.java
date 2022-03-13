package com.ruse.world.content;

import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

/**
 * bank-pin
 * @author Gabriel Hannason
 * NOTE: This was taken & redone from my PI base
 */
public class BankPin {

	public static void deletePin(Player player) {
		if(player.getBankPinAttributes().hasBankPin() && !player.getBankPinAttributes().hasEnteredBankPin()) {
			player.getPacketSender().sendMessage("Please enter your pin before doing that.");
			BankPin.init(player, false);
			return;
		}
		player.getBankPinAttributes().setHasBankPin(false).setHasEnteredBankPin(false).setInvalidAttempts(0).setLastAttempt(System.currentTimeMillis());
		for(int i = 0; i < player.getBankPinAttributes().getBankPin().length; i++) {
			player.getBankPinAttributes().getBankPin()[i] = -1;
			player.getBankPinAttributes().getEnteredBankPin()[i] = -1;
		}
		player.getPacketSender().sendMessage("Your bank pin was deleted.").sendInterfaceRemoval();
	}

	public static void init(Player player, boolean openBankAfter) {
		if(player.getBankPinAttributes().getInvalidAttempts() == 3) {
			if(System.currentTimeMillis() - player.getBankPinAttributes().getLastAttempt() < 400000) {
				player.getPacketSender().sendMessage("You must wait "+(int)((400 - (System.currentTimeMillis() - player.getBankPinAttributes().getLastAttempt()) * 0.001))+" seconds before attempting to enter your bank-pin again.");
				return;
			} else
				player.getBankPinAttributes().setInvalidAttempts(0);
			player.getPacketSender().sendInterfaceRemoval();
		}
		player.setOpenBank(openBankAfter);
		randomizeNumbers(player);
		player.getPacketSender().sendString(15313, "First click the FIRST digit");
		player.getPacketSender().sendString(14923, "");
		player.getPacketSender().sendString(14913, "?");
		player.getPacketSender().sendString(14914, "?");
		player.getPacketSender().sendString(14915, "?");
		player.getPacketSender().sendString(14916, "?");
		sendPins(player);
		player.getPacketSender().sendInterface(7424);
		for(int i = 0; i < player.getBankPinAttributes().getEnteredBankPin().length; i++)
			player.getBankPinAttributes().getEnteredBankPin()[i] = -1;
	}

	public static void clickedButton(Player player, int button) {
		sendPins(player);
		if(player.getBankPinAttributes().getEnteredBankPin()[0] == -1) {
			player.getPacketSender().sendString(15313, "Now click the SECOND digit");
			player.getPacketSender().sendString(14913, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getBankPinAttributes().getEnteredBankPin()[0] = player.getBankPinAttributes().getBankPins()[i];
		} else if(player.getBankPinAttributes().getEnteredBankPin()[1] == -1) {
			player.getPacketSender().sendString(15313, "Now click the THIRD digit");
			player.getPacketSender().sendString(14914, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getBankPinAttributes().getEnteredBankPin()[1] = player.getBankPinAttributes().getBankPins()[i];
		} else if(player.getBankPinAttributes().getEnteredBankPin()[2] == -1) {
			player.getPacketSender().sendString(15313, "Now click the FINAL digit");
			player.getPacketSender().sendString(14915, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getBankPinAttributes().getEnteredBankPin()[2] = player.getBankPinAttributes().getBankPins()[i];
		} else if(player.getBankPinAttributes().getEnteredBankPin()[3] == -1) {
			player.getPacketSender().sendString(14916, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getBankPinAttributes().getEnteredBankPin()[3] = player.getBankPinAttributes().getBankPins()[i];
			if(!player.getBankPinAttributes().hasBankPin()) {
				player.getBankPinAttributes().setHasBankPin(true).setHasEnteredBankPin(true).setBankPin(player.getBankPinAttributes().getEnteredBankPin());
				player.getPacketSender().sendMessage("You've created a bank-pin. Your digit is "+player.getBankPinAttributes().getEnteredBankPin()[0]+"-"+player.getBankPinAttributes().getEnteredBankPin()[1]+"-"+player.getBankPinAttributes().getEnteredBankPin()[2]+"-"+player.getBankPinAttributes().getEnteredBankPin()[3]+". Please write it down.");
				player.getPacketSender().sendInterfaceRemoval();
				return;
			}
			for(int i = 0; i < player.getBankPinAttributes().getEnteredBankPin().length; i++) {
				if(player.getBankPinAttributes().getEnteredBankPin()[i] != player.getBankPinAttributes().getBankPin()[i]) {
					player.getPacketSender().sendInterfaceRemoval();
					int invalidAttempts = player.getBankPinAttributes().getInvalidAttempts() + 1;
					if(invalidAttempts >= 3)
						player.getBankPinAttributes().setLastAttempt(System.currentTimeMillis());
					player.getBankPinAttributes().setInvalidAttempts(invalidAttempts);
					player.getPacketSender().sendMessage("Invalid bank-pin entered entered.");
					return;
				}
			}
			player.getBankPinAttributes().setInvalidAttempts(0).setHasEnteredBankPin(true);
			if(player.openBank()) {
				player.getBank(0).open();
			} else {
				player.getPacketSender().sendInterfaceRemoval();
			}
		}
		randomizeNumbers(player);
	}

	private static void sendPins(Player player) {
		for(int i = 0; i < player.getBankPinAttributes().getBankPins().length; i++)
			player.getPacketSender().sendString(stringIds[i], ""+player.getBankPinAttributes().getBankPins()[i]);
	}

	public static void randomizeNumbers(Player player) {
		int i = Misc.getRandom(5);
		switch(i) {
		case 0:
			player.getBankPinAttributes().getBankPins()[0] = 1;
			player.getBankPinAttributes().getBankPins()[1] = 7;
			player.getBankPinAttributes().getBankPins()[2] = 0;
			player.getBankPinAttributes().getBankPins()[3] = 8;
			player.getBankPinAttributes().getBankPins()[4] = 4;
			player.getBankPinAttributes().getBankPins()[5] = 6;
			player.getBankPinAttributes().getBankPins()[6] = 5;
			player.getBankPinAttributes().getBankPins()[7] = 9;
			player.getBankPinAttributes().getBankPins()[8] = 3;
			player.getBankPinAttributes().getBankPins()[9] = 2;
			break;

		case 1:
			player.getBankPinAttributes().getBankPins()[0] = 5;
			player.getBankPinAttributes().getBankPins()[1] = 4;
			player.getBankPinAttributes().getBankPins()[2] = 3;
			player.getBankPinAttributes().getBankPins()[3] = 7;
			player.getBankPinAttributes().getBankPins()[4] = 8;
			player.getBankPinAttributes().getBankPins()[5] = 6;
			player.getBankPinAttributes().getBankPins()[6] = 9;
			player.getBankPinAttributes().getBankPins()[7] = 2;
			player.getBankPinAttributes().getBankPins()[8] = 1;
			player.getBankPinAttributes().getBankPins()[9] = 0;
			break;

		case 2:
			player.getBankPinAttributes().getBankPins()[0] = 4;
			player.getBankPinAttributes().getBankPins()[1] = 7;
			player.getBankPinAttributes().getBankPins()[2] = 6;
			player.getBankPinAttributes().getBankPins()[3] = 5;
			player.getBankPinAttributes().getBankPins()[4] = 2;
			player.getBankPinAttributes().getBankPins()[5] = 3;
			player.getBankPinAttributes().getBankPins()[6] = 1;
			player.getBankPinAttributes().getBankPins()[7] = 8;
			player.getBankPinAttributes().getBankPins()[8] = 9;
			player.getBankPinAttributes().getBankPins()[9] = 0;
			break;

		case 3:
			player.getBankPinAttributes().getBankPins()[0] = 9;
			player.getBankPinAttributes().getBankPins()[1] = 4;
			player.getBankPinAttributes().getBankPins()[2] = 2;
			player.getBankPinAttributes().getBankPins()[3] = 7;
			player.getBankPinAttributes().getBankPins()[4] = 8;
			player.getBankPinAttributes().getBankPins()[5] = 6;
			player.getBankPinAttributes().getBankPins()[6] = 0;
			player.getBankPinAttributes().getBankPins()[7] = 3;
			player.getBankPinAttributes().getBankPins()[8] = 1;
			player.getBankPinAttributes().getBankPins()[9] = 5;
			break;

		case 4:
			player.getBankPinAttributes().getBankPins()[0] = 8;
			player.getBankPinAttributes().getBankPins()[1] = 7;
			player.getBankPinAttributes().getBankPins()[2] = 6;
			player.getBankPinAttributes().getBankPins()[3] = 2;
			player.getBankPinAttributes().getBankPins()[4] = 5;
			player.getBankPinAttributes().getBankPins()[5] = 4;
			player.getBankPinAttributes().getBankPins()[6] = 1;
			player.getBankPinAttributes().getBankPins()[7] = 0;
			player.getBankPinAttributes().getBankPins()[8] = 3;
			player.getBankPinAttributes().getBankPins()[9] = 9;
			break;
		}
		sendPins(player);
	}

	private static final int stringIds[] = { 
		14883, 14884, 14885, 14886, 
		14887, 14888, 14889, 14890, 
		14891, 14892
	};

	private static final int actionButtons[] = { 
		14873, 14874, 14875, 14876, 
		14877, 14878, 14879, 14880, 
		14881, 14882
	};

	public static class BankPinAttributes {
		public BankPinAttributes() {}

		private boolean hasBankPin;
		private boolean hasEnteredBankPin;
		private int[] bankPin = new int[4];
		private int[] enteredBankPin = new int[4];
		private int bankPins[] = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9
		};
		private int invalidAttempts;
		private long lastAttempt;

		public boolean hasBankPin() {
			return hasBankPin;
		}

		public BankPinAttributes setHasBankPin(boolean hasBankPin) {
			this.hasBankPin = hasBankPin;
			return this;
		}

		public boolean hasEnteredBankPin() {
			return hasEnteredBankPin;
		}

		public BankPinAttributes setHasEnteredBankPin(boolean hasEnteredBankPin) {
			this.hasEnteredBankPin = hasEnteredBankPin;
			return this;
		}

		public int[] getBankPin() {
			return bankPin;
		}
		
		public BankPinAttributes setBankPin(int[] bankPin) {
			this.bankPin = bankPin;
			return this;
		}

		public int[] getEnteredBankPin() {
			return enteredBankPin;
		}

		public int[] getBankPins() {
			return bankPins;
		}

		public int getInvalidAttempts() {
			return invalidAttempts;
		}

		public BankPinAttributes setInvalidAttempts(int invalidAttempts) {
			this.invalidAttempts = invalidAttempts;
			return this;
		}

		public long getLastAttempt() {
			return lastAttempt;
		}
		
		public BankPinAttributes setLastAttempt(long lastAttempt) {
			this.lastAttempt = lastAttempt;
			return this;
		}
	}
}
