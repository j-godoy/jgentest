package ar.edu.ungs.pps2.jgentest.instrument;

import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtOperatorAssignmentImpl;

/**
 * Manipula sentencias del estilo a+=2, b*=3+x,...
 * 
 * @author javi
 *
 */
public class OperatorAssignmentUtils
{
	TypeFilter<CtOperatorAssignmentImpl<?, ?>> filterOperatorAssignment;

	public OperatorAssignmentUtils()
	{
		this.filterOperatorAssignment = new TypeFilter<CtOperatorAssignmentImpl<?, ?>>(CtOperatorAssignmentImpl.class);
	}

	public String getStringParsedOperatorAssignment(CtOperatorAssignmentImpl<?, ?> operatorAssignment)
	{
		String var = operatorAssignment.getAssigned().toString();
		String assignment = operatorAssignment.getAssignment().toString();
		String operator = "";
		switch (operatorAssignment.getKind())
		{
			case PLUS:
				operator = "+";
				break;
			case MUL:
				operator = "*";
				break;
			case DIV:
				operator = "/";
				break;
			case MINUS:
				operator = "-";
				break;
			case MOD:
				operator = "%";
				break;
			default:
				break;
		}
		return var + " = " + var + " " + operator + " (" + assignment + ")";
	}

	public TypeFilter<CtOperatorAssignmentImpl<?, ?>> getFilterUnaryOperator()
	{
		return this.filterOperatorAssignment;
	}

}
