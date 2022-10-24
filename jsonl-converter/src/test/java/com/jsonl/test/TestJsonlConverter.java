package com.jsonl.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.jsonl.JsonlConverter;

public class TestJsonlConverter {

	@Test
	public void testConverter() {
		String inputFileName="src/test/resource/input/DSV input 1.jsonl";
		 String delimeter=",";
		String outputFileName="src/test/resource/output/DSV output 1.jsonl";
		String expectedOutputFileName="src/test/resource/expectedOutput/JSONL output.jsonl";
		
		checkFiles(outputFileName, expectedOutputFileName, inputFileName, delimeter );
		
		 inputFileName="src/test/resource/input/DSV input 2.txt";
		 delimeter="|";
		 outputFileName="src/test/resource/output/DSV output 2.jsonl";
		 checkFiles(outputFileName, expectedOutputFileName, inputFileName, delimeter );
			
			 
	}

	private void checkFiles(String outputFileName, String expectedOutputFileName, String inputFileName, String delimeter) {
		int result= JsonlConverter.generateJsonlFile(inputFileName, delimeter, outputFileName);
		
		
		try {
			BufferedReader bfr= new BufferedReader(new FileReader(outputFileName));
			BufferedReader bfrE= new BufferedReader(new FileReader(expectedOutputFileName));

			String line1= bfr.readLine();
			String line2= bfrE.readLine();
			
			
			while(line1!=null && line2!=null) {
				if(line1==null || line2==null) {
					assert(false);
					break;
				}
				System.out.println(line1);
				System.out.println(line2);
				if(!line1.trim().equals(line2.trim())) {
					assert(false);
				}
				line1=bfr.readLine();
				line2=bfrE.readLine();
			}
			
			bfr.close();
			bfrE.close();
		} catch (FileNotFoundException e) {
			assert(false);
		} catch (IOException e) {
			assert(false);
		}
	}
}
