package msc.refactor.codecleaner.wizard.view.pages;

import java.util.ArrayList;
import java.util.List;

import msc.refactor.codecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.codecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

//import de.tobject.findbugs.FindBugsJob;
//import de.tobject.findbugs.builder.FindBugsWorker;
//import de.tobject.findbugs.builder.WorkItem;
//import edu.umd.cs.findbugs.plugin.eclipse.util.MutexSchedulingRule;

public class SelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

	private Button randomRefactorButton;
	private Button bugFinderButton;
	private Button analyseMeButton;
	private WizardController controller;	

	/**
	 * Selection Dialog constructor
	 * 
	 * @param controller
	 * @param multipleRefactoring
	 * @param labelProvider
	 * @param contentProvider
	 */
	public SelectionDialog(WizardController controller,
			MultipleRefactoring multipleRefactoring, 
			IBaseLabelProvider labelProvider,
			ITreeContentProvider contentProvider) {

		super(controller.getModel().getWindow().getShell(), labelProvider, contentProvider);
		setInput(ResourcesPlugin.getWorkspace().getRoot());
		this.controller = controller;
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite result= (Composite)super.createDialogArea(parent);

		getTreeViewer().addSelectionChangedListener(this);

		randomRefactorButton = new Button(result, SWT.PUSH);
		randomRefactorButton.setText("Apply random refactoring (Surprise me!)"); 
		randomRefactorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				randomRefactor();
			}
		});
		randomRefactorButton.setFont(parent.getFont());
		randomRefactorButton.setEnabled(false);
		
//		bugFinderButton = new Button(result, SWT.PUSH);
//		bugFinderButton.setText("Find Bugs"); 
//		bugFinderButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent event) {
//				//handleFindBugs();
//			}
//		});
//		bugFinderButton.setFont(parent.getFont());
//		bugFinderButton.setEnabled(true);

		analyseMeButton = new Button(result, SWT.PUSH);
		analyseMeButton.setText("Analyse Me"); 
		analyseMeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleAnalyse();
			}
		});
		analyseMeButton.setFont(parent.getFont());
		analyseMeButton.setEnabled(false);
		
		applyDialogFont(result);

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJavaHelpContextIds.BP_SELECT_DEFAULT_OUTPUT_FOLDER_DIALOG);

		return result;
	}

	protected void handleAnalyse() {
		controller.firePropertyChange("ANALYSE", false, null);
		//randomRefactorButton.setEnabled(false);
		
	}

	//	private void handleFindBugs() {
//		WorkItem workItem = new WorkItem(controller.getModel().getFileFromStructuredSelection());
//		List<WorkItem> workItems = new ArrayList<>();
//		workItems.add(workItem);
//		
//		FindBugsJob runFindBugs = new StartedFromCodeCleaner("Finding bugs in " + 
//				controller.getModel().getFileFromStructuredSelection() + "...", 
//				controller.getModel().getFileFromStructuredSelection(), workItems);
//		IProgressMonitor monitor = new NullProgressMonitor();
//        runFindBugs.run(monitor);
//	}
	/**
	 * Button Random Refactor 
	 */
	private void randomRefactor() {
		controller.firePropertyChange("REFACTORED", false, null);
		randomRefactorButton.setEnabled(false);
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
		randomRefactorButton.setEnabled(enableButton);
		analyseMeButton.setEnabled(enableButton);

	}
	
//    private final static class StartedFromCodeCleaner extends FindBugsJob {
//        private final List<WorkItem> resources;
//
//        private StartedFromCodeCleaner(String name, IResource resource, 
//        		List<WorkItem> resources) {
//            super(name, resource);
//            this.resources = resources;
//        }
//
//        @Override
//        protected boolean supportsMulticore(){
//            return MutexSchedulingRule.MULTICORE;
//        }
//
//        @Override
//        protected void runWithProgress(IProgressMonitor monitor) throws CoreException {
//            FindBugsWorker worker = new FindBugsWorker(getResource(), monitor);
//            worker.work(resources);
//        }
//    }

	
}
