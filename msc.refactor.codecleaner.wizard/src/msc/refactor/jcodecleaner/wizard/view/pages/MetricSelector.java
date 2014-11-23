package msc.refactor.jcodecleaner.wizard.view.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.swt.SWT;
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

public class MetricSelector {

	private static final Font DEFAULT_FONT = new Font(Display.getDefault(), "Arial", 9, SWT.BOLD);
	private Map<Button, Metric> metricsCheckboxMap;
	private WizardController controller;
	private List<Metric> selectedMetrics;

	public MetricSelector(WizardController controller) {
		this.controller = controller;
		metricsCheckboxMap = new HashMap<Button, Metric>();
		selectedMetrics = new ArrayList<Metric>();
	}

	public Control createClassSelectorArea(Composite parent) {

		Group metricOptionsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		metricOptionsGroup.setText("Metric Options Available");
		metricOptionsGroup.setFont(DEFAULT_FONT);
		metricOptionsGroup.setLayout(new GridLayout(4, false));
		metricOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		metricOptionsGroup.pack();

		Group refactorOpts = new Group(metricOptionsGroup, SWT.NONE);
		refactorOpts.setLayout(new GridLayout(2, false));

		for (Metric metric : controller.projectMetrics()) {
			Button checkBox = new Button(refactorOpts, SWT.CHECK);
			checkBox.setText(metric.getMetricFullName());
			checkBox.setSelection(true);
			metricsCheckboxMap.put(checkBox, metric);
			selectedMetrics.add(metric);
			addCheckBoxListener(checkBox);
		}
		
		controller.getModel().setMetrics(selectedMetrics);
		return parent;
	}

	private void addCheckBoxListener(Button checkBox) {
		
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Button button = (Button) e.widget;
				boolean selected = button.getSelection();
				Metric metricInQuestion = metricsCheckboxMap.get(button);

				if(!selected) {
					selectedMetrics.remove(metricInQuestion);      
				} else {
					selectedMetrics.add(metricInQuestion);
				}
				
				controller.getModel().setMetrics(selectedMetrics);
			}
		});
	}
}
