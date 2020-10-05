<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="orderEntry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<fmt:formatDate value="${activityData.created}" pattern="dd-MM-yy hh:mm a" />
<div class="container-fluid">
   <div class="row">
      <div class="col-sm-6">
         <div>
            <%--Product Code,Minimum Term, Subscription Term & Effective Date --%>
            <div>
               <strong>
                  <spring:theme code="text.product.productcode"/>
                  &nbsp;:
               </strong>
               &nbsp; ${orderEntry.product.name}
            </div>
            <div>
               <strong>
                  <spring:theme code="text.product.minimumterm"/>
                  &nbsp;:
               </strong>
               &nbsp; ${orderEntry.product.minimumTerm}&nbsp
               <spring:theme code="text.product.usage.months"/>
            </div>
            <div>
               <strong>
                  <spring:theme code="text.product.subscriptionterm"/>
                  &nbsp;:
               </strong>
               &nbsp; ${orderEntry.product.subscriptionValidTerm}&nbsp
               <spring:theme code="text.product.usage.months"/>
            </div>
            <div>
               <strong>
                  <spring:theme code="text.product.effectivedate"/>
                  &nbsp;:
               </strong>
               &nbsp; <%= (new java.util.Date()).toLocaleString()%>
            </div>
            <%--One Time Charges --%>
			<c:set var="oneTimeChargesCount" value="${fn:length(orderEntry.initialSubscriptionPricePlan.oneTimeChargeEntries)}"/>
			<c:if test="${oneTimeChargesCount ne 0}">
            <div>
               <h4>
                  <spring:theme code="text.product.price.onetimecharges"/>
               </h4>
               <table class="table table-striped table-bordered ">
                  <thead>
                     <tr>
                        <th>
                           <spring:theme code="text.product.rateplanelement"/>
                        </th>
                        <th>
                           <spring:theme code="text.product.price"/>
                        </th>
                     </tr>
                  </thead>
                  <tbody>
                     <c:forEach items="${orderEntry.initialSubscriptionPricePlan.oneTimeChargeEntries}" var="oneTimeChargeEntry">
                        <tr>
                           <c:if test="${oneTimeChargeEntry.billingTime ne null and oneTimeChargeEntry.billingTime.code ne 'paynow'}">
                              <td>
                                 <div class="item__code">${oneTimeChargeEntry.billingTime.code}</div>
                              </td>
                              <td>
                                 <p class="price">
                                    <format:fromPrice priceData="${oneTimeChargeEntry.price}"/>
                                 </p>
                              </td>
                           </c:if>
                        </tr>
                     </c:forEach>
                  </tbody>
               </table>
            </div>
			</c:if>
            <%--One Time Charges Ends--%>
            <%--Recurring Charges --%>
			<c:set var="recurringChargesCount" value="${fn:length(orderEntry.initialSubscriptionPricePlan.recurringChargeEntries)}"/>
			<c:if test="${recurringChargesCount ne 0}">
            <div>
               <h4>
                  <spring:theme code="text.product.price.recurringcharges"/>
               </h4>
               <table class="table table-striped table-bordered ">
                  <thead>
                     <tr>
                        <th>
                           <spring:theme code="text.product.rateplanelement"/>
                        </th>
                        <th>
                           <spring:theme code="text.product.price"/>
                        </th>
                     </tr>
                  </thead>
                  <tbody>
                     <c:forEach items="${orderEntry.initialSubscriptionPricePlan.recurringChargeEntries}" var="recurringChargeEntry">
                        <tr>
                           <td>
                              <div class="item__code">${orderEntry.product.subscriptionTerm.termOfServiceFrequency.code}</div>
                           </td>
                           <td>
                              <p class="price">
                                 <format:fromPrice priceData="${recurringChargeEntry.price}"/>
                              </p>
                           </td>
                        </tr>
                     </c:forEach>
                  </tbody>
               </table>
            </div>
			</c:if>
            <%--Recurring Charges Ends--%>
         </div>
      </div>
      <div class="col-sm-6">
         <div>
            <%--Usage Based Charges--%>
            <c:set var="usageChargesCount" value="${fn:length(orderEntry.initialSubscriptionPricePlan.usageCharges)}"/>
            <c:if test="${usageChargesCount ne 0}">
               <%--If there are no charge entries, the following table will not be displayed--%>
               <c:set var="volumeUsageChargeEntriesCount" value="0"/>
               <c:set var="blockUsageChargeEntriesCount" value="0"/>
               <c:set var="tierUsageChargeEntriesCount" value="0"/>
               <c:set var="percentageUsageChargeEntriesCount" value="0"/>
               <c:forEach items="${orderEntry.initialSubscriptionPricePlan.usageCharges}" var="usageCharge">
                  <c:choose>
                     <c:when test="${usageCharge['class'].simpleName eq 'VolumeUsageChargeData'}">
                        <c:set var="volumeUsageChargeEntriesCount" value="${fn:length(usageCharge.usageChargeEntries)}"/>
                        <c:if test="${volumeUsageChargeEntriesCount ne 0}">
                           <c:set var="volumeUsageChargeEntriesCount" value="${volumeUsageChargeEntriesCount + 1}"/>
                        </c:if>
                     </c:when>
                     <c:when test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and (usageCharge.usageChargeType.code == 'block_usage_charge' or usageCharge.usageChargeType.code == '')}">
                        <c:set var="blockUsageChargeEntriesCount" value="${fn:length(usageCharge.usageChargeEntries)}"/>
                        <c:if test="${blockUsageChargeEntriesCount ne 0}">
                           <c:set var="blockUsageChargeEntriesCount" value="${blockUsageChargeEntriesCount + 1}"/>
                        </c:if>
                     </c:when>
                     <c:when test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and usageCharge.usageChargeType.code == 'each_respective_tier'}">
                        <c:set var="tierUsageChargeEntriesCount" value="${fn:length(usageCharge.usageChargeEntries)}"/>
                        <c:if test="${tierUsageChargeEntriesCount ne 0}">
                           <c:set var="tierUsageChargeEntriesCount" value="${tierUsageChargeEntriesCount + 1}"/>
                        </c:if>
                     </c:when>
                     <c:when test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and usageCharge.usageChargeType.code == 'percentage_usage_charge'}" >
                        <c:set var="percentageUsageChargeEntriesCount" value="${fn:length(usageCharge.usageChargeEntries)}"/>
                        <c:if test="${percentageUsageChargeEntriesCount ne 0}">
                           <c:set var="percentageUsageChargeEntriesCount" value="${percentageUsageChargeEntriesCount + 1}"/>
                        </c:if>
                     </c:when>
                  </c:choose>
               </c:forEach>
               <h4>
                  <spring:theme code="text.product.price.usagebasedcharges"/>
               </h4>
               <%--Begin Volume Pricing--%>
               <c:choose>
                  <c:when test="${volumeUsageChargeEntriesCount ne 0}">
                     <h5>
                        <spring:theme code="text.product.price.usagebasedcharges.pricingtype"/>
                        &nbsp;:&nbsp;
                        <spring:theme code="text.product.price.usagebasedcharges.volumerate"/>
                     </h5>
                     <table class="table table-striped table-bordered">
                        <thead>
                           <tr>
                              <th>
                                 <spring:theme code="text.product.rateplanelement"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.upto"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.fixedprice"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.priceperblock"/>
                              </th>
                           </tr>
                        </thead>
                        <tbody>
                           <c:forEach items="${orderEntry.initialSubscriptionPricePlan.usageCharges}" var="usageCharge">
                              <c:if test="${usageCharge['class'].simpleName eq 'VolumeUsageChargeData'}">
                                 <c:set var="rowCount" value="${fn:length(usageCharge.usageChargeEntries)}"/>
                                 <c:set var="prevTierValue" value="-1"/>
                                 <c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">
                                    <tr>
                                       <c:if test="${usageChargeEntry['class'].simpleName eq 'TierUsageChargeEntryData'}">
                                          <td rowspan="${rowCount}">${usageCharge.usageUnit.id}</td>
                                          <td>${usageChargeEntry.tierStart} - ${usageChargeEntry.tierEnd}</td>
                                          <c:set var="prevTierValue" value="${usageChargeEntry.tierEnd}"/>
                                          <%--Fixed Price--%>
                                          <td>
                                             <format:fromPrice priceData="${usageChargeEntry.fixedPrice}"/>
                                          </td>
                                          <%--Price--%>
                                          <td>
                                             <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                          </td>
                                       </c:if>
                                       <c:if test="${usageChargeEntry['class'].simpleName eq 'OverageUsageChargeEntryData'}">
                                          <td>
                                             ${prevTierValue + 1} -
                                             <spring:theme code="text.product.unlimited"/>
                                          </td>
                                          <%--Fixed Price--%>
                                          <td>
                                             <format:fromPrice priceData="${usageChargeEntry.fixedPrice}"/>
                                          </td>
                                          <%--Price--%>
                                          <td>
                                             <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                          </td>
                                       </c:if>
                                    </tr>
                                 </c:forEach>
                              </c:if>
                           </c:forEach>
                        </tbody>
                     </table>
                  </c:when>
               </c:choose>
               <%--Volume Pricing Ends--%>
               <%--Begin Block Pricing--%>
               <c:choose>
                  <c:when test="${blockUsageChargeEntriesCount ne 0}">
                     <h5>
                        <spring:theme code="text.product.price.usagebasedcharges.pricingtype"/>
                        &nbsp;:&nbsp;
                        <spring:theme code="text.product.price.usagebasedcharges.blockrate"/>
                     </h5>
                     <table class="table table-striped table-bordered">
                        <thead>
                           <tr>
                              <th>
                                 <spring:theme code="text.product.rateplanelement"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.includedquantity"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.charges"/>
                              </th>
                           </tr>
                        </thead>
                        <tbody>
                           <c:forEach items="${orderEntry.initialSubscriptionPricePlan.usageCharges}" var="usageCharge" >
                              <c:if test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and (usageCharge.usageChargeType.code == 'block_usage_charge' or usageCharge.usageChargeType.code == '')}">
                                 <c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry" varStatus="usageChargeEntryStatus">
                                    <tr>
                                       <td>${usageCharge.usageUnit.name}</td>
                                       <td>${usageCharge.includedQty} &nbsp; ${usageCharge.usageUnit.namePlural}</td>
                                       <%--Charges--%>
                                       <c:choose>
                                          <c:when test ="${usageCharge.blockSize == 1}">
                                             <td>
                                                <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                                &nbsp;
                                                <spring:theme code="text.product.usage.per"/>
                                                ${usageCharge.usageUnit.name}
                                             </td>
                                          </c:when>
                                          <c:otherwise>
                                             <td>
                                                <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                                &nbsp;
                                                <spring:theme code="text.product.usage.every"/>
                                                &nbsp;${usageCharge.blockSize}&nbsp;${usageCharge.usageUnit.namePlural}
                                             </td>
                                          </c:otherwise>
                                       </c:choose>
                                    </tr>
                                 </c:forEach>
                              </c:if>
                           </c:forEach>
                        </tbody>
                     </table>
                  </c:when>
               </c:choose>
               <%--Block Pricing Ends--%>
               <%--Begin Tiered Pricing --%>
               <c:choose>
                  <c:when test="${tierUsageChargeEntriesCount ne 0}">
                     <h5>
                        <spring:theme code="text.product.price.usagebasedcharges.pricingtype"/>
                        &nbsp;:&nbsp;
                        <spring:theme code="text.product.price.usagebasedcharges.tierrate"/>
                     </h5>
                     <table class="table table-striped table-bordered">
                        <thead>
                           <tr>
                              <th>
                                 <spring:theme code="text.product.rateplanelement"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.minimumblocks"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.upto"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.priceperblock"/>
                              </th>
                           </tr>
                        </thead>
                        <tbody>
                           <c:forEach items="${orderEntry.initialSubscriptionPricePlan.usageCharges}" var="usageCharge">
                              <c:if test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and usageCharge.usageChargeType.code == 'each_respective_tier'}">
                                 <c:set var="rowCount" value="${fn:length(usageCharge.usageChargeEntries)}"/>
                                 <c:set var="prevTierValue" value="-1"/>
                                 <c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">
                                    <tr>
                                       <c:if test="${usageChargeEntry['class'].simpleName eq 'TierUsageChargeEntryData'}">
                                          <c:set var="lastTierValue" value="${usageChargeEntry.tierEnd}"/>
                                          <td rowspan="${rowCount}">${usageCharge.usageUnit.id}</td>
                                          <td>${usageCharge.minBlocks}</td>
                                          <td>${usageChargeEntry.tierStart} - ${usageChargeEntry.tierEnd}</td>
                                          <c:set var="prevTierValue" value="${usageChargeEntry.tierEnd}"/>
                                          <%--Price--%>
                                          <c:choose>
                                             <c:when test ="${usageCharge.blockSize == 1}">
                                                <td>
                                                   <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                                   &nbsp;
                                                   <spring:theme code="text.product.usage.per"/>
                                                   &nbsp;${usageCharge.usageUnit.name}
                                                </td>
                                             </c:when>
                                             <c:otherwise>
                                                <td>
                                                   <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                                   &nbsp;
                                                   <spring:theme code="text.product.usage.every"/>
                                                   ${usageCharge.blockSize} &nbsp; ${usageCharge.usageUnit.namePlural}
                                                </td>
                                             </c:otherwise>
                                          </c:choose>
                                       </c:if>
                                       <c:if test="${usageChargeEntry['class'].simpleName eq 'OverageUsageChargeEntryData'}">
                                          <c:set var="tierValue" value="${lastTierValue + 1}"/>
                                          <td>${usageCharge.minBlocks}</td>
                                          <td>
                                             ${prevTierValue + 1} -
                                             <spring:theme code="text.product.unlimited"/>
                                          </td>
                                          <%--Price--%>
                                          <c:choose>
                                             <c:when test ="${usageCharge.blockSize == 1}">
                                                <td>
                                                   <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                                   &nbsp;
                                                   <spring:theme code="text.product.usage.per"/>
                                                   &nbsp;${usageCharge.usageUnit.name}
                                                </td>
                                             </c:when>
                                             <c:otherwise>
                                                <td>
                                                   <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                                   &nbsp;
                                                   <spring:theme code="text.product.usage.every"/>
                                                   &nbsp; ${usageCharge.blockSize} ${usageCharge.usageUnit.namePlural}
                                                </td>
                                             </c:otherwise>
                                          </c:choose>
                                       </c:if>
                                    </tr>
                                 </c:forEach>
                              </c:if>
                           </c:forEach>
                        </tbody>
                     </table>
                  </c:when>
               </c:choose>
               <%--Tiered Pricing Ends--%>
               <%--Begin Percentage Pricing--%>
               <c:choose>
                  <c:when test="${percentageUsageChargeEntriesCount ne 0}">
                     <h5>
                        <spring:theme code="text.product.price.usagebasedcharges.pricingtype"/>
                        &nbsp;:&nbsp;
                        <spring:theme code="text.product.price.usagebasedcharges.percentagerate"/>
                     </h5>
                     <table class="table table-striped table-bordered">
                        <thead>
                           <tr>
                              <th>
                                 <spring:theme code="text.product.rateplanelement"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.ratio"/>
                              </th>
                              <th>
                                 <spring:theme code="text.product.charges"/>
                              </th>
                           </tr>
                        </thead>
                        <tbody>
                           <c:forEach items="${orderEntry.initialSubscriptionPricePlan.usageCharges}" var="usageCharge" >
                              <c:if test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and usageCharge.usageChargeType.code == 'percentage_usage_charge'}">
                                 <c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry" >
                                    <tr>
                                       <td>${usageCharge.usageUnit.id}</td>
                                       <td>${usageCharge.ratio}</td>
                                       <td>
                                          <format:fromPrice priceData="${usageChargeEntry.price}"/>
                                       </td>
                                    </tr>
                                 </c:forEach>
                              </c:if>
                           </c:forEach>
                        </tbody>
                     </table>
                  </c:when>
               </c:choose>
               <%--Percentage Pricing Ends--%>
               <%--Usage Based Charges Ends--%>
            </c:if>
         </div>
      </div>
   </div>
</div>