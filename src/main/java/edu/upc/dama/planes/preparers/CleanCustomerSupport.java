package edu.upc.dama.planes.preparers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import edu.upc.dama.dex.preparers.Preparer;

public class CleanCustomerSupport implements Preparer {

	private File file;

	private File output;

	public void setFile(File f) {
		this.file = f;
	}

	public File getFile() {
		return file;
	}
	
	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	@Override
	public void execute() throws Exception {
		FileReader fr = new FileReader(file);
		FileWriter fw = new FileWriter(output);

		BufferedReader reader = new BufferedReader(fr);
		BufferedWriter writer = new BufferedWriter(fw);

		try {
			String line = reader.readLine();
			line = reader.readLine();
			while (line != null) {
				if (line.endsWith(";")) {
					line = line.substring(0, line.length() - 1);
				}
				writer.write(line + "\n");
				line = reader.readLine();
			}
		} finally {
			reader.close();
			writer.close();
		}
	}

}
