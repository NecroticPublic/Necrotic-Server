package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.impl.fletching.BowData;
import com.ruse.world.content.skill.impl.fletching.Fletching;
import com.ruse.world.entity.impl.player.Player;
/**
 * Handles fletching X options
 * @author Gabriel Hannason
 */
public class EnterAmountToFletch extends EnterAmount {
	
	public EnterAmountToFletch() {
		
	}
	private int button;
	
	public EnterAmountToFletch setButton(int button) {
		this.button = button;
		return this;
	}

	@Override
	public void handleAmount(Player player, int amount) {
		final int log = player.getSelectedSkillingItem();
		if(log > 0) {
			if(button == SHAFTS_BUTTON && log == 1511) {
				Fletching.fletchBow(player, SHAFTS_ID, amount);
			} else {
				BowData shortBow = BowData.forLog(log, false);
				BowData longBow = BowData.forLog(log, true);
				if(shortBow == null || longBow == null)
					return;
				switch(button) {
				case LONGBOW_BUTTON:
				case LONGBOW_BUTTON_2:
					Fletching.fletchBow(player, shortBow.getBowID(), amount);
					break;
				case SHORTBOW_BUTTON:
				case SHORTBOW_BUTTON_2:
					Fletching.fletchBow(player, longBow.getBowID(), amount);
					break;
				}
			}
		}
	}
	
	private static final int SHAFTS_BUTTON = 8894, SHAFTS_ID = 52;
	private static final int LONGBOW_BUTTON = 8886, LONGBOW_BUTTON_2 = 8871;
	private static final int SHORTBOW_BUTTON = 8890, SHORTBOW_BUTTON_2 = 8875;
}
