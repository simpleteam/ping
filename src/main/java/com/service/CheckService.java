package com.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.alarm.Alarm;
import com.alarm.TelegramAlarm;
import com.entity.Host;
import com.entity.ResultOfCheck;
import com.entity.TypeOfHost;
import com.util.Checker;
import com.util.HttpChecker;
import com.util.HttpPostChecker;
import com.util.PingChecker;
import com.util.Task;

public class CheckService {

	static Logger log = Logger.getLogger(CheckService.class.getName());
	private Map<TypeOfHost, Checker> checkers = new HashMap<>();
	private Map<Host, Integer> flag = new HashMap<>();
	private List<Host> hosts = new ArrayList<>();
	private List<Host> timeHosts = new ArrayList<>();
	private TelegramAlarm alarm = new TelegramAlarm();

	private int count = 0;
	
	
//	private String restCSSTrembitaTestPath = "http://212.90.190.170";
//	private Host restCSSTestTrembita = new Host(restCSSTrembitaTestPath, "TREMBITA_ЦСС_REST_TEST", TypeOfHost.POST);
//	private String restCSSTestTrembitaBody = "{    \"jsonrpc\": \"2.0\", \r\n"
//			+ "    \"method\": \"EdrsrDracDocuments.find\",    \"id\": \"4\",\r\n"
//			+ "    \"params\": {        \"where\": {\r\n"
//			+ "           \"courtId\": \"0306\",            \"caseNum\": \"159/4234/20\",\r\n"
//			+ "            \"docTypeId\": \"5\",            \"docDate\": \"2020-09-28\"\r\n" + "        }    }\r\n"
//			+ "}";
	
	private String restTrembitaPromPath = "http://212.90.190.164/restapi/GOV/26255795/16_EDRSR_prod/v1/rpc?xRoadInstance=SEVDEIR&memberClass=GOV&memberCode=26255795&subsystemCode=16_EDRSR_prod&serviceCode=rpc&serviceVersion=v1&queryId=123";
	private Host restPromTrembita = new Host(restTrembitaPromPath, "TREMBITA_REST_PROD", TypeOfHost.POST);
	private String restPromTrembitaBody = "{\r\n" + "    \"jsonrpc\": \"2.0\",\r\n"
			+ "    \"method\": \"EdrsrDocuments.find\",\r\n"
			+ "    \"id\": \"eb15f8ae-0248-4fde-b048-60260616b298\",\r\n" + "    \"params\": {\r\n"
			+ "        \"where\": {\r\n" + "          \"caseNum\": \"679/1040/21\",\r\n"
			+ "  \"courtId\": \"2211\",\r\n" + "  \"docDate\": \"2023-02-23\",\r\n" + "  \"docTypeId\": \"3\"\r\n"
			+ "}\r\n" + "        }\r\n" + "    }s";

	private String restTrembitaTestPath = "http://212.90.190.150/restapi/GOV/26255795/16_EDRSR_prod/v1/rpc?xRoadInstance=SEVDEIR-TEST&memberClass=GOV&memberCode=26255795&subsystemCode=16_EDRSR_prod&serviceCode=rpc&serviceVersion=v1&queryId=123";
	private Host restTestTrembita = new Host(restTrembitaTestPath, "TREMBITA_REST_TEST", TypeOfHost.POST);
	private String restTestTrembitaBody = "{    \"jsonrpc\": \"2.0\", \r\n"
			+ "    \"method\": \"EdrsrDracDocuments.find\",    \"id\": \"xA2JA9ik49\",\r\n"
			+ "    \"params\": {        \"where\": {\r\n"
			+ "           \"courtId\": \"2605\",            \"caseNum\": \"756/16667/20\",\r\n"
			+ "            \"docTypeId\": \"3\",            \"docDate\": \"2024-02-07\"\r\n" + "        }    }\r\n"
			+ "}";

	private String soapTrembitaTestPath = "http://212.90.190.150";
	private Host soapTestTrembita = new Host(soapTrembitaTestPath, "TREMBITA_SOAP_TEST", TypeOfHost.POST);
	private String soapTestTrembitaBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xro=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:iden=\"http://x-road.eu/xsd/identifiers\" xmlns:iss=\"http://localhost/IssubService/Issub\"> \r\n"
			+ "   <soapenv:Header> \r\n" + "     <xro:client iden:objectType=\"SUBSYSTEM\"> \r\n"
			+ "         <iden:xRoadInstance>SEVDEIR-TEST</iden:xRoadInstance> \r\n"
			+ "         <iden:memberClass>GOV</iden:memberClass> \r\n"
			+ "         <iden:memberCode>26255795</iden:memberCode> \r\n" + "         <!--Optional:--> \r\n"
			+ "         <iden:subsystemCode>16_EDRSR_prod</iden:subsystemCode> \r\n" + "      </xro:client> \r\n"
			+ "      <xro:service iden:objectType=\"SERVICE\"> \r\n"
			+ "         <iden:xRoadInstance>SEVDEIR-TEST</iden:xRoadInstance> \r\n"
			+ "         <iden:memberClass>GOV</iden:memberClass> \r\n"
			+ "         <iden:memberCode>26255795</iden:memberCode> \r\n" + "         <!--Optional:--> \r\n"
			+ "         <iden:subsystemCode>16_EDRSR_prod</iden:subsystemCode> \r\n"
			+ "         <iden:serviceCode>erzo</iden:serviceCode> \r\n" + "         <!--Optional:--> \r\n"
			+ "      </xro:service> \r\n" + "      <xro:id>?</xro:id> \r\n"
			+ "      <xro:protocolVersion>4.0</xro:protocolVersion> \r\n" + "   </soapenv:Header> \r\n"
			+ "   <soapenv:Body> \r\n" + "      <iss:erzo_Params> \r\n"
			+ "         <iss:courtId>0514</iss:courtId> \r\n" + "         <iss:caseNum>227/4349/18</iss:caseNum> \r\n"
			+ "         <iss:docDate>2023-09-05</iss:docDate> \r\n" + "         <iss:docTypeId>5</iss:docTypeId> \r\n"
			+ "      </iss:erzo_Params> \r\n" + "   </soapenv:Body> \r\n" + "</soapenv:Envelope>";

	private String soapTrembitaPromPath = "http://212.90.190.164";
	private Host soapPromTrembita = new Host(soapTrembitaPromPath, "TREMBITA_SOAP_PROD", TypeOfHost.POST);
	private String soapPromTrembitaBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xro=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:iden=\"http://x-road.eu/xsd/identifiers\" xmlns:iss=\"http://localhost/IssubService/Issub\"> \r\n"
			+ "   <soapenv:Header> \r\n" + "     <xro:client iden:objectType=\"SUBSYSTEM\"> \r\n"
			+ "         <iden:xRoadInstance>SEVDEIR</iden:xRoadInstance> \r\n"
			+ "         <iden:memberClass>GOV</iden:memberClass> \r\n"
			+ "         <iden:memberCode>26255795</iden:memberCode> \r\n" + "         <!--Optional:--> \r\n"
			+ "         <iden:subsystemCode>16_EDRSR_prod</iden:subsystemCode> \r\n" + "      </xro:client> \r\n"
			+ "      <xro:service iden:objectType=\"SERVICE\"> \r\n"
			+ "         <iden:xRoadInstance>SEVDEIR</iden:xRoadInstance> \r\n"
			+ "         <iden:memberClass>GOV</iden:memberClass> \r\n"
			+ "         <iden:memberCode>26255795</iden:memberCode> \r\n" + "         <!--Optional:--> \r\n"
			+ "         <iden:subsystemCode>16_EDRSR_prod</iden:subsystemCode> \r\n"
			+ "         <iden:serviceCode>erzo</iden:serviceCode> \r\n" + "         <!--Optional:--> \r\n"
			+ "      </xro:service> \r\n" + "      <xro:id>?</xro:id> \r\n"
			+ "      <xro:protocolVersion>4.0</xro:protocolVersion> \r\n" + "   </soapenv:Header> \r\n"
			+ "   <soapenv:Body> \r\n" + "      <iss:erzo_Params> \r\n"
			+ "         <iss:courtId>0514</iss:courtId> \r\n" + "         <iss:caseNum>227/4349/18</iss:caseNum> \r\n"
			+ "         <iss:docDate>2023-09-05</iss:docDate> \r\n" + "         <iss:docTypeId>5</iss:docTypeId> \r\n"
			+ "      </iss:erzo_Params> \r\n" + "   </soapenv:Body> \r\n" + "</soapenv:Envelope>";

	private void initCheckers() {
		System.out.println("init checkers");
		checkers.put(TypeOfHost.HTTP, new HttpChecker());
		checkers.put(TypeOfHost.IP, new PingChecker());
		checkers.put(TypeOfHost.POST, new HttpPostChecker());

	}

	private List<Host> setUp(Path path) {

		List<Host> hosts = new ArrayList<>();

		try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {

			List<String> list = stream.flatMap(Stream::of).collect(Collectors.toList());
			for (String s : list) {
				if (s.length() == 1) {
					continue;
				}

				s = s.strip();
				String[] l = s.split(" ");

				if(l[0].startsWith("#")) {
					continue;
				}
				
				hosts.add(new Host(l[0], l[1], TypeOfHost.valueOf(l[2])));
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return hosts;
	}

	private void initHosts() {

		System.out.println("init hosts");

//		ubuntu
		Path fileTimePath = Path.of("/home/dc_admin/monitoring/hosts/timeHosts.txt");
		Path filePath = Path.of("/home/dc_admin/monitoring/hosts/hosts.txt");

//		windows
//		Path fileTimePath = Path.of("hosts/timeHosts.txt");
//		Path filePath = Path.of("hosts/hosts.txt");

		timeHosts = setUp(fileTimePath);
		hosts = setUp(filePath);

		for (Host host : hosts) {
			flag.put(host, 0);
		}

		for (Host host : timeHosts) {
			flag.put(host, 0);
		}
		
//		flag.put(restCSSTestTrembita, 0);
		flag.put(restPromTrembita, 0);
		flag.put(restTestTrembita, 0);
		flag.put(soapPromTrembita, 0);
		flag.put(soapTestTrembita, 0);
	}

	public void service() throws InterruptedException, ExecutionException {

		initCheckers();
		initHosts();
		List<Task> tasks = new ArrayList<>();
		List<Task> timeTasks = new ArrayList<>();

		for (Host host : hosts) {
			tasks.add(new Task(checkers.get(host.getType()), host));
		}

		for (Host host : timeHosts) {
			timeTasks.add(new Task(checkers.get(host.getType()), host));
		}

//		String path = "http://212.90.190.150/restapi/GOV/26255795/16_EDRSR_prod/v1/rpc?xRoadInstance=SEVDEIR-TEST&memberClass=GOV&memberCode=26255795&subsystemCode=16_EDRSR_prod&serviceCode=rpc&serviceVersion=v1&queryId=123";
//		Host trembita = new Host(path,"TREMBITA",TypeOfHost.POST);

//		tasks.add(new Task(checkers.get(TypeOfHost.POST), restCSSTestTrembita, restCSSTestTrembitaBody));
		tasks.add(new Task(checkers.get(TypeOfHost.POST), restPromTrembita, restPromTrembitaBody));
		tasks.add(new Task(checkers.get(TypeOfHost.POST), restTestTrembita, restTestTrembitaBody));
		tasks.add(new Task(checkers.get(TypeOfHost.POST), soapPromTrembita, soapPromTrembitaBody));
		tasks.add(new Task(checkers.get(TypeOfHost.POST), soapTestTrembita, soapTestTrembitaBody));

		ExecutorService es = Executors.newCachedThreadPool();

		List<Future<ResultOfCheck>> result = null;
		
		while (true) {
			System.out.println("check hosts");
			
			System.gc();

			if ((LocalDateTime.now().getHour() < 21) && (LocalDateTime.now().getHour() >= 7)) {
				result = es.invokeAll(timeTasks);
				check(result);
			}

			result = es.invokeAll(tasks);
			check(result);
			Thread.currentThread().sleep(120000);
		}

	}

	private void check(List<Future<ResultOfCheck>> results) {

		for (Future<ResultOfCheck> r : results) {
//			try {
//				System.out.println(
//						r.get().getHost().getName() + " " + r.get().getHost().getType() + " " + r.get().isAvailable());
//			} catch (InterruptedException | ExecutionException e) {
//				e.printStackTrace();
//			}
			
			
			try {
				if(r.get().getHost().getUrl().contains("dbstatuser")) {
					r.get().getHost().setUrl(r.get().getHost().getUrl().replace("dbstatuser:Fphks28CvWk43h@", ""));
					System.out.println(r.get().getHost());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			

			try {
				
				
				if ((LocalDateTime.now().getHour() > 22) || (LocalDateTime.now().getHour() <= 7)) {
					if((r.get().getHost().getUrl().equals("https://id-stg-evd.court.gov.ua")) || (r.get().getHost().getUrl().equals("https://id-dev.court.gov.ua")) || (r.get().getHost().getUrl().equals("https://id-evd.court.gov.ua"))){
							continue;
					}
					
				}
				
				
				
				
				
				if (!r.get().isAvailable()) {
					if (flag.get(r.get().getHost()) == 2) {
//						System.out.println(r.get() + "  " + "_DOWN");
						log.info(" " + r.get().getHost().getUrl() + " " + r.get().getHost().getName() + " DOWN");
						alarm.alarm(r.get(), "_DOWN");
						alarm.alarmTeams(r.get(),"_DOWN");
						System.out.println(r.get().getHost().getUrl() + " " + r.get().getHost().getName() + " Down");
						flag.put(r.get().getHost(), flag.get(r.get().getHost()) + 1);
					} else {
						if (flag.get(r.get().getHost()) < 2) {
							
							
							
							
							
							
							flag.put(r.get().getHost(), flag.get(r.get().getHost()) + 1);
						}
					}
				}
				if (r.get().isAvailable()) {
					if (flag.get(r.get().getHost()) > 2) {
						log.info(" " + r.get().getHost().getUrl() + " " + r.get().getHost().getName() + " UP");
						alarm.alarm(r.get(), "_UP");
						alarm.alarmTeams(r.get(), "_UP");
						System.out.println(r.get().getHost().getUrl() + " " + r.get().getHost().getName() + " Up");
					}
					flag.put(r.get().getHost(), 0);
					
					
					
					
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}

	}

}
