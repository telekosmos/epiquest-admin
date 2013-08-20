


/*
 * Global Ajax Response from Register server
 */
var DelPatsFormCtrl = function () {
  
  var ajaxResp;
  var txtNewName;
  var comboProj, comboCountry, comboHosp, comboIntrv, listPats, delPats;
	// var theForm;
  
	var simulation = true;
	
	/**
	 * Get questionnaires based on the project and group they belong to
	 */
	var retrievePats = function () {
		var xReq = new AjaxReq();
		var postData = "what=subj";
		
		var grpCode = $(comboHosp).val();
		grpCode = (grpCode == -1 || grpCode === undefined)? '': grpCode;
		
		var subjType = $(comboType).val();
		subjType = (subjType == -1 || subjType === undefined)? '': subjType;
		
		var prjcode = $(comboProj).val();
		
		if (prjcode != -1) {
			postData += "&grpCode="+grpCode+"&prjid="+prjcode+"&subjType="+subjType;		
	    xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("GET");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onGetSubjects, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();
		} /*
		else {
			$(txtNewName).attr("disabled", "disabled");
			$(trgPrj).attr("disabled", "disabled");
			$(trgGrp).attr("disabled", "disabled");
		}
		*/
	}
	
	
	
	/**
	 * Get the sections for an interview based on intrvid
	 */
	var fillHospitals = function () {
		var xReq = new AjaxReq();
		var postData = "what=hosp";
		
		var grpCode = $(comboCountry).val ();
		
		if (grpCode != -1) {
			postData += "&grpCode="+grpCode;		
	    xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("GET");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onGetHospitals, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();
		}/*
		else {
			$(txtNewName).attr("disabled", "disabled");
			$(trgPrj).attr("disabled", "disabled");
			$(trgGrp).attr("disabled", "disabled");
		}
		*/
		
	}
	
	
	
	
	/**
	 * Send an POST ajax request with the list of patients as param to delete them
	 * @param {Object} patCodesList, an array with the patient codes to delete
	 */
	var sendPostReq = function (patCodesList) {
		var xReq = new AjaxReq();
		var postData = "what=dp&sim="+simulation; // dp stands for Delete Patient
		
		var listEmpty = patCodesList.length == 0;
		
		if (!listEmpty) {
			postData += "&pats="+patCodesList.join();		
	    xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("POST");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onDelPatients, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();
		}
		
	}
	
	
	var init = function () {
		
		var that = this;
		
		console.log("Dump control initializing...");
    ajaxResp = new DelPatsAjaxResponse ();
    
		simulation = $("#chkSimulation").is(":checked");
		$("label.checkbox").tooltip({
			title: "Does not perform a real delete",
			placement: "bottom"
		});
		
		$("#chkSimulation").change(function () {
			simulation = !simulation;
		});
		
		comboProj = $("#frmPrj");
		comboCountry = $("#frmCountry");
		comboHosp = $("#frmHospital");
		comboType = $("#frmSubjType");
		listPats = $("#frmListPats");
		delPats = $("#frmDelPats");
		
		// theForm = $('#frmDump');
		
		// Autofill data on change combos
		$(comboProj).change(retrievePats);
		$(comboCountry).change(fillHospitals);
		$(comboHosp).change(retrievePats);
		$(comboType).change(retrievePats);
		
//		$(srcIntrv).change(enableTarget);
    
		
		$("#add2DeleteBtn").click(function (ev){
			moveOptions(document.getElementById('frmListPats'), document.getElementById('frmDelPats'));
		});
		
		$("#rmvDeleteBtn").click(function (ev) {
			moveOptions(document.getElementById('frmDelPats'), document.getElementById('frmListPats'));
		});
    
		$("#btnReset").click(function (ev) {
			$("#frmPatsDeletion")[0].reset();
			$(delPats).empty();
			$(listPats).empty();
		})
		
		$("#btnSend").click(function(ev) {
			// getDump();
			var patCodes = [];
			$("select#frmDelPats > option").each(function (index) {
				patCodes.push($(this).val());
			});
			overlay.show();
			that.sendPostReq(patCodes);
		});
		
		
		$("#btnClr").click(function (ev) {
//			$(this).css('border', '1px solid darkblue');
			$(delPats).empty();
		});
		
  }
	
	
	
  return {
		fillHospitals: fillHospitals,
		retrievePats: retrievePats,
		sendPostReq: sendPostReq,
//		createClone: createClone,
//		enableTarget: enableTarget,
    init: init
  }
} // EO DBDumpFormCtrl



var delPatsCtrl;
var overlay;
$(document).ready(function () {
	overlay = new Overlay ();
	
	delPatsCtrl = new DelPatsFormCtrl();
	delPatsCtrl.init();
});


/*
var overlay;
var admCtrl;
function onReady () {
  overlay = new Overlay();
  admCtrl = new AdmFormCtrl ();
  admCtrl.init();
  
//  ajaxResp = new AjaxResponse();
}
*/


