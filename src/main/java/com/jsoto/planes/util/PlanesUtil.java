
package com.jsoto.planes.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsoto.planes.data.ICsvWritable;

public class PlanesUtil {


	public static void write(List<ICsvWritable> csvWritable, String folder) {
		for (ICsvWritable csv : csvWritable) {

			String file = folder + csv.getClass().getSimpleName();
			if (!Files.exists(new File(file).toPath())) {
				// Put the headers
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)))) {
					StringBuilder sb = new StringBuilder();
					for (Field field : csv.getClass().getDeclaredFields()) {
						if(Modifier.isProtected(field.getModifiers())){
							sb.append(field.getName());
							sb.append(",");
						}
					}
					sb.deleteCharAt(sb.length() - 1);
					out.println(sb.toString());
				}catch (IOException e) {
					e.printStackTrace();
				}
			} 

			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
				out.println(csv.toCsv());
			}catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) { 
			for(File f: files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					if(!f.delete())
						throw new RuntimeException(f + "");
				}
			}
		}
		folder.delete();
	}

	public static List<Map<String,String>> loadFile(String file) {
		List<Map<String,String>>result = new ArrayList<>();
		String[] headers = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(",");
				if (headers == null) {
					headers = line;
				} else {
					Map<String,String> element = new HashMap<>();
					for (int i = 0; i < line.length; i++) {
						element.put(headers[i], line[i]);
					}
					result.add(element);
				}
			}
			return result;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} 

	}
}
