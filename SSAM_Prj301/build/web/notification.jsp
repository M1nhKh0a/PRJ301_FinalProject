<%--
    Document : notification
    TODO 58. DA LAM. Danh sach thong bao cua nguoi dang dang nhap.
--%>
<%@page import="ssam.model.UserDto"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    UserDto acc = (UserDto) session.getAttribute("USER");
    if (acc == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Smart Semiconductor - Notifications</title>
    </head>
    <body>
        <jsp:include page="header.jsp" />
        <h3>My notifications</h3>

        <p>
            <a href="MainController?action=MyNotification">All</a> |
            <a href="MainController?action=MyNotification&filter=unread">Unread only</a>
        </p>

        <p style="color: green">${requestScope.MSG}</p>
        <p style="color: red">${requestScope.ERROR}</p>

        <c:choose>
            <c:when test="${empty requestScope.LIST_NOTIFICATION}">
                <p>No notification.</p>
            </c:when>
            <c:otherwise>
                <table border="1" cellpadding="4">
                    <tr>
                        <th>At</th><th>Message</th><th>Anomaly</th>
                        <th>Severity</th><th>Status</th><th>Action</th>
                    </tr>
                    <c:forEach var="t" items="${requestScope.LIST_NOTIFICATION}">
                    <%-- Dong chua doc to nen khac va in dam --%>
                    <tr ${t.isRead ? '' : 'bgcolor="#FFF8DC"'}>
                        <td>${t.createdAt}</td>
                        <td>
                            <c:choose>
                                <c:when test="${t.isRead}">${t.message}</c:when>
                                <c:otherwise><b>${t.message}</b></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="MainController?action=LoadAnomaly&id=${t.anomalyID}">
                                ${t.anomalyID}</a>
                        </td>
                        <td>${t.severity}</td>
                        <td>${t.anomalyStatus}</td>
                        <td>
                            <c:if test="${not t.isRead}">
                                <a href="MainController?action=MarkRead&id=${t.notificationID}">
                                    Mark read</a>
                            </c:if>
                        </td>
                    </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </body>
</html>
