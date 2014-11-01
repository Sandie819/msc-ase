package msc.refactor.jcodecleaner.wizard.view.pages;

import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Sandra Mulligan
 *
 */
public class MainSelectorPage extends UserInputWizardPage {

	private Composite mainComposite;
	private WizardController controller;

	public MainSelectorPage(WizardController controller) {
		super("Rigorous refactor selector");
		this.controller = controller;		
	}

	@Override
	public void createControl(Composite parent) {

		mainComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayout(gridLayout);

		SelectionDialog selector = new SelectionDialog(controller, 
				new WorkbenchLabelProvider(), 
				new BaseWorkbenchContentProvider(), this);
		
		setPageComplete(false);		
		selector.createDialogArea(mainComposite);
		setControl(mainComposite);	
	}	
}