package sql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import forumSystemCore.*;
import utility.*;
import user.*;

public class Query {
	
	public static boolean load(ForumSystem fs) throws ClassNotFoundException, SQLException {
		ResultSet users = Executor.query("SELECT * FROM `Super`");
		if (!users.next()) return false;
		User superuser = new User(users.getString("mail"), users.getString("name"), users.getString("username"), users.getString("password"), Rank.superUser);
		ResultSet forums = Executor.query("SELECT * FROM `Forums`");
		ArrayList<Forum> forumList = new ArrayList<Forum>();
		while (forums.next()) forumList.add(loadForum(forums, superuser));
		fs.recover(forumList, superuser);
		return true;
	}
	
	public static Forum loadForum(ResultSet sqlObject, User admin) throws SQLException, ClassNotFoundException {
		Forum forum = new Forum(sqlObject.getString("name"), admin);
		String id = sqlObject.getString("id");
		ArrayList<User> administrators = new ArrayList<User>();
		ArrayList<User> members = new ArrayList<User>();
		ArrayList<SubForum> subForums = new ArrayList<SubForum>();
		ArrayList<Rank> ranks = new ArrayList<Rank>();
		ArrayList<String> adminUsernames = new ArrayList<String>();
		// extract ranks
		ResultSet rankResults = Executor.query("SELECT * FROM `Ranks` WHERE `rel` = '" + id + "'");
		while (rankResults.next()) ranks.add(loadRank(rankResults));
		// extract admin user names
		ResultSet adminResults = Executor.query("SELECT * FROM `_administrators` WHERE `ForumId` = '" + id + "'");
		while (adminResults.next()) adminUsernames.add(adminResults.getString("Username"));
		// load users
		ResultSet userResults = Executor.query("SELECT * FROM `Users` WHERE `rel` = '" + id + "'");
		while (userResults.next()) {
			User member = loadUser(userResults, ranks); 
			members.add(member);
			if (adminUsernames.contains(member.getUsername())) administrators.add(member);
		}
		// recover users
		for (int i=0; i<members.size(); i++) recoverUser(members.get(i), id, members);
		// load sub forums
		ResultSet sfResults = Executor.query("SELECT * FROM `SubForums` WHERE `rel` = '" + id + "'");
		while (sfResults.next()) subForums.add(loadSubForum(sfResults, members));
		// recover and return
		forum.recover(administrators, members, subForums, ranks, id);
		return forum;
	}
	
	public static SubForum loadSubForum(ResultSet sqlObject, ArrayList<User> forumMembers) throws SQLException, ClassNotFoundException {
		String id = sqlObject.getString("id");
		List<User> moderators = new ArrayList<User>();
		List<Complaint> complaints = new ArrayList<Complaint>();
		List<Message> messages = new ArrayList<Message>();
		List<Suspended> suspendedUsers = new ArrayList<Suspended>();
		// load moderators
		ResultSet moderatorResults = Executor.query("SELECT * FROM `_moderators` WHERE `subforumId` = '" + id + "'");
		while (moderatorResults.next()) moderators.add(findUser(forumMembers, moderatorResults.getString("username")));
		// load complaints
		ResultSet complaintResults = Executor.query("SELECT * FROM `Complaints` WHERE `rel` = '" + id + "'");
		while (complaintResults.next()) complaints.add(loadComplaint(complaintResults, forumMembers));
		// load messages
		ResultSet msgResults = Executor.query("SELECT * FROM `Messages` WHERE `subforumRel` = '" + id + "'");
		while (msgResults.next()) messages.add(loadMessage(msgResults, forumMembers));
		// load suspended users
		ResultSet susResults = Executor.query("SELECT * FROM `_suspended` WHERE `subforumId` = '" + id + "'");
		while (susResults.next()) suspendedUsers.add(loadSuspended(susResults, forumMembers));
		SubForum subforum = new SubForum(sqlObject.getString("subject"), moderators.get(0));
		subforum.recover(moderators, complaints, messages, suspendedUsers, id);
		return subforum;
	}
	
	public static Suspended loadSuspended(ResultSet sqlObject, ArrayList<User> forumMembers) throws NumberFormatException, SQLException {
		return new Suspended(findUser(forumMembers, sqlObject.getString("username")), new Date(Integer.valueOf(sqlObject.getString("date")) * 1000));
	}
	
	public static Message loadMessage(ResultSet sqlObject, ArrayList<User> forumMembers) throws SQLException, ClassNotFoundException {
		Message msg = new Message(findUser(forumMembers, sqlObject.getString("writer")), sqlObject.getString("title"), sqlObject.getString("content"));
		List<Message> replies = new ArrayList<Message>();
		String id = sqlObject.getString("id");
		ResultSet msgResults = Executor.query("SELECT * FROM `Messages` WHERE `msgRel` = '" + id + "'");
		while (msgResults.next()) replies.add(loadMessage(msgResults, forumMembers));
		msg.recover(replies, new Date(Long.valueOf(sqlObject.getString("date")) * 1000), id);
		return msg;
	}
	
	public static Complaint loadComplaint(ResultSet sqlObject, ArrayList<User> forumMembers) throws SQLException {
		Complaint complaint = new Complaint(findUser(forumMembers, sqlObject.getString("complainer")), findUser(forumMembers, sqlObject.getString("complainee")), sqlObject.getString("complaintMessage"), new Date(Integer.valueOf(sqlObject.getString("date")) * 1000));
		complaint.recover(sqlObject.getString("id"));
		return complaint;
		
	}
	
	public static User findUser(ArrayList<User> users, String username) {
		for (int i=0; i<users.size(); i++) {
			if (users.get(i).getUsername().equals(username)) return users.get(i); 
		}
		return null;
	}
	
	public static User loadUser(ResultSet sqlObject, ArrayList<Rank> ranks) throws SQLException {
		return new User(sqlObject.getString("mail"), sqlObject.getString("name"), sqlObject.getString("username"), sqlObject.getString("password"), findRank(ranks, sqlObject.getString("rank")));
	}
	
	public static void recoverUser(User user, String forumId, ArrayList<User> forumMembers) throws SQLException, ClassNotFoundException {
		ArrayList<User> friends = new ArrayList<User>();
		ArrayList<User> pendingFriendRequests = new ArrayList<User>();
		ArrayList<User> friendRequests = new ArrayList<User>();
		// load friends
		ResultSet friendResults = Executor.query("SELECT * FROM `_friends` WHERE `rel` = '" + forumId + "' AND `user1` = '" + user.getUsername() + "'");
		while (friendResults.next()) friends.add(findUser(forumMembers, friendResults.getString("user2")));
		// load pending
		ResultSet pendResults = Executor.query("SELECT * FROM `_pendingFriendRequests` WHERE `rel` = '" + forumId + "' AND `user1` = '" + user.getUsername() + "'");
		while (pendResults.next()) pendingFriendRequests.add(findUser(forumMembers, pendResults.getString("user2")));
		// load requests
		ResultSet reqResults = Executor.query("SELECT * FROM `_friendRequests` WHERE `rel` = '" + forumId + "' AND `user1` = '" + user.getUsername() + "'");
		while (reqResults.next()) friendRequests.add(findUser(forumMembers, reqResults.getString("user2")));
		user.recover(friends, pendingFriendRequests, friendRequests);
	}
	
	public static Rank findRank(ArrayList<Rank> rankList, String rankName) {
		if (rankName.equals("SuperUser")) return Rank.superUser;
		if (rankName.equals("Admin")) return Rank.admin;
		if (rankName.equals("Moderator")) return Rank.moderator;
		if (rankName.equals("Member")) return Rank.member;
		for (int i=0; i<rankList.size(); i++) {
			if (rankList.get(i).getName().equals(rankName)) return rankList.get(i); 
		}
		return null;
	}
	
	public static Rank loadRank(ResultSet sqlObject) throws SQLException {
		Rank result = new Rank(sqlObject.getString("name"));
		if (sqlObject.getString("CREATE_FORUM") == "1") result.addPermission(Permissions.CREATE_FORUM);
		if (sqlObject.getString("SET_FORUM_PROPERTIES") == "1") result.addPermission(Permissions.SET_FORUM_PROPERTIES);
		if (sqlObject.getString("CREATE_SUB_FORUM") == "1") result.addPermission(Permissions.CREATE_SUB_FORUM);
		if (sqlObject.getString("CREATE_MESSAGE") == "1") result.addPermission(Permissions.CREATE_MESSAGE);
		if (sqlObject.getString("SET_RANKS") == "1") result.addPermission(Permissions.SET_RANKS);
		if (sqlObject.getString("SET_USER_RANK") == "1") result.addPermission(Permissions.SET_USER_RANK);
		if (sqlObject.getString("DELETE_MESSAGE") == "1") result.addPermission(Permissions.DELETE_MESSAGE);
		if (sqlObject.getString("DELETE_SUB_FORUM") == "1") result.addPermission(Permissions.DELETE_SUB_FORUM);
		if (sqlObject.getString("ADD_ADMIN") == "1") result.addPermission(Permissions.ADD_ADMIN);
		if (sqlObject.getString("REMOVE_ADMIN") == "1") result.addPermission(Permissions.REMOVE_ADMIN);
		if (sqlObject.getString("ADD_MODERATOR") == "1") result.addPermission(Permissions.ADD_MODERATOR);
		if (sqlObject.getString("REMOVE_MODERATOR") == "1") result.addPermission(Permissions.REMOVE_MODERATOR);
		return result;
	}
	
	public static void saveSuper(User user) throws ClassNotFoundException, SQLException {
		Executor.run("DELETE FROM `Super` WHERE `username` = '" + user.getUsername() + "'");
		Executor.run("INSERT INTO `Super`(" + 
				"`mail`, " + 
				"`name`, " + 
				"`username`, " + 
				"`password`" + 
			") VALUES (" + 
				"'" + user.getMail() + "', " + 
				"'" + user.getName() + "', " + 
				"'" + user.getUsername() + "', " + 
				"'" + user.getPassword() + "'" + 
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
	
	public static void initDB() throws ClassNotFoundException, SQLException, IOException {
		String initDbCode = utility.IO.read("init.sql");
		String[] commands = initDbCode.split("--NEXT--");
		for (int i=0; i<commands.length; i++) Executor.run(commands[i]);
	}

	public static void save(Rank rank) throws ClassNotFoundException, SQLException {
		ResultSet currentRecord = Executor.query("SELECT * FROM `Ranks` WHERE `name` = '" + rank.getName() + "'");
		String rel = currentRecord.next() ? currentRecord.getString("rel") : "0";
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
				"'" + rel + "', " + 
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
		ResultSet currentRecord = Executor.query("SELECT * FROM `Complaints` WHERE `id` = '" + comp.getId() + "'");
		String rel = currentRecord.next() ? currentRecord.getString("rel") : "0";
		Executor.run("DELETE FROM `Complaints` WHERE `id` = '" + comp.getId() + "'");
		Executor.run("INSERT INTO `Complaints`(" + 
				"`rel`, " + 
				"`id`, " + 
				"`complainer`, " + 
				"`complainee`, " + 
				"`complaintMessage`, " + 
				"`date`" + 
			") VALUES (" + 
				"'" + rel + "', " + 
				"'" + comp.getId() + "', " + 
				"'" + comp.getComplainer() + "', " + 
				"'" + comp.getComplainee() + "', " + 
				"'" + comp.getComplaintMessage() + "', " + 
				"'" + comp.getDate() + "'" + 
			")");
	}

	public static void save(User user) throws ClassNotFoundException, SQLException {
		if (user.getMail() == null) return;
		ResultSet currentRecord = Executor.query("SELECT * FROM `Users` WHERE `username` = '" + user.getUsername() + "'");
		String rel = currentRecord.next() ? currentRecord.getString("rel") : "0";
		Executor.run("DELETE FROM `Users` WHERE `username` = '" + user.getUsername() + "'");
		Executor.run("INSERT INTO `Users`(" + 
				"`rel`, " + 
				"`mail`, " + 
				"`name`, " + 
				"`username`, " + 
				"`password`, " + 
				"`rank`" + 
			") VALUES (" + 
				"'" + rel + "', " + 
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
		ResultSet currentRecord = Executor.query("SELECT * FROM `Messages` WHERE `id` = '" + msg.getId() + "'");
		String msgRel = "-1";
		String sfRel = "0";
		if (currentRecord.next()) {
			msgRel = currentRecord.getString("msgRel");
			sfRel = currentRecord.getString("subforumRel");
		}
		Executor.run("DELETE FROM `Messages` WHERE `id` = '" + msg.getId() + "'");
		Executor.run("INSERT INTO `Messages`(" + 
				"`msgRel`, " + 
				"`subforumRel`, " + 
				"`id`, " + 
				"`date`, " + 
				"`content`, " + 
				"`title`, " + 
				"`writer`" +
			") VALUES (" + 
				"'" + msgRel + "', " + 
				"'" + sfRel + "', " + 
				"'" + msg.getId() + "', " + 
				"'" + msg.getDate() + "', " + 
				"'" + msg.getContent() + "', " + 
				"'" + msg.getTitle() + "', " + 
				"'" + msg.getUser().getUsername() + "'" + 
			")");
		Executor.run("DELETE FROM `Messages` WHERE `msgRel` = '" + msg.getId() + "'");
		for (int i=0; i<msg.getReplies().size(); i++) {
			Message reply = msg.getReplies().get(i);
			reply.save();
			Executor.run("UPDATE `Messages` SET `msgRel` = '" + msg.getId() + "' WHERE `msgRel` = '0' AND `subforumRel` = '0'");
		}
		Executor.run("UPDATE `Messages` SET `msgRel` = '0' WHERE `msgRel` = '-1'");
	}

	public static void save(SubForum sf) throws ClassNotFoundException, SQLException {
		ResultSet currentRecord = Executor.query("SELECT * FROM `SubForums` WHERE `id` = '" + sf.getId() + "'");
		String rel = currentRecord.next() ? currentRecord.getString("rel") : "0";
		Executor.run("DELETE FROM `SubForums` WHERE `id` = '" + sf.getId() + "'");
		Executor.run("INSERT INTO `SubForums`(" + 
				"`rel`, " + 
				"`id`, " + 
				"`subject`" +
			") VALUES (" + 
				"'" + rel + "', " + 
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
