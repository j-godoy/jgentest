package ar.edu.ungs.pps2.jgentest.model;

public class IOSolutionTest
{
	private Object[]	_inputs;
	private Object		_output;

	public IOSolutionTest(Object[] inputs, Object output)
	{
		_inputs = inputs;
		_output = output;
	}

	public Object[] getInputs()
	{
		return _inputs;
	}

	public Object getOutput()
	{
		return _output;
	}
}
