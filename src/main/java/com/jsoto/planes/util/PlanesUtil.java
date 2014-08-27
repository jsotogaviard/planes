
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsoto.planes.data.ICsvWritable;
import com.jsoto.planes.data.impl.ACsvWritable;

public class PlanesUtil {


	public static void write(List<ICsvWritable> csvWritable, String folder) throws IOException {
		File f = new File(folder);
		if (!f.exists()) {
			Files.createDirectory(f.toPath());
		}
		for (ICsvWritable csv : csvWritable) {

			String file = folder + csv.getClass().getSimpleName();
			if (!Files.exists(new File(file).toPath())) {
				// Put the headers
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)))) {
					StringBuilder sb = new StringBuilder();
					
					// First row is field names
					for (Field field : csv.getClass().getDeclaredFields()) {
						if(Modifier.isProtected(field.getModifiers())){
							sb.append(field.getName());
							sb.append(ICsvWritable.SEPARATOR);
						}
					}
					sb.deleteCharAt(sb.length() - 1);
					out.println(sb.toString());
					
					// Second row is field types
					sb = new StringBuilder();
					for (Field field : csv.getClass().getDeclaredFields()) {
						if(Modifier.isProtected(field.getModifiers())){
							sb.append(field.getType().getSimpleName());
							sb.append(ICsvWritable.SEPARATOR);
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

	public static List<Map<String,Object>> loadFile(String file) {
		List<Map<String,Object>>result = new ArrayList<>();
		String[] headers = null;
		String[] types = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(ICsvWritable.SEPARATOR);
				if (headers == null) {
					headers = line;
				} else {
					if (types == null) {
						types = line;
					} else {
						Map<String,Object> element = new HashMap<>();
						for (int i = 0; i < line.length; i++) {
							final String header = headers[i];
							final String type = types[i];
							
							element.put(header, convert(line[i], type));
						}
						result.add(element);
					}
				}
			}
			return result;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} 

	}

	protected static Object convert(String string, String type) {
		if (type.equals(String.class.getSimpleName())) {
			return string;
		} else if (type.equals(List.class.getSimpleName())) {
			string = string.substring(1, string.length() - 1);
			String[] array = string.split(",");
			return array;
		} else if (type.equals(Integer.class.getSimpleName())) {
			return Integer.parseInt(string);
		} else if (type.equals(Double.class.getSimpleName())) {
			return Double.parseDouble(string);
		} else if (type.equals(Boolean.class.getSimpleName())) {
			return Boolean.parseBoolean(string);
		} else if (type.equals(Date.class.getSimpleName())) {
			try {
				return ACsvWritable.SDF.parse(string).getTime()/1000;
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException(string + " " + type);
		}
	}
}
