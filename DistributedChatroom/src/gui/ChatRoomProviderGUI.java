package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import setup.Init;
import theregistry.TheRegistry;
import chatroomprovider.ChatRoomProvider;
import chatroomprovider.ChatroomServer;


public class ChatRoomProviderGUI extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblChatRoomName;
	private JTextField chatRoomNameTextField;
	private JTextField locationTextField;
	private JLabel lblLocation;
	private JLabel lblRegisterChatRooms;
	private JButton btnRegister;
	private JScrollPane registeredChatRoomsScrollPane;
	private JLabel lblRegisteredChatRooms;
	private JTable registeredChatRoomsTable;
	
	private static TheRegistry reg = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatRoomProviderGUI frame = new ChatRoomProviderGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatRoomProviderGUI() {
		super("Chat Room Provider GUI");
		System.setSecurityManager(new RMISecurityManager());
		try {
			reg = (TheRegistry) Naming.lookup(Init.REGISTRY_LOOKUP_STRING);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 571, 446);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getPanel());
		contentPane.add(getRegisteredChatRoomsScrollPane());
		contentPane.add(getLblRegisteredChatRooms());
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBounds(20, 21, 545, 167);
			panel.setLayout(null);
			panel.add(getLblChatRoomName());
			panel.add(getChatRoomNameTextField());
			panel.add(getLocationTextField());
			panel.add(getLblLocation());
			panel.add(getLblRegisterChatRooms());
			panel.add(getBtnRegister());
		}
		return panel;
	}
	private JLabel getLblChatRoomName() {
		if (lblChatRoomName == null) {
			lblChatRoomName = new JLabel("Chat Room Name");
			lblChatRoomName.setBounds(6, 50, 129, 23);
		}
		return lblChatRoomName;
	}
	private JTextField getChatRoomNameTextField() {
		if (chatRoomNameTextField == null) {
			chatRoomNameTextField = new JTextField();
			chatRoomNameTextField.setBounds(133, 47, 134, 28);
			chatRoomNameTextField.setColumns(10);
		}
		return chatRoomNameTextField;
	}
	private JTextField getLocationTextField() {
		if (locationTextField == null) {
			locationTextField = new JTextField();
			locationTextField.setColumns(10);
			locationTextField.setBounds(378, 47, 134, 28);
		}
		return locationTextField;
	}
	private JLabel getLblLocation() {
		if (lblLocation == null) {
			lblLocation = new JLabel("Location");
			lblLocation.setBounds(293, 53, 95, 23);
		}
		return lblLocation;
	}
	private JLabel getLblRegisterChatRooms() {
		if (lblRegisterChatRooms == null) {
			lblRegisterChatRooms = new JLabel("Register Chat Rooms");
			lblRegisterChatRooms.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblRegisterChatRooms.setBounds(152, 6, 233, 28);
		}
		return lblRegisterChatRooms;
	}
	private JButton getBtnRegister() {
		if (btnRegister == null) {
			btnRegister = new JButton("Register");
			btnRegister.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(goodToGo()){
						ChatRoomProvider cp = ChatRoomProvider.getInstance();
						cp.registerChatRoom(chatRoomNameTextField.getText(), locationTextField.getText());
						try {
							refreshTableRows();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}else{
						JOptionPane.showMessageDialog(null, "Please enter valid values to register the chatroom.","Invalid format",JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnRegister.setBounds(182, 107, 169, 36);
		}
		return btnRegister;
	}
	private JScrollPane getRegisteredChatRoomsScrollPane() {
		if (registeredChatRoomsScrollPane == null) {
			registeredChatRoomsScrollPane = new JScrollPane();
			registeredChatRoomsScrollPane.setBounds(6, 230, 559, 188);
			registeredChatRoomsScrollPane.setViewportView(getRegisteredChatRoomsTable());
		}
		return registeredChatRoomsScrollPane;
	}
	private JLabel getLblRegisteredChatRooms() {
		if (lblRegisteredChatRooms == null) {
			lblRegisteredChatRooms = new JLabel("Registered Chat Rooms");
			lblRegisteredChatRooms.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblRegisteredChatRooms.setBounds(200, 190, 260, 28);
		}
		return lblRegisteredChatRooms;
	}
	private boolean goodToGo(){
		return checkNotNullEmpty(chatRoomNameTextField.getText().trim()) && checkNotNullEmpty(locationTextField.getText().trim());
	}

	private boolean checkNotNullEmpty(String text){
		return text != null && !text.trim().equals("");
	}
	private Action deregisterChatRoomAction = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			int row = Integer.valueOf(e.getActionCommand());
			DefaultTableModel model = (DefaultTableModel) registeredChatRoomsTable.getModel();
			if(ChatRoomProvider.getInstance().deRegisterChatRoom(String.valueOf(model.getValueAt(row, 3)))){
				JOptionPane.showMessageDialog(null, "The chat room has been deregistered successfully!", "Deregistered",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(null, "Problem in deregistering chat room. It does not exist!", "Does not exist",JOptionPane.ERROR_MESSAGE);
			}
			model.removeRow(row);
		}
	};
	private JTable getRegisteredChatRoomsTable() {
		if (registeredChatRoomsTable == null) {
			String[] columnNames = {"Name","Location","Deregister","uuid"};
			DefaultTableModel model = new DefaultTableModel(new String[][]{},columnNames);
			registeredChatRoomsTable = new JTable(model);
			ButtonColumn buttonColumn = new ButtonColumn(registeredChatRoomsTable, deregisterChatRoomAction, 2);
			buttonColumn.setMnemonic(KeyEvent.VK_ENTER);
			registeredChatRoomsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(registeredChatRoomsTable);
			tca.adjustColumns();
			TableColumnManager tcm = new TableColumnManager(registeredChatRoomsTable);
			tcm.hideColumn("uuid");
		}
		return registeredChatRoomsTable;
	}
	public void refreshTableRows() throws RemoteException{
		DefaultTableModel model = (DefaultTableModel) registeredChatRoomsTable.getModel();
		List<ChatroomServer> servers = reg.getChatRooms("SUDO");
		model.setRowCount(0);
		for(ChatroomServer cs : servers){
			Object[] row = {cs.getName(),cs.getLocation(),"Deregister",cs.getUUID()};
			model.addRow(row);
		}
	}

}
