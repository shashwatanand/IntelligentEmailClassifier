package org.samsha.iec.util;

import java.util.Properties;

public class ConfigProperties extends Properties {
	private static final long serialVersionUID = 1L;
	private static ConfigProperties thisRef;
	
	private ConfigProperties() {
		try {
			thisRef = this;
			this.load(this.getClass().getResourceAsStream("/org/samsha/iec/resources/samsha.properties"));
		} catch(Exception ex) {
		}
	}
	
	public static ConfigProperties getInstance() {
		if (thisRef == null) {
			new ConfigProperties();
		}
		return thisRef;
	}
	
	public int getIntProperty(String key) {
		return Integer.parseInt(getProperty(key));
	}
}
