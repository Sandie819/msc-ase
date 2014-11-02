package msc.refactor.jcodecleaner.analyser;

import java.util.ArrayList;
import java.util.List;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;
import msc.refactor.jcodecleaner.metrics.LcomMetric;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class Analyser {

	//private static SystemObject systemObject;
//	private static ASTReader astReader;
//	private ConnectivityMetric connectivityMetric;
	private LcomMetric lcom;
	private List<RefactoringEnum> refactoringsForSuggestion;
	
	public Analyser(){
		refactoringsForSuggestion = new ArrayList<RefactoringEnum>();
		lcom = new LcomMetric();
	}
	
	public void runMetrics(IFile file) {
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		
		try {
			double lcomValue = lcom.calculateLCOM(compilationUnit);
			System.out.print("Lcom value is: " +lcomValue);
			
			//If lcomValue > 0 find refactoring suggestions
			
			if(lcomValue > 0) {
				addRefactoringSuggestionsForLackOfCohesion();
			}
			// Refactoring suggestions.....
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		connectivityMetric = new ConnectivityMetric(astReader.getSystemObject());
//		double connectivity = connectivityMetric.getSystemAverageConnectivity();
		
	}
	
	/**
	 * Add refactorings for lack of cohesion
	 */
	private void addRefactoringSuggestionsForLackOfCohesion() {
		refactoringsForSuggestion.add(RefactoringEnum.EXTRACT_CLASS);
		refactoringsForSuggestion.add(RefactoringEnum.EXTRACT_METHOD);
	}

	public void calculateFitnessFunction(){
		
	}
	
	public void identifyRefactoringOpportunities(){
		
	}

	public List<RefactoringEnum> analyseSelection(WizardController controller) {
		IFile file = controller.getModel().getFileFromStructuredSelection();
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		
//		IProgressMonitor monitor = new NullProgressMonitor();
//		astReader = new ASTReader(compilationUnit.getJavaProject(), monitor);
		
		runMetrics(file);
		
		return refactoringsForSuggestion;
	}
}
