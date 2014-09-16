package msc.refactor.codecleaner.wizard.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class WizardModel {

	private IWorkbenchPart part;
	private ISelection selection;
	private IWorkbenchWindow window;
	private IStructuredSelection structuredSelection;
	
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
	
	public IStructuredSelection getStructuredSelection() {
		return structuredSelection;
	}
	public void setStructuredSelection(IStructuredSelection structuredSelection) {
		this.structuredSelection = structuredSelection;
	}
	
	public IFile getFileFromStructuredSelection() {		 
		Object obj = structuredSelection.getFirstElement();
		IFile file = (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);
		if (file != null) {
			if (obj instanceof IAdaptable) {
				file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);				
			} 
		}
		return file;
	}
	
}
