package com.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.entity.Host;
import com.entity.ResultOfCheck;

public class HttpChecker implements Checker {

	public ResultOfCheck check(Host host) {
		HttpRequest request = null;
		try {
			request = HttpRequest.newBuilder(new URI(host.getUrl())).GET().build();
		} catch (URISyntaxException e) {
			
		}
		
		HttpClient client = HttpClient.newBuilder().build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request, BodyHandlers.ofString());
		} catch (Exception e) {
			
		}
		
		if(response != null) {
			if(response.statusCode() == 200) {
				return new ResultOfCheck(host,true);
			}
		}
		
		return new ResultOfCheck(host,false);
	}

	

}
