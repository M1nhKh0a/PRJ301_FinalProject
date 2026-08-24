<%--
    Document : shiftList
    TODO 60. DA LAM. Lich truc ca kem bieu mau xep ca.

    Bang nay nuoi du lieu cho co che giao viec tu dong o TODO 43, nen phai
    co ca truc cho ngay hien tai thi moi thu duoc nhanh giao viec tu dong.
--%>
<%@page import="ssam.model.UserDto"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    UserDto acc = (UserDto) session.getAttribute("USER");
    if (acc == null || !acc.isAdmin()) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Smart Semiconductor - Shifts</title>
    </head>
    <body>
        <jsp:include page="header.jsp" />
        <h3>Shift schedule</h3>

        <form action="MainController" method="GET">
            <input type="hidden" name="action" value="SearchShift"/>
            From <input type="date" name="fromDate" value="${requestScope.fromDate}"/>
            To <input type="date" name="toDate" value="${requestScope.toDate}"/>
            <input type="submit" value="Search"/>
        </form>

        <p style="color: green">${requestScope.MSG}</p>
        <p style="color: red">${requestScope.ERROR}</p>

        <c:choose>
            <c:when test="${empty requestScope.LIST_SHIFT}">
                <p>No shift in this range.</p>
            </c:when>
            <c:otherwise>
                <table border="1" cellpadding="4">
                    <tr><th>ID</th><th>Date</th><th>Shift</th>
                        <th>Area</th><th>Engineer ID</th><th>Full name</th></tr>
                    <c:forEach var="s" items="${requestScope.LIST_SHIFT}">
                    <tr>
                        <td>${s.shiftID}</td><td>${s.shiftDate}</td>
                        <td>${s.shiftType}</td><td>${s.area}</td>
                        <td>${s.userID}</td><td>${s.fullName}</td>
                    </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>

        <hr/>
        <h4>Schedule a new shift</h4>
        <form action="MainController" method="POST">
            <input type="hidden" name="action" value="SaveShift"/>
            <p>Engineer
                <select name="userID">
                    <c:forEach var="e" items="${requestScope.LIST_ENGINEER}">
                        <option value="${e.userID}">${e.fullName} (${e.area})</option>
                    </c:forEach>
                </select>
            </p>
            <p>Date <input type="date" name="shiftDate" required=""/></p>
            <p>Shift
                <select name="shiftType">
                    <option value="Day">Day</option>
                    <option value="Night">Night</option>
                </select>
            </p>
            <p>Area
                <select name="area">
                    <c:forEach var="a" items="${['Diffusion','Etch','Photo','CMP','Test']}">
                        <option value="${a}">${a}</option>
                    </c:forEach>
                </select>
            </p>
            <p><input type="submit" value="Save"/></p>
        </form>
    </body>
</html>
