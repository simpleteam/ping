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

import com.alarm.Alarm;
import com.alarm.TelegramAlarm;
import com.entity.Host;
import com.entity.ResultOfCheck;
import com.entity.TypeOfHost;
import com.util.Checker;
import com.util.HttpChecker;
import com.util.PingChecker;
import com.util.Task;

public class CheckService {

	private Map<TypeOfHost, Checker> checkers = new HashMap<>();
	private Map<Host, Integer> flag = new HashMap<>();
	private List<Host> hosts = new ArrayList<>();
	private Alarm alarm = new TelegramAlarm();

	private void initCheckers() {

		System.out.println(LocalDateTime.now().getHour());

		checkers.put(TypeOfHost.HTTP, new HttpChecker());
		checkers.put(TypeOfHost.IP, new PingChecker());

	}

	private void initHosts() {


		Path filePath = Path.of("hosts.txt");

		try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {

			List<String> list = stream.flatMap(Stream::of).collect(Collectors.toList());
			for (String s : list) {
				if(s.length() == 1){
					continue;
				}

				s = s.strip();
				String[] l = s.split(" ");
//				System.out.println(Arrays.toString(l));
				hosts.add(new Host(l[0],l[1],TypeOfHost.valueOf(l[2])));
			}
		} catch (IOException e) {
			// handle exception
		}

//-----------------------------------------------------------------------------------------------------------		
		for (Host host : hosts) {
			flag.put(host, 0);
		}

	}

	public void service() throws InterruptedException, ExecutionException {

		initCheckers();
		initHosts();
		List<Task> tasks = new ArrayList<>();
		for (Host host : hosts) {
			tasks.add(new Task(checkers.get(host.getType()), host));
		}

		ExecutorService es = Executors.newCachedThreadPool();

		while (true) {

			if ((LocalDateTime.now().getHour() < 21) && (LocalDateTime.now().getHour() >= 7)) {

				List<Future<ResultOfCheck>> result = es.invokeAll(tasks);

				check(result);

				Thread.currentThread().sleep(20000);

			}

		}

	}

	private void check(List<Future<ResultOfCheck>> results) {

		for (Future<ResultOfCheck> r : results) {
			try {
				System.out.println(
						r.get().getHost().getName() + " " + r.get().getHost().getType() + " " + r.get().isAvailable());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if (!r.get().isAvailable()) {
					if (flag.get(r.get().getHost()) == 2) {
						alarm.alarm(r.get(), "_DOWN");
						flag.put(r.get().getHost(), flag.get(r.get().getHost()) + 1);
					} else {
						if (flag.get(r.get().getHost()) < 2) {
							flag.put(r.get().getHost(), flag.get(r.get().getHost()) + 1);
						}
					}
				}
				if (r.get().isAvailable()) {
					if (flag.get(r.get().getHost()) > 2) {
						alarm.alarm(r.get(), "_UP");
					}
					flag.put(r.get().getHost(), 0);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
