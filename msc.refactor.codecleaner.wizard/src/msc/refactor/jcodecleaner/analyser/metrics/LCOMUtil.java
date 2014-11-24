/*
 *
 * Copyright 2008 Krzysztof Dębski
 * This program is distributed under the terms of the GNU General Public License 3
 * 
 */

package msc.refactor.jcodecleaner.analyser.metrics;

/**
 * Taken & modified from Simple Metrics
 * Modified to work with IMethod 
 */
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class LCOMUtil {

	public static Collection<Set<IMethod>> computeMethodsSet(Collection<IMethod> methods) {
		
		FindAndUnion<IMethod> fAndU = new FindAndUnion<IMethod>(methods);

		try {
			for(IMethod m1: methods){
				//m1.getSource().
				Set<String> v1 = getStringSet(m1.getParameterNames());
				for(IMethod m2: methods){
					if (!m1.getElementName().equals(m2.getElementName())){
						Set<String> v2 = getStringSet(m2.getParameterNames());
						if (!Collections.disjoint(v1, v2)) 
							fAndU.union(m1, m2);
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fAndU.getSet();
	}

	private static Set<String> getStringSet(String[] parameterNames) {
		Set<String> variables = new HashSet<String>();
		for(int i = 0; i<parameterNames.length; i++) {
			variables.add(parameterNames[i]);
		}
		return variables;
	}

//	public static int computeLCOM1(Collection<Set<IMethod>> sets) {
//		int p = 0, q = 0;
//
//		int allMethodsCount = 0;
//		for(Set<IMethod> set: sets){
//			allMethodsCount += set.size();                   
//		}        
//
//		for(Set<IMethod> set: sets){
//			int setSize = set.size();
//			assert (setSize != 0);
//			int pairs = setSize * (setSize - 1);            
//
//			q += pairs;
//
//			p += (allMethodsCount - setSize) * setSize;           
//		}
//
//		int t = p - q;
//
//		t /= 2;
//		return (t > 0 ? t : 0);
//
//	}

//	public static int computeLCOM4(Collection<Set<IMethod>> sets) {       
//		return sets.size();
//	}

//	public static double computeLCOM2(int mets, int vars, int variablesUsedInMethodsCount) {
//		if (mets == 0 || vars == 0) return 0.;
//		return 1. - (double)variablesUsedInMethodsCount / ((double)(mets * vars));        
//	}

//	public static double computeLCOM3(int mets, int vars, int variablesUsedInMethodsCount) {
//		if (mets < 2 || vars == 0) return 0.;
//		return ((double)mets - ((double)variablesUsedInMethodsCount / (double)vars)) / ((double)mets - 1.);        
//	}   
}
