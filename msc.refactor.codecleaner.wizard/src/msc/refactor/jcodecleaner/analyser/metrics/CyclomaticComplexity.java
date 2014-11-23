package msc.refactor.jcodecleaner.analyser.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * Cyclomatic Complexity (CC)
 * Software metric equal to the number of decisions that can be taken in a method
 * 
 * 1-10		Low risk program
 * 11-20	Moderate risk
 * 21-50	High risk
 * >50		Most complex and highly unstable method
 *
 * @author mulligans
 *
 */
public class CyclomaticComplexity extends Metric {

	public Integer NUM_METHODS = 0;
	public Integer COMPLEXITY = 1;

	public CyclomaticComplexity() {
		super("Cyclomatic Complexity", "CC", 2);
	}

	public double calculateMetricValue(IFile file) {
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
		CompilationUnit compilationUnit = parse(new NullProgressMonitor(), cu);

		compilationUnit.accept(new ASTVisitor() {

			@Override
			public boolean visit(final MethodDeclaration method) {
				
				System.out.println(method.getName());
				if (!method.isConstructor()) {
					NUM_METHODS++;
					
					method.accept(new ASTVisitor() {
						@Override
						public boolean visit(CatchClause catchNode) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has CatchClause");
							return true;
						}
						@Override
						public boolean visit(ConditionalExpression conditionalNode) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has ConditionalExpression");
							return true;
						}
						@Override
						public boolean visit(DoStatement node) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has DoStatement");
							return true;
						}
						@Override
						public boolean visit(EnhancedForStatement node) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has EnhancedForStatement");
							return true;
						}

						@Override
						public boolean visit(ForStatement node) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has ForStatement");
							return true;
						}
						@Override
						public boolean visit(IfStatement node) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has IfStatement");
							return true;
						}
						@Override
						public boolean visit(SwitchStatement node) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has SwitchStatement");
							return true;
						}

						@Override
						public boolean visit(WhileStatement node) {
							COMPLEXITY++;
							System.out.println(method.getName()+ " has WhileStatement");
							return true;
						}
						
//						@Override
//						public boolean visit(ReturnStatement node) {
//							COMPLEXITY++;
//							System.out.println(method.getName()+ " has ReturnStatement");
//							return true;
//						}

					});
				}

				return true;
			}

		});

		BigDecimal bComplexity = new BigDecimal(COMPLEXITY);
		BigDecimal bNumMethods = new BigDecimal(NUM_METHODS);
		BigDecimal averageComplexity = bComplexity.divide(bNumMethods, 3, RoundingMode.CEILING);
		setMetricValue(averageComplexity.doubleValue());

		return averageComplexity.doubleValue();
	}

	@Override
	public String toString() {
		return "CyclomaticComplexity [NUM_METHODS= "+NUM_METHODS+ ", COMPLEXITY="+COMPLEXITY
				+ ", threshold=" + threshold + ", metricFullName=" + metricFullName
				+ ", metricShortName=" + metricShortName + ", metricValue=" + metricValue + ", applicableRefactorings="
				+ applicableRefactorings + "]";
	}

}
