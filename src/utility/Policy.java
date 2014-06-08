package utility;

/** This is the default policy **/
public class Policy {
	public boolean[] rules;
	private int minPassLength = 6;
	private boolean lettersInPass = false;
	private boolean capsLockInPass = false;
	
	
	public Policy(){
		rules = new boolean[PolicyRules.values().length];
		for (int i=0; i < PolicyRules.values().length; i++) {
			rules[i] = false; 
		}
	}
	
	public Policy(boolean[] rules){
		this.rules = rules; 
	}
	
	public void setPassRule(int minLength, boolean letters, boolean capsLock){
		minPassLength = minLength;
		lettersInPass = letters;
		capsLockInPass = capsLock;
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
