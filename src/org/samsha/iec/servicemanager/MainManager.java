package org.samsha.iec.servicemanager;

//java packages
import java.io.File;
import java.util.Hashtable;
import java.util.UUID;

//samsha packages
import org.samsha.iec.db.ServerProxy;
import org.samsha.iec.ui.LiveDataConsole;
import org.samsha.iec.util.OutputResult;
import org.samsha.iec.util.SAMSHAUtil;
import org.w3c.dom.Document;
/**
 * @author Samiksha
 * @author Shashwat
 */
public class MainManager implements Runnable {
	private File mainFolder;
	public MainManager() {
		this.mainFolder = new File(SAMSHAUtil.getMainFolderPath());
	}
	
	@Override
	public void run() {
		int SLEEP_TIME = 5000;
		while (true) {
			try {
				if (this.mainFolder == null)
					return;
				//Scanning for files
				File[] currentFoundFiles = this.mainFolder.listFiles();
				if (currentFoundFiles.length != 0 || currentFoundFiles != null) {
					//loops through all files
					for (File file : currentFoundFiles) {
						String newFilePath = this.mainFolder.getAbsolutePath() + "\\" + UUID.randomUUID().toString() + ".xml";
						File newFile = new File(newFilePath);
						//rename the file to UUID
						file.renameTo(newFile);
						Document doc = SAMSHAUtil.getXmlDocument(newFile);
						EmailDoc emailDoc = new EmailDoc(doc, newFilePath);
						
						OutputResult result = ServerProxy.insertUpdateEmailTuple("User Folder", emailDoc.getFileName().trim(), 
								emailDoc.getTo().trim(), emailDoc.getFrom().trim());
						if (result.hasFailed()) {
							LiveDataConsole.getInstance().setLiveData(result.getShortErrorMessage());
						} else {
							SAMSHAUtil.moveFileTo(newFilePath, getUserFolder(emailDoc));
						}
					}
				}
				Thread.sleep(SLEEP_TIME);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private String getUserFolder(EmailDoc emailDoc) {
		String userName = getUserName(emailDoc.getTo().trim());
		Hashtable<String, String> userFolders = SAMSHAUtil.getUserFolderPathsInHashTable();
		return userFolders.get(userName);
		/*String[] userPaths = SAMSHAUtil.getUserFolderPaths();
		for (String userFolder : userPaths) {
			if (userFolder.contains(userName)) {
				return userFolder;
			}
		}
		return null;*/
	}
	
	private String getUserName(String emailToField) {
		return emailToField.split("@")[0];
	}
}
