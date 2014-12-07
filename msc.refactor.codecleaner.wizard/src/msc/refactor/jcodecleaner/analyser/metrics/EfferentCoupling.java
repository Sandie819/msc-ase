package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * Efferent Coupling (Ce) (Coupling between Objects)
 * The number of code elements that it uses
 * Metric indicates how sensitive this package is for changes to other packages.
 * 
 * @author mulligans
 *
 */
public class EfferentCoupling extends Metric {

	public EfferentCoupling() {
		super("Efferent Coupling", "Ce", 5);
	}

	@Override
	public double calculateMetricValue(IFile file, IProgressMonitor monitor) {
		ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(file);	
		CompilationUnit compilationUnit = parse(monitor, iCompilationUnit);
		
		Set<IType> results = findDependencies(compilationUnit);
		
		int dependencies = 0;
		for(IType type: results) {
			
			if(type.getParent().getJavaProject().getElementName().equals(
					iCompilationUnit.getJavaProject().getElementName())) {
				dependencies++;
			}
		}
		
		setMetricValue(dependencies);
		return dependencies;
	}
	
	public static Set<IType> findDependencies(final ASTNode astClassNode) {
		
	    final Set<IType> result = new HashSet<IType>();
	    astClassNode.accept(new ASTVisitor() {
	    	
	        @Override
	        public boolean visit(SimpleName node) {
	            ITypeBinding typeBinding = node.resolveTypeBinding();
	            if (typeBinding == null)
	                return false;
	            IJavaElement element = typeBinding.getJavaElement();
	            if (element != null && element instanceof IType){
	            	//System.out.println("Dependency: "+ element.getElementName());
	                result.add((IType)element);
	            }
	            return false;
	        }
	    });
	    return result;
	}

}
