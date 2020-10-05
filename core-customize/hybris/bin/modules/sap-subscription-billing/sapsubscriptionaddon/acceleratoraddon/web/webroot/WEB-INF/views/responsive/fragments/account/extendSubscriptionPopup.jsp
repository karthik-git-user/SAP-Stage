<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:url value="/my-account/subscription/${subscriptionCode}/extend"
	var="extendSubscriptionUrl" />

<form:form id="subscriptionExtensionForm"
	name="subscriptionExtensionForm"
	action="${fn:escapeXml(extendSubscriptionUrl)}" method="post"
	modelAttribute="subscriptionExtensionForm">

	<div class="mini-cart js-mini-cart">

		<div class="mini-cart-body">

			<div class="mini-cart-totals">
				<div class="key">
					<spring:theme code="text.account.extendsubscription.effDate"/>
					
				</div>
				<div class="value">
                    <c:if test="${not empty extensionData.validUntil}">
                        <fmt:formatDate value="${extensionData.validUntil}" dateStyle="long" timeStyle="short" type="date"/>
                    </c:if>
                    <c:if test="${empty extensionData.validUntil}">
                        <spring:theme code="text.account.extendsubscription.unlimited"/>
                    </c:if>
				</div>
			</div>

			<input type="hidden" name="version" value="${subscriptionExtensionForm.version}" />
			<input type="hidden" name="id" value="${subscriptionCode}"/>
			<input type="hidden" name="extensionPeriod" value="${subscriptionExtensionForm.extensionPeriod}" />
			<input type="hidden" name="validTilldate" value="${subscriptionExtensionForm.validTilldate}" />
			<input type="hidden" name="ratePlanId" value="${subscriptionExtensionForm.ratePlanId}" />
			<input type="hidden" name="unlimited" value="${subscriptionExtensionForm.unlimited}" />

			<button id="extendsub" type="submit"
				class="btn btn-primary btn-block">
				<spring:theme code="text.account.extendsubscription.proceed" />
			</button>
			
			<a href="" class="btn btn-default btn-block js-mini-cart-close-button">
				<spring:theme code="text.account.extendsubscription.cancel"/>
			</a>
		</div>

	</div>


</form:form>
