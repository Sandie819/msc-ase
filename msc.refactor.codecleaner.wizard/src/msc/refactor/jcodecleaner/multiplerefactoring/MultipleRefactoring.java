package msc.refactor.jcodecleaner.multiplerefactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * Multiple-Refactoring facilitates the combination of several Refactorings
 * 
 * @author Sandra Mulligan
 */
public class MultipleRefactoring extends Refactoring {

	private List<Refactoring> refactoringsToBeDone = null;
	private List<Change> changes = null;
	private ICompilationUnit fCompilationUnit;
	private CompilationUnit fJavaAST;

	public MultipleRefactoring() {		
		super();		
		refactoringsToBeDone = new ArrayList<Refactoring>();
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {		
		RefactoringStatus status = new RefactoringStatus();
		status.isOK();
		if(fCompilationUnit!=null){
			fJavaAST = parse(pm, fCompilationUnit);;
			for(Refactoring refactoring: refactoringsToBeDone){
				if(status==null){
					status = refactoring.checkInitialConditions(pm);
				}else{
					status.merge(refactoring.checkInitialConditions(pm));
				}
			}
		}
		return status;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = null;
		
		for(Refactoring refactoring: refactoringsToBeDone){
			
			if(status==null){
				status = refactoring.checkFinalConditions(pm);
			} else{
				status.merge(refactoring.checkFinalConditions(pm));
			}			
		}
		return status;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
	OperationCanceledException {
		Change result = new CompilationUnitChange(
				"Multiple Refactorings", fCompilationUnit);
	
		changes = new ArrayList<Change>();
		for(Refactoring refactoring: refactoringsToBeDone){			
			result = refactoring.createChange(pm);
			changes.add(result);

		}
		return result;
	}

	@SuppressWarnings("deprecation")
	protected CompilationUnit parse(IProgressMonitor monitor, ICompilationUnit compilationUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit); 
		parser.setResolveBindings(true); 
		parser.setBindingsRecovery(true);
		return (CompilationUnit) parser.createAST(monitor);
	}

	public List<Refactoring> getRefactoringsToBeDone() {
		return refactoringsToBeDone;
	}

	public void setRefactoringsToBeDone(List<Refactoring> refactoringsToBeDone) {
		this.refactoringsToBeDone = refactoringsToBeDone;
	}

	public void addRefactoringsToBeDone(Refactoring refactoring) {
		if(refactoringsToBeDone==null) {
			refactoringsToBeDone = new ArrayList<Refactoring>();
		}

		refactoringsToBeDone.add(refactoring);
	}
	
	/** 
	 * @param refactor
	 */
	public void removeRefactoring(RefactoringEnum refactor) {
		
		for (Iterator<Refactoring> it = refactoringsToBeDone.iterator(); it.hasNext(); ) {
			Refactoring refactoring = it.next();
		    if (refactoring.getClass().isAssignableFrom(refactor.getRefactoringType())) {
		    	it.remove();
		    }
		}
	}
	
	/** 
	 * @param refactor
	 */
	public void removeRefactoring() {
		
		for (Iterator<Refactoring> it = refactoringsToBeDone.iterator(); it.hasNext(); ) {
			it.next();
		    it.remove();
		}
	}


	public List<Change> getChanges() {
		return changes;
	}

	public void setChanges(List<Change> changes) {
		this.changes = changes;
	}

	public ICompilationUnit getfCompilationUnit() {
		return fCompilationUnit;
	}

	public void setfCompilationUnit(ICompilationUnit fCompilationUnit) {		
		this.fCompilationUnit = fCompilationUnit;
	}

	public CompilationUnit getfJavaAST() {
		return fJavaAST;
	}

	public void setfJavaAST(CompilationUnit fJavaAST) {
		this.fJavaAST = fJavaAST;
	}

	@SuppressWarnings("rawtypes")
	public RefactoringStatus initialize(Map fArguments) {		
		RefactoringStatus status= new RefactoringStatus();		
		return status;
	}
	
	@Override
	public String getName() {

		StringBuffer stringBuffer = new StringBuffer();
		for(Refactoring refactoring : refactoringsToBeDone) {
			stringBuffer.append(refactoring.getName()).append("+");
		}
		String name = stringBuffer.toString();

		if(!name.isEmpty()) {
			return name.substring(0, name.lastIndexOf("+") );	
		} else {
			return "Multiple Refactoring";
		}

	}

	
}
