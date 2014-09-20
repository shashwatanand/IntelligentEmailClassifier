package org.samsha.iec.util;

//java packages
import java.util.Properties;

public class UserProperties extends Properties {	
	private static final long serialVersionUID = 1L;

	public UserProperties(String path) {
		try {
			this.load(this.getClass().getResourceAsStream(path));
		} catch (Exception ex) {
		}
	}
	
	public int getIntProperty(String key) {
		return Integer.parseInt(getProperty(key));
	}
	
	public String getSpamKeywords() {
		return getProperty("spam");
	}
	
	public String getPlaceNames() {
		return getProperty("place");
	}
	
	public String getSpamFolderPath() {
		return (System.getProperty("user.home") + getProperty("spamfolder"));
	}
	
	public String getPlaceFolderPath() {
		return (System.getProperty("user.home") + getProperty("placefolder"));
	}
}
