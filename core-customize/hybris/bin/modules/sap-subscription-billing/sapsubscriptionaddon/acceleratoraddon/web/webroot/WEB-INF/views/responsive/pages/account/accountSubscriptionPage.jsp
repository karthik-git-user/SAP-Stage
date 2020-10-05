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
<%@ taglib prefix="subscription" tagdir="/WEB-INF/tags/addons/sapsubscriptionaddon/responsive/subscription"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="paymentcard" tagdir="/WEB-INF/tags/responsive/paymentcard" %>
<c:url value="/paymentcard/changePaymentDetailsPopup?s=${subscriptionData.id}&v=${subscriptionData.version}" var="changePaymentPopupUrl"/>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="currentDate" value="${now}" pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="UTC"/>
<fmt:formatDate var="withdrawalPeriodEndDate" value="${subscriptionData.withdrawalPeriodEndDate}" pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="UTC"/>
<head>
   <link href="https://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css"/>
</head>
<spring:htmlEscape defaultHtmlEscape="true"/>
<c:url value="${subscriptionData.productUrl}" var="productUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/cancel" var="cancelSubscriptionUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/withdraw" var="withdrawSubscriptionUrl" />
<c:url value="/my-account/subscription/${subscriptionData.id}/extend" var="extendSubscriptionUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/reverseCancel" var="reverseCancelUrl"/>
<spring:url value="/my-account/subscriptions" var="subscriptionUrl" />
<c:url value="/my-account/subscription/${subscriptionData.id}/caleffDate/${subscriptionData.version}" var="calcEffCancellationDateUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/calcExtnEffDate" var="calcEffExtensionDateUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/withdrawConfirmation/${subscriptionData.version}" var="withdrawSubUrl" />
<c:url value="/my-account/subscription/${subscriptionData.id}/ratePlan" var="viewChargesUrl" />
<common:headline url="${subscriptionUrl}" labelKey="text.account.subscription.detail"/>
<form:form id="subscriptionCancellationForm" name="subscriptionCancellationForm" action="${fn:escapeXml(cancelSubscriptionUrl)}" method="post" modelAttribute="subscriptionCancellationForm">
   <div class="account-section-content	">
      <div class="account-orderhistory">
         <div class="account-overview-table">
            <table class="orderhistory-list-table responsive-table">
               <tr	class="account-orderhistory-table-head responsive-table-head hidden-xs">
                  <th>
                     <spring:theme code="text.account.subscription.productName" text="Product Name"/>
                  </th>
                  <th>
                     <spring:theme code="text.account.subscription.price" text="Price"/>
                  </th>
                  <th>
                     <spring:theme code="text.account.subscription.status" text="Status"/>
                  </th>
                  <th>
                     <spring:theme code="text.account.subscription.startDate" text="Start Date"/>
                  </th>
                  <th>
                     <spring:theme code="text.account.subscription.endDate" text="End Date"/>
                  </th>
                  <th></th>
               </tr>
               <tr class="responsive-table-item">
                  <td class="hidden-sm hidden-md hidden-lg">
                     <spring:theme code="text.account.subscription.productName" text="Product Name"/>
                  </td>
                  <td class="responsive-table-cell"><a href="${productUrl}">${subscriptionData.name}</a></td>
                  <td class="hidden-sm hidden-md hidden-lg">
                     <spring:theme code="text.account.subscription.price" text="Price"/>
                  </td>
                  <td class="responsive-table-cell" style="width:1px;white-space:no-wrap;">
                     <div id="table">
                        <button id="detailedCharges" class="btn btn-link" style="font: bold 9px Arial;" data-product-code="${fn:replace(fn:escapeXml(subscriptionData.name), ' ', '_')}">
                           <spring:theme code="text.account.subscription.viewCharges" text="View Charges"/>
                        </button>
                        <div style="display:none" id="pricepanel_${fn:replace(fn:escapeXml(subscriptionData.name), ' ', '_')}">
                           <subscription:subscriptionRatePlanPanel subscription="${subscriptionData}" />
                        </div>

                     </div>
                  </td>
                  <td class="hidden-sm hidden-md hidden-lg">
                     <spring:theme code="text.account.subscription.status" text="Status"/>
                  </td>
                  <td class="responsive-table-cell">
                     <spring:theme code="text.account.subscriptions.status.${subscriptionData.status}" />
                  </td>
                  <td class="hidden-sm hidden-md hidden-lg">
                     <spring:theme code="text.account.subscription.startDate" text="Start Date"/>
                  </td>
                  <td class="responsive-table-cell">
                     <fmt:formatDate value="${subscriptionData.startDate}" dateStyle="long" timeStyle="short" type="date"/>
                  </td>
                  <td class="hidden-sm hidden-md hidden-lg">
                     <spring:theme code="text.account.subscription.endDate" text="End Date"/>
                  </td>
                  <td class="responsive-table-cell">
                     <c:if test="${not empty subscriptionData.endDate}">
                        <fmt:formatDate	value="${subscriptionData.endDate}" dateStyle="long" timeStyle="short" type="date" />
                     </c:if>
                  </td>
                  <c:set var="rcSubStatus" value="${subscriptionData.status}" />
                  <td class="hidden-sm hidden-md hidden-lg"></td>
                  <td>
                     <c:if test="${subscriptionData.status eq 'ACTIVE'}">
                        <button id="cancelRCSubscription" type="submit" class="btn btn-primary btn-block" data-cancel-sub-url="${calcEffCancellationDateUrl}">
                           <spring:theme code="text.account.subscription.cancelSubscription" text="Cancel"/>
                        </button>
                        <c:if test="${not empty showChangePaymentBtn}">
                           <button id="changePaymentDetails" type="submit" class="btn btn-primary btn-block" data-change-payment-details-popup-url="${changePaymentPopupUrl}">
                              <spring:theme code="text.account.subscription.changePaymentDetails" text="Change Payment Details"/>
                           </button>
                        </c:if>
                     </c:if>
                     <c:if test="${subscriptionData.status eq 'CANCELLED'}">
                        <button id="reverseCancelSubscription" class="btn btn-primary btn-block">
                           <spring:theme code="text.account.subscription.resubscribe" text="Resubscribe"/>
                        </button>
                     </c:if>
                     <input type="hidden" name="version"	value="${fn:escapeXml(subscriptionData.version)}"/>
                     <input type="hidden" name="ratePlanId" value="${fn:escapeXml(subscriptionData.ratePlanId)}"/>
                     <c:if test="${not empty subscriptionData.validTillDate}">
                        <input	type="hidden" name="subscriptionEndDate" value="${fn:escapeXml(subscriptionData.validTillDate)}" />
                     </c:if>
                  </td>
               </tr>
            </table>
         </div>
      </div>
   </div>
   <br><br>
   <table class="orderhistory-list-table responsive-table">
      <tr>
         <c:if test="${(subscriptionData.status eq 'ACTIVE') and (withdrawalPeriodEndDate gt currentDate)}">
            <td>
               <strong>
                  <spring:theme
                     code="text.account.withdrawsubscription.endDate"
                     text="Withdrawal End Date" />
               </strong>
            </td>
            <td class="responsive-table-cell">
               <fmt:formatDate
                  value="${subscriptionData.withdrawalPeriodEndDate}" dateStyle="long"
                  timeStyle="short" type="date" />
            </td>
            <td>
               <form:form id="subscriptionWithdrawalForm"
                  name="subscriptionWithdrawalForm"
                  action="${fn:escapeXml(withdrawSubscriptionUrl)}" method="post"
                  modelAttribute="subscriptionWithdrawalForm">
                  <button id="" type="submit"
                     class="btn btn-primary btn-block"
                     data-withdraw-sub-url="${withdrawSubUrl}" style="width: 250px; height: 50px;float: right;" >
                     <spring:theme
                        code="text.account.subscription.withdrawSubscription"
                        text="Withdraw" />
                  </button>
                  <input type="hidden" name="version"
                     value="${fn:escapeXml(subscriptionData.version)}" />
                  <input type="hidden" name="withdrawalPeriodEndDate"
                     value="${fn:escapeXml(subscriptionData.withdrawalPeriodEndDate)}" />
               </form:form>
            </td>
         </c:if>
      </tr>
   </table>
   <div class="account-section-content">
      <div class="account-orderhistory">
         <div class="account-overview-table">
            <table class="orderhistory-list-table responsive-table">
               <tr class="account-orderhistory-table-head responsive-table-head hidden-xs" style="text-align: left">
                  <th style="text-align: left">
                     <spring:theme code="Current Usage" text="Current Usage" />
                  </th>
                  <th></th>
               </tr>
               <tr class="responsive-table-item" style="text-align: left">
                  <td class="responsive-table-cell" style="text-align: left">
                     <div class="table">
                        <c:forEach items="${subscriptionData.currentUsages}" var="charge">
                           <c:if test="${charge.usage!=null}">
                              <div class="tr">
                                 <div class="td">
                                    &nbsp;${charge.usage}&nbsp;${charge.usageUnit.id}
                                 </div>
                                 <div class="td">&nbsp;</div>
                                 <div class="td">
                                    <spring:theme code="text.account.subscriptions.bill.netAmount" />
                                    : &nbsp;
                                    <format:fromPrice priceData="${charge.netAmount}"/>
                                 </div>
                              </div>
                           </c:if>
                        </c:forEach>
                     </div>
                  </td>
                  <td></td>
               </tr>
            </table>
         </div>
      </div>
   </div>
</form:form>
<br><br>
<form:form id="reverseCancelForm" name="reverseCancelForm" action="${fn:escapeXml(reverseCancelUrl)}" method="post" modelAttribute="reverseCancelForm">
   <input type="hidden" name="version"	value="${fn:escapeXml(subscriptionData.version)}"/>
</form:form>
<c:if test="${subscriptionData.status eq 'ACTIVE' && not empty subscriptionData.validTillDate && empty subscriptionData.renewalTerm}">
   <form:form id="subscriptionExtensionForm" name="subscriptionExtensionForm" modelAttribute="subscriptionExtensionForm"   method="post">
      <div class="form" id="extendRCSubscription">
         <div class="form-group" style="display: inline-block; margin: 10px; width: 180px;">
            <spring:theme code="text.account.subscription.extendSubscriptionBy" text="Extend Subscription By"/>
         </div>
         <div class="form-group" style="display: inline-block; margin: 10px; width: 300px;">
            <input type="text" placeholder="enter valid number" name="extensionPeriod" id="extensionPeriod" class="form-control"/>
         </div>
         <div class="form-group" style="display: inline-block; margin: 10px; width: 100px;">${fn:escapeXml(subscriptionData.contractFrequency)}</div>
         <div class="form-group" style="display: inline-block; margin: 10px; width: 300px;">
            <button id="extendSubscription"
               class="btn btn-primary btn-block"
               data-extend-sub-url="${fn:escapeXml(calcEffExtensionDateUrl)}">
               <spring:theme code="text.account.subscription.extend" text="Extend Subscription" />
            </button>
            <input type="hidden" name="version"	value="${fn:escapeXml(subscriptionData.version)}"/>
            <input type="hidden" name="ratePlanId" value="${fn:escapeXml(subscriptionData.ratePlanId)}"/>
            <input type="hidden" name="billingFrequency" value="${fn:escapeXml(subscriptionData.contractFrequency)}"/>
            <c:if test="${not empty subscriptionData.validTillDate}">
               <input type="hidden" name="validTilldate" value="${fn:escapeXml(subscriptionData.validTillDate)}" />
            </c:if>
         </div>
         <div class="form-group">
            <input id="rcSubUnlimited" name="unlimited" type="checkbox">
            <label class="control-label notification_preference_channel">
            <span>Select for Unlimited Subscription</span>
            </label>
         </div>
      </div>
   </form:form>
</c:if>