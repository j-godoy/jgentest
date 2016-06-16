package ar.edu.ungs.pps2.jgentest.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.ungs.pps2.jgentest.exceptions.InvalidPathException;
import ar.edu.ungs.pps2.jgentest.exceptions.LoadSpoonException;
import ar.edu.ungs.pps2.jgentest.functions.CompilerTool;
import ar.edu.ungs.pps2.jgentest.model.SpoonedClass;
import ar.edu.ungs.pps2.jgentest.model.UTGenerator;
import ar.edu.ungs.pps2.jgentest.parameters.Parameters;
import ar.edu.ungs.pps2.jgentest.utils.StoreFile;
import ar.edu.ungs.pps2.jgentest.view.SeleccionarMetodosView;
import ar.edu.ungs.pps2.jgentest.view.ViewUtils;
import spoon.reflect.declaration.CtMethod;

public class SeleccionarMetodosController
{

	private SpoonedClass			_spoonedClass;
	private SeleccionarMetodosView	_vista;
	private Set<CtMethod<?>>		_spoonedMethods;
	private String					_javaFilePath;
	private String					_javaFileName;

	public SeleccionarMetodosController(SeleccionarMetodosView smv)
	{
		_javaFilePath = Parameters.getJavaFilePath();
		_javaFileName = Parameters.getJavaFileName();
		_vista = smv;
		_vista.setController(this);
		try
		{
			_spoonedClass = new SpoonedClass(_javaFilePath);
		} catch (Exception e)
		{
			ViewUtils.alertException("Se ha producido un error al cargar la clase " + _javaFilePath, e);
			return;
		}
		_spoonedMethods = _spoonedClass.getAllMethods();
	}

	public void init()
	{
		_vista.open(_javaFileName);
		this.clearListAndCompleteMethods(_spoonedClass.getAllMethods());
	}

	private void clearListAndCompleteMethods(Set<CtMethod<?>> methods)
	{
		_vista.getCbList().clearSelection();
		List<String> methodsNames = new ArrayList<>();
		for (CtMethod<?> M : methods)
		{
			methodsNames.add(M.getSignature());
		}
		_vista.addMetodos(methodsNames);
	}

	public void generarCasosDeTest()
	{

		// Necesito que esten disponibles algunas clases que no están en el
		// classpath
		// despues de compilar la clase instrumentada
		try
		{
			CompilerTool.addFilesToClassPath();
		} catch (Exception e)
		{
			e.printStackTrace();
			ViewUtils.alertException("Error inesperado al agregar clase al classPath", e);
			return;
		}

		Set<CtMethod<?>> selectedMethods = new HashSet<>();
		List<String> methodsNames = _vista.getCbList().getSelectedItems();
		for (CtMethod<?> M : _spoonedMethods)
		{
			if (methodsNames.contains(M.getSignature()))
				selectedMethods.add(M);
		}

		if (selectedMethods.isEmpty())
		{
			ViewUtils.alertWarning("Operación no permitida", "Debe seleccionar al menos un método!");
			return;
		}

		Integer k = null;
		try
		{
			k = Integer.parseInt(_vista.getProfundidadCiclos().getText());
		} catch (Exception e)
		{
		}

		if (k == null || _vista.getProfundidadCiclos().getText().isEmpty()
				|| _vista.getProfundidadCiclos().getText().replaceAll("[0]*", "").isEmpty())
		{
			ViewUtils.alertWarning("Operación no permitida",
					"Debe ingresar un número mayor a cero en el campo \"Profundidad\" ciclos");
			return;
		}

		UTGenerator generator;
		try
		{
			generator = new UTGenerator(_spoonedClass.getJavaFilePath(), selectedMethods,
					UTGenerator.TEMP_FILES.BORRAR);
		} catch (IOException e2)
		{
			e2.printStackTrace();
			ViewUtils.alertException("Error al copiar el archivo!", e2);
			return;
		}

		String testClass = null;
		try
		{
			testClass = generator.generarCasos(k);
		} catch (IOException e1)
		{
			e1.printStackTrace();
			ViewUtils.alertException("Error al realizar una acción con un archivo!", e1);
			return;
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			ViewUtils.alertException("Error! No se pudo encontrar un método!", e);
			return;
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			ViewUtils.alertException("Error! No se pudo encontrar una clase!", e);
			return;
		} catch (InstantiationException e)
		{
			e.printStackTrace();
			ViewUtils.alertException("Error al instanciar la clase!", e);
			return;
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
			ViewUtils.alertException("Error! La clase no es accesible!", e);
			return;
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			ViewUtils.alertException("Error! No se pudo encontrar un atributo de la clase!", e);
			return;
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
			ViewUtils.alertException("Error al ejecutar el método!", e);
			return;
		} catch (LoadSpoonException e)
		{
			e.printStackTrace();
			ViewUtils.alertException(e.getMessage(), e);
			return;
		} catch (InvalidPathException e)
		{
			e.printStackTrace();
			ViewUtils.alertException(e.getMessage(), e);
			return;
		}

		String classToTestName = _spoonedClass.getSpoonedClassName();
		String pathToSave = _javaFilePath.replace(classToTestName + Parameters.JAVA_EXTENSION, "");
		StoreFile sf = new StoreFile(pathToSave, Parameters.JAVA_EXTENSION, testClass, classToTestName + "Test",
				StoreFile.CHARSET_UTF8);
		try
		{
			sf.store();
			ViewUtils.alertInformation("Se ha almacenado la clase " + classToTestName + "Test",
					"Se han generado correctamente " + generator.getGeneratedMethodsCount()
							+ " métodos y fueron almacenados en el directorio " + pathToSave);
		} catch (IOException e)
		{
			ViewUtils.alertException("Error al guardar la clase con los casos de test (" + classToTestName + "Test)",
					e);
			e.printStackTrace();
			return;
		}
		// TODO: eliminar!
		System.out.println("Generación finalizada!");
		_vista.dispose();
	}

}