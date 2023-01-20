package com.entity;

public class ResultOfCheck {

	private Host host;
	private boolean isAvailable;

	public ResultOfCheck(Host host, boolean isAvailable) {
		this.host = host;
		this.isAvailable = isAvailable;
	}

	public Host getHost() {
		return host;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

}
