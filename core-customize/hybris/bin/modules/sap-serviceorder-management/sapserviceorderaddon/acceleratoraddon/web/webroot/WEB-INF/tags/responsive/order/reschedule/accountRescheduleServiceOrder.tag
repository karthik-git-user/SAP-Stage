<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/responsive/grid" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ordercancel" tagdir="/WEB-INF/tags/addons/orderselfserviceaddon/responsive/order/cancel" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<form:form action="${request.contextPath}/my-account/order/${fn:escapeXml(order.code)}/rescheduleservice/submit"
           id="rescheduleServiceForm"
           modelAttribute="rescheduleServiceForm"
           class="account-cancel-order-form">



    <ul class="item__list cart__list cancel-order__list">

        <li class="hidden-xs hidden-sm">
            <ul class="item__list--header">
                <li class="item__toggle"></li>
                <li class="item__image"></li>
                <li class="item__info"><spring:theme code="basket.page.item"/></li>
                <li class="item__price"><spring:theme code="basket.page.price"/></li>
                <li class="item__quantity"><spring:theme code="basket.page.qty"/></li>
            </ul>
        </li>

        <c:forEach items="${order.entries}" var="entry" varStatus="loop">
            <c:if test="${entry.entryNumber != -1}">
                <c:if test="${not empty entry.statusSummaryMap}">
                    <c:set var="errorCount" value="${entry.statusSummaryMap.get(errorStatus)}"/>
                    <c:if test="${not empty errorCount && errorCount > 0}">
                        <div class="notification has-error">
                            <spring:theme code="basket.error.invalid.configuration" arguments="${errorCount}"/>
                            <spring:theme code="basket.error.invalid.configuration.edit"/>
                        </div>
                    </c:if>
                </c:if>
                <li class="item__list--item">
                        <%-- chevron for multi-d products --%>
                    <div class="hidden-xs hidden-sm item__toggle">
                        <c:if test="${entry.product.multidimensional}">
                            <div class="js-show-editable-grid" data-index="${loop.index}"
                                 data-read-only-multid-grid="${not entry.updateable}">
                                <ycommerce:testId code="cancel_product_updateQuantity">
                                    <span class="glyphicon glyphicon-chevron-down"></span>
                                </ycommerce:testId>

                            </div>
                        </c:if>
                    </div>

                        <%-- product image --%>
                    <div class="item__image">
                        <product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
                    </div>

                        <%-- product name, code, promotions --%>
                    <div class="item__info">
                        <ycommerce:testId code="cancel_product_name">
                            <span class="item__name">${fn:escapeXml(entry.product.name)}</span>
                        </ycommerce:testId>

                        <div class="item__code">${fn:escapeXml(entry.product.code)}</div>

                        <c:if test="${fn:escapeXml(entry.product.configurable)}">
                            <div class="item__configurations">
                                <c:forEach var="config" items="${entry.configurationInfos}">
                                    <c:set var="style" value=""/>
                                    <c:if test="${config.status eq errorStatus}">
                                        <c:set var="style" value="color:red"/>
                                    </c:if>
                                    <div class="item__configuration-entry row" style="${style}">
                                        <div class="item__configuration-name col-md-4">${fn:escapeXml(config.configurationLabel)}:</div>
                                        <div class="item__configuration-value col-md-8">${fn:escapeXml(config.configurationValue)}</div>
                                    </div>
                                </c:forEach>
                            </div>
                            <c:if test="${not empty entry.configurationInfos}">
                                <div class="item__configurations-edit">
                                    <spring:theme code="basket.page.change.configuration"/>
                                </div>
                            </c:if>
                        </c:if>
                    </div>

                        <%-- price --%>
                    <div class="item__price">
                        <span class="visible-xs visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span>
                        <format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
                    </div>

                        <%-- quantity --%>
                    <div class="item__quantity hidden-xs hidden-sm">
                        <span class="qtyValue"><c:out value="${entry.quantity}"/></span>
                        <input type="hidden" id="item_quantity_${entry.entryNumber}" value="${entry.quantity}"/>
                    </div>


                </li>
                <li>
                    <div class="add-to-cart-order-form-wrap display-none"></div>
                </li>
            </c:if>
            <c:if test="${entry.entryNumber == -1}">
                <c:forEach items="${entry.entries}" var="nentry" varStatus="nloop">
                    <c:if test="${nentry.cancellableQty > 0}">
                        <li class="item__list--item">
                            <div class="hidden-xs hidden-sm item__toggle">
                                <c:if test="${nentry.product.multidimensional}">
                                    <div class="js-show-editable-grid" data-index="${nloop.index}"
                                         data-read-only-multid-grid="${not nentry.updateable}">
                                        <ycommerce:testId code="cancel_product_updateQuantity">
                                            <span class="glyphicon glyphicon-chevron-down"></span>
                                        </ycommerce:testId>

                                    </div>
                                </c:if>
                            </div>
                                <%-- product image --%>
                            <div class="item__image">
                                <product:productPrimaryImage product="${nentry.product}" format="thumbnail"/>
                            </div>

                                <%-- product name, code, promotions --%>
                            <div class="item__info">
                                <ycommerce:testId code="cancel_product_name">
                                    <span class="item__name">${fn:escapeXml(nentry.product.name)}</span>
                                </ycommerce:testId>

                                <div class="item__code">${fn:escapeXml(nentry.product.code)}</div>

                                <c:if test="${nentry.product.configurable}">
                                    <div class="item__configurations">
                                        <c:forEach var="config" items="${nentry.configurationInfos}">
                                            <c:set var="style" value=""/>
                                            <c:if test="${config.status eq errorStatus}">
                                                <c:set var="style" value="color:red"/>
                                            </c:if>
                                            <div class="item__configuration-entry row" style="${style}">
                                                <div class="item__configuration-name col-md-4">${fn:escapeXml(config.configurationLabel)}:</div>
                                                <div class="item__configuration-value col-md-8">${fn:escapeXml(config.configurationValue)}</div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <c:if test="${not empty nentry.configurationInfos}">
                                        <div class="item__configurations-edit">
                                            <spring:theme code="basket.page.change.configuration"/>
                                        </div>
                                    </c:if>
                                </c:if>
                            </div>
                                <%-- price --%>
                            <div class="item__price">
                                <span class="visible-xs visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span>
                                <format:price priceData="${nentry.basePrice}" displayFreeForZero="true"/>
                            </div>

                                <%-- quantity --%>
                            <div class="item__quantity hidden-xs hidden-sm">
                                <span class="qtyValue"><c:out value="${nentry.quantity}"/></span>
                                <input type="hidden" id="item_quantity_${nentry.entryNumber}" value="${nentry.quantity}"/>
                            </div>

                        </li>
                        <li>
                            <div class="add-to-cart-order-form-wrap display-none"></div>
                        </li>
                    </c:if>
                </c:forEach>
            </c:if>
        </c:forEach>

    </ul>
    
  	<div id="serviceRescheduleDateDiv" class="form-group">
		<div class="hidden" data-scheduleleaddays="${scheduleLeadDays}"></div>
		<formElement:formInputBox idKey="scheduleDate" labelKey="checkout.multi.scheduleservicedate.label" path="serviceDate" inputCSS="text form-control" />

		<div class="form-group">
			<label class="control-label " for="serviceTime"><spring:theme
					code="checkout.multi.scheduleservicetime.label" /></label> <select
				id="serviceTime" name="serviceTime" class="form-control">
				<c:forEach items="${serviceScheduleTimeList}" var="time">
					<c:choose>
						<c:when test="${time eq defaultServiceTime}">
							<option selected="selected" value="${time}">${time}</option>
						</c:when>
						<c:otherwise>
							<option value="${time}">${time}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>

		</div>
	</div>
	
    <!--Button begins-->
    <div class="row">
        <div class="col-md-6 col-md-offset-6">
            <div class="cancel-actions">
                <div class="row">
                    <div class="col-sm-6 col-sm-offset-6">

                        <button type="submit" class="btn btn-primary btn-block" id="rescheduleOrderButtonConfirmation">
                            <spring:theme code="text.account.order.orderDetails.service.reschedule.button"/>
                        </button>


                    </div>
                </div>
            </div>
        </div>
    </div>

</form:form>
