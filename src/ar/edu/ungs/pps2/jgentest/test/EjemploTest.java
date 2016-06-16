package ar.edu.ungs.pps2.jgentest.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EjemploTest
{

	@Test
	public void m0_1()
	{
		Ejemplo var = new Ejemplo();
		Integer ret = var.m0(0, 0);
		assertEquals(new Integer(null), ret);
	}

	@Test
	public void m0_2()
	{
		Ejemplo var = new Ejemplo();
		Integer ret = var.m0(3, 1);
		assertEquals(new Integer(null), ret);
	}

	@Test
	public void m0_3()
	{
		Ejemplo var = new Ejemplo();
		Integer ret = var.m0(156, 1);
		assertEquals(new Integer(null), ret);
	}

}