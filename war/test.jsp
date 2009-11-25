<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="javax.jdo.Query" %>
<%@ page import="com.hoskoo.porto.*" %>

<html>
<head></head>
<body>
	<% if(request.getSession().getAttribute("myimage") != null ) {
		%>
		image = <img src="<%=request.getSession().getAttribute("myimage")%>"></img>
		<%
	} %>
	<%=request.getSession().getAttribute("debug") %>	
			
</body>
</html>			
		
		