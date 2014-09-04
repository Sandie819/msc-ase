package msc.refactor.codecleaner.wizard.model;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class WizardModel {

	private IWorkbenchPart part;
	private ISelection selection;
	private IWorkbenchWindow window;
	
	public IWorkbenchPart getPart() {
		return part;
	}
	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}
	public ISelection getSelection() {
		return selection;
	}
	public void setSelection(ISelection selection) {
		this.selection = selection;
	}
	public IWorkbenchWindow getWindow() {
		return window;
	}
	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}
	
}
