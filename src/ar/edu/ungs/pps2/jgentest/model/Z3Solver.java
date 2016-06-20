package ar.edu.ungs.pps2.jgentest.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Z3Exception;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;
import com.singularsys.jep.parser.Node;

import ar.edu.ungs.pps2.jgentest.utils.JEPUtils;

public class Z3Solver
{
	private Context _ctx;

	public Z3Solver() throws Z3Exception
	{
		this._ctx = new Context();
	}

	public Map<String, Integer> getSatisfiableValues(Collection<SymbCondition> conditionsSymb) throws Z3Exception
	{
		Map<String, Integer> mapValues = new HashMap<String, Integer>();
		Solver solver = this._ctx.mkSolver();
		// for (SymbCondition sc : conditionsSymb)
		Iterator<SymbCondition> allCond = conditionsSymb.iterator();
		while (allCond.hasNext())
		{
			SymbCondition symbCond = allCond.next();
			BoolExpr boolExpr = this.getZ3BoolExpression(symbCond.getCondition());
			solver.add(boolExpr);
		}

		switch (solver.check())
		{
			case UNSATISFIABLE:
				// throw new IllegalArgumentException("Hay código muerto en el
				// método instrumentado!!");
				break;
			case UNKNOWN:
				;
				// throw new IllegalArgumentException("No se pudo determinar la
				// solución!!");
				System.out.println("UNKNOWN solution!!! " + conditionsSymb.toString());
				break;
			case SATISFIABLE:
				Model model = solver.getModel();
				FuncDecl[] constDec = model.getConstDecls();
				for (int i = 0; i < constDec.length; i++)
				{
					String symbVarName = constDec[i].getName().toString();
					ArithExpr symbvar = this._ctx.mkIntConst(symbVarName);
					Integer val = Integer.valueOf(model.getConstInterp(symbvar).toString());
					mapValues.put(symbVarName.substring(0, symbVarName.length() - 1), val);
				}
				break;

			default:
				break;
		}

		return mapValues;
	}

	public BoolExpr getZ3BoolExpression(String expr) throws Z3Exception
	{
		Jep j = new Jep();
		Node n;
		try
		{
			n = j.parse(expr);
		} catch (ParseException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error al parsear la expresión: " + expr);
		}
		return (BoolExpr) makeZ3Expression(n);
	}

	public Expr makeZ3Expression(Node node) throws Z3Exception
	{

		// si es (exp1 != exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkNot(mkEqual(exp1', exp2'))
		if (JEPUtils.isNotEqualsOperator(node))
		{
			Expr leftExpr = makeZ3Expression(node.jjtGetChild(0));
			Expr rigthExpr = makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkNot(_ctx.mkEq(leftExpr, rigthExpr));
		}

		// si es !(exp) llamás recursivamente exp' = makeZ3Expression(exp) y
		// devolves ctx.mkNot(exp')
		if (JEPUtils.isNotOperator(node))
		{
			return _ctx.mkNot((BoolExpr) makeZ3Expression(node.jjtGetChild(0)));
		}

		// si es (exp1 && exp2) llamás recursivamente exp' =
		// makeZ3Expression(exp1) y exp'' = makeZ3Expression(exp2)
		// devolves ctx.mkAnd(exp', exp'')
		else if (JEPUtils.isAndOperator(node))
		{
			Expr leftExpr = makeZ3Expression(node.jjtGetChild(0));
			Expr rigthExpr = makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkAnd((BoolExpr) leftExpr, (BoolExpr) rigthExpr);
		}

		// si es (exp1 || exp2) llamás recursivamente exp' =
		// makeZ3Expression(exp1) y exp'' = makeZ3Expression(exp2)
		// devolves ctx.mkOr(exp', exp'')
		else if (JEPUtils.isOrOperator(node))
		{
			Expr leftExpr = makeZ3Expression(node.jjtGetChild(0));
			Expr rigthExpr = makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkOr((BoolExpr) leftExpr, (BoolExpr) rigthExpr);
		}

		// si es (exp1 == exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkEq(exp1', exp2')
		else if (JEPUtils.isEqualsOperator(node))
		{
			Expr leftExpr = makeZ3Expression(node.jjtGetChild(0));
			Expr rigthExpr = makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkEq(leftExpr, rigthExpr);
		}

		// si es (exp1 > exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkGt(exp1', exp2')
		else if (JEPUtils.isGTOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkGt(leftExpr, rigthExpr);
		}

		// si es (exp1 >= exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkGe(exp1', exp2')
		else if (JEPUtils.isGEOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkGe(leftExpr, rigthExpr);
		}

		// si es (exp1 < exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkLt(exp1', exp2')
		else if (JEPUtils.isLTOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkLt(leftExpr, rigthExpr);
		}

		// si es (exp1 <= exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkLe(exp1', exp2')
		else if (JEPUtils.isLEOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkLe(leftExpr, rigthExpr);
		}

		// si es (exp1 + exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkAdd(exp1', exp2')
		else if (JEPUtils.isPlusOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkAdd(leftExpr, rigthExpr);
		}

		// si es (exp1 - exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkSub(exp1', exp2')
		else if (JEPUtils.isSubOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));

			if (JEPUtils.isNegativeNumberOrVariable(node))
			{
				return _ctx.mkInt(new Integer("-" + leftExpr.getSExpr()));
			}

			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkSub(leftExpr, rigthExpr);
		}

		// si es (exp1 / exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkDiv(exp1', exp2')
		else if (JEPUtils.isDivOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkDiv(leftExpr, rigthExpr);
		}

		// si es (exp1 * exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkMul(exp1', exp2')
		else if (JEPUtils.isMultOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkMul(leftExpr, rigthExpr);
		}

		// si es (exp1 % exp2) haces dos llamados recursivos exp1'=makeZ3(exp1)
		// y exp2'=makeZ3(exp2) y devolvés ctx.mkMod(exp1', exp2')
		else if (JEPUtils.isModOperator(node))
		{
			ArithExpr leftExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(0));
			ArithExpr rigthExpr = (ArithExpr) makeZ3Expression(node.jjtGetChild(1));
			return _ctx.mkMod((IntExpr) leftExpr, (IntExpr) rigthExpr);
		}

		// si es hoja y es numero crear y devolver una IntExpr
		else if (JEPUtils.isNumber(node))
		{
			return _ctx.mkInt(node.getValue().toString());
		}

		// si es hoja y es variable crear y devolver una ArithExpr
		else if (JEPUtils.isVariable(node))
		{
			return _ctx.mkIntConst(node.getVar().getName());
		}

		throw new IllegalArgumentException("La sitaxis '" + node.getName() + "' no está soportada actualmente");
	}

}
