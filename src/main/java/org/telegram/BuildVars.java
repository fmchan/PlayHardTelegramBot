package org.telegram;

import org.telegram.services.Property;

public class BuildVars {
	public static final Boolean debug = true;

	public static final String pathToLogs = "./";

	public static final String linkDB = "jdbc:mysql://"
			+ Property.getInstance().getProperty("db.host") + "/"
			+ Property.getInstance().getProperty("db.database")
			+ "?useUnicode=true&characterEncoding=UTF-8";
	public static final String controllerDB = "com.mysql.jdbc.Driver";
	public static final String userDB = Property.getInstance().getProperty(
			"db.user");
	public static final String password = Property.getInstance().getProperty(
			"db.password");
}
