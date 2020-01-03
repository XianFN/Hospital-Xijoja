package es.unileon.xijoja.hospital.nurse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JComboBox;

import es.unileon.xijoja.hospital.Logs;
import es.unileon.xijoja.hospital.PacientesDAO;
import es.unileon.xijoja.hospital.PersonalDAO;
import es.unileon.xijoja.hospital.login.ControlerLoginWindow;
import es.unileon.xijoja.hospital.login.LoginWindow;
import es.unileon.xijoja.hospital.secretary.SecretaryWindow;

public class ControlerNurseWindow implements ActionListener {


	private NurseWindow nurseWindow;
	private Logs log;
	private PacientesDAO dao;
	private PersonalDAO daoPersonal;
	private ArrayList<String[]> arrayNurse, arrayMedic;
	String[] getPatientData = null;
	
	public ControlerNurseWindow(NurseWindow window) {
		this.dao = new PacientesDAO();
		this.daoPersonal= new PersonalDAO();
		this.nurseWindow = window;
		log = new Logs();

	}
	
	

	public void actionPerformed(ActionEvent arg0) {

		if (arg0.getActionCommand().equals("A�adir")) {

			boolean add = true;
			if ((nurseWindow.textFieldName.getText().equals("")) || (nurseWindow.textFieldSurname1.getText().equals(""))
					|| (nurseWindow.textFieldSurname2.getText().equals("")) || (nurseWindow.textFieldNIFNIE.getText().equals(""))
					|| (nurseWindow.textFieldRoom.getText().equals(""))) {

				add = false;
				nurseWindow.lblError.setText("Hay campos vacios");
				log.InfoLog("Error, no se pudo introducir el paciente, hay campos vacios");

			} else if(nurseWindow.jcbMedic.getSelectedItem()==null||nurseWindow.jcbNurse.getSelectedItem()==null) {
				add = false;
				nurseWindow.lblError.setText("No hay medicos/enfermeros disponibles");
				log.InfoLog("Error, no se pudo introducir el paciente, no hay medicos/enfermeros disponibles");

				
			}else if (dao.checkIfRoomIsBusy(Integer.parseInt(nurseWindow.textFieldRoom.getText()))) {
				add = false;
				nurseWindow.lblError.setText("Esa habitacion no est� disponible, proxima: "+ dao.firstRoomFree());
				log.InfoLog("Error, no se pudo introducir el paciente, habitaci�n ocupada");

			}else{
				nurseWindow.lblError.setText("");
			}
				if (add) {// Si da error no se a�ade el empleado
					System.out.println("Correcto");
	
					int id = dao.getLastID()+1;//siguiente id
					
					Date date = new Date(Calendar.getInstance().getTime().getTime());// Obtenemos la fecha actual
					int idMedic=0,idNurse=0;
					try {
						nurseWindow.jcbMedic.getSelectedIndex();
						
						for (int i = 0; i < arrayMedic.size(); i++) {
							if (nurseWindow.jcbMedic.getSelectedItem().toString().equals(arrayMedic.get(i)[1])) {
								idMedic=Integer.parseInt(arrayMedic.get(i)[0]);;
							}
						}
						for (int i = 0; i < arrayNurse.size(); i++) {
							if (nurseWindow.jcbNurse.getSelectedItem().toString().equals(arrayNurse.get(i)[1])) {
								idNurse=Integer.parseInt(arrayNurse.get(i)[0]);
							}
						}
						
	
						dao.addPatient(id, nurseWindow.textFieldName.getText(), nurseWindow.textFieldSurname1.getText(),
								nurseWindow.textFieldSurname2.getText(), nurseWindow.textFieldNIFNIE.getText(), date,
								Integer.parseInt(nurseWindow.textFieldRoom.getText()),idMedic,idNurse);// LLamamos a la
																					// funcion del DAO
																					// que inserta el
																					// paciente
						log.InfoLog("A�adido el paciente con id: "+id);

					} catch (SQLException e1) {
	
						e1.printStackTrace();
					}
				}
			
		} else if (arg0.getActionCommand().equals("Cerrar sesion")) {

				nurseWindow.setVisible(false);
				//TODO arreglar que se borren los campos al cerrar sesion
				try {
					LoginWindow newlogin = new LoginWindow();
					ControlerLoginWindow controlerLogin = new ControlerLoginWindow(newlogin);
					controlerLogin.resetJField();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		} else if (arg0.getActionCommand().equals("Buscar Paciente")) {
				nurseWindow.addPatientPane.setVisible(false);
				nurseWindow.getPatientPane.setVisible(true);
				
		} else if (arg0.getActionCommand().equals("A�adir Paciente")) {
				System.out.println("2");
				nurseWindow.getPatientPane.setVisible(false);
				nurseWindow.addPatientPane.setVisible(true);
				
				
		} else if (arg0.getActionCommand().equals("Buscar")) {
				
			if ((nurseWindow.textFieldSearchDNIGetPatient.getText().toString().equals(""))){
				nurseWindow.lblErrorGetPatient.setText("Error en el formulario");
				log.InfoLog("Error al buscar el paciente");

				
			}else {
				//comprueba si se introduce un dni o numero de habitacion;
				boolean isDniOrRoom = isDni(nurseWindow.textFieldSearchDNIGetPatient.getText().toString());
				
				if (!dao.checkPatientExist(nurseWindow.textFieldSearchDNIGetPatient.getText().toString(),isDniOrRoom)) {
					nurseWindow.lblErrorGetPatient.setText("Error en el formulario");
					log.InfoLog("Error, no se encuentra el paciente indicado");
				}else {
					
					getPatientData = dao.getPatient(nurseWindow.textFieldSearchDNIGetPatient.getText().toString(),isDniOrRoom);
					nurseWindow.textFieldNameGetPatient.setText(getPatientData[1]);
					nurseWindow.textFieldSurname1GetPatient.setText(getPatientData[2]);
					nurseWindow.textFieldSurname2GetPatient.setText(getPatientData[3]);
					nurseWindow.textFieldDNIGetPatient.setText(getPatientData[4]);
					nurseWindow.textFieldRoomGetPatient.setText(getPatientData[6]);
					log.InfoLog("Devuelto el paciente con id: "+getPatientData[0]);

			
				}	
			}
		}
	}
	public void filJComboBox(JComboBox edit, boolean ismedic) {

		ArrayList<String[]> list =  daoPersonal.getNuseAndMedic(ismedic);
		if (ismedic) {
			arrayMedic=list;	
		}else {
			arrayNurse=list;
		}
		
		String[] data = new String[2];
		if (list==null) {
			
		}else {
			for (int i = 0; i < list.size(); i++) {
				data= list.get(i);
				edit.addItem(data[1]);
				
				
			}
		}
		
		
	}
	public boolean isDni (String dniOrRoom) {
		// return true si es dni
	
		char lastChar = dniOrRoom.charAt(dniOrRoom.length()-1);
		if (Character.isLetter(lastChar)) {
			return true;
		}else {
			return false;
		}
		
	}

}
