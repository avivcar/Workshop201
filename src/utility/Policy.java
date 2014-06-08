package utility;

import java.util.regex.Pattern;

/** This is the default policy **/
public class Policy {
	public boolean[] rules;
	private int minPassLength = 4;
	private boolean lettersInPass = false;
	private boolean capsLockInPass = false;
	private int maxModerators = 3;
	private int maxComplaintsOnMod = 10;
	
	public Policy(){
		rules = new boolean[PolicyRules.values().length];
		for (int i=0; i < PolicyRules.values().length; i++) {
			rules[i] = false; 
		}
	}
	
	public Policy(boolean[] rules, int moderators, int complaints,
			int minLength, boolean letters, boolean capsLock){
		this.rules = rules; 
		setMaxModerators(moderators);
		setMaxComplaintsOnMod(complaints);
		minPassLength = minLength;
		lettersInPass = letters;
		capsLockInPass = capsLock;
	}
	
	
	public boolean ruleActive(PolicyRules rule){
		return rules[rule.ordinal()];
	}
	
	
	public boolean isLegaelName(String name) {
		if (name!=null && !name.equals(""))
			return true;
		return false;
	}

	public boolean isLegaelPass(String pass) {
		if (pass.length()<minPassLength)
			return false;
		if (lettersInPass)
			if (!pass.contains("[a-zA-Z]+"))
			     return false;   
		if (capsLockInPass)
			if (!pass.contains("[A-Z]+"))
			     return false;   
		return true;
	}
	
	//g & s
	
	public int getMaxComplaintsOnMod() {
		return maxComplaintsOnMod;
	}

	public int getMaxModerators() {
		return maxModerators;
	}

	public void setMaxModerators(int maxModerators) {
		this.maxModerators = maxModerators;
	}

	public void setMaxComplaintsOnMod(int maxComplaintsOnMod) {
		this.maxComplaintsOnMod = maxComplaintsOnMod;
	}

}
