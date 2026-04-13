package com.efrm.rt.srv.core.common.repo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

@Component
public class Dynamic_VO {

	private Map<String, Object> properties = new HashMap<>();

	@JsonAnySetter
	public void set(String key, Object value) {
		properties.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return properties;
	}
}
