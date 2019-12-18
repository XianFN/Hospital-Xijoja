package es.unileon.xijoja.hospital;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ControlerAdmin implements ActionListener {

	private Logs log;
	private PersonalDAO dao;

	private JTextField textFieldName;
	private JTextField textFieldSurname1;
	private JTextField textFieldSurname2;
	private JTextField textFieldNIFNIE;
	private JTextField textFieldBankAccount;
	private JTextField textFieldEmail;
	private JComboBox<Object> comboBoxJob;
	private JLabel lblUser;
	private JLabel lblPassword;
	private JLabel lblError;

	private int numAdmin;
	private int numDoc;
	private int numNurse;
	private int numSecre;

	private JLabel lblAdministrador;
	private JLabel lblMedico;
	private JLabel lblEnfermero;
	private JLabel lblSecretario;
	private JLabel lblTotal;

	public ControlerAdmin() {
		dao = new PersonalDAO();
		log = new Logs();
	}

	public void setTextFiledsRegister(JTextField textFieldName, JTextField textFieldSurname1,
			JTextField textFieldSurname2, JTextField textFieldNIFNIE, JTextField textFieldBankAccount,
			JTextField textFieldEmail, JLabel lblUser, JLabel lblPassword, JLabel lblError,
			JComboBox<Object> comboBoxJob) {

		this.textFieldName = textFieldName;
		this.textFieldSurname1 = textFieldSurname1;
		this.textFieldSurname2 = textFieldSurname2;
		this.textFieldNIFNIE = textFieldNIFNIE;
		this.textFieldBankAccount = textFieldBankAccount;
		this.textFieldEmail = textFieldEmail;
		this.comboBoxJob = comboBoxJob;
		this.lblError = lblError;
		this.lblPassword = lblPassword;
		this.lblUser = lblUser;

	}

	public void setlblCounts(JLabel lblAdministrador, JLabel lblMedico, JLabel lblEnfermero, JLabel lblSecretario,
			JLabel lblTotal) {

		this.lblAdministrador = lblAdministrador;
		this.lblMedico = lblMedico;
		this.lblEnfermero = lblEnfermero;
		this.lblSecretario = lblSecretario;
		this.lblTotal = lblTotal;
	}

	/**
	 *
	 * @throws SQLException
	 * 
	 *                      Obtenemos el numero de empleados en cada profesion
	 */
	public void setNumberEmployees() throws SQLException {

		numAdmin = 0;
		numDoc = 0;
		numNurse = 0;
		numSecre = 0;
		String[] jobs = dao.getJobsEmployees();
		System.out.println("JOBS " + jobs.length);
		for (int i = 0; i < jobs.length; i++) {
			System.out.print(jobs[i] + "->");
			if (jobs[i].equals("Medico")) {
				numDoc++;
				System.out.println(jobs[i]);
			} else if (jobs[i].equals("Administrador")) {
				numAdmin++;
				System.out.println(jobs[i]);
			} else if (jobs[i].equals("Enfermero")) {
				numNurse++;
				System.out.println(jobs[i]);
			} else if (jobs[i].equals("Secretario")) {
				numSecre++;
				System.out.println(jobs[i]);
			}

		}

	}

	/**
	 * 
	 * @param name
	 * @param surname1
	 * @param surname2
	 * @return
	 * 
	 * 		Generamos un usuario con el nombre y apellidos pasados por parametro
	 */
	private String genUser(String name, String surname1, String surname2) {

		StringBuilder sbName = new StringBuilder();// Formamos el nombre de usuario

		sbName.append(name.charAt(0));// Primera letra del nombre

		sbName.append(surname1.charAt(0));
		sbName.append(surname1.charAt(1));// Dos primeras letras del primer apellido
		sbName.append(surname2.charAt(0));
		sbName.append(surname2.charAt(1));// Dos primeras letras del segundo apellido

		String[] names = null;

		try {
			names = dao.getNamesEmployees();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		for (int i = 0; i < names.length; i++) {
			System.out.println(i + ": " + names[i]);
		}

		int numberOfUser = 0;
		for (int i = 1; i < names.length; i++) {// Vamos comprobando nombre por nombre

			char[] nameBUffer = names[i].toCharArray();
			char[] secondBuffer = new char[5];

			for (int j = 0; j < 5; j++) {
				System.out.println(j);
				secondBuffer[j] = nameBUffer[j];// Quitamos los numero del nombre de usuario
			}

			if (String.valueOf(secondBuffer).equals(sbName.toString().toLowerCase())) {

				numberOfUser++;// Contamos los ususarios con el mismo nombre y aniadimos un numero para que no
								// se repita

			}

		}
		sbName.append(numberOfUser);// Aniadimos el numero
		log.InfoLog("[ADMINWINDOW]:Usuario " + sbName.toString().toLowerCase() + " generado correctamente");
		return sbName.toString().toLowerCase();

	}

	/**
	 * 
	 * @return
	 * 
	 * 		Generamos una contraseña aleatoria
	 */
	private String genPassword() {

		String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder password = new StringBuilder();

		int i = 0;
		while (i < 7) {
			int rand = (int) ((Math.random() * ((61 - 0) + 1)));// Con el random generamos la contraseña sacando los
																// careacteres del array alphabet

			password.append(alphabet.charAt(rand));

			i++;
		}

		return password.toString();
	}

	String[] employeeToEdit = null;

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent arg0) {

		if (arg0.getActionCommand().equals("Registrar")) {////////////////////////////////// REGISTRAR

			// log.InfoLog("Se ha pulsado el boton de registrar");

			boolean add = true;

			if ((textFieldName.getText().equals("")) || (textFieldSurname1.getText().equals(""))
					|| (textFieldSurname2.getText().equals("")) || (textFieldNIFNIE.getText().equals(""))
					|| (textFieldBankAccount.getText().equals("")) || (textFieldEmail.getText().equals(""))) {// Comprobamos
				// si algum
				// campo esta
				// vacio

				add = false;
				lblError.setText("Hay campos vacios");
				// log.InfoLog("Hay campos vacios");
			} else {
				lblError.setText("");
			}

			if (add) {// Si da error no se añade el empleado

				lblUser.setText(
						genUser(textFieldName.getText(), textFieldSurname1.getText(), textFieldSurname1.getText()));
				lblPassword.setText(genPassword());

				int id = dao.getLastID();

				Date date = new Date(Calendar.getInstance().getTime().getTime());// Obtenemos la fecha actual

				try {

					dao.addEmployee(id, textFieldName.getText(), textFieldSurname1.getText(),
							textFieldSurname2.getText(), textFieldNIFNIE.getText(), date,
							textFieldBankAccount.getText(), comboBoxJob.getSelectedItem().toString(),
							lblPassword.getText(), lblUser.getText(), textFieldEmail.getText());// LLamamos a la
																								// funcion del DAO
																								// que inserta el
																								// empleado
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				try {
					setNumberEmployees();
					lblAdministrador.setText(String.valueOf(numAdmin));
					lblMedico.setText(String.valueOf(numDoc));
					lblEnfermero.setText(String.valueOf(numNurse));
					lblSecretario.setText(String.valueOf(numSecre));
					lblTotal.setText(String.valueOf(numAdmin + numDoc + numNurse + numSecre));
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}

			log.InfoLog("Usuario + " + lblUser.getText() + " añadido correctamente");

		}/* else if (arg0.getActionCommand().equals("Añadir trabajador")) {///////////////////////////////// ADD

			seeEmployeesPanel.setVisible(false);
			addEmployeePane.setVisible(true);
			editEmployeesPanel.setVisible(false);
			btnVerPlantilla.setText("Ver plantilla");
			enableAllEdit(false);

		} else if ((arg0.getActionCommand().equals("Ver plantilla")) || (arg0.getActionCommand().equals("Recargar"))) {

			seeEmployeesPanel.setVisible(true);
			addEmployeePane.setVisible(false);
			editEmployeesPanel.setVisible(false);
			btnVerPlantilla.setText("Recargar");

			ArrayList<String[]> insert = null;

			String[] titles = null;

			String[][] matrixToInsert = null;

			try {
				titles = new String[] { "  Id", "Nombre", "Apellido 1", "Apellido 2", "NIF", "Fecha", "Cuenta Bancaria",
						"Puesto", "Contrase�a", "Usuario", "Email" }; // Titulos de la tabla de
																		// los empleados
				insert = dao.getAllEmployees();// ArrayList de Arrays
				System.out.println("Size " + insert.size());
				matrixToInsert = new String[insert.size() + 1][11];
				seeEmployeesPanel.setPreferredSize(new Dimension(624, 20 + 20 * insert.size()));
				seeEmployeesPanel.setBounds(284, 11, 624, 20 + 20 * insert.size());

				for (int i = 0; i < insert.size(); i++) { // rellenamos la matriz que meteremos en la tabla a partir
															// del ArrayList de arrays devuelto del DAO
					for (int j = 0; j < 11; j++) {
						if (i == 0) {

							matrixToInsert[i][j] = titles[j];

						} else {
							matrixToInsert[i][j] = insert.get(i)[j];
						}
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			JTable employeesTable = new JTable();
			employeesTable.setBounds(20, 20, 600, 20 + 20 * insert.size());

			employeesTable.setVisible(true);
			seeEmployeesPanel.add(employeesTable);
			/*
			 * //TODO lo que hizo xian es esto que esta comentado panelquebaja = new
			 * JScrollPane(seeEmployeesPanel);
			 *
			 * panelquebaja.setHorizontalScrollBarPolicy(JScrollPane.
			 * HORIZONTAL_SCROLLBAR_NEVER);
			 * panelquebaja.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
			 * ); panelquebaja.setBounds(seeEmployeesPanel.getBounds());
			 * System.out.println(panelquebaja.getBounds());
			 * getContentPane().add(panelquebaja);
			 *
			 * panelquebaja.setVisible(true);
			 */
		/*
			employeesTable.setAutoscrolls(true);

			System.out.println("VIVA DROTIUM");
			DefaultTableModel tableModel = new DefaultTableModel(matrixToInsert, titles);
			employeesTable.setModel(tableModel);
			
		} else if (arg0.getActionCommand().equals("Editar trabajador")) {

			seeEmployeesPanel.setVisible(false);
			addEmployeePane.setVisible(false);
			editEmployeesPanel.setVisible(true);
			btnVerPlantilla.setText("Ver plantilla");
			enableAllEdit(false);

		} else if (arg0.getActionCommand().equals("Buscar")) {
			// TODO saltar fallo si el dni no existe o el campo esta vacio
			if (Character.isDigit(textFieldSearch.getText().charAt(0))) {
				enableAllEdit(true);
				System.out.println("Busqueda por DNI");

				try {
					employeeToEdit = dao.getEmployee(textFieldSearch.getText().toString());

				} catch (SQLException e) {

					e.printStackTrace();
				}

				textFieldNameEdit.setText(employeeToEdit[1]);
				textFieldSurname1Edit.setText(employeeToEdit[2]);
				textFieldSurname2Edit.setText(employeeToEdit[3]);
				textFieldDNIEdit.setText(employeeToEdit[4]);
				textFieldBankEdit.setText(employeeToEdit[6]);
				comboBoxJobEdit.setSelectedItem(employeeToEdit[7]);
				textFieldEmailEdit.setText(employeeToEdit[10]);
				labelUserNameEdit.setText(employeeToEdit[9]);

			} else {
				// TODO hacer esta busqueda
				System.out.println("Busqueda por nombre y apellidos");
			}

		} else if (arg0.getActionCommand().equals("Guardar")) {

			System.out.println("Comenzamos a editar el trabajador");
			boolean out = false;

			while (!out) {// Repetimos hasta que se cancela o se introduce la contraseña correcta
				JPasswordField pf = new JPasswordField();
				int option = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				System.out.println(pf.getText());

				if (option == JOptionPane.CANCEL_OPTION) {// si se pulsa cancelar
					out = true;
					System.out.println("Se ha cancelado");
				} else {

					if (pf.getText().equals(password)) {// Si se acierta la contraseña
						out = true;

						labelUserNameEdit.setText(genUser(textFieldNameEdit.getText(), textFieldSurname1Edit.getText(),
								textFieldSurname2Edit.getText()));// Generamos el
																	// nuevo usuario

						try {
							dao.editEmployee(Integer.parseInt(employeeToEdit[0]), textFieldNameEdit.getText(),
									textFieldSurname1Edit.getText(), textFieldSurname2Edit.getText(),
									textFieldDNIEdit.getText(), textFieldBankEdit.getText(),
									comboBoxJobEdit.getSelectedItem().toString(), labelUserNameEdit.getText(),
									textFieldEmailEdit.getText());// Llamamos a editar del DAO
						} catch (NumberFormatException | SQLException e) {

							e.printStackTrace();
						}

					} else {// Si se falla la contraseña
						JOptionPane.showMessageDialog(null, "Contraseña incorrecta", "ERROR",
								JOptionPane.ERROR_MESSAGE);
					}
				}
*/
			}

		//}
	}
//}
