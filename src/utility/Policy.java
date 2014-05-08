package utility;
/** THis is the policy **/
public class Policy {
	private String info;
	
	public Policy(){
		
	}

	public boolean isLegaelName(String name) {
		if (name!=null && !name.equals(""))
			return true;
		return false;
	}

	public boolean isLegaelPass(String pass) {
		if (pass!=null && !pass.equals(""))
			return true;
		return false;
	}

}
