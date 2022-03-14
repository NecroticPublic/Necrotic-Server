package com.ruse.engine.task.impl;


import com.ruse.engine.task.Task;
import com.ruse.model.CombatIcon;
import com.ruse.model.Graphic;
import com.ruse.model.Hit;
import com.ruse.model.Hitmask;
import com.ruse.model.Locations.Location;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

/**
 * Barrows
 * @author Gabriel Hannason
 */
public class CeilingCollapseTask extends Task {

	public CeilingCollapseTask(Player player) {
		super(9, player, false);
		this.player = player;
	}

	private Player player;

	@Override
	public void execute() {
		if(player == null || !player.isRegistered() || player.getLocation() != Location.BARROWS && player.getLocation() != Location.KRAKEN || player.getLocation() != Location.ZULRAH || player.getLocation() == Location.BARROWS && player.getPosition().getY() < 8000) {
			player.getPacketSender().sendCameraNeutrality();
			stop();
			return;
		}
		player.performGraphic(new Graphic(60));
		player.getPacketSender().sendMessage("Some rocks fall from the ceiling and hit you.");
		player.forceChat("Ouch!");
		player.dealDamage(new Hit(30 + Misc.getRandom(20), Hitmask.RED, CombatIcon.BLOCK));
	}
}
