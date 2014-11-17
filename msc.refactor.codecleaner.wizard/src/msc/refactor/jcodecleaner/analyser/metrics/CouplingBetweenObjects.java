package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.List;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;

public class CouplingBetweenObjects extends Metric {

	public CouplingBetweenObjects() {
		super("Coupling Between Classes", "CBO", 3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateMetricValue(IFile file) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<RefactoringEnum> getApplicableMetricRefactorings() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
