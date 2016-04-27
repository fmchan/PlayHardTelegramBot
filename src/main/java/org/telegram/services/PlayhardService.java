package org.telegram.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.structure.EventPreiod;

public class PlayhardService {
	private static final String LOGTAG = "PLAYHARDSERVICE";
	private static volatile PlayhardService instance;

	private PlayhardService() {
	}

	public static PlayhardService getInstance() {
		PlayhardService currentInstance;
		if (instance == null) {
			synchronized (PlayhardService.class) {
				if (instance == null) {
					instance = new PlayhardService();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}

	public List<String> getEvents(EventPreiod preiod) {
		final List<String> responseToUser = new ArrayList<>();
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(
					"http://52.77.6.192:5000/api/events?type=" + preiod);
			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			BotLogger.info(LOGTAG, "responseCode:" + responseCode);
			HttpEntity ht = response.getEntity();

			BufferedHttpEntity buf = new BufferedHttpEntity(ht);
			String responseContent = EntityUtils.toString(buf, "UTF-8");

			JSONArray jsonArray = new JSONArray(responseContent);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				// BotLogger.info(LOGTAG, object.getString("title"));
				responseToUser.add("- <b>" + object.getString("title") + "</b> [" + object.getString("date") + "]");
			}
		} catch (IOException e) {
			BotLogger.warn(LOGTAG, e);
		}
		return responseToUser;
	}

}
