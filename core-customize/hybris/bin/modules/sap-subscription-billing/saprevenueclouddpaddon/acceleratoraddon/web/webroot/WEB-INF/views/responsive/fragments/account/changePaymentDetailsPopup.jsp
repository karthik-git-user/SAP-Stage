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

<c:url value="/paymentcard/savedCards?s=${subscriptionId}&v=${version}"
    var="savedCardsUrl" />
<c:url value="/paymentcard/registerNewCard"
    var="newCardUrl" />
<c:url value="/paymentcard/poll?s=${subscriptionId}&v=${version}"
    var="checkCardUrl" />
<c:url value="/my-account/subscription/${fn:escapeXml(subscriptionId)}/changePaymentDetails?invoice=true"
	var="useInvoiceUrl" />

<div>

	<div class="mini-cart js-mini-cart">

		<div class="mini-cart-body">

			<div class="mini-cart-totals">
                <c:if test="${(not empty paymentMethod) && ( (paymentMethod ne 'Payment Card') && (paymentMethod ne 'External Card') )}">
                    <div class="key">
                		<spring:theme code="text.subscription.changePaymentDetails.paymentMethod"/>
                    </div>
                    <div class="value">
                        <c:if test="${paymentMethod eq 'Invoice'}">
                            <spring:theme code="text.subscription.changePaymentDetails.paymentType.Invoice"/>
                        </c:if>
                        <c:if test="${paymentMethod eq 'External Card'}">
                            <spring:theme code="text.subscription.changePaymentDetails.paymentType.ExternalCard"/>
                        </c:if>
                        <c:if test="${paymentMethod eq 'Payment Card'}">
                            <spring:theme code="text.subscription.changePaymentDetails.paymentType.PaymentCard"/>
                        </c:if>
                    </div>
                </c:if>
			    <c:if test="${(paymentMethod eq 'Payment Card') || (paymentMethod eq 'External Card')}">
                    <div>
                        <ul>
                            <strong>${fn:escapeXml(currentPaymentInfo.accountHolderName)}</strong>
                            <br />
                            ${fn:escapeXml(currentPaymentInfo.cardTypeData.name)}<br />
                            ${fn:escapeXml(currentPaymentInfo.cardNumber)}<br />
                            <spring:theme
                                code="checkout.multi.paymentMethod.paymentDetails.expires"
                                arguments="${fn:escapeXml(currentPaymentInfo.expiryMonth)},${fn:escapeXml(currentPaymentInfo.expiryYear)}" />
                            <br />
                            ${fn:escapeXml(currentPaymentInfo.billingAddress.line1)}<br />
                            ${fn:escapeXml(currentPaymentInfo.billingAddress.town)}&nbsp; ${fn:escapeXml(savedCard.billingAddress.region.isocodeShort)}<br />
                            ${fn:escapeXml(currentPaymentInfo.billingAddress.postalCode)}&nbsp; ${fn:escapeXml(savedCard.billingAddress.country.isocode)}<br />
                        </ul>
                    </div>
                </c:if>
			</div>

            <c:if test="${paymentMethod ne 'Invoice'}">
                <form:form id="changePaymentDetailsForm" modelAttribute="changePaymentDetailsForm" name="changePaymentDetailsForm" action="${useInvoiceUrl}" method="post">
                        <input type="hidden" name="version" value="${fn:escapeXml(version)}"/>
                </form:form>
                <button id="btnUseInvoice" type="submit" class="btn btn-primary btn-block" form="changePaymentDetailsForm">
                    <spring:theme code="text.account.subscription.useInvoice" text="Use Invoice"/>
                </button>
             </c:if>

			<button id="btnSavedCards" class="btn btn-primary btn-block" data-saved-cards-url="${fn:escapeXml(savedCardsUrl)}">
				<spring:theme code="text.account.subscription.savedCards" text="Saved Cards"/>
			</button>
			
			<button id="btnAddNewCard" class="btn btn-primary btn-block" data-new-card-url="${newCardUrl}" data-check-card-url="${checkCardUrl}">
				<spring:theme code="text.account.subscription.addNewCard" text="Add New Card"/>
			</button>
		</div>

	</div>

</div>
