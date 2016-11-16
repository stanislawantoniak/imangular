package pl.essay.toolbox;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import pl.essay.angular.security.UserServiceImpl;

public class EmailSender {
	
	protected static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private JavaMailSender javaMailSender;
	
		
	private InternetAddress from;

	public EmailSender(String from){
		try {
			this.from = new InternetAddress(from);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			logger.error("Error creating email sender bean - incorrect from address.",e);
		}
	}
	
	public boolean sendEmail(
			String to,
			String subject,
			String body
			){
		
		//System.out.println("EmailSender starting::"+to);
		
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom( this.from );
			message.setReplyTo( new InternetAddress[]{ this.from });
			
			message.addRecipient(RecipientType.TO, new InternetAddress(to) );
			message.setContent(body, "text/html; charset=utf-8");
			message.setSubject(subject);
			
		} catch (MessagingException e) {
			logger.error("Error creating email - incorrect data for email.",e);
			return false;
		} 

		javaMailSender.send( message );
		//System.out.println("mail sent::"+to);

		return true;
	}

}
