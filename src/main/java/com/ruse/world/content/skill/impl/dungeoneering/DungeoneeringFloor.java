package com.ruse.world.content.skill.impl.dungeoneering;
import com.ruse.model.GameObject;
import com.ruse.model.Position;
import com.ruse.world.entity.impl.npc.NPC;

/**
 * I couldn't be arsed to put all npc spawns in the enum.
 * @author Gabriel Hannason
 */
public enum DungeoneeringFloor {

	FIRST_FLOOR(new Position(2451, 4935), new Position(2448, 4939), new GameObject[] {new GameObject(-1, new Position(2461, 4931))} , new NPC[][]{{new NPC(491, new Position(2440, 4958)), new NPC(688, new Position(2443, 4954)), new NPC(13, new Position(2444, 4958)), new NPC(5664, new Position(2460, 4965)), new NPC(90, new Position(2460, 4961)), new NPC(90, new Position(2462, 4965)), new NPC(1624, new Position(2474, 4958)), new NPC(174, new Position(2477, 4954)), new NPC(2060, new Position(2473, 4940)), new NPC(688, new Position(2471, 4937)), new NPC(688, new Position(2474, 4943))}, {new NPC(124, new Position(2441, 4958)), new NPC(108, new Position(2441, 4954)), new NPC(688, new Position(2443, 4956)), new NPC(111, new Position(2460, 4965)), new NPC(52, new Position(2457, 4961)), new NPC(1643, new Position(2477, 4954)), new NPC(13, new Position(2477, 4958)), new NPC(13, new Position(2474, 4958)), new NPC(8549, new Position(2473, 4940))}, {new NPC(13, new Position(2441, 4958)), new NPC(13, new Position(2441, 4955)), new NPC(13, new Position(2443, 4954)), new NPC(1643, new Position(2445, 4956)), new NPC(13, new Position(2443, 4958)), new NPC(13, new Position(2445, 4958)), new NPC(13, new Position(2445, 4954)), new NPC(2019, new Position(2461, 4965)), new NPC(27, new Position(2458, 4966)), new NPC(27, new Position(2458, 4961)), new NPC(27, new Position(2458, 4967)), new NPC(5361, new Position(2476, 4957)), new NPC(3495, new Position(2475, 4954)), new NPC(491, new Position(2472, 4957)), new NPC(1382, new Position(2473, 4940))}, {new NPC(8162, new Position(2441, 4954)), new NPC(8162, new Position(2441, 4957)), new NPC(90, new Position(2443, 4958)), new NPC(90, new Position(2443, 4954)), new NPC(90, new Position(2440, 4956)), new NPC(2896, new Position(2458, 4967)), new NPC(2896, new Position(2462, 4967)), new NPC(2896, new Position(2462, 4960)), new NPC(2896, new Position(2457, 4960)), new NPC(2896, new Position(2459, 4964)), new NPC(1880, new Position(2456, 4964)), new NPC(110, new Position(2472, 4955)), new NPC(688, new Position(2477, 4954)), new NPC(84, new Position(2477, 4957)), new NPC(9939, new Position(2472, 4940))}});

	DungeoneeringFloor(Position entrance, Position smuggler, GameObject[] objects, NPC[][] npcs) {
		this.entrance = entrance;
		this.smugglerPosition = smuggler;
		this.objects = objects;
		this.npcs = npcs;
	}

	private Position entrance, smugglerPosition;
	private GameObject[] objects;
	private NPC[][] npcs;
	
	public Position getEntrance() {
		return entrance;
	}

	public Position getSmugglerPosition() {
		return smugglerPosition;
	}

	public GameObject[] getObjects() {
		return objects;
	}
	
	public NPC[][] getNpcs() {
		return npcs;
	}

	public static DungeoneeringFloor forId(int id) {
		for(DungeoneeringFloor floors : DungeoneeringFloor.values()) {
			if(floors != null && floors.ordinal() == id) {
				return floors;
			}
		}
		return null;
	}
}