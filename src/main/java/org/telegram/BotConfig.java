package org.telegram;

import org.telegram.services.Property;

public class BotConfig {
	public static final String TOKENPLAYHARD = Property.getInstance().getProperty("bot.token");
	public static final String USERNAMEPLAYHARD = Property.getInstance().getProperty("bot.username");
}