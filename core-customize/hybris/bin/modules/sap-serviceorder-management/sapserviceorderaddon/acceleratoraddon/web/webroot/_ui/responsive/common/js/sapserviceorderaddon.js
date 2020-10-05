/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
$(function(){
    var elem = document.querySelector('[data-scheduleleaddays]'); 
    if(elem){
    var date = new Date();
    var minDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() + parseInt(elem.dataset.scheduleleaddays));
    $("#scheduleDate").datepicker({
        changeMonth: true,
        changeYear: true,
        minDate: minDate,
        dateFormat: 'dd-mm-yy' 
     });
    }
}); 