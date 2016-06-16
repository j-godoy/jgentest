package ar.edu.ungs.pps2.jgentest.model;

import java.util.Map;

import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;
import com.singularsys.jep.parser.Node;

import ar.edu.ungs.pps2.jgentest.utils.JEPUtils;

public class ConcolicExpression
{

	public String getSymbolicExpression(String expr, Map<String, String> symbMap)
	{
		Jep j = new Jep();
		Node nodeExpr;
		try
		{
			nodeExpr = j.parse(expr);
		} catch (ParseException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("No se pudo parsear la expresión: " + expr);
		}
		return getSymbolicExpression(nodeExpr, symbMap);
	}

	public String getSymbolicExpression(Node node, Map<String, String> mapWithSymbolicVar)
	{
		// Sólo puede ser una variable o constante
		if (JEPUtils.isHoja(node))
		{

			if (JEPUtils.isNumber(node))
			{
				Double number = Double.parseDouble(node.getValue().toString());
				return number.intValue() + "";
			}

			if (JEPUtils.isVariable(node))
			{
				// busca y devuelve el valor simbolico asociado a la variable
				// conExp
				return getSymbolicVar(node.getVar().getName(), mapWithSymbolicVar);
			}
		}

		String operand = node.getOperator().getSymbol();
		Node leftSide = node.jjtGetChild(0);

		if (JEPUtils.isNegativeNumberOrVariable(node))
			return "(" + operand + getSymbolicExpression(leftSide, mapWithSymbolicVar) + ")";

		Node rightSide = node.jjtGetNumChildren() == 2 ? node.jjtGetChild(1) : null;

		return "(" + getSymbolicExpression(leftSide, mapWithSymbolicVar) + ")" + operand + "("
				+ getSymbolicExpression(rightSide, mapWithSymbolicVar) + ")";
	}

	public String getSymbolicVar(String name, Map<String, String> mapWithSymbolicVar)
	{
		return mapWithSymbolicVar.get(name);
	}

}
