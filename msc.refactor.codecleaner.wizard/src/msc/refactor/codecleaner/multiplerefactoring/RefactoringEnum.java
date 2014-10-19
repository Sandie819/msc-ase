package msc.refactor.codecleaner.multiplerefactoring;

public enum RefactoringEnum {

	EXTRACT_METHOD("Extract Method"),
	MOVE_METHOD("Move Method"),
	REPLACE_METHOD("Replace Method with method object"),
	REPLACE_DATA_VALUE_WITH_OBJECT("Replace data value with object"),
	EXTRACT_CLASS("Extract Class");

	private String refactoringName;
	
	RefactoringEnum(String refactoringName) {
		this.refactoringName = refactoringName;
	}

	public String getRefactoringName() {
		return refactoringName;
	}

	public void setRefactoringName(String refactoringName) {
		this.refactoringName = refactoringName;
	}
}
