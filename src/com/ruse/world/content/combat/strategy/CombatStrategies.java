 package com.ruse.world.content.combat.strategy;

import java.util.HashMap;
import java.util.Map;

import com.ruse.world.content.combat.strategy.impl.Aviansie;
import com.ruse.world.content.combat.strategy.impl.BandosAvatar;
import com.ruse.world.content.combat.strategy.impl.Brandon;
import com.ruse.world.content.combat.strategy.impl.Callisto;
import com.ruse.world.content.combat.strategy.impl.ChaosElemental;
import com.ruse.world.content.combat.strategy.impl.CorporealBeast;
import com.ruse.world.content.combat.strategy.impl.Crimson;
import com.ruse.world.content.combat.strategy.impl.DagannothSupreme;
import com.ruse.world.content.combat.strategy.impl.DefaultMagicCombatStrategy;
import com.ruse.world.content.combat.strategy.impl.DefaultMeleeCombatStrategy;
import com.ruse.world.content.combat.strategy.impl.DefaultRangedCombatStrategy;
import com.ruse.world.content.combat.strategy.impl.Dragon;
import com.ruse.world.content.combat.strategy.impl.Geerin;
import com.ruse.world.content.combat.strategy.impl.Glacor;
import com.ruse.world.content.combat.strategy.impl.Graardor;
import com.ruse.world.content.combat.strategy.impl.Grimspike;
import com.ruse.world.content.combat.strategy.impl.Gritch;
import com.ruse.world.content.combat.strategy.impl.Growler;
import com.ruse.world.content.combat.strategy.impl.IceQueen;
import com.ruse.world.content.combat.strategy.impl.Jad;
import com.ruse.world.content.combat.strategy.impl.KalphiteQueen;
import com.ruse.world.content.combat.strategy.impl.Kilik;
import com.ruse.world.content.combat.strategy.impl.KreeArra;
import com.ruse.world.content.combat.strategy.impl.Kreeyath;
import com.ruse.world.content.combat.strategy.impl.Nex;
import com.ruse.world.content.combat.strategy.impl.Nomad;
import com.ruse.world.content.combat.strategy.impl.PlaneFreezer;
import com.ruse.world.content.combat.strategy.impl.Revenant;
import com.ruse.world.content.combat.strategy.impl.Scorpia;
import com.ruse.world.content.combat.strategy.impl.Spinolyp;
import com.ruse.world.content.combat.strategy.impl.Steelwill;
import com.ruse.world.content.combat.strategy.impl.TormentedDemon;
import com.ruse.world.content.combat.strategy.impl.Tsutsuroth;
import com.ruse.world.content.combat.strategy.impl.Venenatis;
import com.ruse.world.content.combat.strategy.impl.Vetion;
import com.ruse.world.content.combat.strategy.impl.WingmanSkree;
import com.ruse.world.content.combat.strategy.impl.Zilyana;
import com.ruse.world.content.combat.strategy.impl.ZulrahLogic;

public class CombatStrategies {

	private static final DefaultMeleeCombatStrategy defaultMeleeCombatStrategy = new DefaultMeleeCombatStrategy();
	private static final DefaultMagicCombatStrategy defaultMagicCombatStrategy = new DefaultMagicCombatStrategy();
	private static final DefaultRangedCombatStrategy defaultRangedCombatStrategy = new DefaultRangedCombatStrategy();
	private static final Map<Integer, CombatStrategy> STRATEGIES = new HashMap<Integer, CombatStrategy>();
	
	public static void init() {
		DefaultMagicCombatStrategy defaultMagicStrategy = new DefaultMagicCombatStrategy();
		STRATEGIES.put(13, defaultMagicStrategy);
		STRATEGIES.put(172, defaultMagicStrategy);
		STRATEGIES.put(174, defaultMagicStrategy);
		STRATEGIES.put(2025, defaultMagicStrategy);
		STRATEGIES.put(3495, defaultMagicStrategy);
		STRATEGIES.put(3496, defaultMagicStrategy);
		STRATEGIES.put(3491, defaultMagicStrategy);
		STRATEGIES.put(2882, defaultMagicStrategy);
		STRATEGIES.put(13451, defaultMagicStrategy);
		STRATEGIES.put(13452, defaultMagicStrategy);
		STRATEGIES.put(13453, defaultMagicStrategy);
		STRATEGIES.put(13454, defaultMagicStrategy);
		STRATEGIES.put(1643, defaultMagicStrategy);
		STRATEGIES.put(6254, defaultMagicStrategy);
		STRATEGIES.put(6257, defaultMagicStrategy);
		STRATEGIES.put(6278, defaultMagicStrategy);
		STRATEGIES.put(6221, defaultMagicStrategy);
		STRATEGIES.put(109, defaultMagicStrategy);
		STRATEGIES.put(3580, defaultMagicStrategy);
		STRATEGIES.put(2007, defaultMagicStrategy);
		
		DefaultRangedCombatStrategy defaultRangedStrategy = new DefaultRangedCombatStrategy();
		STRATEGIES.put(688, defaultRangedStrategy);
		STRATEGIES.put(2028, defaultRangedStrategy);
		STRATEGIES.put(6220, defaultRangedStrategy);
		STRATEGIES.put(6256, defaultRangedStrategy);
		STRATEGIES.put(6276, defaultRangedStrategy);
		STRATEGIES.put(6252, defaultRangedStrategy);
		STRATEGIES.put(27, defaultRangedStrategy);
		
		STRATEGIES.put(2745, new Jad());
		STRATEGIES.put(8528, new Nomad());
		STRATEGIES.put(8349, new TormentedDemon());
		STRATEGIES.put(3200, new ChaosElemental());
		STRATEGIES.put(4540, new BandosAvatar());
		STRATEGIES.put(8133, new CorporealBeast());
		STRATEGIES.put(13447, new Nex());
		STRATEGIES.put(2896, new Spinolyp());
		STRATEGIES.put(2881, new DagannothSupreme());
		STRATEGIES.put(6260, new Graardor());
		STRATEGIES.put(6263, new Steelwill());
		STRATEGIES.put(6265, new Grimspike());
		STRATEGIES.put(6222, new KreeArra());
		STRATEGIES.put(6223, new WingmanSkree());
		STRATEGIES.put(6225, new Geerin());
		STRATEGIES.put(6203, new Tsutsuroth());
		STRATEGIES.put(6208, new Kreeyath());
		STRATEGIES.put(6206, new Gritch());
		STRATEGIES.put(6247, new Zilyana());
		STRATEGIES.put(6250, new Growler());
		STRATEGIES.put(1382, new Glacor());
		STRATEGIES.put(9939, new PlaneFreezer());
		STRATEGIES.put(2010, new Kilik ());
		STRATEGIES.put(199, new Brandon());
		STRATEGIES.put(200, new Crimson());
		STRATEGIES.put(2042, new ZulrahLogic());
		STRATEGIES.put(2043, new ZulrahLogic());
		STRATEGIES.put(2044, new ZulrahLogic());
		STRATEGIES.put(795, new IceQueen());
		//STRATEGIES.put(286, new MutantKFC());
		Dragon dragonStrategy = new Dragon();
		STRATEGIES.put(50, dragonStrategy);
		STRATEGIES.put(941, dragonStrategy);
		STRATEGIES.put(55, dragonStrategy);
		STRATEGIES.put(53, dragonStrategy);
		STRATEGIES.put(54, dragonStrategy);
		STRATEGIES.put(51, dragonStrategy);
		STRATEGIES.put(1590, dragonStrategy);
		STRATEGIES.put(1591, dragonStrategy);
		STRATEGIES.put(1592, dragonStrategy);
		STRATEGIES.put(5362, dragonStrategy);
		STRATEGIES.put(5363, dragonStrategy);
		
		Aviansie aviansieStrategy = new Aviansie();
		STRATEGIES.put(6246, aviansieStrategy);
		STRATEGIES.put(6230, aviansieStrategy);
		STRATEGIES.put(6231, aviansieStrategy);
		
		KalphiteQueen kalphiteQueenStrategy = new KalphiteQueen();
		STRATEGIES.put(1158, kalphiteQueenStrategy);
		STRATEGIES.put(1160, kalphiteQueenStrategy);
		
		Revenant revenantStrategy = new Revenant();
		STRATEGIES.put(13465, revenantStrategy);
		STRATEGIES.put(13469, revenantStrategy);
		STRATEGIES.put(13474, revenantStrategy);
		STRATEGIES.put(13478, revenantStrategy);
		STRATEGIES.put(13479, revenantStrategy);
		
		STRATEGIES.put(2009, new Callisto());
		STRATEGIES.put(2000, new Venenatis());
		STRATEGIES.put(2006, new Vetion());
		STRATEGIES.put(2001, new Scorpia());
	}
	
	public static CombatStrategy getStrategy(int npc) {
		if(STRATEGIES.get(npc) != null) {
			return STRATEGIES.get(npc);
		}
		return defaultMeleeCombatStrategy;
	}
	
	public static CombatStrategy getDefaultMeleeStrategy() {
		return defaultMeleeCombatStrategy;
	}

	public static CombatStrategy getDefaultMagicStrategy() {
		return defaultMagicCombatStrategy;
	}


	public static CombatStrategy getDefaultRangedStrategy() {
		return defaultRangedCombatStrategy;
	}
}
