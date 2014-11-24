package msc.refactor.jcodecleaner.wizard.view.pages;

import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.jcodecleaner.multiplerefactoring.PreviewClassBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.changes.CreateCompilationUnitChange;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;


/**
 * Class for creating a snippet preview of the chosen refactoring
 * 
 * @author mulligans
 *
 */
@SuppressWarnings("restriction")
public class PreviewSubPanel {

	private SourceViewer sourceViewer;
	private Group previewGroup;
	private PreviewClassBuilder previewClassBuilder;

	public PreviewSubPanel(Group previewGroup) {
		this.previewGroup = previewGroup;
		
		previewClassBuilder = new PreviewClassBuilder();
	}

	/**
	 * @param extractMethodOpportunities
	 * @param file
	 */
	public void addPreviewForExtractMethodRefactoring(Set<ASTSliceGroup> extractMethodOpportunities, IFile file) {

		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);

		for (ASTSliceGroup astSliceGroup : extractMethodOpportunities) {

			for (ASTSlice astSlice : astSliceGroup.getCandidates()) {
				try {

					IDocument document = new Document(compilationUnit.getSource());
					createPreviewAndSource(" Extracting method from class: " + file.getName(), document);
					highlightSourceViewer(astSlice.getHighlightPositions());

				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}
		previewGroup.layout();
	}

	/**
	 * @param extractClassRefactoring
	 */
	public void addPreviewForExtractClassRefactoring(ExtractClassRefactoring extractClassRefactoring, 
			 IFile file, ExtractClassCandidateRefactoring candidate) {
		CreateCompilationUnitChange unit = previewClassBuilder.createExtractedClass(extractClassRefactoring, candidate, file);
		IDocument document = new Document(unit.getPreview());
		createPreviewAndSource("New class "+extractClassRefactoring.getExtractedTypeName(), document);

		previewGroup.layout();
	}
	
	/**
	 * @param moveMethodCandidates
	 */
	public void addPreviewForMoveMethodRefactoring(List<MoveMethodCandidateRefactoring> moveMethodCandidates) {
		for(MoveMethodCandidateRefactoring candidateRefactoring: moveMethodCandidates) {
			IDocument document = new Document(candidateRefactoring.getSourceMethodDeclaration().toString());
			
			String title = "Moving method " +candidateRefactoring.getMovedMethodName() +" to class " +
					candidateRefactoring.getTargetIFile().getName();
			
			createPreviewAndSource(title, document);
			previewGroup.layout();			
		}
	}

	/**
	 * @param title
	 * @param document
	 */
	private void createPreviewAndSource(String title, IDocument document) {
		clearPreviewGroup();

		previewGroup.setText("Preview for: " + title);
		previewGroup.setVisible(true);
		sourceViewer = new SourceViewer(previewGroup, null, SWT.V_SCROLL | SWT.H_SCROLL);

		GridData data = new GridData(GridData.FILL_BOTH);
		sourceViewer.getControl().setLayoutData(data);
		sourceViewer.getControl().setFont(new Font(Display.getDefault(), "Arial", 9, SWT.NONE));

		JavaTextTools tools = JavaPlugin.getDefault().getJavaTextTools();
		tools.setupJavaDocumentPartitioner(document);

		sourceViewer.configure(new JavaSourceViewerConfiguration(tools.getColorManager(), PreferenceConstants
				.getPreferenceStore(), null, "Refactoring Changes to be made"));

		sourceViewer.setDocument(document, new AnnotationModel());
		sourceViewer.setEditable(false);
		sourceViewer.refresh();

	}
	
	/**
	 * @param highlightPositionMaps
	 */
	public void highlightSourceViewer(Object[] highlightPositionMaps){
		@SuppressWarnings("unchecked")
		Map<Position, String> annotationMap = (Map<Position, String>) highlightPositionMaps[0];

		List<Position> positions = new ArrayList<Position>(annotationMap.keySet());
		Position firstPosition = positions.get(0);
		Position lastPosition = positions.get(positions.size() - 1);
		int offset = firstPosition.getOffset();
		int length = lastPosition.getOffset() + lastPosition.getLength() - firstPosition.getOffset();

		sourceViewer.setTextColor(Display.getDefault().getSystemColor(SWT.COLOR_RED), offset, length, true);
		sourceViewer.setSelectedRange(offset, length);
		sourceViewer.setRangeIndication(offset, length, true);
	}

	/**
	 * Clear down the preview group
	 */
	private void clearPreviewGroup() {
		for (Control widget : previewGroup.getChildren()) {
			if (!widget.isDisposed()) {
				widget.dispose();
			}
		}
		previewGroup.layout();
	}
}