package org.samsha.iec.ui;

//Java packages
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
/**
 * This is UI class which make read only TextArea to show some events, logs, errors etc...
 * @author Samiksha
 * @author Shashwat
 */
public class LiveDataConsole extends JPanel {
	private static final long serialVersionUID = 1L;
	SimpleDateFormat dateFormat = new SimpleDateFormat("E MM/dd/yyyy HH:mm:ss");
	private static LiveDataConsole thisRef;
	private JTextArea jtaLiveData;
	private JLabel currentDateTime;
	Calendar calendar = Calendar.getInstance();
	
	private LiveDataConsole() {
		thisRef = this;
		setLayout(new BorderLayout());
		this.add(createTop(), BorderLayout.NORTH);
		this.add(createBottom(), BorderLayout.CENTER);
		
		setLiveData("System Online ***");
	}
	
	private JPanel createTop() {
		JPanel jpTop = new JPanel(new BorderLayout());
		
		currentDateTime = new JLabel(dateFormat.format(calendar.getTime()), SwingConstants.RIGHT);
		currentDateTime.setPreferredSize(new Dimension(150, 20));
		jpTop.add(currentDateTime, BorderLayout.EAST);
		
		Timer timer = new Timer();
		TimerTask timerTask = new DateTimerTask();
		timer.scheduleAtFixedRate(timerTask, 1000, 1000);
		jpTop.setPreferredSize(new Dimension(150, 20));
		return jpTop;
	}
	
	private Component createBottom() {
		jtaLiveData = new JTextArea();
		jtaLiveData.setFont(getFont());
		jtaLiveData.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(jtaLiveData, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return scrollPane;
	}
	
	public static LiveDataConsole getInstance() {
		if (thisRef == null) {
			new LiveDataConsole();
		}
		return thisRef;
	}
	
	public void setLiveData(String message) {
		StringBuffer stb = new StringBuffer(jtaLiveData.getText());
		stb.append(dateFormat.format(calendar.getTime())).append(" ").append(message).append("\n");
		jtaLiveData.setText(stb.toString());
		jtaLiveData.setCaretPosition(jtaLiveData.getText().length());
	}
	
	private class DateTimerTask extends TimerTask {
		public void run() {
			calendar.setTimeInMillis(System.currentTimeMillis());
			currentDateTime.setText(dateFormat.format(calendar.getTime()));
		}
	}
}
