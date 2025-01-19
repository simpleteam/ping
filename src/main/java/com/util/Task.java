package com.util;

import java.util.concurrent.Callable;
import com.entity.Host;
import com.entity.ResultOfCheck;

public class Task implements Callable<ResultOfCheck> {

	private Checker checker;
	private Host host;
	private String body;
	
	public Task(Checker checker, Host host) {
		this.checker = checker;
		this.host = host;
	}
	
	public Task(Checker checker, Host host, String body) {
		this.checker = checker;
		this.host = host;
		this.body = body;
	}
	
	
	@Override
	public ResultOfCheck call() throws Exception {
		if(body != null) {
			System.out.println("POST TASK");
			return  checker.check(host, body);
		}
		return checker.check(host);
	}

}
