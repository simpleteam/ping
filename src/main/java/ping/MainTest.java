package ping;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.entity.ResultOfCheck;

public class MainTest {

	public static void main(String[] args) {
		
		String host = "http://ex.ics.gov.ua/owa/auth/logon.aspx?replaceCurrent=1&url=https%3a%2f%2fex.ics.gov.ua%2fowa%2f";
		
		
		HttpRequest request = null;
		try {
			request = HttpRequest.newBuilder(new URI(host)).GET().build();
		} catch (URISyntaxException e) {
			System.out.println("1");
		}
		
		HttpClient client = HttpClient.newBuilder().build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(request, BodyHandlers.ofString());
			System.out.println(response);
		} catch (Exception e) {
			System.out.println(2);
		}
		
	
		
		
		

	}

}
