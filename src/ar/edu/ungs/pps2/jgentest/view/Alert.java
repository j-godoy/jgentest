package ar.edu.ungs.pps2.jgentest.view;

import java.awt.Container;

import javax.swing.JDialog;

public class Alert extends JDialog
{
	private Container contenedor;

	public Alert()
	{
		contenedor = getContentPane();
		contenedor.setLayout(null);
		// Asigna un titulo a la barra de titulo
		setTitle("CoDejaVu : Titulo Del JDialog");
		// tamaño de la ventana
		setSize(300, 200);
		// pone la ventana en el Centro de la pantalla
		setLocationRelativeTo(null);
	}
}