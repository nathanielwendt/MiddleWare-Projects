/**
 * 
 */
package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
import buyer.Buyer;
import datechooser.beans.DateChooserCombo;
import events.BidUpdate;
import events.SaleFinalized;
import events.SaleItem;

/**
 * @author raga
 *
 */
public class BuyerGUI extends JFrame {

	/** Bad programming! */
	public boolean autoBid = false;
	public double autoBidMaximum = -1;
	public double bidValue = -1;

	private JPanel contentPane;
	private JPanel publishInterestPanel;
	private JLabel lblItemName;
	private JTextField itemNameTextField;
	private JTextField modifierNameTextField;
	private JLabel lblModifierName;
	private DateChooserCombo dateChooserCombo;
	private JLabel lblItemPurchaseDate;
	private JTextField minimumPriceTextField;
	private JLabel lblMinimumPrice;
	private JLabel lblMaximumPrice;
	private JTextField maximumPriceTextField;
	private JLabel lblPublishInterest;
	private JButton btnPublishInterest;
	private JCheckBox chckbxDCModifierName;
	private JCheckBox chckbxDCMinimumPrice;
	private JCheckBox chckbxDCMaximumPrice;
	private JCheckBox chckbxDCItemPurchaseDate;
	private JLabel lblPublishedInterests;
	private JScrollPane publishedInterestsScrollPane;
	private JTable publishedInterestsTable;
	private JLabel lblMatchingItems;
	private JScrollPane matchingItemsScrollPane;
	private JTable matchingItemsTable;
	private Buyer buyerInstance;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if(Init.VERBOSE){
			//MessageConsole.invokeDebugConsole("Buyer console");
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuyerGUI frame = new BuyerGUI();
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
	public BuyerGUI() {
		super("Buyer GUI");
		buyerInstance = new Buyer(this); //seller object to use its API
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 753, 647);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getPublishInterestPanel());
		contentPane.add(getLblPublishedInterests());
		contentPane.add(getPublishedInterestsScrollPane());
		contentPane.add(getLblMatchingItems());
		contentPane.add(getMatchingItemsScrollPane());
	}
	private JPanel getPublishInterestPanel() {
		if (publishInterestPanel == null) {
			publishInterestPanel = new JPanel();
			publishInterestPanel.setLayout(null);
			publishInterestPanel.setBounds(36, 16, 693, 167);
			publishInterestPanel.add(getLblItemName());
			publishInterestPanel.add(getItemNameTextField());
			publishInterestPanel.add(getModifierNameTextField());
			publishInterestPanel.add(getLblModifierName());
			publishInterestPanel.add(getDateChooserCombo());
			publishInterestPanel.add(getLblItemPurchaseDate());
			publishInterestPanel.add(getMinimumPriceTextField());
			publishInterestPanel.add(getLblMinimumPrice());
			publishInterestPanel.add(getLblMaximumPrice());
			publishInterestPanel.add(getMaximumPriceTextField());
			publishInterestPanel.add(getLblPublishInterest());
			publishInterestPanel.add(getBtnPublishInterest());
			publishInterestPanel.add(getChckbxDCModifierName());
			publishInterestPanel.add(getChckbxDCMinimumPrice());
			publishInterestPanel.add(getChckbxDCMaximumPrice());
			publishInterestPanel.add(getChckbxDCItemPurchaseDate());
		}
		return publishInterestPanel;
	}
	private JLabel getLblItemName() {
		if (lblItemName == null) {
			lblItemName = new JLabel("Item Name");
			lblItemName.setBounds(6, 50, 95, 23);
		}
		return lblItemName;
	}
	private JTextField getItemNameTextField() {
		if (itemNameTextField == null) {
			itemNameTextField = new JTextField();
			itemNameTextField.setColumns(10);
			itemNameTextField.setBounds(105, 47, 134, 28);
		}
		return itemNameTextField;
	}
	private JTextField getModifierNameTextField() {
		if (modifierNameTextField == null) {
			modifierNameTextField = new JTextField();
			modifierNameTextField.setColumns(10);
			modifierNameTextField.setBounds(105, 85, 134, 28);
		}
		return modifierNameTextField;
	}
	private JLabel getLblModifierName() {
		if (lblModifierName == null) {
			lblModifierName = new JLabel("Modifier Name");
			lblModifierName.setBounds(6, 88, 95, 23);
		}
		return lblModifierName;
	}
	private DateChooserCombo getDateChooserCombo() {
		if (dateChooserCombo == null) {
			dateChooserCombo = new DateChooserCombo();
			dateChooserCombo.setBounds(464, 46, 134, 27);
		}
		return dateChooserCombo;
	}
	private JLabel getLblItemPurchaseDate() {
		if (lblItemPurchaseDate == null) {
			lblItemPurchaseDate = new JLabel("Item Purchase Date");
			lblItemPurchaseDate.setBounds(318, 53, 134, 16);
		}
		return lblItemPurchaseDate;
	}
	private JTextField getMinimumPriceTextField() {
		if (minimumPriceTextField == null) {
			minimumPriceTextField = new JTextField();
			minimumPriceTextField.setColumns(10);
			minimumPriceTextField.setBounds(464, 85, 134, 28);
		}
		return minimumPriceTextField;
	}
	private JLabel getLblMinimumPrice() {
		if (lblMinimumPrice == null) {
			lblMinimumPrice = new JLabel("Minimum Price");
			lblMinimumPrice.setBounds(340, 88, 112, 23);
		}
		return lblMinimumPrice;
	}
	private JLabel getLblMaximumPrice() {
		if (lblMaximumPrice == null) {
			lblMaximumPrice = new JLabel("Maximum Price");
			lblMaximumPrice.setBounds(340, 128, 112, 23);
		}
		return lblMaximumPrice;
	}
	private JTextField getMaximumPriceTextField() {
		if (maximumPriceTextField == null) {
			maximumPriceTextField = new JTextField();
			maximumPriceTextField.setColumns(10);
			maximumPriceTextField.setBounds(464, 125, 134, 28);
		}
		return maximumPriceTextField;
	}
	private JLabel getLblPublishInterest() {
		if (lblPublishInterest == null) {
			lblPublishInterest = new JLabel("Publish Interest");
			lblPublishInterest.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblPublishInterest.setBounds(235, 6, 233, 28);
		}
		return lblPublishInterest;
	}
	private JButton getBtnPublishInterest() {
		if (btnPublishInterest == null) {
			btnPublishInterest = new JButton("Publish");
			btnPublishInterest.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(goodToGo() && areCostsValid()){
						try {
							BuyerGUI.this.buyerInstance.publishInterest(itemNameTextField.getText().trim(), 
									chckbxDCModifierName.isSelected() ? SaleItem.MODIFIER_STRING_IGNORE :modifierNameTextField.getText().trim(), 
											chckbxDCItemPurchaseDate.isSelected() ? SaleItem.TIME_STAMP_IGNORE :new SimpleDateFormat("MM/dd/yy").parse(dateChooserCombo.getText()).getTime(), 
													chckbxDCMinimumPrice.isSelected() ? SaleItem.COST_LOWER_BOUND_IGNORE : Double.parseDouble(minimumPriceTextField.getText().trim()), 
															chckbxDCMaximumPrice.isSelected() ? SaleItem.COST_UPPER_BOUND_IGNORE : Double.parseDouble(maximumPriceTextField.getText().trim()));
						} catch (NumberFormatException e1) {
							e1.printStackTrace();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}else{
						JOptionPane.showMessageDialog(null, "Please enter valid values to publish the item.","Invalid format",JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnPublishInterest.setBounds(70, 123, 169, 36);
		}
		return btnPublishInterest;
	}
	private boolean goodToGo(){
		return checkNotNullEmpty(itemNameTextField.getText().trim()) && checkNotNullEmpty(modifierNameTextField.getText().trim())
				&& checkNotNullEmpty(minimumPriceTextField.getText().trim()) && checkNotNullEmpty(maximumPriceTextField.getText().trim())
				&& checkNotNullEmpty(dateChooserCombo.getText().trim());
	}
	private boolean areCostsValid(){
		double lowPrice = 1;
		double highPrice = 1;
		try{
			if(!minimumPriceTextField.getText().equals("Ignore Minimum")){
				lowPrice = Double.parseDouble(minimumPriceTextField.getText().trim());
			}
			if(!maximumPriceTextField.getText().equals("Ignore Maximum")){
				highPrice = Double.parseDouble(maximumPriceTextField.getText().trim());
			}
			if(lowPrice <= 0 || highPrice <= 0){
				throw new Exception();
			}
			return true;
		}catch(Exception e){}
		return false;
	}
	private boolean checkNotNullEmpty(String text){
		return text != null && !text.trim().equals("");
	}
	private JCheckBox getChckbxDCModifierName() {
		if (chckbxDCModifierName == null) {
			chckbxDCModifierName = new JCheckBox("DC");
			chckbxDCModifierName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(chckbxDCModifierName.isSelected()){
						modifierNameTextField.setText("Ignore Modifier");
						modifierNameTextField.setEnabled(false);
					}else{
						modifierNameTextField.setText("");
						modifierNameTextField.setEnabled(true);
					}
				}
			});
			chckbxDCModifierName.setBounds(240, 87, 58, 23);
		}
		return chckbxDCModifierName;
	}
	private JCheckBox getChckbxDCMinimumPrice() {
		if (chckbxDCMinimumPrice == null) {
			chckbxDCMinimumPrice = new JCheckBox("DC");
			chckbxDCMinimumPrice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(chckbxDCMinimumPrice.isSelected()){
						minimumPriceTextField.setText("Ignore Minimum");
						minimumPriceTextField.setEnabled(false);
					}else{
						minimumPriceTextField.setText("");
						minimumPriceTextField.setEnabled(true);
					}
				}
			});
			chckbxDCMinimumPrice.setBounds(610, 87, 58, 23);
		}
		return chckbxDCMinimumPrice;
	}
	private JCheckBox getChckbxDCMaximumPrice() {
		if (chckbxDCMaximumPrice == null) {
			chckbxDCMaximumPrice = new JCheckBox("DC");
			chckbxDCMaximumPrice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(chckbxDCMaximumPrice.isSelected()){
						maximumPriceTextField.setText("Ignore Maximum");
						maximumPriceTextField.setEnabled(false);
					}else{
						maximumPriceTextField.setText("");
						maximumPriceTextField.setEnabled(true);
					}
				}
			});
			chckbxDCMaximumPrice.setBounds(610, 127, 58, 23);
		}
		return chckbxDCMaximumPrice;
	}
	private JCheckBox getChckbxDCItemPurchaseDate() {
		if (chckbxDCItemPurchaseDate == null) {
			chckbxDCItemPurchaseDate = new JCheckBox("DC");
			chckbxDCItemPurchaseDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(chckbxDCItemPurchaseDate.isSelected()){
						dateChooserCombo.setText("Ignore Date");
						dateChooserCombo.setEnabled(false);
					}else{
						dateChooserCombo.setText("");
						dateChooserCombo.setEnabled(true);
					}
				}
			});
			chckbxDCItemPurchaseDate.setBounds(610, 46, 58, 23);
		}
		return chckbxDCItemPurchaseDate;
	}
	private JLabel getLblPublishedInterests() {
		if (lblPublishedInterests == null) {
			lblPublishedInterests = new JLabel("Published Interests");
			lblPublishedInterests.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblPublishedInterests.setBounds(250, 192, 233, 28);
		}
		return lblPublishedInterests;
	}
	private JScrollPane getPublishedInterestsScrollPane() {
		if (publishedInterestsScrollPane == null) {
			publishedInterestsScrollPane = new JScrollPane();
			publishedInterestsScrollPane.setBounds(36, 226, 693, 167);
			publishedInterestsScrollPane.setViewportView(getPublishedInterestsTable());
		}
		return publishedInterestsScrollPane;
	}
	private JTable getPublishedInterestsTable() {
		if (publishedInterestsTable == null) {
			String[] columnNames = {"Item name", "Modifier name", "Purchase Date", "Minimum Price", "Maximum Price"};
			DefaultTableModel model = new DefaultTableModel(new String[][]{},columnNames);
			publishedInterestsTable = new JTable(model);
		}
		return publishedInterestsTable;
	}
	public void addItemToInterestsTable(SaleItem item){
		DefaultTableModel model = (DefaultTableModel) publishedInterestsTable.getModel();
		String date;
		if(item.getTimeStamp() == SaleItem.TIME_STAMP_IGNORE) date = "Ignore Date";
		date = new SimpleDateFormat("MM/dd/yy").format(new Date(item.getTimeStamp()));
		String modifierString = item.getModifierString().equals(SaleItem.MODIFIER_STRING_IGNORE) ? "Ignore Modifier" : item.getModifierString();
		String lowerBound = item.getCostLowerBound() == SaleItem.COST_LOWER_BOUND_IGNORE ? "Ignore Minimum cost" : String.valueOf(item.getCostLowerBound());
		String upperBound = item.getCostUpperBound() == SaleItem.COST_UPPER_BOUND_IGNORE ? "Ignore Maximum cost" : String.valueOf(item.getCostUpperBound());;
		Object[] row = {item.getBaseString(), modifierString, date,lowerBound,upperBound};
		model.addRow(row);
	}
	private JLabel getLblMatchingItems() {
		if (lblMatchingItems == null) {
			lblMatchingItems = new JLabel("Matching Items");
			lblMatchingItems.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblMatchingItems.setBounds(279, 398, 233, 28);
		}
		return lblMatchingItems;
	}
	private JScrollPane getMatchingItemsScrollPane() {
		if (matchingItemsScrollPane == null) {
			matchingItemsScrollPane = new JScrollPane();
			matchingItemsScrollPane.setBounds(36, 427, 693, 182);
			matchingItemsScrollPane.setViewportView(getMatchingItemsTable());
		}
		return matchingItemsScrollPane;
	}
	private Action bidAction = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			int row = Integer.valueOf(e.getActionCommand());
			DefaultTableModel model = (DefaultTableModel) matchingItemsTable.getModel();
			String itemUUID = String.valueOf(model.getValueAt(row, 10));
			BidDialog dialog = new BidDialog(BuyerGUI.this);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setModal(true);
			dialog.setVisible(true);
			if(BuyerGUI.this.bidValue > 0){
				if(!BuyerGUI.this.autoBid){
					buyerInstance.publishBid(itemUUID, bidValue);
				}else{
					buyerInstance.publishAutoBid(itemUUID,BuyerGUI.this.bidValue, BuyerGUI.this.autoBidMaximum);
				}
				JOptionPane.showMessageDialog(null, "Your bid was successfully passed on to the seller.", "Bid Successful",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	};

	private Action subscribeBidAction = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			int row = Integer.valueOf(e.getActionCommand());
			DefaultTableModel model = (DefaultTableModel) matchingItemsTable.getModel();
			String itemUUID = String.valueOf(model.getValueAt(row, 10));
			buyerInstance.interestBidUpdate(itemUUID);
			JOptionPane.showMessageDialog(null, "Successfully subscribed to bids on this item.", "Subscribed to bids",JOptionPane.INFORMATION_MESSAGE);
		}
	};
	private JTable getMatchingItemsTable() {
		if (matchingItemsTable == null) {
			matchingItemsTable = new JTable();
			String[] columnNames = {"Item name", "Modifier name", "Purchase Date", "Minimum Price", 
					"Maximum Price", "Number of bids","Maximum bid", "Maximum bid User", "Bid on Item", "Subscribe to bids", "Item uuid"};
			DefaultTableModel model = new DefaultTableModel(new String[][]{},columnNames);
			matchingItemsTable = new JTable(model);
			ButtonColumn bidButtonColumn = new ButtonColumn(matchingItemsTable, bidAction, 8);
			bidButtonColumn.setMnemonic(KeyEvent.VK_ENTER);
			ButtonColumn autoBidButtonColumn = new ButtonColumn(matchingItemsTable, subscribeBidAction, 9);
			autoBidButtonColumn.setMnemonic(KeyEvent.VK_0);
			matchingItemsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(matchingItemsTable);
			tca.adjustColumns();
			TableColumnManager tcm = new TableColumnManager(matchingItemsTable);
			tcm.hideColumn("Item uuid");
		}
		return matchingItemsTable;
	}
	public void addMatchingItemToTable(SaleItem item){
		DefaultTableModel model = (DefaultTableModel) matchingItemsTable.getModel();
		String date = new SimpleDateFormat("MM/dd/yy").format(new Date(item.getTimeStamp()));
		Object[] row = {item.getBaseString(), item.getModifierString(), date,item.getCostLowerBound(),
				item.getCostUpperBound(),0,0,"","Bid on Item","Subscribe to bids", item.getUuid()};
		model.addRow(row);
	}
	public void updateTableUsingBidUpdate(BidUpdate update){
		DefaultTableModel model = (DefaultTableModel) matchingItemsTable.getModel();
		for(int i=0; i<model.getRowCount(); i++){
			if(model.getValueAt(i, 10).equals(update.getItemUUID())){
				int currentValue = Integer.parseInt(String.valueOf(model.getValueAt(i, 5)));
				currentValue++;
				model.setValueAt(currentValue, i, 5);
				double maxBid =  Double.parseDouble(String.valueOf(model.getValueAt(i, 6)));
				model.setValueAt( (maxBid > update.getBidUpdateValue()) ? maxBid : update.getBidUpdateValue(), i, 6);
				if(maxBid < update.getBidUpdateValue()){
					model.setValueAt(update.getBidderUUID(), i, 7);
				}
			}
		}
	}
	public void notifySaleFinalizedToUser(SaleFinalized update){
		DefaultTableModel model = (DefaultTableModel) matchingItemsTable.getModel();
		int toBeRemoved = -1;
		for(int i=0; i<model.getRowCount(); i++){
			if(model.getValueAt(i, 10).equals(update.getItemUUID())){
				toBeRemoved = i;
			}
		}
		String notification = model.getValueAt(toBeRemoved, 0) + "," + model.getValueAt(toBeRemoved, 1) + "," +
				model.getValueAt(toBeRemoved, 2) + " has been sold to " + update.getBuyerId() + " for $" + update.getSaleValue();
		model.removeRow(toBeRemoved);
		JOptionPane.showMessageDialog(null,notification, "Item Sold",JOptionPane.INFORMATION_MESSAGE);
	}

}
