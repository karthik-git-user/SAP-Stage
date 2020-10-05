<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:if test="${show or profileEdit}">

    <script type="text/javascript">
         window.gigyaHybris = window.gigyaHybris || {};
         window.gigyaHybris.raas = window.gigyaHybris.raas || {};
         <%--  gigyaRaas contains serialized block of json code which is already sanitized when populated by the renderer so it is safe to emit --%> 
         window.gigyaHybris.raas['${ycommerce:encodeJavaScript(id)}'] = ${gigyaRaas};
    </script>
    
    <c:choose>
        <c:when test="${embed}">
            <div id="${fn:escapeXml(containerID)}"></div>
        </c:when>
        <c:otherwise>
            <div class="gigya-raas"><a class="gigya-raas-link" data-gigya-id="${fn:escapeXml(id)}" data-profile-edit="${fn:escapeXml(profileEdit)}"
                                       href="#">${fn:escapeXml(linkText)}</a></div>
        </c:otherwise>
    </c:choose>
    
    <div id="dialog" title="Basic dialog" >
    </div>
    
</c:if>
