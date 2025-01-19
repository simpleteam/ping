package com.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.apache.log4j.Logger;

import com.entity.Host;
import com.entity.ResultOfCheck;

public class HttpChecker implements Checker {

	
	private static final int TIMEOUT = 20;
	
	
	public ResultOfCheck check(Host host) {
		HttpRequest request = null;
		try {
			request = HttpRequest.newBuilder(new URI(host.getUrl())).timeout(Duration.ofSeconds(TIMEOUT)).GET().build();
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
			
			if(host.getUrl().contains("db-stat.ics.gov.ua") && response.statusCode() == 401) {
				return new ResultOfCheck(host,true);
			}
			
			if(response.statusCode() == 200) {
				return new ResultOfCheck(host,true);
			}
		}
		

		return new ResultOfCheck(host,false);
	}

	@Override
	public ResultOfCheck check(Host host, String body) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
