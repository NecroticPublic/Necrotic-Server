package com.ruse.mysql;

import static com.ruse.world.content.Achievements.AchievementData.VOTE_100_TIMES;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.Achievements;
import com.ruse.world.entity.impl.player.Player;


public class Voting implements Runnable {

	private static int voteCount = 0;
	public static final String HOST = "necrotic.org";
	public static final String USER = "voting_user";
	public static final String PASS = "whatnibbahoemadedispassw1rd32";
	public static final String DATABASE = "necrotic_voting";

	private Player player;
	private Connection conn;
	private Statement stmt;

	public Voting(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			if (Misc.checkForOwner()) {
				return;
			}
			if (!connect(HOST, DATABASE, USER, PASS)) {
				System.out.println("The connection to the voting database has failed!");
				World.sendStaffMessage("<img=4> <shad=0>@red@[MAJOR] @bla@Connection to the voting database has failed!");
				return;
			}
			
			boolean message = true;
			boolean found = false;
			String name = player.getUsername().replaceAll(" ", "_");
			ResultSet rs = executeQuery("SELECT * FROM fx_votes WHERE username='"+name+"' AND claimed=0 AND callback_date IS NOT NULL");

			while (rs.next()) {
				//String timestamp = rs.getTimestamp("callback_date").toString();
				//String ipAddress = rs.getString("ip_address");
				//int siteId = rs.getInt("site_id");

				player.getInventory().add(new Item(19670, 1), true);
				if (!found) {
					found = true;
				}
				if (message) {
					player.getPacketSender().sendMessage("<img=10><shad=0><col=bb43df>Thank you for voting and supporting Necrotic!");
					System.out.println(player.getUsername()+" has just voted.");
					message = false;
				}
				player.getLastVoteClaim().reset();
				Achievements.doProgress(player, VOTE_100_TIMES, 1);
				voteCount++;
				if (voteCount >= GameSettings.Vote_Announcer) {
					World.sendMessage("<img=10><shad=0><col=bb43df> 10 more players have just voted! Use ::vote for rewards! Thanks, <col="+ player.getYellHex() + ">" + player.getUsername() + "<col=bb43df>!");
					voteCount = 0;
				}

				//System.out.println("[FoxVote] Vote claimed by "+name+". (sid: "+siteId+", ip: "+ipAddress+", time: "+timestamp+")");

				rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
				rs.updateRow();
			}
			
			if (!found) {
				player.getPacketSender().sendMessage("Found no pending vote rewards. Try again, or wait a few minutes.");
			}

			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
			return true;
		} catch (SQLException e) {
			System.out.println("Failing connecting to database!");
			return false;
		}
	}

	public void destroy() {
        try {
    		conn.close();
        	conn = null;
        	if (stmt != null) {
    			stmt.close();
        		stmt = null;
        	}
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

	public int executeUpdate(String query) {
        try {
        	this.stmt = this.conn.createStatement(1005, 1008);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

	public ResultSet executeQuery(String query) {
        try {
        	this.stmt = this.conn.createStatement(1005, 1008);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
