package com.ruse.world.content.skill.impl.crafting;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.impl.EnterAmountOfGemsToCut;
import com.ruse.util.Misc;
import com.ruse.world.content.Achievements;
import com.ruse.world.content.Achievements.AchievementData;
import com.ruse.world.entity.impl.player.Player;

public class Gems {

	enum GEM_DATA {
		
		OPAL(1625, 1609, 8, 15, new Animation(886), -1),
		JADE(1627, 1611, 13, 20, new Animation(886), -1),
		RED_TOPAZ(1629, 1613, 16, 25, new Animation(887), -1),
		SAPPHIRE(1623, 1607, 20, 50, new Animation(888), 1727),
		EMERALD(1621, 1605, 27, 68, new Animation(889), 1729),
		RUBY(1619, 1603, 34, 85, new Animation(892), 1725),
		DIAMOND(1617, 1601, 43, 108, new Animation(886), 1731),
		DRAGONSTONE(1631, 1615, 55, 138, new Animation(885), 1704),
		ONYX(6571, 6573, 67, 168, new Animation(885), 6585);
		
		GEM_DATA(int uncutGem, int cutGem, int levelReq, int xpReward, Animation animation, int amuletInt) {
			this.uncutGem = uncutGem;
			this.cutGem = cutGem;
			this.levelReq = levelReq;
			this.xpReward = xpReward;
			this.animation = animation;
			this.amuletInt = amuletInt;
		}
		
		private int uncutGem, cutGem, levelReq, xpReward, amuletInt;
		private Animation animation;

		public int getUncutGem() {
			return uncutGem;
		}

		public int getCutGem() {
			return cutGem;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXpReward() {
			return xpReward;
		}
		
		public int getAmuletInt() {
			return amuletInt;
		}

		public Animation getAnimation() {
			return animation;
		}
		
		public static GEM_DATA forUncutGem(int uncutGem) {
			for(GEM_DATA data : GEM_DATA.values()) {
				if(data.getUncutGem() == uncutGem)
					return data;
			}
			return null;
		}
	}
	
	public static void selectionInterface(Player player, int gem) {
		player.getPacketSender().sendInterfaceRemoval();
		GEM_DATA data = GEM_DATA.forUncutGem(gem);
		if(data == null)
			return;
		if (player.getSkillManager().getMaxLevel(Skill.CRAFTING) < data.getLevelReq()) {
			player.getPacketSender().sendMessage("You need a Crafting level of atleast "+ data.getLevelReq() +" to craft this gem.");
			return;
		}
		player.setSelectedSkillingItem(gem);
		player.setInputHandling(new EnterAmountOfGemsToCut());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(gem).getName()).sendInterfaceModel(1746, gem, 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to craft?");
	}

	public static void cutGem(final Player player, final int amount, final int uncutGem) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		final GEM_DATA data = GEM_DATA.forUncutGem(uncutGem);
		if(data == null)
			return;
		player.setCurrentTask(new Task(2, player, true) {
			int amountCut = 0;
			@Override
			public void execute() {
				if(!player.getInventory().contains(uncutGem)) {
					stop();
					return;
				}
				player.performAnimation(data.getAnimation());
				player.getInventory().delete(uncutGem, 1);
				if(player.getSkillManager().skillCape(Skill.CRAFTING) && (data.getAmuletInt() != -1) && (Misc.getRandom(10) == 1)) {
					player.getPacketSender().sendMessage("Your cape instantly turns your gem into an amulet!");
					player.getInventory().add(data.getAmuletInt(), 1);
				} else {
					player.getInventory().add(data.getCutGem(), 1);
				}
				if(data == GEM_DATA.DIAMOND) {
					Achievements.doProgress(player, AchievementData.CRAFT_1000_DIAMOND_GEMS);
				} else if(data == GEM_DATA.ONYX) {
					Achievements.finishAchievement(player, AchievementData.CUT_AN_ONYX_STONE);
				}
				player.getSkillManager().addExperience(Skill.CRAFTING, data.getXpReward());
				amountCut++;
				if(amountCut >= amount)
					stop();
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}

}
