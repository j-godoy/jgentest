package ar.edu.ungs.pps2.jgentest.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CustomJDialog
{
	public static void show(JFrame frame, String msj, String title, int messageType)
	{
		JDialog dialog = new JDialog(frame);
		dialog.setModal(true);
		dialog.setLocationByPlatform(true);
		dialog.setTitle(title);
		JTextArea txtArea = new JTextArea(10, 50);
		txtArea.setAutoscrolls(true);
		txtArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		txtArea.setFont(new Font("Arial", Font.PLAIN, 12));
		txtArea.setLineWrap(true);
		txtArea.setText(msj);
		txtArea.setForeground(Color.WHITE);
		txtArea.setBackground(Color.GRAY);
		txtArea.setMaximumSize(new Dimension(60, 100));
		txtArea.setEditable(false);
		JScrollPane txtAreaScroll = new JScrollPane();
		txtAreaScroll.setViewportView(txtArea);
		txtAreaScroll.setAutoscrolls(true);
		dialog.add(txtAreaScroll);
		dialog.pack();
		dialog.setVisible(true);
	}

}
