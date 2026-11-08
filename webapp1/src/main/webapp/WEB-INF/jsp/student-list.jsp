<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Φοιτητές"/>
<%@ include file="/WEB-INF/jsp/header.jspf" %>

<h1>Φοιτητές</h1>

<c:choose>
  <c:when test="${empty students}">
    <p>Δεν υπάρχουν φοιτητές ακόμα. <a href="${ctx}/students/new">Προσθέστε έναν.</a></p>
  </c:when>
  <c:otherwise>
    <table>
      <thead>
        <tr><th>Α/Α</th><th>Ονοματεπώνυμο</th><th>Email</th><th>Εξάμηνο</th></tr>
      </thead>
      <tbody>
        <c:forEach var="st" items="${students}">
          <tr>
            <td>${st.id}</td>
            <td><a href="${ctx}/students/view?id=${st.id}"><c:out value="${st.firstName} ${st.lastName}"/></a></td>
            <td><c:out value="${st.email}"/></td>
            <td>${st.semester}ο</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/footer.jspf" %>
