package ar.edu.ungs.pps2.jgentest.examples;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class Z3Example
{

	public static void main(String[] args) throws Z3Exception
	{
		Context ctx = new Context();
		Solver s = ctx.mkSolver();

		// Sort int_type = ctx.getIntSort();
		// Expr i1 = ctx.mkConst("i1", int_type);
		ArithExpr x = ctx.mkIntConst("x");
		ArithExpr y = ctx.mkIntConst("y");
		// ArithExpr z = ctx.mkIntConst("5");
		IntExpr five = ctx.mkInt(5);
		IntExpr one = ctx.mkInt(1);

		// x = Real('x')
		// y = Real('y')
		// s = Solver()
		// s.add(x + y > 5, x > 1, y > 1)
		// print s.check()
		// print s.model()
		// s.add(x + y > 5, x > 1, y > 1)

		ArithExpr x_plus_y = ctx.mkAdd(x, y);
		BoolExpr b1 = ctx.mkGt(x_plus_y, five);
		BoolExpr b2 = ctx.mkGt(x, one);
		BoolExpr b3 = ctx.mkGt(y, one);
		s.add(b1, b2, b3);

		System.out.println(s.check());
		if (s.check() == Status.SATISFIABLE)
		{
			Model model = s.getModel();
			// System.out.println("MODEL \n"+model);
			System.out.println("x = " + model.getConstInterp(x));
			System.out.println("y = " + model.getConstInterp(y));
		}

		// (x0)==(y0)

		ArithExpr x0 = ctx.mkIntConst("x0");
		ArithExpr y0 = ctx.mkIntConst("y0");
		BoolExpr c1 = ctx.mkEq(x0, y0);
		System.out.println(c1.getSExpr());

	}

}
