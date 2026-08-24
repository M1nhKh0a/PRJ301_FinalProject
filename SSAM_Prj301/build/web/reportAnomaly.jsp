<%--
    Document : reportAnomaly

    TODO 57. Bieu mau bao mot bat thuong moi.

    Bieu mau gui ve MainController voi action mang gia tri ReportAnomaly,
    phuong thuc POST. Bon o nhap can co:
        equipmentID   o chon, lay tu danh sach thiet bi dang khong Retired
        severity      o chon bon muc Low, Medium, High, Critical
        description   o nhap nhieu dong, toi thieu 20 ky tu
        va mot nut gui

    Khi co loi, trang phai do lai ba gia tri vua chon tu cac ten
    oldEquipment, oldSeverity va oldDescription.

    Trang can danh sach thiet bi de do vao o chon, dat duoi ten LIST_EQUIPMENT.
    Neu servlet chua dat thi trang phai bao ro thay vi de o chon rong.
--%>
<%@page import="ssam.model.UserDto"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    UserDto acc = (UserDto) session.getAttribute("USER");
    if (acc == null || (!acc.isOperator() && !acc.isEngineer())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Smart Semiconductor - Report</title>
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <!--your code here-->

        <p style="color: green">${requestScope.MSG}</p>
        <p style="color: red">${requestScope.ERROR}</p>
    </body>
</html>
