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

<c:url value="/my-account/subscription/${fn:escapeXml(subscriptionId)}/changePaymentDetails"
	var="changePaymentDetails" />


<div class="mini-cart js-mini-cart">

    <div class="mini-cart-body">

        <div id="savedpaymentsbody">
            <c:forEach items="${savedCards}" var="savedCard"
                    varStatus="status">
                <div class="saved-payment-entry">
                    <form:form
                        id="changePaymentDetailsForm" modelAttribute="changePaymentDetailsForm" name="changePaymentDetailsForm"
                        action="${fn:escapeXml(changePaymentDetails)}"
                        method="post">
                        <input type="hidden" name="paymentCardId" value="${fn:escapeXml(savedCard.id)}"/>
                        <input type="hidden" name="version" value="${fn:escapeXml(version)}"/>
                        <ul>
                            <strong>${fn:escapeXml(savedCard.accountHolderName)}</strong>
                            <br />
                            ${fn:escapeXml(savedCard.cardTypeData.name)}<br />
                            ${fn:escapeXml(savedCard.cardNumber)}<br />
                            <spring:theme
                                code="checkout.multi.paymentMethod.paymentDetails.expires"
                                arguments="${fn:escapeXml(savedCard.expiryMonth)},${fn:escapeXml(savedCard.expiryYear)}" />
                            <br />
                            ${fn:escapeXml(savedCard.billingAddress.line1)}<br />
                            ${fn:escapeXml(savedCard.billingAddress.town)}&nbsp; ${fn:escapeXml(savedCard.billingAddress.region.isocodeShort)}<br />
                            ${fn:escapeXml(savedCard.billingAddress.postalCode)}&nbsp; ${fn:escapeXml(savedCard.billingAddress.country.isocode)}<br />
                        </ul>
                        <button type="submit" class="btn btn-primary btn-block"
                            tabindex="${(status.count * 2) - 1}">
                            <spring:theme
                                code="checkout.multi.paymentMethod.addPaymentDetails.useThesePaymentDetails" />
                        </button>
                    </form:form>
                </div>
            </c:forEach>
        </div>
    </div>

</div>

