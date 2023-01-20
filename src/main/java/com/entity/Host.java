package com.entity;

public class Host {

	private Long id;
	private String url;
	private String name;
	private TypeOfHost type;

	public Host(String url, String name, TypeOfHost type) {
		this.url = url;
		this.name = name;
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeOfHost getType() {
		return type;
	}

	public void setType(TypeOfHost type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Host [id=" + id + ", url=" + url + ", name=" + name + ", type=" + type + "]";
	}

}
