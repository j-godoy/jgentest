package ar.edu.ungs.pps2.jgentest.exceptions;

public class LoadSpoonException extends Exception
{

	private static final long serialVersionUID = 1L;

	public LoadSpoonException(String msj, Exception e)
	{
		super(msj + "\n" + e.getMessage());
	}
}
