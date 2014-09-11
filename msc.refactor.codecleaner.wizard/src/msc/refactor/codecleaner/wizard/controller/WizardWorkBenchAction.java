package msc.refactor.codecleaner.wizard.controller;

import msc.refactor.codecleaner.wizard.model.WizardModel;
import msc.refactor.codecleaner.wizard.view.CodeCleanerWizard;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Class associated with the popupMenu for the folder
 * Start the wizard in the run method
 */

public class WizardWorkBenchAction implements IWorkbenchWindowActionDelegate {

	private WizardController controller;
	private WizardModel model;
	
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart part) {
			this.model.setPart(part);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 * Instantiates the wizard and opens it in the wizard container
	 */
	public void run(IAction action) {
		
		// Instantiates and initializes the wizard
		CodeCleanerWizard wizard = new CodeCleanerWizard(controller);
				
		if ((model.getSelection() instanceof IStructuredSelection) || (model.getSelection() == null)) {
			wizard.init(model.getWindow().getWorkbench(), (IStructuredSelection)model.getSelection());
		}
		
		// Instantiates the wizard container with the wizard and opens it
		WizardDialog dialog = new WizardDialog( model.getWindow().getShell(), wizard );
		dialog.create();
		dialog.open();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
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
