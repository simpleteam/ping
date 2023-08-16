package com.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import com.entity.Host;
import com.entity.ResultOfCheck;

public class HttpPostChecker implements Checker {

	private String body;
	
	public HttpPostChecker(String body) {
		this.body = body;
	}
	
	@Override
	public ResultOfCheck check(Host host) {
		HttpRequest request = null;
		try {
			request = HttpRequest.newBuilder(new URI(host.getUrl())).timeout(Duration.ofSeconds(15)).POST(BodyPublishers.ofString(body)).build();
		} catch (URISyntaxException e) {
			System.out.println(e);
		}
		
		HttpClient client = HttpClient.newBuilder().build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request, BodyHandlers.ofString());
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
		if(response != null) {
			if(response.statusCode() == 200) {
				return new ResultOfCheck(host,true);
			}
		}
		

		return new ResultOfCheck(host,false);
	}

}
