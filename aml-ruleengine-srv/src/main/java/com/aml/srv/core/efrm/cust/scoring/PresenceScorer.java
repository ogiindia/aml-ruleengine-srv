package com.aml.srv.core.efrm.cust.scoring;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class PresenceScorer {

	private final FeatureEncoder encoder;
	private final LogisticRegression model;

	@JsonCreator
	public PresenceScorer(@JsonProperty("space") FeatureSpace space, @JsonProperty("model") LogisticRegression model) {
		this.encoder = new FeatureEncoder(space);
		this.model = model;
	}

	/** Input: Map<FeatureName, Collection<String>> -> probability score [0,1] */
	public double score(Map<String, ? extends Collection<String>> input) {
		return model.predictProb(encoder.encode(input));
	}

	/** Also return logit if you want linear score */
	public double logit(Map<String, ? extends Collection<String>> input) {
		return model.predictLogit(encoder.encode(input));
	}
}