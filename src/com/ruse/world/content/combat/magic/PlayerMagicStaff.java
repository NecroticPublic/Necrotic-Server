package com.ruse.world.content.combat.magic;

import com.ruse.model.Item;
import com.ruse.model.definitions.WeaponInterfaces.WeaponInterface;
import com.ruse.world.entity.impl.player.Player;

/**
 * A set of constants representing the staves that can be used in place of
 * runes.
 * 
 * @author lare96
 */
public enum PlayerMagicStaff {

    AIR(new int[] { 1381, 1397, 1405 }, new int[] { 556 }),
    WATER(new int[] { 1383, 1395, 1403 }, new int[] { 555 }),
    EARTH(new int[] { 1385, 1399, 1407 }, new int[] { 557 }),
    FIRE(new int[] { 1387, 1393, 1401 }, new int[] { 554 }),
    MUD(new int[] { 6562, 6563 }, new int[] { 555, 557 }),
    LAVA(new int[] { 3053, 3054 }, new int[] { 554, 557 }),
	OMNI(new int[] { 13642, 15835, 17293 }, new int[] { 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 9075 }),
	BLOOD_BURST(new int[] { 13634 }, new int[] { 562, 560, 565 }),
	ICE_BURST(new int[] { 13632 }, new int[] { 562, 560, 555 }),
	AHRIM(new int[] { 4710 }, new int[] { 556 });

    /** The staves that can be used in place of runes. */
    private int[] staves;
    /** The runes that the staves can be used for. */
    private int[] runes;
    
    public static int[] allstaves = { 1381, 1397, 1405, 1383, 1395, 1403, 1385, 1399, 1407, 1387, 1393, 1401, 6562, 6563, 3053, 3054, 554, 557, 13642, 15835, 17293, 13634, 13632 };

    /**
     * Create a new {@link PlayerMagicStaff}.
     * 
     * @param itemIds
     *            the staves that can be used in place of runes.
     * @param runeIds
     *            the runes that the staves can be used for.
     */
    private PlayerMagicStaff(int[] itemIds, int[] runeIds) {
        this.staves = itemIds;
        this.runes = runeIds;
    }

    /**
     * Suppress items in the argued array if any of the items match the runes
     * that are represented by the staff the argued player is wielding.
     * 
     * @param player
     *            the player to suppress runes for.
     * @param runesRequired
     *            the runes to suppress.
     * @return the new array of items with suppressed runes removed.
     */
    public static Item[] suppressRunes(Player player, Item[] runesRequired) {
        if (player.getWeapon() == WeaponInterface.STAFF) { //17293
            for (PlayerMagicStaff m : values()) {
                if (player.getEquipment().containsAny(m.staves)) {
                    for (int id : m.runes) {
                        for (int i = 0; i < runesRequired.length; i++) {
                            if (runesRequired[i] != null && runesRequired[i].getId() == id) {
                                runesRequired[i] = null;
                            }
                        }
                    }
                }
            }
            return runesRequired;
        }
        return runesRequired;
    }
}
