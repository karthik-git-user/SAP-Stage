<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="productaddon" tagdir="/WEB-INF/tags/addons/sapsubscriptionaddon/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>


<c:url value="/paymentcard/changePaymentDetailsPopup?s=${subscriptionData.id}&v=${subscriptionData.version}" var="changePaymentPopupUrl"/>

<spring:htmlEscape defaultHtmlEscape="true" />
<button id="changePaymentDetails" type="submit" class="btn btn-primary btn-block" data-change-payment-details-popup-url="${changePaymentPopupUrl}">
    <spring:theme code="text.account.subscription.changePaymentDetails" text="Change Payment Details"/>
</button>

