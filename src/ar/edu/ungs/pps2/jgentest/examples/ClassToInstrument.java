package ar.edu.ungs.pps2.jgentest.examples;

public class ClassToInstrument
{

	public boolean example(int x, int y)
	{
		// {"x":"x0"; "y":"y0"}, ret = []
		x = x + 2;
		// {"x":"x0 + 2"; "y":"y0"}

		// if (x < y) ret = [(x0 + 2 < y0)]
		// else ret = [!(x0 + 2 < y0)]
		if (x < y)
		{

			System.out.println("n1 menor que n2");
			return true;
		}
		if (x == y)
			return true;
		int a = 0, b = 2;
		a = a * b;
		return false;
	}

}