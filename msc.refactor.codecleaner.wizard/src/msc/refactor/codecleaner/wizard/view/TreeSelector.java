package msc.refactor.codecleaner.wizard.view;

import msc.refactor.codecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class TreeSelector extends ElementTreeSelectionDialog implements ISelectionChangedListener {

	private Button randomRefactor;
	private WizardController controller;

	public TreeSelector(WizardController controller, IBaseLabelProvider labelProvider,
			ITreeContentProvider contentProvider) {
		super(controller.getModel().getWindow().getShell(), labelProvider, contentProvider);
		setInput(ResourcesPlugin.getWorkspace().getRoot());
		this.controller = controller;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	public Control createDialogArea(Composite parent) {
		Composite result= (Composite)super.createDialogArea(parent);

		getTreeViewer().addSelectionChangedListener(this);

		randomRefactor = new Button(result, SWT.PUSH);
		randomRefactor.setText("Apply random refactoring (Surprise me!)"); 
		randomRefactor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				randomRefactor();
			}
		});
		randomRefactor.setFont(parent.getFont());
		randomRefactor.setEnabled(false);
		
		applyDialogFont(result);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJavaHelpContextIds.BP_SELECT_DEFAULT_OUTPUT_FOLDER_DIALOG);

		return result;
	}

	protected void randomRefactor() {
		
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		boolean isClassFile = false;
		if(selection!=null) 		{

			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				Object obj = ssel.getFirstElement();
				IFile file = (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);
				if (file != null) {
					if (obj instanceof IAdaptable) {
						file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);
						isClassFile = true;						
						controller.firePropertyChange("CLASS_SELECTED", false, file);
					} 
				}

			}
		}
		randomRefactor.setEnabled(isClassFile);
	}

}
