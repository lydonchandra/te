<%@ page session="false" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/jsp/prefix.jsp" %>
<div>
  <ul>
  <c:forEach var="m" items="${modules}">
    <li><a href="<c:url value="/oauth/google/${m.id}"/>">${m.name}</a></li>
  </c:forEach>
  </ul>
  <br/>
  <h5>Best viewed in firefox ... since the feeds are printed out in raw text/xml</h5>
</div>
<%@ include file="/WEB-INF/views/jsp/suffix.jsp" %>