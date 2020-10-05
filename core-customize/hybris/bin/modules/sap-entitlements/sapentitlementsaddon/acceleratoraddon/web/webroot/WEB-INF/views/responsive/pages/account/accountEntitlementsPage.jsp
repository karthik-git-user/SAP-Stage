<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-account/entitlement/" var="entitlementDetailsUrl"/>
<spring:url value="/p/" var="productUrl"/>
<div class="account-section-header">
	<spring:theme code="text.account.entitlements" text="Entitlements"/>
</div>
<c:if test="${empty entitlements}">
	<div class="account-section-content content-empty">
		<spring:theme code="text.account.entitlements.noEntitlements" text="You have no entitlements"/>
	</div>
</c:if>
<c:if test="${not empty entitlements}">
	<div class="account-section-content	">
		<div class="account-orderhistory">
            <div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
					<tr class="account-orderhistory-table-head responsive-table-head hidden-xs">
						<th><spring:theme code="text.account.entitlement.entitlementNumber"/></th>
						<th><spring:theme code="text.account.entitlement"/></th>
						<th><spring:theme code="order.product" text="Product"/></th>
						<th><spring:theme code="order.quantity" text="Quantity"/></th>
	                    <th><spring:theme code="text.account.entitlement.status" text="Status"/></th>
	                    <th><spring:theme code="text.account.entitlement.expires" text="Expires On"/></th>	              
	             	</tr>
					<c:forEach items="${entitlements}" var="entitlement">              
						<tr class="responsive-table-item">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.entitlementNumber"/></td>
								<td class="responsive-table-cell">
                                		<a href="${entitlementDetailsUrl}${entitlement.entitlementNumber}">${entitlement.entitlementNumber}</a>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement"/></td>
								<td class="responsive-table-cell">
                                		${entitlement.name}
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="order.product" text="Product"/></td>
								<td class="responsive-table-cell">
									<c:choose>
                                		<c:when test="${not empty entitlement.product.url}">
                                			<a href="${productUrl}${entitlement.product.code}">${entitlement.product.code}</a>
                                		</c:when>
                                		<c:otherwise>${entitlement.product.code}</c:otherwise>
                            		</c:choose>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="order.quantity" text="Quantity"/></td>
								<td class="responsive-table-cell">
									 ${entitlement.quantity}
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.status" text="Status"/></td>
								<td class="responsive-table-cell">
									 ${entitlement.status}
								</td>								
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.expires" text="Expires On"/></td>
								<td class="responsive-table-cell">
									 <fmt:formatDate value="${entitlement.validTo}" dateStyle="long" timeStyle="short" type="date"/>
								</td>							
						</tr>
					</c:forEach>
				</table>
            </div>
		</div>		
	</div>
</c:if>


