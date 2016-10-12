package pl.essay.languages;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.essay.toolbox.CsvHelper;

public class Translator {

	protected static final Logger logger = LoggerFactory.getLogger(Translator.class);

	private Map<String,String> translations = new HashMap<String,String>();

	private String translationFile;
	private String language;

	public void setLanguage(String l){
		this.language = l;
	}

	public void setTranslationFile(String l){
		logger.info("setting translation file for language "+this.language+": "+ l );
		this.translationFile = l;
	}

	public void init() {
		logger.info("getting translation file for language "+this.language+": "+this.translationFile );

		InputStream is = getClass().getClassLoader().getResourceAsStream(this.translationFile);

		if (is == null){
			logger.error("no translation file found for language "+this.language+" checked file "+this.translationFile);
		} else {
			Map<String, String[]> tempMap = CsvHelper.getMapStringStringArrayFromIS(is);

			for (Map.Entry<String,String[]> lineCsv: tempMap.entrySet()){
				if (lineCsv.getValue().length == 2) {//just skip bad lines
					this.translations.put(lineCsv.getValue()[0].trim(), lineCsv.getValue()[1].trim());
					logger.info(lineCsv.getValue()[0]+":"+lineCsv.getValue()[1]);
				}
			}
			logger.info("translation file for language "+this.language+" processed, "+this.translations.size()+" translation records read");
		}
	}

	public Map<String,String> getTranslations(){
		//logger.info("getting translations for language: "+this.language);
		//for (Map.Entry<String,String> c : this.translations.entrySet())
			//logger.info("line: "+c.getKey()+" : "+c.getValue());

		return this.translations;
	}

	public void addTranslations(Translator fromTranslator){
		for (Map.Entry<String,String> row : fromTranslator.getTranslations().entrySet()){
			if (! this.translations.containsKey(row.getKey())){
				logger.info("adding to "+this.language+" key "+row.getKey()+" from "+fromTranslator.language);
				this.translations.put(row.getKey(), row.getValue());
			}
		}
	}
}
