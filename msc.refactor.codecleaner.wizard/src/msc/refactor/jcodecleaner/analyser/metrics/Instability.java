package msc.refactor.jcodecleaner.analyser.metrics;

import org.eclipse.core.resources.IFile;

/**
 * Instability is the ratio between efferent coupling (Ce) and the total coupling (Ce + Ca)
 * 
 * @author mulligans
 *
 */
public class Instability extends Metric {
	
	private AfferentCoupling afferentCoupling;
	private EfferentCoupling efferentCoupling;
	
	public Instability() {
		super("Instability", "I", 5);
		
		afferentCoupling = new AfferentCoupling();
		efferentCoupling = new EfferentCoupling();		
	}

	@Override
	public double calculateMetricValue(IFile file) {
		double ca = afferentCoupling.calculateMetricValue(file);
		double ce = efferentCoupling.calculateMetricValue(file);
		
		double i = (ce/ (ce+ca));
		
		setMetricValue(i);
		return i;
	}

	
}
