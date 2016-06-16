package ar.edu.ungs.pps2.jgentest.parameters;

import java.io.File;

public class Parameters
{

	/**
	 * Path relativo donde se ubicarán todas las carpetas/archivos temporales
	 * que necesito generar
	 */
	private static final String	TMP_PATH							= "tmp";
	/**
	 * path de la carpeta donde se almacenarán los .java preprocesados
	 */
	private final static String	TMP_PATH_JAVA_PREPROCESS_CLASS		= TMP_PATH + File.separator + "preprocess";
	/**
	 * path de la carpeta donde se almacenarán los .java instrumentados
	 */
	private final static String	TMP_PATH_JAVA_INSTRUMENTED_CLASS	= TMP_PATH + File.separator + "instrument";
	/**
	 * path de la carpeta donde se almacenarán los .class (clase instrumentada
	 * compilada)
	 */
	private final static String	TMP_PATH_COMPILED_CLASS				= TMP_PATH + File.separator + "bin";
	/**
	 * path de la carpeta donde se almacenarán los archivos necesario de Spoon
	 */
	private final static String	SPOONED_FOLDER						= TMP_PATH + File.separator + "spooned";
	public final static String	JAVA_EXTENSION						= ".java";
	public final static String	PROPERTIES_FILE_NAME				= "jgentest.properties";

	/**
	 * Path absoluto del archivo .java sobre el cual se van a generar los test
	 */
	private static String		JAVA_FILE_PATH;
	/**
	 * Path absoluto del proyecto (incluye la carpeta del proyecto) al cual
	 * pertenece la clase .java sobre la cual se van a generar los test
	 */
	private static String		PROJECT_PATH;

	public final static String getTmpPath()
	{
		return getProjectPath() + TMP_PATH;
	}

	public final static String getJavaPathPreProcessClass()
	{
		return getProjectPath() + TMP_PATH_JAVA_PREPROCESS_CLASS;
	}

	public final static String getJavaPathInstrumentedClass()
	{
		return getProjectPath() + TMP_PATH_JAVA_INSTRUMENTED_CLASS;
	}

	public final static String getJavaPathCompiledClass()
	{
		return getProjectPath() + TMP_PATH_COMPILED_CLASS;
	}

	public final static String getSpoonedOutputFolderPath()
	{
		return getProjectPath() + SPOONED_FOLDER;
	}

	public final static String getPackagePreProcess()
	{
		return getPackage(TMP_PATH_JAVA_PREPROCESS_CLASS);
	}

	public final static String getPackageInstrumented()
	{
		return getPackage(TMP_PATH_JAVA_INSTRUMENTED_CLASS);
	}

	private static String getPackage(String path)
	{
		String packageName = path;
		if (path.contains(File.separator))
			packageName = packageName.substring(packageName.lastIndexOf(File.separator) + 1);

		return packageName;
	}

	public static void setJavaFilePath(String javaFilePath)
	{
		JAVA_FILE_PATH = javaFilePath;
	}

	public static String getJavaFilePath()
	{
		if (JAVA_FILE_PATH == null)
			throw new NullPointerException("No se ha seteado el Path del archivo Java!");
		return JAVA_FILE_PATH;
	}

	public static String getJavaFileName()
	{
		if (JAVA_FILE_PATH == null)
			throw new NullPointerException("No se ha seteado el Path del archivo Java!");
		return new File(JAVA_FILE_PATH).getName();
	}

	/**
	 * devuelve el path del proyecto con separador al final. Ejemplo:
	 * "/home/user/NomProyecto/"
	 * 
	 * @return
	 */
	public static String getProjectPath()
	{
		if (PROJECT_PATH == null)
			throw new NullPointerException("No se ha seteado el Path del proyecto!");
		return PROJECT_PATH;
	}

	// Si no tiene separador al final (../), entonces lo agrego
	public static void setProjectPath(String projectFilePath)
	{
		PROJECT_PATH = projectFilePath + getSeparatorIfNecesary(projectFilePath);
	}

	private static String getSeparatorIfNecesary(String path)
	{
		return ("" + path.charAt(path.length() - 1)).equals(File.separator) ? "" : File.separator;
	}

	// /**
	// * Paths de los jars necesarios para compilar en runtime la clase
	// * instrumentada. Básicamente son los jars a los que se haga referencia en
	// * dicha clase.
	// *
	// * @return
	// * @throws InvalidPathException
	// * si el path en el archivos properties no es valido
	// * FileNotFoundException si no se encuentra el archivo
	// * properties IOException
	// */
	// public static List<File> getFilesToAddClassPath() throws
	// InvalidPathException, FileNotFoundException, IOException
	// {
	// List<File> files = new ArrayList<>();
	//
	// // Cargo archivo .properties ubicado en la raiz del proyecto
	// Properties propertiesFile = new Properties();
	// propertiesFile.load(new FileInputStream(getProjectPath() +
	// PROPERTIES_FILE_NAME));
	// String path1 = propertiesFile.getProperty("jep");
	// if (path1 == null || path1.isEmpty())
	// throw new InvalidPathException(path1, "Ruta del archivo
	// 'jep-java-3.4-trial.jar' incorrecto");
	//
	// String path2 = propertiesFile.getProperty("jgen");
	// if (path2 == null || path2.isEmpty())
	// throw new InvalidPathException(path2, "Ruta del archivo 'jgen.jar'
	// incorrecto");
	//
	// files.add(new File(path1));
	// files.add(new File(path2));
	// return files;
	// }

}
