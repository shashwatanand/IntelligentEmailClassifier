package org.samsha.iec.servicemanager;

//java packages
import java.io.File;
import java.util.HashMap;
import java.util.Set;

//samsha packages
import org.samsha.iec.db.ServerProxy;
import org.samsha.iec.ui.LiveDataConsole;
import org.samsha.iec.util.OutputResult;
import org.samsha.iec.util.SAMSHAUtil;
import org.samsha.iec.util.UserProperties;

/**
 * The class which implements UserWatcher Threads
 * @author Shashwat
 */
public class UserManager implements Runnable {
	private File watcherUserFolder;
	private UserProperties props;
	private HashMap<String, String> labels;
	
	public UserManager(File userFolder, String propFilePath) {
		this.watcherUserFolder = userFolder;
		this.props = new UserProperties(propFilePath);
		createUserListedFolder();
		System.out.println(this.props.toString());
	}
	
	private void createUserListedFolder() {
		this.labels = new HashMap<String, String>();
		String userRoot = System.getProperty("user.home");
		String[] labels = this.props.getProperty("labels").split(";");
		for (String label : labels) {
			String folder = label + "folder";
			this.labels.put(label, userRoot + this.props.getProperty(folder));
		}
		Set<String> keys = this.labels.keySet();
		for (String key : keys) {
			File file = new File(this.labels.get(key));
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	@Override
	public void run() {
		int SLEEP_TIME = 5000;
		while (true) {
			try {
				if (this.watcherUserFolder == null)
					return;
				File[] currentFoundFiles = this.watcherUserFolder.listFiles();
				if (currentFoundFiles.length == 0 || currentFoundFiles != null) {
					for (File file : currentFoundFiles) {
						if (!file.isDirectory() && file != null && file.exists()) {
							EmailDoc emailDoc = new EmailDoc(SAMSHAUtil.getXmlDocument(file));
							String emailMessage = emailDoc.getMessage();
							String[] labels = this.props.getProperty("labels").split(";");
							for (String label : labels) {
								String[] keywords = this.props.getProperty(label).split(";");
								for (String keyword : keywords) {
									if (emailMessage.toUpperCase().contains(keyword.toUpperCase())) {
										System.out.println(emailMessage);
										System.out.println(keyword);
										OutputResult result = ServerProxy.insertUpdateEmailTuple(label, emailDoc.getFileName().trim(), 
												emailDoc.getTo().trim(), emailDoc.getFrom().trim());
										if (result.hasFailed()) {
											LiveDataConsole.getInstance().setLiveData(result.getShortErrorMessage());
										} else {
											SAMSHAUtil.moveFileTo(file.getAbsolutePath(), this.labels.get(label));
											break;
										}
									}
								}
							}
						}
					}
				}
				Thread.sleep(SLEEP_TIME);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
