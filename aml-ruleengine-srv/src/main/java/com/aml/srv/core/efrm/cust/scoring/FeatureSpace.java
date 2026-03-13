package com.aml.srv.core.efrm.cust.scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureSpace {

    // Ordered keys -> index
    private final List<String> keyByIndex = new ArrayList<>();
    private final Map<String, Integer> indexByKey = new HashMap<>();

    @Value("${feature.use-bias:false}")
    private boolean useBias;
   // private boolean useBias = false;
    private int biasIndex = -1;
    
    public FeatureSpace(boolean useBias, int dimension) {
    	this.useBias = useBias;
    	this.biasIndex=dimension;
    }
    public FeatureSpace() {
    	this.useBias = true;
    	this.biasIndex=-1;
    }

	/*
	 * public FeatureSpace() {
	 * 
	 * }
	 */
    /**
     * @param schema Map<FeatureName, List<PossibleValues>>
     * @param useBias include bias term as x[0]=1
     */
    public FeatureSpace(Map<String, List<String>> schema, boolean useBias) {
        this.useBias = useBias;
        if (useBias) {
            biasIndex = 0;
            keyByIndex.add("__BIAS__");
            indexByKey.put("__BIAS__", 0);
        }

        // deterministic, stable order
        List<String> featureNames = new ArrayList<>(schema.keySet());
        Collections.sort(featureNames);

        for (String feature : featureNames) {
            List<String> values = new ArrayList<>(schema.get(feature));
            Collections.sort(values);
            for (String val : values) {
                String key = feature + "=" + val;
                int idx = keyByIndex.size();
                keyByIndex.add(key);
                indexByKey.put(key, idx);
            }
        }
    }

    public int size() { return keyByIndex.size(); }
    public boolean usesBias() { return useBias; }
    public int biasIndex() { return biasIndex; }
    public List<String> keys() { return Collections.unmodifiableList(keyByIndex); }

    /** Full key lookup, e.g., "Account_Status=Active" or "__BIAS__" */
    public Integer indexOfKey(String fullKey) { return indexByKey.get(fullKey); }

    /** (feature, value) lookup */
    public Integer indexOf(String feature, String value) { return indexByKey.get(feature + "=" + value); }

    public double[] newZeroVector() { return new double[size()]; }

}