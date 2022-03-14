package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations.Location;
import com.ruse.model.Skill;
import com.ruse.world.content.skill.impl.summoning.SummoningData;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
 
public class SummoningRegenPlayerConstitutionTask extends Task {

	public static final int TIMER = 25; //15 seconds
	
	public SummoningRegenPlayerConstitutionTask(Player player) {
		super(TIMER, player, false);
		this.player = player;
	}
	
	private Player player;
	
	@Override
	public void execute() {
		if (player == null || !player.isRegistered()) {
			stop();
			return;
		}
		if (player.getConstitution() == 0 || player.isDying() || player.getLocation() == Location.DUEL_ARENA) {
			return;
		}
		if (player.getSummoning().getFamiliar() == null || player.getSummoning().getFamiliar().getSummonNpc() == null) {
			return;
		}
		if (!player.couldHeal()) {
			return;
		}
		if (!player.getLocation().isSummoningAllowed()) {
			return;
		}
		
		int npcId = player.getSummoning().getFamiliar().getSummonNpc().getId();
		
		if (SummoningData.regenerationFamililar(npcId) && summoningRestorationData.forId(npcId) != null && player.getSummoning().getFamiliar().getSummonNpc().isRegistered() && player.getSummoning().getFamiliar().getSummonNpc().getConstitution() > 0 && !player.getSummoning().getFamiliar().getSummonNpc().isDying()) {
			summoningRestorationData restoreData = summoningRestorationData.forId(npcId);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.CONSTITUTION);
			double restore = summoningRestorationData.forId(npcId).getHealPercentage();
			double heal = maxHp * restore;
			NPC npc = player.getSummoning().getFamiliar().getSummonNpc();
			
			player.performAnimation(restoreData.getPlayerAnim());
			player.performGraphic(restoreData.getPlayerGfx()); //new Graphic(1313)
			npc.performAnimation(restoreData.getNpcAnim());
			npc.performGraphic(restoreData.getNpcGfx());
			
			player.heal((int) Math.round(heal));
			
			//System.out.println("maxHp = "+maxHp+". restore = "+restore+". heal = "+heal+". restored: "+(int) Math.round(heal));
		} /*else {
			System.out.println("SummoningData.regenerationFamililar(npcId) == " + SummoningData.regenerationFamililar(npcId));
			boolean b = summoningRestorationData.forId(npcId) != null;
			System.out.println("summoningRestorationData.forId(npcId) != null == " + b);
			System.out.println("player.getSummoning().getFamiliar().getSummonNpc().isRegistered() == " + player.getSummoning().getFamiliar().getSummonNpc().isRegistered());
			System.out.println("player.getSummoning().getFamiliar().getSummonNpc().getConstitution() > 0 == "+(player.getSummoning().getFamiliar().getSummonNpc().getConstitution() > 0));
			System.out.println("!player.getSummoning().getFamiliar().getSummonNpc().isDying() == "+!player.getSummoning().getFamiliar().getSummonNpc().isDying());

			
		}*/
		
		
		
	}
	
	public enum summoningRestorationData {
		BUNYIP(6813, 0.02, new Animation(7741), new Graphic(-1), new Animation(-1), new Graphic(1507)),
		UNICORN(6822, 0.03, new Animation(8267), new Graphic(1356), new Animation(-1), new Graphic(1313));
		
		private summoningRestorationData(int npcId, double restorePercent, Animation npcAnim, Graphic npcGfx, Animation playerAnim, Graphic playerGfx) {
			this.npcId = npcId;
			this.restorePercent = restorePercent;
			this.npcAnim = npcAnim;
			this.npcGfx = npcGfx;
			this.playerAnim = playerAnim;
			this.playerGfx = playerGfx;
		}

		private final int npcId;
		private final double restorePercent;
		private final Animation npcAnim;
		private final Graphic npcGfx;
		private final Animation playerAnim;
		private final Graphic playerGfx;

		public static summoningRestorationData forId(int npcId) {
			for (summoningRestorationData familiar : summoningRestorationData.values()) {
				if (familiar.getNpcId() == npcId) {
					return familiar;
				}
			}
			return null;
		}

		public int getNpcId() {
			return npcId;
		}
		public double getHealPercentage() {
			return restorePercent;
		}
		public Animation getNpcAnim() {
			return npcAnim;
		}
		public Graphic getNpcGfx() {
			return npcGfx;
		}
		public Animation getPlayerAnim() {
			return playerAnim;
		}
		public Graphic getPlayerGfx() {
			return playerGfx;
		}
	}
	
}
