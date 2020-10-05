<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/addons/sapserviceorderaddon/responsive/order/reschedule" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<ycommerce:testId code="cancelOrder_section">
    <order:accountRescheduleServiceOrder order="${orderData}"/>
</ycommerce:testId>


