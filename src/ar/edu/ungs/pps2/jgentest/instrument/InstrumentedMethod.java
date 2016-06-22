package ar.edu.ungs.pps2.jgentest.instrument;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.microsoft.z3.Z3Exception;

import ar.edu.ungs.pps2.jgentest.model.IOSolutionTest;
import ar.edu.ungs.pps2.jgentest.model.SymbCondition;
import ar.edu.ungs.pps2.jgentest.model.Z3Solver;
import ar.edu.ungs.pps2.jgentest.utils.Utils;

public class InstrumentedMethod
{
	private String					_conditionsFieldName	= "_conditions";
	private String					_symbParamFieldName		= "_parameters";
	private List<IOSolutionTest>	_ioSolutionTest;
	private Class<?>				_instrumentedClass;

	public InstrumentedMethod(Class<?> clas)
	{
		if (clas == null)
			throw new NullPointerException("La clase recibida no puede ser null!");
		this._instrumentedClass = clas;
	}

	@SuppressWarnings("unchecked")
	public List<IOSolutionTest> generateInputs(String methodName, Class<?>... parameterTypes)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException, Z3Exception
	{
		_ioSolutionTest = new ArrayList<>();
		Object clazz;
		try
		{
			clazz = _instrumentedClass.newInstance();
		} catch (InstantiationException e1)
		{
			throw e1;
		} catch (IllegalAccessException e1)
		{
			throw e1;
		}
		Method methodCall;
		try
		{
			methodCall = clazz.getClass().getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e1)
		{
			throw e1;
		} catch (SecurityException e1)
		{
			throw e1;
		}

		// Inicializo los parametros del método a ejecutar con cero.
		Object[] inputs = new Object[parameterTypes.length];
		for (int i = 0; i < inputs.length; i++)
		{
			inputs[i] = 0;
		}

		Object output = null;
		try
		{
			// TODO: estas dos lineas estan repetidas, extraer a un método!!!
			output = methodCall.invoke(clazz, inputs);
			addValuesToReturnList(output, inputs);
		} catch (Exception e)
		{
			e.printStackTrace();
			// TODO: mejorar!
			throw e;
		}

		Field conditionsField;
		try
		{
			conditionsField = clazz.getClass().getDeclaredField(_conditionsFieldName);
		} catch (NoSuchFieldException e)
		{
			throw e;
		} catch (SecurityException e)
		{
			throw e;
		}
		LinkedHashSet<SymbCondition> conditionsSymb = (LinkedHashSet<SymbCondition>) conditionsField.get(clazz);
		// System.out.println("Condiciones: " + conditionsSymb);

		Field symbParamField = clazz.getClass().getDeclaredField(_symbParamFieldName);
		List<String> symbParams = (ArrayList<String>) symbParamField.get(clazz);

		try
		{
			conditionsSymb = (LinkedHashSet<SymbCondition>) Utils.serializeAndGetObject(conditionsSymb);
		} catch (Exception e1)
		{
			// Esto no debería pasar jamás..
			// sólo serializo y deserealizo el mismo objeto por un problema con
			// el classloader
			// Al parecer, hya clases que están en diferentes classloader
			// Este problema surge que agrego al classloader las clases al
			// momento de compilar
			// la clase instrumentada
			e1.printStackTrace();
		}

		while (!conditionsSymb.isEmpty())
		{
			negarYMarcarUltimaCondicion(conditionsSymb);
			Z3Solver z3 = new Z3Solver();
			// System.out.println("Condiciones a satisfacer: " +
			// conditionsSymb);
			Map<String, Integer> mapValues = z3.getSatisfiableValues(conditionsSymb);

			// si z3 encontro valores, corro con esos valores
			if (!mapValues.isEmpty())
			{
				// System.out.println("mapValues: " + mapValues.toString());
				inputs = new Object[symbParams.size()];
				int i = 0;
				for (String param : symbParams)
				{
					// TODO: a tener en cuenta!
					// Si no interesan todos los parámetros para esta ejecución,
					// a los que no importa les asigno cualquier número
					if (mapValues.get(param) == null)
					{
						inputs[i] = 0;
						break;
					}
					inputs[i] = mapValues.get(param);
					i++;
				}

				try
				{
					output = methodCall.invoke(clazz, inputs);
					addValuesToReturnList(output, inputs);
				} catch (IllegalArgumentException e)
				{
					throw e;
				} catch (InvocationTargetException e)
				{
					throw e;
				}
			}
			deleteUsedConditions(conditionsSymb);
			// System.out.println("conditions processed: " + conditionsSymb);
		}

		return _ioSolutionTest;
	}

	/**
	 * Elimino en orden inverso las condiciones marcadas como ya negadas (sólo
	 * las consecutivas hasta que aparece una condición no marcada)
	 * 
	 * @param conditionsSymb
	 */
	private void deleteUsedConditions(LinkedHashSet<SymbCondition> conditionsSymb)
	{
		List<SymbCondition> copyConditions = new ArrayList<>(conditionsSymb);
		int sizeList = copyConditions.size() - 1;
		for (int i = sizeList; i >= 0; i--)
		{
			SymbCondition actualSymbCondition = copyConditions.get(i);

			if (!actualSymbCondition.isNegado())
				return;

			conditionsSymb.remove(actualSymbCondition);
		}
	}

	private void negarYMarcarUltimaCondicion(LinkedHashSet<SymbCondition> conditionsSymb)
	{
		SymbCondition condition = null;
		Iterator<SymbCondition> allCond = conditionsSymb.iterator();
		while (allCond.hasNext())
		{
			condition = allCond.next();
		}
		conditionsSymb.remove(condition);
		condition = condition.makeNegado();
		condition.setNegado(true);
		conditionsSymb.add(condition);
	}

	private void addValuesToReturnList(Object output, Object... inputs)
	{
		_ioSolutionTest.add(new IOSolutionTest(inputs, output));
	}

}
