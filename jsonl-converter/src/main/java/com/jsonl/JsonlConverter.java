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
	
	

	public static void main(String[] args) {
		//String os = System.getProperty("os.name").substring(0, 7);
		Scanner s = new Scanner(System.in);
		System.out.println("please specify the input file: ");
		String inputFileName = s.nextLine();
		System.out.println("please enter the delimeter: ");
		final String delimeter=s.nextLine();
		System.out.println("please enter the skip charecter: ");
		final String skipCharacter=s.nextLine();
		s.close();
		generateJsonlFile(inputFileName, delimeter, skipCharacter);
		
	}


	private static void generateJsonlFile(String inputFileName, final String delimeter, String skipCharacter) {
		Path inputPath = Paths.get(inputFileName);
		try {
			String firstLine = Files.lines(inputPath).findFirst().get();

			final List<String> fields = Arrays.asList(firstLine.split(Pattern.quote(delimeter)));
		
			try (Stream<String> lines = Files.lines(inputPath)) {
				int nameStrIndx = inputFileName.lastIndexOf("\\");
				File outputFile = new File(inputFileName.substring(0, nameStrIndx + 1)
						+ inputFileName.substring(nameStrIndx + 1, inputFileName.length()) + "-jsonl-converted");
				outputFile.createNewFile();

				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
				outputStreamWriter.append("[");
				lines.skip(1).forEach(line -> {
					try {

						outputStreamWriter.append("{");
						System.out.println(line);
						String[] values = line.split("(?<!"+Pattern.quote(skipCharacter)+")"+Pattern.quote(delimeter));
					//	String[] values = line.split(Pattern.quote(delimeter));
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < fields.size() && i <values.length; i++) {
							String val=formatValue( values[i]);
							sb.append("\""+fields.get(i)+ "\":" + val+ (i< fields.size()-1?",":""));
						}
						System.out.println(sb);
						outputStreamWriter.append(sb + "},"+"\n");

					} catch (IOException e) {
						
						e.printStackTrace();
					}
				});
				outputStreamWriter.append("]");
				outputStreamWriter.close();
				// System.out.println(firstLine);
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static String formatDate(String value) {
		if(value.trim().length()!=10) return value;
		DateTimeFormatter requiredFormat=  DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<String> dateFormats= Arrays.asList("dd/MM/yyyy", "yyyy/MM/dd", "dd-MM-yyyy", "yyyy-MM-dd");
	    for(String format:dateFormats) {
	    	    DateTimeFormatter dtf= DateTimeFormatter.ofPattern(format);
	    		try {
					LocalDate  parsedDate=LocalDate.parse(value, dtf);
					value=parsedDate.format(requiredFormat);
					break;
				} catch (DateTimeParseException e) {
					
				}
			
	    }
	   
		return value;
	}
	public static String formatValue(String value) {
		if(NumberUtils.isParsable(value)) return value;
		value= "\""+formatDate(value)+"\"";
		
		return value;
	}
	
}
