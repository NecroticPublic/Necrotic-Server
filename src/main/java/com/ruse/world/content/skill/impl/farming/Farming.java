package com.ruse.world.content.skill.impl.farming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.ruse.model.Animation;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

public class Farming {
	private final Player player;
	private Plant[] plants = new Plant[4];
	private GrassyPatch[] patches = new GrassyPatch[4];

	public Farming(Player player) {
		this.player = player;

		for (int i = 0; i < patches.length; i++)
			if (patches[i] == null)
				patches[i] = new GrassyPatch();
	}

	public void sequence() {
		for (Plant i : plants) {
			if (i != null) {
				i.process(player);
			}
		}
		for (int i = 0; i < patches.length; i++) {
			if (i >= FarmingPatches.values().length)
				break;
			if ((patches[i] != null) && (!inhabited(FarmingPatches.values()[i].x, FarmingPatches.values()[i].y))) {
				patches[i].process(player, i);
			}
		}
	}

	public void doConfig() {
		for (int i = 0; i < FarmingPatches.values().length; i++) {
			int value = getConfigFor(FarmingPatches.values()[i].config), config = FarmingPatches.values()[i].config;
			if(value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
				player.getPacketSender().sendToggle(config, value);
			else
				player.getPacketSender().sendConfig(config, value);
		}
	}

	public int getConfigFor(int configId) {
		int config = 0;

		for (FarmingPatches i : FarmingPatches.values()) {
			if (i.config == configId) {
				if (inhabited(i.x, i.y)) {
					for (Plant plant : plants)
						if ((plant != null) && (plant.getPatch().ordinal() == i.ordinal())) {
							config += plant.getConfig();
							break;
						}
				} else {
					config += patches[i.ordinal()].getConfig(i.ordinal());
				}
			}
		}

		return config;
	}

	public void clear() {
		for (int i = 0; i < plants.length; i++) {
			plants[i] = null;
		}

		for (int i = 0; i < patches.length; i++)
			patches[i] = new GrassyPatch();
	}

	public void nextWateringCan(int id) {
		if (id == 6797) { //unlimited watering can
			player.getPacketSender().sendMessage("Your Unlimited Watering Can retains its water!");
		} else {
		player.getInventory().delete(id, 1).add(id > 5333 ? (id - 1) : (id - 2), 1);
		player.getPacketSender().sendMessage("<img=10> Members can use an Unlimited Watering Can.");
		}
	}

	public void insert(Plant patch) {
		for (int i = 0; i < plants.length; i++)
			if (plants[i] == null) {
				plants[i] = patch;
				break;
			}
	}

	public boolean inhabited(int x, int y) {
		for (int i = 0; i < plants.length; i++) {
			if (plants[i] != null) {
				FarmingPatches patch = plants[i].getPatch();
				if ((x >= patch.x) && (y >= patch.y) && (x <= patch.x2) && (y <= patch.y2)) {
					if(x == 3054 && y == 3307 && patch != FarmingPatches.SOUTH_FALADOR_FLOWER)
						continue;
					return true;
				}
			}
		}

		return false;
	}

	public boolean click(Player player, int x, int y, int option) {
		if(option == 1)
			for (int i = 0; i < FarmingPatches.values().length; i++) {
				FarmingPatches patch = FarmingPatches.values()[i];
				if ((x >= patch.x) && (y >= patch.y) && (x <= patch.x2) && (y <= patch.y2)) {
					if(x == 3054 && y == 3307 && patch != FarmingPatches.SOUTH_FALADOR_FLOWER)
						continue;
					if(patch == FarmingPatches.SOUTH_FALADOR_ALLOTMENT_SOUTH) {
						player.getPacketSender().sendMessage("This patch is currently disabled.");
						return true;
					}
					if ((inhabited(x, y)) || (patches[i] == null))
						break;
					patches[i].click(player, option, i);
					return true;
				}

			}
		for (int i = 0; i < plants.length; i++) {
			if (plants[i] != null) {
				FarmingPatches patch = plants[i].getPatch();
				if ((x >= patch.x) && (y >= patch.y) && (x <= patch.x2) && (y <= patch.y2)) {
					if(x == 3054 && y == 3307 && patch != FarmingPatches.SOUTH_FALADOR_FLOWER)
						continue;
					plants[i].click(player, option);
					return true;
				}
			}
		}

		return false;
	}

	public void remove(Plant plant) {
		for (int i = 0; i < plants.length; i++)
			if ((plants[i] != null) && (plants[i] == plant)) {
				patches[plants[i].getPatch().ordinal()].setTime();
				plants[i] = null;
				doConfig();
				return;
			}
	}

	public boolean useItemOnPlant(int item, int x, int y) {
		if (item == 5341) {
			for (int i = 0; i < FarmingPatches.values().length; i++) {
				FarmingPatches patch = FarmingPatches.values()[i];
				if ((x >= patch.x) && (y >= patch.y) && (x <= patch.x2) && (y <= patch.y2)) {
					if(x == 3054 && y == 3307 && patch != FarmingPatches.SOUTH_FALADOR_FLOWER)
						continue;
					patches[i].rake(player, i);
					break;
				}
			}
			return true;
		}
		for (int i = 0; i < plants.length; i++) {
			if (plants[i] != null) {
				FarmingPatches patch = plants[i].getPatch();
				if ((x >= patch.x) && (y >= patch.y) && (x <= patch.x2) && (y <= patch.y2)) {
					if(x == 3054 && y == 3307 && patch != FarmingPatches.SOUTH_FALADOR_FLOWER)
						continue;
					plants[i].useItemOnPlant(player, item);
					return true;
				}
			}
		}

		return false;
	}

	public boolean plant(int seed, int object, int x, int y) {
		if (!Plants.isSeed(seed)) {
			return false;
		}
		
		for (FarmingPatches patch : FarmingPatches.values()) {
			if ((x >= patch.x) && (y >= patch.y) && (x <= patch.x2) && (y <= patch.y2)) {
				if(x == 3054 && y == 3307 && patch != FarmingPatches.SOUTH_FALADOR_FLOWER)
					continue;
				if (!patches[patch.ordinal()].isRaked()) {
					player.getPacketSender().sendMessage("This patch needs to be raked before anything can grow in it.");
					return true;
				}

				for (Plants plant : Plants.values()) {
					if (plant.seed == seed) {
						if (player.getSkillManager().getCurrentLevel(Skill.FARMING) >= plant.level) {
							if (inhabited(x, y)) {
								player.getPacketSender().sendMessage("There are already seeds planted here.");
								return true;
							}

							if (patch.seedType != plant.type) {
								player.getPacketSender().sendMessage("You can't plant this type of seed here.");
								return true;
							}
							boolean MAGIC_SECATEURS = player.getInventory().contains(FarmingConstants.MAGIC_SECATEURS) || player.getEquipment().contains(FarmingConstants.MAGIC_SECATEURS);
							if (player.getInventory().contains(FarmingConstants.SECATEURS) || MAGIC_SECATEURS) {
								player.performAnimation(new Animation(2291));
								player.getPacketSender().sendMessage("You bury the seed in the dirt.");
								player.getInventory().delete(seed, 1, true);
								Plant planted = new Plant(patch.ordinal(), plant.ordinal());
								double XP = 1;//percentage
								planted.setTime();
								insert(planted);
								doConfig();
								if(MAGIC_SECATEURS){
									XP = 1.1;//percentage
									player.getPacketSender().sendMessage("Your Magic Secateurs increase your XP by 10%.");
								}
								player.getSkillManager().addExperience(Skill.FARMING, (int)(plant.plantExperience*XP));
							} else {
								String name = ItemDefinition.forId(FarmingConstants.SECATEURS).getName();
								player.getPacketSender().sendMessage("You need " + Misc.anOrA(name) + " " +name+ " to plant seeds.");
							}

						} else {
							player.getPacketSender().sendMessage("You need a Farming level of " + plant.level + " to plant this.");
						}

						return true;
					}
				}

				return false;
			}
		}

		return false;
	}

	public Plant[] getPlants() {
		return plants;
	}

	public void setPlants(Plant[] plants) {
		this.plants = plants;
	}

	public GrassyPatch[] getPatches() {
		return patches;
	}

	public void setPatches(GrassyPatch[] patches) {
		this.patches = patches;
	}

	/*
	 * Saving
	 * Don't wanna fill up player class lol
	 */
	private static final String DIR = "./data/saves/farming/";

	public void save() {
		if(!player.shouldProcessFarming())
			return;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(DIR + ""+player.getUsername()+".txt"));
			for (int i = 0; i < patches.length; i++) {
				if (i >= FarmingPatches.values().length)
					break;
				if (patches[i] != null) {
					writer.write("[PATCH]");
					writer.newLine();
					writer.write("patch: "+i);
					writer.newLine();
					writer.write("stage: "+patches[i].stage);
					writer.newLine();
					writer.write("minute: "+patches[i].minute);
					writer.newLine();
					writer.write("hour: "+patches[i].hour);
					writer.newLine();
					writer.write("day: "+patches[i].day);
					writer.newLine();
					writer.write("year: "+patches[i].year);
					writer.newLine();
					writer.write("END PATCH");
					writer.newLine();
					writer.newLine();
				}
			}
			for (int i = 0; i < plants.length; i++) {
				if (plants[i] != null) {
					writer.write("[PLANT]");
					writer.newLine();
					writer.write("patch: "+plants[i].patch);
					writer.newLine();
					writer.write("plant: "+plants[i].plant);
					writer.newLine();
					writer.write("stage: "+plants[i].stage);
					writer.newLine();
					writer.write("watered: "+plants[i].watered);
					writer.newLine();
					writer.write("harvested: "+plants[i].harvested);
					writer.newLine();
					writer.write("minute: "+plants[i].minute);
					writer.newLine();
					writer.write("hour: "+plants[i].hour);
					writer.newLine();
					writer.write("day: "+plants[i].day);
					writer.newLine();
					writer.write("year: "+plants[i].year);
					writer.newLine();
					writer.write("END PLANT");
					writer.newLine();
					writer.newLine();
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load() {
		if(!player.shouldProcessFarming())
			return;
		try {
			BufferedReader r = new BufferedReader(new FileReader(DIR + ""+player.getUsername()+".txt"));
			int stage = -1, patch = -1, plant = -1, watered = -1, minute = -1, hour = -1, day = -1, year = -1, harvested = -1;
			while(true) {
				String line = r.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				if(line.startsWith("patch"))
					patch = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("stage"))
					stage = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("plant"))
					plant = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("watered"))
					watered = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("minute"))
					minute = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("harvested"))
					harvested = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("hour"))
					hour = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("day"))
					day = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("year"))
					year = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.equals("END PATCH") && patch >= 0) {
					patches[patch].stage = (byte)stage;
					patches[patch].minute = minute;
					patches[patch].hour = hour;
					patches[patch].day = day;
					patches[patch].year = year;
					patch = -1;
				}
				else if(line.equals("END PLANT") && patch >= 0) {
					plants[patch] = new Plant(patch, plant);
					plants[patch].watered = (byte) watered;
					plants[patch].stage = (byte) stage;
					plants[patch].harvested = (byte) harvested;
					plants[patch].minute = minute;
					plants[patch].hour = hour;
					plants[patch].day = day;
					plants[patch].year = year;
					patch = -1;
				}
			}
			r.close();
			doConfig();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
}
