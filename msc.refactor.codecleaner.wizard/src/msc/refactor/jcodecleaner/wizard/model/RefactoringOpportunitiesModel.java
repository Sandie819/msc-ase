package msc.refactor.jcodecleaner.wizard.model;

import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

public class RefactoringOpportunitiesModel {

	private Set<ASTSliceGroup> extractMethodOpportunities;
	private Set<ExtractClassCandidateGroup> extractClassOpportunities;
	private List<MoveMethodCandidateRefactoring> moveMethodOpportunities;
	
	public Set<ASTSliceGroup> getExtractMethodOpportunities() {
		return extractMethodOpportunities;
	}
	public void setExtractMethodOpportunities(
			Set<ASTSliceGroup> extractMethodOpportunities) {
		this.extractMethodOpportunities = extractMethodOpportunities;
	}
	public Set<ExtractClassCandidateGroup> getExtractClassOpportunities() {
		return extractClassOpportunities;
	}
	public void setExtractClassOpportunities(
			Set<ExtractClassCandidateGroup> extractClassOpportunities) {
		this.extractClassOpportunities = extractClassOpportunities;
	}
	public List<MoveMethodCandidateRefactoring> getMoveMethodOpportunities() {
		return moveMethodOpportunities;
	}
	public void setMoveMethodOpportunities(
			List<MoveMethodCandidateRefactoring> moveMethodOpportunities) {
		this.moveMethodOpportunities = moveMethodOpportunities;
	}
	
	public boolean isExtractMethodAvailable(IFile file){
		IJavaElement sourceJavaElement = JavaCore.create(file);
		
		for(ASTSliceGroup astSliceGroup : extractMethodOpportunities){
		
			for(ASTSlice astSlice: astSliceGroup.getCandidates()) {
				if(sourceJavaElement.getElementName().equals(astSlice.getExtractedMethodName())){
					return true;	
				}
				
			}
		}
		return false;
	}
	
}
