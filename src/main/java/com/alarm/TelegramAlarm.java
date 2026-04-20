package com.alarm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.entity.ResultOfCheck;

public class TelegramAlarm implements Alarm {
//  prod
	private String url =  "https://api.telegram.org/bot8694854845:AAHgzIRtGMjZr6bMe7SJtnNbT0N1L5ki7tw/sendmessage?chat_id=-5148520321&text=";  
			// "https://api.telegram.org/bot5849341816:AAEhnNKvZtrMESUhpUrThB8T1S7Rd1IBaM4/sendmessage?chat_id=-815432610&text=";

//	test
//	private String url = "https://api.telegram.org/bot8799329035:AAFtU2nLST4DD7XifT0XG49rUa52LDxgGnE/sendMessage?chat_id=-5292953407&text=";
	
//  prod
	private String urlTeams = "https://sjau.webhook.office.com/webhookb2/5d147e29-bbf8-409f-a047-476743e41d11@3cc6a844-b70c-4073-8398-037b369fd2e4/IncomingWebhook/de33241fddba4f98ad3269c21b7bd16f/e4d2c3a0-036a-4f1e-bce6-175e04274e70/V2lreo2FX0Uutpl3FsLpgi_wycDvVwhhwXgTHJhePNXWI1";
	
//	test
//	private String urlTeams = "https://sjau.webhook.office.com/webhookb2/b36d4d8b-7836-4add-bc97-d1ceb54544d0@3cc6a844-b70c-4073-8398-037b369fd2e4/IncomingWebhook/6e7774cbb90249a7831181cdc4e75834/e4d2c3a0-036a-4f1e-bce6-175e04274e70/V2kgsS4VlajQMXZgLySYDscJUXoOGq-zHiYBddMbvYoo41";
 
	
	
	public void alarmTeams(ResultOfCheck resultOfCheck, String msg) {
		
		String ip = resultOfCheck.getHost().getUrl();
		String hostName = resultOfCheck.getHost().getName();
		
		
		if(ip.length() > 39) {
			ip = ip.substring(0, 39);
		}
		
		
		
//		String data = "{ text : '" + "["+ ip +"]_" + "', text : '"+hostName+"', text : '"+msg+"'}";
		
		String data = "{'type':'message','attachments':[{'contentType':'application/vnd.microsoft.card.adaptive','contentUrl':null,'content':{'$schema':'http://adaptivecards.io/schemas/adaptive-card.json','type':'AdaptiveCard','version':'1.2',"
				+ "'actions': [{'type' : 'Action.OpenUrl','title' : '"+ ip +"','url' : '"+ ip +"'}],"
				+ "'body':["
				+ "{'type': 'TextBlock','text': '"+ ip +"'}"
						+ ",{'type': 'TextBlock','text': '"+ hostName+"'},"
								+ "{'type': 'TextBlock','text': '"+ msg +"'}]"
										+ "}}]}";

	System.out.println(data);

	byte[] postData = data
			.getBytes(StandardCharsets.UTF_8);System.out.println(new String(postData,StandardCharsets.UTF_8));
	int postDataLength = postData.length;
	String request = urlTeams;
	URL url = null;try
	{
		url = new URL(request);
		System.out.println(url);
	}catch(
	MalformedURLException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	HttpURLConnection conn = null;try
	{
		conn = (HttpURLConnection) url.openConnection();
		System.out.println(conn);
	}catch(
	IOException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}conn.setDoOutput(true);
//		conn.setInstanceFollowRedirects( false );
	try
	{
		conn.setRequestMethod("POST");
	}catch(
	ProtocolException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}conn.setRequestProperty("Content-Type","application/json");
//		conn.setRequestProperty( "charset", "utf-8");
//		conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
	conn.setUseCaches(false);try(
	DataOutputStream wr = new DataOutputStream(conn.getOutputStream()))
	{
		wr.write(postData);
		System.out.println("write");
		System.out.println(conn.getResponseCode());
	}catch(
	IOException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	conn.disconnect();
	
	}
	

	@Override
	public void alarm(ResultOfCheck resultOfCheck, String msg) {

		try {

			String ip = resultOfCheck.getHost().getUrl();
			String hostName = resultOfCheck.getHost().getName();

			if (ip.length() > 39) {
				ip = ip.substring(0, 39);
			}

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().GET().timeout(Duration.ofSeconds(10))
					.uri(URI.create(url + "[" + ip + "]_" + hostName + msg)).build();

			HttpResponse<String> response = null;

			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			client.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
