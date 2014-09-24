package edu.upc.dama.planes.preparers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.upc.dama.dex.preparers.CSVAware;
import edu.upc.dama.dex.preparers.Preparer;

public class RemoveAnnulatedLegs implements Preparer, CSVAware {
	private File file;

	private Character separator = CSVParser.DEFAULT_SEPARATOR;

	private Character quoteChar = CSVParser.DEFAULT_QUOTE_CHARACTER;

	private Character scape = CSVParser.DEFAULT_ESCAPE_CHARACTER;

	private Integer line = 0;

	private Boolean strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;

	private File output;


	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}


	@Override
	public void execute() throws Exception {

		CSVReader reader = new CSVReader(new FileReader(file), separator,
				quoteChar, scape, line, strictQuotes);
		CSVWriter writer = new CSVWriter(new FileWriter(output), separator,
				quoteChar, scape);
		try {
			String[] line = reader.readNext();
			while (line != null) {
				if(!"A".equals(line[4])){
					writer.writeNext(line);
				}
				line = reader.readNext();
			}
		} finally {
			reader.close();
			writer.close();
		}

	}

	@Override
	public void setFile(File f) {
		this.file = f;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setSeparator(Character separator) {
		this.separator = separator;
	}

	@Override
	public void setQuoteChar(Character quoteChar) {
		this.quoteChar = quoteChar;
	}

	@Override
	public void setEscape(Character scape) {
		this.scape = scape;
	}

	@Override
	public void setLine(Integer line) {
		this.line = line;
	}

	@Override
	public void setStrictQuotes(Boolean strictQuotes) {
		this.strictQuotes = strictQuotes;
	}

}
