package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.List;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;

public abstract class Metric {
	
	protected double threshold; 
	protected String metricFullName;
	protected String metricShortName;
	protected double metricValue;
	protected List<RefactoringEnum> applicableRefactorings;
	
	public Metric(String metricFullName, String metricShortName, double threshold) {
		this.threshold = threshold;
		this.metricFullName = metricFullName;
		this.metricShortName = metricShortName;
	}
	
	public abstract double calculateMetricValue(IFile file);

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

	/**
	 * @return boolean 
	 */
	public boolean metricExceedsThreshold(){
		return (this.metricValue > this.threshold);
	}

	public abstract List<RefactoringEnum> getApplicableMetricRefactorings();
	
	@Override
	public String toString(){
		return "Metric Name: " +this.metricFullName+
				", metric Value: " +this.metricValue+
				", metric threshold "+this.threshold;
				
	}
}
