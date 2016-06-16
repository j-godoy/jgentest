package ar.edu.ungs.pps2.jgentest.test;

public class Ejemplo
{

	public Integer toTest(int x, int y)
	{
		x = x + 3;
		if (x >= y)
			if (x > 10)
				return x;
		return y;
	}

	public Integer toTest2(int x)
	{
		int y = 0;
		while (y < x)
			y++;
		return y;
	}

	public void m1(int x, int y)
	{
		if (x > 2)
		{
			y = x + 1;
		} else
		{
			y = y + 1;
		}

		for (int i = 0; i < 10; i++)
		{
			if (x <= 2)
			{
				y = y + 1;
			} else
			{
				x = x + 2;
			}
		}
	}

	public Integer m(int x, int y)
	{
		x = x * 3;
		if (x == y)
		{
			x = x + y;
			if (x >= 100 && y > 0)
				return x;
		}
		if (x % 2 == 0)
			return x;
		return y;
	}

	public Integer m0(int x, int y)
	{
		while (x > 2 && y > 0)
		{
			x--;
			int i = x + 1;
			i %= 4;
			while (i < 5)
			{
				i++;
			}
		}
		return x;
	}

	public void m2(int x)
	{
		if (x < 5)
		{
			if (x < 3)
			{
				do
				{
					x--;
					for (int i = 0; i <= x; i++)
					{
						;
					}
				} while (x > 0);
			}
		}
	}

	public Integer getMax(int x, int y, int z)
	{
		Integer max = Integer.MIN_VALUE;
		if (x > y && x > z)
			max = x;
		else if (y > x && y > z)
			max = y;
		else if (z > y && z > x)
			max = z;
		else
			return x;
		return max;
	}

	// Lo uso para test, no borrar!
	public Integer t1(int x)
	{
		if (x > 0)
			return 1;
		return 0;
	}
}
