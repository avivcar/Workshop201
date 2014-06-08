package utility;
import java.util.regex.Pattern;

import forumSystemCore.Forum;


public class TextVerifier {
	

	/**
	 * verifies the name is legal by the given policy
	 * @param name
	 * @param p the policy
	 * @return true/false
	 */
	public static boolean verifyName(String name, Policy p){
		if (p.isLegaelName(name))
			return true;
		return false;
	}
	
	/**
	 * verifies the password is legal by the given policy
	 * @param pass
	 * @param p the policy
	 * @return true/false
	 */
	public static boolean verifyPassword(String pass, Policy p){
		if (p.isLegaelPass(pass))
			return true;
		return false;
	}
	
	/**
	 * verifies the string given is a legal email address
	 * @param email
	 * @return true/false
	 */
	public static boolean verifyEmail(String email, Forum forum){
		final Pattern rfc2822 = Pattern.compile(
		        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
		);

		if (!rfc2822.matcher(email).matches())
		   return false;
		//checks multiplicity
		if(forum!=null)
			for(int i =0; i< forum.getMembers().size(); i++){
				if(forum.getMembers().get(i).getMail().equals(email))
					return false;
			}
		return true;
	}

}
