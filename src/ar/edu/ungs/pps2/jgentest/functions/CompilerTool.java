package ar.edu.ungs.pps2.jgentest.functions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import ar.edu.ungs.pps2.jgentest.exceptions.InvalidPathException;
import ar.edu.ungs.pps2.jgentest.parameters.Parameters;
import ar.edu.ungs.pps2.jgentest.model.ConcolicExpression;
import ar.edu.ungs.pps2.jgentest.view.ViewUtils;

/**
 * Dynamic java class compiler and executer <br>
 * Demonstrate how to compile dynamic java source code, <br>
 * instantiate instance of the class, and finally call method of the class <br>
 *
 * http://www.beyondlinux.com
 *
 * @author david 2011/07
 *
 */
public class CompilerTool
{
	/** where shall the compiled class be saved to (should exist already) */
	private static String _classOutputFolder;

	public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject>
	{
		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic)
		{

			System.out.println("Line Number->" + diagnostic.getLineNumber());
			System.out.println("code->" + diagnostic.getCode());
			System.out.println("Message->" + diagnostic.getMessage(Locale.ENGLISH));
			System.out.println("Source->" + diagnostic.getSource());
			System.out.println(" ");
			ViewUtils.alertInformation("compilerError",
					"Line Number->" + diagnostic.getLineNumber() + "\n" + "code->" + diagnostic.getCode() + "\n"
							+ "Message->" + diagnostic.getMessage(Locale.ENGLISH) + "\n" + "Source->"
							+ diagnostic.getSource(),
					null);
		}
	}

	public static Class<?> CompileAndGetClass(String className, String sourceCode, String classOutputFolder)
			throws ClassNotFoundException, MalformedURLException, InvalidPathException
	{
		CompileFile(className, sourceCode, classOutputFolder);

		// 3.Load your class by URLClassLoader, then instantiate the instance,
		// and call method by reflection
		return LoadClass(className, classOutputFolder);
	}

	public static boolean CompileFile(String className, String sourceCode, String classOutputFolder)
			throws InvalidPathException
	{
		_classOutputFolder = classOutputFolder;
		// 1.Construct an in-memory java source file from your dynamic code
		JavaFileObject file = getJavaFileObject(className, sourceCode);
		Iterable<? extends JavaFileObject> files = Arrays.asList(file);

		// 2.Compile your files by JavaCompiler
		return compile(files);
	}

	/**
	 * Get a simple Java File Object ,<br>
	 * It is just for demo, content of the source code is dynamic in real use
	 * case
	 * 
	 * @param sourceCode
	 * @param className
	 */
	private static JavaFileObject getJavaFileObject(String className, String sourceCode)
	{
		JavaFileObject so = null;
		try
		{
			so = new InMemoryJavaFileObject(className, sourceCode);
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
		return so;
	}

	/**
	 * java File Object represents an in-memory java source file <br>
	 * so there is no need to put the source file on hard disk
	 **/
	public static class InMemoryJavaFileObject extends SimpleJavaFileObject
	{
		private String contents = null;

		public InMemoryJavaFileObject(String className, String contents)
		{
			super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.contents = contents;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
		{
			return contents;
		}
	}

	/**
	 * compile your files by JavaCompiler Antes de compilar la clase, creo el
	 * directorio donde va a guardarse el .class(es necesario que exista!)
	 * 
	 * @return true si la compilación fue correcta
	 * @throws InvalidPathException
	 */
	private static boolean compile(Iterable<? extends JavaFileObject> files) throws InvalidPathException
	{
		new File(_classOutputFolder).mkdirs();

		// get system compiler:
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		// for compilation diagnostic message processing on compilation
		// WARNING/ERROR
		MyDiagnosticListener c = new MyDiagnosticListener();

		StandardJavaFileManager fileManager = null;
		
		try {
			fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);			
		} catch (NullPointerException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		try
		{
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(_classOutputFolder)));
		} catch (IOException e)
		{
			throw new InvalidPathException("No existe el directorio " + _classOutputFolder);
		}
		// Agrego al classpath los archivos necesarios para compilar la
		// clase instrumentada
		try
		{
			fileManager.setLocation(StandardLocation.CLASS_PATH, getFilesToAddClassPath());
		} catch (IOException e)
		{
			throw new InvalidPathException("No existe el directorio " + getFilesToAddClassPath().iterator().toString());
		}

		// specify classes output folder
		Iterable<String> options = Arrays.asList("-d", _classOutputFolder);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, c, options, null, files);
		return task.call();
	}

	private static Iterable<? extends File> getFilesToAddClassPath() throws InvalidPathException
	{
		List<File> files = new ArrayList<>();
		File jgenFile = new File(Parameters.getProjectPath() + "jgentestPlugin" + File.separator + "jgen.jar");
		File jepTrialFile = new File(
				Parameters.getProjectPath() + "jgentestPlugin" + File.separator + "jep-java-3.4-trial.jar");

		if (!jgenFile.exists())
			throw new InvalidPathException("Asegurese de tener jgentestPlugin" + File.separator + "jgen.jar"
					+ " en el directorio de su proyecto");

		if (!jepTrialFile.exists())
			throw new InvalidPathException("Asegurese de tener jgentestPlugin" + File.separator
					+ "jep-java-3.4-trial.jar" + " en el directorio de su proyecto");

		files.add(jgenFile);
		files.add(jepTrialFile);
		return files;
		// final File f = new
		// File(ConcolicExpression.class.getProtectionDomain().getCodeSource().getLocation().getPath()
		// + File.separator + "bin");
		// final File s = new
		// File(Node.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		//
		// files.add(s);
		// files.add(f);
		// ViewUtils.alertInformation("file s: ", s.getAbsolutePath(), null);
		// ViewUtils.alertInformation("file f: ", f.getAbsolutePath(), null);
		// return files;
	}

	/**
	 * run class from the compiled byte code file by URLClassloader
	 * 
	 * @param className
	 * @param classFolder
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public static Class<?> LoadClass(String className, String classFolder)
			throws ClassNotFoundException, MalformedURLException
	{
		if (classFolder == null || classFolder.isEmpty())
			throw new IllegalArgumentException(
					"Es necesario agregar la carpeta donde se encuentra el .class de la clase que se desea cargar!");
		// Create a File object on the root of the directory
		// containing the class file
		File file = new File(classFolder);
		// Convert File to a URL
		@SuppressWarnings("deprecation")
		URL url = file.toURL(); // file:classFolder
		URL[] urls = new URL[] { url };

		// Create a new class loader with the directory
		@SuppressWarnings("resource")
		ClassLoader loader = new URLClassLoader(urls);

		return loader.loadClass(className);
	}

	public static void addFilesToClassPath() throws InvalidPathException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException
	{
		Iterable<? extends File> files = getFilesToAddClassPath();
		for (File file : files)
			addPathToClassPath(file.getAbsolutePath());
	}

	// need to do add path to Classpath with reflection since the
	// URLClassLoader.addURL(URL url) method is protected:
	private static void addPathToClassPath(String s) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException
	{
		File f = new File(s);
		URI u = f.toURI();
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { u.toURL() });
	}

}