package msc.refactor.jcodecleaner.enums;

public enum BadSmellEnum {
	
	DUPLICATED_CODE("Duplicated Code"),
	GOD_CLASS("God Class"),
	DIVERGENT_CHANGE("Divergent Change"),
	SHOTGUN_SURGERY("Shotgun Surgery "),
	FEATURE_ENVY("Feature Envy"),
	LAZY_CLASS("Lazy Class"),
	MIDDLE_MAN("Middle Man"),
	INAPPROPRIATE_INTIMACY("Inappropriate Intimacy");
	
	private String badSmellName;
	
	BadSmellEnum(String badSmellName) {
		this.badSmellName = badSmellName;
	}

	public String getBadSmellName() {
		return badSmellName;
	}

	public void setBadSmellName(String badSmellName) {
		this.badSmellName = badSmellName;
	}
	
	
}
