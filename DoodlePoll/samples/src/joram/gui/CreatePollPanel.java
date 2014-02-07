package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import datastructures.NewPoll;
import datastructures.Poll;
import datastructures.SlotVal;
import datastructures.TimeSlot;

// width - 660
// height - 385

public class CreatePollPanel extends JPanel {
	private JLabel lblCreatePoll;
	private JLabel lblName;
	private JTextField nameTextField;
	private JLabel lblDescription;
	private JTextArea descriptionTextArea;
	private JLabel lblRecipients;
	private JScrollPane recipientsScrollPane;
	private JList recipientsList;
	private JLabel lblTimeSlots;
	private ScrollablePanel timeSlotsPanel;
	private JButton btnAddSlot;
	private JButton btnClearSlots;
	private JPanel startEndPanel;
	private JLabel lblStartTime;
	private JLabel lblEndTime;

	private ArrayList<JPanel> startTimeTimePickers;
	private ArrayList<JPanel> endTimeTimePickers;
	private JScrollPane timeSlotsScrollPane;
	private ArrayList<String> members;

	public CreatePollPanel(ArrayList<String> members) {
		this.members = members;
		setBorder(new LineBorder(Color.LIGHT_GRAY, 3));
		setBackground(Color.WHITE);
		setLayout(null);
		add(getLblCreatePoll());
		add(getLblName());
		add(getNameTextField());
		add(getLblDescription());
		add(getDescriptionTextArea());
		add(getLblRecipients());
		add(getRecipientsScrollPane());
		add(getLblTimeSlots());
		add(getBtnAddSlot());
		add(getBtnClearSlots());
		add(getTimeSlotsScrollPane());

		startTimeTimePickers = new ArrayList<JPanel>();
		endTimeTimePickers  = new ArrayList<JPanel>();
	}
	
	public NewPoll getNewPollFromData(String selfEmailID){
		ArrayList<String> recipients = getSelectedRecipients();
		Poll poll = new Poll(nameTextField.getText(),descriptionTextArea.getText());
		for(int i=0;i<startTimeTimePickers.size();i++){
			poll.addTimeSlot(new TimeSlot(SlotVal.NA,((TimePicker)startTimeTimePickers.get(i)).getCurrentTime().getTime(),
					((TimePicker)endTimeTimePickers.get(i)).getCurrentTime().getTime()));
		}
		NewPoll newPoll = new NewPoll(selfEmailID,poll,recipients);
		return newPoll;
	}
	
	private JLabel getLblCreatePoll() {
		if (lblCreatePoll == null) {
			lblCreatePoll = new JLabel("Create Poll");
			lblCreatePoll.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblCreatePoll.setHorizontalAlignment(SwingConstants.CENTER);
			lblCreatePoll.setBounds(261, 6, 194, 26);
		}
		return lblCreatePoll;
	}
	private JLabel getLblName() {
		if (lblName == null) {
			lblName = new JLabel("Name :");
			lblName.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
			lblName.setBounds(61, 49, 69, 26);
		}
		return lblName;
	}
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setBackground(Color.LIGHT_GRAY);
			nameTextField.setBounds(133, 50, 505, 28);
			nameTextField.setColumns(10);
		}
		return nameTextField;
	}
	private JLabel getLblDescription() {
		if (lblDescription == null) {
			lblDescription = new JLabel("Description :");
			lblDescription.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
			lblDescription.setBounds(17, 82, 123, 26);
		}
		return lblDescription;
	}
	private JTextArea getDescriptionTextArea() {
		if (descriptionTextArea == null) {
			descriptionTextArea = new JTextArea();
			descriptionTextArea.setBackground(Color.LIGHT_GRAY);
			descriptionTextArea.setBounds(137, 89, 495, 70);
		}
		return descriptionTextArea;
	}
	private JLabel getLblRecipients() {
		if (lblRecipients == null) {
			lblRecipients = new JLabel("Recipients");
			lblRecipients.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
			lblRecipients.setBounds(73, 171, 95, 26);
		}
		return lblRecipients;
	}
	private JScrollPane getRecipientsScrollPane() {
		if (recipientsScrollPane == null) {
			recipientsScrollPane = new JScrollPane();
			recipientsScrollPane.setBounds(42, 205, 158, 156);
			recipientsScrollPane.setViewportView(getRecipientsList());
		}
		return recipientsScrollPane;
	}
	private JList getRecipientsList() {
		if (recipientsList == null) {
			recipientsList = new JList(members.toArray());
			recipientsList.setBackground(Color.LIGHT_GRAY);
		}
		return recipientsList;
	}
	private JLabel getLblTimeSlots() {
		if (lblTimeSlots == null) {
			lblTimeSlots = new JLabel("Time Slots");
			lblTimeSlots.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
			lblTimeSlots.setBounds(392, 171, 95, 26);
		}
		return lblTimeSlots;
	}
	private ScrollablePanel getTimeSlotsPanel() {
		if (timeSlotsPanel == null) {
			timeSlotsPanel = new ScrollablePanel();
			timeSlotsPanel.setBackground(Color.LIGHT_GRAY);
			timeSlotsPanel.setLayout(new WrapLayout());
			timeSlotsPanel.add(getStartEndPanel());
		}
		return timeSlotsPanel;
	}
	private JButton getBtnAddSlot() {
		if (btnAddSlot == null) {
			btnAddSlot = new JButton("Add Slot");
			btnAddSlot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					TimePicker startTime = new TimePicker();
					startTime.setPreferredSize(new Dimension(192,28));
					TimePicker endTime = new TimePicker();
					endTime.setPreferredSize(new Dimension(192,28));
					timeSlotsPanel.add(endTime);
					timeSlotsPanel.add(startTime);
					startTimeTimePickers.add(startTime);
					endTimeTimePickers.add(endTime);
					timeSlotsPanel.revalidate();
				}
			});
			btnAddSlot.setBounds(231, 341, 117, 29);
		}
		return btnAddSlot;
	}
	private JButton getBtnClearSlots() {
		if (btnClearSlots == null) {
			btnClearSlots = new JButton("Clear Slots");
			btnClearSlots.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startTimeTimePickers.clear();
					endTimeTimePickers.clear();
					timeSlotsPanel.removeAll();
					timeSlotsPanel.add(getStartEndPanel());
					timeSlotsPanel.revalidate();
				}
			});
			btnClearSlots.setBounds(374, 341, 117, 29);
		}
		return btnClearSlots;
	}
	private JPanel getStartEndPanel() {
		if (startEndPanel == null) {
			startEndPanel = new JPanel();
			startEndPanel.setPreferredSize(new Dimension(399,26));
			startEndPanel.setBackground(Color.WHITE);
			startEndPanel.setLayout(null);
			startEndPanel.add(getLblStartTime());
			startEndPanel.add(getLblEndTime());
		}
		return startEndPanel;
	}
	private JLabel getLblStartTime() {
		if (lblStartTime == null) {
			lblStartTime = new JLabel("Start time");
			lblStartTime.setBounds(67, 6, 61, 16);
		}
		return lblStartTime;
	}
	private JLabel getLblEndTime() {
		if (lblEndTime == null) {
			lblEndTime = new JLabel("End time");
			lblEndTime.setBounds(272, 6, 61, 16);
		}
		return lblEndTime;
	}
	private JScrollPane getTimeSlotsScrollPane() {
		if (timeSlotsScrollPane == null) {
			timeSlotsScrollPane = new JScrollPane();
			timeSlotsScrollPane.setBounds(226, 199, 412, 144);
			timeSlotsScrollPane.setViewportView(getTimeSlotsPanel());
		}
		return timeSlotsScrollPane;
	}
	
	public ArrayList<JPanel> getStartTimeTimePickers(){
		return this.startTimeTimePickers;
	}
	
	public ArrayList<JPanel> getEndTimeTimePickers(){
		return this.endTimeTimePickers;
	}
	
	public String getPollName(){
		return this.nameTextField.getText().trim();
	}
	
	public String getPollDescription(){
		return this.descriptionTextArea.getText().trim();
	}
	
	public ArrayList<String> getSelectedRecipients(){
		ArrayList<String> returnValue = new ArrayList<String>();
		for(Object o : recipientsList.getSelectedValues()){
			returnValue.add((String)o);
		}
		return returnValue;
	}
}
