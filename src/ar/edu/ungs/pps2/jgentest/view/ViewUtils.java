package ar.edu.ungs.pps2.jgentest.view;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ViewUtils
{

	public static void alertInformation(String header, String msj)
	{
		// alert(header, msj, AlertType.INFORMATION);
	}

	public static void alertException(String header, Exception ex)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		// alert(header, exceptionText, AlertType.ERROR);

	}

	public static void alertWarning(String header, String msj)
	{
		// alert(header, msj, AlertType.WARNING);
	}

	private static void alert(String header, String msj, AlertType informationType)
	{
		// Alert alert = new Alert(informationType);
		// alert.setTitle("Info!");
		// alert.setHeaderText(header);
		// if (!(msj.length() > 30))
		// alert.setContentText(msj);
		// else
		// {
		// addTextArea(alert, msj);
		// }
		//
		// alert.showAndWait();
	}

	private static void addTextArea(Alert alert, String msj)
	{
		// Label label = new Label("Información detallada:");
		//
		// TextArea textArea = new TextArea(msj);
		// textArea.setEditable(false);
		// textArea.setWrapText(true);
		//
		// textArea.setMaxWidth(Double.MAX_VALUE);
		// textArea.setMaxHeight(Double.MAX_VALUE);
		// GridPane.setVgrow(textArea, Priority.ALWAYS);
		// GridPane.setHgrow(textArea, Priority.ALWAYS);
		//
		// GridPane expContent = new GridPane();
		// expContent.setMaxWidth(Double.MAX_VALUE);
		// expContent.add(label, 0, 0);
		// expContent.add(textArea, 0, 1);
		//
		// // Set expandable Exception into the dialog pane.
		// alert.getDialogPane().setExpandableContent(expContent);
	}
}
