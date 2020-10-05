<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="urlTestId" required="false" type="java.lang.String"%>
<%@ attribute name="showManageUI" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showManageUIJS" required="false" type="java.lang.String"%>
<%@ attribute name="showManageUILabel" required="false" type="java.lang.String"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="account-section-header">
	<spring:theme code="${labelKey}"/>
	<c:if test="${showManageUI}" >
		<div class="account-section-header-add pull-right">
			
			<ycommerce:testId code="${urlTestId}">
				<a href="#" class="button add ${showManageUIJS}"><spring:theme code="${showManageUILabel}" /></a>
			</ycommerce:testId>
		</div>
	</c:if>
	
</div>
