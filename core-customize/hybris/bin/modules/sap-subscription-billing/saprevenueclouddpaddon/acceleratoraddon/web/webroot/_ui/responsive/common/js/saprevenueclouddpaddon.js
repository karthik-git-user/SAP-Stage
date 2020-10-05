/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

 var isAddNewBtnClicked = false;

 $(document).ready(function() {

     $(document).on("click","#btnSavedCards", function(e){
             e.preventDefault();
             var url = $(this).data("savedCardsUrl");
             ACC.colorbox.open('Saved Cards',{
                 href: url,
                 maxWidth:"100%",
                 width:"380px",
                 initialWidth :"380px"
             });
         });

     $(document).on("click","#btnAddNewCard", function(e){
         e.preventDefault();
         isAddNewBtnClicked = true;
         $("#btnAddNewCard").html('Checking...');
         $("#btnAddNewCard").add('disabled', true);

         var url = $(this).data("newCardUrl");
         var win = window.open(url, '_blank');
         win.focus();
      });

      var checkCardRegistration = function (){
          //If Add New Card button exists
          if ($("#btnAddNewCard").length && isAddNewBtnClicked){
              var url = $("#btnAddNewCard").data("checkCardUrl");
              ACC.colorbox.open('Payment Details',{
                  href: url,
                  maxWidth:"100%",
                  width:"380px",
                  initialWidth :"380px"
              });
          }
      };

     $(window).on("focus", checkCardRegistration);
 });