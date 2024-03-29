package es.unileon.xijoja.hospital.login;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import es.unileon.xijoja.hospital.InfoWindow;
import es.unileon.xijoja.hospital.Logs;
import es.unileon.xijoja.hospital.PersonalDAO;
import es.unileon.xijoja.hospital.admin.AdminWindow;
import es.unileon.xijoja.hospital.secretary.SecretaryWindow;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

@SuppressWarnings("serial")
public class LoginWindow extends JFrame {

	private static final int PWIDTH = 750;
	private static final int PHEIGH = 348;
	private static final int WHEN_IN_FOCUSED_WINDOW = 0;
	private ControlerLoginWindow listener;
	/* LOGIN */
	protected JPanel loginPanel;
	protected JTextField loginUser;
	protected JPasswordField loginPassword;
	protected PersonalDAO dao;
	protected JLabel lblLoginError;
	private Logs log = new Logs();

	public LoginWindow() throws IOException {

		log.InfoLog("SE INICIA LA PANTALLA DE LOGIN");
		getContentPane().setBackground(Color.WHITE);
		setBackground(Color.WHITE);
		this.listener = new ControlerLoginWindow(this);

		setBounds(1024 / 4, 768 / 6, PWIDTH, PHEIGH);

		setUndecorated(true);

		setTitle("Login");

		try {
			initComponents();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initComponents() throws IOException {

		dao = new PersonalDAO();// LLamamos al patron

		getContentPane().setLayout(null);

		JButton crossButton = new JButton(new ImageIcon(LoginWindow.class.getResource("/resources/cross.png")));
		crossButton.setBounds(720, 11, 15, 15);
		getContentPane().add(crossButton);
		crossButton.setBackground(null);
		crossButton.setBorder(null);
		crossButton.setOpaque(false);
		crossButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		JButton minButton = new JButton(new ImageIcon(LoginWindow.class.getResource("/resources/min.png")));
		minButton.setBorder(null);
		minButton.setBackground(null);
		minButton.setBounds(690, 11, 15, 15);
		minButton.setOpaque(false);
		getContentPane().add(minButton);
		minButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				setExtendedState(JFrame.CROSSHAIR_CURSOR);
			}
		});

		JLabel backgroundLabel = new JLabel(new ImageIcon(LoginWindow.class.getResource("/resources/fondo.jpg")));
		backgroundLabel.setBounds(218, 0, 532, 348);
		getContentPane().add(backgroundLabel);

		loginPanel = new JPanel();
		loginPanel.setBackground(Color.WHITE);
		loginPanel.setBounds(0, 0, 750, 348);
		getContentPane().add(loginPanel);
		loginPanel.setLayout(null);
		JLabel iconLabel = new JLabel(new ImageIcon(LoginWindow.class.getResource("/resources/icon.png")));
		iconLabel.setBounds(23, 62, 45, 45);
		loginPanel.add(iconLabel);

		
		
		loginUser = new JTextField();
		loginUser.setBackground(Color.WHITE);
		loginUser.setBounds(83, 115, 115, 20);
		loginPanel.add(loginUser);
		loginUser.setColumns(10);
		loginUser.addKeyListener(listener);

		loginPassword = new JPasswordField();
		loginPassword.setBounds(83, 146, 115, 20);
		loginPanel.add(loginPassword);
		loginPassword.addKeyListener(listener);

		JLabel lblUser = new JLabel("USER");
		lblUser.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 11));
		lblUser.setBounds(49, 118, 73, 14);
		loginPanel.add(lblUser);

		JLabel lblPassword = new JLabel("PASSWORD");
		lblPassword.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 11));
		lblPassword.setBounds(23, 146, 90, 14);
		loginPanel.add(lblPassword);

		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 11));
		btnLogin.setBounds(125, 177, 73, 23);

		btnLogin.setBackground(null);
		// btnLogin.setBorder(null);
		btnLogin.setOpaque(false);
		btnLogin.addKeyListener(listener);
		btnLogin.addActionListener(listener);
		loginPanel.add(btnLogin);

		JButton buttonInfo = new JButton(new ImageIcon(LoginWindow.class.getResource("/resources/--ndice.png")));
		buttonInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InfoWindow info = new InfoWindow("general");
				info.setVisible(true);
			}
		});

		buttonInfo.setOpaque(false);
		buttonInfo.setBorder(null);
		buttonInfo.setBackground((Color) null);
		buttonInfo.setBounds(10, 314, 23, 23);
		loginPanel.add(buttonInfo);
		
		
		ImageIcon icon = new ImageIcon(LoginWindow.class.getResource("/resources/settings.png"));
		Image scaleImage = icon.getImage().getScaledInstance(23, 23,Image.SCALE_DEFAULT);
		JButton buttonSettings = new JButton(new ImageIcon(scaleImage));
	
		buttonSettings.setOpaque(false);
		buttonSettings.setBorder(null);
		buttonSettings.setBackground((Color) null);
		buttonSettings.setBounds(40, 314, 23, 23);
		loginPanel.add(buttonSettings);
		  
		
		  JPopupMenu popupMenu = new JPopupMenu("Configuracion");

		    JMenuItem resetMenuItem = new JMenuItem("Resetear la base de datos");
		    resetMenuItem.addActionListener(listener);

		    popupMenu.add(resetMenuItem);

		    popupMenu.addSeparator();
		    
		    JMenuItem exportMenuItem = new JMenuItem("Exportar la base de datos");
		    exportMenuItem.addActionListener(listener);
		    popupMenu.add(exportMenuItem);
		   
		   
		    buttonSettings.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    popupMenu.show(buttonSettings, buttonSettings.getWidth()/2, buttonSettings.getHeight()/2);
                }
            } );
        
 
           
		
			//	buttonSettings.addActionListener(listener);

		lblLoginError = new JLabel("");
		lblLoginError.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblLoginError.setForeground(Color.RED);
		lblLoginError.setBounds(38, 211, 160, 14);
		loginPanel.add(lblLoginError);

	}
}
