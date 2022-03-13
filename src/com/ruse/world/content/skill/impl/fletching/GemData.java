package com.ruse.world.content.skill.impl.fletching;

import com.ruse.model.Animation;

/**
 * Created by brandon on 4/19/2017.
 */
public enum GemData {
    OPAL(1609, 45, 12, 2, 11, new Animation(886)),
    PEARL(411, 46, 12, 3, 41, new Animation(886)),
    SAPPHIRE(1607, 9189, 12, 4, 56, new Animation(888)),
    EMERALD(1605, 9190, 12, 6, 58, new Animation(889)),
    RUBY(1603, 9191, 12, 7, 63, new Animation(892)),
    DIAMOND(1601, 9192, 12, 8, 65, new Animation(886)),
    DRAGONSTONE(1615, 9193, 12, 9, 71, new Animation(885)),
    ONYX(6573, 9194, 12, 10, 73, new Animation(885))
    ;

    private int gem, outcome, output, xp, levelReq;
    private Animation animation;

    private GemData(int gem, int outcome, int output, int xp, int levelReq, Animation animation) {
        this.gem = gem;
        this.outcome = outcome;
        this.output = output;
        this.xp = xp;
        this.levelReq = levelReq;
        this.animation = animation;
    }

    public int getGem() {
        return gem;
    }

    public int getOutcome() {
        return outcome;
    }

    public int getOutput() { return output; }

    public int getXp() {
        return xp;
    }

    public int getLevelReq() {
        return levelReq;
    }

    public Animation getAnimation() {
        return animation;
    }

    public static GemData forGem(int id) {
        for (GemData gem : GemData.values()) {
            if (gem.getGem() == id) {
                return gem;
            }
        }
        return null;
    }
}


