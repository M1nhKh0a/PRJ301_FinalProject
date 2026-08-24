<%--
    Document : anomalyList
    TODO 55. DA LAM. Danh sach ban ghi bat thuong kem bo loc.

    Cot thao tac doi theo VAI TRO va theo TRANG THAI cua tung dong, day la
    phan the hien ro nhat viec phan quyen co duoc lam can than hay khong.
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
        <title>Smart Semiconductor - Anomalies</title>
    </head>
    <body>
        <jsp:include page="header.jsp" />
        <h3>Anomaly list</h3>

        <form action="MainController" method="GET">
            <input type="hidden" name="action" value="SearchAnomaly"/>
            Keyword <input type="text" name="keyword" value="${requestScope.keyword}"/>

            Area
            <select name="area">
                <option value="">All</option>
                <c:forEach var="a" items="${['Diffusion','Etch','Photo','CMP','Test']}">
                    <option value="${a}" ${requestScope.area eq a ? 'selected' : ''}>${a}</option>
                </c:forEach>
            </select>

            Severity
            <select name="severity">
                <option value="">All</option>
                <c:forEach var="s" items="${['Critical','High','Medium','Low']}">
                    <option value="${s}" ${requestScope.severity eq s ? 'selected' : ''}>${s}</option>
                </c:forEach>
            </select>

            Status
            <select name="status">
                <option value="">All</option>
                <c:forEach var="t" items="${['New','Assigned','Resolved','Closed','Rejected']}">
                    <option value="${t}" ${requestScope.status eq t ? 'selected' : ''}>${t}</option>
                </c:forEach>
            </select>

            <%-- O danh dau chi hien voi ky su --%>
            <c:if test="${sessionScope.USER.engineer}">
                <input type="checkbox" name="onlyMine" value="1"
                       ${not empty requestScope.onlyMine ? 'checked' : ''}/> Only mine
            </c:if>

            <input type="submit" value="Search"/>
        </form>

        <p style="color: green">${requestScope.MSG}</p>
        <p style="color: red">${requestScope.ERROR}</p>

        <c:if test="${empty requestScope.LIST_ANOMALY}">
            <p>No anomaly found.</p>
        </c:if>

        <c:if test="${not empty requestScope.LIST_ANOMALY}">
        <table border="1" cellpadding="4">
            <tr>
                <th>ID</th><th>Equipment</th><th>Area</th><th>Severity</th>
                <th>Description</th><th>Reported by</th><th>Assigned to</th>
                <th>Status</th><th>Reported at</th><th>Action</th>
            </tr>
            <c:forEach var="n" items="${requestScope.LIST_ANOMALY}">
            <tr ${n.severity eq 'Critical' ? 'bgcolor="#FFECEC"' : ''}>
                <td>${n.anomalyID}</td>
                <td>${n.equipmentName}</td>
                <td>${n.area}</td>
                <td><b>${n.severity}</b></td>
                <td>${n.description}</td>
                <td>${n.reporterName}</td>
                <td>
                    <%-- Cot cho phep de trong, khong duoc in ra chu null --%>
                    <c:choose>
                        <c:when test="${empty n.assigneeName}"><i>Not assigned</i></c:when>
                        <c:otherwise>${n.assigneeName}</c:otherwise>
                    </c:choose>
                </td>
                <td>${n.status}</td>
                <td>${n.reportedAt}</td>
                <td>
                    <a href="MainController?action=LoadAnomaly&id=${n.anomalyID}">View</a>
                </td>
            </tr>
            </c:forEach>
        </table>
        </c:if>
    </body>
</html>
