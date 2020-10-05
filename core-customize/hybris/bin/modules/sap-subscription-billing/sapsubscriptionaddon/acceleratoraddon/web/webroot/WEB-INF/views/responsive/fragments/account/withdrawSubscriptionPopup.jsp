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

<c:url value="/my-account/subscription/${subscriptionId}/withdraw"
	var="withdrawSubscriptionUrl" />

<form:form id="subscriptionWithdrawalForm"
	name="subscriptionWithdrawalForm"
	action="${fn:escapeXml(withdrawSubscriptionUrl)}" method="post"
	modelAttribute="subscriptionWithdrawalForm">

	<div class="mini-cart js-mini-cart">

		<div class="mini-cart-body">
			<input type="hidden" name="version" value="${fn:escapeXml(version)}" />
			<button id="withdrawsub" type="submit"
				class="btn btn-default btn-block">
				<spring:theme code="text.account.withdrawsubscription.proceed" />
			</button>

			<a href=""
				class="btn btn-default btn-block js-mini-cart-close-button"> <spring:theme
					code="text.account.withdrawsubscription.cancel" />
			</a>
		</div>

	</div>


</form:form>
