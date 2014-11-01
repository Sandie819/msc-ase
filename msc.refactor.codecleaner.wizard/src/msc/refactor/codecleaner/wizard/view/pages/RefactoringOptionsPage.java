package msc.refactor.codecleaner.wizard.view.pages;

import gr.uom.java.ast.Standalone;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msc.refactor.codecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.codecleaner.multiplerefactoring.RefactoringBuilder;
import msc.refactor.codecleaner.multiplerefactoring.RefactoringEnum;
import msc.refactor.codecleaner.wizard.controller.WizardController;
import msc.refactor.codecleaner.wizard.model.RefactoringOpportunities;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Sandra Mulligan
 *
 */
public class RefactoringOptionsPage extends UserInputWizardPage {

	private List<RefactoringEnum> refactorings;
	private Composite composite;
	private WizardController controller;
	private Map<Button, RefactoringEnum> refactoringCheckboxMap;
	private List<Refactoring> selectedRefactorings;
	private RefactoringBuilder refactoringBuilder;
	private MultipleRefactoring multipleRefactoring;
	
	public RefactoringOptionsPage(WizardController controller, MultipleRefactoring multipleRefactoring) {
		super("Refactoring options");
		this.controller = controller;
		this.multipleRefactoring = multipleRefactoring;
		
		refactoringCheckboxMap = new HashMap<Button, RefactoringEnum>();
		selectedRefactorings = new ArrayList<Refactoring>();
		refactoringBuilder = new RefactoringBuilder();
		
		refactorings = new ArrayList<RefactoringEnum>();
		refactorings.add(RefactoringEnum.EXTRACT_METHOD);
		refactorings.add(RefactoringEnum.EXTRACT_CLASS);
		refactorings.add(RefactoringEnum.REPLACE_METHOD);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);
		
		for(RefactoringEnum refactor: refactorings) {
			Button checkBox = new Button(composite, SWT.CHECK);
			checkBox.setText(refactor.getRefactoringName());
			refactoringCheckboxMap.put(checkBox, refactor);	 
			
			addCheckBoxListener(checkBox);
		}
		
		setControl(composite);
	}

	/**
	 * Adds listener to the given checkBox
	 * @param checkBox
	 */
	private void addCheckBoxListener(Button checkBox) {
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
		    public void widgetSelected(SelectionEvent e) {

				IFile file = controller.getModel().getFileFromStructuredSelection();
				ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(file);
				multipleRefactoring.setfCompilationUnit(iCompilationUnit);
				
		        Button button = (Button) e.widget;
		        if (button.getSelection()) {
		            setPageComplete(true);
		          
		            RefactoringEnum refactor = refactoringCheckboxMap.get(button);
		            if(refactor==RefactoringEnum.EXTRACT_METHOD) {
						ExtractMethodRefactoring extractMethodRefactoring = refactoringBuilder.getExtractedMethodRefactoring(controller);
						multipleRefactoring.addRefactoringsToBeDone(extractMethodRefactoring);
//						selectedRefactorings.add(extractMethodRefactoring);
					} else if(refactor==RefactoringEnum.EXTRACT_CLASS) {
//						selectedRefactorings.add(new ExtractClassRefactoring(sourceFile, sourceCompilationUnit, 
//								sourceTypeDeclaration, extractedFieldFragments, extractedMethods, 
//								delegateMethods, extractedTypeName);
					} else if(refactor==RefactoringEnum.MOVE_METHOD) {
//						selectedRefactorings.add(new MoveMethodRefactoring(sourceCompilationUnit, targetCompilationUnit, 
//								sourceTypeDeclaration, targetTypeDeclaration, sourceMethod, 
//								additionalMethodsToBeMoved, leaveDelegate, movedMethodName);
					}
		            
		            
		        } else {
		        	if(noCheckBoxSelected()) {
		        		setPageComplete(false);
		        		
		        	}
		        }
		    }

			private boolean noCheckBoxSelected() {
				for(Button checkBox: refactoringCheckboxMap.keySet()){
					if(checkBox.getSelection()) {
						return false;
					}
				}
				return true;
			}
		});
	}

	public List<RefactoringEnum> getRefactorings() {
		return refactorings;
	}

	public void setRefactorings(List<RefactoringEnum> refactorings) {
		this.refactorings = refactorings;
	}
	
	public List<Refactoring> getSelectedRefactorings(){
		//List<Refactoring> selectedRefactorings = new ArrayList<>();
		
		
//		for(RefactoringEnum refactor: refactoringCheckboxMap.keySet()){
//			if(refactoringCheckboxMap.get(refactor).getSelection()) {
//				
//				if(refactor==RefactoringEnum.EXTRACT_METHOD) {
//					ExtractMethodRefactoring extractMethodRefactoring = refactoringBuilder.getExtractedMethodRefactoring(controller);
//					selectedRefactorings.add(extractMethodRefactoring);
//				} else if(refactor==RefactoringEnum.EXTRACT_CLASS) {
////					selectedRefactorings.add(new ExtractClassRefactoring(sourceFile, sourceCompilationUnit, 
////							sourceTypeDeclaration, extractedFieldFragments, extractedMethods, 
////							delegateMethods, extractedTypeName);
//				} else if(refactor==RefactoringEnum.MOVE_METHOD) {
////					selectedRefactorings.add(new MoveMethodRefactoring(sourceCompilationUnit, targetCompilationUnit, 
////							sourceTypeDeclaration, targetTypeDeclaration, sourceMethod, 
////							additionalMethodsToBeMoved, leaveDelegate, movedMethodName);
//				}
//				
//			}
//		}
		
		return selectedRefactorings;
	}

	public void findAndSetRefactoringOpportunities() {
		RefactoringOpportunities refactoringOpportunities = new RefactoringOpportunities();
		
		IFile file = controller.getModel().getFileFromStructuredSelection();
		IJavaProject project = JavaCore.createCompilationUnitFrom(file).getJavaProject();
		
		refactoringOpportunities.setExtractClassOpportunities(Standalone.getExtractClassRefactoringOpportunities(project));
		refactoringOpportunities.setExtractMethodOpportunities(Standalone.getExtractMethodRefactoringOpportunities(project));
		refactoringOpportunities.setMoveMethodOpportunities(Standalone.getMoveMethodRefactoringOpportunities(project));		
		
		controller.getModel().setRefactoringOpportunities(refactoringOpportunities);
	}

	
}
