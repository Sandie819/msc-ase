package msc.refactor.jcodecleaner.wizard.view;

import java.util.ArrayList;
import java.util.List;

import msc.refactor.jcodecleaner.analyser.Analyser;
import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;
import msc.refactor.jcodecleaner.wizard.model.RefactoringOpportunitiesModel;
import msc.refactor.jcodecleaner.wizard.model.WizardModel;
import msc.refactor.jcodecleaner.wizard.view.pages.MainSelectorPage;
import msc.refactor.jcodecleaner.wizard.view.pages.RefactoringOptionsPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

public class MultipleRefactorWizard extends RefactoringWizard  {

	private WizardController controller;
	private MainSelectorPage mainSelectorPage;
	private RefactoringOptionsPage refactoringOptionsPage;
	private Analyser analyser;
	private boolean setFileSelected;

	public MultipleRefactorWizard(WizardController controller, 
			MultipleRefactoring refactoring, boolean setFileSelected) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);

		PlatformUI.getWorkbench().saveAll(
				PlatformUI.getWorkbench().getModalDialogShellProvider(), 
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
				null, true);

		this.controller = controller;
		this.setFileSelected = setFileSelected;
		setDefaultPageTitle("JCodeCleaner");		
		setTitleBarColor(new RGB(128,0,128));
		setWindowTitle("JCodeCleaning Wizard");
		
		setNeedsProgressMonitor(true);

		createPages();
	}

	private void createPages(){		
		refactoringOptionsPage = new RefactoringOptionsPage(controller, (MultipleRefactoring)getMultipleRefactoring());
		mainSelectorPage = new MainSelectorPage(controller, setFileSelected);
	}

	@Override
	protected void addUserInputPages() {

		addPage(mainSelectorPage);	
		addPage(refactoringOptionsPage);

		WizardDialog container = (WizardDialog)getContainer();
		container.addPageChangingListener(new IPageChangingListener() {
			@Override
			public void handlePageChanging(PageChangingEvent event) {

				if(event.getTargetPage() instanceof RefactoringOptionsPage) {
					//Reset Model, compilation unit etc
					setUpRefactoringOptionsPage();

				}
			}
		});
	}

	/**
	 * Sets up the configuration for the refactoring options page
	 */
	protected void setUpRefactoringOptionsPage() {
		WizardModel model = controller.getModel();
		IFile file = model.getFileFromStructuredSelection();

		analyser = new Analyser();
		List<Metric> metrics = analyser.analyseSelection(file);
		RefactoringOpportunitiesModel refactoringOpportunities = analyser.identifyRefactoringOpportunities(file);
		double fitnessFunctionValue = analyser.calculateFitnessFunction();
		model.setRefactoringOpportunities(refactoringOpportunities);

		refactoringOptionsPage.onEnterPage(metrics, refactoringOpportunities.getAvailableRefactorings(), 
				fitnessFunctionValue);
	}

	@Override
	public boolean performFinish() {
		IProgressMonitor monitor = new NullProgressMonitor();
		MultipleRefactoring multipleRefactoring = getMultipleRefactoring();
		try {

			if(multipleRefactoring.getChanges()==null || multipleRefactoring.getChanges().isEmpty()) {
				return true;
			} else {				
				
				for(Change change:  multipleRefactoring.getChanges()){
					change.perform(monitor);
				}				
				try {
					((WizardDialog)getContainer()).close();

					multipleRefactoring.setChanges(new ArrayList<Change>()); 
					RefactoringWizardOpenOperation operation = new 
							RefactoringWizardOpenOperation(
									new MultipleRefactorWizard(controller, 
											multipleRefactoring, true));
					
					operation.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"Multiple Refactorings");

				} catch (InterruptedException exception) {
					// Do nothing
				}

			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private MultipleRefactoring getMultipleRefactoring() {
		return (MultipleRefactoring) getRefactoring();
	}
}
