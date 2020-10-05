<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-account/subscription/" var="subscriptionDetailsUrl" htmlEscape="false"/>

<c:set var="searchUrl" value="/my-account/subscriptions?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>

<c:url value="/${subscriptionData.productUrl}" var="productUrl"/>
<div class="account-section-header">
	<spring:theme code="text.account.subscription" text="Subscriptions"/>
</div>
<c:if test="${empty searchPageData.results}">
    <div class="row">
        <div class="col-md-6 col-md-push-3">
	        <div class="account-section-content content-empty">
		<spring:theme code="text.account.subscriptions.noSubscriptions" text="You have no subscriptions"/>
	       </div>
	   </div>
	</div>
</c:if>

<c:if test="${not empty searchPageData.results}">
	<div class="account-section-content	">
		<div class="account-orderhistory">
		    <div class="account-orderhistory-pagination">
                        <nav:pagination top="true" msgKey="${messageKey}" hideRefineButton="true"
                                        supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"
                                        searchPageData="${searchPageData}" searchUrl="${searchUrl}"
                                        numberPagesShown="${numberPagesShown}"/>
            </div>
            <div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
					<tr class="account-orderhistory-table-head responsive-table-head hidden-xs">
						<th><spring:theme code="text.account.subscription.documentNumber"/></th>
						<th><spring:theme code="text.account.subscription.productName" text="Product Name"/></th>
	                    <th><spring:theme code="text.account.subscription.startDate" text="Start Date"/></th>
	                    <th><spring:theme code="text.account.subscription.endDate" text="End Date"/></th>
	                    <th><spring:theme code="text.account.subscription.status" text="Status"/></th>
	                    <th><spring:theme code="text.account.subscription.actions" text="Actions"/></th>
					</tr>
					<c:forEach items="${searchPageData.results}" var="subscription">
						<c:url value="${subscription.productUrl}" var="productUrl"/>
						<tr class="responsive-table-item">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.documentNumber"/></td>
								<td class="responsive-table-cell">
                                		${subscription.documentNumber}
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.productName" text="Product Name"/></td>
								<td class="responsive-table-cell">
									<a href="${productUrl}">${subscription.name}</a>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.startDate" text="Start Date"/></td>
								<td class="responsive-table-cell">
									<fmt:formatDate value="${subscription.startDate}" dateStyle="long" timeStyle="short" type="date"/>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.endDate" text="End Date"/></td>
								<td class="responsive-table-cell">
									 <fmt:formatDate value="${subscription.endDate}" dateStyle="long" timeStyle="short" type="date"/>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.status" text="Status"/></td>
								<td class="responsive-table-cell">
								    <spring:theme code="text.account.subscriptions.status.${subscription.status}" />
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.actions" text="Actions"/></td>
								<td class="responsive-table-cell">
									<a href="${subscriptionDetailsUrl}${subscription.id}" class="responsive-table-link">
										<spring:theme code="text.manage" text="Manage"/>
									</a>
								</td>
						</tr>
					</c:forEach>
				</table>
            </div>
		</div>
	</div>
</c:if>