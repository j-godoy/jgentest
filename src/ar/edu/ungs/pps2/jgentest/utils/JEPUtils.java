package ar.edu.ungs.pps2.jgentest.utils;

import com.singularsys.jep.Operator;
import com.singularsys.jep.parser.Node;

public class JEPUtils
{
	private final static int	NODO_HOJA			= 0;
	private final static int	NODO_UNICO_OPERADOR	= 1;
	private final static String	EQUALS_OPERATOR		= "==";
	private final static String	NOT_EQUALS_OPERATOR	= "!=";
	private final static String	NOT_OPERATOR		= "!";
	private final static String	AND_OPERATOR		= "&&";
	private final static String	OR_OPERATOR			= "||";
	private final static String	GT_OPERATOR			= ">";
	private final static String	GE_OPERATOR			= ">=";
	private final static String	LT_OPERATOR			= "<";
	private final static String	LE_OPERATOR			= "<=";
	private final static String	PLUS_OPERATOR		= "+";
	private final static String	SUB_OPERATOR		= "-";
	private final static String	DIV_OPERATOR		= "/";
	private final static String	MULT_OPERATOR		= "*";
	private final static String	MOD_OPERATOR		= "%";

	public static boolean isHoja(Node node)
	{
		return node.jjtGetNumChildren() == NODO_HOJA;
	}

	public static boolean isNumber(Node node)
	{
		return isHojaAndNumber(node);
	}

	public static boolean isVariable(Node node)
	{
		return !isHojaAndNumber(node);
	}

	public static boolean isHojaAndNumber(Node node)
	{
		// Sólo puede ser una variable o constante
		if (node.jjtGetNumChildren() == NODO_HOJA)
		{
			return node.getVar() == null;
		}

		return false;
	}

	public static boolean isNegativeNumberOrVariable(Node node)
	{
		return node.jjtGetNumChildren() == NODO_UNICO_OPERADOR && node.getOperator() != null
				&& node.getOperator().getSymbol().equals("-");
	}

	public static boolean isNotEqualsOperator(Node node)
	{
		return isOperator(node, NOT_EQUALS_OPERATOR);
	}

	public static boolean isNotOperator(Node node)
	{
		return isOperator(node, NOT_OPERATOR);
	}

	public static boolean isOrOperator(Node node)
	{
		return isOperator(node, OR_OPERATOR);
	}

	public static boolean isAndOperator(Node node)
	{
		return isOperator(node, AND_OPERATOR);
	}

	public static boolean isEqualsOperator(Node node)
	{
		return isOperator(node, EQUALS_OPERATOR);
	}

	public static boolean isGTOperator(Node node)
	{
		return isOperator(node, GT_OPERATOR);
	}

	public static boolean isGEOperator(Node node)
	{
		return isOperator(node, GE_OPERATOR);
	}

	public static boolean isLTOperator(Node node)
	{
		return isOperator(node, LT_OPERATOR);
	}

	public static boolean isLEOperator(Node node)
	{
		return isOperator(node, LE_OPERATOR);
	}

	public static boolean isPlusOperator(Node node)
	{
		return isOperator(node, PLUS_OPERATOR);
	}

	public static boolean isSubOperator(Node node)
	{
		return isOperator(node, SUB_OPERATOR);
	}

	public static boolean isDivOperator(Node node)
	{
		return isOperator(node, DIV_OPERATOR);
	}

	public static boolean isMultOperator(Node node)
	{
		return isOperator(node, MULT_OPERATOR);
	}

	public static boolean isModOperator(Node node)
	{
		return isOperator(node, MOD_OPERATOR);
	}

	private static boolean isOperator(Node node, String oper)
	{
		Operator op = node.getOperator();
		return op != null && op.getSymbol().equals(oper);
	}

}
