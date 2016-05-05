package org.telegram.updateshandlers;

import java.io.File;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import jersey.repackaged.com.google.common.base.Joiner;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.telegram.BotConfig;
import org.telegram.Commands;
import org.telegram.services.BotLogger;
import org.telegram.services.Emoji;
import org.telegram.services.PlayhardService;
import org.telegram.services.Property;
import org.telegram.structure.EventPreiod;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.SendMessage;
import org.telegram.telegrambots.api.methods.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.ReplyKeyboardHide;
import org.telegram.telegrambots.api.objects.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.updateshandlers.SentCallback;

public class PlayhardHandler extends TelegramLongPollingBot {
	private static final String LOGTAG = "PLAYHARDHANDLERS";
	private List<JSONObject> events;

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
			if (message.getText().startsWith(Commands.eventTodayCommand)
					|| message.getText().startsWith(getTodayCommand()))
				onCommandReceived(message, EventPreiod.TODAY);
			else if (message.getText().startsWith(Commands.event7DaysCommand)
					|| message.getText().startsWith(get7DaysCommand()))
				onCommandReceived(message, EventPreiod.SEVEN_DAYS);
			else if (message.getText().startsWith(Commands.eventLaterCommand)
					|| message.getText().startsWith(getLaterCommand()))
				onCommandReceived(message, EventPreiod.LATER);
			else if (message.getText().startsWith(Commands.aboutCommand))
				sendAbout(message);
			else if (message.getText().startsWith(Commands.iconCommand))
				sendIcon(message);
			else if (StringUtils.isNumeric(message.getText()))
				sendNumber(message);
			else if ((message.getText().startsWith(Commands.help) || (message
					.getText().startsWith(Commands.startCommand) || !message
					.isGroupMessage()))) {
				sendHelpMessage(message);
			}
		}
	}

	private void onCommandReceived(Message message, EventPreiod preiod) {
		events = PlayhardService.getInstance().getEvents(preiod);
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId().toString());
		ReplyKeyboardHide replyKeyboardHide = new ReplyKeyboardHide();
		replyKeyboardHide.setSelective(true);
		sendMessageRequest.setReplayMarkup(replyKeyboardHide);
		sendMessageRequest.setReplayToMessageId(message.getMessageId());
		List<String> textList = new ArrayList<String>();
		int index = 0;
		for (JSONObject event : events)
			textList.add(++index + ". <b>" + event.getString("title")
					+ "</b> [" + event.getString("date") + "]");
		sendMessageRequest.setText(events.size() == 0 ? "No event" : Joiner.on(
				"\n").join(textList)
				+ "\n\n" + detailCommand());
		sendMessageRequest.enableHtml(true);
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

	private void sendAbout(Message message) throws InvalidObjectException {
		SendMessage sendMessageRequest = new SendMessage();
		String helpDirectionsFormated = String
				.format("<b>Playhard - events in Hong Kong</b>\n"
						+ "PLAYhard is a last minute, one stop event discovery mobile platform for the fun and spontaneous.\n"
						+ "Browse through a shortlist of most exciting events and book in just two simple taps.\n\n"
						+ "|-- %s : Get Playhard icon\n"
						+ "|-- <a href='http://www.playhard.asia'>Visit Playhard Website</a>\n\n"
						+ "Download App:\n"
						+ "|-- <a href='https://goo.gl/9t8mQC'>App Store for iOS</a>\n"
						+ "|-- <a href='https://goo.gl/2DTreA'>Google Play for Android</a>\n",
						Commands.iconCommand);
		sendMessageRequest.setText(helpDirectionsFormated);
		sendMessageRequest.enableHtml(true);
		sendMessageRequest.setChatId(message.getChatId().toString());
		try {
			sendMessage(sendMessageRequest);
		} catch (TelegramApiException e) {
			BotLogger.error(LOGTAG, e);
		}
	}

	private void sendIcon(Message message) throws InvalidObjectException {
		SendPhoto sendPhotoRequest = new SendPhoto();
		sendPhotoRequest.setChatId(message.getChatId().toString());

		/*
		 * File fileToUpload = new
		 * File("/Users/admin/Documents/chrome-logo-540x334.jpg"); URL url = new
		 * URL("http://www.v3.co.uk/IMG/608/188608/chrome-logo-540x334.jpg");
		 * FileUtils.copyURLToFile(url, fileToUpload); //String fileName =
		 * "/Users/admin/Documents/icon.png";
		 * sendPhotoRequest.setNewPhoto(fileToUpload.getAbsolutePath(),
		 * fileToUpload.getName());
		 */

		File fileToUpload = new File(Property.getInstance().getProperty("icon"));
		sendPhotoRequest.setNewPhoto(fileToUpload.getAbsolutePath(),
				fileToUpload.getName());
		try {
			sendPhoto(sendPhotoRequest);
		} catch (TelegramApiException e) {
			BotLogger.error(LOGTAG, e);
		}
	}

	private void sendNumber(Message message) throws InvalidObjectException {
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId().toString());
		sendMessageRequest.setReplayToMessageId(message.getMessageId());
		int eventId = Integer.valueOf(message.getText());
		String text = "";
		if (events == null || events.size() <= 0 || events.size() < eventId
				|| eventId <= 0)
			text = "Invalid input! "
					+ (events.size() > 0 ? detailCommand() : "")
					+ "\n\nYou may also request an event list:\n"
					+ eventCommand();
		else {
			JSONObject o = events.get(eventId - 1);
			text = "<b>" + o.getString("title") + "</b>\n"
					+ o.getString("date") + "\n<b>Address</b>: "
					+ o.getString("addr") + "\n<b>Price</b>: "
					+ o.getString("price") + "\n\n" + o.getString("desc");
		}
		sendMessageRequest.setText(text);
		sendMessageRequest.enableHtml(true);
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
		ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();
		BotLogger.info(LOGTAG, "user: " + message.getChat().getFirstName()
				+ " " + message.getChat().getLastName() + " - "
				+ message.getChat().getUserName() + " ("
				+ message.getChat().getId() + ")");
		SendMessage sendMessageRequest = new SendMessage();
		String helpDirectionsFormated = String.format("Hello "
				+ message.getChat().getFirstName() + " "
				+ message.getChat().getLastName()
				+ ",\nwhen do you want to play?\n\nTo get events: " + "\n"
				+ eventCommand() + "\n|-- %s : About Playhard",
				Commands.aboutCommand);
		sendMessageRequest.setText(helpDirectionsFormated);
		sendMessageRequest.enableHtml(true);
		sendMessageRequest.setChatId(message.getChatId().toString());
		sendMessageRequest.setReplayMarkup(replyKeyboardMarkup);
		try {
			sendMessage(sendMessageRequest);
		} catch (TelegramApiException e) {
			BotLogger.error(LOGTAG, e);
		}
	}

	private static ReplyKeyboardMarkup getMainMenuKeyboard() {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboad(false);

		List<List<String>> keyboard = new ArrayList<>();
		List<String> keyboardFirstRow = new ArrayList<>();
		keyboardFirstRow.add(getTodayCommand());
		keyboardFirstRow.add(get7DaysCommand());
		keyboardFirstRow.add(getLaterCommand());
		keyboard.add(keyboardFirstRow);
		replyKeyboardMarkup.setKeyboard(keyboard);

		return replyKeyboardMarkup;
	}

	private String detailCommand() {
		return "To get event detail, please input an event number ranging from [1..."
				+ events.size() + "]";
	}

	private String eventCommand() {
		return String.format("|-- %s : Get today event"
				+ "\n|-- %s : Get 7-day event" + "\n|-- %s : Get later event",
				Commands.eventTodayCommand, Commands.event7DaysCommand,
				Commands.eventLaterCommand);
	}

	private static String getTodayCommand() {
		return String.format("%sToday",
				Emoji.GRINNING_FACE_WITH_SMILING_EYES.toString());
	}

	private static String get7DaysCommand() {
		return String.format("%s7 Days",
				Emoji.FACE_WITH_TEARS_OF_JOY.toString());
	}

	private static String getLaterCommand() {
		return String.format("%sLater",
				Emoji.SMILING_FACE_WITH_OPEN_MOUTH.toString());
	}
}