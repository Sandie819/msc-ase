package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;

public class LCOM4 extends Metric  {

	public LCOM4() {
		super("Lack of Cohesion of Methods (4)", "LCOM4", 1);
	}

	public double calculateMetricValue(IFile file, IProgressMonitor monitor) {
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		Set<IMethod> methods = getMethodsFromClass(compilationUnit);
		
		double metricValue = computeLCOM4(LCOMUtil.computeMethodsSet(methods));
		setMetricValue(metricValue);
		
		return metricValue;
	}
	
	public int computeLCOM4(Collection<Set<IMethod>> sets) {       
		return sets.size();
	}
}