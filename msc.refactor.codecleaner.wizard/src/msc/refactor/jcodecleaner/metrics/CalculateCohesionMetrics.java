package msc.refactor.jcodecleaner.metrics;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class CalculateCohesionMetrics {

	private LcomMetric lcom;

	public LcomMetric calculate(IFile file) {
		lcom = new LcomMetric();
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		try {
			double lcomValue = lcom.calculateLCOM(compilationUnit);
			System.out.print("Lcom value is: " +lcomValue);
			
			//If lcomValue > 0 find refactoring suggestions
			// Refactoring suggestions.....
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lcom;
	}
}
