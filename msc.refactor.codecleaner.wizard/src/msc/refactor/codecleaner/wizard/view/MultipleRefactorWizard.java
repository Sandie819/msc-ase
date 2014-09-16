package msc.refactor.codecleaner.wizard.view;

import msc.refactor.codecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.codecleaner.wizard.controller.WizardController;
import msc.refactor.codecleaner.wizard.view.pages.MultipleRefactoringConfigurationPage;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class MultipleRefactorWizard extends RefactoringWizard {

	private WizardController controller;

	public MultipleRefactorWizard(WizardController controller, MultipleRefactoring refactoring) {
//		super(refactoring, DIALOG_BASED_USER_INTERFACE);
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
		this.controller = controller;
		setDefaultPageTitle("Code Cleaner");
		setWindowTitle("My Code Cleaner");
	}

	@Override
	protected void addUserInputPages() {
		addPage(new MultipleRefactoringConfigurationPage(controller, (MultipleRefactoring) getRefactoring()));
	}


//	public static class MultipleRefactoringConfigurationPage extends UserInputWizardPage {
//
//		private Composite mainComposite;
//		private MultipleRefactoring multipleRefactoring;
//		private WizardController controller;
//
//		public MultipleRefactoringConfigurationPage(WizardController controller, MultipleRefactoring multipleRefactoring) {
//			super("Multiple Refactorings Selection Page");
//			this.multipleRefactoring = multipleRefactoring;
//			this.controller = controller;			
//		}
//
//		@Override
//		public void createControl(Composite parent) {
//
//			mainComposite = new Composite(parent, SWT.NONE);
//			GridLayout gridLayout = new GridLayout();
//			gridLayout.numColumns = 1;
//			mainComposite.setLayout(gridLayout);
//
//			SelectionDialog selector = new SelectionDialog(controller, 
//					multipleRefactoring, new WorkbenchLabelProvider(), 
//					new BaseWorkbenchContentProvider());
//			setPageComplete(false);
//			setControl(mainComposite);		
//			selector.createDialogArea(mainComposite);
//		}
//
//	}
}
