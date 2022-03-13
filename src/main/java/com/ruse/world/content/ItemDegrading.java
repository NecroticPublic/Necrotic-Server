package com.ruse.world.content;

import java.util.ArrayList;

import com.ruse.model.Flag;
import com.ruse.model.Item;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class ItemDegrading {
	
	public static int maxRecoilCharges = 40, maxForgingCharges = 140, maxZulrahCharges = 16383, maxAncientCharges = 819;

	public static boolean handleItemDegrading(Player p, DegradingItem d) {
		int equipId = p.getEquipment().getItems()[d.equipSlot].getId();
		if(equipId == d.nonDeg || equipId == d.deg) {
			int maxCharges = d.degradingCharges;
			int currentCharges = getAndIncrementCharge(p, d, false);
			boolean degradeCompletely = currentCharges >= maxCharges; //ONLY WORKS IF REVERSE COUNTING
			if(equipId == d.deg && !degradeCompletely && d.dust) {
				return true;
			}
			if (!d.dust && currentCharges == 0) {
				getAndIncrementCharge(p, d, true);
				p.getPacketSender().sendMessage("Your "+ItemDefinition.forId(equipId).getName()+" has fully depleted!");
				return true;
			}
			if (equipId == d.deg && !degradeCompletely && !d.dust) {
				return true;
			}

			degradeCompletely = degradeCompletely && equipId == d.deg;
			p.getEquipment().setItem(d.equipSlot, new Item(degradeCompletely ? - 1 : d.deg)).refreshItems();
			p.getUpdateFlag().flag(Flag.APPEARANCE);
			getAndIncrementCharge(p, d, true);
			String ext = !degradeCompletely ? "degraded slightly" : "turned into dust";
			p.getPacketSender().sendMessage("Your "+ItemDefinition.forId(equipId).getName().replace(" (deg)", "")+" has "+ext+"!");
			Misc.updateGearBonuses(p);
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("incomplete-switch")
	public static int getAndIncrementCharge(Player p, DegradingItem d, boolean reset) {
		switch(d) {
			case STATIUS_FULL_HELM:
			case STATIUS_PLATEBODY:
			case STATIUS_PLATELEGS:
			case STATIUS_WARHAMMER:

			case VESTAS_CHAINBODY:
			case VESTAS_PLATESKIRT:
			case VESTAS_LONGSWORD:
			case VESTAS_SPEAR:

			case ZURIELS_HOOD:
			case ZURIELS_ROBE_TOP:
			case ZURIELS_ROBE_BOTTOM:
			case ZURIELS_STAFF:

			case MORRIGANS_COIF:
			case MORRIGANS_LEATHER_BODY:
			case MORRIGANS_LEATHER_CHAPS:
				// index2 incase we add to the enum above the ancient armour.
				int index2 = d.ordinal() - 2;
				if(reset) {
					return p.getAncientArmourCharges()[index2] = 0;
				} else {
					return p.getAncientArmourCharges()[index2]++;
				}
		case BRAWLING_GLOVES_COOKING:
		case BRAWLING_GLOVES_FIREMAKING:
		case BRAWLING_GLOVES_FISHING:
		case BRAWLING_GLOVES_HUNTER:
		case BRAWLING_GLOVES_MINING:
		case BRAWLING_GLOVES_PRAYER:
		case BRAWLING_GLOVES_SMITHING:
		case BRAWLING_GLOVES_THIEVING:
		case BRAWLING_GLOVES_WOODCUTTING:
			int index = d.ordinal() - 15;
			if(reset) {
				return p.getBrawlerChargers()[index] = 0;
			} else {
				return p.getBrawlerChargers()[index]++;
			}
		case RING_OF_RECOIL:
			if(reset) {
				return p.setRecoilCharges(0);
			} else {
				return p.setRecoilCharges(p.getRecoilCharges() + 1);
			}
		case RING_OF_FORGING:
			if(reset) {
				return p.setForgingCharges(0);
			} else {
                return p.setForgingCharges(p.getForgingCharges() + 1);
            }
            case TOXIC_BLOWPIPE:
                if(reset) {
                    return p.setBlowpipeCharges(0);
                } else {
                    return p.setBlowpipeCharges(p.getBlowpipeCharges() - 1);
                }
		}
		return d.degradingCharges;
	}

	/*
	 * The enum holding all degradeable equipment
	 */
	public enum DegradingItem {

		/*
		 * Recoil
		 */
		RING_OF_RECOIL(2550, 2550, Equipment.RING_SLOT, maxRecoilCharges, true, false),
		RING_OF_FORGING(2568, 2568, Equipment.RING_SLOT, maxForgingCharges, true, false),
		
		 // Statius's equipment
		 
		STATIUS_FULL_HELM(13896, 13898, Equipment.HEAD_SLOT, maxAncientCharges, true, true),
		STATIUS_PLATEBODY(13884, 13886, Equipment.BODY_SLOT, maxAncientCharges, true, true),
		STATIUS_PLATELEGS(13890, 13892, Equipment.LEG_SLOT, maxAncientCharges, true, true),
		STATIUS_WARHAMMER(13902, 13904, Equipment.WEAPON_SLOT, maxAncientCharges, true, true),

		
		 // Vesta's equipment
		 
		VESTAS_CHAINBODY(13887, 13889, Equipment.BODY_SLOT, maxAncientCharges, true, true),
		VESTAS_PLATESKIRT(13893, 13895, Equipment.LEG_SLOT, maxAncientCharges, true, true),
		VESTAS_LONGSWORD(13899, 13901, Equipment.WEAPON_SLOT, maxAncientCharges, true, true),
		VESTAS_SPEAR(13905, 13907, Equipment.WEAPON_SLOT, maxAncientCharges, true, true),

		
		  // Zuriel's equipment
		 
		ZURIELS_HOOD(13864, 13866, Equipment.HEAD_SLOT, maxAncientCharges, true, true),
		ZURIELS_ROBE_TOP(13858, 13860, Equipment.BODY_SLOT, maxAncientCharges, true, true),
		ZURIELS_ROBE_BOTTOM(13861, 13863, Equipment.LEG_SLOT, maxAncientCharges, true, true),
		ZURIELS_STAFF(13867, 13869, Equipment.WEAPON_SLOT, maxAncientCharges, true, true),

		
		 // Morrigan's equipment
		 
		MORRIGANS_COIF(13876, 13878, Equipment.HEAD_SLOT, maxAncientCharges, true, true),
		MORRIGANS_LEATHER_BODY(13870, 13872, Equipment.BODY_SLOT, maxAncientCharges, true, true),
		MORRIGANS_LEATHER_CHAPS(13873, 13875, Equipment.LEG_SLOT, maxAncientCharges, true, true),

		// ancient armours total: 15
		 
		/*
		 * Brawling gloves
		 */
		BRAWLING_GLOVES_SMITHING(13855, 13855, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_PRAYER(13848, 13848, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_COOKING(13857, 13857, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_FISHING(13856, 13856, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_THIEVING(13854, 13854, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_HUNTER(13853, 13853, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_MINING(13852, 13852, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_FIREMAKING(13851, 13851, Equipment.HANDS_SLOT, 600, true, false),
		BRAWLING_GLOVES_WOODCUTTING(13850, 13850, Equipment.HANDS_SLOT, 600, true, false),

		/*
		 * Toxic equipment
		 */

		TOXIC_BLOWPIPE(12926, 12926, Equipment.WEAPON_SLOT, maxZulrahCharges, false, false);

		DegradingItem(int nonDeg, int deg, int equipSlot, int degradingCharges, boolean dust, boolean degradeWhenHit) {
			this.nonDeg = nonDeg;
			this.deg = deg;
			this.equipSlot = equipSlot;
			this.degradingCharges = degradingCharges;
			this.dust = dust;
			this.degradeWhenHit = degradeWhenHit;
		}

		private final int nonDeg, deg, equipSlot, degradingCharges;
		private final boolean dust, degradeWhenHit;
		
		public static DegradingItem forNonDeg(int item) {
			for(DegradingItem d : DegradingItem.values()) {
				if(d.nonDeg == item) {
					return d;
				}
			}
			return null;
		}
		public int getDeg() {
			return deg;
		}

		public boolean degradeWhenHit() {
			return degradeWhenHit;
		}

		public int getNonDeg() {
			return nonDeg;
		}

		public int getSlot() {
			return equipSlot;
		}
		
		public final static ArrayList<DegradingItem> getWeapons() {
			ArrayList<DegradingItem> values = new ArrayList<DegradingItem>();
			for (DegradingItem d : DegradingItem.values()) {
				if (d.getSlot() == Equipment.WEAPON_SLOT) {
					//System.out.println("Added "+d.toString().toLowerCase()+" to weapons array.");
					values.add(d);
				}
			}
			return values;
		}
		
		public final static ArrayList<DegradingItem> getNonWeapons() {
			ArrayList<DegradingItem> values = new ArrayList<DegradingItem>();
			for (DegradingItem d : DegradingItem.values()) {
				if (d.getSlot() != Equipment.WEAPON_SLOT) {
					//System.out.println("Added "+d.toString().toLowerCase()+" to non-weapons array.");
					values.add(d);
				}
			}
			return values;
		}
		

	}
}
