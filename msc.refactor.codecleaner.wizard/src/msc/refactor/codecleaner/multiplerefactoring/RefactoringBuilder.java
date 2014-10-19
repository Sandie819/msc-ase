package msc.refactor.codecleaner.multiplerefactoring;

import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import java.util.Set;

import msc.refactor.codecleaner.wizard.controller.WizardController;
import msc.refactor.codecleaner.wizard.model.RefactoringOpportunities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class RefactoringBuilder {

	/**
	 * @param compilationUnit
	 */
	public ExtractMethodRefactoring getExtractedMethodRefactoring(WizardController controller) {
		ExtractMethodRefactoring extractMethodRefactoring = null;
		
		IFile file = controller.getModel().getFileFromStructuredSelection();
		ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(file);

		RefactoringOpportunities opportunitites = controller.getModel()
				.getRefactoringOpportunities();
		
		Set<ASTSliceGroup> extractMethodOpportunities = opportunitites
				.getExtractMethodOpportunities();

		for (ASTSliceGroup astSliceGroup : extractMethodOpportunities) {

			for (ASTSlice astSlice : astSliceGroup.getCandidates()) {

				IProgressMonitor monitor = new NullProgressMonitor();
				CompilationUnit compilationUnit = parse(monitor,
						iCompilationUnit);
				
				extractMethodRefactoring = new ExtractMethodRefactoring(
					compilationUnit, astSlice);

			}
		}
		return extractMethodRefactoring;
	}

	/**
	 * Parse ICompilationUnit
	 * @param monitor
	 * @param compilationUnit
	 * @return
	 */
	protected CompilationUnit parse(IProgressMonitor monitor, ICompilationUnit compilationUnit) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		return (CompilationUnit) parser.createAST(monitor);
	}
}
