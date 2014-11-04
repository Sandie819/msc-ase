package msc.refactor.jcodecleaner.wizard.controller;

import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.jcodecleaner.wizard.model.WizardModel;
import msc.refactor.jcodecleaner.wizard.view.MultipleRefactorWizard;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class WizardAction implements IWorkbenchWindowActionDelegate {

	private WizardController controller;
	private WizardModel model;
	private ICompilationUnit fCompilationUnit;

	@Override
	public void run(IAction action) {
		startWizard(new MultipleRefactorWizard(controller, createMultipleRefactoring(), false));
	}

	private MultipleRefactoring createMultipleRefactoring() {
		MultipleRefactoring multipleRefactoring = new MultipleRefactoring();
		multipleRefactoring.setfCompilationUnit(fCompilationUnit);
		return multipleRefactoring;
	}

	public void startWizard(RefactoringWizard wizard) {
		try {			
			Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(
					wizard);
			operation.run(shell, "Multiple Refactorings");
		} catch (InterruptedException exception) {
			// Do nothing
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.model.setSelection(selection);

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		model = new WizardModel();
		model.setWindow(window);
		model.setPart(window.getActivePage().getActivePart());
		controller = new WizardController(model);
	}

}
