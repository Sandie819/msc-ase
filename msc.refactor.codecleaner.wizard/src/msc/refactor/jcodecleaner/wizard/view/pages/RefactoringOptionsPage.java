package msc.refactor.jcodecleaner.wizard.view.pages;

import gr.uom.java.ast.Standalone;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.jcodecleaner.analyser.RefactoringOpportunities;
import msc.refactor.jcodecleaner.enums.RefactoringEnum;
import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.jcodecleaner.multiplerefactoring.RefactoringBuilder;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author mulligans
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
		
<<<<<<< HEAD:msc.refactor.codecleaner.wizard/src/msc/refactor/jcodecleaner/wizard/view/pages/RefactoringOptionsPage.java
		refactorings = new ArrayList<>();
=======
		refactorings = new ArrayList<RefactoringEnum>();
		refactorings.add(RefactoringEnum.EXTRACT_METHOD);
		refactorings.add(RefactoringEnum.EXTRACT_CLASS);
		refactorings.add(RefactoringEnum.REPLACE_METHOD);
>>>>>>> 19444e6b2cf57653df0037e12ecb78da809f7816:msc.refactor.codecleaner.wizard/src/msc/refactor/codecleaner/wizard/view/pages/RefactoringOptionsPage.java
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		
		 // create a new label that will span two columns
	    Label refactorOppsLabel = new Label(composite, SWT.BORDER);
	    refactorOppsLabel.setText("Available Refactorings: "); 
	    
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		composite.setLayout(gridLayout);
				
		setControl(composite);
	}
	
	public void onEnterPage(){
		for(RefactoringEnum refactor: refactorings) {
			Button checkBox = new Button(composite, SWT.CHECK);
			checkBox.setText(refactor.getRefactoringName());
			refactoringCheckboxMap.put(checkBox, refactor);	 
			
			addCheckBoxListener(checkBox);
		}
		composite.pack();
		//setControl(composite);
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
		            
		            OrganizeImportsAction organiseImports = new OrganizeImportsAction(controller.getModel().getPart().getSite());
		            //organiseImports.
		            organiseImports.run(controller.getModel().getSelection());
		            
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
		//List<Refactoring> selectedRefactorings = new ArrayList<Refactoring>();
		
		
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

	public void findAndSetRefactoringOpportunities(List<RefactoringEnum> refactoringsForSuggestion) {
		RefactoringOpportunities refactoringOpportunities = new RefactoringOpportunities();
		
		IFile file = controller.getModel().getFileFromStructuredSelection();
		IJavaProject project = JavaCore.createCompilationUnitFrom(file).getJavaProject();
		
		for(RefactoringEnum suggestedRefactoring: refactoringsForSuggestion){
			
			if(suggestedRefactoring==RefactoringEnum.EXTRACT_CLASS) {
				Set<ExtractClassCandidateGroup> extractedClassCandidates = Standalone.getExtractClassRefactoringOpportunities(project);
				
				for(ExtractClassCandidateGroup candidateGroup: extractedClassCandidates){
					for(ExtractClassCandidateRefactoring candidate : candidateGroup.getCandidates()) {
						if(candidate.getSourceIFile().getFullPath().equals(file.getFullPath())){
							refactoringOpportunities.setExtractClassOpportunities(extractedClassCandidates);							
							refactorings.add(RefactoringEnum.EXTRACT_CLASS);							
						}
					}
				}		
			}
			
			if(suggestedRefactoring==RefactoringEnum.EXTRACT_METHOD) {
				Set<ASTSliceGroup> extractMethodOpportunities = Standalone.getExtractMethodRefactoringOpportunities(project);
				
				for(ASTSliceGroup sliceGroup: extractMethodOpportunities){
					for(ASTSlice slice : sliceGroup.getCandidates()) {
						if(slice.getIFile().getFullPath().equals(file.getFullPath())){
							refactoringOpportunities.setExtractMethodOpportunities(extractMethodOpportunities);
							refactorings.add(RefactoringEnum.EXTRACT_METHOD);
						}
					}
				}		
			}
			
			if(suggestedRefactoring==RefactoringEnum.MOVE_METHOD) {
				List<MoveMethodCandidateRefactoring> moveMethodOpportunities = Standalone.getMoveMethodRefactoringOpportunities(project);
				
				for(MoveMethodCandidateRefactoring candidate: moveMethodOpportunities){
						if(candidate.getSourceIFile().getFullPath().equals(file.getFullPath())){
							refactoringOpportunities.setMoveMethodOpportunities(moveMethodOpportunities);
							refactorings.add(RefactoringEnum.MOVE_METHOD);
						}
				}		
			}
			
		}
			
		controller.getModel().setRefactoringOpportunities(refactoringOpportunities);
	}

	
}
