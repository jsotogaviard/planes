package edu.upc.dama.planes.preparers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.upc.dama.dex.preparers.CSVAware;
import edu.upc.dama.dex.preparers.Preparer;

public class ScheduledFlyingTimeNormalizer implements Preparer, CSVAware {

	private File file;
	private Character separator = CSVParser.DEFAULT_SEPARATOR;
	private Character quoteChar = CSVParser.DEFAULT_QUOTE_CHARACTER;
	private Character scape = CSVParser.DEFAULT_ESCAPE_CHARACTER;
	private Integer line = 0;
	private Boolean strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;

	private Integer scheduledFlyingTimeColumn;

	private File output;

	public Integer getScheduledFlyingTimeColumn() {
		return scheduledFlyingTimeColumn;
	}

	public void setScheduledFlyingTimeColumn(Integer scheduledFlyingTimeColumn) {
		this.scheduledFlyingTimeColumn = scheduledFlyingTimeColumn;
	}

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
				String scheduledTime = line[scheduledFlyingTimeColumn];
				if (!"".equals(scheduledTime)) {
					String[] time = scheduledTime.trim().split(":");
					Integer hours = Integer.parseInt(time[0].trim());
					Integer minutes = Integer.parseInt(time[1].trim());
					line[scheduledFlyingTimeColumn] = Long
							.toString((long) (hours * 60 * 60 * 1000)
									+ (long) (minutes * 60 * 1000));
				}
				writer.writeNext(line);
				line = reader.readNext();
			}
		} finally {
			writer.close();
			reader.close();
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
