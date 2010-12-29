/**
 * 
 */
package org.amanzi.neo.loader.core.network.generator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Kasnitskij_V
 *
 */
public class GeneratorTrafficData {
	
	private static final String SECTOR_NAME = "BTS_Name";
	/**
	 * @param args args[0] - inputFile, args[1] - outputFile
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		generate(args[0], args[1]);
	}
	
	private static void generate(String inputFile, String outputFile) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(inputFile), (char)9);
		
		int neededIndex = 0;
		ArrayList<String> sectorNames = new ArrayList<String>();
		
		String[] data = reader.readNext();
		
		for (int i = 0; i < data.length; i++) {
			if (data[i].equals(SECTOR_NAME)) {
				neededIndex = i;
				break;
			}
		}
		while (true) {
			data = reader.readNext();
			System.out.println(data);
			if (data == null) 
				break;
			
			if (!sectorNames.contains(data[neededIndex])) {
					sectorNames.add(data[neededIndex]);
					System.out.println(data[neededIndex]);
			}
		}
		
		CSVFile file = new CSVFile(new File(outputFile));
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("Sector");
		headers.add("Traffic");
		
		file.writeHeaders(headers);
		
		Iterator<String> iterator = sectorNames.iterator();
		Double traffic = 0.0;
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < sectorNames.size(); i++) {
			traffic = MyRandom.randomDouble(0, 1000, 1);
			values.add(iterator.next());
			values.add(traffic.toString());
			
			file.writeData(values);
			values.clear();
		}
		
		file.close();
	}

}