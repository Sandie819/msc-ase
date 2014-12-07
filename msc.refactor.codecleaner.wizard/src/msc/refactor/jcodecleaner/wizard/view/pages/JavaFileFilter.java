package msc.refactor.jcodecleaner.wizard.view.pages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author mulligans
 *
 */
public class JavaFileFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
    	
        if(element instanceof IFile) {
        	IFile file = (IFile)element;
        	
        	if(file!=null && file.getFileExtension().equalsIgnoreCase("java")) {
        		return true;
        	}
            return false;    
        } else if (element instanceof IContainer) { 			
			
			if (element instanceof IProject && !((IProject)element).isOpen()) {
				return false;
			}
		}
        return true;
    }

}