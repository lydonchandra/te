package com.hoskoo.porto;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
public class TestStockServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		String userid = (String)req.getSession().getAttribute("userid");
		req.getSession().setAttribute("userid", userid);
		
		String action  = (String)req.getParameter("action");
		
		String debug = "";
		String ticker = "gs";
			
		if( ticker == null || ticker.length() == 0 ) {
			return;
		}
		
        debug = "";
//        String myimage = "http://stockcharts.com/c-sc/sc?s=FAZ&p=DAILY&b=5&g=0&i=0&r=3528";
//        req.getSession().setAttribute("myimage", myimage);
//        URL	url = new URL("http://stockcharts.com/c-sc/sc?s=" + ticker + "&p=DAILY&b=5&g=0&i=0&r=3528");		
        URL	url = new URL("http://stockcharts.com/c-sc/sc?s=" + ticker + "&p=D&yr=0&mn=6&dy=0&id=p93873826034");
        //	Example url to retrieve image from stockcharts
        // this works until it doesnt!
        // URL url = new URL("http://stockcharts.com/c-sc/sc?s=FAZ&p=DAILY&b=5&g=0&i=0&r=3528");
        DataInputStream imagestream = new DataInputStream( url.openStream() );
//        DataInputStream imagestream2 = new DataInputStream( url.openStream() );
        byte[] mybytearr = new byte[MyConstants.IMAGE_SIZE];
        
        try {
        	imagestream.readFully(mybytearr);
        } catch (EOFException e) 
        {
        }
        imagestream.reset();
        
        String customerEmail = "lydonchandra@gmail.com";
        SendMail sendmail = new SendMail(customerEmail, url.openStream());
        
        resp.setContentType("image/jpeg");
        resp.getOutputStream().write(mybytearr);
        
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws IOException {

	}
}
