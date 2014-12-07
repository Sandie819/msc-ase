package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public abstract class Metric {
	
	protected double threshold; 
	protected String metricFullName;
	protected String metricShortName;
	protected double metricValue;
	protected double originalMetricValue;
	
	protected List<RefactoringEnum> applicableRefactorings;
	
	public Metric(String metricFullName, String metricShortName, double threshold) {
		this.threshold = threshold;
		this.metricFullName = metricFullName;
		this.metricShortName = metricShortName;
	}
	
	public abstract double calculateMetricValue(IFile file, IProgressMonitor monitor);

	public double getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(double metricValue) {
		this.metricValue = metricValue;
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public String getMetricFullName() {
		return metricFullName;
	}

	public void setMetricFullName(String metricFullName) {
		this.metricFullName = metricFullName;
	}

	public String getMetricShortName() {
		return metricShortName;
	}

	public void setMetricShortName(String metricShortName) {
		this.metricShortName = metricShortName;
	}
	
	public double getOriginalMetricValue() {
		return originalMetricValue;
	}

	public void setOriginalMetricValue(double originalMetricValue) {
		this.originalMetricValue = originalMetricValue;
	}

	/**
	 * @return boolean 
	 */
	public boolean metricExceedsThreshold(){
		return (this.metricValue > this.threshold);
	}
	
	public Set<IMethod> getMethodsFromClass(ICompilationUnit compilationUnit) {
		Set<IMethod> methods = new HashSet<IMethod>();
				
		try {
			IType types[] = compilationUnit.getTypes();
			for (int i = 0; i < types.length; i++) {
				IType type = types[i];
				IMethod[] methodsArray = type.getMethods();
				for (int j = 0; j < methodsArray.length; j++) {
					IMethod method = methodsArray[j];
					methods.add(method);
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return methods;
	}

	public List<RefactoringEnum> getApplicableMetricRefactorings() {		
		applicableRefactorings = new ArrayList<RefactoringEnum>();
		
		applicableRefactorings.add(RefactoringEnum.EXTRACT_CLASS);
		applicableRefactorings.add(RefactoringEnum.EXTRACT_METHOD);
		applicableRefactorings.add(RefactoringEnum.MOVE_METHOD);
		
		return applicableRefactorings;
	}
	
	@SuppressWarnings("deprecation")
	public CompilationUnit parse(IProgressMonitor monitor, ICompilationUnit compilationUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit); 
		parser.setResolveBindings(true); 
		parser.setBindingsRecovery(true);
		return (CompilationUnit) parser.createAST(monitor);
	}
	
	@Override
	public String toString(){
		return "Metric Name: " +this.metricFullName+
				", metric Value: " +this.metricValue+
				", metric threshold "+this.threshold;
				
	}
}
