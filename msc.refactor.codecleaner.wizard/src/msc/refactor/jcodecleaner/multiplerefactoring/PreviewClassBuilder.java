package msc.refactor.jcodecleaner.multiplerefactoring;

import gr.uom.java.ast.util.ExpressionExtractor;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.corext.refactoring.changes.CreateCompilationUnitChange;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

/**
 * Extracted from gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring
 * Builds a CreateCompilationUnitChange to be used as a preview (not to actual apply any changes)
 *
 */
public class PreviewClassBuilder {

	@SuppressWarnings({ "restriction", "unchecked" })
	public CreateCompilationUnitChange createExtractedClass(ExtractClassRefactoring extractClassRefactoring, 
			ExtractClassCandidateRefactoring candidate, IFile file) {
		IContainer contextContainer = (IContainer)file.getParent();
		IFile extractedClassFile = null;
		IFolder contextFolder = (IFolder)contextContainer;
		extractedClassFile = contextFolder.getFile(extractClassRefactoring.getExtractedTypeName() + ".java");

		ICompilationUnit extractedClassICompilationUnit = JavaCore.createCompilationUnitFrom(extractedClassFile);
		
		ASTParser extractedClassParser = ASTParser.newParser(AST.JLS4);
		extractedClassParser.setKind(ASTParser.K_COMPILATION_UNIT);
		Document extractedClassDocument = new Document();
		extractedClassParser.setSource(extractedClassDocument.get().toCharArray());
		
		CompilationUnit extractedClassCompilationUnit = (CompilationUnit)extractedClassParser.createAST(null);
        AST extractedClassAST = extractedClassCompilationUnit.getAST();
        ASTRewrite extractedClassRewriter = ASTRewrite.create(extractedClassAST);
        ListRewrite extractedClassTypesRewrite = extractedClassRewriter.getListRewrite(extractedClassCompilationUnit, CompilationUnit.TYPES_PROPERTY);

        if(extractClassRefactoring.getSourceCompilationUnit().getPackage() != null) {
        	extractedClassRewriter.set(extractedClassCompilationUnit, CompilationUnit.PACKAGE_PROPERTY, extractClassRefactoring.getSourceCompilationUnit().getPackage(), null);
        }
        TypeDeclaration extractedClassTypeDeclaration = extractedClassAST.newTypeDeclaration();
        SimpleName extractedClassName = extractedClassAST.newSimpleName(extractClassRefactoring.getExtractedTypeName() );
        extractedClassRewriter.set(extractedClassTypeDeclaration, TypeDeclaration.NAME_PROPERTY, extractedClassName, null);
        ListRewrite extractedClassModifiersRewrite = extractedClassRewriter.getListRewrite(extractedClassTypeDeclaration, TypeDeclaration.MODIFIERS2_PROPERTY);
        extractedClassModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);

        ListRewrite extractedClassBodyRewrite = extractedClassRewriter.getListRewrite(extractedClassTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
        ExpressionExtractor expressionExtractor = new ExpressionExtractor();
        Set<VariableDeclaration> finalFieldFragments = new LinkedHashSet<VariableDeclaration>();
        Set<VariableDeclaration> finalFieldFragmentsWithoutInitializer = new LinkedHashSet<VariableDeclaration>();
        for(VariableDeclaration fieldFragment : candidate.getExtractedFieldFragments()) {
        	List<Expression> initializerThisExpressions = expressionExtractor.getThisExpressions(fieldFragment.getInitializer());
        	FieldDeclaration extractedFieldDeclaration = null;
        	if(initializerThisExpressions.isEmpty()) {
        		extractedFieldDeclaration = extractedClassAST.newFieldDeclaration((VariableDeclarationFragment)ASTNode.copySubtree(extractedClassAST, fieldFragment));
        	}
        	else {
        		//this.extractedFieldsWithThisExpressionInTheirInitializer.add(fieldFragment);
        		VariableDeclarationFragment fragment = extractedClassAST.newVariableDeclarationFragment();
        		extractedClassRewriter.set(fragment, VariableDeclarationFragment.NAME_PROPERTY, extractedClassAST.newSimpleName(fieldFragment.getName().getIdentifier()), null);
        		extractedFieldDeclaration = extractedClassAST.newFieldDeclaration(fragment);
        	}
        	FieldDeclaration originalFieldDeclaration = (FieldDeclaration)fieldFragment.getParent();
        	extractedClassRewriter.set(extractedFieldDeclaration, FieldDeclaration.TYPE_PROPERTY, originalFieldDeclaration.getType(), null);
    		ListRewrite extractedFieldDeclarationModifiersRewrite = extractedClassRewriter.getListRewrite(extractedFieldDeclaration, FieldDeclaration.MODIFIERS2_PROPERTY);
    		extractedFieldDeclarationModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD), null);
    		List<IExtendedModifier> originalModifiers = originalFieldDeclaration.modifiers();
    		for(IExtendedModifier extendedModifier : originalModifiers) {
    			if(extendedModifier.isModifier()) {
    				Modifier modifier = (Modifier)extendedModifier;
    				if(modifier.isFinal()) {
    					extractedFieldDeclarationModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD), null);
    					finalFieldFragments.add(fieldFragment);
    					if(fieldFragment.getInitializer() == null)
    						finalFieldFragmentsWithoutInitializer.add(fieldFragment);
    				}
    				else if(modifier.isStatic()) {
    					extractedFieldDeclarationModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), null);
    				}
    				else if(modifier.isTransient()) {
    					extractedFieldDeclarationModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.TRANSIENT_KEYWORD), null);
    				}
    				else if(modifier.isVolatile()) {
    					extractedFieldDeclarationModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.VOLATILE_KEYWORD), null);
    				}
    			}
    		}
    		extractedClassBodyRewrite.insertLast(extractedFieldDeclaration, null);
        }
        
        for(VariableDeclaration fieldFragment : candidate.getExtractedFieldFragments()) {
        	MethodDeclaration getterMethodDeclaration = createGetterMethodDeclaration(fieldFragment, extractedClassAST, extractedClassRewriter);
        	extractedClassBodyRewrite.insertLast(getterMethodDeclaration, null);
        	if(!finalFieldFragments.contains(fieldFragment)) {
        		MethodDeclaration setterMethodDeclaration = createSetterMethodDeclaration(fieldFragment, extractedClassAST, extractedClassRewriter, extractClassRefactoring.getExtractedTypeName());
        		extractedClassBodyRewrite.insertLast(setterMethodDeclaration, null);
        	}
        }
        for(MethodDeclaration method : candidate.getExtractedMethods()) {
        	MethodDeclaration extractedMethodDeclaration = createExtractedMethodDeclaration(method, extractedClassAST, extractedClassRewriter);
        	extractedClassBodyRewrite.insertLast(extractedMethodDeclaration, null);
        }
        extractedClassTypesRewrite.insertLast(extractedClassTypeDeclaration, null);

        try {
        
        	TextEdit extractedClassEdit = extractedClassRewriter.rewriteAST(extractedClassDocument, null);
        	extractedClassEdit.apply(extractedClassDocument);
        	CreateCompilationUnitChange createCompilationUnitChange =
        		new CreateCompilationUnitChange(extractedClassICompilationUnit, 
        		extractedClassDocument.get(), extractedClassFile.getCharset());
        	
        	return createCompilationUnitChange;
        	
        } catch (CoreException e) {
        	e.printStackTrace();
        } catch (BadLocationException e) {
        	e.printStackTrace();
        }
		return null;
	}
	
	private MethodDeclaration createGetterMethodDeclaration(VariableDeclaration fieldFragment, AST extractedClassAST, ASTRewrite extractedClassRewriter) {
		String originalFieldName = fieldFragment.getName().getIdentifier();
		String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
		MethodDeclaration getterMethodDeclaration = extractedClassAST.newMethodDeclaration();
		extractedClassRewriter.set(getterMethodDeclaration, MethodDeclaration.NAME_PROPERTY, extractedClassAST.newSimpleName("get" + modifiedFieldName), null);
		FieldDeclaration originalFieldDeclaration = (FieldDeclaration)fieldFragment.getParent();
		extractedClassRewriter.set(getterMethodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, originalFieldDeclaration.getType(), null);
		ListRewrite getterMethodModifiersRewrite = extractedClassRewriter.getListRewrite(getterMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
		getterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
		if((originalFieldDeclaration.getModifiers() & Modifier.STATIC) != 0) {
			getterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), null);
		}
		ReturnStatement returnStatement = extractedClassAST.newReturnStatement();
		extractedClassRewriter.set(returnStatement, ReturnStatement.EXPRESSION_PROPERTY, fieldFragment.getName(), null);
		Block getterMethodBody = extractedClassAST.newBlock();
		ListRewrite getterMethodBodyRewrite = extractedClassRewriter.getListRewrite(getterMethodBody, Block.STATEMENTS_PROPERTY);
		getterMethodBodyRewrite.insertLast(returnStatement, null);
		extractedClassRewriter.set(getterMethodDeclaration, MethodDeclaration.BODY_PROPERTY, getterMethodBody, null);
		return getterMethodDeclaration;
	}
	
	private MethodDeclaration createSetterMethodDeclaration(VariableDeclaration fieldFragment, AST extractedClassAST, ASTRewrite extractedClassRewriter, String extractedTypeName) {
		String originalFieldName = fieldFragment.getName().getIdentifier();
		String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
		MethodDeclaration setterMethodDeclaration = extractedClassAST.newMethodDeclaration();
		extractedClassRewriter.set(setterMethodDeclaration, MethodDeclaration.NAME_PROPERTY, extractedClassAST.newSimpleName("set" + modifiedFieldName), null);
		PrimitiveType type = extractedClassAST.newPrimitiveType(PrimitiveType.VOID);
		extractedClassRewriter.set(setterMethodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, type, null);
		ListRewrite setterMethodModifiersRewrite = extractedClassRewriter.getListRewrite(setterMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
		setterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
		SingleVariableDeclaration parameter = extractedClassAST.newSingleVariableDeclaration();
		extractedClassRewriter.set(parameter, SingleVariableDeclaration.NAME_PROPERTY, fieldFragment.getName(), null);
		FieldDeclaration originalFieldDeclaration = (FieldDeclaration)fieldFragment.getParent();
		extractedClassRewriter.set(parameter, SingleVariableDeclaration.TYPE_PROPERTY, originalFieldDeclaration.getType(), null);
		ListRewrite setterMethodParametersRewrite = extractedClassRewriter.getListRewrite(setterMethodDeclaration, MethodDeclaration.PARAMETERS_PROPERTY);
		setterMethodParametersRewrite.insertLast(parameter, null);
		if((originalFieldDeclaration.getModifiers() & Modifier.STATIC) != 0) {
			setterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), null);
		}
		
		Assignment assignment = extractedClassAST.newAssignment();
		FieldAccess fieldAccess = extractedClassAST.newFieldAccess();
		if((originalFieldDeclaration.getModifiers() & Modifier.STATIC) != 0) {
			extractedClassRewriter.set(fieldAccess, FieldAccess.EXPRESSION_PROPERTY, extractedClassAST.newSimpleName(extractedTypeName), null);
		}
		else {
			ThisExpression thisExpression = extractedClassAST.newThisExpression();
			extractedClassRewriter.set(fieldAccess, FieldAccess.EXPRESSION_PROPERTY, thisExpression, null);
		}
		extractedClassRewriter.set(fieldAccess, FieldAccess.NAME_PROPERTY, fieldFragment.getName(), null);
		extractedClassRewriter.set(assignment, Assignment.LEFT_HAND_SIDE_PROPERTY, fieldAccess, null);
		extractedClassRewriter.set(assignment, Assignment.OPERATOR_PROPERTY, Assignment.Operator.ASSIGN, null);
		extractedClassRewriter.set(assignment, Assignment.RIGHT_HAND_SIDE_PROPERTY, fieldFragment.getName(), null);
		ExpressionStatement expressionStatement = extractedClassAST.newExpressionStatement(assignment);
		Block setterMethodBody = extractedClassAST.newBlock();
		ListRewrite setterMethodBodyRewrite = extractedClassRewriter.getListRewrite(setterMethodBody, Block.STATEMENTS_PROPERTY);
		setterMethodBodyRewrite.insertLast(expressionStatement, null);
		extractedClassRewriter.set(setterMethodDeclaration, MethodDeclaration.BODY_PROPERTY, setterMethodBody, null);
		return setterMethodDeclaration;
	}

	@SuppressWarnings("unchecked")
	private MethodDeclaration createExtractedMethodDeclaration(MethodDeclaration extractedMethod, AST extractedClassAST, ASTRewrite extractedClassRewriter) {
		MethodDeclaration newMethodDeclaration = (MethodDeclaration)ASTNode.copySubtree(extractedClassAST, extractedMethod);
		
		extractedClassRewriter.set(newMethodDeclaration, MethodDeclaration.NAME_PROPERTY, 
				extractedClassAST.newSimpleName(extractedMethod.getName().getIdentifier()), null);
		ListRewrite modifierRewrite = extractedClassRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
		Modifier publicModifier = newMethodDeclaration.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		boolean modifierFound = false;
		List<IExtendedModifier> modifiers = newMethodDeclaration.modifiers();
		for(IExtendedModifier extendedModifier : modifiers) {
			if(extendedModifier.isModifier()) {
				Modifier modifier = (Modifier)extendedModifier;
				if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PUBLIC_KEYWORD)) {
					modifierFound = true;
				}
				else if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PRIVATE_KEYWORD) ||
						modifier.getKeyword().equals(Modifier.ModifierKeyword.PROTECTED_KEYWORD)) {
					modifierFound = true;
					modifierRewrite.replace(modifier, publicModifier, null);
				}
			}
		}
		if(!modifierFound) {
			modifierRewrite.insertFirst(publicModifier, null);
		}
		return newMethodDeclaration;
	}
}
