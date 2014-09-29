package msc.refactor.codecleaner.metrics.cohesion;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class CalculateCohesionMetrics {

	private LCOM lcom;

	public void calculate(IFile file) throws FileNotFoundException {
		lcom = new LCOM();
		String fileName = file.getLocation().toOSString();
		FileInputStream in = new FileInputStream(fileName);
		CompilationUnit cu = null;
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		try {
			// parse the file
			cu = JavaParser.parse(in);
			//lcom.calculate(cu);
			
			lcom.measure(compilationUnit);

		} catch (ParseException | JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	private void createAST(ICompilationUnit unit) {
		// now create the AST for the ICompilationUnits
		org.eclipse.jdt.core.dom.CompilationUnit parse = parse(unit);
		MethodVisitor visitor = new MethodVisitor();
		parse.accept(visitor);

//		parse.accept(new ASTVisitor() {
//		        @Override
//		        public boolean visit(VariableDeclarationFragment node) {
//		            IJavaElement element = node.resolveBinding().getJavaElement();
//		            if (field.equals(element)) {
//		                FieldDeclaration fieldDeclaration = (FieldDeclaration)node.getParent();
//		                IType fieldType = (IType)fieldDeclaration.getType().resolveBinding().getJavaElement();
//		            }
//		            return false;
//		        }
//		    });
		
		for (MethodDeclaration method : visitor.getMethods()) {
			
			SearchPattern pattern = SearchPattern.createPattern("testA",
					IJavaSearchConstants.TYPE, IJavaSearchConstants.REFERENCES,
					SearchPattern.R_EXACT_MATCH);
			
			// step 2: Create search scope
			IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		 
			// step3: define a result collector
			SearchRequestor requestor = new SearchRequestor() {
				public void acceptSearchMatch(SearchMatch match) {
					System.out.println(match.getElement());
				}
			};
		 
			// step4: start searching
			SearchEngine searchEngine = new SearchEngine();
			try {
				searchEngine.search(pattern, new SearchParticipant[] { SearchEngine
								.getDefaultSearchParticipant() }, scope, requestor,
								null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
//			method.accept(new ASTVisitor() {
//		        @Override
//		        public boolean visit(VariableDeclarationFragment node) {
//		            IJavaElement element = node.resolveBinding().getJavaElement();
//		          //  if (field.equals(element)) {
//		                FieldDeclaration fieldDeclaration = (FieldDeclaration)node.getParent();
//		                IType fieldType = (IType)fieldDeclaration.getType().resolveBinding().getJavaElement();
//		         //   }
//		            return false;
//		        }
//		    });
			
			System.out.print("Method name: " + method.getName()
					+ " Return type: " + method.getReturnType2());
		}

	}


	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 * 
	 * @param unit
	 * @return
	 */


	private static org.eclipse.jdt.core.dom.CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null); // parse
	}

	public void calculate(ICompilationUnit compilationUnit){
		lcom = new LCOM();
	//	try {
			createAST(compilationUnit);
			//lcom.measure(compilationUnit);
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
