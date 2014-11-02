package msc.refactor.jcodecleaner.wizard.view.pages;

import gr.uom.java.ast.Standalone;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;
import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.jcodecleaner.multiplerefactoring.RefactoringBuilder;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;
import msc.refactor.jcodecleaner.wizard.model.RefactoringOpportunitiesModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BoxLayout;
import swing2swt.layout.FlowLayout;

/**
 * @author mulligans
 *
 */
public class RefactoringOptionsPage extends UserInputWizardPage {

	private Set<RefactoringEnum> refactorings;
	private Composite composite;
	private WizardController controller;
	private Map<Button, RefactoringEnum> refactoringCheckboxMap;
	private RefactoringBuilder refactoringBuilder;
	private MultipleRefactoring multipleRefactoring;
	
	private Text lcomText;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text;
	private Group refactoringOptionsGroup;
	private Label lblNewLabel;
	
	public RefactoringOptionsPage(WizardController controller, MultipleRefactoring multipleRefactoring) {
		super("Refactoring options");
		this.controller = controller;
		this.multipleRefactoring = multipleRefactoring;
		
		refactoringCheckboxMap = new HashMap<Button, RefactoringEnum>();
		refactoringBuilder = new RefactoringBuilder();
		refactorings = new HashSet<RefactoringEnum>();

	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout(1, true));		
		
		refactoringOptionsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		refactoringOptionsGroup.setText("Refactoring Options Available");
		refactoringOptionsGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		refactoringOptionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		new Label(refactoringOptionsGroup, SWT.NONE);	

		Group metricResultsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		metricResultsGroup.setText("Metric Results");			
		metricResultsGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		metricResultsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		new Label(metricResultsGroup, SWT.NONE);
				
		Label lblLcom = new Label(metricResultsGroup, SWT.NONE);	
		lblLcom.setText("LCOM:");		
		lcomText = new Text(metricResultsGroup, SWT.BORDER);
	
		Label lblM = new Label(metricResultsGroup, SWT.NONE);
		lblM.setText("M2");		
		text_2 = new Text(metricResultsGroup, SWT.BORDER);

		
		Label lblM_1 = new Label(metricResultsGroup, SWT.NONE);
		lblM_1.setText("M3");		
		text_3 = new Text(metricResultsGroup, SWT.BORDER);
		
		Label lblM_2 = new Label(metricResultsGroup, SWT.NONE);
		lblM_2.setText("M4");		
		text_4 = new Text(metricResultsGroup, SWT.BORDER);
		
		Group fitnessFunctionGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		fitnessFunctionGroup.setText("Overall Fitness Function");
		fitnessFunctionGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		fitnessFunctionGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lblNewLabel = new Label(fitnessFunctionGroup, SWT.NONE);
		lblNewLabel.setText("Calculated Fitness functions");		
		text = new Text(fitnessFunctionGroup, SWT.BORDER);
		Label lblHint = new Label(fitnessFunctionGroup, SWT.NONE);
		lblHint.setText("(Based on various metrics)");		
				
		setControl(composite);
	}
	
	public void onEnterPage(){
		for(RefactoringEnum refactor: refactorings) {
			Button checkBox = new Button(refactoringOptionsGroup, SWT.CHECK);
			checkBox.setText(refactor.getRefactoringName());			
			refactoringCheckboxMap.put(checkBox, refactor);	 
			
			addCheckBoxListener(checkBox);
		}
		composite.pack(false);
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
//						
					} else if(refactor==RefactoringEnum.EXTRACT_CLASS) {
						multipleRefactoring.addRefactoringsToBeDone(refactoringBuilder.getExtractedClassRefactoring(controller).iterator().next());
//						for(ExtractClassRefactoring newRefactoring : refactoringBuilder.getExtractedClassRefactoring(controller)){
//							multipleRefactoring.addRefactoringsToBeDone(newRefactoring);	
//						}												
						
					} else if(refactor==RefactoringEnum.MOVE_METHOD) {
//						selectedRefactorings.add(new MoveMethodRefactoring(sourceCompilationUnit, targetCompilationUnit, 
//								sourceTypeDeclaration, targetTypeDeclaration, sourceMethod, 
//								additionalMethodsToBeMoved, leaveDelegate, movedMethodName);
					}
		            
//		            OrganizeImportsAction organiseImports = new OrganizeImportsAction(controller.getModel().getPart().getSite());
//		            //organiseImports.
//		            organiseImports.run(controller.getModel().getSelection());
		            
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

	public Set<RefactoringEnum> getRefactorings() {
		return refactorings;
	}

	public void setRefactorings(Set<RefactoringEnum> refactorings) {
		this.refactorings = refactorings;
	}

	public void findAndSetRefactoringOpportunities(List<RefactoringEnum> refactoringsForSuggestion) {
		RefactoringOpportunitiesModel refactoringOpportunities = new RefactoringOpportunitiesModel();
		
		IFile file = controller.getModel().getFileFromStructuredSelection();
		IJavaProject project = JavaCore.createCompilationUnitFrom(file).getJavaProject();
		
		for(RefactoringEnum suggestedRefactoring: refactoringsForSuggestion){
			
			if(suggestedRefactoring==RefactoringEnum.EXTRACT_CLASS) {
				Set<ExtractClassCandidateGroup> extractedClassCandidates = Standalone.getExtractClassRefactoringOpportunitiesForClass(project, file);
				
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
				Set<ASTSliceGroup> extractMethodOpportunities = Standalone.getExtractMethodRefactoringOpportunitiesForClass(project, file);
				
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
