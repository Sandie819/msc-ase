package msc.refactor.jcodecleaner.enums;

import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.MoveMethodRefactoring;

import org.eclipse.ltk.core.refactoring.Refactoring;

public enum RefactoringEnum {

	EXTRACT_METHOD("Extract Method", ExtractClassRefactoring.class),
	MOVE_METHOD("Move Method", MoveMethodRefactoring.class),
//	REPLACE_METHOD("Replace Method with method object"),
//	REPLACE_DATA_VALUE_WITH_OBJECT("Replace data value with object"),
	EXTRACT_CLASS("Extract Class", ExtractClassRefactoring.class);

	private String refactoringName;
	private Class<? extends Refactoring> refactoringType;
	
	RefactoringEnum(String refactoringName, Class<? extends Refactoring> refactoringType) {
		this.refactoringName = refactoringName;
		this.refactoringType = refactoringType;
	}

	public String getRefactoringName() {
		return refactoringName;
	}

	public void setRefactoringName(String refactoringName) {
		this.refactoringName = refactoringName;
	}

	public Class<? extends Refactoring> getRefactoringType() {
		return refactoringType;
	}

	public void setRefactoringType(Class<? extends Refactoring> refactoringType) {
		this.refactoringType = refactoringType;
	}
	
}
