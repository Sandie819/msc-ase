package msc.refactor.jcodecleaner.multiplerefactoring;

import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import java.util.HashSet;
import java.util.Set;

import msc.refactor.jcodecleaner.wizard.controller.WizardController;
import msc.refactor.jcodecleaner.wizard.model.RefactoringOpportunitiesModel;

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

		RefactoringOpportunitiesModel opportunitites = controller.getModel()
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
	 * @param compilationUnit
	 */
	public Set<ExtractClassRefactoring> getExtractedClassRefactoring(WizardController controller) {
		Set<ExtractClassRefactoring> extractClassRefactorings = new HashSet<ExtractClassRefactoring>();
	
		RefactoringOpportunitiesModel opportunitites = controller.getModel()
				.getRefactoringOpportunities();
		
		Set<ExtractClassCandidateGroup> extractClassOpportunities = opportunitites
				.getExtractClassOpportunities();

		for (ExtractClassCandidateGroup candidateGroup : extractClassOpportunities) {
			
			for (ExtractClassCandidateRefactoring candidateRefactoring : candidateGroup.getCandidates()) {
				IFile file = controller.getModel().getFileFromStructuredSelection();
				
				IProgressMonitor monitor = new NullProgressMonitor();
				CompilationUnit compilationUnit = parse(monitor,
						JavaCore.createCompilationUnitFrom(file));
				
				ExtractClassRefactoring extractClassRefactoring = new ExtractClassRefactoring(
						file, compilationUnit, candidateRefactoring.getSourceClassTypeDeclaration(), 
						candidateRefactoring.getExtractedFieldFragments(), 
						candidateRefactoring.getExtractedMethods(), candidateRefactoring.getDelegateMethods(), 
						candidateRefactoring.getTargetClassName());
				
				extractClassRefactorings.add(extractClassRefactoring);

			}
		}
		return extractClassRefactorings;
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
