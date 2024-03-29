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
import msc.refactor.jcodecleaner.wizard.view.pages.runnable.RunnableCreatorFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
	private RecalculatedMetricsPanel recalculatedMetricsPanel;

	private Group refactoringOptionsGroup;
	private Group metricResultsGroup;
	private Group recalculatedMetricsGroup;
	private Group userInput;

	private List<Metric> metrics;
	private RefactoringOpportunitiesModel refactoringOpportunities;
	private boolean fileSelected;
	private Label userInputLabel;
	private Text userInputText;

	private static final Font DEFAULT_FONT = new Font(Display.getDefault(), "Arial", 9, SWT.BOLD);

	public RefactoringOptionsPage(WizardController controller, MultipleRefactoring multipleRefactoring,
			boolean fileSelected) {
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
		composite.setLayout(new GridLayout(1, false));

		refactoringOptionsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		refactoringOptionsGroup.setText("Refactoring Options Available");
		refactoringOptionsGroup.setFont(DEFAULT_FONT);
		refactoringOptionsGroup.setLayout(new GridLayout(4, true));
		refactoringOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		refactoringOptionsGroup.pack();
		
		metricResultsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		metricResultsGroup.setText("Current Metric Values");
		metricResultsGroup.setFont(DEFAULT_FONT);
		metricResultsGroup.setLayout(new GridLayout(1, false));
		metricResultsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		metricResultsGroup.pack();
		
		recalculatedMetricsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		recalculatedMetricsGroup.setLayout(new GridLayout(1, false));
		recalculatedMetricsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		recalculatedMetricsGroup.setText("Metric Values (With refactoring applied)");
		recalculatedMetricsGroup.setFont(DEFAULT_FONT);
		recalculatedMetricsGroup.pack();
		recalculatedMetricsPanel = new RecalculatedMetricsPanel(recalculatedMetricsGroup, controller, getContainer());

		setControl(composite);
		setPageComplete(false);

		if (fileSelected) {
			onEnterPage();
		}
	}

	/**
	 * Sets up the configuration for the refactoring options page
	 */
	public void onEnterPage() {

		final WizardModel model = controller.getModel();

		try {
			
			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) {

					try {
						monitor.beginTask("Computing refactoring list: ", 3);

						IFile file = model.getIFile();
						multipleRefactoring.removeRefactoring();

						monitor.subTask(" analysing class ");					
						Analyser analyser = new Analyser();				
						analyser.analyseSelectionAndUpdateMetricValues(file, model.getMetrics());
						monitor.worked(1);

						monitor.subTask(" calculating metrics ");
						setRefactoringOpportunities(analyser.identifyRefactoringOpportunities(file));
						monitor.worked(1);
						
						monitor.subTask(" identifying possible refactorings");
						model.addFitnessFunctionCalculation(analyser.calculateFitnessFunction(
								refactoringOpportunities.getAvailableRefactorings(), 
								model.getMetrics()));
						Thread.sleep(700);
						monitor.worked(1);
						
						monitor.done();
						model.setRefactoringOpportunities(refactoringOpportunities);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		createPanels(controller.getModel().getMetrics(), getRefactoringOpportunities().getAvailableRefactorings(),
				model.getFitnessFunctionCalulations());
	}

	/**
	 * On Entering the Re-factoring options page sets metrics, refactorings and
	 * fitness function
	 * 
	 * @param metrics
	 * @param refactorings
	 * @param fitnessFunctionCalcs
	 */
	public void createPanels(List<Metric> metrics, Set<RefactoringEnum> refactorings,
			LinkedList<Double> fitnessFunctionCalcs) {

		recalculatedMetricsGroup.setVisible(false);

		createMetricResultPanel(metrics);
		createRefactoringOptions(refactorings);

		composite.layout();
	}

	/**
	 * Adds each metric to the group for holding metric results
	 * 
	 * @param metrics
	 */
	private void createMetricResultPanel(List<Metric> metrics) {
		clearGroup(metricResultsGroup);

		Group metricGroup = new Group(metricResultsGroup, SWT.NONE);
		metricGroup.setLayout(new GridLayout(4, false));

		for (Metric metric : metrics) {
			Label metricLabel = new Label(metricGroup, SWT.NONE);
			metricLabel.setText(metric.getMetricFullName());
			metricLabel.setToolTipText(metric.getMetricFullName());

			Text metricTextValue = new Text(metricGroup, SWT.NONE);
			metricTextValue.setText(String.format("%.3f", metric.getMetricValue()));
			metricTextValue.setEditable(false);
		}

		metricResultsGroup.pack(true);
		metricResultsGroup.layout();
	}

	/**
	 * Disposes of widgets in given group
	 * 
	 * @param group
	 */
	private void clearGroup(Group group) {
		for (Control widget : group.getChildren()) {
			if (!widget.isDisposed()) {
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

		if (refactorings.isEmpty()) {
			Label noOptions = new Label(refactoringOptionsGroup, SWT.NONE);
			noOptions.setText("No refactorings were identified for your selected class");
		} else {

			Group refactorOpts = new Group(refactoringOptionsGroup, SWT.NONE);
			refactorOpts.setLayout(new GridLayout(2, false));

			for (RefactoringEnum refactor : refactorings) {

				Button checkBox = new Button(refactorOpts, SWT.CHECK);
				checkBox.setText(refactor.getRefactoringName());
				refactoringCheckboxMap.put(checkBox, refactor);

				addCheckBoxListener(checkBox);

			}
			userInput = new Group(refactoringOptionsGroup, SWT.NONE);
			userInput.setLayout(new GridLayout(2, false));
			userInput.setVisible(false);

			userInputLabel = new Label(userInput, SWT.NONE);
			userInputLabel.setText("Name for new method: ");
			userInputLabel.setVisible(false);

			userInputText = new Text(userInput, SWT.BORDER);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			userInputText.setLayoutData(gridData);
			userInputText.setLayoutData(new GridData(90, 15));
			userInputText.setEditable(true);
			userInputText.setVisible(false);
			userInput.pack();
			userInput.layout();
		}
		refactoringOptionsGroup.pack(true);
		refactoringOptionsGroup.layout();
	}

	/**
	 * Adds listener to the given checkBox
	 * 
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

				if (!selected) {
					multipleRefactoring.removeRefactoring(refactor);
				} else {
					if (refactor == RefactoringEnum.EXTRACT_METHOD) {
						
						final ExtractMethodRefactoring extractMethodRefactoring = refactoringBuilder
								.getExtractedMethodRefactoring(iCompilationUnit, file);
						
						multipleRefactoring.addRefactoringsToBeDone(extractMethodRefactoring);
						
						recalculatedMetricsPanel.recalculateMetrics(file, controller.getModel().getMetrics(),
								RunnableCreatorFactory.getInstance(extractMethodRefactoring, controller));
						
						userInputText.setVisible(true);
						userInput.setVisible(true);
						userInputLabel.setVisible(true);
						userInputText.setText(extractMethodRefactoring.getExtractedMethodName());
						userInputLabel.setText("New Method Name: ");

						userInputText.addModifyListener(new ModifyListener() {
							public void modifyText(ModifyEvent event) {
								// Get the widget whose text was modified
								Text text = (Text) event.widget;
								extractMethodRefactoring.setExtractedMethodName(text.getText());
								System.out.println(text.getText());
							}
						});
						
					} else if (refactor == RefactoringEnum.EXTRACT_CLASS) {
						Set<ExtractClassRefactoring> extractClassRefactorings = refactoringBuilder
								.getExtractedClassRefactoring(iCompilationUnit, file);
						
						for (final ExtractClassRefactoring extractClassRefactoring : extractClassRefactorings) {
							recalculatedMetricsPanel.recalculateMetrics(file, controller.getModel().getMetrics(),
									RunnableCreatorFactory.getInstance(extractClassRefactoring, controller));
							
							multipleRefactoring.addRefactoringsToBeDone(extractClassRefactoring);
							userInputText.setVisible(true);
							userInputLabel.setVisible(true);
							userInput.setVisible(true);
							userInputText.setText(extractClassRefactoring.getExtractedTypeName());
							userInputLabel.setText("New Class Name: ");

							userInputText.addModifyListener(new ModifyListener() {
								public void modifyText(ModifyEvent event) {
									// Get the widget whose text was modified
									Text text = (Text) event.widget;
									extractClassRefactoring.setExtractedTypeName(text.getText());
									System.out.println(text.getText());
								}
							});
						}

					} else if (refactor == RefactoringEnum.MOVE_METHOD) {
						Set<MoveMethodRefactoring> moveMethodRefactorings = refactoringBuilder
								.getMoveMethodRefactoring(iCompilationUnit, file);
						for (MoveMethodRefactoring moveMethod : moveMethodRefactorings) {
							multipleRefactoring.addRefactoringsToBeDone(moveMethod);
							recalculatedMetricsPanel.recalculateMetrics(file, controller.getModel().getMetrics(),
									RunnableCreatorFactory.getInstance(moveMethod, controller));
						}
					}

				}
				composite.layout();
				setPageComplete(anyCheckBoxSelected());

			}

			private void enableDisableButtons(boolean selected, Button button) {
				for (Button checkBox : refactoringCheckboxMap.keySet()) {
					if (!checkBox.isDisposed()) {
						if (selected && checkBox != button) {
							checkBox.setEnabled(false);
						}

						if (!selected) {
							checkBox.setEnabled(true);
							userInputText.setVisible(false);
							userInputText.setText("");
							userInputLabel.setVisible(false);
							userInput.setVisible(false);
							recalculatedMetricsGroup.setVisible(false);
						}
					}
				}
			}

		});

		composite.layout();
	}

	private boolean anyCheckBoxSelected() {
		for (Button checkBox : refactoringCheckboxMap.keySet()) {
			if (!checkBox.isDisposed() && checkBox.getSelection()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRefactoringOptions() {
		return refactoringCheckboxMap.size() > 0;
	}

	@Override
	public IWizardPage getPreviousPage() {
		if (anyCheckBoxSelected()) {
			return null;
		} else {
			return super.getPreviousPage();
		}
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public RefactoringOpportunitiesModel getRefactoringOpportunities() {
		return refactoringOpportunities;
	}

	public void setRefactoringOpportunities(RefactoringOpportunitiesModel refactoringOpportunities) {
		this.refactoringOpportunities = refactoringOpportunities;
	}

}
