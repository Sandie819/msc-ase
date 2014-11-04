package msc.refactor.jcodecleaner.wizard.view.pages;

import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

/**
 * @author mulligans
 *
 */
public class SelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

	private WizardController controller;	
	private UserInputWizardPage callingPage;
	private boolean setFileSelected;

	/**
	 * Selection Dialog constructor
	 * 
	 * @param controller
	 * @param labelProvider
	 * @param contentProvider
	 * @param callingPage
	 */
	public SelectionDialog(WizardController controller,
			IBaseLabelProvider labelProvider,
			ITreeContentProvider contentProvider, 
			UserInputWizardPage callingPage,
			boolean setFileSelected) {

		super(controller.getModel().getWindow().getShell(), (ILabelProvider) labelProvider, 
				contentProvider);
		setInput(ResourcesPlugin.getWorkspace().getRoot());
		this.controller = controller;
		this.callingPage = callingPage;
		this.setFileSelected = setFileSelected;
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite result= (Composite)super.createDialogArea(parent);

		JavaFileFilter javaFilter = new JavaFileFilter();
		getTreeViewer().addFilter(javaFilter);

		getTreeViewer().addSelectionChangedListener(this);
		
		if(setFileSelected) {
			getTreeViewer().setSelection(controller.getModel().getStructuredSelection(), true);
			getTreeViewer().refresh();
		}
		//applyDialogFont(result);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJavaHelpContextIds.BP_SELECT_DEFAULT_OUTPUT_FOLDER_DIALOG);

		return result;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();

		IStructuredSelection structuredSelection = null;
		boolean enableButton = false;
		if(selection!=null) {
			if (selection instanceof IStructuredSelection) {
				structuredSelection = (IStructuredSelection) selection;
				
				Object element = structuredSelection.getFirstElement();
				IFile file = (IFile) Platform.getAdapterManager().getAdapter(element, IFile.class);
				if (file != null) {
					if (element instanceof IAdaptable) {						
						enableButton = true;
					} 
				}

			}
		}
		controller.getModel().setStructuredSelection(structuredSelection);
		callingPage.setPageComplete(enableButton);
	}
}
