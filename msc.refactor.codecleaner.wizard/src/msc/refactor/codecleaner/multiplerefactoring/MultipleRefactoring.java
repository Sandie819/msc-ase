package msc.refactor.codecleaner.multiplerefactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

		//ASTRewrite astRewrite = ASTRewrite.create(fJavaAST.getAST());
		Change result = new CompilationUnitChange(
				"Multiple Refactorings", fCompilationUnit);
		List<Change> changes = new ArrayList<Change>();
		for(Refactoring refactoring: refactoringsToBeDone){
			
			result = refactoring.createChange(pm);
			changes.add(result);

		}
//		Change combinedChange = null;
//		TextChangeCombiner changeCombiner = new TextChangeCombiner();
//		for(Change change: changes) {
//			combinedChange = changeCombiner.combineChanges(change);
//		}

		setChanges(changes);
		//		MultiTextEdit root = new MultiTextEdit();
		//	    result.setEdit(root);
		//	    root.addChild(astRewrite.rewriteAST());
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
