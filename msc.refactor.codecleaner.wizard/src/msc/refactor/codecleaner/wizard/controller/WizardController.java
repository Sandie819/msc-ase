package msc.refactor.codecleaner.wizard.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import msc.refactor.codecleaner.wizard.model.WizardModel;

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
}
