<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Αρχική"/>
<%@ include file="/WEB-INF/jsp/header.jspf" %>

<h1>Καλώς ήρθατε</h1>
<p>Σήμερα είναι <strong><%= java.time.LocalDate.now() %></strong>.</p>
<p>
  <a class="button" href="${ctx}/students/new">Νέος φοιτητής</a>
  <a href="${ctx}/students">Δείτε όλους τους φοιτητές</a>
</p>

<%@ include file="/WEB-INF/jsp/footer.jspf" %>
