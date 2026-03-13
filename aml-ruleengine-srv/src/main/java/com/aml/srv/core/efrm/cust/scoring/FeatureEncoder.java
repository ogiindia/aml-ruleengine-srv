package com.aml.srv.core.efrm.cust.scoring;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeatureEncoder {

	@Autowired
	private FeatureSpace space;

	public FeatureEncoder() {
		// default constructor for Jackson
	}

	public FeatureSpace getSpace() {
		return space;
	}

	public void setSpace(FeatureSpace space) {
		this.space = space;
	}

	// @JsonCreator@JsonProperty("space")
	public FeatureEncoder(FeatureSpace space) {
		this.space = space;
	}

	/**
	 * Encode presence-based input into a one-hot vector: input: Map<FeatureName,
	 * Collection<ValuesPresent>>
	 */
	public double[] encode(Map<String, ? extends Collection<String>> input) {
		double[] x = space.newZeroVector();
		if (space.usesBias())
			x[space.biasIndex()] = 1.0;

		if (input == null)
			return x;

		for (Map.Entry<String, ? extends Collection<String>> e : input.entrySet()) {
			String feature = e.getKey();
			Collection<String> presentValues = e.getValue();
			if (presentValues == null)
				continue;

			for (String val : presentValues) {
				Integer idx = space.indexOf(feature, val);
				if (idx != null)
					x[idx] = 1.0;
			}
		}
		return x;
	}

}
