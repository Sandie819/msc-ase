package msc.refactor.codecleaner.multiplerefactoring;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class MultipleRefactoringDescriptor extends RefactoringDescriptor {

	public static final String REFACTORING_ID= "msc.refactor.codecleaner.refactor";

	private final Map<?, ?> fArguments;

	public MultipleRefactoringDescriptor(String project, String description, 
			String comment, Map<?, ?> arguments) {
		
		super(REFACTORING_ID, project, description, comment, RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE);
		fArguments= arguments;
	}

	@Override
	public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
		MultipleRefactoring refactoring= new MultipleRefactoring();
		status.merge(refactoring.initialize(fArguments));
		return refactoring;
	}

	public Map<?, ?> getArguments() {
		return fArguments;
	}
}