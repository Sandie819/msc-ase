package msc.refactor.jcodecleaner.wizard.view.pages;

import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

public class PreviewSubPanel {

	private SourceViewer sourceViewer;
	private Group previewGroup;

	public PreviewSubPanel(Group previewGroup) {
		this.previewGroup = previewGroup;

	}

	public void addPreviewForClassRefactoring(MultipleRefactoring refactoring, IFile file) {		
		previewGroup.setText("Preview for: "+file.getName());
		previewGroup.setVisible(true);
		sourceViewer = new SourceViewer(previewGroup, null, SWT.V_SCROLL | SWT.H_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH);
		sourceViewer.getControl().setLayoutData(data);
		sourceViewer.getControl().setFont(new Font(Display.getDefault(),"Arial", 9, SWT.NONE));
		
		if (refactoring != null) {				
			try {
				IDocument document = new Document(refactoring.getfCompilationUnit().getSource());
				JavaTextTools textTools= JavaPlugin.getDefault().getJavaTextTools();
				IPreferenceStore store= JavaPlugin.getDefault().getCombinedPreferenceStore();

				JavaTextTools tools= JavaPlugin.getDefault().getJavaTextTools();
				tools.setupJavaDocumentPartitioner(document);

				sourceViewer.configure(new JavaSourceViewerConfiguration(
						tools.getColorManager(), 
						PreferenceConstants.getPreferenceStore(), null, 
						"Refactoring Changes to be made"));

				textTools.setupJavaDocumentPartitioner(document);

				sourceViewer.setDocument(document, new AnnotationModel());
				sourceViewer.setEditable(false);
				sourceViewer.refresh();

			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		previewGroup.layout();
	}
}