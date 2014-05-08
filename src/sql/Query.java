package sql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import forumSystemCore.*;
import utility.*;
import user.*;

public class Query {
	
	public static void initDB() throws ClassNotFoundException, SQLException, IOException {
		Executor.run("CREATE DATABASE Forum");
		String initDbCode = utility.IO.read("init.sql");
		String[] commands = initDbCode.split("--NEXT--");
		for (int i=0; i<commands.length; i++) Executor.run(commands[i]);
	}

	public static void save(Rank rank) throws ClassNotFoundException, SQLException {
		Executor.run("DELETE FROM `Ranks` WHERE `name` = '" + rank.getName() + "'");
		Executor.run("INSERT INTO `Ranks`(" + 
				"`rel`, " + 
				"`name`, " + 
				"`CREATE_FORUM`, " + 
				"`SET_FORUM_PROPERTIES`, " + 
				"`CREATE_SUB_FORUM`, " + 
				"`CREATE_MESSAGE`, " + 
				"`SET_RANKS`, " + 
				"`SET_USER_RANK`, " + 
				"`DELETE_MESSAGE`, " + 
				"`DELETE_SUB_FORUM`, " + 
				"`ADD_ADMIN`, " + 
				"`REMOVE_ADMIN`, " + 
				"`ADD_MODERATOR`, " + 
				"`REMOVE_MODERATOR`" + 
			") VALUES (" + 
				"'0', " + 
				"'" + rank.getName() + "', " + 
				"'" + (rank.hasPermission(Permissions.CREATE_FORUM) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.SET_FORUM_PROPERTIES) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.CREATE_SUB_FORUM) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.CREATE_MESSAGE) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.SET_RANKS) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.SET_USER_RANK) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.DELETE_MESSAGE) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.DELETE_SUB_FORUM) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.ADD_ADMIN) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.REMOVE_ADMIN) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.ADD_MODERATOR) ? "1" : "0") + "', " + 
				"'" + (rank.hasPermission(Permissions.REMOVE_MODERATOR) ? "1" : "0") + "'" + 
			")");
	}

	public static void save(Complaint comp) throws ClassNotFoundException, SQLException {
		Executor.run("DELETE FROM `Complaints` WHERE `id` = '" + comp.getId() + "'");
		Executor.run("INSERT INTO `Complaints`(" + 
				"`rel`, " + 
				"`id`, " + 
				"`complainer`, " + 
				"`complainee`, " + 
				"`complaintMessage`, " + 
				"`date`" + 
			") VALUES (" + 
				"'0', " + 
				"'" + comp.getId() + "', " + 
				"'" + comp.getComplainer() + "', " + 
				"'" + comp.getComplainee() + "', " + 
				"'" + comp.getComplaintMessage() + "', " + 
				"'" + comp.getDate() + "'" + 
			")");
	}

	public static void save(User user) throws ClassNotFoundException, SQLException {
		Executor.run("DELETE FROM `Users` WHERE `username` = '" + user.getUsername() + "'");
		Executor.run("INSERT INTO `Users`(" + 
				"`rel`, " + 
				"`mail`, " + 
				"`name`, " + 
				"`username`, " + 
				"`password`, " + 
				"`rank`" + 
			") VALUES (" + 
				"'0', " + 
				"'" + user.getMail() + "', " + 
				"'" + user.getName() + "', " + 
				"'" + user.getUsername() + "', " + 
				"'" + user.getPassword() + "', " + 
				"'" + user.getRank().getName() + "'" + 
			")");
		Executor.run("DELETE FROM `_friends` WHERE `user1` = '" + user.getUsername() + "'");
		for (int i=0; i<user.getFriends().size(); i++) {
			User user2 = user.getFriends().get(i);
			Executor.run("INSERT INTO `_friends`(" + 
					"`rel`, " + 
					"`user1`, " + 
					"`user2`" + 
				") VALUES (" + 
					"'0', " + 
					"'" + user.getUsername() + "', " + 
					"'" + user2.getUsername() + "'" + 
				")");
		}
		Executor.run("DELETE FROM `_friendRequests` WHERE `user1` = '" + user.getUsername() + "'");
		for (int i=0; i<user.getFriendRequests().size(); i++) {
			User user2 = user.getFriendRequests().get(i);
			Executor.run("INSERT INTO `_friendRequests`(" + 
					"`rel`, " + 
					"`user1`, " + 
					"`user2`" + 
				") VALUES (" + 
					"'0', " + 
					"'" + user.getUsername() + "', " + 
					"'" + user2.getUsername() + "'" + 
				")");
		}
		Executor.run("DELETE FROM `_pendingFriendRequests` WHERE `user1` = '" + user.getUsername() + "'");
		for (int i=0; i<user.getPendingFriendRequests().size(); i++) {
			User user2 = user.getPendingFriendRequests().get(i);
			Executor.run("INSERT INTO `_pendingFriendRequests`(" + 
					"`rel`, " + 
					"`user1`, " + 
					"`user2`" + 
				") VALUES (" + 
					"'0', " + 
					"'" + user.getUsername() + "', " + 
					"'" + user2.getUsername() + "'" + 
				")");
		}
	}

	public static void save(Message msg) throws ClassNotFoundException, SQLException {
		Executor.run("DELETE FROM `Messages` WHERE `id` = '" + msg.getId() + "'");
		Executor.run("INSERT INTO `SubForums`(" + 
				"`msgRel`, " + 
				"`subforumRel`, " + 
				"`id`, " + 
				"`date`, " + 
				"`content`, " + 
				"`title`, " + 
				"`writer`" +
			") VALUES (" + 
				"'0', " + 
				"'0', " + 
				"'" + msg.getId() + "', " + 
				"'" + msg.getDate() + "', " + 
				"'" + msg.getContent() + "', " + 
				"'" + msg.getTitle() + "', " + 
				"'" + msg.getUser() + "'" + 
			")");
		Executor.run("DELETE FROM `Messages` WHERE `msgRel` = '" + msg.getId() + "'");
		for (int i=0; i<msg.getReplies().size(); i++) {
			Message reply = msg.getReplies().get(i);
			reply.save();
			Executor.run("UPDATE `Messages` SET `msgRel` = '" + msg.getId() + "' WHERE `msgRel` = '0'");
		}
	}

	public static void save(SubForum sf) throws ClassNotFoundException, SQLException {
		Executor.run("DELETE FROM `SubForums` WHERE `id` = '" + sf.getId() + "'");
		Executor.run("INSERT INTO `SubForums`(" + 
				"`rel`, " + 
				"`id`, " + 
				"`subject`" +
			") VALUES (" + 
				"'0', " + 
				"'" + sf.getId() + "', " + 
				"'" + sf.getSubject() + "'" + 
			")");
		// moderators
		Executor.run("DELETE FROM `_moderators` WHERE `subforumId` = '" + sf.getId() + "'");
		for (int i=0; i<sf.getModerators().size(); i++) {
			User moderator = sf.getModerators().get(i);
			Executor.run("INSERT INTO `_moderators`(`subforumId`, `username`) VALUES ('" + sf.getId() + "', '" + moderator.getUsername() + "')");
		}
		// complaints
		Executor.run("DELETE FROM `Complaints` WHERE `rel` = '" + sf.getId() + "'");
		for (int i=0; i<sf.getComplaints().size(); i++) sf.getComplaints().get(i).save();
		Executor.run("UPDATE `Complaints` SET `rel` = '" + sf.getId() + "' WHERE `rel` = '0'");
		// messages
		Executor.run("DELETE FROM `Messages` WHERE `subforumRel` = '" + sf.getId() + "'");
		for (int i=0; i<sf.getMessages().size(); i++) sf.getMessages().get(i).save();
		Executor.run("UPDATE `Messages` SET `subforumRel` = '" + sf.getId() + "' WHERE `subforumRel` = '0' AND `msgRel` = '0'");
		// suspended
		Executor.run("DELETE FROM `_suspended` WHERE `subforumId` = '" + sf.getId() + "'");
		for (int i=0; i<sf.getSuspendedUsers().size(); i++) {
			Suspended sus = sf.getSuspendedUsers().get(i);
			Executor.run("INSERT INTO `_suspended`(`subforumId`, `username`, `date`) VALUES ('" + sf.getId() + "', '" + sus.getUser() + "', '" + sus.getDate() + "')");
		}
	}

	public static void save(Forum forum) throws ClassNotFoundException, SQLException {
		Executor.run("DELETE FROM `Forums` WHERE `id` = '" + forum.getId() + "'");
		Executor.run("INSERT INTO `Forums`(" + 
				"`id`, " + 
				"`name`" +
			") VALUES (" + 
				"'" + forum.getId() + "', " + 
				"'" + forum.getName() + "'" + 
			")");
		// administrators
		Executor.run("DELETE FROM `_administrators` WHERE `ForumId` = '" + forum.getId() + "'");
		for (int i=0; i<forum.getAdministrators().size(); i++) {
			User admin = forum.getAdministrators().get(i);
			Executor.run("INSERT INTO `_administrators`(`ForumId`, `Username`) VALUES ('" + forum.getId() + "', '" + admin.getUsername() + "')");
		}
		// messages
		Executor.run("DELETE FROM `Users` WHERE `rel` = '" + forum.getId() + "'");
		for (int i=0; i<forum.getMembers().size(); i++) forum.getMembers().get(i).save();
		Executor.run("UPDATE `Users` SET `rel` = '" + forum.getId() + "' WHERE `rel` = '0'");
		// sub forums
		Executor.run("DELETE FROM `SubForums` WHERE `rel` = '" + forum.getId() + "'");
		for (int i=0; i<forum.getSubForums().size(); i++) forum.getSubForums().get(i).save();
		Executor.run("UPDATE `SubForums` SET `rel` = '" + forum.getId() + "' WHERE `rel` = '0'");
		// ranks
		Executor.run("DELETE FROM `Ranks` WHERE `rel` = '" + forum.getId() + "'");
		for (int i=0; i<forum.getRanks().size(); i++) forum.getRanks().get(i).save();
		Executor.run("UPDATE `Ranks` SET `rel` = '" + forum.getId() + "' WHERE `rel` = '0'");
	}
	
}
