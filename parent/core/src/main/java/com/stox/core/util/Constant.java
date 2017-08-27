package com.stox.core.util;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

public class Constant {

	public static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
	public static final String PATH = System.getProperty("user.home") + File.separator + ".stox" + File.separator;
	public static final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	public static final DateFormat dateFormatFull = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
	public static final NumberFormat currencyFormat = NumberFormat.getInstance();
	public static final String LINEFEED = System.getProperty("line.separator", "\n");
	public static final ObjectMapper objectMapper = new ObjectMapper();
	public static final CsvMapper csvMapper = new CsvMapper();

	static {
		Constant.currencyFormat.setGroupingUsed(true);
		Constant.currencyFormat.setMaximumFractionDigits(2);
		Constant.currencyFormat.setMinimumFractionDigits(0);

		Constant.objectMapper.enableDefaultTyping();
		Constant.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Constant.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		Constant.csvMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
	}

}
