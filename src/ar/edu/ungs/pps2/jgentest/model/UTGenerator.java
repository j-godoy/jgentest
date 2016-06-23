package ar.edu.ungs.pps2.jgentest.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import com.microsoft.z3.Z3Exception;

import ar.edu.ungs.pps2.jgentest.exceptions.InvalidPathException;
import ar.edu.ungs.pps2.jgentest.exceptions.LoadSpoonException;
import ar.edu.ungs.pps2.jgentest.functions.CompilerTool;
import ar.edu.ungs.pps2.jgentest.functions.TestGenerator;
import ar.edu.ungs.pps2.jgentest.instrument.Instrumentator;
import ar.edu.ungs.pps2.jgentest.instrument.InstrumentedMethod;
import ar.edu.ungs.pps2.jgentest.parameters.Parameters;
import ar.edu.ungs.pps2.jgentest.utils.SpoonUtils;
import ar.edu.ungs.pps2.jgentest.utils.StoreFile;
import ar.edu.ungs.pps2.jgentest.utils.Utils;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

public class UTGenerator
{

	private Set<CtMethod<?>>	_methods;
	private String				_javaFilePath;
	private String				_packageClass;
	private TestGenerator		_testClass;

	public enum TEMP_FILES
	{
		BORRAR, MANTENER
	};

	private TEMP_FILES _borrarTemporales;

	public UTGenerator(String javaFilePath, Set<CtMethod<?>> methods, TEMP_FILES option) throws IOException
	{
		_javaFilePath = javaFilePath;
		_borrarTemporales = option;
		_packageClass = Utils.getPackageName(_javaFilePath);
		_methods = methods;
		// Copio el archivo java a un directorio temporal
		StoreFile.copyFile(javaFilePath,
				Parameters.getJavaPathPreProcessClass() + File.separator + Parameters.getJavaFileName());
	}

	public String generarCasos(int k) throws LoadSpoonException, NoSuchMethodException, IOException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException,
			InvocationTargetException, InvalidPathException, Z3Exception
	{

		_testClass = new TestGenerator(Utils.getSimpleJavaFileName(_javaFilePath), this._packageClass);
		CtClass<?> ctInstrumentedClass = null;

		System.out.println("Preprocesando...");
		// XXXXXXX: Primero hago un preprocesamiento de cada método
		for (CtMethod<?> actualMethod : _methods)
		{
			instrumentPreProcess(actualMethod, k);
		}

		System.out.println("Instrumentando...");
		// XXXXXXX: Después instrumento los métodos a partir del
		// preprocesamiento anterior
		Instrumentator instrumentator = new Instrumentator(Parameters.getJavaPathPreProcessClass(), true);
		for (CtMethod<?> actualMethod : _methods)
		{
			List<CtTypeReference<?>> typeReferences = SpoonUtils.getTypeReferences(actualMethod);
			instrumentator.instrumentMethod(actualMethod.getSimpleName(), typeReferences);
		}
		ctInstrumentedClass = instrumentator.getInstrumentedClass();

		// XXXXXXX: Guardo la clase instrumentada
		storeClass(ctInstrumentedClass, Parameters.getJavaPathInstrumentedClass(), Parameters.INSTRUMENT);

		// XXXXXXX: Compilo la clase instrumentada y la cargo
		Class<?> instrumentedClass = null;
		String qualifiedClassName = Parameters.getPackageInstrumented() + "." + ctInstrumentedClass.getSimpleName();
		String sourceClass = "package " + Parameters.getPackageInstrumented() + ";\n" + ctInstrumentedClass.toString();
		instrumentedClass = CompilerTool.CompileAndGetClass(qualifiedClassName, sourceClass,
				Parameters.getJavaPathCompiledClass());

		System.out.println("Generando casos...");
		// XXXXXX: Ejecuto cada método instrumentado y guardo los valores
		// obtenidos por el solver
		InstrumentedMethod instrumentedMethod = new InstrumentedMethod(instrumentedClass);
		for (CtMethod<?> actualMethod : _methods)
		{
			// XXXXXX: Obtengo los valores para testear el método
			List<IOSolutionTest> solutionsForTest = null;
			Class<?>[] parameterTypes = SpoonUtils.getParametersTypes(actualMethod);
			solutionsForTest = instrumentedMethod.generateInputs(actualMethod.getSimpleName(), parameterTypes);

			// XXXXXX: creo los casos de test para los inputs generados
			for (IOSolutionTest IOST : solutionsForTest)
			{
				_testClass.addMethodTest(actualMethod.getSimpleName(), actualMethod.getType().getSimpleName(),
						IOST.getInputs(), SpoonUtils.getParametersTypes(actualMethod), IOST.getOutput());
			}
		}

		// Borro las carpeta y clases que creé en el preprocesamiento e
		// instrumentación
		// TODO: cambiar a TEMP_FILES.borrar!!!!
		if (_borrarTemporales.compareTo(TEMP_FILES.BORRAR) == 0)
		{
			Utils.delete(new File(Parameters.getTmpPath()));
		}

		// XXXXXX: Devuelvo el String con la clase que contiene los casos de
		// test
		return _testClass.getGeneratedTestClass();
	}

	private void instrumentPreProcess(CtMethod<?> actualMethod, int k)
			throws NoSuchMethodException, IOException, LoadSpoonException
	{
		Instrumentator instrumentator = new Instrumentator(Parameters.getJavaPathPreProcessClass(), false);

		List<CtTypeReference<?>> typeReferences = SpoonUtils.getTypeReferences(actualMethod);

		if (instrumentator.preProcess(actualMethod.getSimpleName(), typeReferences, k))
		{
			storeClass(instrumentator.getInstrumentedClass(), Parameters.getJavaPathPreProcessClass(),
					Parameters.PREPROCESS);
			instrumentPreProcess(actualMethod, k);
			return;
		}

		if (instrumentator.preProcessLoop(actualMethod.getSimpleName(), typeReferences, k))
		{
			storeClass(instrumentator.getInstrumentedClass(), Parameters.getJavaPathPreProcessClass(),
					Parameters.PREPROCESS);
			instrumentPreProcess(actualMethod, k);
			return;
		}

	}

	public Integer getGeneratedMethodsCount()
	{
		return this._testClass.getGeneratedMethodsCount();
	}

	private void storeClass(CtClass<?> ctClas, String pathToSave, String packageName) throws IOException
	{
		String FileName = ctClas.getSimpleName();

		String contentClass = "package " + packageName + ";\n\n" + ctClas.toString();
		StoreFile sf = new StoreFile(pathToSave + "/", Parameters.JAVA_EXTENSION, contentClass, FileName,
				StoreFile.CHARSET_UTF8);
		sf.store();
	}

}
