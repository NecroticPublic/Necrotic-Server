package com.ruse.world.content;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Position;
import com.ruse.model.RegionInstance;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.combat.strategy.impl.ZulrahLogic;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Zulrah {
	
	public static void enter(Player player) {
		ZulrahInstance zInstance = new ZulrahInstance(player);
		
		int rand = Misc.randomMinusOne(ZulrahLogic.move.length);
		
		NPC zulrah = new NPC(ZulrahLogic.phase[Misc.randomMinusOne(ZulrahLogic.phase.length)], new Position(ZulrahLogic.move[rand].getX(), ZulrahLogic.move[rand].getY(), player.getPosition().getZ()));
		zInstance.getNpcsList().add(zulrah);
		World.register(zulrah);

		player.setRegionInstance(zInstance);
		
		TaskManager.submit(new Task(1, player, false) {
			@Override
			protected void execute() {
				if (player == null || player.getConstitution() <= 0 || player.isDying() || zulrah == null || zulrah.getConstitution() <= 0 || zulrah.isDying()) {
					return;
				}
				zulrah.getCombatBuilder().attack(player);
				stop();
			}
		});
	}



	public static class ZulrahInstance extends RegionInstance {

		public ZulrahInstance(Player p) {
			super(p, RegionInstanceType.ZULRAH);

		}

		private boolean zulrahSlain;

		public boolean zulrahSlain() {
			return zulrahSlain;
		}

		public void setZulrahSlain(boolean killed) {
			this.zulrahSlain = killed;
		}
	}
	
}
