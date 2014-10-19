package msc.refactor.codecleaner.wizard.view;

import msc.refactor.codecleaner.analyser.Analyser;
import msc.refactor.codecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.codecleaner.wizard.controller.WizardController;
import msc.refactor.codecleaner.wizard.view.pages.MainSelectorPage;
import msc.refactor.codecleaner.wizard.view.pages.RefactoringOptionsPage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class MultipleRefactorWizard extends RefactoringWizard  {

	private WizardController controller;
	private MainSelectorPage mainSelectorPage;
	private RefactoringOptionsPage refactoringOptionsPage;
	//private EditorPage editorPage;
	private Analyser analyser;

	public MultipleRefactorWizard(WizardController controller, MultipleRefactoring refactoring) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		this.controller = controller;
		setDefaultPageTitle("Code Cleaner");
		setWindowTitle("My Code Cleaner");
		setNeedsProgressMonitor(true);

		createPages();
	}

	private void createPages(){		
		//editorPage = new EditorPage(controller);
		refactoringOptionsPage = new RefactoringOptionsPage(controller, (MultipleRefactoring)getMultipleRefactoring());
		mainSelectorPage = new MainSelectorPage(controller);
	}

	@Override
	protected void addUserInputPages() {		
		addPage(mainSelectorPage);
		addPage(refactoringOptionsPage);
		//addPage(editorPage);

		WizardDialog container = (WizardDialog)getContainer();
		container.addPageChangingListener(new IPageChangingListener() {
			@Override
			public void handlePageChanging(PageChangingEvent event) {

				if(event.getCurrentPage() instanceof MainSelectorPage) {
					refactoringOptionsPage.findAndSetRefactoringOpportunities();
					analyser = new Analyser();
					analyser.analyseSelection(controller);
				}   
			}
		});
	}


	@Override
	public boolean performFinish() {
		IProgressMonitor monitor = new NullProgressMonitor();
		MultipleRefactoring multipleRefactoring = getMultipleRefactoring();
		try {
			for(Change change:  multipleRefactoring.getChanges()){

				change.perform(monitor);

			}	
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private MultipleRefactoring getMultipleRefactoring() {
		return (MultipleRefactoring) getRefactoring();
	}
}
