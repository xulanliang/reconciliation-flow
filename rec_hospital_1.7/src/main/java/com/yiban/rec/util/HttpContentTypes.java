package com.yiban.rec.util;

public enum HttpContentTypes {
	application_json("application/json"), application_xml("application/xml");

	private String value;

	HttpContentTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
