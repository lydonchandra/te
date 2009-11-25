<%@ page session="false" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/jsp/prefix.jsp" %>
<div>
  <ul>
    <li><a href="<c:url value="/oauth/google"/>">Google</a></li>
  </ul>
</div>
<%@ include file="/WEB-INF/views/jsp/suffix.jsp" %>