package org.samsha.iec.ui;

//java packages
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sun.awt.AWTUtilities;
import com.sun.awt.AWTUtilities.Translucency;

//samsha packages
import org.samsha.iec.db.ServerProxy;
import org.samsha.iec.servicemanager.Manager;
import org.samsha.iec.util.SAMSHAUtil;

/**
 * This is main UI class of project
 * @author Samiksha
 * @author Shashwat
 */
public class MainConsoleWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static MainConsoleWindow thisRef;
	
	public MainConsoleWindow() {
		setTitle("Intelligent Email Classifier");
		thisRef = this;
		
		int width = 800;
		int height = 600;
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setSize(width, height);
		setLocation((int)(screenSize.getWidth() - width)/2, (int)(screenSize.getHeight() - height)/2);
		
		if (AWTUtilities.isTranslucencySupported(Translucency.TRANSLUCENT)) {
			AWTUtilities.setWindowOpacity(this, 0.90f);
		}
		
		setLayout(new BorderLayout());
		this.add(this.createGui());
		setVisible(true);
		addWindowListener(new MainWindowListener());
		initialize();
		try {
			ServerProxy.isConnected();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void initialize() {
		loadLibrary("sqljdbc_auth.dll");
	}
	
	public void loadLibrary(String fileName) {
		File tempFile = null;
		try {
			InputStream in = this.getClass().getResourceAsStream("/org/samsha/iec/resources/" + fileName);
			/*String javaHome = System.getenv("JAVA_HOME");
			if (!StringUtil.emptyString(javaHome)) {
				tempFile = new File(javaHome + "\\bin\\" + fileName);
			} else {
				tempFile = new File(fileName);
			}*/
			tempFile = new File(fileName);
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			}
			SAMSHAUtil.streamAndClose(in, new FileOutputStream(tempFile));
			tempFile.deleteOnExit();
			String absolutePath = tempFile.getAbsolutePath();
			int lastSperatorIndex = absolutePath.lastIndexOf("\\");
			int dotIndex = absolutePath.lastIndexOf(".");
			String libraryName = absolutePath.substring(lastSperatorIndex + 1, dotIndex);
			LiveDataConsole.getInstance().setLiveData("tempfile absolute path : " + tempFile.getAbsolutePath() + ", Library name : " + libraryName);
			System.loadLibrary(libraryName);
		} catch (Exception ex) {
			String message = "Couldn't load the library : " + fileName + " where tempfile absolute path : " + tempFile.getAbsolutePath() + " : " + ex.getMessage();
			LiveDataConsole.getInstance().setLiveData(message);
		}
	}
	
	private JPanel createGui() {
		JPanel jpMain = new JPanel(new BorderLayout());
		LiveDataConsole liveDataConsole = LiveDataConsole.getInstance();
		JScrollPane consoleScrollPane = new JScrollPane(createMainWindow(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, consoleScrollPane, liveDataConsole);
		vSplitPane.setDividerLocation(375);
		vSplitPane.setResizeWeight(0.75);
		
		jpMain.add(vSplitPane, BorderLayout.CENTER);
		return jpMain;
	}
	
	private JPanel createMainWindow() {
		JPanel jpConsole = new JPanel();
		JToggleButton jtbStart, jtbStop;
		jpConsole.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		jtbStart = new JToggleButton("Start Service");
		jtbStart.setToolTipText("<html><FONT FACE=\"courier\" COLOR=\"blue\">Start Service</font></html>");
		jtbStart.setFont(new Font("null", Font.BOLD, 18));
		jtbStart.addActionListener(new ToggleButtonListener());
		jpConsole.add(jtbStart);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		jtbStop = new JToggleButton("Stop Service", true);
		jtbStop.setToolTipText("<html><FONT FACE=\"courier\" COLOR=\"blue\">Stop Service</font></html>");
		jtbStop.setFont(new Font("null", Font.BOLD, 18));
		jtbStop.addActionListener(new ToggleButtonListener());
		jpConsole.add(jtbStop);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(jtbStart);
		buttonGroup.add(jtbStop);
		
		return jpConsole;
	}

	public static MainConsoleWindow getInstance() {
		return thisRef;
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainConsoleWindow();
			}
		});
	}
	
	private class ToggleButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source instanceof JToggleButton && ((JToggleButton)source).getText().equalsIgnoreCase("Start Service")) {
				System.out.println("Started");
				Manager.getInstance().startMainWatcherThread();
				Manager.getInstance().startUserWatcherThread();
				return;
			}
			if (source instanceof JToggleButton && ((JToggleButton)source).getText().equalsIgnoreCase("Stop Service")) {
				System.out.println("Stop");
				SAMSHAUtil.exitApplication("Application Exiting....");
				return;
			}
		}
	}
	
	private class MainWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			ServerProxy.disconnect();
			System.exit(0);
		}
	}
}
