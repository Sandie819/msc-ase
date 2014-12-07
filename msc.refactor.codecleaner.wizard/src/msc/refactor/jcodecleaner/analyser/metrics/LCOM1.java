package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;

public class LCOM1 extends Metric  {

	public LCOM1() {
		super("Lack of Cohesion of Methods (1)", "LCOM1", 0);
	}

	public double calculateMetricValue(IFile file, IProgressMonitor monitor) {
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		Set<IMethod> methods = getMethodsFromClass(compilationUnit);
		
		double metricValue = computeLCOM1(LCOMUtil.computeMethodsSet(methods));
		setMetricValue(metricValue);
		
		return metricValue;
	}
	
	public int computeLCOM1(Collection<Set<IMethod>> sets) {
		int p = 0, q = 0;

		int allMethodsCount = 0;
		for(Set<IMethod> set: sets){
			allMethodsCount += set.size();                   
		}        

		for(Set<IMethod> set: sets){
			int setSize = set.size();
			assert (setSize != 0);
			int pairs = setSize * (setSize - 1);            

			q += pairs;

			p += (allMethodsCount - setSize) * setSize;           
		}

		int t = p - q;

		t /= 2;
		return (t > 0 ? t : 0);

	}

	@Override
	public List<RefactoringEnum> getApplicableMetricRefactorings() {		
		applicableRefactorings = new ArrayList<RefactoringEnum>();
		
		applicableRefactorings.add(RefactoringEnum.EXTRACT_CLASS);
		applicableRefactorings.add(RefactoringEnum.EXTRACT_METHOD);
		applicableRefactorings.add(RefactoringEnum.MOVE_METHOD);
		
		return applicableRefactorings;
	}

}