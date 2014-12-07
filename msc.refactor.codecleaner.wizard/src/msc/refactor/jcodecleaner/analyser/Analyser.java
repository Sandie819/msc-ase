package msc.refactor.jcodecleaner.analyser;

import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.enums.RefactoringEnum;
import msc.refactor.jcodecleaner.wizard.model.RefactoringOpportunitiesModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Class which generates metrics and identifies
 * refactoring opportunities 
 * 
 * @author mulligans
 *
 */
public class Analyser {

	private Set<RefactoringEnum> refactoringsForMetrics;

	public Analyser(){
		refactoringsForMetrics = new HashSet<RefactoringEnum>();
	}

	/**
	 * @param file
	 * @param metrics
	 */
	public void analyseSelectionAndUpdateMetricValues(IFile file, List<Metric> metrics) {
		for(Metric metric: metrics) {
			metric.calculateMetricValue(file, new NullProgressMonitor());

			if(metric.metricExceedsThreshold()){
				refactoringsForMetrics.addAll(metric.getApplicableMetricRefactorings());
			}

			System.out.println(metric.toString());
		}
	}
	
	/**
	 * @param file
	 * @param metrics
	 */
	public void analyseSelectionAndGetMetricValues(IFile file, List<Metric> newMetrics,
			IProgressMonitor monitor) {
		
		for(Metric metric: newMetrics) {
			metric.calculateMetricValue(file, new SubProgressMonitor(monitor, 3));
		}
	}

	/**
	 * @param identifiedRefactorings
	 * @param metrics
	 * @return
	 */
	public double calculateFitnessFunction(Set<RefactoringEnum> identifiedRefactorings,
			List<Metric> metrics){
		double fitness = 0;
		for(Metric metric: metrics) {							
				List<RefactoringEnum> applicableMetricRefactorings = metric.getApplicableMetricRefactorings();
						
				for(RefactoringEnum metricRefactoring: applicableMetricRefactorings){
					if(identifiedRefactorings.contains(metricRefactoring)) {
						fitness++;
					}
				}
				if(metric.metricExceedsThreshold()) {
					fitness = fitness+metric.getMetricValue();
				}
		}
		
		return fitness;
	}

	/**
	 * Identifies refactoring opportunities for given class file
	 * @param file
	 * @return RefactoringOpportunitiesModel
	 */
	public RefactoringOpportunitiesModel identifyRefactoringOpportunities(IFile file){

		RefactoringOpportunitiesModel refactoringOpportunities = new RefactoringOpportunitiesModel();
		IJavaProject project = JavaCore.createCompilationUnitFrom(file).getJavaProject();

		for(RefactoringEnum suggestedRefactoring: refactoringsForMetrics){

			if(suggestedRefactoring==RefactoringEnum.EXTRACT_CLASS) {
				Set<ExtractClassCandidateGroup> extractedClassCandidates = Standalone.getExtractClassRefactoringOpportunitiesForClass(project, file);

				if(!extractedClassCandidates.isEmpty()) {
					refactoringOpportunities.setExtractClassOpportunities(extractedClassCandidates);							
					refactoringOpportunities.addRefactoringOption(RefactoringEnum.EXTRACT_CLASS);
				}		
			}

			if(suggestedRefactoring==RefactoringEnum.EXTRACT_METHOD) {
				Set<ASTSliceGroup> extractMethodOpportunities = Standalone.getExtractMethodRefactoringOpportunitiesForClass(project, file);

				if(!extractMethodOpportunities.isEmpty()) {
					refactoringOpportunities.setExtractMethodOpportunities(extractMethodOpportunities);
					refactoringOpportunities.addRefactoringOption(RefactoringEnum.EXTRACT_METHOD);
				}
			}

			if(suggestedRefactoring==RefactoringEnum.MOVE_METHOD) {
				List<MoveMethodCandidateRefactoring> moveMethodOpportunities = Standalone.getMoveMethodRefactoringOpportunities(project, file);

				if(!moveMethodOpportunities.isEmpty()){
					refactoringOpportunities.setMoveMethodOpportunities(moveMethodOpportunities);
					refactoringOpportunities.addRefactoringOption(RefactoringEnum.MOVE_METHOD);
				}	
			}			
		}
		return refactoringOpportunities;

	}

}
