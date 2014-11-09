package msc.refactor.jcodecleaner.analyser.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msc.refactor.jcodecleaner.enums.RefactoringEnum;

import org.eclipse.core.resources.IFile;
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

public class LcomMetric extends Metric  {

	private Set<IMethod> methods = new HashSet<IMethod>();
	private Set<IField> fields = new HashSet<IField>();
	private static Map<IField, Integer> INSTANCE_VARIABLE_COUNTER;


	private static Boolean METHOD_HAS_REF = null;

	public LcomMetric() {
		super("Lack of Cohesion of Methods", "LCOM", 0);
		INSTANCE_VARIABLE_COUNTER = new HashMap<IField, Integer>();
	}

	public double calculateMetricValue(IFile file) {
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

		for (int i : INSTANCE_VARIABLE_COUNTER.values()) {
			mf += i;
		}

		double sumMF = mf/INSTANCE_VARIABLE_COUNTER.size();

		double lcom2 = 0.0;
		if(f > 0 && m > 0) {
			//(0 is good)
			//LCOM2 = 1 - sum(mf)/(m*f)
			lcom2 = 1.0 - (double)sumMF / (double)(m*f);
			System.out.println("lcom2:" + lcom2);			
		}
		if(f > 0 && m > 1) {
			//LCOM3 = (m - sum(mA)/a) / (m-1)
			// (1 to 2 good; 0 bad, split)
			double lcom3 = ((double)m - ((double)sumMF/(double)f)) / ((double)m - 1.0);
			System.out.println("lcom3:" + lcom3);
		}

		setMetricValue(lcom2);
		return lcom2;
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
				INSTANCE_VARIABLE_COUNTER.put(field, 0);
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
					INSTANCE_VARIABLE_COUNTER.put(field, INSTANCE_VARIABLE_COUNTER.get(field) + 1);
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
		final String methodName = method.getElementName();
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
					System.out.println("ID:" +attributeName+" Found in method: " + methodName);
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
		
		return applicableRefactorings;
	}

}