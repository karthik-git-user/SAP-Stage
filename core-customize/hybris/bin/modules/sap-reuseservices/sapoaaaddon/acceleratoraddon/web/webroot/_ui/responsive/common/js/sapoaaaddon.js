ACC.pickupinstore = window.ACC.pickupinstore || {};


ACC.pickupinstore.createListItemHtml: function (data,id){

	
	var $rdioEl = $("<input>").attr("type","radio")
						.attr("name","storeNamePost")
						.attr("id","pickup-entry-" + id)
						.attr("data-id", id)
						.addClass("js-pickup-store-input")
						.val(data.name);
	
	var $spanElStInfo = $("<span>")
						.addClass("pickup-store-info")
						.append($("<span>").addClass("pickup-store-list-entry-name").text(data.displayName))
						.append($("<span>").addClass("pickup-store-list-entry-address").text(data.line1 + " " + data.line2))
						.append($("<span>").addClass("pickup-store-list-entry-city").text(data.town));
		
	var $spanElStAvail = $("<span>")
						.addClass("store-availability")
						.append(
								$("<span>")
								.addClass("available")
								.append(document.createTextNode(data.formattedDistance))
								.append("<br>")
								.append(data.stockPickupHtml)
						);
	
	var $lblEl = $("<label>").addClass("js-select-store-label")
					.attr("for","pickup-entry-" + id)
					.append($spanElStInfo)
					.append($spanElStAvail);
	
	return $("<li>").addClass("pickup-store-list-entry")
					.append($rdioEl)
					.append($lblEl);
}