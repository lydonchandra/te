package com.hoskoo.porto;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;

public class SendMail{
	String recipient = "";
	String ticker = "";
	Properties properties;
	Session session;
	
	public SendMail( String recipient, InputStream imagestream ) throws ServletException {
		this.recipient = recipient;
        this.properties = new Properties();
        session = Session.getDefaultInstance(properties, null);
        javax.mail.Message msg = new MimeMessage(session);
        
        try {
			msg.setFrom( new InternetAddress("lydonchandra@gmail.com", "dons service"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			msg.setSubject("dons service daily charts");
			
			Multipart multipart = new MimeMultipart();
			
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(" url2: " , "text/html");
			multipart.addBodyPart(htmlPart);

			ByteArrayDataSource mimePartDataSource = new ByteArrayDataSource(imagestream, "image/png");
			
			MimeBodyPart attachment = new MimeBodyPart();
			attachment.setDataHandler(new DataHandler(mimePartDataSource));
			attachment.setFileName("ticker.png");
			multipart.addBodyPart(attachment);
			msg.setContent(multipart);
			
			Transport.send(msg);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServletException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
