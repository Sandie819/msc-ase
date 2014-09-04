package msc.refactor.codecleaner.wizard.view;

import msc.refactor.codecleaner.wizard.controller.WizardController;
import msc.refactor.codecleaner.wizard.model.WizardModel;
import msc.refactor.codecleaner.wizard.view.pages.MainPanelPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Wizard class
 */
public class CodeCleanerWizard extends Wizard implements INewWizard
{
	public static final String copyright = "(c) Copyright IBM Corporation 2002.";	
	// wizard pages
	//HolidayMainPage holidayPage;
	MainPanelPage mainPanelPage;

	/**
	 * TODO:
	 * FindBugs Page
	 * Refactoring Suggestion/Selection Page
	 * Metric enhancements
	 */
	
	// the model
	private WizardModel model;
	
//	// workbench selection when the wizard was started
//	protected IStructuredSelection selection;
	
	// flag indicated whether the wizard can be completed or not 
	// if the user has selected plane as type of transport
	protected boolean planeCompleted = false;

	// flag indicated whether the wizard can be completed or not 
	// if the user has selected car as type of transport
	protected boolean carCompleted = false;
//	
//	// the workbench instance
//	protected IWorkbench workbench;
	
	private WizardController controller;

	/**
	 * Constructor for HolidayMainWizard.
	 * @param controller 
	 */
	public CodeCleanerWizard(WizardController controller) {
		super();
		this.controller = controller;
		model = new WizardModel();
	}
	
	public void addPages() {
		mainPanelPage = new MainPanelPage(controller);
		addPage(mainPanelPage);	
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		controller.getModel().setSelection(selection);
		if (selection != null && !selection.isEmpty()) {
			Object obj = selection.getFirstElement();
			if (obj  instanceof IFile) {
				IFile file = (IFile) obj;				
			}
		}
	}

	public boolean canFinish() {
		return true;
	}
	
	public boolean performFinish() 
	{
		String summary = model.toString();
		MessageDialog.openInformation(controller.getModel().getWindow().getShell(), 
			"Holiday info", summary);
		return true;
	}
}
