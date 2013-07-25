


/*
 * Global Ajax Response from Register server
 */
var DelPatsFormCtrl = function () {
  
  var ajaxResp;
  var txtNewName;
  var comboProj, comboGrp, comboIntrv, listPats, delPats;
	// var theForm;
  
	
	/**
	 * Get questionnaires based on the project and group they belong to
	 */
	var fillIntrvs = function () {
		var xReq = new AjaxReq();
		var postData = "what=intrvs";
		
		var grpid = $(comboGrp).val();
		grpid = grpid == -1? '': grpid;
		var prjid = $(comboProj).val();
		
		if (prjid != -1) {
			postData += "&grpid="+grpid+"&prjid="+prjid;		
	    xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("GET");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onGetIntrvs, ajaxResp.onFail, ajaxResp, null);
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
		var postData = "what=secs";
		
		var intrvid = $(comboIntrv).val ();
		
		if (intrvid != -1) {
			postData += "&intrvid="+intrvid;		
	    xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("GET");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onGetSections, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();
		}/*
		else {
			$(txtNewName).attr("disabled", "disabled");
			$(trgPrj).attr("disabled", "disabled");
			$(trgGrp).attr("disabled", "disabled");
		}
		*/
		
	}
	
	
	
	
	
	
	
	
	var init = function () {
		console.log("Dump control initializing...");
    ajaxResp = new DBDumpAjaxResponse ();
    
		comboProj = $("#frmPrj");
		comboCountry = $("#frmCountry");
		comboHosp = $("#frmHospital");
		listPats = $("#frmListPats");
		delPats = $("#frmDelPats");
		
		theForm = $('#frmDump');
		
		// Autofill data on change combos
		$(comboProj).change(fillIntrvs);
		$(comboGrp).change(fillIntrvs);
		$(comboIntrv).change(fillSections);
		
//		$(srcIntrv).change(enableTarget);
    
    
		$("#btnSend").click(function(ev) {
			// getDump();
			var prjid = $(comboProj).val();
			var grpid = $(comboGrp).val();
			var intrvid = $(comboIntrv).val();
			var secOrder = $(comboSec).val();
			
			grpid = grpid == -1? '': grpid;
			var dumpUrl = APP_ROOT+'/servlet/AjaxUtilServlet?what=dump&prjid='+prjid+'&grpid=';
			dumpUrl += grpid+'&intrvid='+intrvid+'&secid='+secOrder;
			
			document.location.href = dumpUrl;
			$(theForm)[0].reset();
		});
		
		
		$("#btnReset").focus(function (ev) {
//			$(this).css('border', '1px solid darkblue');
			$(theForm)[0].reset();
		});
		
  }
	
	
	
  return {
		fillIntrvs: fillIntrvs,
		fillHospitals: fillHospitals,
		fillSubjects: fillSubjects,
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


