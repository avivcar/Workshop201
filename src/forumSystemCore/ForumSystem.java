package forumSystemCore;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import utility.LogFile;
import user.User;
import utility.*;


public class ForumSystem {
	LogFile operationLog;
	LogFile errorLog;
	ArrayList<Forum> forums;
	User superuser;

	// Constructors:
	public User startSystem(String email, String name, String username,
			String password) {
		try {
			sql.Query.initDB();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!TextVerifier.verifyName(username, new Policy())
				|| !TextVerifier.verifyEmail(email)
				|| !TextVerifier.verifyPassword(password, new Policy())
				|| name.equals(""))
			return null;
		superuser = new User(email, name, username, password, Rank.rankSuperUser());
		operationLog = new LogFile("Operation.txt");
		errorLog = new LogFile("Error.txt");
		forums = new ArrayList<Forum>();
		return superuser;
	}

	public String createForum(String name, User admin) {
		if (!admin.hasPermission(Permissions.CREATE_FORUM) || name.equals(""))
			return null;

		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getName().equals(name))
				return null;
		}
		Forum newForum = new Forum(name, admin);
		forums.add(newForum);
		return newForum.getId();
	}
	
/*
	 public static void main(String[] args) { // TODO Auto-generated method

			
	 }
*/
	
	/**
	 * check if user is admin of forum(forumId)
	 * 
	 * @param forumId
	 * @param user
	 * @return
	 */
	public boolean isAdmin(String forumId, User user) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).isAdmin(user);
		}
		return false;
	}

	/**
	 * sign up to forum(forumId) using this parameters
	 * 
	 * @param mail
	 * @param name
	 * @param username
	 * @param pass
	 * @param forumId
	 * @return
	 */
	public User signup(String mail, String name, String username, String pass,
			String forumId) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).signup(mail, name, username, pass);
		}
		return null;
	}

	/**
	 * check if user is a member of forum(forumId)
	 * 
	 * @param forumId
	 * @param user
	 * @return
	 */
	public boolean isMember(String forumId, User user) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).isMember(user);
		}
		return false;

	}

	/**
	 * login with this parameters return the user(actual pointer) to the
	 * appropiant user;
	 * 
	 * @param username
	 * @param password
	 * @param forumId
	 * @return
	 */
	public User login(String username, String password, String forumId) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).login(username, password);
		}
		return null;
	}

	public String createSubForum(User invoker, User moderator,
			String subForumName, String forumId) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).createSubForum(invoker, moderator,
						subForumName);
		}
		return null;

	}

	public boolean existSubForum(String forumId, String subForumid) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).existSubForum(subForumid);
		}
		return false;
	}

	public Forum getForum(String forumId) {

		for (int i = 0; i < forums.size(); i++)
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i);
		return null;
	}

	public boolean existForum(String forumId) {
		for (int i = 0; i < forums.size(); i++)
			if (forums.get(i).getId().equals(forumId))
				return true;
		return false;
	}

	public String createMessage(String forumId, String subForumId, User user,
			String title, String content) {
		Forum forum = this.getForum(forumId);
		if (forum != null) {
			SubForum subforum = forum.getSubForumById(subForumId);
			if (subforum != null)
				return subforum.createMessage(user, title, content);
		}
		return null;

	}

}
