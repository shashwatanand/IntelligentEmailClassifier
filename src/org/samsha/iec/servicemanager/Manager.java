package org.samsha.iec.servicemanager;

//Java packages
import java.io.File;
import java.util.Hashtable;
import java.util.Set;

//samsha packages
import org.samsha.iec.util.SAMSHAUtil;

public class Manager {
	private static Manager thisRef;
	
	private Manager() {
		createDirectoryStructure();
		thisRef = this;
	}
	
	public static Manager getInstance() {
		if (thisRef == null) {
			new Manager();
		}
		return thisRef;
	}
	
	private void createDirectoryStructure() {
		String emailDumpFolderPath = SAMSHAUtil.getMainFolderPath();
		File emailDumpFolder = new File(emailDumpFolderPath);
		if (!emailDumpFolder.exists()) {
			emailDumpFolder.mkdirs();
		}
		String[] userFolderPaths = SAMSHAUtil.getUserFolderPaths();
		for (int index = 0; index < userFolderPaths.length; index++) {
			File userFolder = new File(userFolderPaths[index]);
			if (!userFolder.exists()) {
				userFolder.mkdirs();
			}
		}
	}
	
	public void startMainWatcherThread() {
		MainManager main = new MainManager();
		Thread mainManagerThread = new Thread(main);
		mainManagerThread.setName("Main Folder Watcher Thread");
		mainManagerThread.start();
	}
	
	public void startUserWatcherThread() {
		Hashtable<String, String> userFolders = SAMSHAUtil.getUserFolderPathsInHashTable();
		Hashtable<String, String> propFiles = SAMSHAUtil.getUserPropertiesFile();
		Set<String> keys = userFolders.keySet();
		for (String key : keys) {
			File file = new File(userFolders.get(key));
			UserManager watcher = new UserManager(file, propFiles.get(key));
			Thread userManagerThread = new Thread(watcher);
			userManagerThread.setName(key + " Folder Watcher Thread");
			userManagerThread.start();
		}
	}
}
