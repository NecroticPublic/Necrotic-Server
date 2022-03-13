package com.ruse.world.content.skill.impl.fletching;

import com.ruse.model.Animation;

/**
 * Created by brandon on 4/19/2017.
 */
public enum BoltData {
   // OPAL(877, 45, 879, 2, 11, 10),
   // PEARL(411, 46, 3, 6, 41, 10),
    SAPPHIRE(9142, 9189, 9337, 4, 56, 10),
    EMERALD(9142, 9190, 9338, 6, 58, 10),
    RUBY(9143, 9191, 9339, 7, 63, 10),
    DIAMOND(9143, 9192, 9340, 8, 65, 10),
    DRAGONSTONE(9144, 9193, 9341, 9, 71, 10),
    ONYX(9144, 9194, 9342, 10, 73, 10 )
    ;

    private int bolt, tip, outcome, levelReq, amount, xp;

    private BoltData(int bolt, int tip, int outcome, int xp, int levelReq, int amount) {
        this.bolt = bolt;
        this.tip = tip;
        this.outcome = outcome;
        this.xp = xp;
        this.levelReq = levelReq;
        this.amount = amount;
    }


    public int getBolt() {
        return bolt;
    }

    public int getTip() {
        return tip;
    }

    public int getOutcome() {
        return outcome;
    }

    public int getLevelReq() {
        return levelReq;
    }

    public int getAmount() {
        return amount;
    }

    public int getXp() {
        return xp;
    }

    public static BoltData forBolts(int id) {
        for (BoltData bolt : BoltData.values()) {
            if (bolt.getBolt() == id) {
                return bolt;
            }
        }
        return null;
    }

    public static BoltData forTip(int id) {
        for (BoltData bolt : BoltData.values()) {
            if (bolt.getTip() == id) {
                return bolt;
            }
        }
        return null;
    }
}


