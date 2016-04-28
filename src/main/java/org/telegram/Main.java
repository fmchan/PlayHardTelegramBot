package org.telegram;

import org.telegram.services.BotLogger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.updateshandlers.*;

public class Main {
	private static final String LOGTAG = "MAIN";

	public static void main(String[] args) {

		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new PlayhardHandler());
		} catch (TelegramApiException e) {
			BotLogger.error(LOGTAG, e);
		}
	}
}