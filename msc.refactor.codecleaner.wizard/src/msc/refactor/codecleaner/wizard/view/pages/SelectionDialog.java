package msc.refactor.codecleaner.wizard.view.pages;

import msc.refactor.codecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.codecleaner.multiplerefactoring.RefactorAction;
import msc.refactor.codecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringTickProvider;
import org.eclipse.ltk.internal.core.refactoring.NotCancelableProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class SelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

	private Button randomRefactorButton;
	private RefactorAction refactorAction;
	private MultipleRefactoring multipleRefactoring;
	private WizardController controller;	
	private IProgressMonitor monitor;

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
		refactorAction = new RefactorAction();
		monitor = new NullProgressMonitor();
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

		applyDialogFont(result);

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJavaHelpContextIds.BP_SELECT_DEFAULT_OUTPUT_FOLDER_DIALOG);

		return result;
	}

	/**
	 * Button Random Refactor (Apply several random re-factorings)
	 */
	protected void randomRefactor() {

//		IFile file = controller.getModel().getFileFromStructuredSelection();
//		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
//
//		multipleRefactoring = new MultipleRefactoring();
//		//multipleRefactoring.addRefactoringsToBeDone(refactorAction.getRenameFirstMethodRefactoring(compilationUnit));
//		multipleRefactoring.addRefactoringsToBeDone(refactorAction.getRenameSelectedClassRefactoring(compilationUnit));
//		multipleRefactoring.setfCompilationUnit(compilationUnit);
//		
//		try {
//			multipleRefactoring.checkInitialConditions(monitor);
//			multipleRefactoring.checkFinalConditions(monitor);
//			multipleRefactoring.createChange(monitor);
//		
//			RefactoringTickProvider rtp= multipleRefactoring.getRefactoringTickProvider();
//			for(Change change:  multipleRefactoring.getChanges()){
//				//change.perform(monitor);
//				change.initializeValidationData(new NotCancelableProgressMonitor(
//						new SubProgressMonitor(monitor, rtp.getInitializeChangeTicks())));
//			}		
			
			controller.firePropertyChange("REFACTORED", false, null);
			randomRefactorButton.setEnabled(false);
//			compilationUnit.save(monitor, false);	

//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
			
//			OrganizeImportsAction organiseImportsAction = new OrganizeImportsAction(controller.getModel().getPart().getSite());
//			organiseImportsAction.run(compilationUnit);
//			try {
//				IProgressMonitor monitor = new NullProgressMonitor();		
//				compilationUnit.save(monitor, false);
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();

		if(selection!=null) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				controller.getModel().setStructuredSelection(structuredSelection);
				randomRefactorButton.setEnabled(true);
			}
		}
		
	}

}
