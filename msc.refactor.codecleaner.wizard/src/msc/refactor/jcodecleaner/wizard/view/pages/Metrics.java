package msc.refactor.jcodecleaner.wizard.view.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import swing2swt.layout.FlowLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import swing2swt.layout.BoxLayout;

public class Metrics extends Composite {
	
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text;
	private Group refactoringOptionsGroup;
	private Label lblNewLabel;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Metrics(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		refactoringOptionsGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		refactoringOptionsGroup.setText("Refactoring Options Available");
		refactoringOptionsGroup.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		refactoringOptionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group metricResultsGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		metricResultsGroup.setText("Metric Results");
		metricResultsGroup.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		metricResultsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		Label lblLcom = new Label(metricResultsGroup, SWT.NONE);
	
		lblLcom.setText("LCOM:");
		
		text_1 = new Text(metricResultsGroup, SWT.BORDER);
	
		
		Label lblM = new Label(metricResultsGroup, SWT.NONE);

		lblM.setText("M2");
		
		text_2 = new Text(metricResultsGroup, SWT.BORDER);

		
		Label lblM_1 = new Label(metricResultsGroup, SWT.NONE);
		lblM_1.setText("M3");
		
		text_3 = new Text(metricResultsGroup, SWT.BORDER);
		
		Label lblM_2 = new Label(metricResultsGroup, SWT.NONE);
		lblM_2.setText("M4");
		
		text_4 = new Text(metricResultsGroup, SWT.BORDER);
		
		Group fitnessFunctionGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		fitnessFunctionGroup.setText("Overall Fitness Function");
		fitnessFunctionGroup.setLayout(new BoxLayout(BoxLayout.X_AXIS));
		fitnessFunctionGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lblNewLabel = new Label(fitnessFunctionGroup, SWT.NONE);
		lblNewLabel.setText("New Label");
		
		
		text = new Text(fitnessFunctionGroup, SWT.BORDER);
	

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
