package pl.essay.toolbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class EmailMaker {
	
	protected static final Logger logger = LoggerFactory.getLogger(EmailMaker.class);

	private String template;
	
	//@Value("${eliczile.domain}")
	private String domain;

	//@Value("${eliczile.appname}")
	private String appname;
	
	public EmailMaker(String file){
		/*
		InputStream is = getClass().getClassLoader().getResourceAsStream(file);
		try {
			this.template = IOUtils.toString(is,"UTF-8");
		} catch (IOException e) {
			logger.error("Problem getting email template form classpath file "+file, e);
		}
		*/
	}
	
	public String getMail(Map<String,String> placeholderMap){
		
		String emailBody = this.template;
		
		for(Map.Entry<String,String> placeHolder: placeholderMap.entrySet())
			emailBody = emailBody.replace(placeHolder.getKey(), placeHolder.getValue());
		
		emailBody = emailBody.replace("@domain@",this.domain);
		emailBody = emailBody.replace("@domainname@", this.appname );
				
		return emailBody;
	}
	
}
