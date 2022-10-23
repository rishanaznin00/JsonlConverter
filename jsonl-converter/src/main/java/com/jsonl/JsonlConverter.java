package com.jsonl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;

public class JsonlConverter {
	private static final int SUCCESS = 1;
	private static final int FAILED = 0;

	public static void main(String[] args) {
		// String os = System.getProperty("os.name").substring(0, 7);
		Scanner s = new Scanner(System.in);
		System.out.println("please specify the input file: ");
		String inputFileName = s.nextLine();
		System.out.println("please enter the delimeter: ");
		final String delimeter = s.nextLine();
		System.out.println("please enter the skip charecter: ");
		final String skipCharacter = s.nextLine();
		s.close();

		String splitChar = "(?<!" + Pattern.quote(skipCharacter) + ")" + Pattern.quote(delimeter);
		int nameStrIndx = inputFileName.lastIndexOf("\\");
		String outputFileName = inputFileName.substring(0, nameStrIndx + 1)
				+ inputFileName.substring(nameStrIndx + 1, inputFileName.length()) + "-jsonl-converted";

		int result = generateJsonlFile(inputFileName, delimeter, splitChar, outputFileName);
		if (result == SUCCESS)
			System.out.println("Jsonl formated file generated successfully in " + outputFileName);
		else
			System.out.println("Some error occured! Please try again....");

	}

	public static int generateJsonlFile(String inputFileName, final String delimeter, String splitChar,
			String outputFileName) {
		Path inputPath = Paths.get(inputFileName);
		try {
			//Creating the output file
			File outputFile = new File(outputFileName);
			outputFile.createNewFile();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
			
			//Getting the field values of json format
			String firstLine = Files.lines(inputPath).findFirst().get();
			final List<String> fields = Arrays.asList(firstLine.split(Pattern.quote(delimeter)));
			
			String secondLine = Files.lines(inputPath).skip(1).limit(2).findFirst().get();
			try (Stream<String> lines = Files.lines(inputPath)) {
				//writing the 1st line with no comma at the end.
				try {
					outputStreamWriter.append("[{");
					String[] values = secondLine.split(splitChar);
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < fields.size() && i < values.length; i++) {
						String val = formatValue(values[i]);
						sb.append("\"" + fields.get(i) + "\":" + val + (i < fields.size() - 1 ? "," : ""));
					}
					outputStreamWriter.append(sb + "}");
				} catch (IOException e) {
					throw e;
				}

				//writing the rest of lines with comma at the start
				lines.skip(2).forEach(line -> {
					try {
						outputStreamWriter.append(",\n" + "{");
						System.out.println(line);
						String[] values = line.split(splitChar);
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < fields.size() && i < values.length; i++) {
							String val = formatValue(values[i]);
							sb.append("\"" + fields.get(i) + "\":" + val + (i < fields.size() - 1 ? "," : ""));
						}
						outputStreamWriter.append(sb + "}");

					} catch (IOException e) {
						throw new RuntimeException();
					}
				});
				outputStreamWriter.append("]");
				outputStreamWriter.close();

			} catch (Exception e) {
				throw e;
			}finally {
				outputStreamWriter.close();
			}
		} catch (Exception e) {
			return FAILED;
		}

		return SUCCESS;
	}

	public static String formatDate(String value) {
		if (value.trim().length() != 10)
			return value;
		DateTimeFormatter requiredFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<String> dateFormats = Arrays.asList("dd/MM/yyyy", "yyyy/MM/dd", "dd-MM-yyyy", "yyyy-MM-dd");
		for (String format : dateFormats) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
			try {
				LocalDate parsedDate = LocalDate.parse(value, dtf);
				value = parsedDate.format(requiredFormat);
				break;
			} catch (DateTimeParseException e) {

			}
		}
		return value;
	}

	public static String formatValue(String value) {
		if (NumberUtils.isParsable(value))
			return value;
		value = "\"" + formatDate(value) + "\"";

		return value;
	}
	
	public static List<String> customSplit(String line, char splitChar){
		List<String> list= new ArrayList<>();
		char[] ltr= line.toCharArray();
		StringBuilder sb= new StringBuilder();
		boolean skip=false;
		for(int i=0; i<ltr.length; i++) {
			if(ltr[i]=='"') {
				skip=!skip;
			}else if(!skip && ltr[i]==splitChar) {
				list.add(sb.toString());
				sb=new StringBuilder();
			}else if(ltr[i]!=splitChar) {
				sb.append(ltr[i]);
			}
		}
		return list;
	}

}
