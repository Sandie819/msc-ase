package msc.refactor.jcodecleaner.wizard.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import msc.refactor.jcodecleaner.analyser.metrics.CyclomaticComplexity;
import msc.refactor.jcodecleaner.analyser.metrics.DepthOfInheritanceTree;
import msc.refactor.jcodecleaner.analyser.metrics.Instability;
import msc.refactor.jcodecleaner.analyser.metrics.LCOM2;
import msc.refactor.jcodecleaner.analyser.metrics.LCOM3;
import msc.refactor.jcodecleaner.analyser.metrics.LCOM4;
import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.wizard.model.WizardModel;

public class WizardController {

	private PropertyChangeSupport changeSupport;
	private WizardModel model;

	public WizardController(WizardModel model) {
		this.model = model;
		this.changeSupport = new PropertyChangeSupport(this);
	}

	public WizardModel getModel() {
		return model;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (changeSupport != null) {
			changeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	public void resetModel() {
		model.setFitnessFunctionCalulations(null);
		model.setRefactoringOpportunities(null);
		model.setSelection(null);
		model.setStructuredSelection(null);
		model.setDeodorantActivator(new gr.uom.java.jdeodorant.refactoring.Activator());		
	}
	
	public List<Metric> projectMetrics(){
		List<Metric> metrics = new ArrayList<Metric>();
		//metrics.add(new LCOM1());
		metrics.add(new LCOM2());
		metrics.add(new LCOM3());
		metrics.add(new LCOM4());
		metrics.add(new CyclomaticComplexity());
		metrics.add(new Instability());
		metrics.add(new DepthOfInheritanceTree());
		
		Collections.sort(metrics, new Comparator<Metric>() {
			@Override
	        public int compare(final Metric object1, final Metric object2) {
	            return object1.getMetricFullName().compareTo(object2.getMetricFullName());
	        }
		});
	        
		return metrics;
	}
}
