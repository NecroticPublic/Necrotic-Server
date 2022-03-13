package com.ruse.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;
import com.ruse.model.Skill;
import com.ruse.world.entity.impl.player.Player;

public class OLDHighscoresHandler implements Runnable{

	/**
	 * Secret key
	 */
	final static String secret = "cb4bfdae9b18915";
	/**
	 * Username that is used for mysql connection
	 */
	final static String user = "27e917";


	private Player player;

	public OLDHighscoresHandler(Player player) {
		this.player = player;
	}
	
	private int gameModeInt() {
		/*if (player.getGameMode() == GameMode.IRONMAN) {
			return 1;
		}
		if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
			return 2;
		}*/
		return 0;
	}
	


	/**
	 * Function that handles everything, it inserts or updates
	 * user data in database
	 */
	@Override
	public void run() {
		/**
		 * Players username
		 */
		final String username = player.getUsername();
		/**
		 * Represents game mode
		 * If you want to set game modes do this:
		 */
		int gameMode = gameModeInt();
		//System.out.println("Sending gameModeInt: "+gameModeInt());
		/**
		 * Represents overall xp
		 */
		long overallXp = player.getSkillManager().getTotalExp();
		/**
		 * Represents attack xp
		 */
		long attackXp = player.getSkillManager().getExperience(Skill.ATTACK);
		/**
		 * Represents defence xp
		 */
		long defenceXp = player.getSkillManager().getExperience(Skill.DEFENCE);
		/**
		 * Represents strength xp
		 */
		long strengthXp = player.getSkillManager().getExperience(Skill.STRENGTH);
		/**
		 * Represents constitution xp
		 */
		long constitutionXp = player.getSkillManager().getExperience(Skill.CONSTITUTION);
		/**
		 * Represents ranged xp
		 */
		long rangedXp = player.getSkillManager().getExperience(Skill.RANGED);
		/**
		 * Represents prayer xp
		 */
		long prayerXp = player.getSkillManager().getExperience(Skill.PRAYER);
		/**
		 * Represents magic xp
		 */
		long magicXp = player.getSkillManager().getExperience(Skill.MAGIC);
		/**
		 * Represents cooking xp
		 */
		long cookingXp = player.getSkillManager().getExperience(Skill.COOKING);
		/**
		 * Represents woodcutting xp
		 */
		long woodcuttingXp = player.getSkillManager().getExperience(Skill.WOODCUTTING);
		/**
		 * Represents fletching xp
		 */
		long fletchingXp = player.getSkillManager().getExperience(Skill.FLETCHING);
		/**
		 * Represents fishing xp
		 */
		long fishingXp = player.getSkillManager().getExperience(Skill.FISHING);
		/**
		 * Represents firemaking xp
		 */
		long firemakingXp = player.getSkillManager().getExperience(Skill.FIREMAKING);
		/**
		 * Represents crafting xp
		 */
		long craftingXp = player.getSkillManager().getExperience(Skill.CRAFTING);
		/**
		 * Represents smithing xp
		 */
		long smithingXp = player.getSkillManager().getExperience(Skill.SMITHING);
		/**
		 * Represents mining xp
		 */
		long miningXp = player.getSkillManager().getExperience(Skill.MINING);
		/**
		 * Represents herblore xp
		 */
		long herbloreXp = player.getSkillManager().getExperience(Skill.HERBLORE);
		/**
		 * Represents agility xp
		 */
		long agilityXp = player.getSkillManager().getExperience(Skill.AGILITY);
		/**
		 * Represents thieving xp
		 */
		long thievingXp = player.getSkillManager().getExperience(Skill.THIEVING);
		/**
		 * Represents slayer xp
		 */
		long slayerXp = player.getSkillManager().getExperience(Skill.SLAYER);
		/**
		 * Represents farming xp
		 */
		long farmingXp = player.getSkillManager().getExperience(Skill.FARMING);
		/**
		 * Represents runecrafting xp
		 */
		long runecraftingXp = player.getSkillManager().getExperience(Skill.RUNECRAFTING);
		/**
		 * Represents hunter xp
		 */
		long hunterXp = 0;//player.getSkillManager().getExperience(Skill.HUNTER);
		/**
		 * Represents construction xp
		 */
		long constructionXp = player.getSkillManager().getExperience(Skill.CONSTRUCTION);
		/**
		 * Creates new instance of jdbc driver
		 * if that driver exists
		 */
		
		if (player.getRights().OwnerDeveloperOnly()) {
			overallXp = 1337;
			attackXp = 0;
			defenceXp = 0;
			strengthXp = 0;
			constitutionXp = 0;
			rangedXp = 0;
			prayerXp = 0;
			magicXp = 0;
			cookingXp = 0;
			woodcuttingXp = 0;
			fletchingXp = 0;
			fishingXp = 0;
			firemakingXp = 0;
			craftingXp = 0;
			smithingXp = 0;
			miningXp = 0;
			herbloreXp = 0;
			agilityXp = 0;
			thievingXp = 0;
			slayerXp = 0;
			farmingXp = 0;
			runecraftingXp = 0;
			hunterXp = 0;
			constructionXp = 0;
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		/**
		 * Sets Connection variable to null
		 */
		Connection connection = null;
		/**
		 * Sets Statement variable to null
		 */
		Statement stmt = null;

		/**
		 * Attempts connecting to database
		 */
		try {
			connection = DriverManager.getConnection("jdbc:mysql://198.211.123.88:3306/admin_scores_data", user, secret);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		/**
		 * Checks if connection isnt null
		 */
		if (connection != null) {
		    try {
		    	stmt = (Statement) connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM `"+user+"_scores` WHERE username='" +username+ "'");
				if(rs.next()) {
					if(rs.getInt("count") > 0)  {
						stmt.executeUpdate("UPDATE `"+user+"_scores` SET overall_xp = '"+overallXp+"', attack_xp = '"+attackXp+"', defence_xp = '"+defenceXp+"', strength_xp = '"+strengthXp+"', constitution_xp = '"+constitutionXp+"', ranged_xp = '"+rangedXp+"', prayer_xp = '"+prayerXp+"', magic_xp = '"+magicXp+"', cooking_xp = '"+cookingXp+"', woodcutting_xp = '"+woodcuttingXp+"', fletching_xp = '"+fletchingXp+"', fishing_xp = '"+fishingXp+"', firemaking_xp = '"+firemakingXp+"', crafting_xp = '"+craftingXp+"', smithing_xp = '"+smithingXp+"', mining_xp = '"+miningXp+"', herblore_xp = '"+herbloreXp+"', agility_xp = '"+agilityXp+"', thieving_xp = '"+thievingXp+"', slayer_xp = '"+slayerXp+"', farming_xp = '"+farmingXp+"', runecrafting_xp = '"+runecraftingXp+"', hunter_xp = '"+hunterXp+"', construction_xp = '"+constructionXp+"' WHERE username = '"+username+"'");
					} else {
						stmt.executeUpdate("INSERT INTO `"+user+"_scores` (username, mode, overall_xp, attack_xp, defence_xp, strength_xp, constitution_xp, ranged_xp, prayer_xp, magic_xp, cooking_xp, woodcutting_xp, fletching_xp, fishing_xp, firemaking_xp, crafting_xp, smithing_xp, mining_xp, herblore_xp, agility_xp, thieving_xp, slayer_xp, farming_xp, runecrafting_xp, hunter_xp, construction_xp) VALUES ('"+username+"', '"+gameMode+"', '"+overallXp+"', '"+attackXp+"', '"+defenceXp+"', '"+strengthXp+"', '"+constitutionXp+"', '"+rangedXp+"', '"+prayerXp+"', '"+magicXp+"', '"+cookingXp+"', '"+woodcuttingXp+"', '"+fletchingXp+"', '"+fishingXp+"', '"+firemakingXp+"', '"+craftingXp+"', '"+smithingXp+"', '"+miningXp+"', '"+herbloreXp+"', '"+agilityXp+"', '"+thievingXp+"', '"+slayerXp+"', '"+farmingXp+"', '"+runecraftingXp+"', '"+hunterXp+"', '"+constructionXp+"')");
					}
				}
				stmt.close();
				connection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("Failed to make hiscores connection!");
		}

		return;
	}
}