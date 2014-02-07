/**
 * 
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import datastructures.ClosePoll;
import datastructures.NewPoll;
import datastructures.PollInquiry;
import doodle.Admin;
import doodle.Client;

/**
 * @author raga
 *
 */
public class ClientGUI extends JFrame {

	private JPanel contentPane;
	private JPanel currentPollsPanel;
	private JPanel createPollPanel;
	private JLabel lblCurrentlyOpenPolls;
	private JButton btnCreateNewPoll;
	private JScrollPane openPollsListScrollPane;
	private ScrollablePanel openPollsListPanel;
	private JPanel createPanel;
	private JButton btnCreatePoll;
	private JButton btnBackToCurrent;

	private String selfEmailID;
	private Client backendObject;
	private ArrayList<String> members;

	private HashMap<String,PollGui> currentPolls = new HashMap<String,PollGui>();

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI(args[0],null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public ClientGUI(String selfEmailID, Client backendObject) throws Exception {
		this.selfEmailID = selfEmailID;
		members = new ArrayList<String>();
		readInputsFromFile();
		this.backendObject = backendObject;
		//this.backendObject = new Client(this.selfEmailID,this);
		//this.backendObject.startListening(); //start listening
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 815, 483);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getCurrentPollsPanel());
		contentPane.add(getCreatePollPanel());

		createPollPanel.setVisible(false);
	}

	private void readInputsFromFile(){
		try{
			FileInputStream fstream = new FileInputStream(Admin.MEMBERS_FILE_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String nextMember;
			while((nextMember = br.readLine()) != null){
				members.add(nextMember);
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void closePoll(ClosePoll closePoll){
		PollGui gui = currentPolls.get(closePoll.getUUID());
		JOptionPane.showMessageDialog(null, gui.getNewPoll().getPoll().getName() + " has been closed and the final timeslot is "  +
				new Date(closePoll.getFinalSlot().getStartTime()) + " - " +
				new Date(closePoll.getFinalSlot().getEndTime()));
		openPollsListPanel.remove(gui);
		openPollsListScrollPane.revalidate();
		openPollsListScrollPane.repaint();
	}

	public void updatePollsAccordingly(String pollUUID,PollInquiry update){
		PollGui gui = currentPolls.get(pollUUID);
		gui.updateTableForPollInquiry(update);
		openPollsListScrollPane.revalidate();
		openPollsListScrollPane.repaint();
	}
	public void showNewPollFromSomeone(NewPoll poll){
		if(currentPolls.get(poll.getPoll().getUUID()) == null){
			PollGui gui = new PollGui(selfEmailID,poll,backendObject);
			gui.setPreferredSize(new Dimension(460,251));
			currentPolls.put(poll.getPoll().getUUID(), gui);
			openPollsListPanel.add(gui);
			openPollsListScrollPane.revalidate();
			openPollsListScrollPane.repaint();
		}
	}

	private JPanel getCurrentPollsPanel() {
		if (currentPollsPanel == null) {
			currentPollsPanel = new JPanel();
			currentPollsPanel.setBackground(Color.WHITE);
			currentPollsPanel.setBounds(6, 6, 803, 449);
			currentPollsPanel.setLayout(null);
			currentPollsPanel.add(getLblCurrentlyOpenPolls());
			currentPollsPanel.add(getBtnCreateNewPoll());
			currentPollsPanel.add(getOpenPollsListScrollPane());
		}
		return currentPollsPanel;
	}
	private JPanel getCreatePollPanel() {
		if (createPollPanel == null) {
			createPollPanel = new JPanel();
			createPollPanel.setBackground(Color.WHITE);
			createPollPanel.setBounds(6, 6, 803, 449);
			createPollPanel.setLayout(null);
			createPollPanel.add(getCreatePanel());
			createPollPanel.add(getBtnCreatePoll());
			createPollPanel.add(getBtnBackToCurrent());
		}
		return createPollPanel;
	}
	private JLabel getLblCurrentlyOpenPolls() {
		if (lblCurrentlyOpenPolls == null) {
			lblCurrentlyOpenPolls = new JLabel("Currently Open Polls");
			lblCurrentlyOpenPolls.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
			lblCurrentlyOpenPolls.setHorizontalAlignment(SwingConstants.CENTER);
			lblCurrentlyOpenPolls.setBounds(299, 6, 198, 28);
		}
		return lblCurrentlyOpenPolls;
	}
	private JButton getBtnCreateNewPoll() {
		if (btnCreateNewPoll == null) {
			btnCreateNewPoll = new JButton("Create New Poll");
			btnCreateNewPoll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentPollsPanel.setVisible(false);
					createPollPanel.setVisible(true);
				}
			});
			btnCreateNewPoll.setBounds(580, 399, 198, 44);
		}
		return btnCreateNewPoll;
	}
	private JScrollPane getOpenPollsListScrollPane() {
		if (openPollsListScrollPane == null) {
			openPollsListScrollPane = new JScrollPane();
			openPollsListScrollPane.setBounds(47, 47, 703, 334);
			openPollsListScrollPane.setViewportView(getOpenPollsListPanel());
		}
		return openPollsListScrollPane;
	}
	private ScrollablePanel getOpenPollsListPanel() {
		if (openPollsListPanel == null) {
			openPollsListPanel = new ScrollablePanel();
			openPollsListPanel.setLayout(new WrapLayout());
			openPollsListPanel.setScrollableWidth( ScrollablePanel.ScrollableSizeHint.FIT );
			openPollsListPanel.setScrollableBlockIncrement(
					ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 150);
		}
		return openPollsListPanel;
	}
	private JPanel getCreatePanel() {
		if (createPanel == null) {
			createPanel = new CreatePollPanel(this.members);
			createPanel.setBounds(67, 20, 685, 379);
		}
		return createPanel;
	}
	private JButton getBtnCreatePoll() {
		if (btnCreatePoll == null) {
			btnCreatePoll = new JButton("Create Poll");
			btnCreatePoll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					NewPoll poll = ((CreatePollPanel)createPanel).getNewPollFromData(selfEmailID);
					PollGui gui = new PollGui(selfEmailID,poll,backendObject);
					gui.setPreferredSize(new Dimension(460,251));
					currentPolls.put(poll.getPoll().getUUID(), gui);
					openPollsListPanel.add(gui);
					backendObject.createPollMessages.add(poll);
					openPollsListPanel.repaint();
					currentPollsPanel.setVisible(true);
					createPollPanel.setVisible(false);
				}
			});
			btnCreatePoll.setBounds(635, 403, 117, 40);
		}
		return btnCreatePoll;
	}
	private JButton getBtnBackToCurrent() {
		if (btnBackToCurrent == null) {
			btnBackToCurrent = new JButton("Back to current polls");
			btnBackToCurrent.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentPollsPanel.setVisible(true);
					createPollPanel.setVisible(false);
				}
			});
			btnBackToCurrent.setBounds(66, 403, 211, 40);
		}
		return btnBackToCurrent;
	}
}
