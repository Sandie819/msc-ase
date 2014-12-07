package msc.refactor.jcodecleaner.wizard.view.pages.runnable;

import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractMethodRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.MoveMethodRefactoring;
import msc.refactor.jcodecleaner.wizard.controller.WizardController;

import org.eclipse.ltk.core.refactoring.Refactoring;

public class RunnableCreatorFactory {
	
	public static RunnableCreator getInstance(Refactoring refactoring,
			WizardController controller){
		
		if(refactoring instanceof ExtractClassRefactoring) {
			return new RunnableExtractClassCreator(controller);			
		} else if(refactoring instanceof ExtractMethodRefactoring) {
			return new RunnableExtractMethodCreator(controller);
		} else if(refactoring instanceof MoveMethodRefactoring) {
			return new RunnableMoveMethodCreator(controller);
		} else {
			return null;
		}
		
	}
}
