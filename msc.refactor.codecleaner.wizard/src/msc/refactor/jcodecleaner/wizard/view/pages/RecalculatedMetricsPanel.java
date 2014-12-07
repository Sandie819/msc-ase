package msc.refactor.jcodecleaner.wizard.view.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;
import msc.refactor.jcodecleaner.wizard.view.pages.runnable.RunnableCreator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * Recalculates and display new metrics
 * 
 * @author mulligans
 *
 */
public class RecalculatedMetricsPanel {

	private Group previewGroup;
	private WizardController controller;
	private IWizardContainer container;	
	
	private static final Font BOLD_FONT = new Font(Display.getDefault(), "Arial", 10, SWT.BOLD);
	
	public RecalculatedMetricsPanel(Group previewGroup, WizardController controller, IWizardContainer container) {
		this.previewGroup = previewGroup;
		this.controller = controller;
		this.container = container;
	}
	
	/**
	 * @param file
	 * @param originalMetrics
	 * @param runnableCreator
	 */
	public void recalculateMetrics(final IFile file, List<Metric> originalMetrics, 
			RunnableCreator runnableCreator) {
		final List<Metric> newMetrics = getNewMetrics(originalMetrics);
		
		IRunnableWithProgress runnable = runnableCreator.createRunnableProgress(file, newMetrics);
				
		try {
			container.run(false, true, runnable);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		createRecalculatedMetricsPanel(newMetrics);
		previewGroup.layout();
	}

	/**
	 * @param originalChosenMetrics
	 * @return
	 */
	private List<Metric> getNewMetrics(List<Metric> originalChosenMetrics) {
		List<Metric> newMetrics = new ArrayList<Metric>();
		System.out.println("Original Metrics");

		for(Metric projectMetric: controller.projectMetrics()) {
			for(Metric chosenMetric : originalChosenMetrics) {				
				if(chosenMetric.getMetricFullName().equals(projectMetric.getMetricFullName())) {
					System.out.println(chosenMetric.getMetricShortName() +" "+ chosenMetric.getMetricValue());
					projectMetric.setOriginalMetricValue(chosenMetric.getMetricValue());
					newMetrics.add(projectMetric);	
				}

			}
		}
		return newMetrics;
	}

	
	/**
	 * @param newMetrics
	 */
	private void createRecalculatedMetricsPanel(List<Metric> newMetrics) {
		clearPreviewGroup();

		Group metricGroup = new Group(previewGroup, SWT.NONE);
		metricGroup.setLayout(new GridLayout(4, false));

		for (Metric metric : newMetrics) {
			Label metricLabel = new Label(metricGroup, SWT.NONE);
			metricLabel.setText(metric.getMetricFullName());
			metricLabel.setToolTipText(metric.getMetricFullName());

			Text metricTextValue = new Text(metricGroup, SWT.NONE);
			metricTextValue.setText(String.format("%.3f", metric.getMetricValue()));

			if(metric.getMetricValue()< metric.getOriginalMetricValue()){
				metricTextValue.setFont(BOLD_FONT);
				metricTextValue.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
			} else if (metric.getMetricValue() > metric.getOriginalMetricValue()) {
				metricTextValue.setFont(BOLD_FONT);
				metricTextValue.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
			}
			metricTextValue.setEditable(false);
		}

		previewGroup.setVisible(true);
	}
	
	/**
	 * Clear down the preview group
	 */
	private void clearPreviewGroup() {
		for (Control widget : previewGroup.getChildren()) {
			if (!widget.isDisposed()) {
				widget.dispose();
			}
		}
		previewGroup.layout();
	}
}