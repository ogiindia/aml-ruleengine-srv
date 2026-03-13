package com.aml.srv.core.efrm.cust.scoring;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class LogisticRegression {

	private final FeatureSpace space;
	private final double[] w; // weights
	private Random rnd = new Random(42); // reproducible training

	public LogisticRegression() {
		this.space = new FeatureSpace();
		this.w = null;
		
	}
	public LogisticRegression(FeatureSpace space) {
		this.space = space;
		this.w = new double[space.size()];
	}

	public LogisticRegression(FeatureSpace space, double[] initialWeights) {
		this.space = space;
		if (initialWeights.length != space.size()) {
			throw new IllegalArgumentException("initialWeights length mismatch");
		}
		this.w = Arrays.copyOf(initialWeights, initialWeights.length);
	}

	public void setRandomSeed(long seed) {
		this.rnd = new Random(seed);
	}

	public static double sigmoid(double z) {
		// numerically stable sigmoid
		if (z >= 0) {
			double ez = Math.exp(-z);
			return 1.0 / (1.0 + ez);
		} else {
			double ez = Math.exp(z);
			return ez / (1.0 + ez);
		}
	}

	/** yhat = sigmoid(w·x) */
	public double predictProb(double[] x) {
		double dot = 0.0;
		for (int i = 0; i < w.length; i++)
			dot += w[i] * x[i];
		return sigmoid(dot);
	}

	/** Raw score (logit) = w·x */
	public double predictLogit(double[] x) {
		double dot = 0.0;
		for (int i = 0; i < w.length; i++)
			dot += w[i] * x[i];
		return dot;
	}

	/** Get copy of weights */
	public double[] weights() {
		return Arrays.copyOf(w, w.length);
	}

	/** Set weight for a specific full key or __BIAS__ */
	public void setWeight(String key, double value) {
		Integer idx;
		if ("__BIAS__".equals(key)) {
			if (!space.usesBias())
				throw new IllegalArgumentException("No bias in space");
			idx = space.biasIndex();
		} else {
			idx = space.indexOfKey(key);
		}
		if (idx == null || idx < 0)
			throw new IllegalArgumentException("Unknown key: " + key);
		w[idx] = value;
	}

	/**
	 * Train with simple SGD for binary classification. L2 regularization applied to
	 * weights EXCEPT bias (common practice).
	 * 
	 * @param X      list of feature vectors
	 * @param y      list of labels (0 or 1)
	 * @param epochs training epochs
	 * @param lr     learning rate
	 * @param l2     L2 regularization strength (e.g., 1e-6 to 1e-3)
	 */
	public void fitSGD(List<double[]> X, List<Integer> y, int epochs, double lr, double l2) {
		if (X.size() != y.size())
			throw new IllegalArgumentException("X.size() != y.size()");
		int n = X.size();
		int d = w.length;

		int[] order = new int[n];
		for (int i = 0; i < n; i++)
			order[i] = i;

		boolean hasBias = space.usesBias();
		int biasIdx = space.biasIndex();

		for (int ep = 0; ep < epochs; ep++) {
			shuffle(order, rnd);
			for (int id : order) {
				double[] xi = X.get(id);
				int yi = y.get(id);
				double p = predictProb(xi);
				double error = p - yi; // derivative of logloss wrt logit

				// SGD update with L2; exclude bias from L2
				for (int j = 0; j < d; j++) {
					double reg = (hasBias && j == biasIdx) ? 0.0 : l2 * w[j];
					double grad = error * xi[j] + reg;

					// optional gradient clipping
					if (grad > 10)
						grad = 10;
					else if (grad < -10)
						grad = -10;

					w[j] -= lr * grad;
				}
			}
		}
	}

	private static void shuffle(int[] a, Random r) {
		for (int i = a.length - 1; i > 0; i--) {
			int j = r.nextInt(i + 1);
			int t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	/* ---- Convenience for persistence ---- */
	public Map<String, Double> exportWeights() {
		Map<String, Double> m = new LinkedHashMap<>();
		List<String> keys = space.keys();
		for (int i = 0; i < w.length; i++)
			m.put(keys.get(i), w[i]);
		return m;
	}

	public void importWeights(Map<String, Double> m) {
		for (Map.Entry<String, Double> e : m.entrySet()) {
			setWeight(e.getKey(), e.getValue());
		}
	}

}
