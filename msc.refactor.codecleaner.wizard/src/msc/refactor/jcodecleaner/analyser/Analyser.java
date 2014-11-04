package msc.refactor.jcodecleaner.analyser;

import gr.uom.java.ast.Standalone;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import msc.refactor.jcodecleaner.analyser.metrics.DepthOfInheritanceTree;
import msc.refactor.jcodecleaner.analyser.metrics.LcomMetric;
import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.enums.RefactoringEnum;
import msc.refactor.jcodecleaner.wizard.model.RefactoringOpportunitiesModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class Analyser {

	private Set<RefactoringEnum> refactoringsForMetrics;
	private List<Metric> metrics;

	public Analyser(){
		refactoringsForMetrics = new HashSet<RefactoringEnum>();
		metrics = new ArrayList<Metric>();
	}

	public List<Metric> analyseSelection(IFile file) {
		buildMetricsList();
		runMetrics(file);

		return metrics;
	}
	
	private void buildMetricsList() {
		metrics.add(new LcomMetric());
		metrics.add(new DepthOfInheritanceTree());
	}

	public void runMetrics(IFile file) {

		for(Metric metric: metrics) {
			metric.calculateMetricValue(file);

			if(metric.metricExceedsThreshold()){
				refactoringsForMetrics.addAll(metric.getApplicableMetricRefactorings());
			}

			System.out.println(metric.toString());
		}
	}


	public double calculateFitnessFunction(){
		int fitness = metrics.size();
		for(Metric metric: metrics){
			if(metric.metricExceedsThreshold()){
				fitness = fitness -1;
			}
		}
		
		return fitness;
	}

	public RefactoringOpportunitiesModel identifyRefactoringOpportunities(IFile file){

		RefactoringOpportunitiesModel refactoringOpportunities = new RefactoringOpportunitiesModel();
		IJavaProject project = JavaCore.createCompilationUnitFrom(file).getJavaProject();

		for(RefactoringEnum suggestedRefactoring: refactoringsForMetrics){

			if(suggestedRefactoring==RefactoringEnum.EXTRACT_CLASS) {
				Set<ExtractClassCandidateGroup> extractedClassCandidates = Standalone.getExtractClassRefactoringOpportunitiesForClass(project, file);

				for(ExtractClassCandidateGroup candidateGroup: extractedClassCandidates){
					for(ExtractClassCandidateRefactoring candidate : candidateGroup.getCandidates()) {
						if(candidate.getSourceIFile().getFullPath().equals(file.getFullPath())){
							refactoringOpportunities.setExtractClassOpportunities(extractedClassCandidates);							
							refactoringOpportunities.addRefactoringOption(RefactoringEnum.EXTRACT_CLASS);							
						}
					}
				}		
			}

			if(suggestedRefactoring==RefactoringEnum.EXTRACT_METHOD) {
				Set<ASTSliceGroup> extractMethodOpportunities = Standalone.getExtractMethodRefactoringOpportunitiesForClass(project, file);

				for(ASTSliceGroup sliceGroup: extractMethodOpportunities){
					for(ASTSlice slice : sliceGroup.getCandidates()) {
						if(slice.getIFile().getFullPath().equals(file.getFullPath())){
							refactoringOpportunities.setExtractMethodOpportunities(extractMethodOpportunities);
							refactoringOpportunities.addRefactoringOption(RefactoringEnum.EXTRACT_METHOD);
						}
					}
				}		
			}

			if(suggestedRefactoring==RefactoringEnum.MOVE_METHOD) {
				List<MoveMethodCandidateRefactoring> moveMethodOpportunities = Standalone.getMoveMethodRefactoringOpportunities(project);

				for(MoveMethodCandidateRefactoring candidate: moveMethodOpportunities){
					if(candidate.getSourceIFile().getFullPath().equals(file.getFullPath())){
						refactoringOpportunities.setMoveMethodOpportunities(moveMethodOpportunities);
						refactoringOpportunities.addRefactoringOption(RefactoringEnum.MOVE_METHOD);
					}
				}		
			}

		}
		return refactoringOpportunities;

	}

}
