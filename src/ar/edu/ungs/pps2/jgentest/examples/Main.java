package ar.edu.ungs.pps2.jgentest.examples;

import ar.edu.ungs.pps2.jgentest.instrument.Instrumentator;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		String path = "/media/javi/DATOS/workspace/Eclipse/TPs_UNGS/GCT/src/test/Ejemplo.java";

		Instrumentator instrument = new Instrumentator(path);
		instrument.showStatements("m2");
	}

}
