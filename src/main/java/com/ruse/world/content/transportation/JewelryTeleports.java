package com.ruse.world.content.transportation;

import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;

public enum JewelryTeleports {

	GLORY(new Position[]{new Position(3088, 3506, 0), new Position(2918, 3176, 0), new Position(3105, 3251, 0), new Position(3292, 3176, 0)}, 
			new int[]{1712, 1710, 1708, 1706, 1704},
			88, 48),
	COMBAT(new Position[]{new Position(3088, 3506, 0), new Position(2918, 3176, 0), new Position(3105, 3251, 0), new Position(3292, 3176, 0)},
			new int[]{11118, 11120, 11122, 11124, 11126},
			88, 48),
	DUELING(new Position[]{new Position(3370, 3269, 0), new Position(2441, 3090, 0)},
			new int[]{2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566},
			196, 196),
	GAMES(new Position[]{new Position(2898, 3562, 0), new Position(2552, 3563, 0)},
			new int[]{3853, 3855, 3857, 3859, 3861, 3863, 3865, 3867}, 
			197, 197),
	DIGSITE(new Position[]{new Position(3351, 3346, 0)},
			new int[]{11194, 11193, 11192, 11191, 11190},
			198, 198),
	;
	
	private Position[] pos;
	private int[] itemId;
	private int dialogueId, dialogueActionId;
	
	private JewelryTeleports(Position[] pos, int[] itemId, int dialogueId, int dialogueActionId) {
		this.pos = pos;
		this.itemId = itemId;
		this.dialogueId = dialogueId;
		this.dialogueActionId = dialogueActionId;
	}
	
	//YOUR (JEWL) has (WHATEVER) uses left
	
	public Position[] getPos() {
		return this.pos;
	}
	
	public Position getPositionIndex(int i) {
		return this.pos[i];
	}
	
	public int[] getItemId() {
		return this.itemId;
	}
	
	public int getDialogueId() {
		return this.dialogueId;
	}
	
	public int getDialogueActionId() {
		return this.dialogueActionId;
	}
	
	public int getItemIdIndex(int i) {
		//System.out.println(i+" = index, length = "+this.itemId.length);
		if (i > this.itemId.length-1) {
			return 592; //ash
		} else {
			return this.itemId[i];
		}
	}
	
	public static void handleDialogue(Player player, int itemId, int jeweleryIndex) {
		for (int i = 0; i < JewelryTeleports.values()[jeweleryIndex].getItemId().length; i++) {
			if (itemId == JewelryTeleports.values()[jeweleryIndex].getItemIdIndex(i)) {
				player.setSelectedSkillingItem(itemId);
				player.setSelectedSkillingItemTwo(JewelryTeleports.values()[jeweleryIndex].getItemIdIndex(i+1));
				player.setStrippedJewelryName(stripName(itemId));
				player.setDialogueActionId(JewelryTeleports.values()[jeweleryIndex].getDialogueActionId());
				DialogueManager.start(player, JewelryTeleports.values()[jeweleryIndex].getDialogueId());
				break;
			}
		}
	}
	
	public static String stripName(int itemId) {
		String jewelName = ItemDefinition.forId(itemId).getName().toLowerCase();
		jewelName = jewelName.substring(0, jewelName.length()-3);
		jewelName = jewelName.replaceAll(" ", "");
		jewelName = jewelName.replaceAll("necklace", "");
		jewelName = jewelName.replaceAll("of", "");
		jewelName = jewelName.replaceAll("amulet", "");
		jewelName = jewelName.replaceAll("bracelet", "");
		jewelName = jewelName.replaceAll("pendant", "");
		jewelName = jewelName.replaceAll("ring", "");
		jewelName = jewelName.replaceAll("1", "");
		jewelName = jewelName.replaceAll("2", "");
		jewelName = jewelName.replaceAll("3", "");
		jewelName = jewelName.replaceAll("4", "");
		jewelName = jewelName.replaceAll("5", "");
		jewelName = jewelName.replaceAll("6", "");
		jewelName = jewelName.replaceAll("7", "");
		jewelName = jewelName.replaceAll("8", "");
		return jewelName;
	}
	
	public static int jewelIndex(String enchantedName) {
		enchantedName = enchantedName.toUpperCase();
		switch (enchantedName) {
		case "GLORY":
			return 0;
		case "COMBAT":
			return 1;
		case "DUELLING":
			return 2;
		case "GAMES":
			return 3;
		case "DIGSITE":
			return 4;
		}
		return -1;
	}
	
	
}
