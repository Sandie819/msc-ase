package msc.refactor.jcodecleaner.wizard.view.pages;

import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class PageListener implements ModifyListener {

	private WizardController controller;

	public PageListener(WizardController controller) {
		this.controller = controller;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		
		Text text = (Text) event.widget;
		System.out.println(text.getText());
	}

}
