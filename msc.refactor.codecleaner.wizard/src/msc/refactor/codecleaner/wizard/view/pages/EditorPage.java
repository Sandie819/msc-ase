package msc.refactor.codecleaner.wizard.view.pages;

import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.views.SliceAnnotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.codecleaner.wizard.controller.WizardController;
import msc.refactor.codecleaner.wizard.model.RefactoringOpportunities;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorPage extends UserInputWizardPage {

	private Composite composite;
	private WizardController controller;
	private SourceViewer sourceViewer;

	public EditorPage(WizardController controller) {
		super("Class Viewer");
		this.controller = controller;
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);

		createTextArea(composite);

		setControl(composite);	
		setPageComplete(true);
	}

	public SourceViewer createTextArea(Composite container) {
		sourceViewer = new SourceViewer(container, null, SWT.V_SCROLL | SWT.H_SCROLL);
		
//		// set up spell-check annotations
//		final SourceViewerDecorationSupport decorationSupport = new SourceViewerDecorationSupport(
//				sourceViewer, null, new DefaultMarkerAnnotationAccess(), EditorsUI.getSharedTextColors());
//
//		AnnotationPreference pref = EditorsUI.getAnnotationPreferenceLookup()
//				.getAnnotationPreference(SpellingAnnotation.TYPE);
//
//		decorationSupport.setAnnotationPreference(pref);
//		decorationSupport.install(EditorsUI.getPreferenceStore());

	
		
//		sourceViewer.getTextWidget().addDisposeListener(new DisposeListener() {
//			public void widgetDisposed(DisposeEvent e) {
//				decorationSupport.uninstall();
//			}
//		});
		GridData data = new GridData(GridData.FILL_BOTH);
		sourceViewer.getControl().setLayoutData(data);
		return sourceViewer;
	}

	/**
	 * @param compilationUnit
	 */
	public void setDocumentOnSourceViewer(){
		IFile file = controller.getModel().getFileFromStructuredSelection();
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);

		RefactoringOpportunities opportunitites = controller.getModel().getRefactoringOpportunities();
		Set<ASTSliceGroup> extractMethodOpportunities = opportunitites.getExtractMethodOpportunities();
		
		opportunitites.isExtractMethodAvailable(file);
		for(ASTSliceGroup astSliceGroup : extractMethodOpportunities){
			
			for(ASTSlice astSlice: astSliceGroup.getCandidates()) {
				
				try {
					IJavaElement sourceJavaElement = JavaCore.create(file);
					
					ITextEditor sourceEditor = (ITextEditor)JavaUI.openInEditor(sourceJavaElement);
					
					IDocument document = new Document(compilationUnit.getSource());
					
					// Setting up the Java Syntax Highlighting
					JavaTextTools tools= JavaPlugin.getDefault().getJavaTextTools();
					tools.setupJavaDocumentPartitioner(document);
					
					sourceViewer.configure(new JavaSourceViewerConfiguration(
							tools.getColorManager(), 
							PreferenceConstants.getPreferenceStore(), null, 
							"Refactoring Changes to be made"));
					
					sourceViewer.setDocument(document, new AnnotationModel());
					sourceViewer.setEditable(false);
					sourceViewer.refresh();

//					@SuppressWarnings("restriction")
//					JavaTextTools tools = JavaPlugin.getDefault().getJavaTextTools();
//					tools.setupJavaDocumentPartitioner(document);
//
//					@SuppressWarnings("restriction")
//					JavaSourceViewerConfiguration config = new JavaSourceViewerConfiguration(tools.getColorManager(), JavaPlugin
//									.getDefault().getCombinedPreferenceStore(), null, null);
//
//					sourceViewer.configure(config);
					
					Object[] highlightPositionMaps = astSlice.getHighlightPositions();
					Map<Position, String> annotationMap = (Map<Position, String>)highlightPositionMaps[0];
					Map<Position, Boolean> duplicationMap = (Map<Position, Boolean>)highlightPositionMaps[1];
					AnnotationModel annotationModel = (AnnotationModel)JavaUI.getDocumentProvider().getAnnotationModel(sourceEditor.getEditorInput());
					Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
					while(annotationIterator.hasNext()) {
						Annotation currentAnnotation = annotationIterator.next();
						if(currentAnnotation.getType().equals(SliceAnnotation.EXTRACTION) || currentAnnotation.getType().equals(SliceAnnotation.DUPLICATION)) {
							annotationModel.removeAnnotation(currentAnnotation);
						}
					}
					for(Position position : annotationMap.keySet()) {
						SliceAnnotation annotation = null;
						String annotationText = annotationMap.get(position);
						boolean duplicated = duplicationMap.get(position);
						if(duplicated)
							annotation = new SliceAnnotation(SliceAnnotation.DUPLICATION, annotationText);
						else
							annotation = new SliceAnnotation(SliceAnnotation.EXTRACTION, annotationText);
						annotationModel.addAnnotation(annotation, position);
					}
					List<Position> positions = new ArrayList<Position>(annotationMap.keySet());
					Position firstPosition = positions.get(0);
					Position lastPosition = positions.get(positions.size()-1);
					int offset = firstPosition.getOffset();
					int length = lastPosition.getOffset() + lastPosition.getLength() - firstPosition.getOffset();
										
					//sourceEditor.setHighlightRange(offset, length, true);
					Color highlightColor=new Color(Display.getCurrent(),new RGB(111,33,152));
					
					sourceViewer.setTextColor(highlightColor, offset, length, true);
					sourceViewer.setSelectedRange(offset, length);
					sourceViewer.setRangeIndication(offset, length, true);
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (JavaModelException e) {
					e.printStackTrace();
				} 
				
				
				
			}
		}
	}
}
