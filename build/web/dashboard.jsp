<%--
    Document : dashboard

    TODO 54. Man hinh tong quan.

    Trang doc cac ten thuoc tinh sau, tang dieu khien phai dat dung ten:
        UNREAD          so thong bao chua doc
        LIST_SEVERITY   thong ke so ban ghi theo tung muc do nghiem trong
        COUNT_NEW, COUNT_ASSIGNED, COUNT_RESOLVED   so ban ghi theo trang thai
        LIST_MYTASK     danh sach viec cua ky su, chi co khi vai tro la EN
        LIST_PENDING    danh sach cho duyet, chi co khi vai tro la QA

    Yeu cau:
        Hien ba o so lieu cho ba trang thai New, Assigned va Resolved.
        Hien bang thong ke theo muc do nghiem trong, moi dong mot muc.
        Ky su thay them bang viec dang duoc giao cho minh.
        QA thay them bang ban ghi dang cho duyet.
        Muc Critical nen lam noi bat bang mau do de nguoi truc nhin thay ngay.
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
        <title>Smart Semiconductor - Dashboard</title>
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <!--your code here-->

        <p style="color: green">${requestScope.MSG}</p>
        <p style="color: red">${requestScope.ERROR}</p>
    </body>
</html>
