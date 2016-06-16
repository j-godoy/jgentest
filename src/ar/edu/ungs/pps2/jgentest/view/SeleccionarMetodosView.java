package ar.edu.ungs.pps2.jgentest.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import ar.edu.ungs.pps2.jgentest.controller.SeleccionarMetodosController;

public class SeleccionarMetodosView extends JFrame
{

	private static final long				serialVersionUID	= 1L;
	private JPanel							contentPane;
	private JTextField						_claseSeleccionada;
	private CheckBoxList					_cbList;
	private JLabel							_labelClaseSeleccionada;
	private JTextField						_profundidadCiclos;
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
		setResizable(false);
		setTitle("JGenTest: Generar casos de Test automáticos");
		setBounds(100, 100, 450, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnGenerarTest = new JButton("Generar Test!");
		btnGenerarTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_controller.generarCasosDeTest();
			}
		});
		btnGenerarTest.setBounds(140, 271, 138, 23);
		contentPane.add(btnGenerarTest);

		_cbList = new CheckBoxList();
		_cbList.setBounds(23, 85, 382, 163);
		contentPane.add(_cbList);

		JLabel lblClaseSeleccionada = new JLabel("Clase seleccionada:");
		lblClaseSeleccionada.setFont(new Font("Arial", Font.BOLD, 11));
		lblClaseSeleccionada.setBounds(23, 18, 120, 16);
		contentPane.add(lblClaseSeleccionada);

		_claseSeleccionada = new JTextField();
		_claseSeleccionada.setEditable(false);
		_claseSeleccionada.setBounds(167, 12, 238, 28);
		contentPane.add(_claseSeleccionada);
		_claseSeleccionada.setColumns(10);

		_labelClaseSeleccionada = new JLabel("\"Profundidad\" Ciclos:");
		_labelClaseSeleccionada.setFont(new Font("Arial", Font.BOLD, 11));
		_labelClaseSeleccionada.setBounds(23, 46, 120, 16);
		contentPane.add(_labelClaseSeleccionada);

		_profundidadCiclos = new JTextField();
		_profundidadCiclos.setColumns(10);
		_profundidadCiclos.setBounds(167, 40, 58, 28);
		contentPane.add(_profundidadCiclos);
	}

	/**
	 * Launch the application.
	 */
	public void open(String javaFileName)
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
				}
		}

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

	public void setController(SeleccionarMetodosController seleccionarMetodosController)
	{
		_controller = seleccionarMetodosController;
	}

}