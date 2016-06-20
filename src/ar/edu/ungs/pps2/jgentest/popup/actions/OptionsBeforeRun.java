package ar.edu.ungs.pps2.jgentest.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ar.edu.ungs.pps2.jgentest.controller.SeleccionarMetodosController;
import ar.edu.ungs.pps2.jgentest.parameters.Parameters;
import ar.edu.ungs.pps2.jgentest.view.SeleccionarMetodosView;

public class OptionsBeforeRun implements IObjectActionDelegate
{

	// private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public OptionsBeforeRun()
	{
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		// shell = targetPart.getSite().getShell();
	}

	// obtengo el path de la clase java seleccionada e inicio aplicación
	@Override
	public void run(IAction action)
	{
		String javaFilePath = null;
		String projectFilePath = null;
		IWorkbenchWindow windowWorkbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (windowWorkbench != null)
		{
			for (IWorkbenchPage wp : windowWorkbench.getPages())
			{
				IFile fileSelected = wp.getActiveEditor().getEditorInput().getAdapter(IFile.class);
				javaFilePath = fileSelected.getLocation().toOSString();
				projectFilePath = fileSelected.getProject().getLocation().toOSString();
			}
		}

		Parameters.setJavaFilePath(javaFilePath);
		Parameters.setProjectPath(projectFilePath);

		SeleccionarMetodosView smv = SeleccionarMetodosView.getInstance();
		SeleccionarMetodosController controller = new SeleccionarMetodosController(smv);
		controller.init();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
