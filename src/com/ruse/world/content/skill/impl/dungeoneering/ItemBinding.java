package com.ruse.world.content.skill.impl.dungeoneering;

import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class ItemBinding {

	private static final int[][] BINDABLE_ITEMS = { 
		//Novite Items
		{15753, 16207}, {16273, 15936}, {16339, 16262}, {16383, 16024}, {16405, 16174}, {16647, 16196}, 
		{16669, 15925}, {16691, 15914}, {16713, 16080}, {16889, 16127}, {16935, 16035}, {17019, 16116}, 
		{17239, 16013}, {17341, 15808}, 
		//Bathus Items
		{15755, 16208}, {16275, 15937}, {16341, 16263}, {16385, 16025}, {16407, 16175}, {16649, 16197}, 
		{16671, 15926}, {16693, 15915}, {16715, 16081}, {16891, 16128}, {16937, 16036}, {17021, 16117}, 
		{17241, 16014}, {17343, 15809},
		//Marmaros Items
		{15757, 16209}, {16277, 15938}, {16343, 16264}, {16387, 16026}, {16409, 16176}, {16651, 16198}, 
		{16673, 15927}, {16695, 15916}, {16717, 16082}, {16893, 16129}, {16939, 16037}, {17023, 16118}, 
		{17243, 16015}, {17345, 15810},
		//Kratonite Items
		{15759, 16210}, {16279, 15939}, {16345, 16265}, {16389, 16027}, {16411, 16177}, {16653, 16199}, 
		{16675, 15928}, {16697, 15917}, {16719, 16083}, {16895, 16130}, {16941, 16038}, {17025, 16119}, 
		{17245, 16016}, {17347, 15811},
		//Fractite Items
		{15761, 16211}, {16281, 15940}, {16347, 16266}, {16391, 16028}, {16413, 16178}, {16655, 16200}, 
		{16677, 15929}, {16699, 15918}, {16721, 16084}, {16897, 16131}, {16943, 16039}, {17027, 16120}, 
		{17247, 16017}, {17349, 15812},
		//Zephyrium Items
		{15763, 16212}, {16283, 15941}, {16349, 16267}, {16393, 16029}, {16415, 16179}, {16657, 16201}, 
		{16679, 15930}, {16701, 15919}, {16723, 16085}, {16899, 16132}, {16945, 16040}, {17029, 16121}, 
		{17249, 16018}, {17351, 15813},
		//Argonite Items
		{15765, 16213}, {16285, 15942}, {16351, 16268}, {16395, 16030}, {16417, 16180}, {16659, 16202}, 
		{16681, 15931}, {16703, 15920}, {16725, 16086}, {16901, 16133}, {16947, 16041}, {17031, 16122}, 
		{17251, 16019}, {17353, 15814},
		//Katagon Items
		{15767, 16214}, {16287, 15943}, {16353, 16269}, {16397, 16031}, {16419, 16181}, {16661, 16203}, 
		{16683, 15932}, {16705, 15921}, {16727, 16087}, {16903, 16134}, {16949, 16042}, {17033, 16123}, 
		{17253, 16020}, {17355, 15815},
		//Gorgonite Items
		{15769, 16215}, {16289, 15944}, {16355, 16270}, {16399, 16032}, {16421, 16182}, {16663, 16204}, 
		{16685, 15933}, {16707, 15922}, {16729, 16088}, {16905, 16135}, {16951, 16043}, {17035, 16124}, 
		{17255, 16021}, {17357, 15816},
		//Promethium Items
		{15771, 16216}, {16291, 15945}, {16357, 16271}, {16401, 16033}, {16423, 16183}, {16665, 16205}, 
		{16687, 15934}, {16709, 15923}, {16731, 16089}, {16907, 16136}, {16953, 16044}, {17037, 16125}, 
		{17257, 16022}, {17359, 15817},
		//Primal Items
		{15773, 16217}, {16293, 15946}, {16359, 16272}, {16403, 16034}, {16425, 16184}, {16667, 16206}, 
		{16689, 15935}, {16711, 15924}, {16733, 16090}, {16909, 16137}, {16955, 16045}, {17039, 16126}, 
		{17259, 16023}, {17361, 15818}, 
		//Customs
		//Celestial armour(magic armour).
		{16755, 15902}, {17237, 15847}, {16865, 15807}, {16931, 15796}, {17171, 16195},
		//sagittarian armour(range armour)
		{17061, 16056}, {17193, 16078}, {17339, 16067}, {17317, 16012}, {17215, 16115},
		{17293, 15835}, //doomcore staff

	};

	public static int getRandomBindableItem() {
		int index = Misc.getRandom(BINDABLE_ITEMS.length - 1);
	/*	if(ItemDefinition.forId(BINDABLE_ITEMS[index][0]).getName().toLowerCase().contains("body") || ItemDefinition.forId(BINDABLE_ITEMS[index][0]).getName().toLowerCase().contains("legs") || ItemDefinition.forId(BINDABLE_ITEMS[index][0]).getName().toLowerCase().contains("skirt")) {
			index = index + 1 >= BINDABLE_ITEMS[0].length ? index-1 : index + 1;
		} */
		return BINDABLE_ITEMS[index][0];
	}

	public static boolean isBindable(int item) {
		for(int i = 0; i < BINDABLE_ITEMS.length; i++) {
			if(BINDABLE_ITEMS[i][0] == item)
				return true;
		}
		return false;
	}

	public static boolean isBoundItem(int item) {
		for(int i = 0; i < BINDABLE_ITEMS.length; i++) {
			if(BINDABLE_ITEMS[i][1] == item)
				return true;
		}
		return false;
	}

	public static int getItem(int currentId) {
		for(int i = 0; i < BINDABLE_ITEMS.length; i++) {
			if(BINDABLE_ITEMS[i][0] == currentId)
				return BINDABLE_ITEMS[i][1];
		}
		return -1;
	}

	public static void unbindItem(Player p, int item) {
		if(Dungeoneering.doingDungeoneering(p)) {
			for(int i = 0; i  < p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems().length; i++) {
				if(p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()[i] == item) {
					p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()[i] = 0;
					p.getPacketSender().sendMessage("You unbind the item..");
					break;
				}
			}
		}
	}

	public static void bindItem(Player p, int item) {
		if(Dungeoneering.doingDungeoneering(p)) {
			if(!isBindable(item))
				return;
			int amountBound = 0;
			for(int i = 0; i  < p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems().length; i++) {
				if(p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()[i] != 0)
					amountBound++;
			}
			if(amountBound >= 5) {
				p.getPacketSender().sendMessage("You have already bound four items, which is the maximum.");
				return;
			} else if(amountBound == 4 && p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) < 95) {
				p.getPacketSender().sendMessage("You need a Dungeoneering level of at least 95 to have 5 bound items.");
				return;
			} else if(amountBound == 3 && p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) < 80) {
				p.getPacketSender().sendMessage("You need a Dungeoneering level of at least 80 to have 4 bound items.");
				return;
			} else if(amountBound == 2 && p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) < 60) {
				p.getPacketSender().sendMessage("You need a Dungeoneering level of at least 60 to have 3 bound items.");
				return;
			} else if(amountBound == 1 && p.getSkillManager().getCurrentLevel(Skill.DUNGEONEERING) < 40) {
				p.getPacketSender().sendMessage("You need a Dungeoneering level of at least 40 to have 2 bound items.");
				return;
			}
			int bind = getItem(item);
			int index = -1;
			for(int i = 0; i < p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems().length; i++) {
				if(p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()[i] != 0)
					continue;
				index = i;
				break;
			}
			if(bind != -1 && index != -1) {
				p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()[index] = bind;
				p.getInventory().delete(item, 1).add(bind, 1);
				p.getPacketSender().sendMessage("You bind the item..");
			}
		}
	}

	public static void onDungeonEntrance(Player p) {
		for(int i = 0; i < p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems().length; i++) {
			if(p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()[i] != 0) {
				p.getInventory().add(p.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()[i], 1);
			}
		}
	}
}
