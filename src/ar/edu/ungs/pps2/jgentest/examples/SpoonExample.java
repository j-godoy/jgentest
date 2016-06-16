package ar.edu.ungs.pps2.jgentest.examples;

import java.util.List;

import spoon.Launcher;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonExample
{

	public static void main(String[] args)
	{
		Launcher spoon = new Launcher();
		spoon.addInputResource("src/examples/");
		spoon.run();
		Factory factory = spoon.getFactory();

		// list all classes of the model
		for (CtType<?> s : factory.Class().getAll())
		{
			// System.out.println("class: "+s.getQualifiedName());
			// System.out.println("signature: " + s.getSignature());
			// System.out.println("simple name: " + s.getSimpleName());
			// System.out.println("all methods: " + s.getAllMethods());
			// System.out.println("declared fields: " + s.getDeclaredFields());
			if (s.getActualClass().equals(ClassToInstrument.class))
			{
				System.out.println("Instrumentando clase: " + s.getSimpleName());
				List<CtMethod<?>> methods = s.getMethodsByName("example");
				System.out.println("Contiene " + methods.size() + " métodos\n");
				CtMethod<?> met = methods.get(0);
				List<CtParameter<?>> param = met.getParameters();
				System.out.print("Tiene " + param.size() + " parámetros: ");
				System.out.println(param.get(0).getSimpleName() + ", " + param.get(1).getSimpleName());
				System.out.println("cuerpo del método:\n" + met.getBody() + "\n");

				// Agrego código al ppio del método: parámetro 1 = parámetro 2
				String param1 = param.get(0).getSimpleName();
				String param2 = param.get(1).getSimpleName();
				CtCodeSnippetStatement snippet = factory.Code().createCodeSnippetStatement(param1 + " += " + param2);
				met.getBody().insertBegin(snippet);

				System.out.println("Body después: " + met.getBody());

				// Obtengo todas las asignaciones
				// TODO: ver por qué no toma la asignación que agregué
				TypeFilter<CtAssignment<?, ?>> filterAssigment = new TypeFilter<CtAssignment<?, ?>>(CtAssignment.class);
				List<CtAssignment<?, ?>> assigments = met.getBody().getElements(filterAssigment);
				System.out.println("Asignaciones: " + assigments);
				for (CtAssignment<?, ?> as : assigments)
				{
					System.out.println("** " + as.toString());
					// obtengo la variable que se modifica (si n1 = 2 obtengo
					// n1)
					System.out.println("Assigned: " + as.getAssigned());

					// obtengo la variable/valor que se asigna (si n1 = 2
					// obtengo 2)
					System.out.println("Assignment: " + as.getAssignment());

					// as.delete();
				}

				// System.out.println("Body después: " + met.getBody());
			}

			// CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);

			// CtMethod<?> method = (CtMethod<?>)
			// clazz1.getAllMethods().toArray()[0];

			// CtInvocation<?> invo = (CtInvocation<?>)
			// method.getBody().getStatement(0);
			//
			// CtLiteral<?> argument1 = (CtLiteral<?>)
			// invo.getArguments().get(0);
		}

	}

	// private static void
	// showStatementsAndProcessIF_LOCALVAR_ASSIGNMENTS(CtMethod<?> method,
	// Factory factory)
	// {
	// TypeFilter<CtStatement> f = new
	// TypeFilter<CtStatement>(CtStatement.class);
	// List<CtStatement> st = method.getElements(f);
	//
	// for (CtStatement s : st)
	// {
	// // System.out.println("S--> " + s.toString() + " : " +
	// // s.getClass());
	//
	// // procesar condicional
	// if (s instanceof CtIfImpl)
	// {
	// String condition = ((CtIf) s).getCondition().toString();
	// String symbolicCondition = "";// getSymbolicExpression(condition);
	//
	// CtCodeSnippetStatement snippetIf = factory.Code()
	// .createCodeSnippetStatement("if (" + condition + ") conditions.add(\"" +
	// symbolicCondition
	// + "\"); else conditions.add(\"!(" + symbolicCondition + ")\")");
	// ((CtIf) s).insertBefore(snippetIf);
	//
	// }
	//
	// // procesar declaración de variables locales
	// else if (s instanceof CtLocalVariableImpl<?>)
	// {
	// String var = ((CtLocalVariableImpl<?>) s).getSimpleName();
	// String assignment = ((CtLocalVariableImpl<?>)
	// s).getAssignment().toString();
	//
	// String assigmentSymbolic = "";// getSymbolicExpression(assignment);
	//
	// String sourcePutInMap = "";// getStringPutSymbolicMap(MAP_NAME,
	// // var, assigmentSymbolic);
	// CtCodeSnippetStatement snippetAssign =
	// factory.Code().createCodeSnippetStatement(sourcePutInMap);
	// ((CtLocalVariableImpl<?>) s).insertAfter(snippetAssign);
	// }
	//
	// // procesar asignaciones
	// else if (s instanceof CtAssignment<?, ?>)
	// {
	// String assigned = ((CtAssignment<?, ?>) s).getAssigned().toString();
	// String assignment = ((CtAssignment<?, ?>) s).getAssignment().toString();
	//
	// String assigmentSymbolic = "";// getSymbolicExpression(assignment);
	//
	// String sourcePutInMap = "";// getStringPutSymbolicMap(SYM_MAP_VARNAME,
	// // assigned, assigmentSymbolic);
	// CtCodeSnippetStatement snippetAssign =
	// factory.Code().createCodeSnippetStatement(sourcePutInMap);
	// ((CtAssignment<?, ?>) s).insertAfter(snippetAssign);
	//
	// }
	//
	// }
	// }

}
