var buttonLoad;
var dropdownCountries;
var buttonAdd;
var buttonUpdate;
var buttonDelete;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;
$(document).ready(function(){
	buttonLoad = $("#butLoad");
	dropdownCountries = $("#dropdownCountries");
	buttonAdd= $("#butAdd");
	buttonUpdate= $("#butUpdate");
	buttonDelete= $("#butDelete");
	labelCountryName = $("#labelCountryName");
	fieldCountryName = $("#countryName");
	fieldCountryCode = $("#countryCode");
	buttonLoad.click(function(){
		loadCountries();
	});
	dropdownCountries.on("change", function() {
		changeFormStateToSelectedCountry();
	});
	buttonAdd.click(function(){
		if(buttonAdd.val() == "Add")
		{
			addCountry();
		}
		else{
		changeFormStateToNew();			
		}
	});
	buttonUpdate.click(function() {
		updateCountry();
	});
	buttonDelete.click(function() {
		deleteCountry();
	});
});
function validateformCountry(){
	formCountry = document.getElementById("countryForm");
	if(!formCountry.checkValidity()){
		formCountry.reportValidity();
		return false;
	}
	return true;
}
function deleteCountry(){
	countryId = dropdownCountries.val().split("-")[0];
	url = contextPath+ "countries/delete/" + countryId;
	$.get(url,function() {
		$("#dropdownCountries option[value = '" + dropdownCountries.val() +"']").remove();
		changeFormStateToNew();
	}).done(function() {
		showToastMessage("The country has been deleted.");
	}).fail(function() {
		showToastMessage("ERROR: Could not connected to server or server encountered a error.");
	});
}
function updateCountry(){
	if(!validateformCountry()){
		return;
	}
	url = contextPath+ "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	countryId = dropdownCountries.val().split("-")[0];
	jsonData = {id: countryId,name: countryName, code : countryCode};
	$.ajax({
		type : 'POST',
		url : url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType : 'application/json'
	}).done(function(countryId) {
		$("#dropdownCountries option:selected").text(countryName);
		$("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
		showToastMessage("The country has been updated");
		changeFormStateToNew();
	}).fail(function() {
		showToastMessage("ERROR: Could not connected to server or server encountered a error.");
	});
}

function addCountry(){
	if(!validateformCountry()){
		return;
	}
	url = contextPath+ "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	jsonData = {name: countryName, code : countryCode};
	$.ajax({
		type : 'POST',
		url : url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType : 'application/json'
	}).done(function(countryId) {
		selectedNewlyAddedCountryID(countryId,countryName,countryCode);
		showToastMessage("The new country has been added");
	}).fail(function() {
		showToastMessage("ERROR: Could not connected to server or server encountered a error.");
	});
}
function selectedNewlyAddedCountryID(id, name, code){
	optionValue = id+ "-" + code;
	$("<option>").val(optionValue).text(name).prop("selected", true).appendTo(dropdownCountries);
	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}
function changeFormStateToNew(){
	buttonAdd.prop("value", "Add");
	buttonUpdate.prop("disabled", true);
	buttonDelete.prop("disabled", true);
	labelCountryName.text("Country Name:")
	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}
function changeFormStateToSelectedCountry(){
	buttonAdd.prop("value", "New");
	buttonUpdate.prop("disabled", false);
	buttonDelete.prop("disabled", false);
	selectedCountryName = $("#dropdownCountries option:selected").text();
	selectedCountryCode = $("#dropdownCountries option:selected").val().split("-")[1];
	labelCountryName.text("Selected Country:")
	fieldCountryCode.val(selectedCountryCode);
	fieldCountryName.val(selectedCountryName);
}
function loadCountries(){
	url = contextPath+ "countries/list";
	$.get(url,function(responseJSON) {
		dropdownCountries.empty();
		$.each(responseJSON, function(index,country){
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropdownCountries);
		})
	}).done(function() {
		buttonLoad.val("Refresh Country List");
		showToastMessage("All countries has been loaded.");
	}).fail(function() {
		showToastMessage("ERROR: Could not connected to server or server encountered a error.");
	});
	
}
function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}