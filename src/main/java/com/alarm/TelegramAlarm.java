package com.alarm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.entity.ResultOfCheck;

public class TelegramAlarm implements Alarm {

	private String url = "";
	
	@Override
	public void alarm(ResultOfCheck resultOfCheck, String msg) {
		
		
		String hostName = resultOfCheck.getHost().getName();
		
		HttpClient client = HttpClient.newHttpClient();
		
		HttpRequest request = HttpRequest.newBuilder()
							  .GET()
							  .timeout(Duration.ofSeconds(10))
							  .uri(URI.create(url + hostName + msg))
							  .build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
