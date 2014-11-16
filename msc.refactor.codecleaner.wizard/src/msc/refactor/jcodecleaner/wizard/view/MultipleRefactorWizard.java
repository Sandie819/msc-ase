package msc.refactor.jcodecleaner.wizard.view;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;
import msc.refactor.jcodecleaner.wizard.Activator;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;
import msc.refactor.jcodecleaner.wizard.view.pages.MainSelectorPage;
import msc.refactor.jcodecleaner.wizard.view.pages.RefactoringOptionsPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.internal.ui.refactoring.PreviewWizardPage;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public class MultipleRefactorWizard extends RefactoringWizard  {

	private WizardController controller;
	private MainSelectorPage mainSelectorPage;
	private RefactoringOptionsPage refactoringOptionsPage;
	private boolean fileSelected;

	public MultipleRefactorWizard(WizardController controller, MultipleRefactoring refactoring, boolean fileSelected) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);

		PlatformUI.getWorkbench().saveAll(
				PlatformUI.getWorkbench().getModalDialogShellProvider(), 
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
				null, false);

		this.controller = controller;
		this.fileSelected = fileSelected;

		setDefaultPageTitle("JCodeCleaner");		
		setDefaultPageImageDescriptor(getImageDescriptor("wizard-icon.gif"));
		setTitleBarColor(new RGB(128,0,128));
		setWindowTitle("JCodeCleaning Wizard");
		setNeedsProgressMonitor(true);

		createPages();
		
		try {
			if(fileSelected) {
				IProject activeProject = controller.getModel().getIFile().getProject();
				activeProject.refreshLocal(IResource.FORCE, null);			
				activeProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
				activeProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	private ImageDescriptor getImageDescriptor(String relativePath) {
		ImageDescriptor descriptor = null;
		try {
			URL iconBaseURL = new URL(Activator.getDefault().getBundle().getEntry("/"), "icons/");

			Activator.getDefault().getImageRegistry().put("wizard-icon",

					descriptor = ImageDescriptor.createFromURL(new URL(iconBaseURL + "wizard-icon.gif")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return descriptor;		
	}

	private void createPages(){				
		refactoringOptionsPage = new RefactoringOptionsPage(controller, (MultipleRefactoring)getMultipleRefactoring(), fileSelected);
		mainSelectorPage = new MainSelectorPage(controller, fileSelected);
	}

	@Override
	protected void addUserInputPages() {
		if(!fileSelected) {
			addPage(mainSelectorPage);
		}
		
		addPage(refactoringOptionsPage);
		
		MultipleRefactorWizardDialog container = (MultipleRefactorWizardDialog)getContainer();
		container.addPageChangingListener(new IPageChangingListener() {
			@Override
			public void handlePageChanging(PageChangingEvent event) {

				if(event.getTargetPage() instanceof RefactoringOptionsPage) {
					setUpRefactoringOptionsPage();
					
				}
				handleFinishButton();
			}

		});
		
		
	}

	protected void handleFinishButton() {
		if(hasValidRefactorings()) {
			controller.getModel().getMultipleRefactorWizardDialog().renameFinishButton("Apply Refactoring");			
		} else {
			controller.getModel().getMultipleRefactorWizardDialog().renameFinishButton("Finish");	
		}
	}

	/**
	 * Sets up the configuration for the refactoring options page
	 */
	protected void setUpRefactoringOptionsPage() {
		refactoringOptionsPage.onEnterPage();
	}

	@Override
	public boolean performFinish() {
		IProgressMonitor monitor = new NullProgressMonitor();
		MultipleRefactoring multipleRefactoring = getMultipleRefactoring();

		OrganizeImportsAction organiseImportsAction = new OrganizeImportsAction(controller.getModel().getPart().getSite());
		try {

			if(!hasValidRefactorings()) {
				return true;
			} else {				

				for(Change change:  multipleRefactoring.getChanges()){
					change.perform(monitor);					 
				}				
				//try {
				((WizardDialog)getContainer()).close();

				controller.getModel().getDeodorantActivator().stop(FrameworkUtil.getBundle(
						controller.getModel().getIFile().
						getClass()).getBundleContext());

				organiseImportsAction.run(JavaCore.createCompilationUnitFrom(controller.getModel().getIFile()));
				controller.getModel().setRefactoringOpportunities(null);					
				controller.getModel().setDeodorantActivator(new gr.uom.java.jdeodorant.refactoring.Activator());

				multipleRefactoring.setRefactoringsToBeDone(new ArrayList<Refactoring>());
				multipleRefactoring.setChanges(new ArrayList<Change>()); 
				
				MultipleRefactorWizard wizard = new MultipleRefactorWizard(controller, multipleRefactoring, true);
				MultipleRefactorWizardDialog wizardDialog = new MultipleRefactorWizardDialog(Display.getDefault().getActiveShell(), wizard);
				MulitpleRefactoringWizardOpenOperation operation = new MulitpleRefactoringWizardOpenOperation(wizard, wizardDialog);
				controller.getModel().setMultipleRefactorWizardDialog(wizardDialog);
				
				operation.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Multiple Refactorings");
			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (InterruptedException exception) {
			// Do nothing
		} catch (Exception exception) {

		}
		return false;
	}

	private MultipleRefactoring getMultipleRefactoring() {
		return (MultipleRefactoring) getRefactoring();
	}

	private boolean hasValidRefactorings() {
		return !(getMultipleRefactoring().getChanges()==null || getMultipleRefactoring().getChanges().isEmpty());
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		
		if(fileSelected && nextPage instanceof RefactoringOptionsPage) {
			setUpRefactoringOptionsPage();
			return refactoringOptionsPage;
		} else {
			return super.getNextPage(page);
		}
	}
	   
	@Override
	public boolean canFinish() {
		handleFinishButton();
		
		if((getContainer().getCurrentPage() == refactoringOptionsPage &&
				!refactoringOptionsPage.hasRefactoringOptions())
				|| getContainer().getCurrentPage() instanceof PreviewWizardPage) {			
			return true;
		} else {
			return false;
		}
	}
}
