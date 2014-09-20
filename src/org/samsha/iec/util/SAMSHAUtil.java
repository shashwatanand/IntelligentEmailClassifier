package org.samsha.iec.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.samsha.iec.ui.LiveDataConsole;
import org.samsha.iec.ui.MainConsoleWindow;
import org.w3c.dom.Document;

public class SAMSHAUtil {
	/**
	 * The method displays a dialog with useful information when application
	 * exits
	 * @param displayMessage String
	 */
	public static synchronized void exitApplication(String displayMessage) {
		MainConsoleWindow appWindow = MainConsoleWindow.getInstance();
		if (appWindow != null) {
			appWindow.setVisible(false);
			appWindow.dispose();
		}
		if (StringUtil.emptyString(displayMessage))
			System.exit(0);
		else {
			destroyRunningThreads();
			JOptionPane.showMessageDialog(null, displayMessage, "Intelligent Email Classifier", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}
	/**
	 * destroy all running threads except the one showing the application exit
	 * dialog.
	 */
	private static void destroyRunningThreads() {
		Thread currentThread = Thread.currentThread();
		ThreadGroup group = currentThread.getThreadGroup();
		while (group.getParent() != null) {
			group = group.getParent();
		}
		Thread[] allThreads = new Thread[group.activeCount()];
		group.enumerate(allThreads);
		for (Thread t : allThreads) {
			if (!t.equals(currentThread))
				t.interrupt();
		}
	}
	
	public static void moveFileTo(String fromPath, String toPath) {
		try {
			File fromFile = new File(fromPath);
			String fileName = extractFileName(fromPath);
			File toFile = new File(toPath + "\\" + fileName);
			InputStream inStream = new FileInputStream(fromFile);
			OutputStream outStream = new FileOutputStream(toFile);
			streamAndClose(inStream, outStream);
			
			StringBuilder message = new StringBuilder();
			message.append("File : ").append(fileName)
				.append(" moved from : ").append(fromPath)
				.append(" to : ").append(toPath);
			LiveDataConsole.getInstance().setLiveData(message.toString());
			
			System.out.print("File deleted : " + fromFile.delete());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static String extractFileName(String filePath) {
		return filePath.substring(filePath.lastIndexOf("\\"));
	}
	
	public static void streamAndClose(InputStream in, OutputStream out) throws IOException {
		try {
			stream(in, out);
		} finally {
			try {
				in.close();
			} finally {
				out.close();
			}
		}
	}
	
	private static void stream(InputStream in, OutputStream out) throws IOException {
		// Copy the input stream to the output stream
		final int BUFFER_SIZE = 1024;
		byte[] buffer = new byte[BUFFER_SIZE];
		int len = buffer.length;
		while (true) {
			len = in.read(buffer);
			if (len == -1) {
				break;
			}
			out.write(buffer, 0, len);
		}
		out.flush();
	}
	
	public static String getMainFolderPath() {
		return (System.getProperty("user.home") + ConfigProperties.getInstance().getProperty("MainFolderPath"));
	}
	
	public static String[] getUserFolderPaths() {
		ConfigProperties config = ConfigProperties.getInstance();
		String userHomeDir = System.getProperty("user.home");
		int noOfUsers = config.getIntProperty("NumberOfUsers");
		String[] userFolderPaths = new String[noOfUsers];
		for (int index = 0; index < noOfUsers; index++) {
			String userFolderPath = userHomeDir + config.getProperty("User" + (index + 1) + "Folder");
			userFolderPaths[index] = userFolderPath;
		}
		return userFolderPaths;
	}
	
	public static Hashtable<String, String> getUserPropertiesFile() {
		Hashtable<String, String> propFiles = new Hashtable<String, String>();
		int noOfUsers = ConfigProperties.getInstance().getIntProperty("NumberOfUsers");
		for (int index = 0; index < noOfUsers; index++) {
			String userName = "user" + (index + 1);
			propFiles.put(userName, "/org/samsha/iec/resources/" + userName + ".properties");
		}
		return propFiles;
	}
	
	public static Hashtable<String, String> getUserFolderPathsInHashTable() {
		Hashtable<String, String> userFolders = new Hashtable<String, String>();
		ConfigProperties config = ConfigProperties.getInstance();
		String userHomeDir = System.getProperty("user.home");
		int noOfUsers = config.getIntProperty("NumberOfUsers");
		for (int index = 0; index < noOfUsers; index++) {
			String userFolderPath = userHomeDir + config.getProperty("User" + (index + 1) + "Folder");
			userFolders.put("user" + (index+1), userFolderPath);
		}
		return userFolders;
	}
	
	public static Document getXmlDocument(File file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			return doc;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
