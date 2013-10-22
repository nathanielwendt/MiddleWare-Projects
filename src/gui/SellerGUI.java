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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import seller.Seller;
import setup.Init;
import datechooser.beans.DateChooserCombo;
import events.Bid;
import events.SaleItem;


public class SellerGUI extends JFrame {

	private JPanel contentPane;
	private DateChooserCombo dateComboBox;
	private JPanel panel;
	private JLabel lblItemName;
	private JTextField itemNameTextField;
	private JTextField modifierNameTextField;
	private JLabel lblModifierName;
	private JLabel lblItemPurchaseDate;
	private JTextField minimumPriceTextField;
	private JLabel lblItemMinimumPrice;
	private JLabel lblItemMaximumPrice;
	private JTextField maximumPriceTextField;
	private JLabel lblPublishAvailableItem;
	private JButton btnPublish;
	private JScrollPane publishedItemsScrollPane;
	private JLabel lblPublishedItems;
	private JTable publishedItemsTable;

	private Seller sellerInstance;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if(Init.VERBOSE){
			MessageConsole.invokeDebugConsole("Seller console");
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SellerGUI frame = new SellerGUI();
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
	public SellerGUI() {
		super("Seller GUI");
		sellerInstance = new Seller(this); //seller object to use its API
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 571, 446);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getPanel());
		contentPane.add(getPublishedItemsScrollPane());
		contentPane.add(getLblPublishedItems());
	}
	private DateChooserCombo getDateComboBox() {
		if (dateComboBox == null) {
			dateComboBox = new DateChooserCombo();
			dateComboBox.setBounds(386, 47, 153, 27);
		}
		return dateComboBox;
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBounds(20, 21, 545, 167);
			panel.setLayout(null);
			panel.add(getLblItemName());
			panel.add(getItemNameTextField());
			panel.add(getModifierNameTextField());
			panel.add(getLblModifierName());
			panel.add(getDateComboBox());
			panel.add(getLblItemPurchaseDate());
			panel.add(getMinimumPriceTextField());
			panel.add(getLblItemMinimumPrice());
			panel.add(getLblItemMaximumPrice());
			panel.add(getMaximumPriceTextField());
			panel.add(getLblPublishAvailableItem());
			panel.add(getBtnPublish());
		}
		return panel;
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
			itemNameTextField.setBounds(105, 47, 134, 28);
			itemNameTextField.setColumns(10);
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
	private JLabel getLblItemPurchaseDate() {
		if (lblItemPurchaseDate == null) {
			lblItemPurchaseDate = new JLabel("Item Purchase Date");
			lblItemPurchaseDate.setBounds(251, 53, 134, 16);
		}
		return lblItemPurchaseDate;
	}
	private JTextField getMinimumPriceTextField() {
		if (minimumPriceTextField == null) {
			minimumPriceTextField = new JTextField();
			minimumPriceTextField.setColumns(10);
			minimumPriceTextField.setBounds(386, 85, 134, 28);
		}
		return minimumPriceTextField;
	}
	private JLabel getLblItemMinimumPrice() {
		if (lblItemMinimumPrice == null) {
			lblItemMinimumPrice = new JLabel("Minimum Price");
			lblItemMinimumPrice.setBounds(251, 88, 112, 23);
		}
		return lblItemMinimumPrice;
	}
	private JLabel getLblItemMaximumPrice() {
		if (lblItemMaximumPrice == null) {
			lblItemMaximumPrice = new JLabel("Maximum Price");
			lblItemMaximumPrice.setBounds(251, 128, 112, 23);
		}
		return lblItemMaximumPrice;
	}
	private JTextField getMaximumPriceTextField() {
		if (maximumPriceTextField == null) {
			maximumPriceTextField = new JTextField();
			maximumPriceTextField.setColumns(10);
			maximumPriceTextField.setBounds(386, 125, 134, 28);
		}
		return maximumPriceTextField;
	}
	private JLabel getLblPublishAvailableItem() {
		if (lblPublishAvailableItem == null) {
			lblPublishAvailableItem = new JLabel("Publish Available Item");
			lblPublishAvailableItem.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblPublishAvailableItem.setBounds(152, 6, 233, 28);
		}
		return lblPublishAvailableItem;
	}
	private JButton getBtnPublish() {
		if (btnPublish == null) {
			btnPublish = new JButton("Publish");
			btnPublish.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(goodToGo() && areCostsValid()){
						try {
							SellerGUI.this.sellerInstance.publishAvailableItem(itemNameTextField.getText().trim(), 
									modifierNameTextField.getText().trim(), 
									new SimpleDateFormat("MM/dd/yy").parse(dateComboBox.getText()).getTime(), 
									Double.parseDouble(minimumPriceTextField.getText().trim()), 
									Double.parseDouble(maximumPriceTextField.getText().trim()));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}else{
						JOptionPane.showMessageDialog(null, "Please enter valid values to publish the item.","Invalid format",JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnPublish.setBounds(70, 123, 169, 36);
		}
		return btnPublish;
	}
	private JScrollPane getPublishedItemsScrollPane() {
		if (publishedItemsScrollPane == null) {
			publishedItemsScrollPane = new JScrollPane();
			publishedItemsScrollPane.setBounds(6, 230, 559, 188);
			publishedItemsScrollPane.setViewportView(getPublishedItemsTable());
		}
		return publishedItemsScrollPane;
	}
	private JLabel getLblPublishedItems() {
		if (lblPublishedItems == null) {
			lblPublishedItems = new JLabel("Published Items");
			lblPublishedItems.setFont(new Font("Lucida Grande", Font.BOLD, 20));
			lblPublishedItems.setBounds(200, 190, 233, 28);
		}
		return lblPublishedItems;
	}
	private boolean goodToGo(){
		return checkNotNullEmpty(itemNameTextField.getText().trim()) && checkNotNullEmpty(modifierNameTextField.getText().trim())
				&& checkNotNullEmpty(minimumPriceTextField.getText().trim()) && checkNotNullEmpty(maximumPriceTextField.getText().trim())
				&& checkNotNullEmpty(dateComboBox.getText().trim());
	}
	private boolean areCostsValid(){
		double lowPrice;
		double highPrice;
		try{
			lowPrice = Double.parseDouble(minimumPriceTextField.getText().trim());
			highPrice = Double.parseDouble(maximumPriceTextField.getText().trim());
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
	private Action finalizeSaleAction = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			int row = Integer.valueOf(e.getActionCommand());
			ListTableModel model = (ListTableModel) publishedItemsTable.getModel();
			SellerGUI.this.sellerInstance.publishSaleFinalized(String.valueOf(model.getValueAt(row,7)), 
					String.valueOf(model.getValueAt(row,9)), 
					Double.parseDouble(String.valueOf(model.getValueAt(row,6))));
			model.removeRows(row);
			JOptionPane.showMessageDialog(null, "The item has been finalized and all the bidders have been notified about the final bid.", "Finalized",JOptionPane.INFORMATION_MESSAGE);
		}
	};
	private JTable getPublishedItemsTable() {
		if (publishedItemsTable == null) {
			String[] columnNames = {"Item name", "Modifier name", "Purchase Date", "Minimum Price", 
					"Maximum Price", "Number of bids","Maximum bid", "Maximum bid User", "Finalize Item", "Item uuid"};
			ListTableModel model = new ListTableModel(Arrays.asList(columnNames));
			model.setModelEditable(false);
			publishedItemsTable = new JTable(model);
			ButtonColumn buttonColumn = new ButtonColumn(publishedItemsTable, finalizeSaleAction, 8);
			buttonColumn.setMnemonic(KeyEvent.VK_ENTER);
			publishedItemsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(publishedItemsTable);
			tca.adjustColumns();
			TableColumnManager tcm = new TableColumnManager(publishedItemsTable);
			tcm.hideColumn("Item uuid");
		}
		return publishedItemsTable;
	}
	public void addItemToTable(SaleItem item){
		ListTableModel model = (ListTableModel) publishedItemsTable.getModel();
		String date = new SimpleDateFormat("MM/dd/yy").format(new Date(item.getTimeStamp()));
		Object[] row = {item.getBaseString(), item.getModifierString(), date,item.getCostLowerBound(),
				item.getCostUpperBound(),0,0,"","Finalize", item.getUuid()};
		model.addRow(row);
	}
	public void updateTableDataAccordingToBid(Bid bid){
		ListTableModel model = (ListTableModel) publishedItemsTable.getModel();
		for(int i=0; i<model.getRowCount(); i++){
			if(model.getValueAt(i, 9).equals(bid.getItemUUID())){
				int currentValue = Integer.parseInt(String.valueOf(model.getValueAt(i, 5)));
				currentValue++;
				model.setValueAt(currentValue, i, 5);
				double maxBid =  Double.parseDouble(String.valueOf(model.getValueAt(i, 6)));
				model.setValueAt( (maxBid > bid.getBidValue()) ? maxBid : bid.getBidValue(), i, 6);
				if(maxBid < bid.getBidValue()){
					model.setValueAt(bid.getBidderUUID(), i, 7);
				}
			}
		}
	}
}
