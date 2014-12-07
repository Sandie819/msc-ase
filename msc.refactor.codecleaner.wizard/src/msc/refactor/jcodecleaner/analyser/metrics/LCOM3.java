package msc.refactor.jcodecleaner.analyser.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class LCOM3 extends Metric  {

	private Set<IMethod> methods = new HashSet<IMethod>();
	private Set<IField> fields = new HashSet<IField>();
	private Map<IField, Integer> variable_map_counter;

	private static Boolean METHOD_HAS_REF = null;

	public LCOM3() {
		super("Lack of Cohesion of Methods (3)", "LCOM3", 0);
		variable_map_counter = new HashMap<IField, Integer>();
	}
	
	public double calculateMetricValue(IFile file, IProgressMonitor monitor) {
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		try {
			buildInstanceFields(compilationUnit);
			buildMethodsInClass(compilationUnit);
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		findReferenceToInstanceFields();

		int m = methods.size();
		int f = fields.size();
		double mf = 0.0;

		for (int i : variable_map_counter.values()) {
			mf += i;
		}

		double sumMF = mf/variable_map_counter.size();

		double lcom3 = 0.0;
		if(f > 0 && m > 1) {
			//LCOM3 = (m - sum(mA)/a) / (m-1)
			// (1 to 2 good; 0 bad, split)
			lcom3 = ((double)m - ((double)sumMF/(double)f)) / ((double)m - 1.0);
			//System.out.println("lcom3:" + lcom3);
		}

		BigDecimal lcomBigDecimal = new BigDecimal(lcom3).setScale(3, RoundingMode.CEILING);
	
		setMetricValue(lcomBigDecimal.doubleValue());
		return lcom3;
	}

	/**
	 * @param compilationUnit
	 * @throws JavaModelException
	 */
	private void buildMethodsInClass(ICompilationUnit compilationUnit) throws JavaModelException {
		IType types[] = compilationUnit.getTypes();
		for (int i = 0; i < types.length; i++) {
			IType type = types[i];
			IMethod[] methodsArray = type.getMethods();
			for (int j = 0; j < methodsArray.length; j++) {
				IMethod method = methodsArray[j];
				
				
				methods.add(method);
			}
		}
	}

	/**
	 * @param compilationUnit
	 * @throws JavaModelException
	 */
	private void buildInstanceFields(ICompilationUnit compilationUnit) throws JavaModelException {
		IType types[] = compilationUnit.getTypes();
		for (int i = 0; i < types.length; i++) {
			IType type = types[i];
			IField[] fieldsArray = type.getFields();
			for (int j = 0; j < fieldsArray.length; j++) {
				IField field = fieldsArray[j];
				fields.add(field);
				variable_map_counter.put(field, 0);
			}
		}
	}

	/**
	 * Find references to instance fields within the methods
	 * of the class
	 */
	public void findReferenceToInstanceFields() {

		for (IField field : fields) {
			for (IMethod method: methods) {
				if(findSearchPattern(method, field)) {
					variable_map_counter.put(field, variable_map_counter.get(field) + 1);
				} 
			}
		}
	}

	/**
	 * Java search pattern
	 * @param method
	 * @param field
	 * @return
	 */
	private static boolean findSearchPattern(IMethod method, IField field) {
		METHOD_HAS_REF = false;
		final String attributeName = field.getElementName();
		try {			
			SearchPattern pattern = SearchPattern.createPattern(attributeName, 
					IJavaSearchConstants.FIELD,
					IJavaSearchConstants.REFERENCES, SearchPattern.R_PATTERN_MATCH);

			IJavaElement javaMethod[] = {method};
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(javaMethod);

			SearchRequestor requestor = new SearchRequestor() {
				@Override
				public void acceptSearchMatch(SearchMatch match) {
					METHOD_HAS_REF = true;
				}
			};

			SearchEngine searchEngine = new SearchEngine();
			SearchParticipant[] searchParticipants = new SearchParticipant[] { SearchEngine
					.getDefaultSearchParticipant() };
			searchEngine.search(pattern, searchParticipants, scope, requestor, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return METHOD_HAS_REF;
	}

	@Override
	public List<RefactoringEnum> getApplicableMetricRefactorings() {		
		applicableRefactorings = new ArrayList<RefactoringEnum>();
		
		applicableRefactorings.add(RefactoringEnum.EXTRACT_CLASS);
		applicableRefactorings.add(RefactoringEnum.EXTRACT_METHOD);
		applicableRefactorings.add(RefactoringEnum.MOVE_METHOD);
		
		return applicableRefactorings;
	}

}