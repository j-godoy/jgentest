package ar.edu.ungs.pps2.jgentest.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.singularsys.jep.ParseException;

import ar.edu.ungs.pps2.jgentest.model.ConcolicExpression;

public class ConcolicExpressionTest
{

	ConcolicExpression	concreteExp;
	Map<String, String>	symbolicMap;

	@Before
	public void inicializarMap()
	{
		symbolicMap = new HashMap<>();
		symbolicMap.put("x", "x0");
		symbolicMap.put("y", "y0");
		symbolicMap.put("z", "z0");
		symbolicMap.put("t", "t0");
		symbolicMap.put("w", "w0");
		symbolicMap.put("x2", "x20");
		// TODO: SOLO DE PRUEBA! EN LOS TEST USO COMO EL METODO Q RECIBE EL NODO
		concreteExp = new ConcolicExpression();
	}

	@Test
	public void getSymbolicExpressionNumber() throws ParseException
	{
		assertEquals("1", concreteExp.getSymbolicExpression("1", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionNumberDouble() throws ParseException
	{
		assertEquals("1", concreteExp.getSymbolicExpression("1.0", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionSumNumber() throws ParseException
	{
		assertEquals("(1)+(74)", concreteExp.getSymbolicExpression("1+74", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionSumNumberNegative() throws ParseException
	{
		assertEquals("(-10)", concreteExp.getSymbolicExpression("-10", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionVariable() throws ParseException
	{
		assertEquals("x0", concreteExp.getSymbolicExpression("x", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionVariablePlusNumber() throws ParseException
	{
		assertEquals("(x0)+(1)", concreteExp.getSymbolicExpression("x+1", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionVariableMinusNum() throws ParseException
	{
		assertEquals("(x0)-(10)", concreteExp.getSymbolicExpression("x-10", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionNumPlusVariable() throws ParseException
	{
		assertEquals("(10)+(x0)", concreteExp.getSymbolicExpression("10+x", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionNumMinusVariable() throws ParseException
	{
		assertEquals("(45)-(x0)", concreteExp.getSymbolicExpression("45-x", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionTestParentesis() throws ParseException
	{
		assertEquals("((1)-(7))*(2)", concreteExp.getSymbolicExpression("(1-7)*2", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionVariableNegative() throws ParseException
	{
		assertEquals("(-x20)", concreteExp.getSymbolicExpression("-x2", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarGTNum() throws ParseException
	{
		assertEquals("(x0)>(2)", concreteExp.getSymbolicExpression("x>2", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarLTNum() throws ParseException
	{
		assertEquals("(x0)<(10)", concreteExp.getSymbolicExpression("x < 10", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarGTVar() throws ParseException
	{
		assertEquals("(x0)>(y0)", concreteExp.getSymbolicExpression("x > y", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarLTNumNeg() throws ParseException
	{
		assertEquals("(x0)<((-102))", concreteExp.getSymbolicExpression("x < -102", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarLENum() throws ParseException
	{
		assertEquals("(x0)<=(15052)", concreteExp.getSymbolicExpression("x <= 15052", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarGENum() throws ParseException
	{
		assertEquals("(x0)>=(210)", concreteExp.getSymbolicExpression("x >= 210", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarGENumNegTRIM() throws ParseException
	{
		assertEquals("(t0)>=((-45))", concreteExp.getSymbolicExpression("t>=-45", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarGENumNeg() throws ParseException
	{
		assertEquals("(x0)>=((-4))", concreteExp.getSymbolicExpression("x >= -4", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalVarGENumWithParen() throws ParseException
	{
		assertEquals("(x0)>=(45)", concreteExp.getSymbolicExpression("((x)>=(45))", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionConditionalComplex() throws ParseException
	{
		assertEquals("(((x0)*(y0))+((4)/((t0)*((y0)+(2)))))>=((45)-(((y0)/(z0))+((5)*(x20))))",
				concreteExp.getSymbolicExpression("(x*y)+4/(t*(y+2)) >= 45-(y/z+5*x2)", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionNumComplex() throws ParseException
	{
		assertEquals("(((((-2))*(8))*(7))+((1)*(7)))-(2)",
				concreteExp.getSymbolicExpression("-2*8*7+1*7-2", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionNumComplex2() throws ParseException
	{
		assertEquals("((7)+((1)*(7)))-(2)", concreteExp.getSymbolicExpression("7+1*7-2", symbolicMap));
	}

	@Test
	public void getSymbolicExpressionVarComplex() throws ParseException
	{
		assertEquals("((5)-((y0)/(2)))-(7)", concreteExp.getSymbolicExpression("5-y/2-7", symbolicMap));
	}

}
