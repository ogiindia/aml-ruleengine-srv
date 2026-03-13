package com.aml.srv.core.efrm.trans.scoring;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.k11i.xgboost.Predictor;

public class UltraFraudUtils {
	private static final Logger log = LoggerFactory.getLogger(UltraFraudUtils.class);
	// ======= Configuration (same as Python) =======
	public static final double BASE_CLASS_PROBABILITY = 0.02; // 2%
	public static final int HIGH_THRESHOLD = 80;
	public static final int MEDIUM_THRESHOLD = 50;

	// Model features order (must match training)
	public static final String[] MODEL_FEATURES = new String[] { "amount", "country_risk", "device_trusted",
			"velocity_1hr", "account_age_days" };

	public static final Set<String> HIGH_RISK_COUNTRIES = new HashSet<>(Arrays.asList("IR", "KP", "SY", "AF", "PK"));

	public static final String DEFAULT_OUTPUT = "TRNS_SCORED.csv";
	// private static final String DEFAULT_MODEL = "xgb_fraud_model.bin"; // binary
	// model (see README)
	public static final String DEFAULT_MODEL = "C:\\Users\\FIS\\Downloads\\xgb_fraud_model.json"; 
	private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("d/M/uuuu"), DateTimeFormatter.ofPattern("dd/MM/uuuu"),
			DateTimeFormatter.ofPattern("uuuu-MM-dd"), DateTimeFormatter.ofPattern("M/d/uuuu"), };
	private static final DateTimeFormatter[] TIME_FORMATS = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("H:mm:ss"), DateTimeFormatter.ofPattern("H:mm"),
			DateTimeFormatter.ofPattern("HHmmss"), DateTimeFormatter.ofPattern("HHmm") };

	// ---------- Helpers ----------
	public static double parseDoubleSafe(String s, double def) {
		if (s == null || s.isBlank())
			return def;
		try {
			return Double.parseDouble(s.trim());
		} catch (Exception e) {
			return def;
		}
	}

	public static LocalDate parseDateSafe(String s) {
		if (s == null || s.isBlank())
			return null;
		for (DateTimeFormatter f : DATE_FORMATS) {
			try {
				return LocalDate.parse(s.trim(), f);
			} catch (DateTimeParseException ignored) {
			}
		}
		return null;
	}

	public static LocalTime parseTimeSafe(String s) {
		if (s == null || s.isBlank())
			return null;
		for (DateTimeFormatter f : TIME_FORMATS) {
			try {
				return LocalTime.parse(s.trim(), f);
			} catch (DateTimeParseException ignored) {
			}
		}
		return null;
	}
	
	public static double[] buildFeatureVector(Map<String, String> r) {
		double[] v = new double[MODEL_FEATURES.length];
		for (int i = 0; i < MODEL_FEATURES.length; i++) {
			v[i] = parseDoubleSafe(r.get(MODEL_FEATURES[i]), 0.0);
		}
		return v;
	}

	public static String[] applyDecision(double score) {
		if (score >= HIGH_THRESHOLD)
			return new String[] { "HIGH", "DECLINE" };
		if (score >= MEDIUM_THRESHOLD)
			return new String[] { "MEDIUM", "REVIEW" };
		return new String[] { "LOW", "APPROVE" };
	}

	public static String deriveReason(Map<String, String> r, double prob) {
		List<String> reasons = new ArrayList<>();
		double velocity = parseDoubleSafe(r.get("velocity_1hr"), 0);
		double amount = parseDoubleSafe(r.get("amount"), 0);
		double countryRisk = parseDoubleSafe(r.get("country_risk"), 0);
		double acctAge = parseDoubleSafe(r.get("account_age_days"), 365);

		if (velocity >= 5)
			reasons.add("High transaction velocity (≥5 txns in 1 hour)");
		if (amount >= 100000)
			reasons.add("High transaction amount");
		if (countryRisk >= 5)
			reasons.add("High-risk counterparty country");
		if (acctAge < 30)
			reasons.add("Newly opened account");
		if (prob >= 0.90)
			reasons.add("High fraud probability based on combined risk signals");

		return reasons.isEmpty() ? "Normal transaction behavior" : String.join("; ", reasons);
	}
	
	// ---------- Scoring ----------
		private Optional<Predictor> loadPredictor(String modelPath) {
			File f = new File(modelPath);
			if (!f.exists()) {
				log.warn("Model file not found: {} (will use fallback scoring).", f.getAbsolutePath());
				return Optional.empty();
			}
			try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
				return Optional.of(new Predictor(is));
			} catch (IOException e) {
				log.warn("Unable to load model '{}': {} (using fallback scoring).", modelPath, e.toString());
				return Optional.empty();
			}
		}

		public static double toDoubleTwoDecimals(Object obj) {
			if (obj == null)
				return 0.0;

			double value;
			if (obj instanceof Double) {
				value = (Double) obj;
			} else if (obj instanceof Float) {
				value = ((Float) obj).doubleValue();
			} else if (obj instanceof Integer) {
				value = ((Integer) obj).doubleValue();
			} else if (obj instanceof Long) {
				value = ((Long) obj).doubleValue();
			} else if (obj instanceof String) {
				try {
					value = Double.parseDouble((String) obj);
				} catch (NumberFormatException e) {
					value = 0.0;
					throw new IllegalArgumentException("String cannot be parsed to double: " + obj);
				}
			} else {
				value = 0.0;
				throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
			}

			// Round to 2 decimal places using BigDecimal
			return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}

}
