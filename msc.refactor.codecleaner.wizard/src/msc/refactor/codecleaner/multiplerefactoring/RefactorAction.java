package msc.refactor.codecleaner.multiplerefactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class RefactorAction {

	/**
	 * Renames the first method in the class -  
	 * this is just for getting to grips with calling eclipse refactorings 
	 * programmatically
	 * 
	 * @param compilationUnit
	 * @return
	 */
	public Refactoring getRenameFirstMethodRefactoring(ICompilationUnit compilationUnit) {
		IMethod firstMethod = null;
		RefactoringContribution contribution =
				RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_METHOD);		

		RefactoringStatus status = new RefactoringStatus();
		Refactoring refactoring = null;
		try {
			IType[] types = compilationUnit.getTypes();
			for (int i = 0; i < types.length; i++) {
				IType type = types[i];
				IMethod[] methods = type.getMethods();
				firstMethod = methods[0];
				System.out.println(firstMethod.getElementName());
				
				if(firstMethod.isConstructor()){
					continue;
				}
					
				RenameJavaElementDescriptor descriptor =
						(RenameJavaElementDescriptor) contribution.createDescriptor();
				descriptor.setProject(compilationUnit.getResource().getProject().getName());
				descriptor.setNewName(firstMethod.getElementName()+"RandomRename"); // new name for a Class
				descriptor.setJavaElement(firstMethod);
				descriptor.setUpdateReferences(true);
				
				refactoring = descriptor.createRefactoring(status);
			}

		} catch (CoreException e) {			
			e.printStackTrace();
		}
		
		return refactoring;
	}
	
	/**
	 * @param compilationUnit
	 * @return
	 */
	public Refactoring getRenameSelectedClassRefactoring(ICompilationUnit compilationUnit) {
		RefactoringStatus status = new RefactoringStatus();
		Refactoring refactoring = null;
		try {
			RefactoringContribution contribution =
					RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_COMPILATION_UNIT);
			RenameJavaElementDescriptor descriptor =
					(RenameJavaElementDescriptor) contribution.createDescriptor();
			descriptor.setProject(compilationUnit.getResource().getProject().getName());
			descriptor.setNewName("RandomRenamedTested101"); // new name for a Class
			descriptor.setJavaElement(compilationUnit);
			descriptor.setUpdateReferences(true);
			
			refactoring = descriptor.createRefactoring(status);
	
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return refactoring;
	}

}
