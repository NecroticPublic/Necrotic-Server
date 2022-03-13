package com.ruse.world.content.skill.impl.herblore;

import java.util.HashMap;

import com.ruse.model.container.impl.Equipment;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.effect.CombatPoisonEffect.PoisonType;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.player.Player;

public class WeaponPoison {

	/**
	 * Represents a weapon that can be poisoned. Stores the initial weapon item
	 * id, the type of poison used on the weapon and the new poisoned weapon
	 * that will be obtained.
	 * 
	 */
	private enum Weapon {
		/**
		 * Dragon dagger.
		 */
		DRAGON_DAGGER(1215, new int[][] { { 5940, 5698 }, { 5937, 5680 } }),

		/**
		 * Rune dagger.
		 */
		RUNE_DAGGER(1213, new int[][] { { 5940, 5696 }, { 5937, 5678 } }),

		/**
		 * Adamant dagger.
		 */
		ADAMANT_DAGGER(1211, new int[][] { { 5940, 5694 }, { 5937, 5676 } }),

		/**
		 * Mithril dagger.
		 */
		MITHRIL_DAGGER(1209, new int[][] { { 5940, 5692 }, { 5937, 5674 } }),

		/**
		 * Black dagger.
		 */
		BLACK_DAGGER(1217, new int[][] { { 5940, 5700 }, { 5937, 5682 } }),

		/**
		 * Steel dagger.
		 */
		STEEL_DAGGER(1207, new int[][] { { 5940, 5690 }, { 5937, 5672 } }),

		/**
		 * Iron dagger.
		 */
		IRON_DAGGER(1203, new int[][] { { 5940, 5686 }, { 5937, 5668 } }),

		/**
		 * Bronze dagger.
		 */
		BRONZE_DAGGER(1205, new int[][] { { 5940, 5688 }, { 5937, 5670 } });

		/**
		 * Creates the weapon.
		 * 
		 * @param itemId
		 *            The weapon item id.
		 * @param newItemId
		 *            The poisoned weapon item id.
		 */
		private Weapon(int itemId, int[][] newItemId) {
			this.itemId = itemId;
			this.newItemId = newItemId;
		}

		/**
		 * Gets the item id.
		 * 
		 * @return the itemId
		 */
		public int getItemId() {
			return itemId;
		}

		/**
		 * The weapon item id.
		 */
		private int itemId;

		/**
		 * The poisoned weapon item id.
		 */
		private int[][] newItemId;

		/**
		 * Represents a map for the weapon item ids.
		 */
		public static HashMap<Integer, Weapon> weapon = new HashMap<Integer, Weapon>();

		/**
		 * Gets the weapon id by the item.
		 * 
		 * @param id
		 *            The item id.
		 * @return returns null if itemId is not a weapon.
		 */
		@SuppressWarnings("unused")
		public static Weapon forId(int id) {
			return weapon.get(id);
		}

		/**
		 * @return the newItemId
		 */
		public int[][] getNewItemId() {
			return newItemId;
		}

		/**
		 * Populates a map for the weapons.
		 */
		static {
			for (Weapon w : Weapon.values())

				weapon.put(w.getItemId(), w);

		}
	}
	
	/**
	 * Starts the weapon poison event for each individual weapon item from the
	 * enumeration <code>Weapon</code>.
	 * 
	 * @param player
	 *            The Player player.
	 * @param itemUse
	 *            The first item use.
	 * @param useWith
	 *            The second item use.
	 */
	public static void execute(final Player player, int itemUse, int useWith) {
		final Weapon weapon = Weapon.weapon.get(useWith);
		if (weapon != null) {
			for (int element[] : weapon.getNewItemId())
				if (itemUse == element[0] && player.getInventory().contains(itemUse)) {
					player.getPacketSender().sendMessage("You poison your weapon..");
					player.getInventory().delete(element[0], 1);
					player.getInventory().delete(weapon.getItemId(), 1);
					player.getInventory().add(229, 1);
					player.getInventory().add(element[1], 1);
				}
		}
	}

	/**
	 * Checks if poison should be applied for a target.
	 * @param p			The player who is going to apply poison onto the target.
	 * @param target	The target who is going to be poisoned.
	 */
	public static void handleWeaponPoison(Player p, Character target) {
		int plrWeapon = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		for(Weapon w : Weapon.weapon.values()) {
			if(w != null) {
				int random = 0;
				if(w.getNewItemId()[0][1] == plrWeapon) //Player has p++
					random = 5;
				else if(w.getNewItemId()[1][1] == plrWeapon) //Player has p+
					random = 10;
				if(random > 0) {
					if(Misc.getRandom(random) == 1)
						CombatFactory.poisonEntity(target, random == 5 ? PoisonType.EXTRA : PoisonType.MILD);
					break;
				}
			}
		}
	}
}
