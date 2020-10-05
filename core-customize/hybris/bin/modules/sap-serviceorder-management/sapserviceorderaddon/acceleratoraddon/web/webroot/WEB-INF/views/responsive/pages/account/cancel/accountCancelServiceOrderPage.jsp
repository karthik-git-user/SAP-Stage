<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/addons/sapserviceorderaddon/responsive/order/cancel" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<ycommerce:testId code="cancelOrder_section">
    <order:accountCancelServiceOrder order="${orderData}"/>
</ycommerce:testId>


