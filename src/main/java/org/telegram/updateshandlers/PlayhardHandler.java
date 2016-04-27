package org.telegram.updateshandlers;

import java.io.InvalidObjectException;
import java.util.List;

import jersey.repackaged.com.google.common.base.Joiner;

import org.json.JSONObject;
import org.telegram.BotConfig;
import org.telegram.Commands;
import org.telegram.services.BotLogger;
import org.telegram.services.PlayhardService;
import org.telegram.structure.EventPreiod;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.ReplyKeyboardHide;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.updateshandlers.SentCallback;

public class PlayhardHandler extends TelegramLongPollingBot {
	private static final String LOGTAG = "PLAYHARDHANDLERS";

	@Override
	public String getBotUsername() {
		return BotConfig.USERNAMEPLAYHARD;
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			handle(update);
		} catch (InvalidObjectException e) {
			BotLogger.error(LOGTAG, e);
		}
	}

	@Override
	public String getBotToken() {
		return BotConfig.TOKENPLAYHARD;
	}

	private void handle(Update update) throws InvalidObjectException {
		Message message = update.getMessage();
		if (message != null && message.hasText()) {
			if (message.getText().startsWith(Commands.eventTodayCommand))
				onCommandReceived(message, EventPreiod.TODAY);
			else if (message.getText().startsWith(Commands.event7DaysCommand))
				onCommandReceived(message, EventPreiod.SEVEN_DAYS);
			else if (message.getText().startsWith(Commands.eventLaterCommand))
				onCommandReceived(message, EventPreiod.LATER);
			else if ((message.getText().startsWith(Commands.help) || (message
					.getText().startsWith(Commands.startCommand) || !message
					.isGroupMessage()))) {
				sendHelpMessage(message);
			}
		}
	}

	private void onCommandReceived(Message message, EventPreiod preiod) {
		List<String> events = PlayhardService.getInstance().getEvents(preiod);
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId().toString());
		ReplyKeyboardHide replyKeyboardHide = new ReplyKeyboardHide();
		replyKeyboardHide.setSelective(true);
		sendMessageRequest.setReplayMarkup(replyKeyboardHide);
		sendMessageRequest.setReplayToMessageId(message.getMessageId());
		String text = events.size() == 0 ? "No event" : Joiner.on("\n").join(events);
		sendMessageRequest.setText(text);
		try {
			sendMessageAsync(sendMessageRequest, new SentCallback<Message>() {
				@Override
				public void onResult(BotApiMethod<Message> botApiMethod,
						JSONObject jsonObject) {
				}
				@Override
				public void onError(BotApiMethod<Message> botApiMethod,
						JSONObject jsonObject) {
				}
				@Override
				public void onException(BotApiMethod<Message> botApiMethod,
						Exception e) {
				}
			});
		} catch (TelegramApiException e) {
			BotLogger.error(LOGTAG, e);
		}

	}

	private void sendHelpMessage(Message message) throws InvalidObjectException {
		SendMessage sendMessageRequest = new SendMessage();
		String helpDirectionsFormated = String.format(
				"When do you want to play?\n\nTo get events: "
						+ "\n|-- %s : Get today event"
						+ "\n|-- %s : Get 7-day event"
						+ "\n|-- %s : Get later event",
				Commands.eventTodayCommand, Commands.event7DaysCommand,
				Commands.eventLaterCommand);
		sendMessageRequest.setText(helpDirectionsFormated);
		sendMessageRequest.setChatId(message.getChatId().toString());
		try {
			sendMessage(sendMessageRequest);
		} catch (TelegramApiException e) {
			BotLogger.error(LOGTAG, e);
		}
	}
}