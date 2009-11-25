<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="javax.jdo.Query" %>
<%@ page import="com.hoskoo.porto.*" %>

<html>
<head></head>
<body>
	<%=request.getSession().getAttribute("debug")%>
	<form method=post action="/stock" >
		<% 
			//Object obj = request.getSession().getAttribute("portofolio");
			String id = (String)request.getSession().getAttribute("id");
			String userid = "";
				String ticker = "";
				String buy = "";
				String quantity = "" ;
				String high = "" ;
				String low = "";
				String close = "";
				String comment = "";
				PersistenceManager pm = null;
				Query query = null;
			if( id!= null && id.length() > 0 ) {
				pm = PMF.get().getPersistenceManager();
				query = pm.newQuery("SELECT FROM " + Portofolio.class.getName() + " WHERE id == " + id );
				List<Portofolio> objs = (List<Portofolio>)query.execute();
				Object obj = objs.get(0);
				Portofolio portof = null;
				
				//String id = "";
				
				if( obj != null ) {
				   portof = (Portofolio)obj;
				   ticker = portof.getTicker();
				   id = portof.getId() != null ? portof.getId().toString() : "" ;
				   userid = portof.getUserid() != null ? portof.getUserid().toString() : "";
				   buy = portof.getBuyprice() != null ? portof.getBuyprice().toString() : "" ;
				   quantity = portof.getQuantity() != null ? portof.getQuantity().toString() : "";
				   high = portof.getHigh() != null ? portof.getHigh().toString() : "" ;
				   low = portof.getLow() != null ? portof.getLow().toString() : "" ;
				   close = portof.getClose() != null ? portof.getClose().toString() : "";
				   comment = portof.getComment() != null ? portof.getComment().toString() : "";
				}  
			}
			
		%>
			
		<table>
			<input type=hidden name=id value=<%=id%> ></input>
			<input type=hidden name=userid value=<%=userid%> ></input>
			<tr>
				<td>Ticker:</td>  
				<td><input type=text name=ticker value=<%=ticker%>></td>
			</tr>
			<tr>
				<td>Buy Price:</td>  
				<td><input type=text name=buyprice value=<%=buy%> ></td>
			</tr>
			<tr>
				<td>Quantity</td>  
				<td><input type=text name=quantity value=<%=quantity%> ></td>
			</tr>
			<tr>
				<td>Day High</td>  
				<td><input type=text name=high value=<%=high%> ></td>
			</tr>
			<tr>
				<td>Day Low</td>  
				<td><input type=text name=low value=<%=low%> ></td>
			</tr>
			<tr>
				<td>Day Close</td>  
				<td><input type=text name=close value=<%=close%> ></td>
			</tr>
			<tr>
				<td>Comment</td>  
				<td><textarea name=comment><%=comment%></textarea></td>
			</tr>
			<tr>
				<td><input type=Submit name=submit></td>
			</tr>
		</table>
	</form>


		<% 
			List<Portofolio> portofolios = new ArrayList();
			
			pm = PMF.get().getPersistenceManager();
			query = null;
			String useridrequest = (String)request.getSession().getAttribute("userid");
			// String useridrequest = (String)request.getParameter("userid");
			
			if( useridrequest.length() > 0 ) {
				query = pm.newQuery("SELECT FROM " + Portofolio.class.getName() + " WHERE userid == '" + useridrequest + "'");
			//else
			//	query = pm.newQuery("SELECT FROM " + Portofolio.class.getName() );
			
				
				portofolios = (List<Portofolio>)query.execute();
			}
			
		%>
		Userid: <%=useridrequest%>
		
		<%
			if(portofolios.isEmpty()) {
		%>
			<div>
				Empty
			</div>	
		<%
			} else {
		%>
			<table>
			<tr><th>Ticker</th>
				<th>Price</th>
				<th>Quantity</th>
				<th>High</th>
				<th>Low</th>
				<th>Close</th>
				<th>Grade</th>
				<th>Comment</th>
				<th>Chart</th>
				<th colspan=2>Action</th>
				
			</tr>
		<%
				for( Portofolio portofolio : portofolios ) {
		%>
				<tr>
					<td>
						<%= portofolio.getTicker() %>
					</td>
					<td>
						<%= portofolio.getBuyprice() %>
					</td>
					<td>
						<%= portofolio.getQuantity() %>
					</td>
					<td>
						<%= portofolio.getHigh() %>
					</td>
					<td>
						<%= portofolio.getLow() %>
					</td>
					<td>
						<%= portofolio.getClose() %>
					</td>
					<td>
						<%= portofolio.getGrade() %>
					</td>
					<td>
						<%= portofolio.getComment() %>
					</td>
					<td>
						<img src="/image?ticker=<%=portofolio.getTicker() %>"></img>
					</td>
					<td><a href="/stock?action=delete&id=<%=portofolio.getId()%>" > Delete </a>
						<a href="/stock?action=edit&id=<%=portofolio.getId()%>" > Edit </a>
						<a href="/stock?action=grade&id=<%=portofolio.getId()%>" > Re-grade </a>
						<a href="/stock?action=send2mail&id=<%=portofolio.getId()%>&ticker=<%=portofolio.getTicker()%>" > Send-to-mail</a>
					</td>
				</tr>
		<%
				} // end foreach
			
			} // end if
		%>
			</table>
			
</body>
</html>			
		
		