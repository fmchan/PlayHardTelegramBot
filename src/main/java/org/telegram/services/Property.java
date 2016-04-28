package org.telegram.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Property {
	private static final String LOGTAG = "PROPERTY";
	private static volatile Property instance;

	private Property() {
	}

	public static Property getInstance() {
		Property currentInstance;
		if (instance == null) {
			synchronized (Property.class) {
				if (instance == null) {
					instance = new Property();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}

	public String getProperty(String property) {
		Properties mainProperties = new Properties();
		try {
			mainProperties
					.load(new FileInputStream("./application.properties"));
		} catch (IOException e) {
			BotLogger.error(LOGTAG, e);
		}
		return mainProperties.getProperty(property);
	}
}
