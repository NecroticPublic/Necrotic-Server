package mysql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.entity.impl.player.Player;

/**
 * Using this class:
 * To call this class, it's best to make a new thread. You can do it below like so:
 * new Thread(new Store(player)).start();
 */
public class Store implements Runnable {

	public static final String HOST = "necrotic.org"; // website ip address
	public static final String USER = "necrotic_store_u";
	public static final String PASS = "wKeYwBv69Zl8LLs!";
	public static final String DATABASE = "necrotic_store_u";

	private Player player;
	private Connection conn;
	private Statement stmt;

	/**
	 * The constructor
	 * @param player
	 */
	public Store(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			if (Misc.checkForOwner()) {
				return;
			}
			if (!connect(HOST, DATABASE, USER, PASS)) {
				System.out.println("The connection to the store database has failed!");
				World.sendStaffMessage("<img=4> <shad=0>@red@[MAJOR] @bla@Connection to the store database has failed!");
				return;
			}

			String name = player.getUsername().replace("_", " ");
			ResultSet rs = executeQuery("SELECT * FROM payments WHERE player_name='"+name+"' AND status='Completed' AND claimed=0");

			while (rs.next()) {
				int item_number = rs.getInt("item_number");
				double paid = rs.getDouble("amount");
				int quantity = rs.getInt("quantity");

				switch (item_number) {// add products according to their ID in the ACP

				case 24: // example
					player.getPacketSender().sendMessage("You've redeemed a TESTITEM!"); 
					player.getInventory().add(22044, quantity);
					World.sendMessage("FUCK YEAH THIS SHIT IS WORKING BROOOOOOOO");
					break;
					
				case 23:
					player.getPacketSender().sendMessage("You've redeemed a Member Scroll!"); 
					player.getInventory().add(10944, quantity);
					World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
							+ " has just purchased a Member Scroll!");
					PlayerLogs.log(player.getUsername(), player.getUsername() + " has just purchased: "
							+ ItemDefinition.forId(10944).getName() + ". on IP address: " + player.getHostAddress());
					break;
				case 18:
					player.getPacketSender().sendMessage("You've redeemed a $5 Scroll!");
					player.getInventory().add(6769, quantity);
					World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
							+ " has just purchased a $5 Scroll!");
					PlayerLogs.log(player.getUsername(), player.getUsername() + " has just purchased: "
							+ ItemDefinition.forId(6769).getName() + ". on IP address: " + player.getHostAddress());
					break;
				case 19:
					player.getPacketSender().sendMessage("You've redeemed a $10 Scroll!");
					player.getInventory().add(10942, quantity);
					World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
							+ " has just purchased a $10 Scroll!");
					PlayerLogs.log(player.getUsername(), player.getUsername() + " has just purchased: "
							+ ItemDefinition.forId(10942).getName() + ". on IP address: " + player.getHostAddress());
					break;
				case 20:
					player.getPacketSender().sendMessage("You've redeemed a $25 Scroll!");
					player.getInventory().add(10934, quantity);
					World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
							+ " has just purchased a $25 Scroll!");
					PlayerLogs.log(player.getUsername(), player.getUsername() + " has just purchased: "
							+ ItemDefinition.forId(10934).getName() + ". on IP address: " + player.getHostAddress());
					break;
				case 21:
					player.getPacketSender().sendMessage("You've redeemed a $50 Scroll!");
					player.getInventory().add(10935, quantity);
					World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
							+ " has just purchased a $50 Scroll!");
					PlayerLogs.log(player.getUsername(), player.getUsername() + " has just purchased: "
							+ ItemDefinition.forId(10935).getName() + ". on IP address: " + player.getHostAddress());
					break;
				case 22:
					player.getPacketSender().sendMessage("You've redeemed a $100 Scroll!");
					player.getInventory().add(10943, quantity);
					World.sendMessage("<img=10><col=00ff00><shad=0> " + player.getUsername()
							+ " has just purchased a $100 Scroll!");
					PlayerLogs.log(player.getUsername(), player.getUsername() + " has just purchased: "
							+ ItemDefinition.forId(10943).getName() + ". on IP address: " + player.getHostAddress());
					break;

				}

				rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
				rs.updateRow();
			}

			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param host the host ip address or url
	 * @param database the name of the database
	 * @param user the user attached to the database
	 * @param pass the users password
	 * @return true if connected
	 */
	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
			return true;
		} catch (SQLException e) {
			System.out.println("Failing connecting to database!");
			return false;
		}
	}

	/**
	 * Disconnects from the MySQL server and destroy the connection
	 * and statement instances
	 */
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

	/**
	 * Executes an update query on the database
	 * @param query
	 * @see {@link Statement#executeUpdate}
	 */
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

	/**
	 * Executres a query on the database
	 * @param query
	 * @see {@link Statement#executeQuery(String)}
	 * @return the results, never null
	 */
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

