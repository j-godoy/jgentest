package ar.edu.ungs.pps2.jgentest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Z3Exception;

import ar.edu.ungs.pps2.jgentest.model.SymbCondition;
import ar.edu.ungs.pps2.jgentest.model.Z3Solver;

public class Z3SolverTest
{

	@Test
	public void getZ3Expression() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		ArithExpr y = ctx.mkIntConst("y");
		IntExpr five = ctx.mkInt(5);
		ArithExpr x_plus_y = ctx.mkAdd(x, y);
		BoolExpr bEsperado = ctx.mkGt(x_plus_y, five);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("x + y > 5");
		assertEquals(bActual.getSExpr(), bEsperado.getSExpr());
	}

	@Test
	public void getZ3Expression2() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		ArithExpr y = ctx.mkIntConst("y");
		BoolExpr bEsperado = ctx.mkLt(x, y);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("x < y");
		assertEquals(bActual.getSExpr(), bEsperado.getSExpr());
	}

	@Test
	public void getZ3Expression3() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		ArithExpr y = ctx.mkIntConst("y");
		BoolExpr bEsperado = ctx.mkGt(x, y);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("x < y");
		assertFalse(bActual.getSExpr().equals(bEsperado.getSExpr()));
	}

	@Test
	public void getZ3Expression4() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		IntExpr three = ctx.mkInt(3);
		BoolExpr bEsperado = ctx.mkEq(x, three);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("x == 3");
		assertEquals(bActual.getSExpr(), bEsperado.getSExpr());
	}

	@Test
	public void getZ3Expression5() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		IntExpr three = ctx.mkInt(3);
		BoolExpr b = ctx.mkEq(x, three);
		BoolExpr bEsperado = ctx.mkNot(b);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("x != 3");
		assertEquals(bActual.getSExpr(), bEsperado.getSExpr());
	}

	@Test
	public void getZ3Expression6() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		ArithExpr y = ctx.mkIntConst("y");
		BoolExpr b = ctx.mkEq(x, y);
		BoolExpr bEsperado = ctx.mkNot(b);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("!(x == y)");
		assertEquals(bActual.getSExpr(), bEsperado.getSExpr());
	}

	@Test
	public void getZ3Expression7() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		ArithExpr y = ctx.mkIntConst("y");
		IntExpr one = ctx.mkInt(1);

		BoolExpr b = ctx.mkGt(x, y);
		BoolExpr b2 = ctx.mkLe(x, one);
		BoolExpr bEsperado = ctx.mkAnd(b, b2);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("((x > y) && (x <= 1))");
		assertEquals(bActual.getSExpr(), bEsperado.getSExpr());
	}

	@Test
	public void getZ3Expression8() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x0");
		ArithExpr y = ctx.mkIntConst("y0");
		IntExpr one = ctx.mkInt(1);
		IntExpr two = ctx.mkInt(2);

		ArithExpr x0_plus_one = ctx.mkAdd(x, one);
		ArithExpr two_mult_x0PlusOne = ctx.mkMul(two, x0_plus_one);
		ArithExpr two_mult_x0PlusOne_plus_y = ctx.mkAdd(two_mult_x0PlusOne, y);
		BoolExpr b = ctx.mkEq(two_mult_x0PlusOne_plus_y, y);
		BoolExpr bEsperado = ctx.mkNot(b);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("(((2)*((x0)+(1)))+(y0))!=(y0)");
		assertEquals(bActual.getSExpr(), bEsperado.getSExpr());
	}

	@Test
	public void getZ3Expression9() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		IntExpr one = ctx.mkInt(-1);
		BoolExpr bEsperado = ctx.mkGt(x, one);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("((x) > (-1))");
		assertEquals(bEsperado.getSExpr(), bActual.getSExpr());
	}

	@Test
	public void getZ3Expression10() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();

		Context ctx = new Context();
		ArithExpr x = ctx.mkIntConst("x");
		ArithExpr y = ctx.mkIntConst("y");
		IntExpr intNum = ctx.mkInt(-150);
		ArithExpr b1 = ctx.mkSub(x, y);
		BoolExpr bEsperado = ctx.mkEq(b1, intNum);

		BoolExpr bActual = (BoolExpr) z3.getZ3BoolExpression("((x)-(y) == (-150))");
		assertEquals(bEsperado.getSExpr(), bActual.getSExpr());
	}

	@Test
	public void getZ3Expression11() throws Z3Exception
	{
		Z3Solver z3 = new Z3Solver();
		List<SymbCondition> conditionsSymb = new ArrayList<>();
		conditionsSymb.add(new SymbCondition("((x0)>(0))"));
		conditionsSymb.add(new SymbCondition("((y0)<(0))"));
		conditionsSymb.add(new SymbCondition("(((y0)+(x0))<(10))"));
		System.out.println(z3.getSatisfiableValues(conditionsSymb));
		assertEquals(2, z3.getSatisfiableValues(conditionsSymb).size());
	}
}
