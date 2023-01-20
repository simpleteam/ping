package com.util;

import java.io.IOException;
import java.net.InetAddress;

import com.entity.Host;
import com.entity.ResultOfCheck;

public class PingChecker implements Checker{

	@Override
	public ResultOfCheck check(Host host) {
		
		InetAddress inet;
		try {
			inet = InetAddress.getByName(host.getUrl());
			if (inet.isReachable(5000)) {
				return new ResultOfCheck(host,true);
			} 
		} catch (IOException e) {
			
		}

		return new ResultOfCheck(host,false);
	}



}
