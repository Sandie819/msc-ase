package msc.refactor.codecleaner.multiplerefactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

public class CompositeRefactoringProcessor extends RenameProcessor {

	@Override
	public Object[] getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessorName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isApplicable() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

//	@Singleton
//	public static class Access {
//
//		private CompositeRefactoringProcessor current;
//
//		private RefactoringProcessor currentJdtSourceRefactoringProcessor;
//
//		@Inject
//		private Provider<CompositeRefactoringProcessor> compositeRefactoringProvider;
//
//		public CompositeRefactoringProcessor getCurrent(RefactoringProcessor jdtSourceRefactoring) {
//			if (current == null || currentJdtSourceRefactoringProcessor != jdtSourceRefactoring) {
//				current = compositeRefactoringProvider.get();
//				currentJdtSourceRefactoringProcessor = jdtSourceRefactoring;
//			}
//			return current;
//		}
//
//		public void disposeCurrent() {
//			current = null;
//			currentJdtSourceRefactoringProcessor = null;
//		}
//		
//		public boolean isDisposed() {
//			return current == null && currentJdtSourceRefactoringProcessor == null;
//		}
//	}
//
//	@Inject
//	private TextChangeCombiner textChangeCombiner;
//
//	private Set<RefactoringProcessor> processors = newLinkedHashSet();
//
//	private String newName;
//
//	public boolean addProcessor(RefactoringProcessor processor) {
//		return processors.add(processor);
//	}
//
//	public int getNumProcessors() {
//		return processors.size();
//	}
//
//	@Override
//	public Object[] getElements() {
//		List<Object> elements = newArrayList();
//		for (RefactoringProcessor processor : processors) {
//			for (Object element : processor.getElements())
//				elements.add(element);
//		}
//		return toArray(elements, Object.class);
//	}
//
//	@Override
//	public String getProcessorName() {
//		return "Rename element and inferred Java artifacts";
//	}
//
//	@Override
//	public boolean isApplicable() throws CoreException {
//		for (RefactoringProcessor processor : processors) {
//			if (!processor.isApplicable())
//				return false;
//		}
//		return true;
//	}
//
//	@Override
//	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
//			OperationCanceledException {
//		SubMonitor monitor = SubMonitor.convert(pm, processors.size());
//		RefactoringStatus status = new RefactoringStatus();
//		for (RefactoringProcessor processor : processors) {
//			if (pm.isCanceled()) {
//				throw new OperationCanceledException();
//			}
//			status.merge(processor.checkInitialConditions(monitor.newChild(1)));
//		}
//		return status;
//	}
//
//	@Override
//	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context)
//			throws CoreException, OperationCanceledException {
//		SubMonitor monitor = SubMonitor.convert(pm, processors.size());
//		RefactoringStatus status = new RefactoringStatus();
//		for (RefactoringProcessor processor : processors) {
//			if (pm.isCanceled()) {
//				throw new OperationCanceledException();
//			}
//			status.merge(processor.checkFinalConditions(monitor.newChild(1), context));
//		}
//		return status;
//	}
//
//	@Override
//	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
//		SubMonitor monitor = SubMonitor.convert(pm, processors.size());
//		CompositeChange compositeChange = new CompositeChange(getProcessorName());
//		for (RefactoringProcessor processor : processors) {
//			if (pm.isCanceled()) {
//				throw new OperationCanceledException();
//			}
//			compositeChange.add(processor.createChange(monitor.newChild(1)));
//		}
//		return textChangeCombiner.combineChanges(compositeChange);
//	}
//
//	@Override
//	public RefactoringParticipant[] loadParticipants(final RefactoringStatus status,
//			final SharableParticipants sharedParticipants) throws CoreException {
//		List<RefactoringParticipant> participants = newArrayList();
//		for (RefactoringProcessor processor : processors) {
//			for (RefactoringParticipant participant : processor.loadParticipants(status, sharedParticipants)) {
//				if (!(participant instanceof JdtRenameParticipant))
//					participants.add(participant);
//			}
//		}
//		return toArray(participants, RefactoringParticipant.class);
//	}
//
//	@Override
//	public String getIdentifier() {
//		return getClass().getName();
//	}
//
//	@Override
//	public boolean initialize(IRenameElementContext renameElementContext) {
//		return false;
//	}
//
//	@Override
//	public String getOriginalName() {
//		return null;
//	}
//
//	@Override
//	public String getNewName() {
//		return newName;
//	}
//
//	@Override
//	public void setNewName(String newName) {
//		this.newName = newName;
//		for (RefactoringProcessor processor : processors) {
//			if (processor instanceof AbstractRenameProcessor)
//				((AbstractRenameProcessor) processor).setNewName(newName);
//		}
//	}
//
//	@Override
//	public RefactoringStatus validateNewName(String newName) {
//		RefactoringStatus status = new RefactoringStatus();
//		for (RefactoringProcessor processor : processors) {
//			if (processor instanceof AbstractRenameProcessor)
//				status.merge(((AbstractRenameProcessor) processor).validateNewName(newName));
//		}
//		return status;
//	}
}