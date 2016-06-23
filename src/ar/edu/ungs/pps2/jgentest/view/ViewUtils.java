package ar.edu.ungs.pps2.jgentest.view;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ViewUtils
{

	public static void alertInformation(String header, String msj, JFrame frame)
	{
		alert(header, msj, JOptionPane.INFORMATION_MESSAGE, frame);
	}

	public static void alertException(String header, Exception ex, JFrame frame)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		alert(header, exceptionText, JOptionPane.ERROR_MESSAGE, frame);
	}

	public static void alertWarning(String header, String msj, JFrame frame)
	{
		alert(header, msj, JOptionPane.WARNING_MESSAGE, frame);
	}

	private static void alert(String header, String msj, int messageType, JFrame frame)
	{
		// JOptionPane.showMessageDialog(frame, msj, header, messageType);
		CustomJDialog.show(frame, msj, header, messageType);
	}

}
