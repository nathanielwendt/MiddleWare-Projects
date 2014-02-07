/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author raga
 *
 */
public class BidDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField bidValueTextField;
	private JCheckBox chckbxAutoBid;
	private JLabel lblMaxBidValue;
	private JLabel lblPleaseEnterYour;
	private JTextField maxBidValueTextField;
	private BuyerGUI parent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			BidDialog dialog = new BidDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public BidDialog(final BuyerGUI parent) {
		this.parent = parent;
		setModal(true);
		setBounds(100, 100, 384, 156);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		lblPleaseEnterYour = new JLabel("Please enter your bid value - ");
		lblPleaseEnterYour.setBounds(23, 16, 196, 16);
		contentPanel.add(lblPleaseEnterYour);
		contentPanel.add(getBidValueTextField());
		contentPanel.add(getChckbxAutoBid());
		contentPanel.add(getLblMaxBidValue());
		contentPanel.add(getMaxBidValueTextField());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try{
							parent.autoBid = chckbxAutoBid.isSelected();
							if(parent.autoBid){
								parent.autoBidMaximum = Double.parseDouble(maxBidValueTextField.getText().trim());
							}
							parent.bidValue = Double.parseDouble(bidValueTextField.getText().trim());
						}catch(Exception e1){
							JOptionPane.showMessageDialog(null, "Please enter valid values for the bid!", 
									"Error with bid",JOptionPane.INFORMATION_MESSAGE);
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						parent.autoBid = false;
						parent.bidValue = -1;
						parent.autoBidMaximum = -1;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	private JTextField getBidValueTextField() {
		if (bidValueTextField == null) {
			bidValueTextField = new JTextField();
			bidValueTextField.setBounds(228, 10, 134, 28);
			bidValueTextField.setColumns(10);
		}
		return bidValueTextField;
	}
	private JCheckBox getChckbxAutoBid() {
		if (chckbxAutoBid == null) {
			chckbxAutoBid = new JCheckBox("Auto bid");
			chckbxAutoBid.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chckbxAutoBid.isSelected()){
						maxBidValueTextField.setEnabled(true);
						lblPleaseEnterYour.setText("Set increment value -");
					}else{
						maxBidValueTextField.setEnabled(false);
						lblPleaseEnterYour.setText("Please enter your bid value - ");
					}
				}
			});
			chckbxAutoBid.setBounds(16, 44, 128, 23);
		}
		return chckbxAutoBid;
	}
	private JLabel getLblMaxBidValue() {
		if (lblMaxBidValue == null) {
			lblMaxBidValue = new JLabel("Max Bid Value -");
			lblMaxBidValue.setBounds(120, 48, 99, 16);
		}
		return lblMaxBidValue;
	}
	private JTextField getMaxBidValueTextField() {
		if (maxBidValueTextField == null) {
			maxBidValueTextField = new JTextField();
			maxBidValueTextField.setColumns(10);
			maxBidValueTextField.setBounds(228, 42, 134, 28);
			maxBidValueTextField.setEnabled(false);
		}
		return maxBidValueTextField;
	}
}
