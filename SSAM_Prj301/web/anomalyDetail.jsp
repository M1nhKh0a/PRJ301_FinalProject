<%--
    Document : anomalyDetail
    TODO 56. DA LAM. Trang chi tiet, cung la noi thuc hien bon thao tac.

    CHU Y: bon bieu mau thao tac KHONG duoc long vao nhau. Moi bieu mau mo va
    dong doc lap, dat trong mot o cua bang hoac ngoai bang, khong bao quanh
    ca dong.

    Bon khoi thao tac deu goi ve MainController, va bon servlet tuong ung la
    bai tap cua cac em: AssignAnomaly, ResolveAnomaly, ReviewAnomaly,
    InspectAnomaly.
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
        <title>Smart Semiconductor - Anomaly detail</title>
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <c:set var="n" value="${requestScope.ANOMALY}"/>
        <c:if test="${n == null}">
            <p style="color: red">Anomaly not found.</p>
        </c:if>

        <c:if test="${n != null}">
        <h3>Anomaly ${n.anomalyID}</h3>
        <table border="1" cellpadding="4">
            <tr><td>Equipment</td><td>${n.equipmentID} - ${n.equipmentName}</td></tr>
            <tr><td>Area</td><td>${n.area}</td></tr>
            <tr><td>Severity</td><td><b>${n.severity}</b></td></tr>
            <tr><td>Description</td><td>${n.description}</td></tr>
            <tr><td>Reported by</td><td>${n.reporterName} at ${n.reportedAt}</td></tr>
            <tr><td>Assigned to</td>
                <td>
                    <c:choose>
                        <c:when test="${empty n.assigneeName}"><i>Not assigned</i></c:when>
                        <c:otherwise>${n.assigneeName}</c:otherwise>
                    </c:choose>
                </td></tr>
            <tr><td>Status</td><td>${n.status}</td></tr>
            <tr><td>Root cause</td>
                <td>
                    <c:choose>
                        <c:when test="${empty n.rootCause}"><i>Not yet</i></c:when>
                        <c:otherwise>${n.rootCause}</c:otherwise>
                    </c:choose>
                </td></tr>
            <tr><td>Resolved at</td>
                <td>
                    <c:choose>
                        <c:when test="${empty n.resolvedAt}"><i>Not yet</i></c:when>
                        <c:otherwise>${n.resolvedAt}</c:otherwise>
                    </c:choose>
                </td></tr>
            <tr><td>QC inspections</td>
                <td>${n.inspectionCount} total, ${n.passCount} passed</td></tr>
        </table>

        <p style="color: green">${requestScope.MSG}</p>
        <p style="color: red">${requestScope.ERROR}</p>

        <hr/>

        <%-- Khoi 1: giao viec. AD hoac EN, trang thai chua phai Resolved hay Closed --%>
        <c:if test="${(sessionScope.USER.admin or sessionScope.USER.engineer)
                      and (n.status eq 'New' or n.status eq 'Assigned' or n.status eq 'Rejected')}">
            <h4>Assign to an engineer</h4>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="AssignAnomaly"/>
                <input type="hidden" name="id" value="${n.anomalyID}"/>
                <select name="engineerID">
                    <c:forEach var="e" items="${requestScope.LIST_ENGINEER}">
                        <option value="${e.userID}">${e.fullName} (${e.area})</option>
                    </c:forEach>
                </select>
                <input type="submit" value="Assign"/>
            </form>
        </c:if>

        <%-- Khoi 2: xu ly. EN va ban ghi duoc giao cho chinh nguoi dang nhap --%>
        <c:if test="${sessionScope.USER.engineer
                      and n.assignedTo eq sessionScope.USER.userID
                      and (n.status eq 'Assigned' or n.status eq 'Rejected')}">
            <h4>Resolve</h4>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="ResolveAnomaly"/>
                <input type="hidden" name="id" value="${n.anomalyID}"/>
                <textarea name="rootCause" rows="3" cols="70"
                          placeholder="Root cause, at least 30 characters">${n.rootCause}</textarea>
                <br/><input type="submit" value="Mark as resolved"/>
            </form>
        </c:if>

        <%-- Khoi 3: duyet. QA va trang thai la Resolved --%>
        <c:if test="${sessionScope.USER.qa and n.status eq 'Resolved'}">
            <h4>QA review</h4>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="ReviewAnomaly"/>
                <input type="hidden" name="id" value="${n.anomalyID}"/>
                <select name="decision">
                    <option value="Approve">Approve</option>
                    <option value="Reject">Reject</option>
                </select>
                <input type="text" name="comment" size="50" placeholder="Comment"/>
                <input type="submit" value="Submit review"/>
            </form>
        </c:if>

        <%-- Khoi 4: kiem tra. QC va trang thai la Resolved hoac Closed --%>
        <c:if test="${sessionScope.USER.qc
                      and (n.status eq 'Resolved' or n.status eq 'Closed')}">
            <h4>QC inspection</h4>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="InspectAnomaly"/>
                <input type="hidden" name="id" value="${n.anomalyID}"/>
                Lot <input type="text" name="lotID" size="20"/>
                Result
                <select name="result">
                    <option value="Pass">Pass</option>
                    <option value="Fail">Fail</option>
                    <option value="Rework">Rework</option>
                </select>
                <input type="text" name="note" size="35" placeholder="Note"/>
                <input type="submit" value="Save inspection"/>
            </form>
        </c:if>

        <hr/>
        <h4>Review history</h4>
        <c:choose>
            <c:when test="${empty requestScope.LIST_REVIEW}">
                <p>No review yet.</p>
            </c:when>
            <c:otherwise>
                <table border="1" cellpadding="4">
                    <tr><th>ID</th><th>Reviewer</th><th>Decision</th>
                        <th>Comment</th><th>At</th></tr>
                    <c:forEach var="r" items="${requestScope.LIST_REVIEW}">
                    <tr>
                        <td>${r.reviewID}</td><td>${r.reviewerName}</td>
                        <td>${r.decision}</td><td>${r.comment}</td><td>${r.reviewedAt}</td>
                    </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>

        <h4>QC inspections</h4>
        <c:choose>
            <c:when test="${empty requestScope.LIST_INSPECT}">
                <p>No inspection yet.</p>
            </c:when>
            <c:otherwise>
                <table border="1" cellpadding="4">
                    <tr><th>ID</th><th>Inspector</th><th>Lot</th>
                        <th>Result</th><th>Note</th><th>At</th></tr>
                    <c:forEach var="i" items="${requestScope.LIST_INSPECT}">
                    <tr>
                        <td>${i.inspectionID}</td><td>${i.inspectorName}</td>
                        <td>${i.lotID}</td><td>${i.result}</td>
                        <td>${i.note}</td><td>${i.inspectedAt}</td>
                    </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>

        <p><a href="MainController?action=SearchAnomaly">Back to list</a></p>
        </c:if>
    </body>
</html>
