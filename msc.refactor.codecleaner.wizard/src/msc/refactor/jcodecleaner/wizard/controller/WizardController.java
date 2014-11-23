package msc.refactor.jcodecleaner.wizard.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import msc.refactor.jcodecleaner.analyser.metrics.CyclomaticComplexity;
import msc.refactor.jcodecleaner.analyser.metrics.Instability;
import msc.refactor.jcodecleaner.analyser.metrics.LCOM1;
import msc.refactor.jcodecleaner.analyser.metrics.LCOM2;
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
	
	public Set<Metric> projectMetrics(){
		Set<Metric> metrics = new HashSet<Metric>();
		metrics.add(new LCOM1());
		metrics.add(new LCOM2());
		metrics.add(new LCOM4());
		metrics.add(new CyclomaticComplexity());
		metrics.add(new Instability());
		
		return metrics;
	}
}
