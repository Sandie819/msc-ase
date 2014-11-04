package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.ArrayList;
import java.util.List;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class DepthOfInheritanceTree extends Metric {
	
	public DepthOfInheritanceTree() {
		super("Depth of Inheritance Tree", "DIT", 3);
	}

	public double calculateMetricValue(IFile file) {
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);

		double depthOfInheritance = 0.0;

		try{
			IType types[] = compilationUnit.getTypes();
			for (IType type : types) {
				if(type.getResource().getFullPath().equals(file.getFullPath())) {

					if(type.isInterface()){
						continue;
					} else {
						ITypeHierarchy typeHierarchy = type.newTypeHierarchy(null);
						depthOfInheritance = typeHierarchy.getAllSuperclasses(type).length;
					}	
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setMetricValue(depthOfInheritance);
		
		return depthOfInheritance;
	}

	@Override
	public List<RefactoringEnum> getApplicableMetricRefactorings() {
		List<RefactoringEnum> applicableRefactorings = new ArrayList<RefactoringEnum>();		
		applicableRefactorings.add(RefactoringEnum.EXTRACT_CLASS);
		applicableRefactorings.add(RefactoringEnum.MOVE_METHOD);
		
		return applicableRefactorings;
	}
}
