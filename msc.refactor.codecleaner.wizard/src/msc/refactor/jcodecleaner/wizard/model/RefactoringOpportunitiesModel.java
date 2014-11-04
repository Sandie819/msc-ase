package msc.refactor.jcodecleaner.wizard.model;

import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

public class RefactoringOpportunitiesModel {

	private Set<ASTSliceGroup> extractMethodOpportunities;
	private Set<ExtractClassCandidateGroup> extractClassOpportunities;
	private List<MoveMethodCandidateRefactoring> moveMethodOpportunities;	
	private Set<RefactoringEnum> availableRefactorings;
	
	public RefactoringOpportunitiesModel(){
		extractMethodOpportunities = new TreeSet<ASTSliceGroup>();
		extractClassOpportunities = new TreeSet<ExtractClassCandidateGroup>();
		moveMethodOpportunities = new ArrayList<MoveMethodCandidateRefactoring>();
		availableRefactorings = new TreeSet<RefactoringEnum>();		
	}
	
	
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
		
	
	public Set<RefactoringEnum> getAvailableRefactorings() {
		return availableRefactorings;
	}
	public void setAvailableRefactorings(Set<RefactoringEnum> availableRefactorings) {
		this.availableRefactorings = availableRefactorings;
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
	
	public void addRefactoringOption(RefactoringEnum availableRefactoring) {
		if(availableRefactorings==null){
			availableRefactorings = new HashSet<RefactoringEnum>();
		}
		availableRefactorings.add(availableRefactoring);
	}
	
}
