package ar.edu.ungs.pps2.jgentest.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ar.edu.ungs.pps2.jgentest.controller.SeleccionarMetodosController;
import ar.edu.ungs.pps2.jgentest.parameters.Parameters;
import ar.edu.ungs.pps2.jgentest.view.SeleccionarMetodosView;
import ar.edu.ungs.pps2.jgentest.view.ViewUtils;

public class OptionsBeforeRun implements IObjectActionDelegate
{
	// private Shell shell;
	private static IProject actualProject;

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
		actualProject = null;
		IWorkbenchWindow windowWorkbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (windowWorkbench != null)
		{
			for (IWorkbenchPage wp : windowWorkbench.getPages())
			{
				IEditorPart activeEditor = wp.getActiveEditor();
				if (activeEditor == null)
				{
					ViewUtils.alertWarning("un momento!", "Debe tener abierto un .java en el editor", null);
					return;
				}
				IFile fileSelected = activeEditor.getEditorInput().getAdapter(IFile.class);
				javaFilePath = fileSelected.getLocation().toOSString();

				actualProject = fileSelected.getProject();
				projectFilePath = actualProject.getLocation().toOSString();
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

	public static void updateWorkspace() throws CoreException
	{
		actualProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

}
