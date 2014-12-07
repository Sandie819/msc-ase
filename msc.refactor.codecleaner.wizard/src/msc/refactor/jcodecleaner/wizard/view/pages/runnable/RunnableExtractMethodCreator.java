package msc.refactor.jcodecleaner.wizard.view.pages.runnable;

import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;

import java.util.List;

import msc.refactor.jcodecleaner.analyser.metrics.Metric;
import msc.refactor.jcodecleaner.multiplerefactoring.RefactoringBuilder;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ltk.core.refactoring.CompositeChange;

public class RunnableExtractMethodCreator extends RunnableCreator {

	private RefactoringBuilder refactoringBuilder;
	private IProject copiedProject; 
	
	public RunnableExtractMethodCreator(WizardController controller){
		super(controller);
		refactoringBuilder = new RefactoringBuilder();
	}
	/**
	 * @param file
	 * @param newMetrics
	 * @return 
	 */
	public IRunnableWithProgress createRunnableProgress(final IFile file, final List<Metric> newMetrics) {
		IRunnableWithProgress operation = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) {
				try {
					int total = 100;

					monitor.beginTask("Refactoring Simulation: ", total);
					copiedProject = null;
					
					monitor.subTask("  copying project ");
					monitor.worked(10);
					copiedProject = getCopiedProjectForRefactoringSimulation(file, new NullProgressMonitor());
					
					String packageName = file.getParent().getFullPath().toString()
							.replace(file.getProject().getName() + "/", "");
					
					IFile copiedFile = copiedProject.getFile(packageName.concat("/").concat(file.getName()));
					if (copiedFile.exists()) {
						ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(copiedFile);
						monitor.worked(30);

						monitor.subTask("  performing refactor ");
						
						ExtractMethodRefactoring extractMethodRefactoring = refactoringBuilder
								.getExtractedMethodRefactoring(compilationUnit, copiedFile);
						CompositeChange change = checkAndCreateChange(extractMethodRefactoring, monitor);

						monitor.worked(30);

						if (monitor.isCanceled()) {
							throw new OperationCanceledException();
						}

						change.perform(new NullProgressMonitor());

						monitor.subTask("  analysing refactored simulated class ");
						performAnalysisOfCopiedFile(copiedFile, newMetrics, monitor);
						
						monitor.worked(30);
						Thread.sleep(800);
					}

					copiedProject.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					try {
						if (copiedProject != null) {
							copiedProject.delete(true, new SubProgressMonitor(monitor, 100));
						}
						monitor.done();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}

		};
		return operation;
	}

}
