package ar.edu.ungs.pps2.jgentest.functions;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGenerator
{
	/**
	 * String de la Clase Test a generar
	 */
	private StringBuilder			_stringClassTest;

	/**
	 * métodos de la Clase Test a generar
	 */
	private List<StringBuilder>		_methods;

	/**
	 * Nombre de la clase a generar
	 */
	private String					_className;

	/**
	 * Número de método
	 */
	private Map<String, Integer>	_methodNum;

	private String					_packageClass;

	public TestGenerator(String classToTest, String packetClass)
	{
		this._stringClassTest = new StringBuilder();
		this._methods = new ArrayList<>();
		this._className = classToTest;
		this._packageClass = packetClass;
		this._methodNum = new HashMap<>();
	}

	public String getGeneratedTestClass()
	{
		this._stringClassTest.append("package " + this._packageClass + ";\n");
		this._stringClassTest.append("import org.junit.Test;\n");
		this._stringClassTest.append("import static org.junit.Assert.assertEquals;\n");
		this._stringClassTest.append("\n\n");

		this._stringClassTest.append("public class " + this._className + "Test" + "\n {\n");

		for (StringBuilder method : this._methods)
		{
			this._stringClassTest.append("\n" + method + "\n");
		}

		this._stringClassTest.append("\n}");

		return this._stringClassTest.toString();
	}

	public void addMethodTest(String methodName, String returnType, Object[] parameters, Class<?>[] parametersTypes,
			Object ouput) throws MalformedURLException
	{
		addCounterMethod(methodName);
		StringBuilder method = new StringBuilder();
		method.append("\t@Test\n");
		method.append("\tpublic void " + methodName + "_" + this._methodNum.get(methodName) + "()\n\t{\n");
		String varName = "var";
		method.append("\t\t" + this._className + " " + varName + " = new " + this._className + "();\n");
		String params = "";
		for (Object p : parameters)
		{
			params += p + ", ";
		}
		// Saco los últimos caracteres (para sacar la ", ")
		params = params.substring(0, params.length() - 2);
		String methodCall = varName + "." + methodName + "(" + params + ")";

		if (returnType.equals("Integer"))
		{
			method.append("\t\tassertEquals(new Integer(" + ouput + "), " + methodCall + ");\n");
		} else if (returnType.equals("int"))
		{
			method.append("\t\tassertEquals(" + ouput + ", " + methodCall + ");\n");
		} else if (returnType.equals("double") || returnType.equals("Double"))
		{
			method.append("\t\tassertEquals(" + ouput + ", " + methodCall + "," + "1e-10);\n");
		} else if (returnType.equals("String"))
		{
			method.append("\t\tassertEquals(\"" + ouput + "\", " + methodCall + ");\n");
		} else // if (returnType.equals("void"))
		{
			method.append("\t\t" + methodCall + ";\n");
		}

		method.append("\t}");
		this._methods.add(method);
	}

	private void addCounterMethod(String methodName)
	{
		Integer n = this._methodNum.get(methodName) == null ? 1 : (this._methodNum.get(methodName) + 1);
		this._methodNum.put(methodName, n);
	}

	public Integer getGeneratedMethodsCount()
	{
		Integer n = 0;
		for (String method : this._methodNum.keySet())
		{
			n += this._methodNum.get(method);
		}
		return n;
	}

}
