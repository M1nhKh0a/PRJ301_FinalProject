<%--
    Document : equipmentList
    TODO 59. DA LAM. Danh sach thiet bi kem bo loc va bieu mau them sua.

    Bieu mau o duoi doi che do theo thuoc tinh EQUIPMENT:
      khac null  che do sua, o ma de readonly
      bang null  che do them moi, khong co o ma vi ma sinh tu dong
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
        <title>Smart Semiconductor - Equipment</title>
    </head>
    <body>
        <jsp:include page="header.jsp" />
        <h3>Equipment</h3>

        <form action="MainController" method="GET">
            <input type="hidden" name="action" value="SearchEquipment"/>
            Name <input type="text" name="searchName" value="${requestScope.searchName}"/>
            Area
            <select name="searchArea">
                <option value="">All</option>
                <c:forEach var="a" items="${['Diffusion','Etch','Photo','CMP','Test']}">
                    <option value="${a}" ${requestScope.searchArea eq a ? 'selected' : ''}>${a}</option>
                </c:forEach>
            </select>
            Status
            <select name="searchStatus">
                <option value="">All</option>
                <c:forEach var="s" items="${['Running','Maintenance','Retired']}">
                    <option value="${s}" ${requestScope.searchStatus eq s ? 'selected' : ''}>${s}</option>
                </c:forEach>
            </select>
            <input type="submit" value="Search"/>
        </form>

        <p style="color: green">${requestScope.MSG}</p>
        <p style="color: red">${requestScope.ERROR}</p>

        <table border="1" cellpadding="4">
            <tr><th>ID</th><th>Name</th><th>Area</th><th>Model</th>
                <th>Status</th><th>Action</th></tr>
            <c:forEach var="e" items="${requestScope.LIST_EQUIPMENT}">
            <tr>
                <td>${e.equipmentID}</td><td>${e.equipmentName}</td>
                <td>${e.area}</td><td>${e.model}</td><td>${e.status}</td>
                <td>
                    <a href="MainController?action=LoadEquipment&equipmentID=${e.equipmentID}">Edit</a>
                    <c:forEach var="s" items="${['Running','Maintenance','Retired']}">
                        <c:if test="${e.status ne s}">
                            | <a href="MainController?action=ChangeEquipmentStatus&equipmentID=${e.equipmentID}&newStatus=${s}">${s}</a>
                        </c:if>
                    </c:forEach>
                </td>
            </tr>
            </c:forEach>
        </table>

        <hr/>
        <c:set var="f" value="${requestScope.EQUIPMENT}"/>
        <h4>${f == null ? 'Create a new equipment' : 'Update equipment'}</h4>
        <form action="MainController" method="POST">
            <input type="hidden" name="action" value="SaveEquipment"/>
            <c:if test="${f != null}">
                <%-- readonly chu khong disabled, vi disabled thi trinh duyet
                     khong gui gia tri di va cau lenh khong sua duoc dong nao --%>
                <p>ID <input type="text" name="equipmentID"
                             value="${f.equipmentID}" readonly=""/></p>
            </c:if>
            <p>Name <input type="text" name="equipmentName" maxlength="100"
                           value="${f.equipmentName}" required=""/></p>
            <p>Area
                <select name="area">
                    <c:forEach var="a" items="${['Diffusion','Etch','Photo','CMP','Test']}">
                        <option value="${a}" ${f.area eq a ? 'selected' : ''}>${a}</option>
                    </c:forEach>
                </select>
            </p>
            <p>Model <input type="text" name="model" value="${f.model}" required=""/></p>
            <p><input type="submit" value="Save"/>
               <a href="MainController?action=SearchEquipment">Cancel</a></p>
        </form>
    </body>
</html>
