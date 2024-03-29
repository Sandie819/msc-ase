package msc.refactor.jcodecleaner.analyser;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.ast.decomposition.cfg.CFG;
import gr.uom.java.ast.decomposition.cfg.PDG;
import gr.uom.java.ast.decomposition.cfg.PDGObjectSliceUnion;
import gr.uom.java.ast.decomposition.cfg.PDGObjectSliceUnionCollection;
import gr.uom.java.ast.decomposition.cfg.PDGSliceUnion;
import gr.uom.java.ast.decomposition.cfg.PDGSliceUnionCollection;
import gr.uom.java.ast.decomposition.cfg.PlainVariable;
import gr.uom.java.ast.util.StatementExtractor;
import gr.uom.java.distance.DistanceMatrix;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.distance.MySystem;
import gr.uom.java.jdeodorant.preferences.PreferenceConstants;
import gr.uom.java.jdeodorant.refactoring.Activator;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckEliminationGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Copied and Modified version Standalone.java from JDeodorant 
 *
 */
public class Standalone {

	public static List<MoveMethodCandidateRefactoring> getMoveMethodRefactoringOpportunities(IJavaProject project, IFile file) {
		CompilationUnitCache.getInstance().clearCache();
		new ASTReader(project, null);
		SystemObject systemObject = ASTReader.getSystemObject();
		
		Set<String> classNamesToBeExamined = new LinkedHashSet<String>();
		
		for(ClassObject classObject : systemObject.getClassObjects()){
			if(classObject.getIFile().getFullPath().equals(file.getFullPath())
					&& !classObject.isEnum()) {
				classNamesToBeExamined.add(classObject.getName());
			}
		}
		
		MySystem system = new MySystem(systemObject, false);
		DistanceMatrix distanceMatrix = new DistanceMatrix(system);
		distanceMatrix.generateDistances(null);
		
		List<MoveMethodCandidateRefactoring> moveMethodCandidateList = new ArrayList<MoveMethodCandidateRefactoring>();
		moveMethodCandidateList.addAll(distanceMatrix.getMoveMethodCandidateRefactoringsByAccess(classNamesToBeExamined, null));
		Collections.sort(moveMethodCandidateList);
		
		return moveMethodCandidateList;
	}
	
	public static Set<ExtractClassCandidateGroup> getExtractClassRefactoringOpportunitiesForClass(IJavaProject project, IFile file) {
		CompilationUnitCache.getInstance().clearCache();
		new ASTReader(project, null);
		SystemObject systemObject = ASTReader.getSystemObject();
		
		Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
		
		for(ClassObject classObj : systemObject.getClassObjects()){
			if(classObj.getIFile().getFullPath().equals(file.getFullPath())) {
				classObjectsToBeExamined.add(classObj);
			}
		}
		
		Set<String> classNamesToBeExamined = new LinkedHashSet<String>();
		for(ClassObject classObject : classObjectsToBeExamined) {
			if(!classObject.isEnum())
				classNamesToBeExamined.add(classObject.getName());
		}
		MySystem system = new MySystem(systemObject, true);
		DistanceMatrix distanceMatrix = new DistanceMatrix(system);
		distanceMatrix.generateDistances(null);
		
		List<ExtractClassCandidateRefactoring> extractClassCandidateList = new ArrayList<ExtractClassCandidateRefactoring>();
		extractClassCandidateList.addAll(distanceMatrix.getExtractClassCandidateRefactorings(classNamesToBeExamined, null));
		
		HashMap<String, ExtractClassCandidateGroup> groupedBySourceClassMap = new HashMap<String, ExtractClassCandidateGroup>();
		for(ExtractClassCandidateRefactoring candidate : extractClassCandidateList) {
			if(groupedBySourceClassMap.keySet().contains(candidate.getSourceEntity())) {
				groupedBySourceClassMap.get(candidate.getSourceEntity()).addCandidate(candidate);
			}
			else {
				ExtractClassCandidateGroup group = new ExtractClassCandidateGroup(candidate.getSourceEntity());
				group.addCandidate(candidate);
				groupedBySourceClassMap.put(candidate.getSourceEntity(), group);
			}
		}
		for(String sourceClass : groupedBySourceClassMap.keySet()) {
			groupedBySourceClassMap.get(sourceClass).groupConcepts();
		}
		
		return new TreeSet<ExtractClassCandidateGroup>(groupedBySourceClassMap.values());
	}



	public static Set<TypeCheckEliminationGroup> getTypeCheckEliminationRefactoringOpportunities(IJavaProject project) {
		CompilationUnitCache.getInstance().clearCache();
		new ASTReader(project, null);
		SystemObject systemObject = ASTReader.getSystemObject();
		
		Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
		classObjectsToBeExamined.addAll(systemObject.getClassObjects());
		
		Set<TypeCheckEliminationGroup> typeCheckEliminationGroups = new TreeSet<TypeCheckEliminationGroup>();
		typeCheckEliminationGroups.addAll(systemObject.generateTypeCheckEliminations(classObjectsToBeExamined, null));
		
		return typeCheckEliminationGroups;
	}


	public static Set<ASTSliceGroup> getExtractMethodRefactoringOpportunitiesForClass(IJavaProject project, IFile file) {
		CompilationUnitCache.getInstance().clearCache();
		new ASTReader(project, null);
		SystemObject systemObject = ASTReader.getSystemObject();
		
		Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
		
		for(ClassObject classObj : systemObject.getClassObjects()){
			if(classObj.getIFile().getFullPath().equals(file.getFullPath())) {
				classObjectsToBeExamined.add(classObj);
			}
		}
				
		Set<ASTSliceGroup> extractedSliceGroups = new TreeSet<ASTSliceGroup>();
		
		for(ClassObject classObject : classObjectsToBeExamined) {
			if(!classObject.isEnum() && !classObject.isInterface()) {
				ListIterator<MethodObject> methodIterator = classObject.getMethodIterator();
				while(methodIterator.hasNext()) {
					MethodObject methodObject = methodIterator.next();
					processMethod(extractedSliceGroups,classObject, methodObject);
				}
			}
		}
		return extractedSliceGroups;
	}
	
	private static void processMethod(Set<ASTSliceGroup> extractedSliceGroups, ClassObject classObject, MethodObject methodObject) {
		if(methodObject.getMethodBody() != null) {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			int minimumMethodSize = store.getInt(PreferenceConstants.P_MINIMUM_METHOD_SIZE);
			StatementExtractor statementExtractor = new StatementExtractor();
			int numberOfStatements = statementExtractor.getTotalNumberOfStatements(methodObject.getMethodBody().getCompositeStatement().getStatement());
			if(numberOfStatements >= minimumMethodSize) {
				ITypeRoot typeRoot = classObject.getITypeRoot();
				CompilationUnitCache.getInstance().lock(typeRoot);
				CFG cfg = new CFG(methodObject);
				PDG pdg = new PDG(cfg, classObject.getIFile(), classObject.getFieldsAccessedInsideMethod(methodObject), null);
				for(VariableDeclaration declaration : pdg.getVariableDeclarationsInMethod()) {
					PlainVariable variable = new PlainVariable(declaration);
					PDGSliceUnionCollection sliceUnionCollection = new PDGSliceUnionCollection(pdg, variable);
					double sumOfExtractedStatementsInGroup = 0.0;
					double sumOfDuplicatedStatementsInGroup = 0.0;
					double sumOfDuplicationRatioInGroup = 0.0;
					int maximumNumberOfExtractedStatementsInGroup = 0;
					int groupSize = sliceUnionCollection.getSliceUnions().size();
					ASTSliceGroup sliceGroup = new ASTSliceGroup();
					for(PDGSliceUnion sliceUnion : sliceUnionCollection.getSliceUnions()) {
						ASTSlice slice = new ASTSlice(sliceUnion);
						int numberOfExtractedStatements = slice.getSliceStatements().size();
						int numberOfRemovableStatements = slice.getRemovableStatements().size();
						int numberOfDuplicatedStatements = numberOfExtractedStatements - numberOfRemovableStatements;
						double duplicationRatio = (double)numberOfDuplicatedStatements/(double)numberOfExtractedStatements;
						sumOfExtractedStatementsInGroup += numberOfExtractedStatements;
						sumOfDuplicatedStatementsInGroup += numberOfDuplicatedStatements;
						sumOfDuplicationRatioInGroup += duplicationRatio;
						if(numberOfExtractedStatements > maximumNumberOfExtractedStatementsInGroup)
							maximumNumberOfExtractedStatementsInGroup = numberOfExtractedStatements;
						sliceGroup.addCandidate(slice);
					}
					if(!sliceGroup.getCandidates().isEmpty()) {
						sliceGroup.setAverageNumberOfExtractedStatementsInGroup(sumOfExtractedStatementsInGroup/(double)groupSize);
						sliceGroup.setAverageNumberOfDuplicatedStatementsInGroup(sumOfDuplicatedStatementsInGroup/(double)groupSize);
						sliceGroup.setAverageDuplicationRatioInGroup(sumOfDuplicationRatioInGroup/(double)groupSize);
						sliceGroup.setMaximumNumberOfExtractedStatementsInGroup(maximumNumberOfExtractedStatementsInGroup);
						extractedSliceGroups.add(sliceGroup);
					}
				}
				for(VariableDeclaration declaration : pdg.getVariableDeclarationsAndAccessedFieldsInMethod()) {
					PlainVariable variable = new PlainVariable(declaration);
					PDGObjectSliceUnionCollection objectSliceUnionCollection = new PDGObjectSliceUnionCollection(pdg, variable);
					double sumOfExtractedStatementsInGroup = 0.0;
					double sumOfDuplicatedStatementsInGroup = 0.0;
					double sumOfDuplicationRatioInGroup = 0.0;
					int maximumNumberOfExtractedStatementsInGroup = 0;
					int groupSize = objectSliceUnionCollection.getSliceUnions().size();
					ASTSliceGroup sliceGroup = new ASTSliceGroup();
					for(PDGObjectSliceUnion objectSliceUnion : objectSliceUnionCollection.getSliceUnions()) {
						ASTSlice slice = new ASTSlice(objectSliceUnion);
						int numberOfExtractedStatements = slice.getSliceStatements().size();
						int numberOfRemovableStatements = slice.getRemovableStatements().size();
						int numberOfDuplicatedStatements = numberOfExtractedStatements - numberOfRemovableStatements;
						double duplicationRatio = (double)numberOfDuplicatedStatements/(double)numberOfExtractedStatements;
						sumOfExtractedStatementsInGroup += numberOfExtractedStatements;
						sumOfDuplicatedStatementsInGroup += numberOfDuplicatedStatements;
						sumOfDuplicationRatioInGroup += duplicationRatio;
						if(numberOfExtractedStatements > maximumNumberOfExtractedStatementsInGroup)
							maximumNumberOfExtractedStatementsInGroup = numberOfExtractedStatements;
						sliceGroup.addCandidate(slice);
					}
					if(!sliceGroup.getCandidates().isEmpty()) {
						sliceGroup.setAverageNumberOfExtractedStatementsInGroup(sumOfExtractedStatementsInGroup/(double)groupSize);
						sliceGroup.setAverageNumberOfDuplicatedStatementsInGroup(sumOfDuplicatedStatementsInGroup/(double)groupSize);
						sliceGroup.setAverageDuplicationRatioInGroup(sumOfDuplicationRatioInGroup/(double)groupSize);
						sliceGroup.setMaximumNumberOfExtractedStatementsInGroup(maximumNumberOfExtractedStatementsInGroup);
						extractedSliceGroups.add(sliceGroup);
					}
				}
				CompilationUnitCache.getInstance().releaseLock();
			}
		}
	}
}
