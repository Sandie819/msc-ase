package msc.refactor.codecleaner.wizard.view.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;

import msc.refactor.codecleaner.metrics.cohesion.CalculateCohesionMetrics;
import msc.refactor.codecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.codecleaner.multiplerefactoring.RefactorAction;
import msc.refactor.codecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringTickProvider;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class MultipleRefactoringConfigurationPage extends UserInputWizardPage implements PropertyChangeListener {

	private Composite mainComposite;
	private MultipleRefactoring multipleRefactoring;
	private WizardController controller;
	private RefactorAction refactorAction;
	private CalculateCohesionMetrics calculateCohesionMetrics;

	public MultipleRefactoringConfigurationPage(WizardController controller, MultipleRefactoring multipleRefactoring) {
		super("Multiple Refactorings Selection Page");
		this.multipleRefactoring = multipleRefactoring;
		this.controller = controller;		
		refactorAction = new RefactorAction();
		controller.addPropertyChangeListener(this);
	}

	@Override
	public void createControl(Composite parent) {

		mainComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayout(gridLayout);

		SelectionDialog selector = new SelectionDialog(controller, 
				multipleRefactoring, new WorkbenchLabelProvider(), 
				new BaseWorkbenchContentProvider());
		setPageComplete(false);
		setControl(mainComposite);		
		selector.createDialogArea(mainComposite);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("REFACTORED")) {
			handleRandomRefactoringApplied(evt.getOldValue(), evt.getNewValue());
		} else if(evt.getPropertyName().equals("ANALYSE")) {
			handleAnaylseSelection(evt.getOldValue(), evt.getNewValue());
		}
	}

	private void handleAnaylseSelection(Object oldValue, Object newValue) {
		IFile file = controller.getModel().getFileFromStructuredSelection();
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		calculateCohesionMetrics = new CalculateCohesionMetrics();
		try {
			calculateCohesionMetrics.calculate(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//calculateCohesionMetrics.calculate(compilationUnit);
	}

	private void handleRandomRefactoringApplied(Object oldValue, Object newValue) {
		IFile file = controller.getModel().getFileFromStructuredSelection();
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);

		multipleRefactoring = getMultipleRefactoring();
		if(multipleRefactoring.getRefactoringsToBeDone()==null || multipleRefactoring.getRefactoringsToBeDone().isEmpty()) {
			multipleRefactoring.addRefactoringsToBeDone(refactorAction.getRenameMethodRefactoring(compilationUnit, 1));
			multipleRefactoring.addRefactoringsToBeDone(refactorAction.getRenameMethodRefactoring(compilationUnit, 2));
			//multipleRefactoring.addRefactoringsToBeDone(refactorAction.getRenameSelectedClassRefactoring(compilationUnit));
		}
		multipleRefactoring.setfCompilationUnit(compilationUnit);
		IProgressMonitor monitor = new NullProgressMonitor();
		try {
			multipleRefactoring.checkInitialConditions(monitor);
			multipleRefactoring.checkFinalConditions(monitor);
			multipleRefactoring.createChange(monitor);

			RefactoringTickProvider rtp= multipleRefactoring.getRefactoringTickProvider();
			for(Change change:  multipleRefactoring.getChanges()){
				//change.perform(monitor);
				//				change.initializeValidationData(new NotCancelableProgressMonitor(
				//						new SubProgressMonitor(monitor, rtp.getInitializeChangeTicks())));
			}		
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setPageComplete(true);
	}

	private MultipleRefactoring getMultipleRefactoring() {
		return (MultipleRefactoring) getRefactoring();
	}

}