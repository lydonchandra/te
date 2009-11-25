package com.hoskoo.porto;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
public class StockServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		String userid = (String)req.getSession().getAttribute("userid");
//		userid = "https://www.google.com/accounts/o8/id?id=AItOawle0cby30Za_-lFIRx6Y6ho5ADO5oNRJDU";
		req.getSession().setAttribute("userid", userid);
		
		String action  = (String)req.getParameter("action");
		
		String debug = "debug: " + action;
		if(  userid != null && userid.length() > 0) {
			String ticker = (String)req.getParameter("ticker");
			if( action != null && action.length() > 0 &&
				ticker != null && ticker.length() > 0 ) {
			
				if( action.equals("send2mail")) {
					//URL	url = new URL("http://stockcharts.com/c-sc/sc?s=" + ticker + "&p=DAILY&b=5&g=0&i=0&r=3528");		
					URL	url = new URL("http://stockcharts.com/c-sc/sc?s=" + ticker + "&p=D&yr=0&mn=6&dy=0&id=p93873826034");
//					http://stockcharts.com/h-sc/ui?s=AMD&p=D&yr=0&mn=6&dy=0&id=p93873826034
			        //	Example url to retrieve image from stockcharts
			        // this works until it doesnt!
			        // URL url = new URL("http://stockcharts.com/c-sc/sc?s=FAZ&p=DAILY&b=5&g=0&i=0&r=3528");
			        DataInputStream imagestream = new DataInputStream( url.openStream() );
			        // to do retrieve size of the image dynamically ?
			        
			        // the image from stockcharts.com should be much less than 100KB, average is 30KB
			        byte[] mybytearr = new byte[MyConstants.IMAGE_SIZE];
			        
			        try {
			        	imagestream.readFully(mybytearr);
			        } catch (EOFException e) 
			        {
			        }
			        
			        String customerEmail = "lydonchandra@gmail.com";
			        SendMail sendmail = new SendMail(customerEmail, url.openStream());
			        
			        resp.setContentType("image/jpeg");
			        resp.getOutputStream().write(mybytearr);
		            
				} else {
					debug += " else 3";
					req.getSession().setAttribute("debug", debug);
					resp.sendRedirect("/stock.jsp");
				}
			} else if( action != null && action.equals("delete")){
				debug = debug + " id = " + req.getParameter("id");
				PersistenceManager pm = PMF.get().getPersistenceManager();
				Query query = pm.newQuery("SELECT FROM " + Portofolio.class.getName() + " WHERE id == " + req.getParameter("id") );
				List<Portofolio> objs = (List<Portofolio>)query.execute();
				for( Portofolio obj : objs ) {
					pm.deletePersistent(obj);
				}
				pm.close();
				
				req.getSession().setAttribute("debug", debug);
				req.getSession().removeAttribute("portofolio");
				resp.sendRedirect("/stock.jsp");
			} else if( action != null  &&  action.equals("edit") ) {
				debug = "editing...";
				PersistenceManager pm = PMF.get().getPersistenceManager();
				Query query = pm.newQuery("SELECT FROM " + Portofolio.class.getName() + " WHERE id == " + req.getParameter("id") );
				List<Portofolio> objs = (List<Portofolio>)query.execute();
				Portofolio portof = objs.get(0);
				req.getSession().setAttribute("id", req.getParameter("id")); // adding comment
				
				req.getSession().setAttribute("debug", "sdddaaaa");
				
				resp.sendRedirect("/stock.jsp");
			} else {
				debug = "else2...";
				req.getSession().setAttribute("debug", debug);
				resp.sendRedirect("/stock.jsp");
			}
		} else {
			resp.sendRedirect("/hybrid");
		}
		
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws IOException {

		String userid = (String)req.getSession().getAttribute("userid");
		if(  userid != null && userid.length() > 0) { 
			String ticker = req.getParameter("ticker");
			
			float buyprice = 0;
			if( req.getParameter("buyprice") != null && 
				req.getParameter("buyprice").length() > 0 ) {
				buyprice = Float.parseFloat(req.getParameter("buyprice")); 
			}
			
			float high = 0;
			if( req.getParameter("high") != null && 
				req.getParameter("high").length() > 0 ) {
					high = Float.parseFloat(req.getParameter("high")); 
			}
			
			float low = 0;
			if( req.getParameter("low") != null && 
				req.getParameter("low").length() > 0 ) {
				low = Float.parseFloat(req.getParameter("low")); 
			}
			
			long quant = 0;
			if( req.getParameter("quantity").toString().length() > 0 ) {
				quant = Long.parseLong( req.getParameter("quantity").toString() );
			}
			
			long id = 0;
			if( req.getParameter("id").toString().length() > 0 ) {
				id = Long.parseLong( req.getParameter("id").toString() );
			}
			
			String comment = "";
			if( req.getParameter("comment").toString().length() > 0 ) {
				comment = req.getParameter("comment");
			}
			
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			
			if( 0 == id ) {
				Portofolio portofolio = new Portofolio(ticker, buyprice, quant, high, low, comment, userid);
				pm.makePersistent(portofolio);
				
			} else {
				Query query = pm.newQuery("SELECT FROM " + Portofolio.class.getName() + " WHERE id == " + id );
				List<Portofolio> objs = (List<Portofolio>)query.execute();
				Portofolio portof = (Portofolio)objs.get(0);
				portof.setBuyprice(buyprice);
				portof.setHigh(high);
				portof.setLow(low);
				portof.setQuantity(quant);
				portof.setComment(comment);
				portof.setUserid(userid);
				pm.makePersistent(portof);
			}
			
			pm.close();
		}
		req.getSession().setAttribute("userid", userid);
		req.getSession().removeAttribute("portofolio");
		resp.sendRedirect("stock.jsp");
	}
}
