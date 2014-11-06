package msc.refactor.jcodecleaner.wizard.view.pages;

import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.enums.RefactoringEnum;
import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.jcodecleaner.multiplerefactoring.RefactoringBuilder;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * @author mulligans
 */
public class RefactoringOptionsPage extends UserInputWizardPage {

	private Composite composite;
	private WizardController controller;
	private Map<Button, RefactoringEnum> refactoringCheckboxMap;
	private RefactoringBuilder refactoringBuilder;
	private MultipleRefactoring multipleRefactoring;
	
	private Group refactoringOptionsGroup;
	private Group metricResultsGroup;
	private Group fitnessFunctionGroup;
	
	public RefactoringOptionsPage(WizardController controller, MultipleRefactoring multipleRefactoring) {
		super("Refactoring options");
		setMessage("Metric results, please select refactoring from given choices");
		
		this.controller = controller;
		this.multipleRefactoring = multipleRefactoring;
		
		refactoringCheckboxMap = new HashMap<Button, RefactoringEnum>();
		refactoringBuilder = new RefactoringBuilder();
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);		
		composite.setLayout(new GridLayout(1, true));		
		
		fitnessFunctionGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		fitnessFunctionGroup.setText("Overall Fitness Function");
//		fitnessFunctionGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		fitnessFunctionGroup.setLayout(new GridLayout(2, true));
		fitnessFunctionGroup.setLayoutData(new GridData(GridData.FILL_BOTH));	
		
		metricResultsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		metricResultsGroup.setText("Metric Results");			
		//metricResultsGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		metricResultsGroup.setLayout(new GridLayout(4, true));
		metricResultsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		refactoringOptionsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		refactoringOptionsGroup.setText("Refactoring Options Available");
		//refactoringOptionsGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		refactoringOptionsGroup.setLayout(new GridLayout(4, true));
		refactoringOptionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		setControl(composite);
	}
	
	/**
	 * On Entering the Re-factoring options page sets metrics, refactorings
	 * and fitness function
	 * 
	 * @param metrics
	 * @param refactorings
	 * @param fitnessFunctionValue
	 */
	public void onEnterPage(List<Metric> metrics, Set<RefactoringEnum> refactorings,
			double fitnessFunctionValue){
		
		createFitnessFunctionPanel(fitnessFunctionValue);		
		createMetricResultPanel(metrics);
		createRefactoringOptions(refactorings);

		composite.layout();
	}

	/**
	 * @param fitnessFunctionValue
	 */
	private void createFitnessFunctionPanel(double fitnessFunctionValue) {
		clearGroup(fitnessFunctionGroup);
		
		Label fitnessLabel = new Label(fitnessFunctionGroup, SWT.NONE);
		fitnessLabel.setText("Calculated Fitness functions");		
		Text text = new Text(fitnessFunctionGroup, SWT.BORDER);
		text.setText(String.valueOf(fitnessFunctionValue));
		text.setEditable(false);
		
		Label lblHint = new Label(fitnessFunctionGroup, SWT.NONE);
		lblHint.setText("(Based on various metrics outlined below)");		
	}

	/**
	 * Adds each metric to the group for holding metric results
	 * 
	 * @param metrics
	 */
	private void createMetricResultPanel(List<Metric> metrics) {
		clearGroup(metricResultsGroup);
		
		for(Metric metric: metrics) {
			Label metricLabel = new Label(metricResultsGroup, SWT.NONE);
			metricLabel.setText(metric.getMetricShortName());
			metricLabel.setToolTipText(metric.getMetricFullName());
			
			Text metricTextValue = new Text(metricResultsGroup, SWT.BORDER);
			metricTextValue.setText(String.valueOf(metric.getMetricValue()));
			metricTextValue.setEditable(false);
		}
	}

	private void clearGroup(Group group) {		
		for(Control widget: group.getChildren()){
			widget.dispose();
		}
		composite.layout();
	}

	/**
	 * @param refactorings
	 */
	private void createRefactoringOptions(Set<RefactoringEnum> refactorings) {
		clearGroup(refactoringOptionsGroup);
		
		if(refactorings.isEmpty()){
			Label noOptions = new Label(refactoringOptionsGroup, SWT.NONE);
			noOptions.setText("No refactorings are available for your selected class");
		}
		
		for(RefactoringEnum refactor: refactorings) {
			Button checkBox = new Button(refactoringOptionsGroup, SWT.CHECK);
			checkBox.setText(refactor.getRefactoringName());			
			refactoringCheckboxMap.put(checkBox, refactor);	 
			
			addCheckBoxListener(checkBox);
		}
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
		        boolean selected = button.getSelection();
		        
		        RefactoringEnum refactor = refactoringCheckboxMap.get(button);
		        
		        if(!selected) {
		        	multipleRefactoring.removeRefactoring(refactor);
		        } else {
		        	if(refactor==RefactoringEnum.EXTRACT_METHOD) {
		        		ExtractMethodRefactoring extractMethodRefactoring = refactoringBuilder.getExtractedMethodRefactoring(controller);
		        		multipleRefactoring.addRefactoringsToBeDone(extractMethodRefactoring);
		        	} else if(refactor==RefactoringEnum.EXTRACT_CLASS) {
		        		ExtractClassRefactoring extractClassRefactoring = refactoringBuilder.getExtractedClassRefactoring(controller).iterator().next();
		        		if(extractClassRefactoring!=null) {
		        			multipleRefactoring.addRefactoringsToBeDone(extractClassRefactoring);	
		        		}
		        		//for(ExtractClassRefactoring newRefactoring : refactoringBuilder.getExtractedClassRefactoring(controller)){
		        		//	multipleRefactoring.addRefactoringsToBeDone(newRefactoring);	
		        		//}												

		        	} else if(refactor==RefactoringEnum.MOVE_METHOD) {
		        		// selectedRefactorings.add(new MoveMethodRefactoring(sourceCompilationUnit, targetCompilationUnit, 
		        		//								sourceTypeDeclaration, targetTypeDeclaration, sourceMethod, 
		        		//								additionalMethodsToBeMoved, leaveDelegate, movedMethodName);
		        	}

		        }     		      
		        setPageComplete(anyCheckBoxSelected());		  
		        
		    }

			
		});
	}
	
	private boolean anyCheckBoxSelected() {
		for(Button checkBox: refactoringCheckboxMap.keySet()){
			if(checkBox.getSelection()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public IWizardPage getPreviousPage() {
		if(anyCheckBoxSelected()) {
			return null;
		} else {
			return super.getPreviousPage();
		}
    }

}
