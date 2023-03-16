package com.util;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.entity.Host;
import com.entity.ResultOfCheck;

public class PingChecker implements Checker {

	public ResultOfCheck check(Host host) {
		InetAddress inet;
		try {
			inet = InetAddress.getByName(host.getUrl());
			if (inet.isReachable(5000)) {
				return new ResultOfCheck(host, true);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return new ResultOfCheck(host, false);
	}

}
