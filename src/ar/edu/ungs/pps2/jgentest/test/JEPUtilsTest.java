package ar.edu.ungs.pps2.jgentest.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;
import com.singularsys.jep.parser.Node;

import ar.edu.ungs.pps2.jgentest.utils.JEPUtils;

public class JEPUtilsTest
{

	@Test
	public void isNumberOkPositive() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("2");
		assertTrue(JEPUtils.isNumber(nodeExample));
	}

	@Test
	public void isNumberNegative() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("-750");
		assertFalse(JEPUtils.isNumber(nodeExample));
	}

	@Test
	public void isNumberVariable() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("x");
		assertFalse(JEPUtils.isNumber(nodeExample));
	}

	@Test
	public void isNumberExpression() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("x+7*(x-6)");
		assertFalse(JEPUtils.isNumber(nodeExample));
	}

	@Test
	public void isNumberSumNumbers() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("1+1");
		assertFalse(JEPUtils.isNumber(nodeExample));
	}

	@Test
	public void isNumberRestNumbers() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("3-4");
		assertFalse(JEPUtils.isNumber(nodeExample));
	}

	@Test
	public void isNumberZero() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("0");
		assertTrue(JEPUtils.isNumber(nodeExample));
	}

	@Test
	public void isVariable() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("x");
		assertTrue(JEPUtils.isVariable(nodeExample));
	}

	@Test
	public void isNegativeNumberOrVariableOk() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("-x");
		assertTrue(JEPUtils.isNegativeNumberOrVariable(nodeExample));
	}

	@Test
	public void isNegativeNumberOrVariableNum() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("-1");
		assertTrue(JEPUtils.isNegativeNumberOrVariable(nodeExample));
	}

	@Test
	public void isNegativeNumberOrVariableNumPos() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("10");
		assertFalse(JEPUtils.isNegativeNumberOrVariable(nodeExample));
	}

	@Test
	public void isNegativeNumberOrVariableNumPosSymb() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("+10");
		assertFalse(JEPUtils.isNegativeNumberOrVariable(nodeExample));
	}

	@Test
	public void isNegativeNumberOrVariableNot() throws ParseException
	{
		Jep j = new Jep();
		Node nodeExample = j.parse("-x-1");
		assertFalse(JEPUtils.isNegativeNumberOrVariable(nodeExample));
	}

}
