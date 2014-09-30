package msc.refactor.codecleaner.metrics.cohesion;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class CalculateCohesionMetrics {

	private LCOM lcom;

	public void calculate(IFile file) {
		lcom = new LCOM();
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

	}
}
