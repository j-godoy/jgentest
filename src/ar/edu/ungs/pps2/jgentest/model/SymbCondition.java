package ar.edu.ungs.pps2.jgentest.model;

import java.io.Serializable;

public class SymbCondition implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String				_condition;
	private boolean				_negado;

	public SymbCondition(String condition)
	{
		this._condition = condition;
		this._negado = false;
	}

	public String getCondition()
	{
		return _condition;
	}

	public boolean isNegado()
	{
		return _negado;
	}

	public void setNegado(boolean negado)
	{
		this._negado = negado;
	}

	/**
	 * 
	 * @return Una nueva SymbCondition negando this NO modifica el this
	 */
	public SymbCondition makeNegado()
	{
		if (this._condition.startsWith("!"))
		{
			return new SymbCondition(this._condition.substring(2, this._condition.length() - 1));
		}
		return new SymbCondition("!(" + this._condition + ")");
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_condition == null) ? 0 : _condition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SymbCondition other = (SymbCondition) obj;
		if (_condition == null)
		{
			if (other._condition != null)
				return false;
		} else if (!_condition.equals(other._condition))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		String negado = this._negado ? "[T]" : "[F]";
		return this._condition + negado;
	}

}
