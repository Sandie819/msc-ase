package msc.refactor.jcodecleaner.wizard.view.pages.runnable;

import java.util.List;

import msc.refactor.jcodecleaner.analyser.Analyser;
import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;

public abstract class RunnableCreator {
	protected WizardController controller;
	
	public RunnableCreator(WizardController controller){
		this.controller = controller;
	}
	
	public abstract IRunnableWithProgress createRunnableProgress(final IFile file, final List<Metric> newMetrics);
	
	/**
	 * @param refactoring
	 * @param monitor
	 * @return
	 * @throws OperationCanceledException
	 * @throws CoreException
	 */
	protected CompositeChange checkAndCreateChange(Refactoring refactoring, IProgressMonitor monitor) throws OperationCanceledException, CoreException  {
		refactoring.checkFinalConditions(new NullProgressMonitor());
		CompositeChange change = (CompositeChange) refactoring.createChange(new NullProgressMonitor());
		return change;
	}
	


	/**
	 * @param file
	 * @param subProgressMonitor 
	 * @return Copy of the original Project
	 * @throws CoreException 
	 */
	protected IProject getCopiedProjectForRefactoringSimulation(IFile file, IProgressMonitor monitor) throws CoreException {

		IProject copiedProject = null;
		String copiedProjectName = file.getProject().getFullPath().toString().concat("-RefactorSimulator");
		Path path = new Path(copiedProjectName);
		
		IProject existingDuplicateProject = ResourcesPlugin.getWorkspace().getRoot().getProject(copiedProjectName);
		if(existingDuplicateProject.exists()) {
			existingDuplicateProject.delete(true, monitor);
		}
		
		file.getProject().copy(path, true, monitor);
		copiedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(copiedProjectName);

		return copiedProject;

	}
	
	/**
	 * @param copiedFile
	 * @param newMetrics
	 */
	protected void performAnalysisOfCopiedFile(IFile copiedFile, List<Metric> newMetrics,
			IProgressMonitor monitor) {
		Analyser analyser = new Analyser();
		analyser.analyseSelectionAndGetMetricValues(copiedFile, newMetrics, monitor);
		System.out.println("New Metrics");
		for(Metric m: newMetrics) {
			System.out.println(m.getMetricShortName() +" "+ m.getMetricValue());
		}
	}

}
