



var DelIntrvFormCtrl = function () {
	var ajaxResp;
	var simulation = true;
	
	var init = function () {
		var that = this;
		ajaxResp = new DelIntrvsAjaxResponse();
		simulation = $("#chkSimulation").is(":checked");
		$("label.checkbox").tooltip({
			title: "Does not perform a real delete",
			placement: "bottom"
		});
		
		$("#chkSimulation").change(function () {
			simulation = !simulation;
		});
		
		// Autocomplete programming
		$('#type_code').typeahead({
			minLength: 3,
			items:9999,
			source: function(query, process) {
				return $.get('/admtool/servlet/AjaxUtilServlet', {
					what: 'pats', q: query
				}, function (data) {
					return process(data.subjects)
				})
					
			}
		});
		
		
		// Search button ajax request
		$("#btnSearch").click(function (ev) {
//			ajaxResp.test();
			var xReq = new AjaxReq();
			var postData = "what=intrv4pat&code=";
			
			var patCode = $('#type_code').val();
			postData += patCode;
			
			xReq.setUrl(APP_ROOT+"/servlet/qryservlet");
	    xReq.setMethod("GET");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onGetInterviews, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();
		});		
		
		$("#btnClear").click(function(ev) {
			$("#type_code").val('');
			$("#frmListIntrvs").empty();
		});
		
		
		// Submission to delete the selected interviews
		$("#btnSubmit").click(function (ev) {
			if ($("#frmListIntrvs option:selected").length == 0) {
				alert ("At least one interview has to be selected");
				return;
			}
			
			var xReq = new AjaxReq();
			var postData = "what=di&sim="+simulation+"&pat=";
			
			var patCode = $('#type_code').val();
			postData += patCode;
			
			var selIntrvs = [];
			$("#frmListIntrvs option:selected").each(function(index){
				selIntrvs.push($(this).text());
			})
			
			postData += "&intrvs="+selIntrvs.join(",");
			postData = encodeURI(postData);
			
			xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("POST");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onResponseDelete, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();	
		});
		
	} // EO init
	
	
	
	
	return {
		init: init,
		
	}
	
}



var delCtrl;
var overlay;
$(document).ready(function () {
	console.log("startup typeahead!!");
	
	overlay = new Overlay ();
	delCtrl = new DelIntrvFormCtrl();
	delCtrl.init();
});