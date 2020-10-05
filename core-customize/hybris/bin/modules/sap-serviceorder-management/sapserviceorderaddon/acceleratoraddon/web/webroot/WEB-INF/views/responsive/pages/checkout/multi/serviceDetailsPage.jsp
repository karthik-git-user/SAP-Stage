<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/responsive/nav/breadcrumb" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
 
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
 
<div class="row">
    <div class="col-sm-6">
        <div class="checkout-headline">
            <span class="glyphicon glyphicon-lock"></span>
            <spring:theme code="checkout.multi.secure.checkout" />
        </div>
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
				<ycommerce:testId code="checkoutStepFour">
							<spring:url var="selectServiceDetailsMethodUrl" value="{contextPath}/checkout/multi/service-details/choose" htmlEscape="false" >
								<spring:param name="contextPath" value="${request.contextPath}" />
							</spring:url>
							 
							 <form:form id="selectServiceDetailsForm" modelAttribute="serviceDetailsForm" action="${fn:escapeXml(selectServiceDetailsMethodUrl)}" method="post">
								 <div class="checkout-indent checkout-shipping">
								<div class="column scheduleform  scheduleform_left">
								</div>
								<c:choose>
								<c:when test="${containsService}">
								<div class="hidden" data-scheduleleaddays="${scheduleLeadDays}"></div>
						        <div class="headline"><p><spring:theme code="checkout.multi.serviceDetails.headline" /></p></div>
								<formElement:formInputBox idKey="scheduleDate" labelKey="checkout.multi.scheduleservicedate.label" path="serviceDate" inputCSS="text form-control" />
								
								<div class="form-group">
									<label class="control-label " for="serviceTime"><spring:theme code="checkout.multi.scheduleservicetime.label" /></label>
									<select id="serviceTime"  name="serviceTime" class="form-control">
										<c:forEach items="${serviceScheduleTimeList}" var="time">
										<c:choose>
											<c:when test="${time eq defaultServiceTime}">
												<option selected="selected" value="${time}">${time}</option>
											</c:when>
											<c:otherwise>
												<option value="${time}">${time}</option>
											</c:otherwise>
										</c:choose>
										</c:forEach>
									</select>
									
								</div>
								</c:when>
								<c:otherwise>
								<br>
								<p><spring:theme code="checkout.multi.serviceDetails.noServiceDetailsRequired" /></p>
								</c:otherwise>
								</c:choose>
     						      </div>
     						   
     						   <button id="chooseServiceDetails_continue_button" type="submit" class="btn btn-primary btn-block checkout-next">
									<spring:theme code="checkout.multi.servicedetails.continue"/>
							   </button>
							</form:form>
					</ycommerce:testId>
			</jsp:body>
		</multi-checkout:checkoutSteps>
	</div>
           
   <div class="col-sm-6 hidden-xs">
		<multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
    </div>

    <div class="col-sm-12 col-lg-12">
        <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </div>
</div>
 </template:page> 