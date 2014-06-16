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
	
	public static void truncateDB() {
		try {
			Executor.run("truncate table `Super`");
			Executor.run("truncate table `Complaints`");
			Executor.run("truncate table `Forums`");
			Executor.run("truncate table `Messages`");
			Executor.run("truncate table `Ranks`");
			Executor.run("truncate table `SubForums`");
			Executor.run("truncate table `Users`");
			Executor.run("truncate table `_administrators`");
			Executor.run("truncate table `_friendRequests`");
			Executor.run("truncate table `_friends`");
			Executor.run("truncate table `_moderators`");
			Executor.run("truncate table `_pendingFriendRequests`");
			Executor.run("truncate table `_suspended`");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean load(ForumSystem fs) throws ClassNotFoundException, SQLException {
		if (Executor.DISABLE_SQL) return false;
		ResultSet users = Executor.query("SELECT * FROM `Super`");
		if (!users.next()) return false;
		User superuser = new User(users.getString("mail"), users.getString("name"), users.getString("username"), users.getString("password"), Rank.superUser, null);
		ResultSet forums = Executor.query("SELECT * FROM `Forums`");
		ArrayList<Forum> forumList = new ArrayList<Forum>();
		while (forums.next()) forumList.add(loadForum(forums, superuser));
		fs.recover(forumList, superuser);
		return true;
	}
	
	public static Forum loadForum(ResultSet sqlObject, User admin) throws SQLException, ClassNotFoundException {
		String id = sqlObject.getString("id");
		Forum forum = new Forum(sqlObject.getString("name"), admin, id);
		ArrayList<User> administrators = new ArrayList<User>();
		ArrayList<User> members = new ArrayList<User>();
		ArrayList<SubForum> subForums = new ArrayList<SubForum>();
		ArrayList<Rank> ranks = new ArrayList<Rank>();
		ArrayList<String> adminUsernames = new ArrayList<String>();
		// extract ranks
		ResultSet rankResults = Executor.query("SELECT * FROM `Ranks` WHERE `forumId` = '" + id + "'");
		while (rankResults.next()) ranks.add(loadRank(rankResults));
		// extract admin user names
		ResultSet adminResults = Executor.query("SELECT * FROM `_administrators` WHERE `ForumId` = '" + id + "'");
		while (adminResults.next()) adminUsernames.add(adminResults.getString("Username"));
		// load users
		ResultSet userResults = Executor.query("SELECT * FROM `Users` WHERE `forumId` = '" + id + "' OR `forumId` = '0'");
		while (userResults.next()) {
			User member = loadUser(userResults, ranks); 
			members.add(member);
			if (adminUsernames.contains(member.getUsername())) administrators.add(member);
		}
		// recover users
		for (int i=0; i<members.size(); i++) recoverUser(members.get(i), id, members);
		// load sub forums
		ResultSet sfResults = Executor.query("SELECT * FROM `SubForums` WHERE `forumId` = '" + id + "'");
		while (sfResults.next()) subForums.add(loadSubForum(sfResults, members));
		// recover and return
		forum.recover(administrators, members, subForums, ranks, id);
		return forum;
	}
	
	public static SubForum loadSubForum(ResultSet sqlObject, ArrayList<User> forumMembers) throws SQLException, ClassNotFoundException {
		String id = sqlObject.getString("id"), forumId = sqlObject.getString("forumId");
		List<User> moderators = new ArrayList<User>();
		List<Complaint> complaints = new ArrayList<Complaint>();
		List<Message> messages = new ArrayList<Message>();
		List<Suspended> suspendedUsers = new ArrayList<Suspended>();
		// load moderators
		ResultSet moderatorResults = Executor.query("SELECT * FROM `_moderators` WHERE `subforumId` = '" + id + "'");
		while (moderatorResults.next()) moderators.add(findUser(forumMembers, moderatorResults.getString("username")));
		// load complaints
		ResultSet complaintResults = Executor.query("SELECT * FROM `Complaints` WHERE `subforumId` = '" + id + "'");
		while (complaintResults.next()) complaints.add(loadComplaint(complaintResults, forumMembers));
		// load messages
		ResultSet msgResults = Executor.query("SELECT * FROM `Messages` WHERE `subforumRel` = '" + id + "'");
		while (msgResults.next()) messages.add(loadMessage(msgResults, forumMembers));
		// load suspended users
		ResultSet susResults = Executor.query("SELECT * FROM `_suspended` WHERE `subforumId` = '" + id + "'");
		while (susResults.next()) suspendedUsers.add(loadSuspended(susResults, forumMembers));
		SubForum subforum = new SubForum(sqlObject.getString("subject"), moderators.get(0), forumId, id);
		subforum.recover(moderators, complaints, messages, suspendedUsers, id);
		return subforum;
	}
	
	public static Suspended loadSuspended(ResultSet sqlObject, ArrayList<User> forumMembers) throws NumberFormatException, SQLException {
		return new Suspended(findUser(forumMembers, sqlObject.getString("username")), new Date(Integer.valueOf(sqlObject.getString("date")) * 1000));
	}
	
	public static Message loadMessage(ResultSet sqlObject, ArrayList<User> forumMembers) throws SQLException, ClassNotFoundException {
		List<Message> replies = new ArrayList<Message>();
		String id = sqlObject.getString("id");
		Message msg = new Message(findUser(forumMembers, sqlObject.getString("writer")), sqlObject.getString("title"), sqlObject.getString("content"), sqlObject.getString("subforumRel"), sqlObject.getString("msgRel"), id);
		ResultSet msgResults = Executor.query("SELECT * FROM `Messages` WHERE `msgRel` = '" + id + "'");
		while (msgResults.next()) replies.add(loadMessage(msgResults, forumMembers));
		msg.recover(replies, new Date(Long.valueOf(sqlObject.getString("date")) * 1000), id);
		return msg;
	}
	
	public static Complaint loadComplaint(ResultSet sqlObject, ArrayList<User> forumMembers) throws SQLException {
		Complaint complaint = new Complaint(findUser(forumMembers, sqlObject.getString("complainer")), findUser(forumMembers, sqlObject.getString("complainee")), sqlObject.getString("complaintMessage"), new Date(Integer.valueOf(sqlObject.getString("date")) * 1000), sqlObject.getString("subforumId"));
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
		User u = new User(sqlObject.getString("mail"), sqlObject.getString("name"), sqlObject.getString("username"), sqlObject.getString("password"), findRank(ranks, sqlObject.getString("rank")), sqlObject.getString("forumId"));
		u.setNotifType(Integer.valueOf(sqlObject.getString("notifTypes")));
		u.setFirstLogin(sqlObject.getString("firstLogin") == "1");
		u.setMailCode(sqlObject.getString("code"));
		return u;
	}
	
	public static void recoverUser(User user, String forumId, ArrayList<User> forumMembers) throws SQLException, ClassNotFoundException {
		ArrayList<User> friends = new ArrayList<User>();
		ArrayList<User> pendingFriendRequests = new ArrayList<User>();
		ArrayList<User> friendRequests = new ArrayList<User>();
		// load friends
		ResultSet friendResults = Executor.query("SELECT * FROM `_friends` WHERE `forumId` = '" + forumId + "' AND `user1` = '" + user.getUsername() + "'");
		while (friendResults.next()) friends.add(findUser(forumMembers, friendResults.getString("user2")));
		// load pending
		ResultSet pendResults = Executor.query("SELECT * FROM `_pendingFriendRequests` WHERE `forumId` = '" + forumId + "' AND `user1` = '" + user.getUsername() + "'");
		while (pendResults.next()) pendingFriendRequests.add(findUser(forumMembers, pendResults.getString("user2")));
		// load requests
		ResultSet reqResults = Executor.query("SELECT * FROM `_friendRequests` WHERE `forumId` = '" + forumId + "' AND `user1` = '" + user.getUsername() + "'");
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
		Rank result = new Rank(sqlObject.getString("name"), sqlObject.getString("forumId"));
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
	
	
	
	
	
	/////////////////////////////////////////////////////////// ---- save
	
	
	
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
					"`forumId`, " + 
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
					"`forumId`, " + 
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
					"`forumId`, " + 
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
		if (Executor.DISABLE_SQL) return;
		Executor.run("DELETE FROM `Ranks` WHERE `name` = '" + rank.getName() + "'");
		Executor.run("INSERT INTO `Ranks`(" + 
				"`forumId`, " + 
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
				"'" + (rank.getForumId() == null ? 0 : rank.getForumId()) + "', " + 
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
		if (Executor.DISABLE_SQL) return;
		Executor.run("DELETE FROM `Complaints` WHERE `id` = '" + comp.getId() + "'");
		Executor.run("INSERT INTO `Complaints`(" + 
				"`subforumId`, " + 
				"`id`, " + 
				"`complainer`, " + 
				"`complainee`, " + 
				"`complaintMessage`, " + 
				"`date`" + 
			") VALUES (" + 
				"'" + comp.getSubforumId() + "', " + 
				"'" + comp.getId() + "', " + 
				"'" + comp.getComplainer() + "', " + 
				"'" + comp.getComplainee() + "', " + 
				"'" + comp.getComplaintMessage() + "', " + 
				"'" + comp.getDate() + "'" + 
			")");
	}

	public static void save(User user) throws ClassNotFoundException, SQLException {
		if (Executor.DISABLE_SQL) return;
		if (user.getMail() == null) return;
		Executor.run("DELETE FROM `Users` WHERE `username` = '" + user.getUsername() + "'");
		Executor.run("INSERT INTO `Users`(" + 
				"`forumId`, " + 
				"`mail`, " + 
				"`name`, " + 
				"`username`, " + 
				"`password`, " + 
				"`rank`, " + 
				"`notifTypes`, " + 
				"`firstLogin`, " + 
				"`code`" +  
			") VALUES (" + 
				"'" + (user.getForumId() == null ? 0 : user.getForumId()) + "', " + 
				"'" + user.getMail() + "', " + 
				"'" + user.getName() + "', " + 
				"'" + user.getUsername() + "', " + 
				"'" + user.getPassword() + "', " + 
				"'" + user.getRank().getName() + "', " + 
				"'" + user.getNotifType() + "', " + 
				"'" + (user.isFirstLogin() ? "1" : "0") + "', " + 
				"'" + user.getMailCode() + "'" + 
			")");
	}
	
	public static void saveFriend(String tblName, User user, User user2) {
		try {
			Executor.run("DELETE FROM `" + tblName + "` WHERE `user1` = '" + user.getUsername() + "' AND `user2` = '" + user2.getUsername() + "'");
			Executor.run("INSERT INTO `" + tblName + "`(" + 
					"`forumId`, " + 
					"`user1`, " + 
					"`user2`" + 
				") VALUES (" + 
					"'" + user.getForumId() + "', " + 
					"'" + user.getUsername() + "', " + 
					"'" + user2.getUsername() + "'" + 
				")");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void save(Message msg) throws ClassNotFoundException, SQLException {
		if (Executor.DISABLE_SQL) return;
		remove(msg);
		Executor.run("INSERT INTO `Messages`(" + 
				"`msgRel`, " + 
				"`subforumRel`, " + 
				"`id`, " + 
				"`date`, " + 
				"`content`, " + 
				"`title`, " + 
				"`writer`" +
			") VALUES (" + 
			"'" + (msg.getMsgRel() == null ? 0 : msg.getMsgRel()) + "', " + 
			"'" + (msg.getSubforumId() == null ? 0 : msg.getSubforumId()) + "', " + 
				"'" + msg.getId() + "', " + 
				"'" + msg.getDate() + "', " + 
				"'" + msg.getContent() + "', " + 
				"'" + msg.getTitle() + "', " + 
				"'" + msg.getUser().getUsername() + "'" + 
			")");
	}
	
	public static void remove(Message msg) {
		try {
			Executor.run("DELETE FROM `Messages` WHERE `id` = '" + msg.getId() + "'");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void save(SubForum sf) throws ClassNotFoundException, SQLException {
		if (Executor.DISABLE_SQL) return;
		remove(sf);
		Executor.run("INSERT INTO `SubForums`(" + 
				"`forumId`, " + 
				"`id`, " + 
				"`subject`" +
			") VALUES (" + 
				"'" + sf.getForumId() + "', " + 
				"'" + sf.getId() + "', " + 
				"'" + sf.getSubject() + "'" + 
			")");
	}
	
	public static void remove(SubForum sf) {
		try {
			Executor.run("DELETE FROM `SubForums` WHERE `id` = '" + sf.getId() + "'");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void saveModerator(String subforumId, User user) {
		try {
			Executor.run("DELETE FROM `_moderators` WHERE `subforumId` = '" + subforumId + "' AND `username` = '" + user.getUsername() + "'");
			Executor.run("INSERT INTO `_moderators`(`subforumId`, `username`) VALUES ('" + (subforumId == null ? 0 : subforumId) + "', '" + user.getUsername() + "')");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void removeModerator(String subforumId, User user) {
		try {
			Executor.run("DELETE FROM `_moderators` WHERE `subforumId` = '" + subforumId + "' AND `username` = '" + user.getUsername() + "'");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void saveSuspended(String subforumId, User user, String date) {
		try {
			Executor.run("DELETE FROM `_suspended` WHERE `subforumId` = '" + subforumId + "'' AND `username` = '" + user.getUsername() + "'");
			Executor.run("INSERT INTO `_suspended`(`subforumId`, `username`, `date`) VALUES ('" + subforumId + "', '" + user.getUsername() + "', '" + date + "')");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void removeSuspended(String subforumId, User user, String date) {
		try {
			Executor.run("DELETE FROM `_suspended` WHERE `subforumId` = '" + subforumId + "'' AND `username` = '" + user.getUsername() + "'");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void save(Forum forum) throws ClassNotFoundException, SQLException {
		if (Executor.DISABLE_SQL) return;
		Executor.run("DELETE FROM `Forums` WHERE `id` = '" + forum.getId() + "'");
		Executor.run("INSERT INTO `Forums`(" + 
				"`id`, " + 
				"`name`" +
			") VALUES (" + 
				"'" + forum.getId() + "', " + 
				"'" + forum.getName() + "'" + 
			")");
	}
	
	public static void saveAdmin(String forumId, User user) {
		try {
			removeAdmin(forumId, user);
			Executor.run("INSERT INTO `_administrators`(`ForumId`, `Username`) VALUES ('" + forumId + "', '" + user.getUsername() + "')");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void removeAdmin(String forumId, User user) {
		try {
			Executor.run("DELETE FROM `_administrators` WHERE `ForumId` = '" + forumId + "' AND `Username` = '" + user.getUsername() + "'");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
