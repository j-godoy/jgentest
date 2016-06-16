package ar.edu.ungs.pps2.jgentest.instrument;

import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

/**
 * Manipula sentencias del estilo a++, --x,...
 * 
 * @author javi
 *
 */
public class UnaryOperatorsUtils
{
	TypeFilter<CtUnaryOperatorImpl<?>> filterUnaryOperators;

	public UnaryOperatorsUtils()
	{
		this.filterUnaryOperators = new TypeFilter<CtUnaryOperatorImpl<?>>(CtUnaryOperatorImpl.class);
	}

	public String getStringParsedUnaryOperator(CtUnaryOperatorImpl<?> unaryOperator)
	{
		String var = unaryOperator.getOperand().toString();
		String operator = "";
		switch (unaryOperator.getKind())
		{
			case POSTINC:
				operator = "+";
				break;
			case PREINC:
				operator = "+";
				break;
			case POSTDEC:
				operator = "-";
				break;
			case PREDEC:
				operator = "-";
				break;

			default:
				return unaryOperator.toString();
		}

		return var + " = " + var + " " + operator + " " + 1;
	}

	public void replaceUnaryOperatorAssignment(CtRHSReceiver<?> element, Factory factory)
	{
		if (containsUnaryOperator(element))
		{
			String newLocalVarImpl = element.toString();
			for (CtUnaryOperatorImpl<?> unaryOperator : element.getAssignment()
					.getElements(this.getFilterUnaryOperator()))
			{
				String parsed = getStringParsedUnaryOperator(unaryOperator);

				CtCodeSnippetStatement snippetUnOperParsed = factory.Code().createCodeSnippetStatement(parsed);

				if (unaryOperator.getKind().equals(UnaryOperatorKind.POSTDEC)
						|| unaryOperator.getKind().equals(UnaryOperatorKind.POSTINC))
				{
					((CtStatement) element).insertAfter(snippetUnOperParsed);

				} else if (unaryOperator.getKind().equals(UnaryOperatorKind.PREDEC)
						|| unaryOperator.getKind().equals(UnaryOperatorKind.PREINC))
				{
					((CtStatement) element).insertBefore(snippetUnOperParsed);
				}
				newLocalVarImpl = newLocalVarImpl.replace(unaryOperator.toString(),
						unaryOperator.getOperand().toString());
			}

			CtCodeSnippetStatement snippetLocalVarParsed = factory.Code().createCodeSnippetStatement(newLocalVarImpl);
			((CtStatement) element).replace(snippetLocalVarParsed);
		}
	}

	public boolean containsUnaryOperator(CtRHSReceiver<?> element)
	{
		CtExpression<?> assignment = element.getAssignment();
		return assignment.getElements(this.getFilterUnaryOperator()).size() > 0;
	}

	public TypeFilter<CtUnaryOperatorImpl<?>> getFilterUnaryOperator()
	{
		return this.filterUnaryOperators;
	}

}
