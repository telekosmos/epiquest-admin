


/*
 * Global Ajax Response from Register server
 */



var ClonFormCtrl = function () {
  
  var ajaxResp;
  var txtNewName;
  var srcPrj, srcGrp, srcIntrv;
	var trgPrj, trgGrp;
	var theForm;
	
  
	
	
	var fillIntrvSrc = function () {
		var xReq = new AjaxReq();
		var postData = "what=clon&action=intrvs";
		
		var grpid = $(srcGrp).val ();
		var prjid = $(srcPrj).val ();
		
		if (grpid != -1 && prjid != -1) {
			postData += "&grpid="+grpid+"&prjid="+prjid;		
	    xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("POST");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onGetIntrvs, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();
		}
		else {
			$(txtNewName).attr("disabled", "disabled");
			$(trgPrj).attr("disabled", "disabled");
			$(trgGrp).attr("disabled", "disabled");
		}
		
	}
	
	
	
	
	var enableTarget = function () {
		if ($(srcIntrv).val() != -1) {
			$("#frmTargetPrj").removeAttr ("disabled");
			$("#frmTargetGrp").removeAttr ("disabled");
			$("#frmTargetIntrv").removeAttr ("disabled");
		}
	}
	
	
	
	var createClone = function () {
		var newName = $(txtNewName).val();
		
		if (newName == "" || $(trgPrj).val() == -1 || $(trgGrp).val() == -1) {
			alert ("You must choose target project, group and name for the new interview");
			return;
		}
		else {
			var xReq = new AjaxReq();
			var postData = "what=clon&action=clon";
			var grpid = $(trgGrp).val ();
			var prjid = $(trgPrj).val ();
			var intrvid = $(srcIntrv).val();
			var intrvName = $("#frmSrcIntrvs option:selected").text();
			
		
			postData += "&intrvid="+intrvid+"&grpid="+grpid+"&prjid="+prjid+"&name="+newName;		
	    xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
	    xReq.setMethod("POST");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onCloned, ajaxResp.onFail, ajaxResp, null);
			if (confirm ("Clone '"+intrvName+"' into '"+newName+"'?"))
	    	xReq.startReq();
		}
		
	}
	
	
	
	
	var init = function () {
    ajaxResp = new ClonAjaxResponse ();
    
		srcPrj = $("#frmSrcPrj");
		srcGrp = $("#frmSrcGrp");
		srcIntrv = $("#frmSrcIntrvs");
		
		txtNewName = $("#frmTargetIntrv");
		trgPrj = $("#frmTargetPrj");
		trgGrp = $("#frmTargetGrp");
				
		theForm = $('#frmCloning');
		
		
		$(srcPrj).change (fillIntrvSrc);
		$(srcGrp).change (fillIntrvSrc);
		$(srcIntrv).change (enableTarget);
    
    
		$("#btnOk").click(function(ev) {
			createClone ();			
		});
		
		
		$("#btnClr").focus(function (ev) {
//			$(this).css('border', '1px solid darkblue');
			$(theForm)[0].reset();
		});
	
  }
	
	
  return {
		fillIntrvSrc: fillIntrvSrc,
//		createClone: createClone,
//		enableTarget: enableTarget,
		
    init:init
  }
}



var clonCtrl;
$(document).ready(function () {
//	overlay = new Overlay ();
	clonCtrl = new ClonFormCtrl();
	clonCtrl.init ();
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


