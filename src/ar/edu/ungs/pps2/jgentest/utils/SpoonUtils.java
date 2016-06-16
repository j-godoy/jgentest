package ar.edu.ungs.pps2.jgentest.utils;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

public class SpoonUtils
{

	public static List<CtTypeReference<?>> getTypeReferences(CtMethod<?> method)
	{
		List<CtTypeReference<?>> typeRefs = new ArrayList<>();
		for (CtParameter<?> P : method.getParameters())
		{
			typeRefs.add(P.getType());
		}

		return typeRefs;
	}

	public static Class<?>[] getParametersTypes(CtMethod<?> method)
	{
		Class<?>[] parametersTypes = new Class<?>[method.getParameters().size()];
		for (int i = 0; i < method.getParameters().size(); i++)
		{
			parametersTypes[i] = method.getParameters().get(i).getType().getActualClass();
		}

		return parametersTypes;
	}
}
