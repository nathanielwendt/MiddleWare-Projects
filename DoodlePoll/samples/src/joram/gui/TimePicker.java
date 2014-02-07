package gui;
import java.util.*;
import java.awt.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.*;
import com.lavantech.gui.comp.*;

public class TimePicker extends JPanel implements ActionListener, DateUnavailabilityModel{
	private DateTimePicker picker1;

	public TimePicker(){
		super(new BorderLayout());
		JPanel gridPanel = new JPanel(new CompactGridLayout(0,2));
		add(gridPanel,BorderLayout.NORTH);
		GregorianCalendar cal = new GregorianCalendar();
		picker1 = new DateTimePicker(cal, "dd-MM-yyyy HH:mm:ss");
		gridPanel.add(picker1);
		picker1.addActionListener(this);
		picker1.setDateUnavailabilityModel(this);
		picker1.setDisplayTodayButton(false);
	}

	public void actionPerformed(ActionEvent evt) {
		DateTimePicker picker = (DateTimePicker)evt.getSource();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		if(picker.getCalendar() != null){
			formatter.setCalendar(((DateTimePicker)evt.getSource()).getCalendar());
			System.out.println(formatter.format(((DateTimePicker)evt.getSource()).getCalendar().getTime()));
			System.out.println(getCurrentTime());
		}
	}
	
	public Date getCurrentTime(){
		return picker1.getCalendar().getTime();
	}

	public int[] getUnavailableDaysInAMonth(int month, int year){
		Vector<Integer> unavailableDays = new Vector<Integer>();
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		int firstday = cal.getActualMinimum(Calendar.DATE);
		int lastday = cal.getActualMaximum(Calendar.DATE);
		for(int i=firstday; i<= lastday; i++)
		{
			cal.set(Calendar.DATE, i);
			int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
			if(dayofweek == Calendar.SUNDAY || dayofweek == Calendar.SATURDAY)
				unavailableDays.add(new Integer(i));
		}
		int[] retval = new int[unavailableDays.size()];
		for(int i=0; i<retval.length; i++)
			retval[i] = ((Integer)(unavailableDays.elementAt(i))).intValue();
		return retval;
	}
}
