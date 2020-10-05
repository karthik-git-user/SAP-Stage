<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/my-account/order/${fn:escapeXml(orderData.code)}" var="orderDetailsUrl" htmlEscape="false"/>
<common:headline url="${orderDetailsUrl}" labelKey="text.account.order.orderDetails.service.reschedule.popup.title" labelArguments="${fn:escapeXml(orderData.code)}" />
