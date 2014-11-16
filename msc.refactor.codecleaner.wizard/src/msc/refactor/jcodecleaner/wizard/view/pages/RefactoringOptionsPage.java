package msc.refactor.jcodecleaner.wizard.view.pages;

import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.MoveMethodRefactoring;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.jcodecleaner.analyser.Analyser;
import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.enums.RefactoringEnum;
import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.jcodecleaner.multiplerefactoring.RefactoringBuilder;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;
import msc.refactor.jcodecleaner.wizard.model.RefactoringOpportunitiesModel;
import msc.refactor.jcodecleaner.wizard.model.WizardModel;

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
import org.eclipse.swt.layout.RowLayout;
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
//	private Group fitnessFunctionGroup;
	
	private boolean fileSelected;
	
	public RefactoringOptionsPage(WizardController controller, 
			MultipleRefactoring multipleRefactoring, boolean fileSelected) {
		super("Refactoring options");
		setMessage("Metric results, please select refactoring from given choices");
		
		this.controller = controller;
		this.multipleRefactoring = multipleRefactoring;
		this.fileSelected = fileSelected;
		
		refactoringCheckboxMap = new HashMap<Button, RefactoringEnum>();
		refactoringBuilder = new RefactoringBuilder();
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);		
		composite.setLayout(new GridLayout(1, true));		
		
//		fitnessFunctionGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
//		fitnessFunctionGroup.setText("Overall Fitness Function");
//		fitnessFunctionGroup.setLayout(new GridLayout(1, true));
//		fitnessFunctionGroup.setLayoutData(new GridData(GridData.FILL_BOTH));	
		
		metricResultsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		metricResultsGroup.setText("Metric Results");			
		metricResultsGroup.setLayout(new GridLayout(1, false));
		metricResultsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		refactoringOptionsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		refactoringOptionsGroup.setText("Refactoring Options Available");
		refactoringOptionsGroup.setLayout(new GridLayout(4, true));
		refactoringOptionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		setControl(composite);
		setPageComplete(false);		
		
		if(fileSelected) {
			onEnterPage();
		}
	}
	
	/**
	 * Sets up the configuration for the refactoring options page
	 */
	public void onEnterPage() {
		WizardModel model = controller.getModel();
		IFile file = model.getIFile();
		multipleRefactoring.removeRefactoring();

		Analyser analyser = new Analyser();
		List<Metric> metrics = analyser.analyseSelection(file);
		RefactoringOpportunitiesModel refactoringOpportunities = analyser.identifyRefactoringOpportunities(file);
		model.addFitnessFunctionCalculation(analyser.calculateFitnessFunction(refactoringOpportunities.getAvailableRefactorings()));
		model.setRefactoringOpportunities(refactoringOpportunities);

		createPanels(metrics, refactoringOpportunities.getAvailableRefactorings(), model.getFitnessFunctionCalulations());
	}
	
	/**
	 * On Entering the Re-factoring options page sets metrics, refactorings
	 * and fitness function
	 * 
	 * @param metrics
	 * @param refactorings
	 * @param fitnessFunctionCalcs
	 */
	public void createPanels(List<Metric> metrics, Set<RefactoringEnum> refactorings,
			LinkedList<Double> fitnessFunctionCalcs){
		
		//createFitnessFunctionPanel(fitnessFunctionCalcs);		
		createMetricResultPanel(metrics);
		createRefactoringOptions(refactorings);

		composite.layout();
	}

//	/**
//	 * @param fitnessFunctionCalcs
//	 */
//	private void createFitnessFunctionPanel(LinkedList<Double> fitnessFunctionCalcs) {
//		clearGroup(fitnessFunctionGroup);
//		
//		Label fitnessLabel = new Label(fitnessFunctionGroup, SWT.NONE);
//		fitnessLabel.setText("Calculated Fitness functions");
//		
//		Group fitnessFunctionResultGroup = new Group(fitnessFunctionGroup, SWT.SHADOW_ETCHED_IN);		
//		fitnessFunctionResultGroup.setLayout(new GridLayout(4, true));
//		
//		int index = 1;
//		for(Double fitnessCalculation: fitnessFunctionCalcs) {
//			Label resultLabel = new Label(fitnessFunctionResultGroup, SWT.NONE);
//			resultLabel.setText("Phase "+index+ ": ");
//			
//			Text text = new Text(fitnessFunctionResultGroup, SWT.BORDER);
//			text.setText(String.valueOf(fitnessCalculation));
//			text.setEditable(false);
//			index++;
//		}
//		
//		Label lblHint = new Label(fitnessFunctionGroup, SWT.NONE);
//		lblHint.setText("Zero is optimal, above zero means there is room for improvment \n"
//				+ "Based on weighted values from the various metrics outlined below.");		
//	}

	/**
	 * Adds each metric to the group for holding metric results
	 * 
	 * @param metrics
	 */
	private void createMetricResultPanel(List<Metric> metrics) {
		clearGroup(metricResultsGroup);
		
		Group metricGroup = new Group(metricResultsGroup, SWT.NONE);		
		metricGroup.setLayout(getRowLayout());
		
		for(Metric metric: metrics) {
			Group metricAndValueGroup = new Group(metricGroup, SWT.SHADOW_ETCHED_IN);		
			metricAndValueGroup.setLayout(new GridLayout(2, false));
			
			Label metricLabel = new Label(metricAndValueGroup, SWT.NONE);
			metricLabel.setText(metric.getMetricFullName());
			metricLabel.setToolTipText(metric.getMetricFullName());
			
			Text metricTextValue = new Text(metricAndValueGroup, SWT.NONE);
			metricTextValue.setText(String.valueOf(metric.getMetricValue()));
			metricTextValue.setEditable(false);
		}
	}

	private void clearGroup(Group group) {		
		for(Control widget: group.getChildren()){
			if(!widget.isDisposed())  {
				widget.dispose();
			}
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
			noOptions.setText("No refactorings were identified for your selected class");
		}
		
		for(RefactoringEnum refactor: refactorings) {
			Button checkBox = new Button(refactoringOptionsGroup, SWT.CHECK);
			checkBox.setText(refactor.getRefactoringName());			
			refactoringCheckboxMap.put(checkBox, refactor);	 
			
			addCheckBoxListener(checkBox);
		}
	}
	
	private RowLayout getRowLayout() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = true;
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 5;
		return rowLayout;
	}


	/**
	 * Adds listener to the given checkBox
	 * @param checkBox
	 */
	private void addCheckBoxListener(Button checkBox) {
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
		    public void widgetSelected(SelectionEvent e) {

				IFile file = controller.getModel().getIFile();
				ICompilationUnit iCompilationUnit = JavaCore.createCompilationUnitFrom(file);
				multipleRefactoring.setfCompilationUnit(iCompilationUnit);
				
		        Button button = (Button) e.widget;
		        boolean selected = button.getSelection();
		        enableDisableButtons(selected, button);
		        
		        RefactoringEnum refactor = refactoringCheckboxMap.get(button);
		        
		        if(!selected) {
		        	multipleRefactoring.removeRefactoring(refactor);		        
		        } else {
		        	if(refactor==RefactoringEnum.EXTRACT_METHOD) {
		        		ExtractMethodRefactoring extractMethodRefactoring = refactoringBuilder.getExtractedMethodRefactoring(controller);
		        		multipleRefactoring.addRefactoringsToBeDone(extractMethodRefactoring);
		        	} else if(refactor==RefactoringEnum.EXTRACT_CLASS) {
		        		Set<ExtractClassRefactoring> extractClassRefactorings = refactoringBuilder.getExtractedClassRefactoring(controller);		        		
		        		for(ExtractClassRefactoring extractClassRefactoring: extractClassRefactorings) {
		        			multipleRefactoring.addRefactoringsToBeDone(extractClassRefactoring);	
		        		}
		        	} else if(refactor==RefactoringEnum.MOVE_METHOD) {
		        		Set<MoveMethodRefactoring> moveMethodRefactorings = refactoringBuilder.getMoveMethodRefactoring(controller);
		        		for(MoveMethodRefactoring moveMethod: moveMethodRefactorings) {
		        			multipleRefactoring.addRefactoringsToBeDone(moveMethod);
		        		}
		        	}

		        }     		      
		        setPageComplete(anyCheckBoxSelected());		  
		        
		    }

			private void enableDisableButtons(boolean selected, Button button) {
				for(Button checkBox: refactoringCheckboxMap.keySet()){
					if(!checkBox.isDisposed()) {
						if(selected && checkBox!=button) {
							checkBox.setEnabled(false);
						}

						if(!selected){
							checkBox.setEnabled(true);
						}
					}
				}
			}

			
		});
	}
	
	private boolean anyCheckBoxSelected() {
		for(Button checkBox: refactoringCheckboxMap.keySet()){
			if(!checkBox.isDisposed() && checkBox.getSelection()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRefactoringOptions() {
		return refactoringCheckboxMap.size()>0;
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
