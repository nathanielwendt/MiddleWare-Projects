package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import datastructures.NewPoll;
import datastructures.Poll;
import datastructures.PollInquiry;
import datastructures.SlotVal;
import datastructures.TimeSlot;

/**
 * @author raga
 *
 */
public class TestGUI extends JFrame {

	private JPanel contentPane;
	private JScrollPane scrollPane;
	private ScrollablePanel scrollPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestGUI frame = new TestGUI();
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
	public TestGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 826, 455);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getScrollPane());
		
		// Code to add dynamically.
		JLabel lab = new JLabel(new Date().toString());
		lab.setUI(new VerticalLabelUI());
		scrollPanel.add(lab);
		contentPane.add(getPanel());
		scrollPanel.revalidate();
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(34, 6, 167, 242);
			scrollPane.setViewportView(getScrollPanel());
		}
		return scrollPane;
	}
	private ScrollablePanel getScrollPanel() {
		if (scrollPanel == null) {
			scrollPanel = new ScrollablePanel();
			scrollPanel.setLayout(new WrapLayout());
			scrollPanel.setScrollableWidth( ScrollablePanel.ScrollableSizeHint.FIT );
			scrollPanel.setScrollableBlockIncrement(
					ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 150);
			
			for(int i=0;i<10;i++){
				JButton but = new JButton("Publish");
				but.setPreferredSize(new Dimension(175,40));
				JButton but1 = new JButton("Publish");
				but1.setPreferredSize(new Dimension(175,40));
				JButton but2 = new JButton("Publish");
				but2.setPreferredSize(new Dimension(100,40));
				scrollPanel.add(but);
				scrollPanel.add(but1);
				scrollPanel.add(but2);
			}
			
		}
		return scrollPanel;
	}
	
	private JPanel panel;
	private JPanel getPanel() {
		if (panel == null) {
			ArrayList<String> recipients = new ArrayList<String>();
			recipients.add("raga@mit.edu");
			recipients.add("raga@gmail.edu");
			recipients.add("raga@yahoo.com");
			Poll poll = new Poll("poll name","Poll description");
			poll.addTimeSlot(new TimeSlot(SlotVal.YES,new Date().getTime(),new Date().getTime()));
			poll.addTimeSlot(new TimeSlot(SlotVal.NO,new Date().getTime(),new Date().getTime()));
			poll.addTimeSlot(new TimeSlot(SlotVal.MAYBE,new Date().getTime(),new Date().getTime()));
			poll.addTimeSlot(new TimeSlot(SlotVal.MAYBE,new Date().getTime(),new Date().getTime()));
			NewPoll newPoll = new NewPoll("myself",poll,recipients);
			
			panel = new PollGui("raga@gmail.com",newPoll,null);
			PollInquiry pi = new PollInquiry(poll,"raga@mit.edu");
			((PollGui)panel).updateTableForPollInquiry(pi);
			panel.setBounds(205, 6, 460, 251);
		}
		return panel;
	}
}
