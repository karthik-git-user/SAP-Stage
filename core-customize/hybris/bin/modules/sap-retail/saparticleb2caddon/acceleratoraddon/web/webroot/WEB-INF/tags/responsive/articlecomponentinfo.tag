<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>

<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>

<c:choose> 
 	<c:when test="${not empty product.articleComponents}">

		<div class="tabhead">
			<a href=""><spring:theme code="articlecomponent.tab.title" /></a>
			<span class="glyphicon"></span>
		</div>
		<div class="tabbody">

	 		<div class="carousel-component carousel-articlecomponent">
				<div class="carousel js-owl-carousel js-owl-default">
				
					<c:forEach items="${product.articleComponents}" var="articleComponent">
						<c:url value="${articleComponent.component.url}" var="articleComponentUrl"/> 
						
						<div class="item">
							<a href="${articleComponentUrl}" title="${articleComponent.component.name}"
									class="js-reference-item">
			
								<div class="thumb">
									<product:productPrimaryImage product="${articleComponent.component}" format="product"/>
								</div>	

							    <div class="item-name">${articleComponent.component.name}</div>
							    <div class="articlecomponent-uom-container">
								    <span class="articlecomponent-uomq">${articleComponent.quantity}</span>
								    <span class="articlecomponent-uomu">${articleComponent.unit}</span>
							    </div>	
 							</a>
 						</div>	
					</c:forEach>
				</div>
			</div>	
		
		</div>
 	</c:when> 

 	<c:otherwise> 
		<component:emptyComponent/> 
 	</c:otherwise> 
 </c:choose> 