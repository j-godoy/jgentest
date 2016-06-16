package ar.edu.ungs.pps2.jgentest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ar.edu.ungs.pps2.jgentest.functions.RenameVar;

public class RenameVarTest extends RenameVar
{

	@Test
	public void testIsRenamed1()
	{
		String var = "i";
		assertFalse(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed2()
	{
		String var = "i_";
		assertFalse(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed3()
	{
		String var = "i" + RenameVar.ADD_TO_VAR_IN_WHILE;
		assertFalse(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed4()
	{
		String var = "i" + RenameVar.ADD_TO_VAR_IN_WHILE + "_2";
		assertFalse(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed5()
	{
		String var = "i" + RenameVar.ADD_TO_VAR_IN_WHILE + "1";
		assertTrue(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed6()
	{
		String var = "i" + RenameVar.ADD_TO_VAR_IN_WHILE + "123";
		assertTrue(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed7()
	{
		String var = "i" + RenameVar.ADD_TO_VAR_IN_WHILE + "123" + RenameVar.ADD_TO_VAR_IN_WHILE + "12";
		assertTrue(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed8()
	{
		String var = RenameVar.ADD_TO_VAR_IN_WHILE + "123";
		assertFalse(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed9()
	{
		String var = "c" + RenameVar.ADD_TO_VAR_IN_WHILE + RenameVar.ADD_TO_VAR_IN_WHILE + "123";
		assertTrue(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testIsRenamed10()
	{
		String var = "12" + RenameVar.ADD_TO_VAR_IN_WHILE + "1r2e3q";
		assertFalse(RenameVar.isRenamedInWhile(var));
	}

	@Test
	public void testGetNextVar1()
	{
		String var = "c" + RenameVar.ADD_TO_VAR_IN_WHILE + "1";
		assertEquals("c" + RenameVar.ADD_TO_VAR_IN_WHILE + "2", RenameVar.getNextVar(var));
	}

	@Test
	public void testGetNextVar2()
	{
		String var = "c" + RenameVar.ADD_TO_VAR_IN_WHILE + "_321";
		assertEquals("c" + RenameVar.ADD_TO_VAR_IN_WHILE + "_321", RenameVar.getNextVar(var));
	}

	@Test
	public void testGetNextVar3()
	{
		String var = "varName" + RenameVar.ADD_TO_VAR_IN_WHILE + "23";
		assertEquals("varName" + RenameVar.ADD_TO_VAR_IN_WHILE + "24", RenameVar.getNextVar(var));
	}

}
