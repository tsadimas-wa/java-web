<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Νέος φοιτητής"/>
<%@ include file="/WEB-INF/jsp/header.jspf" %>

<h1>Νέος φοιτητής</h1>

<c:if test="${not empty errors}">
  <div class="alert alert-error">Η φόρμα έχει λάθη. Διορθώστε τα πεδία με κόκκινο.</div>
</c:if>

<form action="${ctx}/students/new" method="post">

  <div class="field">
    <label for="firstName">Όνομα</label>
    <input type="text" id="firstName" name="firstName"
           value="${fn:escapeXml(student.firstName)}" required>
    <c:if test="${not empty errors.firstName}">
      <span class="error">${errors.firstName}</span>
    </c:if>
  </div>

  <div class="field">
    <label for="lastName">Επώνυμο</label>
    <input type="text" id="lastName" name="lastName"
           value="${fn:escapeXml(student.lastName)}" required>
    <c:if test="${not empty errors.lastName}">
      <span class="error">${errors.lastName}</span>
    </c:if>
  </div>

  <div class="field">
    <label for="email">Email</label>
    <input type="email" id="email" name="email"
           value="${fn:escapeXml(student.email)}" required>
    <c:if test="${not empty errors.email}">
      <span class="error">${errors.email}</span>
    </c:if>
  </div>

  <div class="field">
    <label for="semester">Εξάμηνο</label>
    <select id="semester" name="semester">
      <c:forEach var="s" begin="1" end="10">
        <option value="${s}" ${student.semester == s ? 'selected' : ''}>${s}ο</option>
      </c:forEach>
    </select>
    <c:if test="${not empty errors.semester}">
      <span class="error">${errors.semester}</span>
    </c:if>
  </div>

  <div class="field">
    <label class="checkbox">
      <input type="checkbox" name="active" value="yes" ${student.active ? 'checked' : ''}>
      Ενεργός φοιτητής
    </label>
  </div>

  <button type="submit">Αποθήκευση</button>
</form>

<%@ include file="/WEB-INF/jsp/footer.jspf" %>
