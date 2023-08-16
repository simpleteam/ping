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
	private Alarm alarm = new TelegramAlarm();
	
	String path = "http://212.90.190.164/restapi/GOV/26255795/16_EDRSR_prod/v1/rpc?xRoadInstance=SEVDEIR&memberClass=GOV&memberCode=26255795&subsystemCode=16_EDRSR_prod&serviceCode=rpc&serviceVersion=v1&queryId=123";
	Host trembita = new Host(path,"TREMBITA",TypeOfHost.POST);

	private void initCheckers() {
		System.out.println("init checkers");
		checkers.put(TypeOfHost.HTTP, new HttpChecker());
		checkers.put(TypeOfHost.IP, new PingChecker());
		
		String body = "{\r\n"
				+ "    \"jsonrpc\": \"2.0\",\r\n"
				+ "    \"method\": \"EdrsrDocuments.find\",\r\n"
				+ "    \"id\": \"eb15f8ae-0248-4fde-b048-60260616b298\",\r\n"
				+ "    \"params\": {\r\n"
				+ "        \"where\": {\r\n"
				+ "          \"caseNum\": \"679/1040/21\",\r\n"
				+ "  \"courtId\": \"2211\",\r\n"
				+ "  \"docDate\": \"2023-02-23\",\r\n"
				+ "  \"docTypeId\": \"3\"\r\n"
				+ "}\r\n"
				+ "        }\r\n"
				+ "    }s";
		
		checkers.put(TypeOfHost.POST, new HttpPostChecker(body));

	}

	private List<Host> setUp(Path path){
		
		List<Host> hosts = new ArrayList<>();
		
		try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {

			List<String> list = stream.flatMap(Stream::of).collect(Collectors.toList());
			for (String s : list) {
				if(s.length() == 1){
					continue;
				}

				s = s.strip();
				String[] l = s.split(" ");

				hosts.add(new Host(l[0],l[1],TypeOfHost.valueOf(l[2])));
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return hosts;
	}
	
	
	private void initHosts() {

		System.out.println("init hosts");
		
		Path filePath = Path.of("hosts/timeHosts.txt");
		timeHosts = setUp(filePath);
		filePath = Path.of("hosts/hosts.txt");
		hosts = setUp(filePath);
	
		for (Host host : hosts) {
			flag.put(host, 0);
		}
		
		for(Host host : timeHosts) {
			flag.put(host, 0);
		}
		
		flag.put(trembita, 0);
		
	}

	public void service() throws InterruptedException, ExecutionException {

		initCheckers();
		initHosts();
		List<Task> tasks = new ArrayList<>();
		List<Task> timeTasks = new ArrayList<>();
		
		for (Host host : hosts) {
			tasks.add(new Task(checkers.get(host.getType()), host));
		}

		for(Host host : timeHosts) {
			timeTasks.add(new Task(checkers.get(host.getType()), host));
		}
		
//		String path = "http://212.90.190.150/restapi/GOV/26255795/16_EDRSR_prod/v1/rpc?xRoadInstance=SEVDEIR-TEST&memberClass=GOV&memberCode=26255795&subsystemCode=16_EDRSR_prod&serviceCode=rpc&serviceVersion=v1&queryId=123";
//		Host trembita = new Host(path,"TREMBITA",TypeOfHost.POST);
		
		tasks.add(new Task(checkers.get(TypeOfHost.POST), trembita));
		
		ExecutorService es = Executors.newCachedThreadPool();

		while (true) {
			System.out.println("check hosts");
			
			if ((LocalDateTime.now().getHour() < 21) && (LocalDateTime.now().getHour() >= 7)) {
				List<Future<ResultOfCheck>> result = es.invokeAll(timeTasks);
				check(result);
			}

			List<Future<ResultOfCheck>> result = es.invokeAll(tasks);
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
				if (!r.get().isAvailable()) {
					if (flag.get(r.get().getHost()) == 2) {
//						System.out.println(r.get() + "  " + "_DOWN");
						log.info(" " + r.get().getHost().getUrl() + " " + r.get().getHost().getName() + " DOWN");
						alarm.alarm(r.get(), "_DOWN");
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
