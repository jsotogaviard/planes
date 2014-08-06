
package com.jsoto.planes.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.jsoto.planes.data.ICsvWritable;
import com.jsoto.planes.data.impl.Person;

public class PlanesUtil {


	public static void write(List<ICsvWritable> csvWritable, String folder) {
		for (ICsvWritable csv : csvWritable) {
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(folder + csv.getClass().getSimpleName(), true)))) {
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
					f.delete();
				}
			}
		}
	}

	public static void generatePeople(int num, String folder) {
		List<ICsvWritable> persons = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			persons.add(new Person("person" + i, "John", "Sullivan"));
		}
		write(persons, folder);

	}
}
