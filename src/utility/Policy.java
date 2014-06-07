package utility;

/** THis is the policy **/
public class Policy {
	private String info;
	public boolean[] rules;
	
	public Policy(){
		for (int i=0; i < PolicyRules.values().length; i++) {
			rules[i] = false; 
		}
	}
	
	public Policy(boolean[] rules){
		this.rules = rules; 
	}
	
	public boolean ruleActive(PolicyRules rule){
		return rules[rule.ordinal()];
	}
	
	
	//dafuq are these?
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
