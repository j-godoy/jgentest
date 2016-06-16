package ar.edu.ungs.pps2.jgentest.examples;

import java.util.Map.Entry;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.Operator;
import com.singularsys.jep.parser.Node;
import com.singularsys.jep.walkers.TreeAnalyzer;

public class JEPExample
{

	public static void main(String[] args) throws JepException
	{
		Jep j = new Jep();
		Node n = j.parse("!(x+y > 1)");
		System.out.println("MIN OP: " + n.getOperator().getSymbol());
		System.out.println("child: " + n.jjtGetChild(0));
		System.out.println("EXAMPLE: " + n.jjtGetNumChildren());

		TreeAnalyzer ta = new TreeAnalyzer(n);

		System.out.println("SUM: " + ta.summary());

		ta = new TreeAnalyzer(n.jjtGetChild(0));
		System.out.println("sum2: " + ta.summary());

		// get sorted list of variable names
		String[] vars = ta.getVariableNames();
		for (String v : vars)
		{
			System.out.println("VAR: " + v);
		}
		for (Entry<Operator, Integer> o : ta.getOperators().entrySet())
		{
			System.out.println("K: " + o.getKey());
			// System.out.println("V: " + o.getValue());
		}
		// System.out.println(ta.toString());
		// System.out.println(n.toString());

		if (true)
			return;

	}

}
