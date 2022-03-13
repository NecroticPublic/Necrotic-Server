package com.ruse.model;

import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.world.World;
import com.ruse.world.content.minigames.impl.Barrows;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles a custom region instance for a player
 * @author Gabriel
 */
public class RegionInstance {

	public enum RegionInstanceType {
		BARROWS,
		THE_SIX,
		GRAVEYARD,
		FIGHT_CAVE,
		WARRIORS_GUILD,
		NOMAD,
		RECIPE_FOR_DISASTER,
		CONSTRUCTION_HOUSE,
		CONSTRUCTION_DUNGEON,
		TRIO,
		KRAKEN,
		ZULRAH;
	}

	private Player owner;
	private RegionInstanceType type;
	private CopyOnWriteArrayList<NPC> npcsList;
	private CopyOnWriteArrayList<Player> playersList;
	
	public RegionInstance(Player p, RegionInstanceType type) {
		this.owner = p;
		this.type = type;
		this.npcsList = new CopyOnWriteArrayList<NPC>();
		if(type == RegionInstanceType.CONSTRUCTION_HOUSE || type == RegionInstanceType.THE_SIX) {
			this.playersList = new CopyOnWriteArrayList<Player>();
		}
	}

	public void destruct() {
		for(NPC n : npcsList) {
			if(n != null && n.getConstitution() > 0 && World.getNpcs().get(n.getIndex()) != null && !n.isDying()) {
				
				if(type == RegionInstanceType.WARRIORS_GUILD) {
					
					if(n.getId() >= 4278 && n.getId() <= 4284) {
						owner.getMinigameAttributes().getWarriorsGuildAttributes().setSpawnedArmour(false);
					}
					
				} else if(type == RegionInstanceType.BARROWS) {

					if(n.getId() >= 2024 && n.getId() <= 2034) {
						Barrows.killBarrowsNpc(owner, n, false);
					}
					
				}
				
				World.deregister(n);
				//System.out.println("Is this running?");
			}
		}
		npcsList.clear();
		owner.setRegionInstance(null);
		//System.out.println("Is this ru222nning?");
	}

	public void add(Character c) {
		if(type == RegionInstanceType.CONSTRUCTION_HOUSE) {
			if(c.isPlayer()) {
				playersList.add((Player)c);
			} else if(c.isNpc()) {
				npcsList.add((NPC)c);
			}

			if(c.getRegionInstance() == null || c.getRegionInstance() != this) {
				c.setRegionInstance(this);
			}
		}
	}

	public void remove(Character c) {
		if(type == RegionInstanceType.CONSTRUCTION_HOUSE) {
			if(c.isPlayer()) {
				playersList.remove((Player)c);
				if(owner == ((Player)c)) {
					destruct();
				}
			} else if(c.isNpc()) {
				npcsList.remove((NPC)c);
			}

			if(c.getRegionInstance() != null && c.getRegionInstance() == this) {
				c.setRegionInstance(null);
			}
		}
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public RegionInstanceType getType() {
		return type;
	}

	public CopyOnWriteArrayList<NPC> getNpcsList() {
		return npcsList;
	}

	public CopyOnWriteArrayList<Player> getPlayersList() {
		return playersList;
	}

	@Override
	public boolean equals(Object other) {
		return (RegionInstanceType)other == type;
	}
}
