<%--
    Document : header
    DA VIET SAN, khong phai sua.

    Menu doi theo vai tro cua nguoi dang nhap. Con so ben canh muc thong bao
    lay tu thuoc tinh UNREAD do tang dieu khien dat vao.

    Trang nao goi vao van phai TU kiem tra phien, vi lenh return trong trang
    duoc goi vao chi dung chinh trang do chu khong dung duoc trang cha.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
    <h2>${sessionScope.USER.fullName}
        <small>(${sessionScope.USER.roleID} - ${sessionScope.USER.area})</small></h2>
    <ul>
        <li><a href="MainController?action=Dashboard">Dashboard</a></li>
        <li><a href="MainController?action=SearchAnomaly">Anomaly list</a></li>

        <c:if test="${sessionScope.USER.operator or sessionScope.USER.engineer}">
            <li><a href="reportAnomaly.jsp">Report an anomaly</a></li>
        </c:if>

        <c:if test="${sessionScope.USER.admin}">
            <li><a href="MainController?action=SearchEquipment">Equipment</a></li>
            <li><a href="MainController?action=SearchShift">Shift schedule</a></li>
        </c:if>

        <li>
            <a href="MainController?action=MyNotification">Notifications</a>
            <c:if test="${requestScope.UNREAD gt 0}">
                <b>(${requestScope.UNREAD})</b>
            </c:if>
        </li>
        <li><a href="MainController?action=Logout">Logout</a></li>
    </ul>
    <hr/>
</div>
