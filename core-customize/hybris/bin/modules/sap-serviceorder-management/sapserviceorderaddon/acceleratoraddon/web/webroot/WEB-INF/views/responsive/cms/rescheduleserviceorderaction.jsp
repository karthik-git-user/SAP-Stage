<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:url var="rescheduleServiceOrderUrl" value="${fn:escapeXml(fn:replace(url, '{orderCode}', orderCode))}" scope="page"/>

<c:if test="${serviceOrderReschedulable}">
    <form:form action="${rescheduleServiceOrderUrl}" id="rescheduleserviceorderForm" modelAttribute="rescheduleserviceorderForm"
               class="cancelorderForm--ButtonWrapper">
            <button type="submit" class="btn btn-default btn-block" id="rescheduleServiceOrderButton">
                <spring:theme code="text.account.order.orderDetails.service.reschedule.button"/>
            </button>
    </form:form>
</c:if>