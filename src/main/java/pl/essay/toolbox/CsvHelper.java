package pl.essay.toolbox;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

public class CsvHelper {
	
	protected static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);
	
	public static String getStringFromInputStream(InputStream csv) throws IOException {
		String content = IOUtils.toString(csv,"UTF-8");
		return content;
	}
	
	public static Map<String, String[]> getMapFromCsv(String fileContent, int indexForKey ) throws IOException{

		List<String[]> csvSplitted = new ArrayList<String[]>();

		CSVReader reader = new CSVReader(new StringReader(fileContent),',','"' ) ;
		csvSplitted = reader.readAll();
		reader.close();
		
		Map<String, String[]> csv = new HashMap<String, String[]>();
		Iterator<String[]> csvIterator = csvSplitted.iterator();
		while (csvIterator.hasNext()) {
			String[] line = csvIterator.next();
			csv.put(line[indexForKey], line);
		}
		return csv;
	}
	
	public static Map<String, String[]> getMapStringStringArrayFromIS(InputStream is) {
		//InputStream csv =	(InputStream) Translator.class.getResourceAsStream(file);

		Map<String, String[]> tempMap = new HashMap<String,String[]>();
		if (is == null)
			return tempMap;
	
		String content = "";
		try {
			content = CsvHelper.getStringFromInputStream(is);
		} catch (IOException e1) {
			logger.error("", e1);
		}
		
		try {
			tempMap = CsvHelper.getMapFromCsv(content, 0);
		} catch (IOException e) {
			logger.error("", e);
		}
		if (is != null)
			try {
				is.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		return tempMap;
	}

}
