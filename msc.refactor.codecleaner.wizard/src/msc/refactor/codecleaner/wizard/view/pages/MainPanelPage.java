package msc.refactor.codecleaner.wizard.view.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import msc.refactor.codecleaner.wizard.controller.WizardController;
import msc.refactor.codecleaner.wizard.view.TreeSelector;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class MainPanelPage extends WizardPage implements Listener, PropertyChangeListener {

	private static final String CLASS_SELECTED = "CLASS_SELECTED";
	private Composite classComposite;
	private WizardController controller;
	Composite mainComposite;
	private SourceViewer viewer;

	protected MainPanelPage(Shell shell, IWorkbench workbench, IStructuredSelection selection) {
		super("Page1");
		setTitle("Project structure");
		setDescription("Select class for cleaning");
	}

	public MainPanelPage(WizardController controller) {
		super("Page1");
		setTitle("Project structure");
		setDescription("Select class for cleaning");
		this.controller = controller;
		controller.addPropertyChangeListener(this);
	}

	@Override
	public void createControl(Composite parent) {
		// create the composite to hold the widgets
		mainComposite = new Composite(parent, SWT.NONE);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 2;
	    mainComposite.setLayout(gridLayout);

		TreeSelector treeSelector = new TreeSelector(controller, new WorkbenchLabelProvider(), 
				new BaseWorkbenchContentProvider());

		treeSelector.createDialogArea(mainComposite);		
	//	classComposite = new Composite(mainComposite, SWT.NULL);
				
		viewer = new SourceViewer(mainComposite, null, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER); 
		Document doc = new Document(); 
		doc.set(""); 
		viewer.setDocument(doc); 
		JavaSourceViewerConfiguration javaConf = new JavaSourceViewerConfiguration
				(org.eclipse.jdt.ui.JavaUI.getColorManager(), 
				org.eclipse.jdt.ui.PreferenceConstants.getPreferenceStore(), null, 
				null); 
		
		viewer.configure(javaConf);	
		
		setControl(mainComposite);		
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(CLASS_SELECTED)) {
			handleClassSelectedEvent(evt.getOldValue(), evt.getNewValue());
		}
	}

	private void handleClassSelectedEvent(Object oldValue, Object newValue) {
		IFile file = (IFile) newValue;
		System.out.print("File name = "+file.getName()+ " file extension= " +file.getFileExtension());
			
//		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
//		ITextEditor javaEditor = null;
//		try {
//			javaEditor = (ITextEditor) JavaUI.openInEditor(compilationUnit);
//		} catch (PartInitException | JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		IPreferenceStore store = JavaPlugin.getDefault().getCombinedPreferenceStore();
//		//SourceViewer viewer = new SourceViewer(mainComposite, null, SWT.V_SCROLL | SWT.H_SCROLL);
//		JavaSourceViewerConfiguration config = new JavaSourceViewerConfiguration(
//				JavaPlugin.getDefault().getJavaTextTools().getColorManager(), 
//				store, javaEditor, IJavaPartitions.JAVA_PARTITIONING);
//		viewer.configure(config);		
//		viewer.getTextWidget().setFont(JFaceResources.getFont("org.eclipse.jdt.ui.editors.textfont"));
//		viewer.refresh();
	
	}

}
	