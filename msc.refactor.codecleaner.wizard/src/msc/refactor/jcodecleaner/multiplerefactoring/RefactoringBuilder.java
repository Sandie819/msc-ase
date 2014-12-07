package msc.refactor.jcodecleaner.multiplerefactoring;

import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.MoveMethodRefactoring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msc.refactor.jcodecleaner.analyser.Standalone;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class RefactoringBuilder {

	private boolean classFound = false;
	
	/**
	 * @param subProgressMonitor 
	 * @param compilationUnit
	 */
	public ExtractMethodRefactoring getExtractedMethodRefactoring(ICompilationUnit iCompilationUnit, IFile file){
		ExtractMethodRefactoring extractMethodRefactoring = null;

		Set<ASTSliceGroup> extractMethodOpportunities = Standalone.getExtractMethodRefactoringOpportunitiesForClass(iCompilationUnit.getJavaProject(),file);

		for (ASTSliceGroup astSliceGroup : extractMethodOpportunities) {

			for (ASTSlice astSlice : astSliceGroup.getCandidates()) {

				CompilationUnit compilationUnit = parse(new NullProgressMonitor(),
						iCompilationUnit);

				extractMethodRefactoring = new ExtractMethodRefactoring(
						compilationUnit, astSlice);

			}
		}
		return extractMethodRefactoring;
	}
	
	/**
	 * @param extractClassOpportunities
	 * @return
	 */
	public Set<ExtractClassRefactoring> getExtractedClassRefactoring(ICompilationUnit compilationUnit, IFile file) {
		Set<ExtractClassRefactoring> extractClassRefactorings = new HashSet<ExtractClassRefactoring>();
	
		Set<ExtractClassCandidateGroup> extractClassOpportunities = 
				Standalone.getExtractClassRefactoringOpportunitiesForClass(compilationUnit.getJavaProject(), file);
		
		for (ExtractClassCandidateGroup candidateGroup : extractClassOpportunities) {

			ExtractClassCandidateRefactoring candidateRefactoring = candidateGroup.getCandidates().get(0);

			IFile sourceFile = candidateRefactoring.getSourceIFile();
			CompilationUnit sourceCompilationUnit = (CompilationUnit)candidateRefactoring.getSourceClassTypeDeclaration().getRoot();
			
			String[] tokens = candidateRefactoring.getTargetClassName().split("\\.");
			String extractedClassName = tokens[tokens.length-1];

			validateNewClassName(extractedClassName, sourceFile);			

			Set<VariableDeclaration> extractedFieldFragments = candidateRefactoring.getExtractedFieldFragments();
			Set<MethodDeclaration> extractedMethods = candidateRefactoring.getExtractedMethods();
			ExtractClassRefactoring extractClassRefactoring = new ExtractClassRefactoring(
					sourceFile, sourceCompilationUnit,
					candidateRefactoring.getSourceClassTypeDeclaration(),
					extractedFieldFragments, extractedMethods,
					candidateRefactoring.getDelegateMethods(), extractedClassName);

			extractClassRefactorings.add(extractClassRefactoring);
		}
		return extractClassRefactorings;
	}	
	
	/**
	 * @param extractedClassName
	 * @param sourceFile
	 */
	private void validateNewClassName(String extractedClassName, IFile sourceFile) {
		boolean tryAgain = true;
		
		int classNo = 0;
		if(duplicateClassName(extractedClassName, sourceFile)){
			while(tryAgain) {
				classNo++;
				int classNameLength = extractedClassName.length();				
				String classNumber = extractedClassName.substring(classNameLength - 1);				
				if(classNumber.matches("-?\\d+(\\.\\d+)?")) {
					int newClassNumber = Integer.valueOf(classNumber);	
					newClassNumber++;
					String newExtractedClassName = extractedClassName.substring(0, classNameLength - 1) +
							String.valueOf(newClassNumber);
					if(!duplicateClassName(newExtractedClassName, sourceFile)){
						extractedClassName = newExtractedClassName;
						tryAgain = false;
					}
				} else {
					String newExtractedClassName = extractedClassName+String.valueOf(classNo);
					if(!duplicateClassName(newExtractedClassName, sourceFile)){
						extractedClassName = newExtractedClassName;
						tryAgain = false;
					}
				}
			}
		}
	}

	/**
	 * @param extractedClassName
	 * @param sourceFile
	 * @return
	 */
	private boolean duplicateClassName(final String extractedClassName, IFile sourceFile) {

		classFound = false;

		IJavaProject javaProject = JavaCore.create(sourceFile.getProject()); 

		SearchPattern pattern = SearchPattern.createPattern(  
				extractedClassName,   
				IJavaSearchConstants.CLASS,   
				IJavaSearchConstants.DECLARATIONS,   
				SearchPattern.R_EXACT_MATCH);  

		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { javaProject });  

		SearchEngine searchEngine = new SearchEngine();		
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				IJavaElement e = (IJavaElement) match.getElement();
				if(e!=null) {				
					System.out.println("File already exist: "+ extractedClassName);
					classFound = true;
				}
			}
		};
		try {
			searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
					requestor, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return classFound;
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

	/**
	 * @param controller
	 * @return
	 */
	public Set<MoveMethodRefactoring> getMoveMethodRefactoring(ICompilationUnit compilationUnit, IFile file) {
		
		Set<MoveMethodRefactoring> moveMethodRefactorings = new HashSet<MoveMethodRefactoring>();
		
		List<MoveMethodCandidateRefactoring> moveMethodCandidates = 
				Standalone.getMoveMethodRefactoringOpportunities(compilationUnit.getJavaProject(), file);
		
		for(MoveMethodCandidateRefactoring candidateRefactoring: moveMethodCandidates) {

			CompilationUnit sourceCompilationUnit = (CompilationUnit)candidateRefactoring.getSourceClassTypeDeclaration().getRoot();			
			CompilationUnit targetCompilationUnit = (CompilationUnit)candidateRefactoring.getTargetClassTypeDeclaration().getRoot();

			MoveMethodRefactoring moveMethod = new MoveMethodRefactoring(
					sourceCompilationUnit, targetCompilationUnit, 
					candidateRefactoring.getSourceClassTypeDeclaration(), 
					candidateRefactoring.getTargetClassTypeDeclaration(), 
					candidateRefactoring.getSourceMethodDeclaration(), 
					candidateRefactoring.getAdditionalMethodsToBeMoved(), 
					candidateRefactoring.leaveDelegate(), 
					candidateRefactoring.getMovedMethodName());

			moveMethodRefactorings.add(moveMethod);
		}
		return moveMethodRefactorings;
	}
}
