<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="productaddon"
    tagdir="/WEB-INF/tags/addons/sapsubscriptionaddon/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<head>
    <link href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css"/>
</head>
<spring:htmlEscape defaultHtmlEscape="true"/>
<c:url value="/p/${entitlementData.product.code}" var="productUrl"/>
<c:url value="/my-account/order/${entitlementData.orderNumber}/" var="orderUrl"/>
<spring:url value="/my-account/entitlements" var="entitlementUrl" />
<common:headline url="${entitlementUrl}" labelKey="text.account.entitlement.detail"/>
    <div class="account-section-content ">
        <div class="account-orderhistory">
            <div class="account-overview-table">
                <table class="orderhistory-list-table responsive-table">
                    <tr  class="account-orderhistory-table-head responsive-table-head hidden-xs">
                        <th><spring:theme code="text.account.entitlement" text="Entitlement"/></th>
                        <th><spring:theme code="order.product" text="Product"/></th>
                        <th><spring:theme code="order.quantity" text="Quantity"/></th>

                        <th><spring:theme code="text.account.entitlement.status" text="Status"/></th>
                        <th><spring:theme code="text.account.entitlement.right" text="Right"/></th>

                        <th><spring:theme code="text.account.entitlement.validFrom" text="Valid From"/></th>
                        <th><spring:theme code="text.account.entitlement.expires" text="Expires"/></th>

                    </tr>
                    <tr class="responsive-table-item">
                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement" text="Entitlement"/></td>
                        <td class="responsive-table-cell">${entitlementData.name}</td>
                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="order.product" text="Product"/></td>
                        <td class="responsive-table-cell">
                            <c:choose>
                                <c:when test="${not empty entitlementData.product.url}">
                                    <a href="${productUrl}">${entitlementData.product.code}</a>
                                </c:when>
                                <c:otherwise>${entitlementData.product.code}</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.quantity" text="Quantity"/></td>
                        <td class="responsive-table-cell">${entitlementData.quantity}</td>

                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.status" text="Status"/></td>
                        <td class="responsive-table-cell">${entitlementData.status}</td>

                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.right" text="Right"/></td>
                        <td class="responsive-table-cell">${entitlementData.right}</td>

                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.validFrom" text="Valid From"/></td>
                        <td class="responsive-table-cell"><fmt:formatDate value="${entitlementData.validFrom}" dateStyle="long" timeStyle="short" type="date"/></td>
                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.entitlement.expires" text="Expires"/></td>
                        <td class="responsive-table-cell"><fmt:formatDate value="${entitlementData.validTo}" dateStyle="long" timeStyle="short" type="date"/></td>
                        </td>

                    </tr>



                </table></div></div></div> <br><br><br>
                <div class="account-section-content">
                    <div class="account-orderhistory">
                        <div class="account-overview-table">
                            <table class="orderhistory-list-table responsive-table">
                                <tr class="responsive-table-head hidden-xs" style="text-align: left">
                                    <th><strong><spring:theme code="text.account.entitlement.additionalInformation" text="Additional Information"/></strong></th>
                                    <th></th>

                                </tr>
                                <tr class="responsive-table-item">
                                        <td class="responsive-table-cell" style="text-align: left">
                                                        <div class="td">
                                                            <spring:theme code="text.account.entitlement.region" text="Region"/>
                                                        </div>
                                        </td>
                                        <td class="responsive-table-cell" style="text-align: left">

                                            <div class="td">${entitlementData.region}</div>

                                        </td>
                                </tr>
                                <c:forEach items="${entitlementData.attributes}" var="entitlementAttribute">
                                <tr class="responsive-table-item">
                                    <td class="responsive-table-cell" style="text-align: left">

                                        <div class="td">${entitlementAttribute.attributeCode}</div>

                                    </td>
                                    <td class="responsive-table-cell" style="text-align: left">
                                    <c:forEach var="attributeValue" items="${entitlementAttribute.attributeValue}">
                                        <c:choose>

                                             <c:when test="${attributeValue['class'].simpleName eq 'String'}">
                                                ${attributeValue}
                                             </c:when>
                                             <c:otherwise>
                                                 <c:choose>
                                                     <c:when test="${attributeValue.value['class'].simpleName eq 'String'}">

                                                        ${attributeValue.value}
                                                    </c:when>
                                                    <c:when test="${attributeValue.value['class'].simpleName eq 'Integer'}">
                                                        ${attributeValue.value}
                                                    </c:when>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>

                                    </c:forEach>



                                    </td>
                                </tr>
                                </c:forEach>

                            </table>
                        </div>
                    </div>
                </div>