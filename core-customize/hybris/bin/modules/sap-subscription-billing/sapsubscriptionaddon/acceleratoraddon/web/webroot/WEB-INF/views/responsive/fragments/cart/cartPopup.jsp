<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:theme code="text.addToCart" var="addToCartText"/>
<spring:theme code="text.popupCartTitle" var="popupCartTitleText"/>
<c:url value="/cart" var="cartUrl"/>
<c:url value="/cart/checkout" var="checkoutUrl"/>

<c:choose>
	<c:when test="${not empty cartData.quoteData}">
		<c:set var="miniCartProceed" value="quote.view"/>
	</c:when>
	<c:otherwise>
		<c:set var="miniCartProceed" value="checkout.checkout"/>
	</c:otherwise>
</c:choose>
			

<div class="mini-cart js-mini-cart">
	<ycommerce:testId code="mini-cart-popup">
		<div class="mini-cart-body">
			<c:choose>
				<c:when test="${numberShowing > 0 }">
						<div class="legend">
							<spring:theme code="popup.cart.showing" arguments="${numberShowing},${numberItemsInCart}"/>
							<c:if test="${numberItemsInCart > numberShowing}">
								<a href="${cartUrl}"><spring:theme code="popup.cart.showall"/></a>
							</c:if>
						</div>

						<ol class="mini-cart-list">
							<c:forEach items="${entries}" var="entry" end="${numberShowing - 1}">
							<c:url value="${entry.product.url}" var="entryProductUrl"/>
								<li class="mini-cart-item">
									<div class="thumb">
										<a href="${entryProductUrl}">
											<product:productPrimaryImage product="${entry.product}" format="cartIcon"/>
										</a>
									</div>
									<div class="details">
										<a class="name" href="${entryProductUrl}">${fn:escapeXml(entry.product.name)}</a>
										<div class="qty"><spring:theme code="popup.cart.quantity"/>: ${entry.quantity}</div>
										<c:forEach items="${entry.product.baseOptions}" var="baseOptions">
											<c:forEach items="${baseOptions.selected.variantOptionQualifiers}" var="baseOptionQualifier">
												<c:if test="${baseOptionQualifier.qualifier eq 'style' and not empty baseOptionQualifier.image.url}">
													<div class="itemColor">
														<span class="label"><spring:theme code="product.variants.colour"/></span>
														<img src="${baseOptionQualifier.image.url}" alt="${fn:escapeXml(baseOptionQualifier.value)}" title="${fn:escapeXml(baseOptionQualifier.value)}"/>
													</div>
												</c:if>
												<c:if test="${baseOptionQualifier.qualifier eq 'size'}">
													<div class="itemSize">
														<span class="label"><spring:theme code="product.variants.size"/></span>
															${fn:escapeXml(baseOptionQualifier.value)}
													</div>
												</c:if>
											</c:forEach>
										</c:forEach>
										<c:if test="${not empty entry.deliveryPointOfService.name}">
											<div class="itemPickup"><span class="itemPickupLabel"><spring:theme code="popup.cart.pickup"/></span>&nbsp;${fn:escapeXml(entry.deliveryPointOfService.name)}</div>
										</c:if>
									</div>
									<div class="itemPrice">
						               <c:choose>
						                    <c:when test="${not empty entry.product.subscriptionTerm}">
						                        <c:forEach items="${entry.orderEntryPrices}" var="orderEntryPrice"> <!-- please ensure that these TDs get rendered always to avoid a uneven number of TDs per Row -->
						                            <span class="visible-xs visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span>
											  		<c:if test = "${orderEntryPrice.billingTime ne null and orderEntryPrice.billingTime.code eq 'paynow'}">
														
														<span>${orderEntryPrice.billingTime.name}: </span>
														<format:price priceData="${orderEntryPrice.basePrice}" displayFreeForZero="false" />
												</c:if>
						                        </c:forEach>
						                    </c:when>
						                    <c:otherwise>
						                      <span class="visible-xs visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span>
											  <format:price priceData="${entry.basePrice}" displayFreeForZero="false"/>
						                    </c:otherwise>
						                </c:choose>
									</div>
								</li>
							</c:forEach>
						</ol>

						<c:if test="${not empty lightboxBannerComponent && lightboxBannerComponent.visible}">
							<cms:component component="${lightboxBannerComponent}" evaluateRestriction="true"  />
						</c:if>

						<div class="mini-cart-totals">
							<div class="key"><spring:theme code="popup.cart.total"/></div>
							<div class="value"><format:price priceData="${cartData.totalPrice}"/></div>
						</div>
						<a href="${cartUrl}" class="btn btn-primary btn-block mini-cart-checkout-button">
							<spring:theme code="${miniCartProceed }" />
						</a>
						<a href="" class="btn btn-default btn-block js-mini-cart-close-button">
							<spring:theme code="cart.page.continue"/>
						</a>
				</c:when>

				<c:otherwise>
					<c:if test="${not empty lightboxBannerComponent && lightboxBannerComponent.visible}">
						<cms:component component="${lightboxBannerComponent}" evaluateRestriction="true"  />
					</c:if>

					<button class="btn btn-block" disabled="disabled">
						<spring:theme code="${miniCartProceed }" />
					</button>
					<a href="" class="btn btn-default btn-block js-mini-cart-close-button">
						<spring:theme text="Continue Shopping" code="cart.page.continue"/>
					</a>
				</c:otherwise>
			</c:choose>
		</div>
	</ycommerce:testId>
</div>


