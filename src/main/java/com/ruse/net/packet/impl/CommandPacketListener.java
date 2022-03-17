package com.ruse.net.packet.impl;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.BonusExperienceTask;
import com.ruse.model.Animation;
import com.ruse.model.Difficulty;
import com.ruse.model.Flag;
import com.ruse.model.GameMode;
import com.ruse.model.GameObject;
import com.ruse.model.Graphic;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.MagicSpellbook;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Prayerbook;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.container.impl.Shop.ShopManager;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.mysql.Voting;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.Misc;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.World;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.CrystalChest;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.Doom;
import com.ruse.world.content.DropsInterface;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.PlayerPunishment;
import com.ruse.world.content.PlayerPunishment.Jail;
import com.ruse.world.content.PlayersOnlineInterface;
import com.ruse.world.content.TeleportInterface;
import com.ruse.world.content.WellOfGoodwill;
import com.ruse.world.content.clan.ClanChat;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.cluescrolls.CLUESCROLL;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.DesolaceFormulas;
import com.ruse.world.content.combat.effect.EquipmentBonus;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.range.ToxicBlowpipe;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.grandexchange.GrandExchange;
import com.ruse.world.content.grandexchange.GrandExchangeOffers;
import com.ruse.world.content.holidayevents.christmas2016;
import com.ruse.world.content.holidayevents.easter2017;
import com.ruse.world.content.randomevents.EvilTree;
import com.ruse.world.content.randomevents.ShootingStar;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.content.skill.impl.construction.Construction;
import com.ruse.world.content.skill.impl.crafting.Jewelry;
import com.ruse.world.content.skill.impl.fletching.BoltData;
import com.ruse.world.content.skill.impl.herblore.Decanting;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerHandler;
import com.ruse.world.entity.impl.player.PlayerSaving;

import mysql.impl.Store;


/**
 * This packet listener manages commands a player uses by using the
 * command console prompted by using the "`" char.
 *
 * @author Gabriel Hannason
 */

public class CommandPacketListener implements PacketListener {

    public static int voteCount = 8;

    @Override
    public void handleMessage(Player player, Packet packet) {
        String command = Misc.readString(packet.getBuffer());
        String[] parts = command.toLowerCase().split(" ");
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        try {
            switch (player.getRights()) {
                case PLAYER:
                    playerCommands(player, parts, command);
                    break;
                case MODERATOR:
                    playerCommands(player, parts, command);
                    memberCommands(player, parts, command);
                    helperCommands(player, parts, command);
                    moderatorCommands(player, parts, command);
                    contributorCommands(player, parts, command);
                    break;
                case ADMINISTRATOR:
                    playerCommands(player, parts, command);
                    memberCommands(player, parts, command);
                    helperCommands(player, parts, command);
                    moderatorCommands(player, parts, command);
                    contributorCommands(player, parts, command);
                    administratorCommands(player, parts, command);
                    break;
                case OWNER:
                    playerCommands(player, parts, command);
                    memberCommands(player, parts, command);
                    helperCommands(player, parts, command);
                    moderatorCommands(player, parts, command);
                    administratorCommands(player, parts, command);
                    contributorCommands(player, parts, command);
                    ownerCommands(player, parts, command);
                    developerCommands(player, parts, command);
                    break;
                case DEVELOPER:
                    playerCommands(player, parts, command);
                    memberCommands(player, parts, command);
                    helperCommands(player, parts, command);
                    moderatorCommands(player, parts, command);
                    administratorCommands(player, parts, command);
                    contributorCommands(player, parts, command);
                    ownerCommands(player, parts, command);
                    developerCommands(player, parts, command);
                    break;
                case SUPPORT:
                    playerCommands(player, parts, command);
                    memberCommands(player, parts, command);
                    helperCommands(player, parts, command);
                    contributorCommands(player, parts, command);
                    break;
                case CONTRIBUTOR:
                    playerCommands(player, parts, command);
                    contributorCommands(player, parts, command);
                    break;
                case VETERAN:
                    playerCommands(player, parts, command);
                    break;
                case MEMBER:
                    playerCommands(player, parts, command);
                    contributorCommands(player, parts, command);
                    memberCommands(player, parts, command);
                    break;
                default:
                    break;
            }
        } catch (Exception exception) {
            exception.printStackTrace();

            if (player.getRights() == PlayerRights.DEVELOPER) {
                player.getPacketSender().sendMessage("Error executing that command.");

            } else {
                player.getPacketSender().sendMessage("Error executing that command.");
            }

        }
    }

    private static void playerCommands(final Player player, String[] command, String wholeCommand) {
        if (wholeCommand.equalsIgnoreCase("bank") && !player.getRights().OwnerDeveloperOnly()) {
            player.forceChat("Hey, everyone, I just tried to do something very silly!");
        }
        if (command[0].equalsIgnoreCase("time") || command[0].equalsIgnoreCase("date") || command[0].equalsIgnoreCase("clock")) {
            int month = 1 + Misc.getMonth();
            String mo = Integer.toString(month);
            String dd = Integer.toString(Misc.getDayOfMonth());
            String weekend = "";

            if (Misc.getDayOfMonth() < 10) {
                dd = "0" + dd;
            }

            if (month < 10) {
                mo = "0" + mo;
            }

            if (Misc.isWeekend()) {
                weekend = ". It's the weekend";
            }

            player.getPacketSender().sendMessage("@cya@<shad=0>[Time] <shad=-1>@bla@[" + mo + "/" + dd + "] (MM/DD) @ " + Misc.getCurrentServerTime() + " (24:00) in New York City, USA" + weekend + ".");
        }
        if (command[0].equalsIgnoreCase("hasVoid")) {
            if (EquipmentBonus.wearingVoid(player)) {
                player.getPacketSender().sendMessage("You are wearing void.");
            } else {
                player.getPacketSender().sendMessage("You're not wearing void.");
            }
            if (EquipmentBonus.voidElite(player)) {
                player.getPacketSender().sendMessage("You're wearing voidElite.");
            } else {
                player.getPacketSender().sendMessage("You're not wearing voidElite.");
            }
            if (EquipmentBonus.voidMage(player)) {
                player.getPacketSender().sendMessage("You're wearing mage void.");
            } else {
                player.getPacketSender().sendMessage("You're not wearing mage void");
            }
            if (EquipmentBonus.voidMelee(player)) {
                player.getPacketSender().sendMessage("You're wearing melee void.");
            } else {
                player.getPacketSender().sendMessage("You're not wearing melee void");
            }
            if (EquipmentBonus.voidRange(player)) {
                player.getPacketSender().sendMessage("You're wearing range void.");
            } else {
                player.getPacketSender().sendMessage("You're not wearing range void");
            }
        }
        if (command[0].equalsIgnoreCase("maxhit")) {
            Character b = player.getCombatBuilder().getLastAttacker();
            if (b == null) {
                player.getPacketSender().sendMessage("You have no previous target. Attack something for the calculation.");
                return;
            } else {
                player.getPacketSender().sendMessage("Successfully found your previous target.");
            }
            if (command[1] == null) {
                player.getPacketSender().sendMessage("Invalid usage. Proper usage is <shad=0>@red@::maxhit melee @bla@ or @gre@::maxhit range");
                return; //TODO change all commands to fix spaces
            }
            if (command[1].equalsIgnoreCase("melee")) {
                player.getPacketSender().sendMessage("Your max Melee hit is: " + CombatFactory.calculateMaxMeleeHit(player, b) + ". (Constitution)");
            } else if (command[1].equalsIgnoreCase("range") || command[1].equalsIgnoreCase("ranged")) {
                player.getPacketSender().sendMessage("Your max Range hit is: " + CombatFactory.calculateMaxRangedHit(player, b) + ". (Constitution)");
            } else if (command[1].equalsIgnoreCase("magic") || command[1].equalsIgnoreCase("mage")) {
                player.getPacketSender().sendMessage("Good thinking, but each spell actually has a built in damage cap.").sendMessage("You can check the OSRS Wiki for the caps. *10 them for constitution.");
            } else {
                player.getPacketSender().sendMessage("Invalid usage. Proper usage is <shad=0>@red@::maxhit melee @gre@::maxhit range");
            }
        }
        if (command[0].equalsIgnoreCase("indung")) {
            player.getPacketSender().sendMessage("isdung is currently: " + player.isInDung());
        }
        if (command[0].equalsIgnoreCase("auth") || command[0].equalsIgnoreCase("redeem")) {
            if (player.getLocation() == Location.DUNGEONEERING) {
                player.getPacketSender().sendMessage("Please finish your current dungeon before claiming votes.");
                return;
            }
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("Please exit the wilderness before claiming votes.");
                return;
            }
            if (!player.getLastVoteClaim().elapsed(250)) {
                player.getPacketSender().sendMessage("You must wait at least 1 second before using " + command[0] + " again.");
                return;
            }
			/*String auth = command[1];
			player.getLastVoteClaim().reset();
			doMotivote.main(player, auth);
			*/
            player.getPacketSender().sendMessage("Checking for votes...");
            new Thread(new Voting(player)).start();
        }

        if (command[0].equalsIgnoreCase("whatdrops")) {
            try {
                boolean isItem = false;
                int itemId = 0;
                String target = wholeCommand.substring(command[0].length() + 1);
                player.getPacketSender().sendMessage("Finding what drops \"" + target + "\".");

                for (int i = 0; i < ItemDefinition.getDefinitions().length; i++) {
                    if (ItemDefinition.forId(i) == null || ItemDefinition.forId(i).getName() == null) {
                        continue;
                    }
                    if (ItemDefinition.forId(i).getName().toLowerCase().equalsIgnoreCase(target)) {
                        isItem = true;
                        itemId = i;
                        break;
                    }
                }
                if (!isItem) {
                    player.getPacketSender().sendMessage("No item named \"" + target + "\" was found.");
                    return;
                } /*else {
					player.getPacketSender().sendMessage("Found the item");
				}*/


                for (int i = 0; i < NpcDefinition.getDefinitions().length; i++) {
                    if (NpcDefinition.forId(i) == null || NpcDefinition.forId(i).getName() == null || NPCDrops.forId(i) == null || NPCDrops.forId(i).getDropList() == null || NPCDrops.forId(i).getDropList().length <= 0) {
                        //System.out.println("fuck sake");
                        continue;
                    }

                    for (int q = 0; q < NPCDrops.forId(i).getDropList().length; q++) {
                        if (ItemDefinition.forId(NPCDrops.forId(i).getDropList()[q].getItem().getId()) == null || ItemDefinition.forId(NPCDrops.forId(i).getDropList()[q].getItem().getId()).getName() == null) {
                            //System.out.println("what the hell");
                            continue;
                        }
                        //System.out.println(ItemDefinition.forId(NPCDrops.forId(i).getDropList()[q].getId()).getName()+" VS "+ItemDefinition.forId(itemId).getName());

                        if (ItemDefinition.forId(NPCDrops.forId(i).getDropList()[q].getId()).getName().equalsIgnoreCase(ItemDefinition.forId(itemId).getName())) {
                            player.getPacketSender().sendMessage(NpcDefinition.forId(i).getName() + " drops " + target + ".");
                            continue;
                        }
                    }
                }




				/*for (int i = 0; i < NpcDefinition.getDefinitions().length; i++) {
					if (NpcDefinition.forId(i) == null || NpcDefinition.forId(i).getName() == null || NPCDrops.forId(i) == null || NPCDrops.forId(i).getDropList() == null || NPCDrops.forId(i).getDropList().length >= 0) {
						continue;
					}

					for (int q = 0; q < NPCDrops.forId(i).getDropList().length; q++) {
						if (ItemDefinition.forId(NPCDrops.forId(i).getDropList()[q].getItem().getId()) == null || ItemDefinition.forId(NPCDrops.forId(i).getDropList()[q].getItem().getId()).getName() == null) {
							continue;
						}
						if (ItemDefinition.forId(NPCDrops.forId(i).getDropList()[q].getItem().getId()).getName().toLowerCase().equalsIgnoreCase(target)) {
							player.getPacketSender().sendMessage(NpcDefinition.forId(i).getName()+" drops "+target+".");
							continue;
						}
					}
				}*/


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (command[0].equalsIgnoreCase("drops")) {
            player.getPacketSender().sendMessage("Opening drops interface...");
            DropsInterface.open(player);
			/*try {
				String target = wholeCommand.substring(command[0].length()+1);
				player.getPacketSender().sendMessage("Finding drops for \"" + target+"\".");

				boolean found = false, drops = false;
				for (int i = 0; i < NpcDefinition.getDefinitions().length; i++) {
					if (NpcDefinition.forId(i) == null || NpcDefinition.forId(i).getName() == null || NPCDrops.forId(i) == null || NPCDrops.forId(i).getDropList() == null) {
						continue;
					}
					if (NpcDefinition.forId(i).getName().toLowerCase().equalsIgnoreCase(target)) {
						found = true;
						if (NPCDrops.forId(i).getDropList().length > 0) {
							drops = true;
							player.getPacketSender().sendMessage("Sending " + target + "'s drop table!");
							NPCDrops.sendDropTableInterface(player, i);
							break;
						}
					}
				}
				if (!found) {
					player.getPacketSender().sendMessage("No NPC named \"" + target + "\" has been found!");
				} else if (found && !drops) {
					player.getPacketSender().sendMessage("Found NPC named \""+target+"\", however it has no specific drops.");
				}
			} catch (Exception e) {
				//e.printStackTrace();
				player.getPacketSender().sendMessage("Usage - ::drops [full npc name] - please try again.");
			}*/
        }
        if (wholeCommand.equalsIgnoreCase("commands")) {
            player.getPacketSender().sendString(1, GameSettings.CommandsUrl);
            player.getPacketSender().sendMessage("Attempting to open the command list.");
        }
        if (command[0].equalsIgnoreCase("referral") || command[0].equalsIgnoreCase("referred") || command[0].equalsIgnoreCase("refer") || command[0].equalsIgnoreCase("referredby")) {
            String ref = wholeCommand.substring(command[0].length() + 1);
            //String reff = command[1];
            Player player2 = World.getPlayerByName(ref);
            if (player2 == null) {
                player.getPacketSender().sendMessage(ref + " returned errors. Check name? They must be online to complete a referral.");
                return;
            } else if (player.getUsername().equals(player2.getUsername())) {
                player.getPacketSender().sendMessage("Uhm... You can't refer yourself.");
                return;
            } else if (player.getHostAddress().equals(player2.getHostAddress())) {
                World.sendStaffMessage("<img=10> @mag@<shad=0> [Refs] " + player.getUsername() + " and " + player2.getUsername() + " tried to reffer eachother on the same IP!");
                World.sendStaffMessage("<img=10> @mag@<shad=0> [Refs] " + player.getUsername() + "'s IP: " + player.getHostAddress() + " | " + player2.getUsername() + "'s IP: " + player2.getHostAddress());
                player.getPacketSender().sendMessage("<img=10> @red@<shad=0>Self reffering is against the rules. This action has been logged.");
                player2.getPacketSender().sendMessage("<img=10> @red@<shad=0>Self reffering is against the rules. This action has been logged.");
                PlayerLogs.log("1 - refboosts", player.getUsername() + " tried to set their refferer to " + player2.getUsername() + " on the same IP! IP: " + player.getHostAddress());
                return;
            } else if (player.gotReffered() == true) {
                player2.getPacketSender().sendMessage(player.getUsername() + "  just tried to set their refferrer to you, but failed as they were already reffered.");
                player.getPacketSender().sendMessage(player2.getUsername() + " could not be set as your refferrer, because you have already been reffered.");
                return;
            } else if (player.getInventory().getFreeSlots() == 0 && player2.getInventory().getFreeSlots() == 0) {
                player.getPacketSender().sendMessage("Both you and " + player2.getUsername() + " need 1 free slot to finish the referral.");
                player2.getPacketSender().sendMessage(player.getUsername() + " tried to reffer you, but it failed because you both need a free inventory space.");
                return;
            } else {
                //player.getPacketSender().sendMessage(player2.getUsername()+" was found! Do referral method!");
                BonusExperienceTask.addBonusXp(player, 60);
                if (player.getInventory().isFull()) {
                    player.getBank(0).add(14017, 1);
                } else {
                    player.getInventory().add(14017, 1);
                }
                BonusExperienceTask.addBonusXp(player2, 30);
                player.setReffered(true);
                player.getPacketSender().sendMessage("You have successfully been reffered by " + player2.getUsername());
                player2.getPacketSender().sendMessage(player.getUsername() + " has succesfully been reffered by you.");
                World.sendStaffMessage("<img=10><shad=0> @mag@[Ref] @bla@" + player.getUsername() + " has set their refferrer to " + player2.getUsername());
                PlayerLogs.log("1 - realreferrals", player.getUsername() + " set their refferrer to " + player2.getUsername() + " successfully.");
            }
        }
        if (wholeCommand.equalsIgnoreCase("ironmaninfo") || wholeCommand.equalsIgnoreCase("hciminfo")) {
            player.getPacketSender().sendString(1, GameSettings.IronManModesUrl);
            player.getPacketSender().sendMessage("Attempting to open Iron Man Mode Info");
        }
        if (wholeCommand.equalsIgnoreCase("donorinfo") || wholeCommand.equalsIgnoreCase("memberinfo")) {
            player.getPacketSender().sendString(1, GameSettings.RankFeaturesUrl);
            player.getPacketSender().sendMessage("Attempting to open Member Info");
        }
        if (command[0].equalsIgnoreCase("wiki") || command[0].equalsIgnoreCase("wikia")) {
            player.getPacketSender().sendString(1, GameSettings.WikiaUrl);
            player.getPacketSender().sendMessage("Attempting to open the Necrotic Wikia!");
        }
        if (command[0].equalsIgnoreCase("checkwikia") || command[0].equalsIgnoreCase("checkwiki") || command[0].equalsIgnoreCase("searchwiki") || command[0].equalsIgnoreCase("searchwikia")) {
            String toSearch = wholeCommand.substring(command[0].length() + 1).replaceAll(" ", "+");
            player.getPacketSender().sendString(1, GameSettings.WikiaUrl + "/wiki/Special:Search?search=" + toSearch + "&fulltext=Search");
            player.getPacketSender().sendMessage("Attempting to search wiki for: " + wholeCommand.substring(command[0].length() + 1));
        }
        if (command[0].equalsIgnoreCase("thread") || command[0].equalsIgnoreCase("tid")
                || command[0].equalsIgnoreCase("showthread")) {
            try {
                int num = Integer.parseInt(command[1]);
                player.getPacketSender().sendString(1, GameSettings.ThreadUrl + num);
                player.getPacketSender().sendMessage("Attempting to open forum thread id: " + num);
            } catch (Exception e) {
                player.getPacketSender()
                        .sendMessage("Make sure you include the thread's ID. This is the TID in the web url.")
                        .sendMessage("For example, ::rules brings up a thread with TID 17.");
            }
        }
        if (command[0].equalsIgnoreCase("claim")) {
            if (player.getInventory().getFreeSlots() >= 6 || player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)) {
                player.getPacketSender().sendMessage("Checking for pending purchases...");
                new Thread(new Store(player)).start();
                //player.rspsdata(player, player.getUsername());
            } else {
                player.getPacketSender().sendMessage("You need at least 6 free inventory slots to claim purchased items.");
            }
        }
        if (wholeCommand.equalsIgnoreCase("donate") || wholeCommand.equalsIgnoreCase("store")) {
            player.getPacketSender().sendString(1, GameSettings.StoreUrl);
            player.getPacketSender().sendMessage("Attempting to open the store");
        }
        if (wholeCommand.equalsIgnoreCase("discord") || wholeCommand.equalsIgnoreCase("chat")) {
            player.getPacketSender().sendString(1, GameSettings.DiscordUrl);
            player.getPacketSender().sendMessage("Attempting to open our Discord Server");
        }
        if (wholeCommand.equalsIgnoreCase("forums") || wholeCommand.equalsIgnoreCase("forum")) {
            player.getPacketSender().sendString(1, GameSettings.ForumUrl);
            player.getPacketSender().sendMessage("Attempting to open the forums");
        }
        if (wholeCommand.equalsIgnoreCase("rule") || wholeCommand.equalsIgnoreCase("rules")) {
            player.getPacketSender().sendString(1, GameSettings.RuleUrl);
            player.getPacketSender().sendMessage("Attempting to open the Rules.");
        }
        if (command[0].equalsIgnoreCase("attacks")) {
            int attack = DesolaceFormulas.getMeleeAttack(player);
            int range = DesolaceFormulas.getRangedAttack(player);
            int magic = DesolaceFormulas.getMagicAttack(player);
            player.getPacketSender().sendMessage("@bla@Melee attack: @or2@" + attack + "@bla@, ranged attack: @or2@" + range + "@bla@, magic attack: @or2@" + magic);
        }
        if (command[0].toLowerCase().startsWith("ownermode") || command[0].toLowerCase().startsWith("noclip") && !(player.getRights().isStaff()) || command[0].toLowerCase().startsWith("rickroll") || command[0].toLowerCase().startsWith("hack") || command[0].toLowerCase().startsWith("godmode")) {
            player.getPacketSender().sendString(1, GameSettings.RickRoll);
            player.getPacketSender().sendMessage("<img=10> @red@[Announcements] @bla@" + player.getUsername() + " just got rick rolled! Everyone laugh at them! <img=25>");
        }
        if (command[0].equalsIgnoreCase("vote")) {
            player.getPacketSender().sendString(1, GameSettings.VoteUrl);//"http://Ruseps.com/vote/?user="+player.getUsername());
            player.getPacketSender().sendMessage("Attempting to open the vote panel");
        }
        if (command[0].equalsIgnoreCase("hiscores") || command[0].equalsIgnoreCase("highscores")) {
            player.getPacketSender().sendString(1, GameSettings.HiscoreUrl);
            player.getPacketSender().sendMessage("Attempting to open the hiscores.");
        }
        if (command[0].equalsIgnoreCase("toggleglobalmessages") || command[0].equalsIgnoreCase("globalmessages")) {
            if (player.toggledGlobalMessages() == false) {
                player.getPacketSender().sendMessage("You have opted out from filterable global messages.");
                player.setToggledGlobalMessages(true);
            } else {
                player.getPacketSender().sendMessage("You have opted in to filterable global messages.");
                player.setToggledGlobalMessages(false);
            }
        }
        if (command[0].equalsIgnoreCase("home")) {
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = GameSettings.HOME_CORDS;
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you home!");
        }
        if (command[0].equalsIgnoreCase("gamble")) {
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = new Position(3213, 3424, 0);
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you to the Gambling Area!");
        }


        if (command[0].equalsIgnoreCase("edge")) {
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = GameSettings.EDGE_CORDS;
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            ;
            player.getPacketSender().sendMessage("Welcome to Edgeville.");
        }
        if (command[0].equalsIgnoreCase("trade") || command[0].equalsIgnoreCase("ge") || command[0].equalsIgnoreCase("grandexchange")) {
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = GameSettings.TRADE_CORDS;
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you to the trading area!");
        }
        if ((command[0].equalsIgnoreCase("shop") && !player.getRights().OwnerDeveloperOnly()) || command[0].equalsIgnoreCase("shops")) {
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = new Position(3690, 2977, 0);
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you to our shops!");
        }
        if (command[0].equalsIgnoreCase("chill")) {
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = GameSettings.CHILL_CORDS;
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you to <col=#002AF8>Chill!");
        }
        if (command[0].equalsIgnoreCase("tome")) {
            player.getInventory().add(9003, 1);
            player.getPacketSender().sendMessage("You get a tome of inquisition.");
        }
        if (command[0].equalsIgnoreCase("help")) {
            if (player.getLastYell().elapsed(30000)) {
                if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                    World.sendStaffMessage("<col=FF0066><img=10> [TICKET SYSTEM]<col=6600FF> " + player.getUsername()
                            + " has requested help, but is @red@*IN LEVEL " + player.getWildernessLevel() + " WILDERNESS*<col=6600FF>. Be careful.");
                }
                if (PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
                    World.sendStaffMessage("<col=FF0066><img=10> [TICKET SYSTEM]<col=6600FF> " + player.getUsername()
                            + " has requested help, but is @red@*MUTED*<col=6600FF>. Be careful.");
                } else {
                    World.sendStaffMessage("<col=FF0066><img=10> [TICKET SYSTEM]<col=6600FF> " + player.getUsername()
                            + " has requested help. Please help them!");
                }
                player.getLastYell().reset();
                player.getPacketSender()
                        .sendMessage("<col=663300>Your help request has been received. Please be patient.");
            } else {
                player.getPacketSender().sendMessage("<col=663300>You need to wait 30 seconds before using this again.")
                        .sendMessage(
                                "<col=663300>If it's an emergency, please private message a staff member directly instead.");
            }
        }
        if (command[0].equalsIgnoreCase("empty")) {
            player.getPacketSender().sendInterfaceRemoval().sendMessage("You clear your inventory.");
            player.getSkillManager().stopSkilling();
            for (int i = 0; i < player.getInventory().capacity(); i++) {
                if (player.getInventory().get(i) != null && player.getInventory().get(i).getId() > 0) {
                    if (ItemDefinition.forId(player.getInventory().get(i).getId()) != null && ItemDefinition.forId(player.getInventory().get(i).getId()).getName() != null) {
                        PlayerLogs.log(player.getUsername(), "Emptied item (id:" + player.getInventory().get(i).getId() + ", amount:" + player.getInventory().get(i).getAmount() + ") -- " + ItemDefinition.forId(player.getInventory().get(i).getId()).getName());
                    } else {
                        PlayerLogs.log(player.getUsername(), "Emptied item (id:" + player.getInventory().get(i).getId() + ", amount:" + player.getInventory().get(i).getAmount() + ")");
                    }
                }
            }
            player.getInventory().resetItems().refreshItems();
        }
        if (command[0].equalsIgnoreCase("players")) {
            player.getPacketSender().sendInterfaceRemoval();
            PlayersOnlineInterface.showInterface(player);
        }
        if (command[0].equalsIgnoreCase("fly")) {
            if (player.canFly() == false) {
                player.getPacketSender().sendMessage("You do not understand the complexities of flight.");
                return;
            }
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot fly in the Wilderness.");
                return;
            }
            if (player.canFly() && player.isFlying()) {
                player.getPacketSender().sendMessage("You stop flying.");
                player.setFlying(false);
                player.newStance();
                return;
            }
            if (player.canFly() && player.isFlying() == false) {
                player.getPacketSender().sendMessage("You begin flying.");
                player.setFlying(true);
                player.newStance();
                return;
            }
            return;
        }
        if (command[0].equalsIgnoreCase("ghostwalk")) {
            if (player.canGhostWalk() == false) {
                player.getPacketSender().sendMessage("You do not understand the complexities of death.");
                return;
            }
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot ghost walk in the Wilderness.");
                return;
            }
            if (player.canGhostWalk() && player.isGhostWalking()) {
                player.getPacketSender().sendMessage("You stop ghost-walking.");
                player.setGhostWalking(false);
                player.newStance();
                return;
            }
            if (player.canGhostWalk() && player.isGhostWalking() == false) {
                player.getPacketSender().sendMessage("You begin ghost walking.");
                player.setGhostWalking(true);
                player.newStance();
                return;
            }
            return;
        }
        if (command[0].equalsIgnoreCase("[cn]")) {
            if (player.getInterfaceId() == 40172) {
                ClanChatManager.setName(player, wholeCommand.substring(wholeCommand.indexOf(" ") + 1));
            }
        }
    }

    private static void memberCommands(final Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("setyelltitle")) {
            try {
                String target = wholeCommand.substring(command[0].length() + 1).toLowerCase();
                if (target.length() > 6 || target.contains("mod") || target.contains("staff") || target.contains("owner") || target.contains("developer") || target.contains("support") || target.contains("m0d")) {
                    player.getPacketSender().sendMessage("Bad title. Max length is 6, title must be appropriate.");
                    return;
                } else {
                    player.setYellTitle(Misc.capitalizeString(target));
                    player.getPacketSender().sendMessage("Your yell title has been set to " + Misc.capitalizeString(target) + ".");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (wholeCommand.toLowerCase().startsWith("yell")) {
            if (PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
                player.getPacketSender().sendMessage("You are muted and cannot yell.");
                return;
            }
            if (PlayerPunishment.Jail.isJailed(player)) {
                player.getPacketSender().sendMessage("You can yell as soon as you're out of jail.");
                return;
            }
            int delay = 1000;
            if (!player.getLastYell().elapsed((delay))) {
                player.getPacketSender().sendMessage("You must wait at least 1 second between every yell-message you send.");
                return;
            }
            String yellMessage = wholeCommand.substring(5, wholeCommand.length());
			if (!Misc.isAcceptableMessage(yellMessage)) {
                player.getPacketSender().sendMessage("Your message could not be sent because of the symbols.");
                System.out.println(player.getUsername()+" Unacceptable yell message.");
                PlayerLogs.log("1 - unhandled text", player.getUsername()+" tried to send yell an unhandled message.");
                return;
			}
            String formatYell = yellMessage.substring(0, 1).toUpperCase() + yellMessage.substring(1).toLowerCase();
            if (Misc.blockedWord(yellMessage) && !(player.getRights().OwnerDeveloperOnly())) {
                DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
                return;
            } else {

                String rights;
                if (player.getYellTitle() != null && !player.getYellTitle().equalsIgnoreCase("null") && !player.getYellTitle().equalsIgnoreCase("")) {
                    rights = player.getYellTitle();
                } else {
                    rights = player.getRights().toString().substring(0, 1).toUpperCase() + player.getRights().toString().substring(1).toLowerCase();
                }

                String toYell = "<shad=0>@yel@[Yell]" + player.getRights().getYellPrefix() + "[" + rights + "] <img=" + player.getGameModeIconId() + "> <img=" + player.getRights().ordinal() + "> <shad=0><col=" + player.getYellHex() + ">" + player.getUsername() + ": " + formatYell;
                if (toYell.length() > 200) {
                    toYell = toYell.substring(0, 200);
                    player.getPacketSender().sendMessage("Your yell was too long. The game has shortened it.");
                }

                World.sendMessage(toYell);

                PlayerLogs.log(player.getUsername(), player.getUsername() + " yelled: " + formatYell);
                player.getLastYell().reset();
            }
        }
		/*if(wholeCommand.equalsIgnoreCase("bank") && player.getAmountDonated() >= 200) {
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			if(player.getLocation() == Location.WILDERNESS || player.getLocation() == Location.DUNGEONEERING || player.getLocation() == Location.DUEL_ARENA) {
				player.getPacketSender().sendMessage("You cannot open your bank here.");
				return;
			}
			player.getBank(player.getCurrentBankTab()).open();
		}*/
        if (wholeCommand.equalsIgnoreCase("crystalchest") || wholeCommand.equalsIgnoreCase("crystalkey")
                || wholeCommand.equalsIgnoreCase("crystal") || wholeCommand.equalsIgnoreCase("ckey")) {
            if (!player.getClickDelay().elapsed(500))
                return;
            if (!player.getInventory().contains(989)) {
                player.getPacketSender().sendMessage("How am I going to do this without a Crystal Key?");
                return;
            }
            CrystalChest.handleChest(player, null, true);
            player.getClickDelay().reset();
        }

		/*

		if (command[0].equalsIgnoreCase("setgear")) {
			player.getPreSetEquipment().setItem(Equipment.HEAD_SLOT, new Item(player.getEquipment().get(Equipment.HEAD_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.CAPE_SLOT, new Item(player.getEquipment().get(Equipment.CAPE_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.AMULET_SLOT, new Item(player.getEquipment().get(Equipment.AMULET_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.WEAPON_SLOT, new Item(player.getEquipment().get(Equipment.WEAPON_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.BODY_SLOT, new Item(player.getEquipment().get(Equipment.BODY_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.SHIELD_SLOT, new Item(player.getEquipment().get(Equipment.SHIELD_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.LEG_SLOT, new Item(player.getEquipment().get(Equipment.LEG_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.HANDS_SLOT, new Item(player.getEquipment().get(Equipment.HANDS_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.RING_SLOT, new Item(player.getEquipment().get(Equipment.RING_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.FEET_SLOT, new Item(player.getEquipment().get(Equipment.FEET_SLOT).getId()));
			player.getPreSetEquipment().setItem(Equipment.AMMUNITION_SLOT, new Item(player.getEquipment().get(Equipment.AMMUNITION_SLOT).getId()));
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "You succesfully saved your gear!");
		}

		if (command[0].equalsIgnoreCase("checkgear")) {
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Helmet: @gre@"+ player.getPreSetEquipment().get(Equipment.HEAD_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Cape: @gre@"+ player.getPreSetEquipment().get(Equipment.CAPE_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Amulet: @gre@"+ player.getPreSetEquipment().get(Equipment.AMULET_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Weapon: @gre@"+ player.getPreSetEquipment().get(Equipment.WEAPON_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Torso: @gre@"+ player.getPreSetEquipment().get(Equipment.BODY_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Shield: @gre@"+ player.getPreSetEquipment().get(Equipment.SHIELD_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Legs: @gre@"+ player.getPreSetEquipment().get(Equipment.LEG_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Hands: @gre@"+ player.getPreSetEquipment().get(Equipment.HANDS_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Ring: @gre@"+ player.getPreSetEquipment().get(Equipment.RING_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Boots: @gre@"+ player.getPreSetEquipment().get(Equipment.FEET_SLOT).getDefinition().getName());
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "Ammunition: @gre@"+ player.getPreSetEquipment().get(Equipment.AMMUNITION_SLOT).getDefinition().getName());
		}
		if (command[0].equalsIgnoreCase("loadgear")) {
			player.getLocation();
			if(player.getLocation() == Location.EDGEVILLE ||
			   player.getLocation() == Location.MEMBER_ZONE){
				boolean canLoad = false;
				Bank playerBank = new Bank(player);
				if(player.getInventory().contains(player.getPreSetEquipment().getItems())){
					player.getInventory().deleteItemSet(player.getPreSetEquipment().getItems());
					canLoad = true;
				}
				if(playerBank.contains(player.getPreSetEquipment().getItems())){
					playerBank.deleteItemSet(player.getPreSetEquipment().getItems());
					canLoad = true;
				}
				if(canLoad){
				player.getEquipment().setItem(Equipment.HEAD_SLOT, new Item(player.getPreSetEquipment().get(Equipment.HEAD_SLOT).getId()));
				player.getEquipment().setItem(Equipment.CAPE_SLOT, new Item(player.getPreSetEquipment().get(Equipment.CAPE_SLOT).getId()));
				player.getEquipment().setItem(Equipment.AMULET_SLOT, new Item(player.getPreSetEquipment().get(Equipment.AMULET_SLOT).getId()));
				player.getEquipment().setItem(Equipment.WEAPON_SLOT, new Item(player.getPreSetEquipment().get(Equipment.WEAPON_SLOT).getId()));
				player.getEquipment().setItem(Equipment.BODY_SLOT, new Item(player.getPreSetEquipment().get(Equipment.BODY_SLOT).getId()));
				player.getEquipment().setItem(Equipment.SHIELD_SLOT, new Item(player.getPreSetEquipment().get(Equipment.SHIELD_SLOT).getId()));
				player.getEquipment().setItem(Equipment.LEG_SLOT, new Item(player.getPreSetEquipment().get(Equipment.LEG_SLOT).getId()));
				player.getEquipment().setItem(Equipment.HANDS_SLOT, new Item(player.getPreSetEquipment().get(Equipment.HANDS_SLOT).getId()));
				player.getEquipment().setItem(Equipment.RING_SLOT, new Item(player.getPreSetEquipment().get(Equipment.RING_SLOT).getId()));
				player.getEquipment().setItem(Equipment.FEET_SLOT, new Item(player.getPreSetEquipment().get(Equipment.FEET_SLOT).getId()));
				player.getEquipment().setItem(Equipment.AMMUNITION_SLOT, new Item(player.getPreSetEquipment().get(Equipment.AMMUNITION_SLOT).getId()));
				BonusManager.update(player);
				WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
				WeaponAnimations.update(player);
				player.getEquipment().refreshItems();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "You succesfully loaded your gear!");
				}
				if(!canLoad){
					player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "@red@You do not have the required items!");
				}
			}
			player.getPacketSender().sendMessage(MessageType.PLAYER_ALERT, "@red@You can only do this in edge/dzone!");
		}*/


        if (command[0].equalsIgnoreCase("dzone") || command[0].equalsIgnoreCase("donorzone") || command[0].equalsIgnoreCase("memberzone") || command[0].equalsIgnoreCase("mzone")) {
            if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS || player.getLocation() == Location.DUNGEONEERING || player.getLocation() == Location.DUEL_ARENA) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            TeleportHandler.teleportPlayer(player, GameSettings.MEMBER_ZONE, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Thanks for supporting " + GameSettings.RSPS_NAME + "!");
        }
        if (command[0].equalsIgnoreCase("decant")) {
            player.getPacketSender().sendMessage("A magic power decants your potions!");
            Decanting.notedDecanting(player);
        }
    }

    private static void contributorCommands(final Player player, String[] command, String wholeCommand) {
        if (wholeCommand.equalsIgnoreCase("hexcodes") || wholeCommand.equalsIgnoreCase("hex")) {
            player.getPacketSender().sendString(1, GameSettings.HexUrl);
            player.getPacketSender().sendMessage("Attempting to open the Hex page..");
        }
        if (command[0].equalsIgnoreCase("getyellhex")) {
            player.getPacketSender().sendMessage("Your current yell hex is: <shad=0><col=" + player.getYellHex() + ">#" + player.getYellHex());
            return;
        }
        if (command[0].equalsIgnoreCase("setyellhex")) {
            String hex = command[1].replaceAll("#", "");
            player.setYellHex(hex);
            player.getPacketSender().sendMessage("You have set your hex color to: <shad=0><col=" + hex + ">#" + hex);
            if (player.getYellHex() == null)
                player.getPacketSender().sendMessage("There was an error setting your yell hex. You entered: " + hex);
            return;
        }
    }

    private static void helperCommands(final Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("isinwild") || command[0].equalsIgnoreCase("checkwild")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick != null) {
                if (playerToKick.getLocation() != null && playerToKick.getLocation() != Location.WILDERNESS) {
                    player.getPacketSender().sendMessage(playerToKick.getUsername() + " @gre@is *NOT* in the Wilderness.");
                } else {
                    player.getPacketSender().sendMessage(playerToKick.getUsername() + " @red@is *IN LEVEL " + playerToKick.getWildernessLevel() + " WILDERNESS*");
                }

            } else {
                player.getPacketSender().sendMessage("Could not find \"" + player2 + "\".");
            }
        }
        if (command[0].equalsIgnoreCase("mute")) {
            try {
                String target = wholeCommand.substring(command[0].length() + 1);
                if (!PlayerSaving.playerExists(target)) {
                    player.getPacketSender().sendMessage("Player: " + target + " does not exist.");
                    return;
                } else {
                    if (PlayerPunishment.muted(target)) {
                        player.getPacketSender().sendMessage("Player: " + target + " already has an active mute.");
                        return;
                    }
                    PlayerLogs.log(player.getUsername(), player.getUsername() + " just muted " + target + "!");
                    World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just muted " + target);
                    PlayerPunishment.mute(target);/*, GameSettings.Temp_Mute_Time);*/
                    player.getPacketSender().sendMessage("Player " + target + " was successfully muted");// for "+GameSettings.Temp_Mute_Time/1000/60/60+" hour(s). Command logs written.");
                    Player plr = World.getPlayerByName(target);
                    if (plr != null) {
                        plr.getPacketSender().sendMessage("You have been muted by " + player.getUsername() + "."); // for "+GameSettings.Temp_Mute_Time/1000/60/60+" hour(s).");
                    }
                }
            } catch (Exception e) {
                player.getPacketSender().sendMessage("The player must be online.");
            }
        }
        if (command[0].equalsIgnoreCase("movehome")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToMove = World.getPlayerByName(player2);
            if (playerToMove != null && playerToMove.getDueling().duelingStatus < 5) {
                if (playerToMove.getLocation() != null && playerToMove.getLocation() == Location.WILDERNESS) {
                    PlayerLogs.log(player.getUsername(), "Just moved " + playerToMove.getUsername() + " to home from level " + playerToMove.getWildernessLevel() + " wild.");
                } else {
                    PlayerLogs.log(player.getUsername(), "Just moved " + playerToMove.getUsername() + " to home.");
                }
                playerToMove.moveTo(GameSettings.DEFAULT_POSITION.copy());
                playerToMove.getPacketSender().sendMessage("You've been teleported home by " + player.getUsername() + ".");
                player.getPacketSender().sendMessage("Sucessfully moved " + playerToMove.getUsername() + " to home.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just moved " + playerToMove.getUsername() + " to home.");
                player.performGraphic(new Graphic(730));
                playerToMove.performGraphic(new Graphic(730));
            } else {
                player.getPacketSender().sendMessage("Could not send \"" + player2 + "\" home. Try kicking first?").sendMessage("Will also fail if they're in duel/wild.");
            }
        }
        if (command[0].equalsIgnoreCase("permmute")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            if (!PlayerSaving.playerExists(player2)) {
                player.getPacketSender().sendMessage("Player " + player2 + " does not exist.");
                return;
            } else {
                if (PlayerPunishment.muted(player2)) {
                    player.getPacketSender().sendMessage("Player " + player2 + " already has an active mute.");
                    return;
                }
                PlayerLogs.log(player.getUsername(), "" + player.getUsername() + " just perm muted " + player2 + "!");
                PlayerPunishment.mute(player2);
                player.getPacketSender().sendMessage("Player " + player2 + " was successfully muted. Command logs written.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just perm muted " + player2);
                Player plr = World.getPlayerByName(player2);
                if (plr != null) {
                    plr.getPacketSender().sendMessage("You have been muted by " + player.getUsername() + ".");
                }
            }
        }

        if (command[0].equalsIgnoreCase("tempmute")) {
            String targ = wholeCommand.substring(command[0].length() + 1);
            Player target = World.getPlayerByName(targ);
            if (target == null) {
                player.getPacketSender().sendMessage("Could not find player \"" + targ + "\".");
                return;
            }
            if (PlayerPunishment.muted(targ)) {
                player.getPacketSender().sendMessage(targ + " is already muted.");
                return;
            }
            PlayerPunishment.tempMute(targ);

            //if (command[2])
        }
        if (wholeCommand.toLowerCase().startsWith("sc")) {
            String yellMessage = wholeCommand.substring(3, wholeCommand.length());
            String formatYell = yellMessage.substring(0, 1).toUpperCase() + yellMessage.substring(1).toLowerCase();
            String rights = player.getRights().toString();
            String rank = rights.substring(0, 1).toUpperCase() + rights.substring(1).toLowerCase();
            //World.sendMessage("<shad=0>@yel@[Yell]"+player.getRights().getYellPrefix()+"["+rank+"]<img="+player.getGameModeIconId()+"><img="+player.getRights().ordinal()+"> <shad=0><col="+player.getYellHex()+">"+player.getUsername()+": "+formatYell);
            World.sendStaffMessage("<shad=F200CE>@red@[SC]<shad=0>" + player.getRights().getYellPrefix() + "[" + rank + "] <img=" + player.getGameModeIconId() + "><img=" + player.getRights().ordinal() + "> <shad=0><col=" + player.getYellHex() + ">" + player.getUsername() + ": " + formatYell);
            PlayerLogs.log(player.getUsername(), player.getUsername() + " SC said: " + formatYell);
        }
        if (command[0].equalsIgnoreCase("newjail")) {
            TeleportHandler.teleportPlayer(player, new Position(2519, 9320), TeleportType.NORMAL);
        }
        if (command[0].equalsIgnoreCase("jail")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1, wholeCommand.length()));
            if (player2 != null) {
                if (Jail.isJailed(player2)) {
                    player.getPacketSender().sendMessage("That player is already jailed!");
                    return;
                }
                if (Jail.jailPlayer(player2) && player2.getDueling().duelingStatus == 0) {
                    player2.getSkillManager().stopSkilling();
                    PlayerLogs.log(player.getUsername(),
                            player.getUsername() + " just jailed " + player2.getUsername() + "!");
                    player.getPacketSender().sendMessage("Jailed player: " + player2.getUsername());
                    player2.getPacketSender().sendMessage("You have been jailed by " + player.getUsername() + ".");
                    World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                            + " just jailed " + player2.getUsername());
                    player2.performAnimation(new Animation(1994));
                    player.performGraphic(new Graphic(730));
                } else {
                    if (player2.getDueling().duelingStatus > 0) {
                        player.getPacketSender().sendMessage(
                                "That player is currently in a stake. Please wait before jailing them, or kick then jail.");
                        return;
                    } else {
                        player.getPacketSender().sendMessage("This shouldn't happen, message Crimson. Error: JAIL45");
                    }
                }
            } else {
                player.getPacketSender().sendMessage("Could not find that player online.");
            }
        }
        if (command[0].equalsIgnoreCase("remindvote")) {
            World.sendMessage("<img=10> <col=008FB2><shad=0>Remember to collect rewards by using the ::vote command every 12 hours!");
        }
        if (command[0].equalsIgnoreCase("unjail")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1, wholeCommand.length()));
            if (player2 != null) {
                Jail.unjail(player2);
                PlayerLogs.log(player.getUsername(), "" + player.getUsername() + " just unjailed " + player2.getUsername() + "!");
                player.getPacketSender().sendMessage("Unjailed player: " + player2.getUsername() + "");
                player2.getPacketSender().sendMessage("You have been unjailed by " + player.getUsername() + ".");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just unjailed " + player2.getUsername());
                player2.performAnimation(new Animation(1993));
                player.performGraphic(new Graphic(730));
            } else {
                player.getPacketSender().sendMessage("Could not find \"" + wholeCommand.substring(command[0].length() + 1, wholeCommand.length()) + "\" online.");
            }
        }
        if (command[0].equalsIgnoreCase("staffzone")) {
            if (command.length > 1 && command[1].equalsIgnoreCase("all") && player.getRights().OwnerDeveloperOnly()) {
                player.getPacketSender().sendMessage("Teleporting all staff to staffzone.");
                for (Player players : World.getPlayers()) {
                    if (players != null) {
                        if (players.getRights().isStaff()) {
                            TeleportHandler.teleportPlayer(players, new Position(1883, 5106), TeleportType.NORMAL);
                        }
                    }
                }
            } else {
                TeleportHandler.teleportPlayer(player, new Position(1883, 5106), TeleportType.NORMAL);
            }
        }
        if (command[0].equalsIgnoreCase("teleto")) {
            String playerToTele = wholeCommand.substring(command[0].length() + 1);
            Player player2 = World.getPlayerByName(playerToTele);
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            } else {
                boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy()) && player.getRegionInstance() == null && player2.getRegionInstance() == null;
                if (canTele) {
                    player.performGraphic(new Graphic(342));
                    TeleportHandler.teleportPlayer(player, player2.getPosition().copy(), TeleportType.NORMAL);
                    player.getPacketSender().sendMessage("Teleporting to player: " + player2.getUsername() + "");
                    player2.performGraphic(new Graphic(730));
                } else
                    player.getPacketSender().sendMessage("You can not teleport to this player at the moment. Minigame maybe?");
            }
        }
    }

    private static void moderatorCommands(final Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("ban")) {
            String playerToBan = wholeCommand.substring(command[0].length() + 1);
            if (!PlayerSaving.playerExists(playerToBan)) {
                player.getPacketSender().sendMessage("Player " + playerToBan + " does not exist.");
                return;
            } else {
                if (PlayerPunishment.banned(playerToBan)) {
                    player.getPacketSender().sendMessage("Player " + playerToBan + " already has an active ban.");
                    return;
                }
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just banned " + playerToBan + "!");
                PlayerPunishment.ban(playerToBan);
                player.getPacketSender().sendMessage("Player " + playerToBan + " was successfully banned. Command logs written.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just banned " + playerToBan + ".");
                Player toBan = World.getPlayerByName(playerToBan);
                if (toBan != null) {
                    World.deregister(toBan);
                }
            }
        }
        if (command[0].equalsIgnoreCase("anim")) {
            int id = Integer.parseInt(command[1]);
            player.performAnimation(new Animation(id));
            player.getPacketSender().sendMessage("Sending animation: " + id);
        }
        if (command[0].equalsIgnoreCase("gfx")) {
            int id = Integer.parseInt(command[1]);
            player.performGraphic(new Graphic(id));
            player.getPacketSender().sendMessage("Sending graphic: " + id);
        }
        if (command[0].equalsIgnoreCase("votecount")) {
            player.getPacketSender().sendMessage("Votecount: " + voteCount + ", with votes announcing every " + GameSettings.Vote_Announcer + " vote.");
        }
        if (command[0].equalsIgnoreCase("addvote")) {
            voteCount++;
            if (voteCount >= GameSettings.Vote_Announcer) {
                World.sendMessage("<img=10><shad=0><col=bb43df>10 more players have just voted! Use ::vote for rewards!");
                voteCount = 0;
            } else {
                player.getPacketSender().sendMessage("<img=10><shad=0><col=bb43df>Thank you for voting and supporting Necrotic!");
            }
            player.getPacketSender().sendMessage("Votecount: " + voteCount + ", with votes announcing every " + GameSettings.Vote_Announcer + " vote.");
        }
        if (command[0].equalsIgnoreCase("unmute")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            if (!PlayerSaving.playerExists(player2)) {
                player.getPacketSender().sendMessage("Player " + player2 + " does not exist.");
                return;
            } else {
                if (!PlayerPunishment.muted(player2)) {
                    player.getPacketSender().sendMessage("Player " + player2 + " is not muted!");
                    return;
                }
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just unmuted " + player2 + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just unmuted " + player2);
                PlayerPunishment.unmute(player2);
                player.getPacketSender().sendMessage("Player " + player2 + " was successfully unmuted. Command logs written.");
                Player plr = World.getPlayerByName(player2);
                if (plr != null) {
                    plr.getPacketSender().sendMessage("You have been unmuted by " + player.getUsername() + ".");
                }
            }
        }
        if (command[0].equalsIgnoreCase("ipmute")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1, wholeCommand.length()));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Could not find that player online.");
                return;
            } else {
                if (PlayerPunishment.IPMuted(player2.getHostAddress())) {
                    player.getPacketSender().sendMessage("Player " + player2.getUsername() + "'s IP is already IPMuted. Command logs written.");
                    return;
                }
                final String mutedIP = player2.getHostAddress();
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just IP Muted " + player2.getUsername() + " on " + mutedIP);
                PlayerPunishment.addMutedIP(mutedIP);
                player.getPacketSender().sendMessage("Player " + player2.getUsername() + " was successfully IPMuted. Command logs written.");
                player2.getPacketSender().sendMessage("You have been IPMuted by " + player.getUsername() + ".");
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just IPMuted " + player2.getUsername() + "!");
            }
        }
        if (command[0].equalsIgnoreCase("teletome")) {
            String playerToTele = wholeCommand.substring(command[0].length() + 1);
            Player player2 = World.getPlayerByName(playerToTele);
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            } else {
                boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy()) && player.getRegionInstance() == null && player2.getRegionInstance() == null;
                if (canTele && player2.getDueling().duelingStatus < 5) {
                    TeleportHandler.teleportPlayer(player2, player.getPosition().copy(), TeleportType.NORMAL);
                    player.performGraphic(new Graphic(730));
                    player.getPacketSender().sendMessage("Teleporting " + player2.getUsername() + " to you.");
                    player2.getPacketSender().sendMessage("You're being teleported to " + player.getUsername() + "...");
                    World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just teleported " + player2.getUsername() + " to them.");
                    player2.performGraphic(new Graphic(342));
                } else
                    player.getPacketSender().sendMessage("You can not teleport that player at the moment. Maybe you or they are in a minigame?").sendMessage("Also will fail if they're in duel/wild.");
            }
        }
        if (command[0].equalsIgnoreCase("movetome")) {
            String playerToTele = wholeCommand.substring(command[0].length() + 1);
            Player player2 = World.getPlayerByName(playerToTele);
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player..");
                return;
            } else {
                boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy()) && player.getRegionInstance() == null && player2.getRegionInstance() == null;
                if (canTele && player2.getDueling().duelingStatus < 5) {
                    player.getPacketSender().sendMessage("Moving player: " + player2.getUsername() + "");
                    player2.getPacketSender().sendMessage("You've been moved to " + player.getUsername());
                    World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just moved " + player2.getUsername() + " to them.");
                    player2.moveTo(player.getPosition().copy());
                    player2.performGraphic(new Graphic(342));
                } else
                    player.getPacketSender().sendMessage("Failed to move player to your coords. Are you or them in a minigame?").sendMessage("Also will fail if they're in duel/wild.");
            }
        }
        if (command[0].equalsIgnoreCase("kick")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick == null) {
                player.getPacketSender().sendMessage("Player " + player2 + " couldn't be found on " + GameSettings.RSPS_NAME + ".");
                return;
            } else if (playerToKick.getDueling().duelingStatus < 5) {
                PlayerHandler.handleLogout(playerToKick, true);
                player.getPacketSender().sendMessage("Kicked " + playerToKick.getUsername() + ".");
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just kicked " + playerToKick.getUsername() + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just kicked " + playerToKick.getUsername() + ".");
                player.getPacketSender().sendMessage("You can try ::kick2 if this command didn't work.");
            } else {
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just tried to kick " + playerToKick.getUsername() + " in an active duel.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just tried to kick " + playerToKick.getUsername() + " in an active duel.");
                player.getPacketSender().sendMessage("You've tried to kick someone in duel arena/wild. Logs written.");
            }
        }
        if (command[0].equalsIgnoreCase("kick2")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick == null) {
                player.getPacketSender().sendMessage("Player " + player2 + " couldn't be found on " + GameSettings.RSPS_NAME + ".");
                return;
            } else if (playerToKick.getDueling().duelingStatus < 5) {
                World.getPlayers().remove(playerToKick);
                player.getPacketSender().sendMessage("Kicked " + playerToKick.getUsername() + ".");
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just kicked " + playerToKick.getUsername() + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just kicked " + playerToKick.getUsername() + ".");
            } else {
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just tried to kick " + playerToKick.getUsername() + " in an active duel.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just tried to kick " + playerToKick.getUsername() + " in an active duel.");
                player.getPacketSender().sendMessage("You've tried to kick someone in duel arena/wild. Logs written.");
            }
        }
    }


    private static void administratorCommands(final Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("clicktele")) {
            player.setClickToTeleport(!player.isClickToTeleport());
            player.getPacketSender().sendMessage("Click to teleport set to: " + player.isClickToTeleport() + ".");
        }
        if (command[0].equalsIgnoreCase("sd")) {
            if (player.getUsername().equalsIgnoreCase("Sesshomaru") || player.getUsername().equalsIgnoreCase("Higurashi")) {
                player.getPacketSender().sendMessage("Do not use your main account for testing difficulties.");
                return;
            }
            try {
                if (command[1].equalsIgnoreCase("1")) {
                    Difficulty.set(player, Difficulty.FUN, true);
                } else if (command[1].equalsIgnoreCase("2")) {
                    Difficulty.set(player, Difficulty.EASY, true);
                } else if (command[1].equalsIgnoreCase("3")) {
                    Difficulty.set(player, Difficulty.REGULAR, true);
                } else if (command[1].equalsIgnoreCase("4")) {
                    Difficulty.set(player, Difficulty.HARD, true);
                } else if (command[1].equalsIgnoreCase("5")) {
                    Difficulty.set(player, Difficulty.EXTREME, true);
                } else {
                    player.getPacketSender().sendMessage("Did not understand.");
                    player.getPacketSender().sendMessage("Your current gamemode is: " + player.getDifficulty().toString().toLowerCase());
                    return;
                }
                player.getPacketSender().sendMessage("You have set your difficulty to: " + player.getDifficulty().toString().toLowerCase());
            } catch (Exception e) {
                player.getPacketSender().sendMessage("Invalid syntax; ::sd [1-5]");
            }
        }
        if (command[0].equalsIgnoreCase("pos")) {
            player.getPacketSender().sendMessage(player.getPosition().toString());
        }
        if (command[0].equalsIgnoreCase("getpos")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Could not find that player online.");
                return;
            } else {
                player.getPacketSender().sendMessage("[@red@" + player2.getUsername() + "@bla@] " + player2.getPosition().toString() + " @red@| @bla@Location: " + player2.getLocation());
            }
        }
        if (command[0].equalsIgnoreCase("wpos")) {
            System.out.println(player.getPosition().toString());
        }
        if (command[0].equalsIgnoreCase("tele")) {
            int x = Integer.valueOf(command[1]), y = Integer.valueOf(command[2]);
            int z = player.getPosition().getZ();
            if (command.length > 3)
                z = Integer.valueOf(command[3]);
            Position position = new Position(x, y, z);
            player.moveTo(position);
            player.getPacketSender().sendMessage("Teleporting to " + position.toString());
        }
        if (command[0].equalsIgnoreCase("isob")) {
            player.getPacketSender().sendMessage("Are you on a custom object? " + CustomObjects.objectExists(player.getPosition().copy()));
        }
        if (command[0].equalsIgnoreCase("ipban")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Could not find that player online.");
                return;
            } else {
                if (PlayerPunishment.IPBanned(player2.getHostAddress())) {
                    player.getPacketSender().sendMessage("Player " + player2.getUsername() + "'s IP is already banned.");
                    return;
                }
                final String bannedIP = player2.getHostAddress();
                PlayerPunishment.ban(player2.getUsername());
                PlayerPunishment.addBannedIP(bannedIP);
                player.getPacketSender().sendMessage("Player " + player2.getUsername() + "'s IP was successfully banned. Command logs written.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just IP Banned " + player2.getUsername());
                for (Player playersToBan : World.getPlayers()) {
                    if (playersToBan == null)
                        continue;
                    if (playersToBan.getHostAddress() == bannedIP) {
                        PlayerLogs.log(player.getUsername(), "" + player.getUsername() + " just IPBanned " + playersToBan.getUsername() + "!");
                        PlayerPunishment.ban(playersToBan.getUsername());
                        World.deregister(playersToBan);
                        if (player2.getUsername() != playersToBan.getUsername())
                            player.getPacketSender().sendMessage("Player " + playersToBan.getUsername() + " was successfully IPBanned. Command logs written.");
                    }
                }
            }
        }
        if (command[0].equalsIgnoreCase("unipmute")) {
            player.getPacketSender().sendMessage("Unipmutes can only be handled manually.");
        }
        if (command[0].equalsIgnoreCase("trio")) {
            Position position = GameSettings.TRIO_CORDS;
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you to trio!");
        }
        if (command[0].equalsIgnoreCase("KFC")) {
            Position position = GameSettings.KFC_CORDS;
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you to KFC!");
        }
        if (command[0].equalsIgnoreCase("unban")) {
            String playerToBan = wholeCommand.substring(6);
            if (!PlayerSaving.playerExists(playerToBan)) {
                player.getPacketSender().sendMessage("Player " + playerToBan + " does not exist.");
                return;
            } else {
                if (!PlayerPunishment.banned(playerToBan)) {
                    player.getPacketSender().sendMessage("Player " + playerToBan + " is not banned!");
                    return;
                }
                PlayerLogs.log(player.getUsername(), "" + player.getUsername() + " just unbanned " + playerToBan + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just unbanned " + playerToBan + ".");
                PlayerPunishment.unban(playerToBan);
                player.getPacketSender().sendMessage("Player " + playerToBan + " was successfully unbanned. Command logs written.");
            }
        }


        if (command[0].equalsIgnoreCase("host")) {
            String plr = wholeCommand.substring(command[0].length() + 1);
            Player playr2 = World.getPlayerByName(plr);
            if (playr2 != null) {
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just tried to check " + playr2.getUsername() + "'s IP address.");
                if (player.getRights().equals(PlayerRights.MODERATOR) && playr2.getRights().equals(PlayerRights.ADMINISTRATOR) || playr2.getRights().equals(PlayerRights.OWNER) || playr2.getRights().equals(PlayerRights.DEVELOPER)) {
                    player.getPacketSender().sendMessage(playr2.getUsername() + " is a higher rank than you. You can't resolve their IP.");
                    return;
                } else if (player.getRights().equals(PlayerRights.ADMINISTRATOR) && playr2.getRights().equals(PlayerRights.OWNER) || playr2.getRights().equals(PlayerRights.DEVELOPER)) {
                    player.getPacketSender().sendMessage(playr2.getUsername() + " is a higher rank than you. You can't resolve their IP.");
                    return;
                }
                player.getPacketSender().sendMessage(playr2.getUsername() + " ip: " + playr2.getHostAddress() + ", serial #: " + playr2.getSerialNumber());
                player.getPacketSender().sendString(1, "www.ipaddressden.com/ip/" + playr2.getHostAddress() + ".html"); //http://www.ipaddressden.com/ip/192.168.0.1.html
            } else
                player.getPacketSender().sendMessage("Could not find player: " + plr);
        }
        if (command[0].equalsIgnoreCase("checkgold")) {
            Player p = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (p != null) {
                long gold = 0;
                for (Item item : p.getInventory().getItems()) {
                    if (item != null && item.getId() > 0 && item.tradeable())
                        gold += item.getDefinition().getValue();
                }
                for (Item item : p.getEquipment().getItems()) {
                    if (item != null && item.getId() > 0 && item.tradeable())
                        gold += item.getDefinition().getValue();
                }
                for (int i = 0; i < 9; i++) {
                    for (Item item : p.getBank(i).getItems()) {
                        if (item != null && item.getId() > 0 && item.tradeable())
                            gold += item.getDefinition().getValue();
                    }
                }
                gold += p.getMoneyInPouch();
                player.getPacketSender().sendMessage(p.getUsername() + " has " + Misc.insertCommasToNumber(String.valueOf(gold)) + " coins.");
            } else
                player.getPacketSender().sendMessage("Can not find player online.");
        }

        // start brandon random debug commands kek

        if (command[0].equalsIgnoreCase("bpcharges")) {
            player.getPacketSender().sendMessage(player.getBlowpipeCharges() + " charges.");
        }
        if (command[0].equalsIgnoreCase("bptest")) {
            player.getPacketSender().sendMessage("loadPipe is: " + ToxicBlowpipe.loadPipe(player));
            player.getPacketSender().sendMessage("pipe charges: " + player.getBlowpipeCharges());
            return;
        }
        if (command[0].equalsIgnoreCase("rbp")) {
            //player.getPacketSender().sendMessage("loadPipe is: " + ToxicBlowpipe.loadPipe(player, 806, 12926));
            player.getPacketSender().sendMessage("pipe charges: " + player.setBlowpipeCharges(1));
            return;
        }


    }


    private static void ownerCommands(final Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("listuntradeables")) {
            Misc.listUntradeables();
        }
        if (command[0].equalsIgnoreCase("roll")) {
            if (player.getClanChatName() == null) {
                player.getPacketSender().sendMessage("You need to be in a clanchat channel to roll a dice.");
                return;
            } else if (player.getClanChatName().equalsIgnoreCase("help")) {
                player.getPacketSender().sendMessage("You can't roll a dice in this clanchat channel!");
                return;
            } else if (player.getClanChatName().equalsIgnoreCase("necrotic")) {
                player.getPacketSender().sendMessage("You can't roll a dice in this clanchat channel!");
                return;
            }
            int dice = Integer.parseInt(command[1]);
            player.getMovementQueue().reset();
            player.performAnimation(new Animation(11900));
            player.performGraphic(new Graphic(2075));
            ClanChatManager.sendMessage(player.getCurrentClanChat(), "@bla@[ClanChat] @whi@" + player.getUsername() + " just rolled @bla@" + dice + "@whi@ on the percentile dice.");
        }
        if (command[0].equalsIgnoreCase("dc")) {
            String msg = "";
            for (int i = 1; i < command.length; i++) {
                msg += command[i] + " ";
            }
            DiscordMessager.test(Misc.stripIngameFormat(msg));
            player.getPacketSender().sendMessage("Sent: " + wholeCommand.substring(command[0].length() + 1));
        }
        if (command[0].equalsIgnoreCase("resetny")) {
            player.setNewYear2017(0);
            player.getPacketSender().sendMessage("Set setNewYear2017 to: " + player.getNewYear2017());
        }
        if (command[0].equalsIgnoreCase("xmascount")) {
            player.getPacketSender().sendMessage("xmas count; " + player.getChristmas2016());
        }
        if (command[0].equalsIgnoreCase("resetxmas")) {
            player.setchristmas2016(0);
        }
        if (command[0].equalsIgnoreCase("christmas")) {
            //christmas2016.announceChristmas();
            System.out.println(christmas2016.isChristmas());
        }
        if (command[0].equalsIgnoreCase("di") && command[1] != null) {
            NPCDrops.sendDropTableInterface(player, Integer.parseInt(command[1]));
        }
        if (command[0].equalsIgnoreCase("olddrops") && command[1] != null) {
            NPCDrops.getDropTable(player, Integer.parseInt(command[1]));
        }
        if (command[0].equalsIgnoreCase("setxmas") && command[1] != null) {
            player.setchristmas2016(Integer.parseInt(command[1]));
            player.getPacketSender().sendMessage("Set Christmas2016 to " + player.getChristmas2016());
        }
        if (command[0].equalsIgnoreCase("easteri")) {
            easter2017.openInterface(player);
        }
        if (command[0].equalsIgnoreCase("easterc")) {
            player.getPacketSender().sendMessage("easter status: " + player.getEaster2017());
        }
        if (command[0].equalsIgnoreCase("seteaster")) {
            int inty = Integer.parseInt(command[1]);
            player.setEaster2017(inty);
            player.getPacketSender().sendMessage("Set your easter to: " + inty);
        }
        if (command[0].equalsIgnoreCase("item")) {
            int id = Integer.parseInt(command[1]);
            if (id > ItemDefinition.getMaxAmountOfItems()) {
                player.getPacketSender().sendMessage("Invalid item id entered. Max amount of items: " + ItemDefinition.getMaxAmountOfItems());
                return;
            }
            int amount = (command.length == 2 ? 1 : Integer.parseInt(command[2].trim().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000")));
            if (amount > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE;
            }
            Item item = new Item(id, amount);
            player.getInventory().add(item, true);
        }
        if (command[0].equalsIgnoreCase("giveitem")) {
            int id = Integer.parseInt(command[1]);
            int amount = Integer.parseInt(command[2]);
            String plrName = wholeCommand.substring(command[0].length() + command[1].length() + command[2].length() + 3);
            Player target = World.getPlayerByName(plrName);
            if (target == null) {
                player.getPacketSender().sendMessage(plrName + " must be online to give them stuff!");
            } else {
                target.getInventory().add(id, amount);
                player.getPacketSender().sendMessage("Gave " + amount + "x " + ItemDefinition.forId(id).getName() + " to " + plrName + ".");
            }
        }
        if (command[0].equalsIgnoreCase("thieving")) {
            int lvl = Integer.parseInt(command[1]);
            player.getSkillManager().setMaxLevel(Skill.THIEVING, lvl);
            player.getSkillManager().setCurrentLevel(Skill.THIEVING, lvl);
            player.getPacketSender().sendMessage("Set your Thieving level to " + lvl + ".");
        }
        if (command[0].equalsIgnoreCase("master")) {
            for (Skill skill : Skill.values()) {
                int level = SkillManager.getMaxAchievingLevel(skill);
                player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level == 120 ? 120 : 99));
            }
            player.getPacketSender().sendMessage("You are now a master of all skills.");
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (command[0].equalsIgnoreCase("matrix")) {
            TeleportInterface.open(player);
			/* player.getPacketSender().sendInterfaceRemoval();
			player.getPacketSender().sendInterface(13999);*/
        }
        if (command[0].equalsIgnoreCase("reset")) {
            for (Skill skill : Skill.values()) {
                int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
                player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
            }
            player.getPacketSender().sendMessage("Your skill levels have now been reset.");
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (command[0].equalsIgnoreCase("rights")) {
            int rankId = Integer.parseInt(command[1]);
            if (player.getUsername().equalsIgnoreCase("server") && rankId != 10) {
                player.getPacketSender().sendMessage("You cannot do that.");
                return;
            }
            // wholeCommand.substring(command[0].length()+2+rankId.length);
            Player target = World.getPlayerByName(wholeCommand.substring(command[0].length() + command[1].length() + 2));
            if (target == null) {
                player.getPacketSender().sendMessage("Player must be online to give them rights!");
            } else {
                target.setRights(PlayerRights.forId(rankId));
                target.getPacketSender().sendMessage("Your player rights have been changed.");
                target.getPacketSender().sendRights();
            }
            //}
        }
        if (command[0].equalsIgnoreCase("setlevel")) {
            int skillId = Integer.parseInt(command[1]);
            int level = Integer.parseInt(command[2]);
            if (level > 15000) {
                player.getPacketSender().sendMessage("You can only have a maxmium level of 15000.");
                return;
            }
            Skill skill = Skill.forId(skillId);
            player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level));
            player.getPacketSender().sendMessage("You have set your " + skill.getName() + " level to " + level);
        }
        if (wholeCommand.toLowerCase().startsWith("yell") && player.getRights() == PlayerRights.PLAYER) {
            player.getPacketSender().sendMessage("Only contibutor+ can yell. To become one, simply use ::store, buy a scroll").sendMessage("and then claim it.");
        }
        if (command[0].equalsIgnoreCase("pure")) {
            int[][] data =
                    new int[][]{
                            {Equipment.HEAD_SLOT, 1153},
                            {Equipment.CAPE_SLOT, 10499},
                            {Equipment.AMULET_SLOT, 1725},
                            {Equipment.WEAPON_SLOT, 4587},
                            {Equipment.BODY_SLOT, 1129},
                            {Equipment.SHIELD_SLOT, 1540},
                            {Equipment.LEG_SLOT, 2497},
                            {Equipment.HANDS_SLOT, 7459},
                            {Equipment.FEET_SLOT, 3105},
                            {Equipment.RING_SLOT, 2550},
                            {Equipment.AMMUNITION_SLOT, 9244}
                    };
            for (int i = 0; i < data.length; i++) {
                int slot = data[i][0], id = data[i][1];
                player.getEquipment().setItem(slot, new Item(id, id == 9244 ? 500 : 1));
            }
            BonusManager.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            WeaponAnimations.update(player);
            player.getEquipment().refreshItems();
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getInventory().resetItems();
            player.getInventory().add(1216, 1000).add(9186, 1000).add(862, 1000).add(892, 10000).add(4154, 5000).add(2437, 1000).add(2441, 1000).add(2445, 1000).add(386, 1000).add(2435, 1000);
            player.getSkillManager().newSkillManager();
            player.getSkillManager().setMaxLevel(Skill.ATTACK, 60).setMaxLevel(Skill.STRENGTH, 85).setMaxLevel(Skill.RANGED, 85).setMaxLevel(Skill.PRAYER, 520).setMaxLevel(Skill.MAGIC, 70).setMaxLevel(Skill.CONSTITUTION, 850);
            for (Skill skill : Skill.values()) {
                player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill)).setExperience(skill, SkillManager.getExperienceForLevel(player.getSkillManager().getMaxLevel(skill)));
            }
        }
        if (command[0].equalsIgnoreCase("emptyitem")) {
            if (player.getInterfaceId() > 0 || player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            int item = Integer.parseInt(command[1]);
            int itemAmount = player.getInventory().getAmount(item);
            Item itemToDelete = new Item(item, itemAmount);
            player.getInventory().delete(itemToDelete).refreshItems();
        }
        if (command[0].equalsIgnoreCase("prayer") || command[0].equalsIgnoreCase("pray")) {
            player.getSkillManager().setCurrentLevel(Skill.PRAYER, 15000);
        }
        if (command[0].equalsIgnoreCase("zulrah")) {
            TeleportHandler.teleportPlayer(player, new Position(3406, 2794, 0), player.getSpellbook().getTeleportType());
            //player.getPacketSender().sendMessage("Old cords: 3363, 3807");
        }
        if (command[0].equalsIgnoreCase("cashineco")) {
            int gold = 0, plrLoops = 0;
            for (Player p : World.getPlayers()) {
                if (p != null) {
                    for (Item item : p.getInventory().getItems()) {
                        if (item != null && item.getId() > 0 && item.tradeable())
                            gold += item.getDefinition().getValue();
                    }
                    for (Item item : p.getEquipment().getItems()) {
                        if (item != null && item.getId() > 0 && item.tradeable())
                            gold += item.getDefinition().getValue();
                    }
                    for (int i = 0; i < 9; i++) {
                        for (Item item : player.getBank(i).getItems()) {
                            if (item != null && item.getId() > 0 && item.tradeable())
                                gold += item.getDefinition().getValue();
                        }
                    }
                    gold += p.getMoneyInPouch();
                    plrLoops++;
                }
            }
            player.getPacketSender().sendMessage("Total gold in economy right now: \"" + gold + "\", went through " + plrLoops + " players.");
        }
        if (command[0].equalsIgnoreCase("bank")) {
            player.getBank(player.getCurrentBankTab()).open();
        }
        if (command[0].equalsIgnoreCase("findnpc")) {
            String name = wholeCommand.substring(command[0].length() + 1);
            player.getPacketSender().sendMessage("Finding item id for item - " + name);
            boolean found = false;
            for (int i = 0; i < NpcDefinition.getDefinitions().length; i++) {
                if (NpcDefinition.forId(i) == null || NpcDefinition.forId(i).getName() == null) {
                    continue;
                }
                if (NpcDefinition.forId(i).getName().toLowerCase().contains(name)) {
                    player.getPacketSender().sendMessage("Found NPC with name [" + NpcDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
                    found = true;
                }
            }
            if (!found) {
                player.getPacketSender().sendMessage("No NPC with name [" + name + "] has been found!");
            }
        }
        if (command[0].equalsIgnoreCase("find") || command[0].equalsIgnoreCase("fi")) {
            String name = wholeCommand.substring(command[0].length() + 1).toLowerCase().replaceAll("_", " ");
            player.getPacketSender().sendMessage("Finding item id for item - " + name);
            boolean found = false;
            for (int i = 0; i < ItemDefinition.getMaxAmountOfItems(); i++) {
                if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
                    player.getPacketSender().sendMessage("Found item with name [" + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
                    found = true;
                }
            }
            if (!found) {
                player.getPacketSender().sendMessage("No item with name [" + name + "] has been found!");
            }
        } else if (command[0].equalsIgnoreCase("id")) {
            String name = wholeCommand.substring(3).toLowerCase().replaceAll("_", " ");
            player.getPacketSender().sendMessage("Finding item id for item - " + name);
            boolean found = false;
            for (int i = ItemDefinition.getMaxAmountOfItems() - 1; i > 0; i--) {
                if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
                    player.getPacketSender().sendMessage("Found item with name [" + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
                    found = true;
                }
            }
            if (!found) {
                player.getPacketSender().sendMessage("No item with name [" + name + "] has been found!");
            }
        }
        if (command[0].equalsIgnoreCase("spec")) {
            player.setSpecialPercentage(15000);
            CombatSpecial.updateBar(player);
        }
        if (command[0].equalsIgnoreCase("jewel")) {
            Jewelry.jewelryInterface(player);
        }
        if (command[0].equalsIgnoreCase("jint")) {
            player.getPacketSender().sendInterface(4161);
        }
        if (command[0].equalsIgnoreCase("sendstring")) {
            player.getPacketSender().sendMessage("::sendstring id text");
            if (command.length >= 3 && Integer.parseInt(command[1]) <= Integer.MAX_VALUE) {
                int id = Integer.parseInt(command[1]);
                String text = wholeCommand.substring(command[0].length() + command[1].length() + 2);
                player.getPacketSender().sendString(Integer.parseInt(command[1]), text);
                player.getPacketSender().sendMessage("Sent \"" + text + "\" to: " + id);
            }
        }
        if (command[0].equalsIgnoreCase("sendteststring")) {
            player.getPacketSender().sendMessage("sendstring syntax: id");
            if (command.length == 2 && Integer.parseInt(command[1]) <= Integer.MAX_VALUE) {
                player.getPacketSender().sendString(Integer.parseInt(command[1]), "TEST STRING");
                player.getPacketSender().sendMessage("Sent \"TEST STRING\" to " + Integer.parseInt(command[1]));
            }
        }
        if (command[0].equalsIgnoreCase("senditemoninterface")) {
            player.getPacketSender().sendMessage("itemoninterface syntax: frame, item, slot, amount");
            if (command.length == 5 && Integer.parseInt(command[4]) <= Integer.MAX_VALUE) {
                player.getPacketSender().sendMessage("Sent the following: " + Integer.parseInt(command[1]) + " " + Integer.parseInt(command[2]) + " "
                        + "" + Integer.parseInt(command[3]) + " " + Integer.parseInt(command[4]));
            }
        }
        if (command[0].equalsIgnoreCase("sendinterfacemodel")) {
            player.getPacketSender().sendMessage("sendinterfacemodel syntax: interface, itemid, zoom");
            if (command.length == 4 && Integer.parseInt(command[3]) <= Integer.MAX_VALUE) {
                player.getPacketSender().sendInterfaceModel(Integer.parseInt(command[1]), Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                player.getPacketSender().sendMessage("Sent the following: " + Integer.parseInt(command[1]) + " " + Integer.parseInt(command[2]) + " "
                        + "" + Integer.parseInt(command[3]));
            }
        }
        if (command[0].equalsIgnoreCase("ancients") || command[0].equalsIgnoreCase("ancient")) {
            player.setSpellbook(MagicSpellbook.ANCIENT);
            player.performAnimation(new Animation(645));
            player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");
            Autocasting.resetAutocast(player, true);
        }
        if (command[0].equalsIgnoreCase("lunar") || command[0].equalsIgnoreCase("lunars")) {
            player.setSpellbook(MagicSpellbook.LUNAR);
            player.performAnimation(new Animation(645));
            player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");
            Autocasting.resetAutocast(player, true);
        }
        if (command[0].equalsIgnoreCase("regular") || command[0].equalsIgnoreCase("normal")) {
            player.setSpellbook(MagicSpellbook.NORMAL);
            player.performAnimation(new Animation(645));
            player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId()).sendMessage("Your magic spellbook is changed..");
            Autocasting.resetAutocast(player, true);
        }
        if (command[0].equalsIgnoreCase("curses")) {
            player.performAnimation(new Animation(645));
            if (player.getPrayerbook() == Prayerbook.NORMAL) {
                player.getPacketSender().sendMessage("You sense a surge of power flow through your body!");
                player.setPrayerbook(Prayerbook.CURSES);
            } else {
                player.getPacketSender().sendMessage("You sense a surge of purity flow through your body!");
                player.setPrayerbook(Prayerbook.NORMAL);
            }
            player.getPacketSender().sendTabInterface(GameSettings.PRAYER_TAB, player.getPrayerbook().getInterfaceId());
            PrayerHandler.deactivateAll(player);
            CurseHandler.deactivateAll(player);
        }
        if (command[0].equalsIgnoreCase("dropi")) {
            //String search = wholeCommand.substring(command[0].length()+1);
            DropsInterface.open(player);
            player.getPacketSender().sendMessage("Sent drop interface.");
        }
        if (command[0].equalsIgnoreCase("tdropi")) {
            String search = wholeCommand.substring(command[0].length() + 1);
            DropsInterface.getList(search);
        }
        if (command[0].equalsIgnoreCase("rt")) {
            player.getSlayer().resetSlayerTask();
        }
        if (command[0].equalsIgnoreCase("bcr")) {
            player.getPacketSender().sendMessage("needsNewSalt ? " + Misc.needsNewSalt(player.getSalt()));
        }
        if (command[0].equalsIgnoreCase("god")) {
            player.setSpecialPercentage(15000);
            CombatSpecial.updateBar(player);
            player.getSkillManager().setCurrentLevel(Skill.PRAYER, 150000);
            player.getSkillManager().setCurrentLevel(Skill.ATTACK, 15000);
            player.getSkillManager().setCurrentLevel(Skill.STRENGTH, 15000);
            player.getSkillManager().setCurrentLevel(Skill.DEFENCE, 15000);
            player.getSkillManager().setCurrentLevel(Skill.RANGED, 15000);
            player.getSkillManager().setCurrentLevel(Skill.MAGIC, 15000);
            player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 150000);
            player.getSkillManager().setCurrentLevel(Skill.SUMMONING, 15000);
            player.setHasVengeance(true);
            player.performAnimation(new Animation(725));
            player.performGraphic(new Graphic(1555));
            player.getPacketSender().sendMessage("You're a god, and everyone knows it.");
        }
        if (command[0].equalsIgnoreCase("getanim")) {
            player.getPacketSender().sendMessage("Your last animation ID is: " + player.getAnimation().getId());
        }
        if (command[0].equalsIgnoreCase("getgfx")) {
            player.getPacketSender().sendMessage("Your last graphic ID is: " + player.getGraphic().getId());
        }
        if (command[0].equalsIgnoreCase("vengrunes")) {
            player.setHasVengeance(true);
            player.getInventory().add(new Item(560, 1000000)).add(new Item(9075, 1000000)).add(new Item(557, 1000000));
            player.getPacketSender().sendMessage("You cast Vengeance").sendMessage("You get some Vengeance runes.");
        }
        if (command[0].equalsIgnoreCase("veng")) {
            player.setHasVengeance(true);
            player.performAnimation(new Animation(4410));
            player.performGraphic(new Graphic(726));
            player.getPacketSender().sendMessage("You cast Vengeance.");
        }
        if (command[0].equalsIgnoreCase("barragerunes") || command[0].equalsIgnoreCase("barrage")) {
            player.getInventory().add(new Item(565, 1000000)).add(new Item(560, 1000000)).add(new Item(555, 1000000));
            player.getPacketSender().sendMessage("You get some Ice Barrage runes.");
        }
        if (command[0].equalsIgnoreCase("ungod")) {
            player.setSpecialPercentage(100);
            CombatSpecial.updateBar(player);
            player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER));
            player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getMaxLevel(Skill.ATTACK));
            player.getSkillManager().setCurrentLevel(Skill.STRENGTH, player.getSkillManager().getMaxLevel(Skill.STRENGTH));
            player.getSkillManager().setCurrentLevel(Skill.DEFENCE, player.getSkillManager().getMaxLevel(Skill.DEFENCE));
            player.getSkillManager().setCurrentLevel(Skill.RANGED, player.getSkillManager().getMaxLevel(Skill.RANGED));
            player.getSkillManager().setCurrentLevel(Skill.MAGIC, player.getSkillManager().getMaxLevel(Skill.MAGIC));
            player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
            player.getSkillManager().setCurrentLevel(Skill.SUMMONING, player.getSkillManager().getMaxLevel(Skill.SUMMONING));
            player.setSpecialPercentage(100);
            player.setHasVengeance(false);
            player.performAnimation(new Animation(860));
            player.getPacketSender().sendMessage("You cool down, and forfeit god mode.");
        }
        if (command[0].equalsIgnoreCase("todoom")) {
            player.moveTo(new Position(2321, 5227, 0));
            player.getPacketSender().sendMessage("Moved you to doom.");
        }
        if (command[0].equalsIgnoreCase("doomspawn") || command[0].equalsIgnoreCase("spawndoom")) {
            Doom.spawnMonsters(player);
            player.getPacketSender().sendMessage("Done spawning doom shit");
        }
        if (command[0].equalsIgnoreCase("runes")) {
            for (Item t : ShopManager.getShops().get(0).getItems()) {
                if (t != null) {
                    player.getInventory().add(new Item(t.getId(), 200000));
                }
            }
        }
        if (wholeCommand.equalsIgnoreCase("afk")) {
            World.sendMessage("<img=10> <col=FF0000><shad=0>" + player.getUsername() + ": I am now away, please don't message me; I won't reply.");
        }
        if (command[0].equalsIgnoreCase("isduel") || command[0].equalsIgnoreCase("checkduel")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick != null) {
                if (playerToKick.getDueling().duelingStatus == 0) {
                    player.getPacketSender().sendMessage(playerToKick.getUsername() + " is not dueling.");
                } else {
                    if (playerToKick.getDueling().duelingStatus == 1) {
                        player.getPacketSender()
                                .sendMessage(playerToKick.getUsername() + " has opened the first duel interface with "
                                        + playerToKick.getDueling().getDuelOpponent() + ".");
                    } else {
                        if (playerToKick.getDueling().duelingStatus == 2) {
                            player.getPacketSender()
                                    .sendMessage(playerToKick.getUsername() + " has accepted the first screen, and is waiting for "
                                            + playerToKick.getDueling().getDuelOpponent() + " to confirm.");
                        } else {
                            if (playerToKick.getDueling().duelingStatus == 3) {
                                player.getPacketSender()
                                        .sendMessage(playerToKick.getUsername() + " and their opponent, "
                                                + playerToKick.getDueling().getDuelOpponent()
                                                + " are in the final confirmation screen.");
                            } else {
                                if (playerToKick.getDueling().duelingStatus == 4) {
                                    player.getPacketSender()
                                            .sendMessage(playerToKick.getUsername() + "  has confirmed the second, and is waiting for their opponent, "
                                                    + playerToKick.getDueling().getDuelOpponent()
                                                    + ".");
                                } else {
                                    if (playerToKick.getDueling().duelingStatus == 5) {
                                        player.getPacketSender()
                                                .sendMessage(playerToKick.getUsername() + " is currently in the arena with their opponent, "
                                                        + playerToKick.getDueling().getDuelOpponent()
                                                        + ".");
                                    } else {
                                        if (playerToKick.getDueling().duelingStatus == 6) {
                                            player.getPacketSender().sendMessage(
                                                    playerToKick.getUsername() + " has just declined a duel request.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                player.getPacketSender().sendMessage("Could not find `" + command[1] + "`... Typo/offline?");
            }
        }
        if (command[0].equalsIgnoreCase("buff")) {
            String playertarget = wholeCommand.substring(command[0].length() + 1);
            Player player2 = World.getPlayerByName(playertarget);
            if (player2 != null) {
                player2.getSkillManager().setCurrentLevel(Skill.ATTACK, 1000);
                player2.getSkillManager().setCurrentLevel(Skill.DEFENCE, 1000);
                player2.getSkillManager().setCurrentLevel(Skill.STRENGTH, 1000);
                player2.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 149000);
                player.getPacketSender().sendMessage("We've buffed " + player2.getUsername() + "'s attack, def, and str to 1000.");
                World.sendOwnerDevMessage("@red@<img=3><img=4> [OWN/DEV]<col=6600FF> " + player.getUsername() + " just buffed " + player2.getUsername() + "'s stats.");
            } else {
                player.getPacketSender().sendMessage("Invalid player... We could not find \"" + playertarget + "\"...");
            }
        }
        if (command[0].equalsIgnoreCase("update")) {
            int time = Integer.parseInt(command[1]);
            if (time > 0) {
                GameServer.setUpdating(true);
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername() + " just started an update in " + time + " ticks.");
                //DiscordMessager.sendDebugMessage(player.getUsername()+" has queued an update, we will be going down in "+time+" seconds.");
                for (Player players : World.getPlayers()) {
                    if (players == null)
                        continue;
                    players.getPacketSender().sendSystemUpdate(time);
                }
                TaskManager.submit(new Task(time) {
                    @Override
                    protected void execute() {
                        for (Player player : World.getPlayers()) {
                            if (player != null) {
                                World.deregister(player);
                            }
                        }
                        WellOfGoodwill.save();
                        GrandExchangeOffers.save();
                        ClanChatManager.save();
                        GameServer.getLogger().info("Update task finished!");
                        //DiscordMessager.sendDebugMessage("The server has gone offline, pending an update.");
                        stop();
                    }
                });
            }
        }
    }


    private static void developerCommands(Player player, String command[], String wholeCommand) {
        if (wholeCommand.contains("potup")) {
            player.getSkillManager().setCurrentLevel(Skill.ATTACK, 118);
            player.getSkillManager().setCurrentLevel(Skill.STRENGTH, 118);
            player.getSkillManager().setCurrentLevel(Skill.DEFENCE, 118);
            player.getSkillManager().setCurrentLevel(Skill.RANGED, 114);
            player.getSkillManager().setCurrentLevel(Skill.MAGIC, 110);
            player.setHasVengeance(true);
            player.getPacketSender().sendMessage("<shad=330099>You now have Vengeance's effect.");
        }
        if (wholeCommand.startsWith("delvp")) {
            Player p2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + command[1].length() + 2));
            int amt = Integer.parseInt(command[1]);
            if (p2 != null) {
                p2.getPointsHandler().setVotingPoints(-amt, true);
                player.getPacketSender().sendMessage("Deleted " + amt + " vote points from " + p2.getUsername());
                PlayerPanel.refreshPanel(p2);
            }
        }
        if (wholeCommand.contains("poh")) {
            Construction.buyHouse(player); //If player doesn't have a house > make one
            Construction.enterHouse(player, player, true);
        }
		/*if(command[0].equalsIgnoreCase("sendstring")) {
			int child = Integer.parseInt(command[1]);
			String string = command[2];
			player.getPacketSender().sendString(child, string);
		}*/
        if (command[0].equalsIgnoreCase("tasks")) {
            player.getPacketSender().sendMessage("Found " + TaskManager.getTaskAmount() + " tasks.");
        }
        if (command[0].equalsIgnoreCase("reloadnewbans")) {
            ConnectionHandler.reloadUUIDBans();
            player.getPacketSender().sendMessage("Reloaded UUID bans reloaded!");
        }
        if (command[0].equalsIgnoreCase("reloadipbans")) {
            PlayerPunishment.reloadIPBans();
            player.getPacketSender().sendMessage("IP bans reloaded!");
        }
        if (command[0].equalsIgnoreCase("reloadipmutes")) {
            PlayerPunishment.reloadIPMutes();
            player.getPacketSender().sendMessage("IP mutes reloaded!");
        }
        if (command[0].equalsIgnoreCase("ipban2")) {
            String ip = wholeCommand.substring(7);
            PlayerPunishment.addBannedIP(ip);
            player.getPacketSender().sendMessage("" + ip + " IP was successfully banned. Command logs written.");
        }
        if (command[0].equalsIgnoreCase("void")) {
            int[][] VOID_ARMOUR = {
                    {Equipment.BODY_SLOT, 19785},
                    {Equipment.LEG_SLOT, 19786},
                    {Equipment.HANDS_SLOT, 8842}
            };
            for (int i = 0; i < VOID_ARMOUR.length; i++) {
                player.getEquipment().set(VOID_ARMOUR[i][0], new Item(VOID_ARMOUR[i][1]));
            }
            int index = Integer.parseInt(command[1]);
            switch (index) {
                case 1:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(11665));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(19111));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(11732));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(6585));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(18349));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13262));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15220));
                    break;
                case 2:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(11664));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(10499));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(11732));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(6585));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(18357));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13740));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15019));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(9244, 500));
                    break;
                case 3:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(11663));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(2413));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(6920));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(18335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(14006));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13738));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15018));
                    break;
            }
            WeaponAnimations.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getEquipment().refreshItems();
        }
        if (command[0].equalsIgnoreCase("crim")) {
            int index = Integer.parseInt(command[1]);
            switch (index) {
                case 1:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(9788));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(19709));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20000));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(16403));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13964));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(773));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(2583));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(2585));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(14484));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(11212, 1000000));
                    break;
                case 2:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(9788));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(19709));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20000));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(20171));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13964));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(773));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(2583));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(2585));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(14484));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(11212, 1000000));
                    break;
                case 3:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(14484));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(13999, 1000000));
                    break;
            }
            WeaponAnimations.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getEquipment().refreshItems();
        }
        if (command[0].equalsIgnoreCase("kilik")) {
            int index = Integer.parseInt(command[1]);
            switch (index) {
                case 1:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(14008));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(14019));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20000));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13742));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15220));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(14009));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(14010));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(7462));
                    break;
                case 2:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(14014));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(14019));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20002));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(21777));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13738));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15018));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(14015));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(14016));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(7462));
                    break;
                case 3:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(14011));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(14019));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20001));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(20171));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(18361));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15019));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(14012));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(14013));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(7462));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(11212, 1000000));
                    break;
            }
            WeaponAnimations.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getEquipment().refreshItems();
        }
        if (command[0].equalsIgnoreCase("massacreitems")) {
            int i = 0;
            while (i < GameSettings.MASSACRE_ITEMS.length) {
                player.getInventory().add(GameSettings.MASSACRE_ITEMS[i], 1);
                i++;
            }
        }
        if (command[0].equalsIgnoreCase("location")) {
            player.getPacketSender().sendConsoleMessage("Current location: " + player.getLocation().toString() + ", coords: " + player.getPosition());
        }
        if (command[0].equalsIgnoreCase("freeze")) {
            player.getMovementQueue().freeze(15);
        }
        if (command[0].equalsIgnoreCase("sendsong") && command[1] != null) {
            int song = Integer.parseInt(command[1]);
            player.getPacketSender().sendSong(song);
        }
        if (command[0].equalsIgnoreCase("memory")) {
            //	ManagementFactory.getMemoryMXBean().gc();
			/*MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			long mb = (heapMemoryUsage.getUsed() / 1000);*/
            long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            long megabytes = used / 1000000;
            player.getPacketSender().sendMessage("Heap usage: " + Misc.insertCommasToNumber("" + megabytes + "") + " megabytes, or " + Misc.insertCommasToNumber("" + used + "") + " bytes.");
        }
        if (command[0].equalsIgnoreCase("star")) {
            ShootingStar.despawn(true);
            player.getPacketSender().sendMessage("star method called.");
        }
        if (command[0].equalsIgnoreCase("tree")) {
            EvilTree.despawn(true);
            player.getPacketSender().sendMessage("evil tree method called.");
        }
        if (command[0].equalsIgnoreCase("dispose")) {
            player.dispose();
        }
        if (command[0].equalsIgnoreCase("save")) {
            player.save();
            player.getPacketSender().sendMessage("Saved your character.");
        }
        if (command[0].equalsIgnoreCase("saveall")) {
            World.savePlayers();
        }
        if (command[0].equalsIgnoreCase("v1")) {
            World.sendMessage("<img=10> <col=008FB2>Another 20 voters have been rewarded! Vote now using the ::vote command!");
        }
        if (command[0].equalsIgnoreCase("test")) {
            GrandExchange.open(player);
        }
        if (command[0].equalsIgnoreCase("frame")) {
            int frame = Integer.parseInt(command[1]);
            String text = command[2];
            player.getPacketSender().sendString(frame, text);
        }
        if (command[0].equalsIgnoreCase("npc")) {
            int id = Integer.parseInt(command[1]);
            NPC npc = new NPC(id, new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()));
            World.register(npc);
            npc.setConstitution(20000);
            npc.setEntityInteraction(player);
            //npc.getCombatBuilder().attack(player);
            //	player.getPacketSender().sendEntityHint(npc);
			/*TaskManager.submit(new Task(5) {

				@Override
				protected void execute() {
					npc.moveTo(new Position(npc.getPosition().getX() + 2, npc.getPosition().getY() + 2));
					player.getPacketSender().sendEntityHintRemoval(false);
					stop();
				}

			});*/
            //npc.getMovementCoordinator().setCoordinator(new Coordinator().setCoordinate(true).setRadius(5));
        }
		/*
		 * So... this command actually works, but I'm a dipshit and needs to be done client sided. lol. also do without fancy list
		 *
		 *
		if (command[0].equalsIgnoreCase("dumpmobdef")) {
			int id = Integer.parseInt(command[1]);
			MobDefinition def;

			if (MobDefinition.get(id) != null) {
				def = MobDefinition.get(id);
			} else {
				player.getPacketSender().sendMessage("The mob definition was null.");
				return;
			}

			ArrayList<String> list = new ArrayList<String>();
			list.add("MobDefinition Dump for NPCid: "+id);
			if (def.name != null) {
				list.add("name: "+def.name);
			} else {
				list.add("name: null");
			}
			list.add("combatLevel: "+def.combatLevel);
			list.add("degreesToTurn: "+def.degreesToTurn);
			list.add("headIcon: "+def.headIcon);
			list.add("npcSizeInSquares: "+def.npcSizeInSquares);
			list.add("standAnimation: "+def.standAnimation);
			list.add("walkAnimation: "+def.walkAnimation);
			list.add("walkingBackwardsAnimation: "+def.walkingBackwardsAnimation);
			list.add("walkLeftAnimation: "+def.walkLeftAnimation);
			list.add("walkRightAnimation: "+def.walkRightAnimation);
			for (int i = 0; i > def.actions.length; i++) {
				if (def.actions[i] != null) {
					list.add("actions["+i+"]: "+def.actions[i]);
				} else {
					list.add("actions["+i+"]: null");
				}
			}
			for (int i = 0; i > def.childrenIDs.length; i++) {
					list.add("childrenIds["+i+"]: "+def.childrenIDs[i]);
			}
			if (def.description != null) {
				list.add("description: "+def.description.toString());
			}
			list.add("disableRightClick: "+def.disableRightClick);
			list.add("drawYellowDotOnMap: "+def.drawYellowDotOnMap);
			for (int i = 0; i > def.npcModels.length; i++) {
				list.add("npcModels["+i+"]: "+def.npcModels[i]);
			}
			list.add("visibilityOrRendering: "+def.visibilityOrRendering);

			for (String string : list) {
				System.out.println(string);
			}
			System.out.println("---Dump Complete---");
			list.clear();
		}
		*/
        if (command[0].equalsIgnoreCase("skull")) {
            if (player.getSkullTimer() > 0) {
                player.setSkullTimer(0);
                player.setSkullIcon(0);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
            } else {
                CombatFactory.skullPlayer(player);
            }
        }
        if (command[0].equalsIgnoreCase("fillinv") || command[0].equalsIgnoreCase("fill")) {
            if (command.length > 1 && command[1] != null && command[1].equalsIgnoreCase("y")) {

                /* Empty the inv first */
                player.getInventory().resetItems().refreshItems();

            }

            while (player.getInventory().getFreeSlots() > 0) { //why 22052? Fuck you. that's why.
                int it = Misc.inclusiveRandom(1, 22052);
                if (ItemDefinition.forId(it) == null || ItemDefinition.forId(it).getName() == null || ItemDefinition.forId(it).getName().equalsIgnoreCase("null")) {
                    continue;
                } else {
                    player.getInventory().add(it, 1);
                }
            }
        }
        if (command[0].equalsIgnoreCase("playnpc")) {
            int npcID = Integer.parseInt(command[1]);
            player.setNpcTransformationId(npcID);
            player.getStrategy(npcID);
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        } else if (command[0].equalsIgnoreCase("playobject")) {
            player.getPacketSender().sendObjectAnimation(new GameObject(2283, player.getPosition().copy()), new Animation(751));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (command[0].equalsIgnoreCase("interface")) {
            int id = Integer.parseInt(command[1]);
            player.getPacketSender().sendInterface(id);
        }
        if (command[0].equalsIgnoreCase("walkableinterface")) {
            int id = Integer.parseInt(command[1]);
            player.getPacketSender().sendWalkableInterface(id);
        }
        if (command[0].equalsIgnoreCase("object")) {
            int id = Integer.parseInt(command[1]);
            player.getPacketSender().sendObject(new GameObject(id, player.getPosition(), 10, 3));
            player.getPacketSender().sendMessage("Sending object: " + id);
        }
        if (command[0].equalsIgnoreCase("config")) {
            int id = Integer.parseInt(command[1]);
            int state = Integer.parseInt(command[2]);
            player.getPacketSender().sendConfig(id, state).sendMessage("Sent config.");
        }
        if (command[0].equalsIgnoreCase("gamemode")) {
            if (command[1].equalsIgnoreCase("1")) {
                player.getGameMode();
                GameMode.set(player, GameMode.NORMAL, false);
            } else if (command[1].equalsIgnoreCase("2")) {
                player.getGameMode();
                GameMode.set(player, GameMode.IRONMAN, false);
            } else if (command[1].equalsIgnoreCase("3")) {
                player.getGameMode();
                GameMode.set(player, GameMode.ULTIMATE_IRONMAN, false);
            } else
                player.getPacketSender().sendMessage("<img=10> Correct usage ::gamemode (#), 1 = Norm, 2 = IM, 3 = UIM");
        }
        if (command[0].equalsIgnoreCase("fly")) {
            player.getPlayerViewingIndex();
        }
        if (command[0].equalsIgnoreCase("checkbank")) {
            Player plr = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (plr != null) {
                player.getPacketSender().sendMessage("Loading bank..");
                for (Bank b : player.getBanks()) {
                    if (b != null) {
                        b.resetItems();
                    }
                }
                for (int i = 0; i < plr.getBanks().length; i++) {
                    for (Item it : plr.getBank(i).getItems()) {
                        if (it != null) {
                            player.getBank(i).add(it, false);
                        }
                    }
                }
                player.getBank(0).open();
            } else {
                player.getPacketSender().sendMessage("Player is offline!");
            }
        }
        if (command[0].equalsIgnoreCase("setpray")) {
            int setlv = Integer.parseInt(command[1]);
            player.getPacketSender().sendMessage("You've set your current prayer points to: @red@" + setlv + "@bla@.");
            player.getSkillManager().setCurrentLevel(Skill.PRAYER, setlv);
        }
        if (command[0].equalsIgnoreCase("sethp") || command[0].equalsIgnoreCase("sethealth")) {
            int setlv = Integer.parseInt(command[1]);
            player.getPacketSender().sendMessage("You've set your constitution to: @red@" + setlv + "@bla@.");
            player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, setlv);
        }
        if (command[0].equalsIgnoreCase("clani")) {
            ClanChatManager.updateList(player.getCurrentClanChat());
            player.getPacketSender().sendMessage("Int to enter: " + ClanChat.RANK_REQUIRED_TO_ENTER);
            player.getPacketSender().sendMessage("Int to talk: " + ClanChat.RANK_REQUIRED_TO_TALK);
            player.getPacketSender().sendMessage("Int to kick: " + ClanChat.RANK_REQUIRED_TO_KICK);
            player.getPacketSender().sendMessage("Int to guild: " + ClanChat.RANK_REQUIRED_TO_VISIT_GUILD).sendMessage("");
            player.getPacketSender().sendMessage(player.getClanChatName() + " is ur clan. " + player.getCurrentClanChat() + "");
        }
        if (command[0].equalsIgnoreCase("getintitem")) {
            if (player.getInteractingItem() == null) {
                player.getPacketSender().sendMessage("It's a null from here.");
                return;
            }
            player.getPacketSender().sendMessage("ID: " + player.getInteractingItem().getId() + ", amount: " + player.getInteractingItem().getAmount());
        }
        if (command[0].equalsIgnoreCase("tits")) {
            //	ClanChat.RANK_REQUIRED_TO_ENTER = 7;
            player.getPacketSender().sendMessage("tits are done");
            player.getPacketSender().sendMessage("tits are: " + ClanChat.RANK_REQUIRED_TO_ENTER);
        }
        if (command[0].equalsIgnoreCase("index")) {
            player.getPacketSender().sendMessage("Player index: " + player.getIndex());
            player.getPacketSender().sendMessage("Player index * 4: " + player.getIndex() * 4);
        }
        if (command[0].equalsIgnoreCase("claninstanceid")) {
            player.getPacketSender().sendMessage(player.getCurrentClanChat().getRegionInstance() + " test.");
        }
        if (command[0].equalsIgnoreCase("loc")) {
            player.getPacketSender().sendMessage("Your location: " + player.getLocation());
        }
        if (command[0].equalsIgnoreCase("getpray")) {
            player.getPacketSender().sendMessage("Your current prayer points are: @red@"
                    + player.getSkillManager().getCurrentLevel(Skill.PRAYER) + "@bla@.");
        }
        if (command[0].equalsIgnoreCase("skillcapes")) {
            for (Skill skill : Skill.values()) {
                player.getInventory().add(skill.getSkillCapeId(), 1);
            }
        }
        if (command[0].equalsIgnoreCase("skillcapest") || command[0].equalsIgnoreCase("skillcapet")) {
            for (Skill skill : Skill.values()) {
                player.getInventory().add(skill.getSkillCapeTrimmedId(), 1);
            }
        }
        if (command[0].equalsIgnoreCase("pets")) {
            for (Skill skill : Skill.values()) {
                player.getInventory().add(skill.getPetId(), 1);
            }
        }
        if (command[0].equalsIgnoreCase("clues")) {
            for (Item i : player.getInventory().getItems()) {
                if (i != null) {
                    player.getInventory().delete(i);
                }
            }
            player.getInventory().add(952, 1);
            for (int i = 0; i < CLUESCROLL.values().length; i++) {
                player.getInventory().add(CLUESCROLL.values()[i].getClueId(), 1);
            }
        }
        if (command[0].equalsIgnoreCase("checkinv")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player.getInventory().setItems(player2.getInventory().getCopiedItems()).refreshItems();
        }
        if (command[0].equalsIgnoreCase("mockcasket")) {
            player.getPacketSender().sendMessage("Started mock...");
            CLUESCROLL.mockCasket(Integer.parseInt(command[1]));
            player.getPacketSender().sendMessage("Done mock.");
        }
        if (command[0].equalsIgnoreCase("easter")) {
            if (Misc.easter(Misc.getYear())) {
                player.getPacketSender().sendMessage("easter is true");
            }
        }
        if (command[0].equalsIgnoreCase("bgloves")) {
            player.getPacketSender().sendMessage(player.getBrawlerChargers() + " charges");
        }
        if (command[0].equalsIgnoreCase("checkequip") || command[0].equalsIgnoreCase("checkgear")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player.getEquipment().setItems(player2.getEquipment().getCopiedItems()).refreshItems();
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            WeaponAnimations.update(player);
            BonusManager.update(player);
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (command[0].equalsIgnoreCase("togglediscord")) {
            DiscordMessager.active = !DiscordMessager.active;
            player.getPacketSender().sendMessage("Discord messages is now set to: " + DiscordMessager.active);
        }
        if (command[0].equalsIgnoreCase("crewards")) {
            CrystalChest.sendRewardInterface(player);
        }
        if (command[0].equalsIgnoreCase("bolts")) {
            for (int i = 0; i < BoltData.values().length; i++) {
                player.getInventory().add(BoltData.values()[i].getBolt(), 1000).add(BoltData.values()[i].getTip(), 1000);
            }
        }
    }
}
