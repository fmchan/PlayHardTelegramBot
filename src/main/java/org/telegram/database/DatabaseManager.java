/*
 * This is the source code of Telegram Bot v. 2.0
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Ruben Bermudez, 3/12/14.
 */
package org.telegram.database;

import org.telegram.services.BotLogger;

import java.sql.SQLException;

public class DatabaseManager {
	private static final String LOGTAG = "DATABASEMANAGER";

	private static volatile DatabaseManager instance;
	private static volatile ConectionDB connetion;

	/**
	 * Private constructor (due to Singleton)
	 */
	private DatabaseManager() {
		connetion = new ConectionDB();
		final int currentVersion = connetion.checkVersion();
		BotLogger.info(LOGTAG, "Current db version: " + currentVersion);
		if (currentVersion < CreationStrings.version) {
			recreateTable(currentVersion);
		}
	}

	/**
	 * Get Singleton instance
	 *
	 * @return instance of the class
	 */
	public static DatabaseManager getInstance() {
		final DatabaseManager currentInstance;
		if (instance == null) {
			synchronized (DatabaseManager.class) {
				if (instance == null) {
					instance = new DatabaseManager();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}

	/**
	 * Recreates the DB
	 */
	private void recreateTable(int currentVersion) {
		try {
			connetion.initTransaction();
			if (currentVersion == 0) {
				currentVersion = createNewTables();
			}
			connetion.commitTransaction();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
	}

	private int createNewTables() throws SQLException {
		connetion.executeQuery(CreationStrings.createVersionTable);
		connetion.executeQuery(String.format(
				CreationStrings.insertCurrentVersion, CreationStrings.version));
		return CreationStrings.version;
	}
}
