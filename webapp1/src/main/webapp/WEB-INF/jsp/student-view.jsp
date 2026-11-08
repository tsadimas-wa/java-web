<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Στοιχεία φοιτητή"/>
<%@ include file="/WEB-INF/jsp/header.jspf" %>

<c:if test="${param.saved == '1'}">
  <div class="alert alert-success">Ο φοιτητής αποθηκεύτηκε.</div>
</c:if>

<h1><c:out value="${student.firstName} ${student.lastName}"/></h1>

<dl class="details">
  <dt>Α/Α</dt>      <dd>${student.id}</dd>
  <dt>Όνομα</dt>    <dd><c:out value="${student.firstName}"/></dd>
  <dt>Επώνυμο</dt>  <dd><c:out value="${student.lastName}"/></dd>
  <dt>Email</dt>    <dd><c:out value="${student.email}"/></dd>
  <dt>Εξάμηνο</dt>  <dd>${student.semester}ο</dd>
  <dt>Ενεργός</dt>  <dd>${student.active ? 'Ναι' : 'Όχι'}</dd>
</dl>

<p>
  <a class="button" href="${ctx}/students/new">Νέος φοιτητής</a>
  <a href="${ctx}/students">Όλοι οι φοιτητές</a>
</p>

<%@ include file="/WEB-INF/jsp/footer.jspf" %>
