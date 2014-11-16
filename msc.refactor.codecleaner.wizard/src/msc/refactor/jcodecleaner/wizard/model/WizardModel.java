package msc.refactor.jcodecleaner.wizard.model;

import java.util.LinkedList;

import msc.refactor.jcodecleaner.wizard.view.MultipleRefactorWizardDialog;

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
	private RefactoringOpportunitiesModel refactoringOpportunities;
	private LinkedList<Double> fitnessFunctionCalulations;
	private gr.uom.java.jdeodorant.refactoring.Activator deodorantActivator;
	private MultipleRefactorWizardDialog multipleRefactorWizardDialog;
	
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
	
	public RefactoringOpportunitiesModel getRefactoringOpportunities() {
		return refactoringOpportunities;
	}
	public void setRefactoringOpportunities(
			RefactoringOpportunitiesModel refactoringOpportunities) {
		this.refactoringOpportunities = refactoringOpportunities;
	}
	
	public LinkedList<Double> getFitnessFunctionCalulations() {
		return fitnessFunctionCalulations;
	}
	public void setFitnessFunctionCalulations(LinkedList<Double> fitnessFunctionCalulations) {
		this.fitnessFunctionCalulations = fitnessFunctionCalulations;
	}
	/**
	 * Add fitness function to the linked list (maintains insertion order)
	 * 
	 * @param fitnessFunction
	 */
	public void addFitnessFunctionCalculation(Double fitnessFunction) {
		if(fitnessFunctionCalulations==null) {
			fitnessFunctionCalulations = new LinkedList<Double>();
		}
		
		fitnessFunctionCalulations.add(fitnessFunction);
	}
	
	public gr.uom.java.jdeodorant.refactoring.Activator getDeodorantActivator() {
		return deodorantActivator;
	}
	public void setDeodorantActivator(gr.uom.java.jdeodorant.refactoring.Activator deodorantActivator) {
		this.deodorantActivator = deodorantActivator;
	}	
	
	public MultipleRefactorWizardDialog getMultipleRefactorWizardDialog() {
		return multipleRefactorWizardDialog;
	}
	public void setMultipleRefactorWizardDialog(MultipleRefactorWizardDialog multipleRefactorWizardDialog) {
		this.multipleRefactorWizardDialog = multipleRefactorWizardDialog;
	}
	public IFile getIFile() {		 
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
