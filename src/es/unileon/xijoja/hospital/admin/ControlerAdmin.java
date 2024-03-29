package es.unileon.xijoja.hospital.admin;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import es.unileon.xijoja.hospital.Logs;
import es.unileon.xijoja.hospital.PacientesDAO;
import es.unileon.xijoja.hospital.PersonalDAO;
import es.unileon.xijoja.hospital.login.ControlerLoginWindow;
import es.unileon.xijoja.hospital.login.LoginWindow;

public class ControlerAdmin implements ActionListener {

	private Logs log;
	private PersonalDAO personalDao;
	private PacientesDAO patientsDao;
	private AdminWindow adminWindow;
	private ArrayList<String[]> arrayNurse, arrayMedic;
	protected int numAdmin;
	protected int numDoc;
	protected int numNurse;
	protected int numSecre;
	String[] employeeToEdit = null;

	public ControlerAdmin(AdminWindow adminWindow) {
		personalDao = new PersonalDAO();
		patientsDao = new PacientesDAO();
		log = new Logs();
		this.adminWindow = adminWindow;
	}

	/**
	 *
	 * @throws SQLException
	 * 
	 *                      Obtenemos el numero de empleados en cada profesion
	 */
	public void setNumberEmployees() {

		numAdmin = 0;
		numDoc = 0;
		numNurse = 0;
		numSecre = 0;
		String[] jobs = personalDao.getJobsEmployees();
		for (int i = 0; i < jobs.length; i++) {
			if (jobs[i].equals("Medico")) {
				numDoc++;
			} else if (jobs[i].equals("Administrador")) {
				numAdmin++;
			} else if (jobs[i].equals("Enfermero")) {
				numNurse++;
			} else if (jobs[i].equals("Secretario")) {
				numSecre++;
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
	 *         Generamos un usuario con el nombre y apellidos pasados por parametro
	 */
	private String genUser(String name, String surname1, String surname2) {

		StringBuilder sbName = new StringBuilder();// Formamos el nombre de usuario

		sbName.append(name.charAt(0));// Primera letra del nombre

		sbName.append(surname1.charAt(0));
		sbName.append(surname1.charAt(1));// Dos primeras letras del primer apellido
		sbName.append(surname2.charAt(0));
		sbName.append(surname2.charAt(1));// Dos primeras letras del segundo apellido

		String[] names = null;

		names = personalDao.getNamesEmployees();

		int numberOfUser = 0;
		for (int i = 1; i < names.length; i++) {// Vamos comprobando nombre por nombre

			char[] nameBUffer = names[i].toCharArray();
			char[] secondBuffer = new char[5];

			for (int j = 0; j < 5; j++) {

				secondBuffer[j] = nameBUffer[j];// Quitamos los numero del nombre de usuario
			}

			if (String.valueOf(secondBuffer).equals(sbName.toString().toLowerCase())) {

				numberOfUser++;// Contamos los ususarios con el mismo nombre y aniadimos un numero para que no
								// se repita

			}

		}
		sbName.append(numberOfUser);// Aniadimos el numero
		log.InfoLog("Usuario " + sbName.toString().toLowerCase() + " generado correctamente");
		return sbName.toString().toLowerCase();

	}

	/**
	 * 
	 * @return
	 * 
	 *         Generamos una contraseña aleatoria
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

	/**
	 * 
	 * @param state
	 * 
	 *              Metodo que permite activar o desactivar los componentes de
	 *              editar empleado, de esta forma ahorramos tiempo de ponerlo cada
	 *              vez que lo necesitamos
	 */
	@SuppressWarnings("deprecation")
	private void enableAllEdit(boolean state) {

		adminWindow.textFieldNameEdit.enable(state);
		adminWindow.textFieldSurname1Edit.enable(state);
		adminWindow.textFieldSurname2Edit.enable(state);
		adminWindow.textFieldDNIEdit.enable(state);
		adminWindow.textFieldBankEdit.enable(state);
		adminWindow.textFieldEmailEdit.enable(state);
		adminWindow.comboBoxJobEdit.enable(state);
		adminWindow.btnSaveEdit.enable(state);

		if (!state) {// Si es falso borramos las string que habian anteriormente

			adminWindow.textFieldSearchDNIEdit.setText("");
			adminWindow.textFieldNameEdit.setText("");
			adminWindow.textFieldSurname1Edit.setText("");
			adminWindow.textFieldSurname2Edit.setText("");
			adminWindow.textFieldDNIEdit.setText("");
			adminWindow.textFieldBankEdit.setText("");
			adminWindow.textFieldEmailEdit.setText("");

		}

	}

	public void filJComboBox(JComboBox edit, boolean ismedic) {

		ArrayList<String[]> list = personalDao.getNuseAndMedic(ismedic);
		if (ismedic) {
			arrayMedic = list;
		} else {
			arrayNurse = list;
		}

		String[] data = new String[2];
		if (list == null) {

		} else {
			for (int i = 0; i < list.size(); i++) {
				data = list.get(i);
				edit.addItem(data[1]);

			}
		}

	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent arg0) {

		if (arg0.getActionCommand().equals("Registrar")) {////////////////////////////////// REGISTRAR

			// log.InfoLog("Se ha pulsado el boton de registrar");

			boolean add = true;

			if ((adminWindow.textFieldName.getText().equals("")) || (adminWindow.textFieldSurname1.getText().equals(""))
					|| (adminWindow.textFieldSurname2.getText().equals(""))
					|| (adminWindow.textFieldDNI.getText().equals(""))
					|| (adminWindow.textFieldBankAccount.getText().equals(""))
					|| (adminWindow.textFieldEmail.getText().equals(""))) {// Comprobamos
				// si algum
				// campo esta
				// vacio

				add = false;
				adminWindow.lblError.setText("Hay campos vacios");
				// log.InfoLog("Hay campos vacios");
			} else {
				adminWindow.lblError.setText("");
			}

			if (add) {// Si da error no se añade el empleado

				adminWindow.lblUser.setText(genUser(adminWindow.textFieldName.getText(),
						adminWindow.textFieldSurname1.getText(), adminWindow.textFieldSurname1.getText()));
				adminWindow.lblPassword.setText(genPassword());

				int id = personalDao.firstIdFree();// siguiente id

				Date date = new Date(Calendar.getInstance().getTime().getTime());// Obtenemos la fecha actual

				personalDao.addEmployee(id, adminWindow.textFieldName.getText(),
						adminWindow.textFieldSurname1.getText(), adminWindow.textFieldSurname2.getText(),
						adminWindow.textFieldDNI.getText(), date, adminWindow.textFieldBankAccount.getText(),
						adminWindow.comboBoxJob.getSelectedItem().toString(), adminWindow.lblPassword.getText(),
						adminWindow.lblUser.getText(), adminWindow.textFieldEmail.getText());// LLamamos a la
				// funcion del DAO que inserta el empleado

				setNumberEmployees();
				adminWindow.lblAdministrador.setText(String.valueOf(numAdmin));
				adminWindow.lblMedico.setText(String.valueOf(numDoc));
				adminWindow.lblEnfermero.setText(String.valueOf(numNurse));
				adminWindow.lblSecretario.setText(String.valueOf(numSecre));
				adminWindow.lblTotal.setText(String.valueOf(numAdmin + numDoc + numNurse + numSecre));

			}

			log.InfoLog("Usuario + " + adminWindow.lblUser.getText() + " añadido correctamente");

		} else if (arg0.getActionCommand().equals("Añadir trabajador")) {///////////////////////////////// ADD

			adminWindow.seeEmployeesPanel.setVisible(false);
			adminWindow.addEmployeePanel.setVisible(true);
			adminWindow.editEmployeesPanel.setVisible(false);
			adminWindow.btnVerPlantilla.setText("Ver plantilla");
			adminWindow.deletePanel.setVisible(false);
			enableAllEdit(false);
			adminWindow.lblError.setText("");
			adminWindow.lblErrorDelete.setText("");
			adminWindow.lblErrorEdit.setText("");
			adminWindow.addPatientsPanel.setVisible(false);
			adminWindow.seePacientsPanel.setVisible(false);

		} else if ((arg0.getActionCommand().equals("Ver plantilla")) || (arg0.getActionCommand().equals("Recargar"))) {

			adminWindow.seeEmployeesPanel.setVisible(true);
			adminWindow.addEmployeePanel.setVisible(false);
			adminWindow.editEmployeesPanel.setVisible(false);
			adminWindow.btnVerPlantilla.setText("Recargar");
			adminWindow.deletePanel.setVisible(false);
			adminWindow.lblError.setText("");
			adminWindow.lblErrorDelete.setText("");
			adminWindow.lblErrorEdit.setText("");
			adminWindow.addPatientsPanel.setVisible(false);
			adminWindow.seePacientsPanel.setVisible(false);

			ArrayList<String[]> insert = null;

			String[] titles = null;

			String[][] matrixToInsert = null;

			titles = new String[] { "  Id", "Nombre", "Apellido 1", "Apellido 2", "NIF", "Fecha", "Cuenta Bancaria",
					"Puesto", "Contrase�a", "Usuario", "Email" }; // Titulos de la tabla de
																	// los empleados
			insert = personalDao.getAllEmployees();// ArrayList de Arrays
			System.out.println("inset vale" + insert.size());
			for (int i = 0; i < insert.size(); i++) {
				System.out.println(insert.get(i)[0]);
			}
			matrixToInsert = new String[insert.size() + 1][11];
			adminWindow.seeEmployeesPanel.setPreferredSize(new Dimension(624, 20 + 20 * insert.size()));
			adminWindow.seeEmployeesPanel.setBounds(284, 11, 624, 20 + 20 * insert.size());

			for (int i = 0; i < insert.size() + 1; i++) { // rellenamos la matriz que meteremos en la tabla a partir
															// del ArrayList de arrays devuelto del DAO /// +1 PORQUE NO
															// CONTABA QUE HACIA FALTA TAMBIEN LA FILA EN LA CUAL EST�N
															// LOS TITULOS
				for (int j = 0; j < 11; j++) {
					if (i == 0) {

						matrixToInsert[i][j] = titles[j];

					} else {
						matrixToInsert[i][j] = insert.get(i - 1)[j];// a�adi un -1, no se tenia en cuenta la fila que s
																	// euarda para los titulos, entonces empezaba en el
																	// id 1
					}
				}
			}

			JTable employeesTable = new JTable();
			employeesTable.setBounds(20, 20, 600, 20 + 20 * insert.size());

			employeesTable.setVisible(true);
			adminWindow.seeEmployeesPanel.add(employeesTable);
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

			employeesTable.setAutoscrolls(true);

			DefaultTableModel tableModel = new DefaultTableModel(matrixToInsert, titles);
			employeesTable.setModel(tableModel);

		} else if (arg0.getActionCommand().equals("Editar trabajador")) {

			adminWindow.seeEmployeesPanel.setVisible(false);
			adminWindow.addEmployeePanel.setVisible(false);
			adminWindow.editEmployeesPanel.setVisible(true);
			adminWindow.btnVerPlantilla.setText("Ver plantilla");
			adminWindow.deletePanel.setVisible(false);
			enableAllEdit(false);
			adminWindow.lblError.setText("");
			adminWindow.lblErrorDelete.setText("");
			adminWindow.lblErrorEdit.setText("");
			adminWindow.addPatientsPanel.setVisible(false);
			adminWindow.seePacientsPanel.setVisible(false);

		} else if (arg0.getActionCommand().equals("Buscar")) {
			// TODO saltar fallo si el dni no existe o el campo esta vacio

			if ((adminWindow.textFieldSearchDNIEdit.getText().toString().equals(""))
					|| (!personalDao.checkEmployeeExist(adminWindow.textFieldSearchDNIEdit.getText().toString()))) {
				adminWindow.lblErrorEdit.setText("Error en el formulario");
			} else {
				enableAllEdit(true);
				employeeToEdit = personalDao.getEmployee(adminWindow.textFieldSearchDNIEdit.getText().toString());

				adminWindow.textFieldNameEdit.setText(employeeToEdit[1]);
				adminWindow.textFieldSurname1Edit.setText(employeeToEdit[2]);
				adminWindow.textFieldSurname2Edit.setText(employeeToEdit[3]);
				adminWindow.textFieldDNIEdit.setText(employeeToEdit[4]);
				adminWindow.textFieldBankEdit.setText(employeeToEdit[6]);
				adminWindow.comboBoxJobEdit.setSelectedItem(employeeToEdit[7]);
				adminWindow.textFieldEmailEdit.setText(employeeToEdit[10]);
				adminWindow.labelUserNameEdit.setText(employeeToEdit[9]);
			}

		} else if (arg0.getActionCommand().equals("Guardar")) {

			System.out.println("Comenzamos a editar el trabajador");
			boolean out = false;

			while (!out) {// Repetimos hasta que se cancela o se introduce la contraseña correcta
				JPasswordField pf = new JPasswordField();
				int option = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if (option == JOptionPane.CANCEL_OPTION) {// si se pulsa cancelar
					out = true;
					System.out.println("Se ha cancelado");
				} else {

					if (pf.getText().equals(adminWindow.password)) {// Si se acierta la contraseña
						out = true;

						adminWindow.labelUserNameEdit.setText(genUser(adminWindow.textFieldNameEdit.getText(),
								adminWindow.textFieldSurname1Edit.getText(),
								adminWindow.textFieldSurname2Edit.getText()));// Generamos el
						// nuevo usuario

						// Llamamos a editar del DAO
						personalDao.editEmployee(Integer.parseInt(employeeToEdit[0]),
								adminWindow.textFieldNameEdit.getText(), adminWindow.textFieldSurname1Edit.getText(),
								adminWindow.textFieldSurname2Edit.getText(), adminWindow.textFieldDNIEdit.getText(),
								adminWindow.textFieldBankEdit.getText(),
								adminWindow.comboBoxJobEdit.getSelectedItem().toString(),
								adminWindow.labelUserNameEdit.getText(), adminWindow.textFieldEmailEdit.getText());

					} else {// Si se falla la contraseña
						JOptionPane.showMessageDialog(null, "Contraseña incorrecta", "ERROR",
								JOptionPane.ERROR_MESSAGE);
					}
				}

			}

		} else if (arg0.getActionCommand().equals("Borrar empleado")) {

			adminWindow.seeEmployeesPanel.setVisible(false);
			adminWindow.addEmployeePanel.setVisible(false);
			adminWindow.editEmployeesPanel.setVisible(false);
			adminWindow.btnVerPlantilla.setText("Ver plantilla");
			adminWindow.deletePanel.setVisible(true);
			adminWindow.lblError.setText("");
			adminWindow.lblErrorDelete.setText("");
			adminWindow.lblErrorEdit.setText("");
			adminWindow.addPatientsPanel.setVisible(false);
			adminWindow.seePacientsPanel.setVisible(false);
		} else if (arg0.getActionCommand().equals("Cerrar sesion")) {

			adminWindow.setVisible(false);
			// TODO arreglar que se borren los campos al cerrar sesion
			try {
				LoginWindow newlogin = new LoginWindow();
				ControlerLoginWindow controlerLogin = new ControlerLoginWindow(newlogin);
				controlerLogin.resetJField();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (arg0.getActionCommand().equals("Borrar")) {
			// TODO no funciona, no se por que

			
			if ((!personalDao.checkEmployeeExist(adminWindow.textFieldSearchDNIEdit.getText().toString()))) {
				adminWindow.lblErrorDelete.setText("Empleado no encontrado");
				// TODO comporbar que el DNI coincida con el nombre y los apellidos
			} else {
				System.out.println("Boton borrar pulsado");
				personalDao.deleteEmployee(adminWindow.textFieldNameToDelete.getText(),
						adminWindow.textFieldFirstDeleteToDelete.getText(),
						adminWindow.textFieldSecondDeleteToDelete.getText(),
						adminWindow.textFieldDNIToDelete.getText());
			}

		} else if (arg0.getActionCommand().contentEquals("Ingresar paciente")) {

			adminWindow.seeEmployeesPanel.setVisible(false);
			adminWindow.addEmployeePanel.setVisible(false);
			adminWindow.editEmployeesPanel.setVisible(false);
			adminWindow.btnVerPlantilla.setText("Ver plantilla");
			adminWindow.deletePanel.setVisible(false);
			adminWindow.lblError.setText("");
			adminWindow.lblErrorDelete.setText("");
			adminWindow.lblErrorEdit.setText("");
			adminWindow.addPatientsPanel.setVisible(true);
			adminWindow.seePacientsPanel.setVisible(false);

		} else if (arg0.getActionCommand().contentEquals("Ingresar")) {

			// log.InfoLog("Se ha pulsado el boton de registrar");
			System.out.println(adminWindow.roomAddPatients.getText() + " HOLA");
			boolean add = true;

			if ((adminWindow.NombreP.getText().equals("")) || (adminWindow.surname1AddPatients.getText().equals(""))
					|| (adminWindow.surname2AddPatients.getText().equals("")) || (adminWindow.DNI.getText().equals(""))
					|| (adminWindow.roomAddPatients.getText().equals(""))) {// Comprobamos
				// si algum
				// campo esta
				// vacio

				add = false;
				adminWindow.lberror.setText("Hay campos vacios");
			} else if (adminWindow.jcbMedic.getSelectedItem() == null
					|| adminWindow.jcbNurse.getSelectedItem() == null) {
				add = false;
				adminWindow.lberror.setText("No hay medicos/enfermeros disponibles");

			} else if (patientsDao.checkIfRoomIsBusy(Integer.parseInt(adminWindow.roomAddPatients.getText()))) {
				add = false;
				adminWindow.lberror
						.setText("Esa habitacion no est� disponible, proxima: " + patientsDao.firstRoomFree());
			} else {
				adminWindow.lberror.setText("");
			}
			// TOOD: comprob
			// TOOD: comprobar haitacion unica
			if (add) {// Si da error no se a�ade el empleado
				System.out.println("Correcto");

				int id = personalDao.getLastID() + 1;// siguiente id

				Date date = new Date(Calendar.getInstance().getTime().getTime());// Obtenemos la fecha actual
				int idMedic = 0, idNurse = 0;
				try {
					adminWindow.jcbMedic.getSelectedIndex();

					for (int i = 0; i < arrayMedic.size(); i++) {
						if (adminWindow.jcbMedic.getSelectedItem().toString().equals(arrayMedic.get(i)[1])) {
							idMedic = Integer.parseInt(arrayMedic.get(i)[0]);
							;
						}
					}
					for (int i = 0; i < arrayNurse.size(); i++) {
						if (adminWindow.jcbNurse.getSelectedItem().toString().equals(arrayNurse.get(i)[1])) {
							idNurse = Integer.parseInt(arrayNurse.get(i)[0]);
						}
					}
					System.out.println("id medico: " + idMedic + " id Enfermero: " + idNurse);

					patientsDao.addPatient(id, adminWindow.NombreP.getText(), adminWindow.surname1AddPatients.getText(),
							adminWindow.surname2AddPatients.getText(), adminWindow.DNI.getText(), date,
							Integer.parseInt(adminWindow.roomAddPatients.getText()),
							adminWindow.textEnfermedad.getText(), idMedic, idNurse);

				} catch (SQLException e1) {

					e1.printStackTrace();
				}
			}

		}else if (arg0.getActionCommand().contentEquals("Ver pacientes")) {
			
			adminWindow.seeEmployeesPanel.setVisible(false);
			adminWindow.addEmployeePanel.setVisible(false);
			adminWindow.editEmployeesPanel.setVisible(false);
			adminWindow.btnVerPlantilla.setText("Ver plantilla");
			adminWindow.deletePanel.setVisible(false);
			adminWindow.lblError.setText("");
			adminWindow.lblErrorDelete.setText("");
			adminWindow.lblErrorEdit.setText("");
			adminWindow.addPatientsPanel.setVisible(false);
			adminWindow.seePacientsPanel.setVisible(true);
			
			ArrayList<String[]> insert = null;

			String[] titles = null;

			String[][] matrixToInsert = null;

			titles = new String[] { "  Id", "Nombre", "Apellido 1", "Apellido 2", "NIF", "Fecha", "Habitación",
					"Enfermedad", "Producto", "Medico", "Unidades medicamento", "Enfermero " }; // Titulos de la tabla de
																	// los empleados
			insert = patientsDao.getAllPatients();// ArrayList de Arrays
		
			matrixToInsert = new String[insert.size() + 1][12];
			adminWindow.seePacientsPanel.setPreferredSize(new Dimension(624, 20 + 20 * insert.size()));
			adminWindow.seePacientsPanel.setBounds(284, 11, 624, 20 + 20 * insert.size());
			
			for (int i = 0; i < insert.size()+1; i++) { // rellenamos la matriz que meteremos en la tabla a partir
				// del ArrayList de arrays devuelto del DAO
				for (int j = 0; j < 12; j++) {
					if (i == 0) {
						

						matrixToInsert[i][j] = titles[j];

					} else {
						matrixToInsert[i][j] = insert.get(i-1)[j];					}
				}
}
			
			JTable PatientsTable = new JTable();
			PatientsTable.setBounds(5, 5, 600, 20 + 20 * insert.size());

			PatientsTable.setVisible(true);
			adminWindow.seePacientsPanel.add(PatientsTable);
			PatientsTable.setAutoscrolls(true);
			

			
			DefaultTableModel tableModel = new DefaultTableModel(matrixToInsert, titles);
			PatientsTable.setModel(tableModel);
			
		}
	}
}
