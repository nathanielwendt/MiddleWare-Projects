package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import datastructures.ClosePoll;
import datastructures.NewPoll;
import datastructures.Poll;
import datastructures.PollInquiry;
import datastructures.SlotVal;
import datastructures.TimeSlot;
import doodle.Client;

public class PollGui extends JPanel {

	//width - 460
	//height - 251

	private String selfEmailId;
	private NewPoll newPoll;
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private JScrollPane pollStatsScrollPane;
	private JButton btnClosePoll;
	private JButton btnSubmit;
	private JTable pollStatsTable;
	private JLabel lblName;
	private JLabel lblDescripiton;
	private JLabel lblNameDisplay;
	private JLabel lblDescriptionDisplay;

	private String[] tableColumnNames;
	private boolean isTableEditable = true;
	private int selfColumnNumber = -1;
	private Client backendObject;

	public PollGui(String selfEmailId,NewPoll newPoll, Client backendObject) {
		this.backendObject = backendObject;
		this.selfEmailId = selfEmailId;
		this.newPoll = newPoll;
		setBorder(new LineBorder(Color.LIGHT_GRAY, 3));
		setBackground(Color.WHITE);
		setLayout(null);
		add(getPollStatsScrollPane());
		add(getBtnClosePoll());
		add(getBtnSubmit());
		add(getLblName());
		add(getLblDescripiton());
		add(getLblNameDisplay());
		add(getLblDescriptionDisplay());
		this.lblNameDisplay.setText(this.newPoll.getPoll().getName());
		this.lblDescriptionDisplay.setText(this.newPoll.getPoll().getDescription());
	}

	private JScrollPane getPollStatsScrollPane() {
		if (pollStatsScrollPane == null) {
			pollStatsScrollPane = new JScrollPane();
			pollStatsScrollPane.setBounds(6, 61, 449, 145);
			pollStatsScrollPane.setViewportView(getPollStatsTable());
			JTable rowTable = new RowNumberTable(getPollStatsTable());
			pollStatsScrollPane.setRowHeaderView(rowTable);
			pollStatsScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,rowTable.getTableHeader());
		}
		return pollStatsScrollPane;
	}
	private JButton getBtnClosePoll() {
		if (btnClosePoll == null) {
			btnClosePoll = new JButton("Close Poll");
			btnClosePoll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ArrayList<TimeSlot> slots = newPoll.getPoll().getTimeSlots();
					ArrayList<String> options = new ArrayList<String>();
					for(TimeSlot ts : slots){
						options.add(new Date(ts.getStartTime()).toString() + " to " + new Date(ts.getEndTime()).toString());
					}
					Object[] possibilities = options.toArray();
					String s = (String)JOptionPane.showInputDialog(null,"Choose a time slot","Close poll",
							JOptionPane.PLAIN_MESSAGE,null,possibilities,possibilities[0]);
					//If a string was returned, say so.
					if ((s != null) && (s.length() > 0)) {
						int index = options.indexOf(s);
						TimeSlot target = slots.get(index);
						ClosePoll closePoll = new ClosePoll(newPoll.getPoll().getUUID(),new TimeSlot(SlotVal.NA,
								target.getStartTime(),target.getEndTime()));
						closePoll.setRecipients(newPoll.getRecipients());
						backendObject.closePollMessages.add(closePoll);
					}
				}
			});
			btnClosePoll.setFont(new Font("Lucida Grande", Font.BOLD, 15));
			btnClosePoll.setBackground(Color.WHITE);
			btnClosePoll.setBounds(6, 205, 151, 40);
			if(!this.selfEmailId.equals(this.newPoll.getSender())){
				btnClosePoll.setVisible(false);
			}
		}
		return btnClosePoll;
	}
	private JButton getBtnSubmit() {
		if (btnSubmit == null) {
			btnSubmit = new JButton("Submit");
			btnSubmit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//basically submitting the combobox stuff here. disable the combobox. Update the column
					// and send the update.
					isTableEditable = false;
					Poll existingPoll = PollGui.this.newPoll.getPoll();
					for(int i=0;i< existingPoll.getTimeSlots().size();i++){
						existingPoll.getTimeSlots().get(i).setVal(String.valueOf(((DefaultTableModel)pollStatsTable.getModel()).getValueAt(i, selfColumnNumber)));
					}
					PollInquiry inq = new PollInquiry(existingPoll,selfEmailId);
					inq.setRecipients(newPoll.getRecipients());
					backendObject.respondToPollMessages.add(inq);
					btnSubmit.setVisible(false);
				}
			});
			btnSubmit.setFont(new Font("Lucida Grande", Font.BOLD, 15));
			btnSubmit.setBackground(Color.WHITE);
			btnSubmit.setBounds(304, 205, 151, 40);
			if(!isTableEditable){
				btnSubmit.setVisible(false);
			}
		}
		return btnSubmit;
	}
	private JTable getPollStatsTable() {
		if (pollStatsTable == null) {
			//String[] columnNames = {"raga@mit.edu", "raga@yahoo.com","myself", "timestamps"};

			String[] columnNames = new String[this.newPoll.getRecipients().size() + 1];
			for(int i=0;i < this.newPoll.getRecipients().size();i++){
				columnNames[i] = this.newPoll.getRecipients().get(i);
			}
			columnNames[columnNames.length - 1] = "timestamps";
			tableColumnNames = columnNames;
			String[][] data = new String[this.newPoll.getPoll().getTimeSlots().size()][columnNames.length];
			for(int i=0;i< this.newPoll.getPoll().getTimeSlots().size();i++){
				for(int j=0;j<columnNames.length;j++){
					if(j == columnNames.length-1){
						data[i][j] = timeSlotToHtml(this.newPoll.getPoll().getTimeSlots().get(i));
					}else{

						if(columnNames[j].equals(this.selfEmailId)){
							data[i][j] = "Choose";
							selfColumnNumber = j;
						}else{
							data[i][j] = "NA";
						}
					}
				}
			}

			/*
			String[][] dataBkup = new String[][]{
					{"NA","NA","choose","<html> 21-12-2013 05:06:12 <br> to <br> 21-12-2013 05:06:12</html>"},
					{"NA","NA","choose","<html> 21-12-2013 05:06:12 <br> to <br> 21-12-2013 05:06:12</html>"},
					{"NA","NA","choose","<html> 21-12-2013 05:06:12 <br> to <br> 21-12-2013 05:06:12</html>"}};
			 */

			DefaultTableModel model = new DefaultTableModel(data,columnNames){
				@Override
				public boolean isCellEditable(int row, int column) {
					return isTableEditable;
				}
			};
			pollStatsTable = new JTable(model);
			pollStatsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			pollStatsTable.setRowHeight(45);
			TableColumnAdjuster tca = new TableColumnAdjuster(pollStatsTable);
			tca.adjustColumns();
			TableColumnManager tcm = new TableColumnManager(pollStatsTable);
			tcm.hideColumn("timestamps");

			ComboBoxTableEditor editor = new ComboBoxTableEditor(0);
			int target = -1;
			for(int i = 0;i<columnNames.length;i++){
				if(columnNames[i].equals(this.selfEmailId)){
					target = i;
					selfColumnNumber = i;
				}
			}
			if(target != -1){ //should never be false
				pollStatsTable.getColumnModel().getColumn(target).setCellEditor( editor );
				editor.addModel("NAAA", new String[]{"Yes","No","Maybe"});
			}else{
				isTableEditable = false;
			}
			pollStatsTable.revalidate();

			/*
			//uncomment to get vertical headings
			TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
			for(int i=0;i< pollStatsTable.getColumnCount();i++){
				pollStatsTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			} */
		}
		return pollStatsTable;
	}

	public void updateTableForPollInquiry(PollInquiry update){
		if(update.getPoll().getUUID().equals(this.newPoll.getPoll().getUUID())){
			int target = -1;
			for(int i = 0;i<this.tableColumnNames.length;i++){
				if(this.tableColumnNames[i].equals(update.getSender())){
					target = i;
				}
			}
			if(target != -1){
				for(int i=0;i<update.getPoll().getTimeSlots().size();i++){
					((DefaultTableModel)pollStatsTable.getModel()).setValueAt(update.getPoll().getTimeSlots().get(i).getVal(), i, target);
				}
			}
			((DefaultTableModel)pollStatsTable.getModel()).fireTableDataChanged();
		}else{
			System.out.println("Poll UUIDs don't match!");
		}
	}

	public NewPoll getNewPoll(){
		return this.newPoll;
	}

	private String timeSlotToHtml(TimeSlot timeSlot){
		StringBuilder sb = new StringBuilder("<html>");
		sb.append(formatter.format(new Date(timeSlot.getStartTime())));
		sb.append(" <br> to <br> ");
		sb.append(formatter.format(new Date(timeSlot.getEndTime())));
		sb.append("</html>");
		return sb.toString();
	}

	private JLabel getLblName() {
		if (lblName == null) {
			lblName = new JLabel("Name :");
			lblName.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
			lblName.setBounds(42, 6, 55, 21);
		}
		return lblName;
	}
	private JLabel getLblDescripiton() {
		if (lblDescripiton == null) {
			lblDescripiton = new JLabel("Description :");
			lblDescripiton.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
			lblDescripiton.setBounds(6, 28, 98, 21);
		}
		return lblDescripiton;
	}
	private JLabel getLblNameDisplay() {
		if (lblNameDisplay == null) {
			lblNameDisplay = new JLabel("");
			lblNameDisplay.setBackground(Color.LIGHT_GRAY);
			lblNameDisplay.setBounds(109, 9, 335, 16);
		}
		return lblNameDisplay;
	}
	private JLabel getLblDescriptionDisplay() {
		if (lblDescriptionDisplay == null) {
			lblDescriptionDisplay = new JLabel("");
			lblDescriptionDisplay.setBackground(Color.LIGHT_GRAY);
			lblDescriptionDisplay.setBounds(109, 31, 335, 21);
		}
		return lblDescriptionDisplay;
	}
}
