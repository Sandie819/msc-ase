package msc.refactor.codecleaner.analyser;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.ast.metrics.ConnectivityMetric;
import msc.refactor.codecleaner.metrics.cohesion.CalculateCohesionMetrics;
import msc.refactor.codecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;

public class Analyser {

	private static SystemObject systemObject;
	private static ASTReader astReader;
	private ConnectivityMetric connectivityMetric;
	
	public void runMetrics(IFile file) {
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		
		CalculateCohesionMetrics calculateCohesionMetrics = new CalculateCohesionMetrics();
		calculateCohesionMetrics.calculate(file);
		
		connectivityMetric = new ConnectivityMetric(astReader.getSystemObject());
		double connectivity = connectivityMetric.getSystemAverageConnectivity();
		
	}
	
	public void calculateFitnessFunction(){
		
	}
	
	public void identifyRefactoringOpportunities(){
		
	}

	public void analyseSelection(WizardController controller) {
		IFile file = controller.getModel().getFileFromStructuredSelection();
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		
		IProgressMonitor monitor = new NullProgressMonitor();
		astReader = new ASTReader(compilationUnit.getJavaProject(), monitor);
		
		runMetrics(file);
	}
}
