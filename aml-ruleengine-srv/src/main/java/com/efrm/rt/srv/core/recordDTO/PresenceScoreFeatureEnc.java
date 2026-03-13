package com.efrm.rt.srv.core.recordDTO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//public recore PresenceScoreFeatureEnc(PresenceScorer scorer, FeatureEncoder encoder, FeatureSpace space) {
public class PresenceScoreFeatureEnc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private PresenceScorer scorer;
//
//	private FeatureEncoder encoder;
//
//	private FeatureSpace space;
	
	List<Map<String, Collection<String>>> samples;
	
	List<Integer> labels;


	public List<Map<String, Collection<String>>> getSamples() {
		return samples;
	}

	public void setSamples(List<Map<String, Collection<String>>> samples) {
		this.samples = samples;
	}

	public List<Integer> getLabels() {
		return labels;
	}

	public void setLabels(List<Integer> labels) {
		this.labels = labels;
	}
	
}