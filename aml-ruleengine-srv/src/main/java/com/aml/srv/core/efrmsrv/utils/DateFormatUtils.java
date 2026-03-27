package com.aml.srv.core.efrmsrv.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DateFormatUtils {

	// List of possible formats you expect
	private static final String[] DATE_PATTERNS = 
		{ "dd-MM-yyyy","dd-MMM-yyyy",
		  "dd-MMM-yy hh.mm.ss.nnnnnnnnn a",
		  "yyyy-MM-dd HH:mm:ss", 
		  "yyyy-MM-dd", 
		  "dd/MM/yyyy", 
	      "MM/dd/yyyy",
		  "dd-MM-yyyy HH:mm:ss", "MM-dd-yyy"};
	
	public LocalDate parseToLocalDate(String text) {
	    for (String pattern : DATE_PATTERNS) {
	        try {
	            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
	                    .parseCaseInsensitive()             // handles JAN / Jan / jan
	                    .appendPattern(pattern)
	                    .toFormatter(Locale.ENGLISH);      // month names in English

	            TemporalAccessor ta = formatter.parse(text);

	            int year  = ta.get(ChronoField.YEAR);
	            int month = ta.get(ChronoField.MONTH_OF_YEAR);
	            int day   = ta.get(ChronoField.DAY_OF_MONTH);

	            return LocalDate.of(year, month, day);
	        } catch (DateTimeParseException ex) {
	            // try next pattern
	        }
	    }
	    throw new IllegalArgumentException("Unsupported date format: " + text);
	}
	
	public Timestamp getTimestampValue(Map<String, Object> map, String key) {
        Object value = map.getOrDefault(key, null);
        if (value == null) {
            return null;
        }
        // Already a Timestamp
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        // Already a Date
        if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        }

        // Try parsing string with multiple patterns
        String strVal = value.toString().trim();
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                Date parsed = sdf.parse(strVal);
                return new Timestamp(parsed.getTime());
            } catch (Exception ignored) {
                // try next pattern
            }
        }

        System.out.println("Could not parse date for key: " + key + " value: " + strVal);
        return null;
    }
	
	public java.sql.Timestamp toTimestamp(String str) {
	   // String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd/MM/yyyy"};
	    for (String p : DATE_PATTERNS) {
	        try {
	            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(p);
	            return new java.sql.Timestamp(sdf.parse(str).getTime());
	        } catch (Exception ignored) {}
	    }
	    return null;
	}
	
	public boolean isDate(String str) {
	    //String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd/MM/yyyy"};
	    for (String p : DATE_PATTERNS) {
	        try {
	            new java.text.SimpleDateFormat(p).parse(str);
	            return true;
	        } catch (Exception ignored) {}
	    }
	    return false;
	}
	
	public String chageDateFormatLocalDate(String destFormat,LocalDate locDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(destFormat);
		String text = locDate.format(formatter);
		return text;
	}
	
	public String chageDateFormatLocalDateTime(String destFormat,LocalDateTime  locDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(destFormat);
		String text = locDate.format(formatter);
		return text;
	}
	
	public long getCurrentTIme() {
		return new Date().getTime();
	}
}
