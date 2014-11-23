package msc.refactor.jcodecleaner.analyser.metrics;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

/**
 * Afferent Coupling (Ca) 
 * The number of code elements that use it
 * Good indicator of how changes to class would influence other parts of the software.
 * @author mulligans
 *
 */
public class AfferentCoupling extends Metric {

	private Integer numberOfReferences = 0;

	public AfferentCoupling() {
		super("Afferent Coupling", "Ca", 5);
	}

	@Override
	public double calculateMetricValue(IFile file) {
		
		findClassReferences(file);
		
		setMetricValue(numberOfReferences);
		return numberOfReferences;
	}

	/**
	 * Java search pattern
	 * 
	 * @param method
	 * @param field
	 * @return
	 */
	private void findClassReferences(IFile file) {
		ICompilationUnit complilationUnit = JavaCore.createCompilationUnitFrom(file);
		
		String classNamePattern = file.getName().replace(file.getFileExtension(), "");

		SearchPattern pattern = SearchPattern.createPattern(classNamePattern.replace(".", ""), 
				IJavaSearchConstants.TYPE,
				IJavaSearchConstants.REFERENCES, 
				SearchPattern.R_PATTERN_MATCH);
		
		SearchEngine searchEngine = new SearchEngine();
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope((IJavaElement[]) new IJavaProject[] { complilationUnit.getJavaProject() });
		
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				IJavaElement e = (IJavaElement) match.getElement();
				String elementName = e.getElementName();
				if(e!=null) {
					numberOfReferences++;
					System.out.print("Found class in"+ elementName);
				}
			}
		};
		try {
			searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
					requestor, null);
		} catch (CoreException e) {
		}
	}
}
