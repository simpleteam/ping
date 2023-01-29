package com.util;

import java.util.concurrent.Callable;
import com.entity.Host;
import com.entity.ResultOfCheck;

public class Task implements Callable<ResultOfCheck> {

	private Checker checker;
	private Host host;
	
	public Task(Checker checker, Host host) {
		this.checker = checker;
		this.host = host;
	}
	
	
	@Override
	public ResultOfCheck call() throws Exception {
		return checker.check(host);
	}

}
