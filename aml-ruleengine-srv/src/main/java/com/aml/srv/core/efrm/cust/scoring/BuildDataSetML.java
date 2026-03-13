package com.aml.srv.core.efrm.cust.scoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class BuildDataSetML {

	public static final Logger LOGGER = LoggerFactory.getLogger(BuildDataSetML.class);

	 /** Synthetic demo dataset (replace with real labeled data) */
   public void buildDemoDataset(List<Map<String, Collection<String>>> samples, List<Integer> labels) {
        // Label 1 = "genuine" (or low risk). Adjust per your use-case.
        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Aadhaar_eKYC"),
                "Account_Status", Arrays.asList("Active"),
                "Customer_Segment", Arrays.asList("Salary"),
                "Channel", Arrays.asList("UPI")
        )); labels.add(1);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Branch"),
                "Account_Status", Arrays.asList("Dormant"),
                "Customer_Segment", Arrays.asList("Retail"),
                "Channel", Arrays.asList("IMPS")
        )); labels.add(0);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("VideoKYC")
              /*  "Account_Status", Arrays.asList("Active"),
                "Customer_Segment", Arrays.asList("Retail"),
                "Channel", Arrays.asList("NEFT")*/
        )); labels.add(1);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Physical")
               /* "Account_Status", Arrays.asList("Closed"),
                "Customer_Segment", Arrays.asList("Corporate"),
                "Channel", Arrays.asList("IMPS", "RTGS")*/
        )); labels.add(0);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Aadhaar_eKYC")
               /* "Account_Status", Arrays.asList("Active"),
                "Customer_Segment", Arrays.asList("Retail"),
                "Channel", Arrays.asList("UPI", "NEFT")*/
        )); labels.add(1);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Branch")
               /* "Account_Status", Arrays.asList("Inactive"),
                "Customer_Segment", Arrays.asList("Corporate"),
                "Channel", Arrays.asList("RTGS")*/
        )); labels.add(0);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("VideoKYC")
                /*"Account_Status", Arrays.asList("Active"),
                "Customer_Segment", Arrays.asList("Salary"),
                "Channel", Arrays.asList("ATM")*/
        )); labels.add(1);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Physical")
               /* "Account_Status", Arrays.asList("Dormant"),
                "Customer_Segment", Arrays.asList("Retail"),
                "Channel", Arrays.asList("IMPS")*/
        )); labels.add(0);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Corporate")
               /* "Account_Status", Arrays.asList("Active"),
                "Customer_Segment", Arrays.asList("Corporate"),
                "Channel", Arrays.asList("NEFT", "RTGS")*/
        )); labels.add(1);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Branch")
               /* "Account_Status", Arrays.asList("Closed"),
                "Customer_Segment", Arrays.asList("Retail"),
                "Channel", Arrays.asList("POS")*/
        )); labels.add(0);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Aadhaar_eKYC")
               /* "Account_Status", Arrays.asList("Active"),
                "Customer_Segment", Arrays.asList("HNI"),
                "Channel", Arrays.asList("UPI")*/
        )); labels.add(1);

        samples.add(mapOf(
                "Mode_of_Acc_Open", Arrays.asList("Branch")
                /*"Account_Status", Arrays.asList("Dormant"),
                "Customer_Segment", Arrays.asList("Corporate"),
                "Channel", Arrays.asList("IMPS")*/
        )); labels.add(0);
    }
    
  

	

	

	/**
	 * 
	 * @param kvPairs
	 * @return
	 */
	
    public Map<String, Collection<String>> mapOf(Object... kvPairs) {
        if (kvPairs.length % 2 != 0) throw new IllegalArgumentException("Pairs must be even length");
        Map<String, Collection<String>> m = new LinkedHashMap<>();
        for (int i = 0; i < kvPairs.length; i += 2) {
            String k = (String) kvPairs[i];
            @SuppressWarnings("unchecked")
            Collection<String> v = (Collection<String>) kvPairs[i + 1];
            m.put(k, v);
        }
        return m;
    }
    
	/**
	 * 
	 * @param prob
	 * @param mediumCut
	 * @param highCut
	 * @return
	 */
   public static String riskBand(double prob, double mediumCut, double highCut) {
        if (prob >= highCut) return "HIGH";
        if (prob >= mediumCut) return "MEDIUM";
        return "LOW";
    }
    
   /**
    * 
    * @param samples
    * @param labels
    * @param trainRatio
    * @param seed
    * @return
    */
	public Split trainTestSplit(List<Map<String, Collection<String>>> samples, List<Integer> labels,
			double trainRatio, long seed) {
		if (samples.size() != labels.size())
			throw new IllegalArgumentException("Size mismatch");
		int n = samples.size();
		List<Integer> idx = new ArrayList<>(n);
		for (int i = 0; i < n; i++)
			idx.add(i);
		Collections.shuffle(idx, new Random(seed));

		int nTrain = (int) Math.round(n * trainRatio);
		Split s = new Split();
		s.trainSamples = new ArrayList<>();
		s.trainLabels = new ArrayList<>();
		s.testSamples = new ArrayList<>();
		s.testLabels = new ArrayList<>();

		for (int i = 0; i < n; i++) {
			int id = idx.get(i);
			if (i < nTrain) {
				s.trainSamples.add(samples.get(id));
				s.trainLabels.add(labels.get(id));
			} else {
				s.testSamples.add(samples.get(id));
				s.testLabels.add(labels.get(id));
			}
		}
		return s;
	}
}
