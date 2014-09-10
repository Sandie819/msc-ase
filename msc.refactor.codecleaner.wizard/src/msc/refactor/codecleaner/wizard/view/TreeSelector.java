package msc.refactor.codecleaner.wizard.view;

import msc.refactor.codecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class TreeSelector extends ElementTreeSelectionDialog implements ISelectionChangedListener {

	private Button randomRefactorButton;
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

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJavaHelpContextIds.BP_SELECT_DEFAULT_OUTPUT_FOLDER_DIALOG);

		return result;
	}

	protected void randomRefactor() {
		IFile file = controller.getModel().getCurrentSelectedFile();

		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);

		renameSelectedClass(compilationUnit);
		renameFirstMethod(compilationUnit);

	}

	private void renameFirstMethod(ICompilationUnit compilationUnit) {
		IMethod firstMethod = null;
		RefactoringContribution contribution =
				RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_METHOD);		

		RefactoringStatus status = new RefactoringStatus();
		
		try {
			IType[] types = compilationUnit.getTypes();
			for (int i = 0; i < types.length; i++) {
				IType type = types[i];
				IMethod[] methods = type.getMethods();
				firstMethod = methods[0];
				System.out.println(firstMethod.getElementName());
					
				RenameJavaElementDescriptor descriptor =
						(RenameJavaElementDescriptor) contribution.createDescriptor();
				descriptor.setProject(compilationUnit.getResource().getProject().getName());
				descriptor.setNewName(firstMethod.getElementName()+"RandomRename"); // new name for a Class
				descriptor.setJavaElement(firstMethod);
				
				Refactoring refactoring = descriptor.createRefactoring(status);

				IProgressMonitor monitor = new NullProgressMonitor();
				refactoring.checkInitialConditions(monitor);
				refactoring.checkFinalConditions(monitor);
				Change change = refactoring.createChange(monitor);
				change.perform(monitor);
			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void renameSelectedClass(ICompilationUnit compilationUnit) {

		RefactoringContribution contribution =
				RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_COMPILATION_UNIT);
		RenameJavaElementDescriptor descriptor =
				(RenameJavaElementDescriptor) contribution.createDescriptor();
		descriptor.setProject(compilationUnit.getResource().getProject().getName());
		descriptor.setNewName("RandomRenamed"); // new name for a Class
		descriptor.setJavaElement(compilationUnit);

		RefactoringStatus status = new RefactoringStatus();
		try {
			Refactoring refactoring = descriptor.createRefactoring(status);

			IProgressMonitor monitor = new NullProgressMonitor();
			refactoring.checkInitialConditions(monitor);
			refactoring.checkFinalConditions(monitor);
			Change change = refactoring.createChange(monitor);
			change.perform(monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		boolean isClassFile = false;
		IFile file = null;
		if(selection!=null) 		{

			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				Object obj = ssel.getFirstElement();
				file = (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);
				if (file != null) {
					if (obj instanceof IAdaptable) {
						file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);
						isClassFile = true;						
						controller.firePropertyChange("CLASS_SELECTED", false, file);
					} 
				}

			}
		}
		controller.getModel().setCurrentSelectedFile(file);
		randomRefactorButton.setEnabled(isClassFile);
	}

}
