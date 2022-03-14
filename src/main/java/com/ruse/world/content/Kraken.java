package com.ruse.world.content;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.CeilingCollapseTask;
import com.ruse.model.Position;
import com.ruse.model.RegionInstance;
import com.ruse.world.World;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Kraken {

	private static enum WhirpoolData {

		SMALL_POOL_1(2895, new Position(3679, 9884)),
		SMALL_POOL_2(2900, new Position(3676, 9884)),
		SMALL_POOL_3(2902, new Position(3676, 9891)),
		SMALL_POOL_4(2903, new Position(3679, 9891)),
		BIG_POOL(2891, new Position(3677, 9887));

		WhirpoolData(int npc, Position spawn) {
			this.npc = npc;
			this.spawn = spawn;
		}

		int npc;
		Position spawn;

		static WhirpoolData getPool(int npc) {
			for(WhirpoolData d : WhirpoolData.values()) {
				if(d.npc == npc) {
					return d;
				}
			}
			return null;
		}
	}

	public static void enter(Player player) {
		KrakenInstance kInstance = new KrakenInstance(player);

		for(WhirpoolData d : WhirpoolData.values()) {
			NPC whirpool = new NPC(d.npc, new Position(d.spawn.getX(), d.spawn.getY(), player.getPosition().getZ()));
			kInstance.getNpcsList().add(whirpool);
			World.register(whirpool);
		}

		player.setRegionInstance(kInstance);
	}

	public static boolean isWhirpool(NPC n) {
		int npc = n.getId();
		return npc == 2895 || npc == 2900 || npc == 2902 || npc == 2903 || npc == 2891;
	}

	public static void attackPool(Player player, NPC npc) {
		WhirpoolData d = WhirpoolData.getPool(npc.getId());
		if(d != null) {
			if(((KrakenInstance)player.getRegionInstance()).disturbedPool(d.ordinal()))
				return;
			player.getRegionInstance().getNpcsList().remove(npc);
			((KrakenInstance)player.getRegionInstance()).setDisturbedPool(d.ordinal(), true);
			World.deregister(npc);

			final boolean kraken = d == WhirpoolData.BIG_POOL;
			TaskManager.submit(new Task(1, player, false) {
				@Override
				protected void execute() {
					int npcToSpawn = kraken ? 2007 : 3580;
					Position positionToSpawn = kraken ? new Position(3677, 9887, player.getPosition().getZ()) : new Position(d.spawn.getX() + 2, d.spawn.getY() + 1, player.getPosition().getZ());
					NPC spawn = new NPC(npcToSpawn, positionToSpawn);
					player.getRegionInstance().getNpcsList().add(spawn);
					World.register(spawn);
					spawn.getCombatBuilder().attack(player);
					stop();
					
					if(kraken) {
						player.getPacketSender().sendCameraShake(3, 2, 3, 2);
						player.getPacketSender().sendMessage("The cave begins to collapse...");
						TaskManager.submit(new CeilingCollapseTask(player));
					}
				}
			});
		}
	}

	public static class KrakenInstance extends RegionInstance {

		public KrakenInstance(Player p) {
			super(p, RegionInstanceType.KRAKEN);

		}

		private boolean[] disturbedPool = new boolean[5];

		public boolean disturbedPool(int index) {
			return disturbedPool[index];
		}

		public void setDisturbedPool(int index, boolean disturbed) {
			this.disturbedPool[index] = disturbed;
		}
	}
}
