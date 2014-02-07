/**
 * 
 */
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
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import setup.Init;
import theregistry.TheRegistry;
import chatclient.ChatClientImpl;
import chatroomprovider.ChatroomServer;
import entity.UUIDGenerator;


public class ChatClientGUI extends JFrame {
	private JPanel contentPane;
	private JPanel availableChatRoomsPanel;
	private JLabel lblAvailableRooms;
	private JScrollPane availableChatRoomsScrollPane;
	private JTable availableChatRoomsTable;
	private JTextPane allChatBoxTextPane;
	private JTextPane clientSentChatTextPane;
	private JButton btnSend;
	private JButton btnRefreshList;
	private ChatClientImpl me;

	private static TheRegistry reg = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClientGUI frame = new ChatClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws RemoteException 
	 */
	public ChatClientGUI() throws RemoteException {
		super("Chat Client GUI");
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

		//need to replace this with something meaningful, when we actually start deregistering clients from a different interface
		me = new ChatClientImpl(UUIDGenerator.getNextUUID(),UUIDGenerator.getNextUUID(), this);
		reg.register(me);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 664, 484);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getAvailableChatRoomsPanel());
		contentPane.add(getAllChatBoxTextPane());
		contentPane.add(getClientSentChatTextPane());
		contentPane.add(getBtnSend());
	}
	private JPanel getAvailableChatRoomsPanel() {
		if (availableChatRoomsPanel == null) {
			availableChatRoomsPanel = new JPanel();
			availableChatRoomsPanel.setBounds(6, 6, 187, 450);
			availableChatRoomsPanel.setLayout(null);
			availableChatRoomsPanel.add(getLblAvailableRooms());
			availableChatRoomsPanel.add(getAvailableChatRoomsScrollPane());
			availableChatRoomsPanel.add(getBtnRefreshList());
		}
		return availableChatRoomsPanel;
	}
	private JLabel getLblAvailableRooms() {
		if (lblAvailableRooms == null) {
			lblAvailableRooms = new JLabel("Available Rooms");
			lblAvailableRooms.setFont(new Font("Lucida Grande", Font.BOLD, 16));
			lblAvailableRooms.setBounds(6, 6, 175, 31);
		}
		return lblAvailableRooms;
	}
	private JScrollPane getAvailableChatRoomsScrollPane() {
		if (availableChatRoomsScrollPane == null) {
			availableChatRoomsScrollPane = new JScrollPane();
			availableChatRoomsScrollPane.setBounds(6, 46, 175, 350);
			availableChatRoomsScrollPane.setViewportView(getAvailableChatRoomsTable());
		}
		return availableChatRoomsScrollPane;
	}
	private Action joinLeaveAction = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			int row = Integer.valueOf(e.getActionCommand());
			DefaultTableModel model = (DefaultTableModel) availableChatRoomsTable.getModel();

			if(String.valueOf(model.getValueAt(row, 1)).equals("Join")){
				List<ChatroomServer> roomList = null;
				try {
					roomList = reg.getChatRooms(me);
					if(roomList == null){
						System.err.println("NULL Return, no permission to get chat roooms");
					}
					for(ChatroomServer csc : roomList){
						if(csc.getName().equals(String.valueOf(model.getValueAt(row, 0)))){
							csc.join(me);
							model.setValueAt("Leave", row, 1);
							JOptionPane.showMessageDialog(null, "Successfully Joined!", "Joined chatroom",JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}else{
				List<ChatroomServer> roomList = null;
				try {
					roomList = reg.getChatRooms(me);
					if(roomList == null){
						System.err.println("NULL Return, no permission to get chat roooms");
					}
					for(ChatroomServer csc : roomList){
						if(csc.getName().equals(String.valueOf(model.getValueAt(row, 0)))){
							csc.leave(me);
							model.setValueAt("Join", row, 1);
							JOptionPane.showMessageDialog(null, "Successfully Left!", "Left chatroom",JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		}
	};
	private JTable getAvailableChatRoomsTable() {
		if (availableChatRoomsTable == null) {
			String[] columnNames = {"Name","Join/Leave","uuid"};
			DefaultTableModel model = new DefaultTableModel(new String[][]{},columnNames);
			availableChatRoomsTable = new JTable(model);
			ButtonColumn buttonColumn = new ButtonColumn(availableChatRoomsTable, joinLeaveAction, 1);
			buttonColumn.setMnemonic(KeyEvent.VK_ENTER);
			availableChatRoomsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			availableChatRoomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
			TableColumnAdjuster tca = new TableColumnAdjuster(availableChatRoomsTable);
			tca.adjustColumns();
			TableColumnManager tcm = new TableColumnManager(availableChatRoomsTable);
			tcm.hideColumn("uuid");
			try {
				refreshTableRows();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return availableChatRoomsTable;
	}
	public void refreshTableRows() throws RemoteException{
		DefaultTableModel model = (DefaultTableModel) availableChatRoomsTable.getModel();
		List<ChatroomServer> servers = reg.getChatRooms(me);
		model.setRowCount(0);
		for(ChatroomServer cs : servers){
			if(cs.hasJoined(me)){
				model.addRow(new Object[]{cs.getName(),"Leave",cs.getUUID()});
			}else{
				model.addRow(new Object[]{cs.getName(),"Join",cs.getUUID()});
			}
		}
	}
	private JTextPane getAllChatBoxTextPane() {
		if (allChatBoxTextPane == null) {
			allChatBoxTextPane = new JTextPane();
			allChatBoxTextPane.setBounds(204, 6, 454, 319);
			allChatBoxTextPane.setEditable(false);
			allChatBoxTextPane.setFocusable(false);
		}
		return allChatBoxTextPane;
	}
	public void writeToAllChatPane(String text){
		StyledDocument doc = allChatBoxTextPane.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), text + "\n", null );
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	private JTextPane getClientSentChatTextPane() {
		if (clientSentChatTextPane == null) {
			clientSentChatTextPane = new JTextPane();
			clientSentChatTextPane.setBounds(205, 337, 367, 110);
			clientSentChatTextPane.setFocusable(true);
		}
		return clientSentChatTextPane;
	}
	private JButton getBtnSend() {
		if (btnSend == null) {
			btnSend = new JButton("Send");
			btnSend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(clientSentChatTextPane.getText().length() > 0){
						try {
							DefaultTableModel model = (DefaultTableModel) availableChatRoomsTable.getModel();
							List<ChatroomServer> servers = reg.getChatRooms(me);
							if(availableChatRoomsTable.getSelectedRow() == -1){
								JOptionPane.showMessageDialog(null, "Please select a chatroom to send to!", "Select chatroom",JOptionPane.INFORMATION_MESSAGE);
								return;
							}
							String selectedName = String.valueOf(model.getValueAt(availableChatRoomsTable.getSelectedRow(), 0));
							for(ChatroomServer cs : servers){
								if(cs.getName().equals(selectedName)){
									cs.talk(cs.getName() + " - " +  clientSentChatTextPane.getText());
								}
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			});
			btnSend.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			btnSend.setBounds(575, 337, 89, 110);
		}
		return btnSend;
	}
	private JButton getBtnRefreshList() {
		if (btnRefreshList == null) {
			btnRefreshList = new JButton("Refresh List");
			btnRefreshList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						refreshTableRows();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
			btnRefreshList.setFont(new Font("Lucida Grande", Font.BOLD, 16));
			btnRefreshList.setBounds(6, 394, 175, 50);
		}
		return btnRefreshList;
	}
}
