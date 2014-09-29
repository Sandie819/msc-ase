package msc.refactor.codecleaner.metrics.cohesion;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.VariableDeclaratorId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class LCOM  {

	private static int NUMBER_OF_METHODS;
	private static Map<VariableDeclaratorId, Integer> INSTANCE_VARIABLE_COUNT;
	//private final static Map<IMethod, VariableDeclaratorId> MATCHES_FOUND = new HashMap<IMethod, VariableDeclaratorId>();
	//private static Set<MethodDeclaration> methods = new HashSet<MethodDeclaration>();
	
	private Set<IMethod> methods = new HashSet<IMethod>();
	private Set<IField> fields = new HashSet<IField>();
	private static Map<IField, Integer> INSTANCE_VARIABLE_COUNTER;
	
	private static Boolean METHOD_HAS_REF = null;

	public LCOM() {
		//NUMBER_OF_METHODS = 0;
		INSTANCE_VARIABLE_COUNT = new HashMap<VariableDeclaratorId, Integer>();
		INSTANCE_VARIABLE_COUNTER = new HashMap<IField, Integer>();
	}

	/*
	 * (non-Javadoc)
	 * @see truerefactor.metrics.Metric#measure()
	 */
	public double measure(ICompilationUnit compilationUnit) throws JavaModelException
	{		
		buildInstanceFields(compilationUnit);
		buildMethodsInClass(compilationUnit);
		
		// The number of methods that access one or more of the same attributes
        // Low LCOM = high cohesion = Good
        // measure by looking at the connections within a class

        double tLCOM = 0; // the total LCOM of the hierarchy
        double mLCOM = 0; // the mean LCOM

        tLCOM += calculateAverageLCOM(compilationUnit);
     
        mLCOM = (double) (tLCOM / 1);

        System.out.println("mLCOM:" +mLCOM);
        /////////////////////////////////////////////////////////////////////////////////
        
        //LCOM = 1 – (sum(MF)/M*F)
		
        int m = methods.size();
        int f = fields.size();
        int mf = 0;

        for (int i : INSTANCE_VARIABLE_COUNTER.values()) {
        	mf += i;
        }

        int sumMF = mf/INSTANCE_VARIABLE_COUNTER.size();

        //(0 is good)
        //LCOM2 = 1 - sum(mf)/(m*f)
        int LCOM2 = (1- sumMF/ m*f);
        
        //LCOM3 = (m - sum(mA)/a) / (m-1)
        // (1 to 2 good; 0 bad, split)
        int LCOM3 = (m - sumMF/f)/(m-1);
        
        System.out.println("LCOM2:" + LCOM2);
		System.out.println("LCOM3:" + LCOM3);
		
        return mLCOM;

	}

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
	 * Measures the average LCOM 
	 * @throws JavaModelException 
	 */
	public double calculateAverageLCOM(ICompilationUnit compilationUnit) throws JavaModelException
	{

		double cLCOM = 0;
		int access = 0;
		int noaccess = 0;

		for (IField field : fields)
		{
			int count = 0;
			int nocount = 0;

			for (IMethod method: methods) {

				if(findSearchPattern(method, field)) {
					INSTANCE_VARIABLE_COUNTER.put(field, INSTANCE_VARIABLE_COUNTER.get(field) + 1);
					count++;
				} else {
					nocount++;
				}
			}
			access += count / 2;
			noaccess += nocount / 2;
		}
		
		cLCOM = noaccess - access;

		if (cLCOM < 0)
			return cLCOM = 0;

		return cLCOM;
	}

	private static boolean findSearchPattern(IMethod iMethod,
			IField field) {
		METHOD_HAS_REF = false;
		final String methodName = iMethod.getElementName();
		final String attributeName = field.getElementName();
		try {			
			SearchPattern pattern = SearchPattern.createPattern(attributeName, 
					IJavaSearchConstants.FIELD,
					IJavaSearchConstants.REFERENCES, SearchPattern.R_PATTERN_MATCH);

			IJavaElement javaMethod[] = {iMethod};
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

	/**
	 * 	LCOM = 1 – sum(MF) / M*F
	 * 	LCOM_HS = (M – sum(MF) / F) (M-1)
	 * 
	 * 		Where: 
	 * 				M is the number of methods (both static and instance methods are counted, as well as constructors);
	 * 				F is the number of instance variables;
	 * 				MF is the number of methods accessing a particular instance field;
	 * 				Sum(MF) is the sum of MF over all instance fields of the class.
	 * @param cu
	 */
	public void calculate(CompilationUnit cu) {
		//new FieldVisitor().visit(cu, null);
		//new MethodVisitor().visit(cu, null);
		
		System.out.println(INSTANCE_VARIABLE_COUNT);
		
		//LCOM = 1 – (sum(MF)/M*F)
		
		int m = NUMBER_OF_METHODS;
		int f = INSTANCE_VARIABLE_COUNT.size();
		int mf = 0;
		
		for (int i : INSTANCE_VARIABLE_COUNT.values()) {
			mf += i/f;
		}
		
		int sumMF = mf/INSTANCE_VARIABLE_COUNT.size();
		
		int LCOM = (1- sumMF/ m*f);
		int LCOM_HS = (m - sumMF/ f);
		
		System.out.println("LCOM:" + LCOM);
		System.out.println("LCOM_HS:" + LCOM_HS);
		
	}



//	/**
//	 * Simple visitor implementation for visiting MethodDeclaration nodes. 
//	 */
//	private static class MethodVisitor extends VoidVisitorAdapter<Object> {
//
//		@Override
//		public void visit(MethodDeclaration n, Object arg) {
//			NUMBER_OF_METHODS++;
//			System.out.println(n.getName());
//			methods.add(n);
//			for(IMethod method: MATCHES_FOUND.keySet()){
//				if(method.getElementName().equals(n.getName())) {
//					INSTANCE_VARIABLE_COUNT.put(MATCHES_FOUND.get(method), INSTANCE_VARIABLE_COUNT.get(MATCHES_FOUND.get(method)) + 1);
//				}
//
//			}
//		}
//	}
//
//	/**
//	 * Simple visitor implementation for visiting MethodDeclaration nodes. 
//	 */
//	private static class FieldVisitor extends VoidVisitorAdapter<Object> {
//
//		@Override
//		public void visit(FieldDeclaration n, Object arg) {
//			List<VariableDeclarator> variableDelarators =	n.getVariables();
//
//			for(VariableDeclarator variableDec: variableDelarators) {
//				INSTANCE_VARIABLE_COUNT.put(variableDec.getId(), 0);
//
//				final VariableDeclaratorId variableId = variableDec.getId();
//				final String variableName = variableDec.getId().getName();
//				try {
//					SearchPattern pattern = SearchPattern.createPattern(variableName, 
//							IJavaSearchConstants.FIELD,
//							IJavaSearchConstants.REFERENCES, SearchPattern.R_PATTERN_MATCH);
//
//					IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
//
//					SearchRequestor requestor = new SearchRequestor() {
//						@Override
//						public void acceptSearchMatch(SearchMatch match) {
//							IMethod method = (IMethod)match.getElement();
//							MATCHES_FOUND.put(method, variableId);
//							System.out.println("ID:" +variableId+" Found in method: " + method.getElementName());
//						}
//					};
//
//					SearchEngine searchEngine = new SearchEngine();
//					SearchParticipant[] searchParticipants = new SearchParticipant[] { SearchEngine
//							.getDefaultSearchParticipant() };
//					searchEngine.search(pattern, searchParticipants, scope, requestor, null);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//
//	}
//

//	/**
//	 * Simple visitor implementation for visiting MethodDeclaration nodes. 
//	 */
//	private static class VariableDeclarationExprVisitor extends VoidVisitorAdapter<Object> {
//
//		@Override
//		public void visit(VariableDeclarationExpr n, Object arg) {
//			n.getType().accept(this, arg);
//
//			VariableDeclaratorId key = (VariableDeclaratorId) arg;
//			List<VariableDeclarator> variables = n.getVars();
//
//			for (VariableDeclarator var: variables) {
//				var.accept(this, arg);
//				System.out.println("ID: "+key.getName() +" var: " +var.getId());
//				if (var.getId().equals(key)) {
//					INSTANCE_VARIABLE_COUNT.put(key, INSTANCE_VARIABLE_COUNT.get(key) + 1);
//					System.out.println("Match found, ID: "+key.getName() +" times: " +INSTANCE_VARIABLE_COUNT.get(key));
//				}
//			}
//
//		}
//
//	}
	

	//	/**
	//	 * Simple visitor implementation for visiting MethodDeclaration nodes. 
	//	 */
	//	private static class StatementVisitor extends VoidVisitorAdapter<Object> {
	//
	//		@Override
	//		public void visit(BlockStmt blockStatement, Object arg) {
	//			VariableDeclaratorId id = (VariableDeclaratorId) arg;			
	//		}
	//
	//	}

}