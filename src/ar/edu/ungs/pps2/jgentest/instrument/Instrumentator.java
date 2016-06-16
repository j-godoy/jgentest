package ar.edu.ungs.pps2.jgentest.instrument;

import java.util.List;
import java.util.Set;

import com.singularsys.jep.ParseException;

import ar.edu.ungs.pps2.jgentest.exceptions.LoadSpoonException;
import ar.edu.ungs.pps2.jgentest.functions.RenameVar;
import ar.edu.ungs.pps2.jgentest.model.SpoonedClass;
import ar.edu.ungs.pps2.jgentest.model.SymbCondition;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtDoImpl;
import spoon.support.reflect.code.CtForImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtOperatorAssignmentImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;
import spoon.support.reflect.code.CtWhileImpl;

public class Instrumentator
{

	private final String	SYMB_MAP_VARNAME						= "varSymValMap";
	private final String	SYMB_CONDITIONS_LINKEDHASHSET_VARNAME	= "_conditions";
	private final String	PARAMS_LIST_VARNAME						= "_parameters";
	private final String	CONCOLICEXPR_CLASS_NAME					= "ar.edu.ungs.pps2.jgentest.model.ConcolicExpression";
	private final String	CONCOLICEXPR_VARNAME					= "symbResolver";
	private final String	SYMBCONDITIONAL_CLASS_NAME				= "ar.edu.ungs.pps2.jgentest.model.SymbCondition";

	private Factory			_factory;
	private CtClass<?>		_instrumentedClass;
	SpoonedClass			_spoonedClass;

	/**
	 * inicializa atributos de clase en el parámetro classToInstrument
	 * 
	 * @param classToInstrument
	 * @param factory
	 * @throws LoadSpoonException
	 */
	public Instrumentator(String javaPathFileToinstrument) throws LoadSpoonException
	{
		_spoonedClass = null;

		_spoonedClass = new SpoonedClass(javaPathFileToinstrument);
		_instrumentedClass = _spoonedClass.getSpoonedClass();
		_factory = _spoonedClass.getFactory();
		this.addFieldClass();
	}

	private void addFieldClass()
	{
		// si ya se agregaron las variables, no vuelvo a agregarlas
		if (_instrumentedClass.getField(SYMB_CONDITIONS_LINKEDHASHSET_VARNAME) != null)
		{
			return;
		}

		// Agrego atributo LinkedHashSet<symbCondition> a la clase
		CtType<Set<SymbCondition>> typeSet = _factory.Class()
				.create("java.util.LinkedHashSet<" + SYMBCONDITIONAL_CLASS_NAME + ">");
		CtTypeReference<Set<SymbCondition>> type = _factory.Type().createReference(typeSet);
		CtField<Set<SymbCondition>> snippetFieldListConditions = _factory.Code().createCtField(
				SYMB_CONDITIONS_LINKEDHASHSET_VARNAME, type,
				"new java.util.LinkedHashSet<" + SYMBCONDITIONAL_CLASS_NAME + ">()", ModifierKind.PUBLIC);
		_instrumentedClass.addField(snippetFieldListConditions);

		// Agrego atributo List<String> a la clase para tener los parametros del
		// método
		CtType<List<String>> typeListString = _factory.Class().create("java.util.List<String>");
		CtTypeReference<List<String>> typeList = _factory.Type().createReference(typeListString);
		CtField<List<String>> snippetFieldListSymbVars = _factory.Code().createCtField(PARAMS_LIST_VARNAME, typeList,
				"null", ModifierKind.PUBLIC);
		_instrumentedClass.addField(snippetFieldListSymbVars);

	}

	/**
	 * Instrumenta el método recibido por parámetro.
	 * 
	 * @throws ParseException
	 * @throws NoSuchMethodException
	 *             En caso de no encontrar el método en la clase a instrumentar
	 */
	public void instrumentMethod(String methodName, List<CtTypeReference<?>> typeReferences)
			throws NoSuchMethodException
	{
		CtMethod<?> instrumentedMethod = getCtMethod(methodName, typeReferences);
		this.initializeMethod(instrumentedMethod);
		this.processAsigments(instrumentedMethod);
		this.processConditionalStatements(instrumentedMethod);
	}

	private CtMethod<?> getCtMethod(String methodName, List<CtTypeReference<?>> typeReferences)
			throws NoSuchMethodException
	{
		CtTypeReference<?>[] typeReferencesArray = new CtTypeReference<?>[typeReferences.size()];
		for (int i = 0; i < typeReferences.size(); i++)
		{
			typeReferencesArray[i] = typeReferences.get(i);
		}

		if (_instrumentedClass.getMethod(methodName, typeReferencesArray) == null)
			throw new NoSuchMethodException("No se encontró el método " + methodName);

		return _instrumentedClass.getMethod(methodName, typeReferencesArray);
	}

	/**
	 * Agrega una <tt>LinkedHashSet</tt> como atributo de la clase que contendrá
	 * las condiciones del método con los valores simbólicos de las variables.
	 * Este atributo es public</br>
	 * 
	 * Crea el objeto ConcolicExpression para poder obtener los valores
	 * simbólicos de las asignaciones.</br>
	 * 
	 * Crea un Map para almacenar las variables y sus valores simbólicos,
	 * inicializándolo con las variables recibidas por parámetro en el
	 * método.</br>
	 * 
	 * @param instrumentedMethod
	 */
	private void initializeMethod(CtMethod<?> instrumentedMethod)
	{
		CtCodeSnippetStatement snippetResolver = _factory.Code().createCodeSnippetStatement(
				CONCOLICEXPR_CLASS_NAME + " " + CONCOLICEXPR_VARNAME + "= new " + CONCOLICEXPR_CLASS_NAME + "()");
		instrumentedMethod.getBody().insertBegin(snippetResolver);

		CtCodeSnippetStatement snippetAddParams = _factory.Code()
				.createCodeSnippetStatement(PARAMS_LIST_VARNAME + " = new java.util.ArrayList<String>()");
		instrumentedMethod.getBody().insertBegin(snippetAddParams);

		CtCodeSnippetStatement snippetMap = _factory.Code().createCodeSnippetStatement(
				"java.util.Map<String, String> " + SYMB_MAP_VARNAME + " = new java.util.HashMap<String, String>()");
		snippetResolver.insertAfter(snippetMap);

		// Agrego al map como clave los parámetros y como valor el simbolico de
		// cada parametro. Ej: {x:x0,y:y0}
		// También agrego al set sólo los valores symbólicos: x0, y0.
		for (CtParameter<?> ctp : instrumentedMethod.getParameters())
		{
			String varName = ctp.getSimpleName();
			String symVarName = ctp.getSimpleName().toString() + "0";
			String soucePutInMap = getStringPutSymbolicMap(SYMB_MAP_VARNAME, varName, "\"" + symVarName + "\"");
			CtCodeSnippetStatement snippetMapAsign = _factory.Code().createCodeSnippetStatement(soucePutInMap);
			snippetMap.insertAfter(snippetMapAsign);

			String addSymbVar = PARAMS_LIST_VARNAME + ".add(\"" + varName + "\")";
			CtCodeSnippetStatement snippetAddSymbVar = _factory.Code().createCodeSnippetStatement(addSymbVar);
			snippetResolver.insertBefore(snippetAddSymbVar);
		}
	}

	// preprocesa statements como a++(CtUnaryOperatorImpl),
	// a+=2(CtOperatorAssignmentImpl), a=b++ + c..
	// TODO no funciona para casos como "a = --b - --b;"
	/**
	 * Realiza un preproceso sobre el método deseado para reemplazar las
	 * sentencias que no son identificadas correctamente por Spoon(como a++,
	 * a+=2, a=b++). Reemplazo estas sentencias por sentencias sin
	 * "abreviaturas".Ejemeplos:<br/>
	 * si posee <b>"a++"</b> lo reemplazo por <b>"a = a + 1"</b> <br/>
	 * si posee <b>"a*=2+x"</b> lo reemplazo por <b>"a = a * (2+x)"</b> <br/>
	 * si posee <b>"int a = b++ + x"</b> lo reemplazo por dos sentencias: <b>
	 * "int a = b + x"</b> y <b>"b = b + 1"</b> <br/>
	 * 
	 * <b>IMPORTANTE!</b> No funciona para casos similares a <br/>
	 * <b>"a = --b - --b"</b>
	 * 
	 * @param instrumentedMethod
	 * 
	 * @return true si se modificó el método, es decir, había sentencias que el
	 *         método process no identifica correctamente. <br/>
	 *         false si no se modifica el método.
	 * @throws NoSuchMethodException
	 */
	public boolean preProcess(String methodName, List<CtTypeReference<?>> typeReferences, int k)
			throws NoSuchMethodException
	{
		CtMethod<?> instrumentedMethod = getCtMethod(methodName, typeReferences);
		boolean change = false;
		UnaryOperatorsUtils unaryOperatorUtils = new UnaryOperatorsUtils();

		TypeFilter<CtUnaryOperatorImpl<?>> filterUnaryOperators = unaryOperatorUtils.getFilterUnaryOperator();
		List<CtUnaryOperatorImpl<?>> unaryOperatorsStatements = instrumentedMethod.getElements(filterUnaryOperators);

		// XXXXXXX: Reemplaza a++, ++a, a--, --a
		for (CtUnaryOperatorImpl<?> unOperatorSt : unaryOperatorsStatements)
		{
			String unaryOpParsed = unaryOperatorUtils.getStringParsedUnaryOperator(unOperatorSt);
			// Si no cambió, que continue. Puede ser un "-1" que debe quedar
			// igual
			if (unaryOpParsed.equals(unOperatorSt.toString()))
				continue;

			CtCodeSnippetStatement snippetUnOperParsed = _factory.Code().createCodeSnippetStatement(unaryOpParsed);
			unOperatorSt.replace(snippetUnOperParsed);
			change = true;
		}

		// XXXXXXX: Modifico declaraciones locales. si posee "int a = b++ + x"
		// lo reemplazo por dos sentencias: "int a = b + x" y "b = b + 1"
		TypeFilter<CtLocalVariableImpl<?>> filterLocalVar = new TypeFilter<CtLocalVariableImpl<?>>(
				CtLocalVariableImpl.class);
		List<CtLocalVariableImpl<?>> localVars = instrumentedMethod.getElements(filterLocalVar);

		for (CtLocalVariableImpl<?> localVar : localVars)
		{
			CtExpression<?> assignment = localVar.getAssignment();
			if (assignment.getElements(filterUnaryOperators).size() > 0)
			{
				unaryOperatorUtils.replaceUnaryOperatorAssignment(localVar, _factory);
				change = true;
			}
		}

		// XXXXXXX: Modifico asignaciones. si posee "a = b++ + x"
		// lo reemplazo por dos sentencias: "a = b + x" y "b = b + 1"
		TypeFilter<CtAssignment<?, ?>> filterAssignment = new TypeFilter<CtAssignment<?, ?>>(CtAssignment.class);
		List<CtAssignment<?, ?>> assignments = instrumentedMethod.getElements(filterAssignment);

		for (CtAssignment<?, ?> concreteAssignment : assignments)
		{
			CtExpression<?> assignment = concreteAssignment.getAssignment();
			if (assignment.getElements(filterUnaryOperators).size() > 0)
			{
				unaryOperatorUtils.replaceUnaryOperatorAssignment(concreteAssignment, _factory);
				change = true;
			}
		}

		// XXXXXXX: Reemplaza operadores de asignacion: a+=, a-=, a*=, a/= por
		// "a = a + (...)", "a = a *(...)",...
		OperatorAssignmentUtils operatorAssigUtils = new OperatorAssignmentUtils();
		TypeFilter<CtOperatorAssignmentImpl<?, ?>> filterOperatorAssignment = operatorAssigUtils
				.getFilterUnaryOperator();
		List<CtOperatorAssignmentImpl<?, ?>> operatorAssignments = instrumentedMethod
				.getElements(filterOperatorAssignment);

		for (CtOperatorAssignmentImpl<?, ?> operAssignment : operatorAssignments)
		{
			String operAssignmentParsed = operatorAssigUtils.getStringParsedOperatorAssignment(operAssignment);
			CtCodeSnippetStatement snippetoperAssignmentParsed = _factory.Code()
					.createCodeSnippetStatement(operAssignmentParsed);
			operAssignment.replace(snippetoperAssignmentParsed);
			change = true;
		}

		return change;
	}

	// TODO: add CtForEachImpl
	/**
	 * cambiar While por "k" ifs anidados
	 * 
	 * @param methodName
	 *            nombre del método a instrumentar
	 * @param typeReferences
	 *            tipo de parametros en orden del método a instrumentar
	 * @param k
	 *            cantidad de ifs a reemplazar cada ciclo
	 * @return true si se modificó la clase (porque había un while)
	 * 
	 * @throws NoSuchMethodException
	 *             Si no existe el método según los parámetros recibidos
	 */
	public boolean preProcessLoop(String methodName, List<CtTypeReference<?>> typeReferences, int k)
			throws NoSuchMethodException
	{
		CtMethod<?> instrumentedMethod = getCtMethod(methodName, typeReferences);

		return preProcessFor(instrumentedMethod) || preProcessDoWhile(instrumentedMethod)
				|| preProcessWhile(instrumentedMethod, k);
	}

	/**
	 * Reemplaza los for(int i=0;condition;i++) por un while<br/>
	 * 
	 * <b>int = 0;<br/>
	 * while(condition){<br/>
	 * ...<br/>
	 * i++;<br/>
	 * }<br/>
	 * </b>
	 * 
	 * @param instrumentedMethod
	 * @return true si encontró un for para reemplazar
	 */
	// CtForImpl
	private boolean preProcessFor(CtMethod<?> instrumentedMethod)
	{
		TypeFilter<CtForImpl> filterFor = new TypeFilter<CtForImpl>(CtForImpl.class);
		List<CtForImpl> fors = instrumentedMethod.getElements(filterFor);

		boolean change = !fors.isEmpty();

		if (change == false)
			return change;

		CtForImpl concreteFor = fors.get(0);

		// XXXXX: Las declaraciones del for las agrego antes del for
		List<CtStatement> forInitStatements = concreteFor.getForInit();
		for (CtStatement s : forInitStatements)
		{
			concreteFor.insertBefore(s);
		}

		// XXXXX: Los update del for los agrego al final del cuerpo del for
		List<CtStatement> forUpdateStatements = concreteFor.getForUpdate();
		for (CtStatement s : forUpdateStatements)
		{
			concreteFor.getBody().insertAfter(s);
		}
		String forBody = concreteFor.getBody().toString();
		CtCodeSnippetStatement snippetForBody = _factory.Code().createCodeSnippetStatement(forBody);

		String forExpression = concreteFor.getExpression().toString();
		CtCodeSnippetExpression<Boolean> snippetForExpression = _factory.Code()
				.createCodeSnippetExpression(forExpression);

		// XXXXX: Creo un while, le seteo la misma expression del for, su cuerpo
		// y reemplazo el for por este while
		CtWhile ctWhile = _factory.Core().createWhile();
		ctWhile.setLoopingExpression(snippetForExpression);
		ctWhile.setBody(snippetForBody);
		concreteFor.replace(ctWhile);

		return change;
	}

	/**
	 * Reemplaza los do{S}while(condition) por un while<br/>
	 * 
	 * <b>S<br/>
	 * while(condition){<br/>
	 * S }<br/>
	 * </b>
	 * 
	 * @param instrumentedMethod
	 * @return true si encontró un do While para reemplazar
	 */
	private boolean preProcessDoWhile(CtMethod<?> instrumentedMethod)
	{
		TypeFilter<CtDoImpl> filterFor = new TypeFilter<CtDoImpl>(CtDoImpl.class);
		List<CtDoImpl> doWhiles = instrumentedMethod.getElements(filterFor);

		boolean change = !doWhiles.isEmpty();

		if (change == false)
			return change;

		CtDoImpl concreteDoWhile = doWhiles.get(0);
		concreteDoWhile.insertBefore(concreteDoWhile.getBody());

		CtWhile ctWhile = _factory.Core().createWhile();
		ctWhile.setBody(concreteDoWhile.getBody());
		ctWhile.setLoopingExpression(concreteDoWhile.getLoopingExpression());

		concreteDoWhile.replace(ctWhile);

		return change;
	}

	private boolean preProcessWhile(CtMethod<?> instrumentedMethod, int k)
	{
		TypeFilter<CtWhileImpl> filterWhile = new TypeFilter<CtWhileImpl>(CtWhileImpl.class);
		List<CtWhileImpl> whiles = instrumentedMethod.getElements(filterWhile);

		boolean change = !whiles.isEmpty();

		if (change == false)
			return change;

		CtWhileImpl concreteWhile = whiles.get(0);

		// Sólo en caso que haya declaraciones locales dentro del while tengo
		// que cambiar nombre a las vars locales en los ifs anidados
		TypeFilter<CtLocalVariableImpl<?>> filterLocalVar = new TypeFilter<CtLocalVariableImpl<?>>(
				CtLocalVariableImpl.class);
		List<CtLocalVariableImpl<?>> localVars = concreteWhile.getElements(filterLocalVar);

		if (!localVars.isEmpty())
		{

			TypeFilter<CtLocalVariableReference<?>> filterLocalVarRef = new TypeFilter<CtLocalVariableReference<?>>(
					CtLocalVariableReference.class);
			List<CtLocalVariableReference<?>> localVarsRef = concreteWhile.getElements(filterLocalVarRef);

			for (CtLocalVariableReference<?> ctLocalVariableRef : localVarsRef)
			{
				String varName = ctLocalVariableRef.getSimpleName();

				if (RenameVar.isRenamedInWhile(varName))
					varName = RenameVar.getNextVar(varName);
				else
					varName = varName + RenameVar.ADD_TO_VAR_IN_WHILE + 1;

				ctLocalVariableRef.getDeclaration().setSimpleName(varName);
				ctLocalVariableRef.setSimpleName(varName);
			}
		}
		// changeLocalVarName = true;
		CtExpression<Boolean> loopExpression = concreteWhile.getLoopingExpression();
		CtStatement whileBody = concreteWhile.getBody();

		CtIf ifStatment = _factory.Core().createIf();
		ifStatment.setCondition(loopExpression);
		ifStatment.setThenStatement(whileBody);

		// k-1 porque ya agregue un if
		joinNestedIfs(ifStatment, k - 1, !localVars.isEmpty());

		concreteWhile.replace(ifStatment);
		return change;
	}

	private void joinNestedIfs(CtIf ifStatment, int k, boolean containsLocalVar)
	{
		if (k == 0)
			return;

		String newIfContent = "";
		if (containsLocalVar)
		{
			newIfContent = RenameVar.renameVarsIn(ifStatment.getCondition().toString());
		} else
		{
			newIfContent = ifStatment.getCondition().toString();
		}
		CtCodeSnippetExpression<Boolean> snippetIfCondition = _factory.Code().createCodeSnippetExpression(newIfContent);

		if (containsLocalVar)
		{
			newIfContent = RenameVar.renameVarsIn(ifStatment.getThenStatement().toString());
		} else
		{
			newIfContent = ifStatment.getThenStatement().toString();
		}

		CtCodeSnippetStatement snippetThenBody = _factory.Code().createCodeSnippetStatement(newIfContent);

		CtIf ifStatmentNested = _factory.Core().createIf();
		ifStatmentNested.setCondition(snippetIfCondition);
		ifStatmentNested.setThenStatement(snippetThenBody);

		ifStatment.getThenStatement().insertAfter(ifStatmentNested);
		joinNestedIfs(ifStatmentNested, k - 1, containsLocalVar);
	}

	/**
	 * Instrumenta el método agregando en el código luego de las asignaciones
	 * (incluye declaraciones locales) las sentencias necesarias para guardar en
	 * un Map<String,String> las valores simbólicos de las variables.
	 * 
	 * Ejemplo: "x = 12 + y" se guarda en un map(x:12+y0), siendo y0 el valor
	 * simbólico de 'y' en ese momento
	 * 
	 * @param instrumentedMethod
	 * 
	 * @throws ParseException
	 */
	private void processAsigments(CtMethod<?> instrumentedMethod)
	{

		TypeFilter<CtLocalVariableImpl<?>> filterLocalVar = new TypeFilter<CtLocalVariableImpl<?>>(
				CtLocalVariableImpl.class);
		List<CtLocalVariableImpl<?>> localVars = instrumentedMethod.getElements(filterLocalVar);

		// System.out.println("Declaraciones Locales: " + localVars);
		int i = 0;
		for (CtLocalVariableImpl<?> localVar : localVars)
		{
			i++;
			String var = localVar.getSimpleName();
			String assignment = localVar.getAssignment().toString();

			String varNameToAssignExpression = "symbExprLocalVar_" + i;
			CtCodeSnippetStatement snippetSymbExprLocalVar = _factory.Code().createCodeSnippetStatement(
					getDeclaringStringCondition(varNameToAssignExpression, assignment, SYMB_MAP_VARNAME));
			localVar.insertAfter(snippetSymbExprLocalVar);

			String sourcePutInMap = getStringPutSymbolicMap(SYMB_MAP_VARNAME, var, varNameToAssignExpression);
			CtCodeSnippetStatement snippetAssign = _factory.Code().createCodeSnippetStatement(sourcePutInMap);
			snippetSymbExprLocalVar.insertAfter(snippetAssign);
		}

		TypeFilter<CtAssignment<?, ?>> filterAssignment = new TypeFilter<CtAssignment<?, ?>>(CtAssignment.class);
		List<CtAssignment<?, ?>> assignments = instrumentedMethod.getElements(filterAssignment);

		// System.out.println("Asignaciones: " + assignments);
		int a = 0;
		for (CtAssignment<?, ?> concreteAssignment : assignments)
		{
			a++;
			String assigned = concreteAssignment.getAssigned().toString();
			String assignment = "";
			assignment = concreteAssignment.getAssignment().toString();

			String varNameToAssignExpression = "symbExprAssignment" + a;
			CtCodeSnippetStatement snippetSymbExprAssignment = _factory.Code().createCodeSnippetStatement(
					getDeclaringStringCondition(varNameToAssignExpression, assignment, SYMB_MAP_VARNAME));
			concreteAssignment.insertAfter(snippetSymbExprAssignment);

			String sourcePutInMap = getStringPutSymbolicMap(SYMB_MAP_VARNAME, assigned, varNameToAssignExpression);
			CtCodeSnippetStatement snippetAssign = _factory.Code().createCodeSnippetStatement(sourcePutInMap);
			snippetSymbExprAssignment.insertAfter(snippetAssign);
		}
	}

	/**
	 * Instrumenta el método agregando las condiciones que aparecen en los
	 * ifStatement del método en una lista. Estas son las restricciones
	 * simbólicas.
	 * 
	 * @param instrumentedMethod
	 * 
	 * @throws ParseException
	 */
	private void processConditionalStatements(CtMethod<?> instrumentedMethod)
	{
		TypeFilter<CtIf> filterAssignment = new TypeFilter<CtIf>(CtIf.class);
		List<CtIf> ifStatements = instrumentedMethod.getElements(filterAssignment);

		int n = 0;
		for (CtIf ifSt : ifStatements)
		{
			n++;
			String condition = ifSt.getCondition().toString();

			String varNameToAssignExpression = "symbExprConditional_" + n;
			CtCodeSnippetStatement snippetSymbExpr = _factory.Code().createCodeSnippetStatement(
					getDeclaringSymbolicCondition(varNameToAssignExpression, condition, SYMB_MAP_VARNAME));
			ifSt.insertBefore(snippetSymbExpr);

			CtCodeSnippetStatement snippetIf = _factory.Code()
					.createCodeSnippetStatement("if (" + condition + ") " + SYMB_CONDITIONS_LINKEDHASHSET_VARNAME
							+ ".add(" + varNameToAssignExpression + "); else " + SYMB_CONDITIONS_LINKEDHASHSET_VARNAME
							+ ".add(" + varNameToAssignExpression + ".makeNegado())");
			ifSt.insertBefore(snippetIf);
		}
	}

	private String getStringPutSymbolicMap(String mapName, String varName, String symVarName)
	{
		return mapName + ".put(\"" + varName + "\"," + symVarName + ")";
	}

	private String getStringCallMethodGetSymbolicExpression(String assignment, String mapName)
	{
		return CONCOLICEXPR_VARNAME + ".getSymbolicExpression(\"" + assignment + "\"," + mapName + ")";
	}

	private String getDeclaringSymbolicCondition(String varNameToAssign, String assignment, String mapName)
	{
		return SYMBCONDITIONAL_CLASS_NAME + " " + varNameToAssign + " = new " + SYMBCONDITIONAL_CLASS_NAME + "("
				+ getStringCallMethodGetSymbolicExpression(assignment, mapName) + ")";
	}

	private String getDeclaringStringCondition(String varNameToAssign, String assignment, String mapName)
	{
		return "String " + varNameToAssign + " = " + getStringCallMethodGetSymbolicExpression(assignment, mapName);
	}

	/**
	 * only for test/ debug. No admite sobrecarga de métodos
	 */
	public void showStatements(String methodName)
	{
		CtMethod<?> instrumentedMethod = _instrumentedClass.getMethodsByName(methodName).get(0);
		TypeFilter<CtStatement> f = new TypeFilter<CtStatement>(CtStatement.class);
		List<CtStatement> st = instrumentedMethod.getElements(f);

		for (CtStatement s : st)
		{
			System.out.println("S--> " + s.toString() + " : " + s.getClass());

		}
	}

	public CtClass<?> getInstrumentedClass()
	{
		return _instrumentedClass;
	}

}
