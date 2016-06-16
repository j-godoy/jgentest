package ar.edu.ungs.pps2.jgentest.model;

import java.util.Set;

import ar.edu.ungs.pps2.jgentest.exceptions.LoadSpoonException;
import ar.edu.ungs.pps2.jgentest.parameters.Parameters;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class SpoonedClass
{
	private Launcher	_spoon;
	private CtClass<?>	_ctClass;
	private String		_pathJavaFile;

	public SpoonedClass(String pathFile) throws LoadSpoonException
	{
		this._spoon = new Launcher();
		this._pathJavaFile = pathFile;
		loadClass();
	}

	private void loadClass() throws LoadSpoonException
	{
		_spoon.addInputResource(_pathJavaFile);
		_spoon.setSourceOutputDirectory(Parameters.getSpoonedOutputFolderPath());
		_spoon.setBinaryOutputDirectory(Parameters.getJavaPathCompiledClass());
		try
		{
			_spoon.run();
		} catch (Exception e)
		{
			throw new LoadSpoonException("Error al cargar la clase " + _pathJavaFile, e);
		}

		Factory factory = _spoon.getFactory();
		_ctClass = (CtClass<?>) factory.Class().getAll().get(0);
	}

	public Factory getFactory()
	{
		return _spoon.getFactory();
	}

	public ClassLoader getClassLoader()
	{
		return this._spoon.getEnvironment().getClassLoader();
	}

	// TODO: modificar! puede haber sobrecarga!
	public CtMethod<?> getSpoonedMethod(String method)
	{
		if (method.isEmpty() || method == null)
			return null;
		return _ctClass.getMethodsByName(method).get(0);
	}

	public CtClass<?> getSpoonedClass()
	{
		return this._ctClass;
	}

	public Launcher getLaucher()
	{
		return this._spoon;
	}

	public Set<CtMethod<?>> getAllMethods()
	{
		return _ctClass.getAllMethods();
	}

	public String getSpoonedClassName()
	{
		return this._ctClass.getSimpleName();
	}

	public String getJavaFilePath()
	{
		return _pathJavaFile;
	}

}
