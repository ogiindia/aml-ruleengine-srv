package com.aml.srv.core.efrm.cust.scoring;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Split {
	public List<Map<String, Collection<String>>> trainSamples;
	public List<Integer> trainLabels;
	public List<Map<String, Collection<String>>> testSamples;
	public List<Integer> testLabels;
}
