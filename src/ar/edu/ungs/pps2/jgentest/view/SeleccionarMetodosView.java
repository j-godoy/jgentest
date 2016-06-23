package ar.edu.ungs.pps2.jgentest.view;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import ar.edu.ungs.pps2.jgentest.controller.SeleccionarMetodosController;
import ar.edu.ungs.pps2.jgentest.parameters.Parameters;

public class SeleccionarMetodosView extends JFrame
{

	private static final long				serialVersionUID	= 1L;
	private JPanel							contentPane;
	private JTextField						_claseSeleccionada;
	private CheckBoxList					_cbList;
	private JTextField						_profundidadCiclos;
	private JButton							_btnGenerarTest;
	private SeleccionarMetodosController	_controller;
	private static SeleccionarMetodosView	INSTANCE;

	public static SeleccionarMetodosView getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new SeleccionarMetodosView();
		return INSTANCE;
	}

	/**
	 * Create the frame.
	 */
	private SeleccionarMetodosView()
	{
		// Aplico lookAndFeel "Nimbus"
		for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
		{
			if ("Nimbus".equals(laf.getName()))
				try
				{
					UIManager.setLookAndFeel(laf.getClassName());
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
		}

		ImageIcon image = new ImageIcon(Parameters.getProjectPath() + "jgentestPlugin/img/ungs.png");
		setIconImage(image.getImage());
		setFont(new Font("Arial", Font.PLAIN, 12));
		setAlwaysOnTop(true);
		setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		setResizable(false);
		setTitle("JGenTest: Generar casos de Test automáticos");
		setBounds(100, 100, 450, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		_btnGenerarTest = new JButton("Generar Test!");
		_btnGenerarTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_controller.generarCasosDeTest();
			}
		});
		_btnGenerarTest.setBounds(140, 271, 170, 25);
		contentPane.add(_btnGenerarTest);

		_cbList = new CheckBoxList();
		_cbList.setBounds(23, 85, 390, 163);
		contentPane.add(_cbList);

		JLabel lblClaseSeleccionada = new JLabel("Clase seleccionada:");
		lblClaseSeleccionada.setFont(new Font("Arial", Font.PLAIN, 11));
		lblClaseSeleccionada.setBounds(23, 18, 134, 16);
		contentPane.add(lblClaseSeleccionada);

		_claseSeleccionada = new JTextField();
		_claseSeleccionada.setEditable(false);
		_claseSeleccionada.setBounds(167, 12, 238, 28);
		contentPane.add(_claseSeleccionada);
		_claseSeleccionada.setColumns(10);

		JLabel labelProfundidadCiclos = new JLabel("\"Profundidad\" Ciclos:");
		labelProfundidadCiclos.setFont(new Font("Arial", Font.PLAIN, 11));
		labelProfundidadCiclos.setBounds(23, 45, 134, 16);
		contentPane.add(labelProfundidadCiclos);

		_profundidadCiclos = new JTextField();
		_profundidadCiclos.setColumns(10);
		_profundidadCiclos.setBounds(167, 40, 40, 28);
		contentPane.add(_profundidadCiclos);
	}

	/**
	 * Launch the application.
	 */
	public void open(String javaFileName)
	{
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					// SeleccionarMetodos frame = new SeleccionarMetodos();
					// frame.setVisible(true);
					setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
		getContentPane().setLayout(null);

		this._claseSeleccionada.setText(javaFileName);
	}

	@SuppressWarnings("unchecked")
	public void addMetodos(List<String> methodsNames)
	{
		JCheckBox[] listOfCheckBox = new JCheckBox[methodsNames.size()];
		int i = 0;
		for (String M : methodsNames)
		{
			JCheckBox check = new JCheckBox(M);
			listOfCheckBox[i] = check;
			i++;
		}
		_cbList.setListData(listOfCheckBox);
	}

	public CheckBoxList getCbList()
	{
		return _cbList;
	}

	public JTextField getProfundidadCiclos()
	{
		return _profundidadCiclos;
	}

	public JButton getBtnGenerarTest()
	{
		return _btnGenerarTest;
	}

	public void setController(SeleccionarMetodosController seleccionarMetodosController)
	{
		_controller = seleccionarMetodosController;
	}

}
