package utility;

import forumSystemCore.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class Rank {
	private String name;
	private ArrayList<Permissions> permissions;
	public static Rank superUser = Rank.rankSuperUser();
	public static Rank admin = Rank.rankAdmin();
	public static Rank moderator = Rank.rankModerator();
	public static Rank member = Rank.rankMember();

	public Rank(String name) {
		this.name = name;
		this.permissions = new ArrayList<Permissions>();
	}

	public static Rank rankSuperUser(){
		Rank r = new Rank("SuperUser");
		r.addPermission(Permissions.CREATE_FORUM);
		r.addPermission(Permissions.SET_FORUM_PROPERTIES);
		r.addPermission(Permissions.CREATE_SUB_FORUM);
		r.addPermission(Permissions.CREATE_MESSAGE);
		r.addPermission(Permissions.SET_RANKS);
		r.addPermission(Permissions.SET_USER_RANK);
		r.addPermission(Permissions.DELETE_MESSAGE);
		r.addPermission(Permissions.DELETE_SUB_FORUM);
		r.addPermission(Permissions.ADD_ADMIN);
		r.addPermission(Permissions.REMOVE_ADMIN);
		r.addPermission(Permissions.ADD_MODERATOR);
		r.addPermission(Permissions.REMOVE_MODERATOR);
		return r;	
	}

	public static Rank rankAdmin(){
		Rank r = new Rank("Admin");
		r.addPermission(Permissions.SET_FORUM_PROPERTIES);
		r.addPermission(Permissions.CREATE_SUB_FORUM);
		r.addPermission(Permissions.CREATE_MESSAGE);
		r.addPermission(Permissions.SET_RANKS);
		r.addPermission(Permissions.SET_USER_RANK);
		r.addPermission(Permissions.DELETE_MESSAGE);
		r.addPermission(Permissions.DELETE_SUB_FORUM);
		r.addPermission(Permissions.ADD_ADMIN);
		r.addPermission(Permissions.REMOVE_ADMIN);
		r.addPermission(Permissions.ADD_MODERATOR);
		r.addPermission(Permissions.REMOVE_MODERATOR);
		return r;	
	}

	public static Rank rankModerator(){
		Rank r = new Rank("Moderator");
		r.addPermission(Permissions.CREATE_MESSAGE);
		r.addPermission(Permissions.DELETE_MESSAGE);
		return r;	
	}

	public static Rank rankMember(){
		Rank r = new Rank("Member");
		r.addPermission(Permissions.CREATE_MESSAGE);
		return r;	
	}

	/**
	 * @return the Permissions on this rank
	 */
	public ArrayList<Permissions> getPermissions() {
		return permissions;
	}

	/**
	 * add Permissions to this rank type
	 * 
	 * @param Permissionss
	 *            ArrayList to add
	 */
	public void setPermissions(ArrayList<Permissions> Permissions) {
		this.permissions = Permissions;
		save();
	}

	public void addPermission(Permissions permissions) {
		this.permissions.add(permissions);
		save();
	}

	/**
	 * check if this rank got a specific Permissions
	 * 
	 * @param p
	 *            the Permissions
	 * @return true or false
	 */
	public boolean hasPermission(Permissions p) {
		if (permissions.contains(p))
			return true;
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public void save() {
		try {
			sql.Query.save(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
