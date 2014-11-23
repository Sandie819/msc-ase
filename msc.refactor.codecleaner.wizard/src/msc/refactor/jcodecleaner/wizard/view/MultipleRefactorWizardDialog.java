package msc.refactor.jcodecleaner.wizard.view;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MultipleRefactorWizardDialog extends WizardDialog {

	public MultipleRefactorWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);	
		
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	    super.createButtonsForButtonBar(parent);

	    Button button = getButton(IDialogConstants.FINISH_ID);
	    button.setText("Apply Refactoring");
	    GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = 112;
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}
	
	public void renameFinishButton(String name) {
		Button finishButton = getButton(IDialogConstants.FINISH_ID);		
		if(finishButton!=null) {
			finishButton.setText(name);
		}
	}
}



