package com.alarm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.entity.ResultOfCheck;

public class TelegramAlarm implements Alarm {
//  prod
	private String url = "https://api.telegram.org/bot5849341816:AAEhnNKvZtrMESUhpUrThB8T1S7Rd1IBaM4/sendmessage?chat_id=-815432610&text=";
	
//	test
//	private String url = "https://api.telegram.org/bot6083027589:AAG3C2RpQJzoKEoIgu2PSEITEjq7_jCQ2AA/sendMessage?chat_id=-802155529&text=";
	
	@Override
	public void alarm(ResultOfCheck resultOfCheck, String msg) {
		
		try {
			
		String ip = resultOfCheck.getHost().getUrl();
		String hostName = resultOfCheck.getHost().getName();
		
		HttpClient client = HttpClient.newHttpClient();
		
		HttpRequest request = HttpRequest.newBuilder()
							  .GET()
							  .timeout(Duration.ofSeconds(10))
							  .uri(URI.create(url + "["+ ip +"]_" + hostName + msg))
							  .build();
		
		HttpResponse<String> response = null;
		
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
