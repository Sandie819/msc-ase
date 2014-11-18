package msc.refactor.jcodecleaner.wizard.view.pages;

import msc.refactor.jcodecleaner.multiplerefactoring.MultipleRefactoring;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CreateChangeOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class PreviewSubPanel {

	private CreateTextFilePreviewer fPane;
	private SourceViewer fSourceViewer;
	private Group previewGroup;

	public PreviewSubPanel(Group previewGroup) {
		this.previewGroup = previewGroup;
		
	}

	public void addPreviewForClassRefactoring(final MultipleRefactoring refactoring) {
		
		previewGroup.setText("Preview");
		Label classNameLabel = new Label(previewGroup, SWT.NONE);
		classNameLabel.setText("Test");
		//fPane= new CreateTextFilePreviewer(previewGroup, SWT.BORDER | SWT.FLAT);

		fSourceViewer = new SourceViewer(previewGroup, null, SWT.V_SCROLL | SWT.H_SCROLL);
		//fSourceViewer.getControl().setFont(JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT));
		//fPane.setContent(fSourceViewer.getControl());
		GridData data = new GridData(GridData.FILL_BOTH);
		fSourceViewer.getControl().setLayoutData(data);
		
		if (refactoring != null) {
			IProgressMonitor monitor = new NullProgressMonitor();
				
				
				try {
					IDocument document = new Document(refactoring.getfCompilationUnit().getSource());
					JavaTextTools textTools= JavaPlugin.getDefault().getJavaTextTools();
					IPreferenceStore store= JavaPlugin.getDefault().getCombinedPreferenceStore();
				
					JavaTextTools tools= JavaPlugin.getDefault().getJavaTextTools();
					tools.setupJavaDocumentPartitioner(document);
					
					fSourceViewer.configure(new JavaSourceViewerConfiguration(
							tools.getColorManager(), 
							PreferenceConstants.getPreferenceStore(), null, 
							"Refactoring Changes to be made"));
					
						textTools.setupJavaDocumentPartitioner(document);
						//fSourceViewer.configure(new JavaSourceViewerConfiguration(textTools.getColorManager(), store, null, null));
						//fSourceViewer.getTextWidget().setOrientation(SWT.LEFT_TO_RIGHT);
						
						fSourceViewer.setDocument(document, new AnnotationModel());
						fSourceViewer.setEditable(false);
						fSourceViewer.refresh();
//					fSourceViewer.setInput(document);
					
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				String textType= textFileChange.getTextType();
				
		}
		previewGroup.layout();
	}

	private Change createChange(final Refactoring refactoring, final IProgressMonitor monitor)
			{
		Assert.isNotNull(refactoring);
		Assert.isNotNull(monitor);
		final CreateChangeOperation operation = new CreateChangeOperation(refactoring);
		try {
			operation.run(monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return operation.getChange();
	}

	private static class CreateTextFilePreviewer extends org.eclipse.jdt.internal.ui.util.ViewerPane {

		private ImageDescriptor fDescriptor;

		private Image fImage;

		public CreateTextFilePreviewer(Composite parent, int style) {
			super(parent, style);
			addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					disposeImage();
				}
			});
		}

		/*package*/ void disposeImage() {
			if (fImage != null) {
				fImage.dispose();
			}
		}

		public void setImageDescriptor(ImageDescriptor imageDescriptor) {
			fDescriptor= imageDescriptor;
		}

		@Override
		public void setText(String text) {
			super.setText(text);
			Image current= null;
			if (fDescriptor != null) {
				current= fImage;
				fImage= fDescriptor.createImage();
			} else {
				current= fImage;
				fImage= null;
			}
			setImage(fImage);
			if (current != null) {
				current.dispose();
			}
		}

	}
}
