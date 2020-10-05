<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/sapbillinginvoiceaddon/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty orderData.extBillingDocuments}">
    <div class="account-orderdetail well well-tertiary billing-invoice marging-bottom20px">
        <div class="well-headline">
           <spring:theme code="text.account.order.details.invoice.title" />
        </div>
        
        <div class="responsive-table">
                <table class="responsive-table">
                    <thead>
                    <tr class="responsive-table-head hidden-xs">
                        <th id="header1"><spring:theme code="text.account.order.details.invoice.id"/></th>
                        <th id="header2"><spring:theme code="text.account.order.details.invoice.type"/></th>
                        <th id="header3"><spring:theme code="text.account.order.details.invoice.billingdate"/></th>
                        <th id="header4"><spring:theme code="text.account.order.details.invoice.totalamount"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    	<c:forEach items="${orderData.extBillingDocuments}" var="billingDoc" varStatus="loop">
                        	<tr class="responsive-table-item">
                        		<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.order.details.invoice.id"/></td>
                            	<td headers="header1" class="responsive-table-cell">
                                   
                                    <ycommerce:testId>
			                          <spring:url value="/my-account/manage-billing-invoice/billingInvoice/{/serviceOrderCode}/{/billingDocId}" var="billingInvoiceDetailsUrl" htmlEscape="false">
			                              <spring:param name="serviceOrderCode" value="${billingDoc.sapOrderCode}" />
			                              <spring:param name="billingDocId" value="${billingDoc.billingDocumentId}" />
			                          </spring:url>
			                          <a href="${fn:escapeXml(billingInvoiceDetailsUrl)}" class="responsive-table-link">
			                          	<span class="item-value">${billingDoc.billingDocumentId}</span>
			                          </a>
			                      </ycommerce:testId>
                            	</td>
                            	<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.order.details.invoice.type"/></td>
                            	<td headers="header2" class="responsive-table-cell">
                                    ${billingDoc.billingDocType}
                            	</td>
                            	<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.order.details.invoice.billingdate"/></td>
                            	<td headers="header3" class="responsive-table-cell">
                                    <fmt:formatDate value="${billingDoc.billingInvoiceDate}" dateStyle="long" timeStyle="short" type="date"/>
                            	</td>
                            	<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.order.details.invoice.totalamount"/></td>
                            	<td headers="header4" class="responsive-table-cell">
                            		${billingDoc.billingInvoiceNetAmount}
                                    
                            	</td>
                        	</tr>
                        </c:forEach>
                    </tbody>
                </table>
         </div>       
    </div>
</c:if>