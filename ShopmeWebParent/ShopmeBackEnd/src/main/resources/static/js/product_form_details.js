/**
 * 
 */
 $(document).ready(function() {

	$("a[name = 'linkRemoveDetail']").each(function(index){
		$(this).click(function(){
			removeDetailSectionByIndex(index);
		});
	});
	
});

 function addNextDetailSection(){
	 allDivDetails = $("[id^='divDetail']");
	 // select all element with id started with divDetail, ex divDetail0, divDetail1, divDetail2, ....
	 divDetailCount = allDivDetails.length;
	 htmlExtraDetailSection =
	 `
	 <div class="form-inline" id = "divDetail${divDetailCount}">
	 	<input type = "hidden" name = "detailIDs" value = "0">
		<label class = "m-3">Name:</label>
		<input type ="text" class = "form-control w-25" name = "detailNames" maxlength="255">
		<label class= "m-3">Value:</label>
		<input type ="text" class = "form-control w-25" name = "detailValues" maxlength="255">
	</div>
	 `;
	 $("#divProductDetails").append(htmlExtraDetailSection);
	previousDivDetailSection = allDivDetails.last(); 
	previousDivDetailId = previousDivDetailSection.attr("id");
	htmlLinkRemove = `
	<a class="btn fas fa-times-circle fa-2x icon-dark" 
	title="Remove this detail"
	href="javascript:removeDetailSectionById('${previousDivDetailId}')"> </a>
	`;

	 previousDivDetailSection.append(htmlLinkRemove);
 }
 function removeDetailSectionById(id)
 {
	 //alert(id);
	 $("#"+id).remove();
 }
 function removeDetailSectionByIndex(index)
 {
	 $("#divDetail"+index).remove();
 }
 