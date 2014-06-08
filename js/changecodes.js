

var ChangeCodesCtrl = function() {
  var ajaxResp, simulation = true;
  var comboProj, comboCountry, comboType, comboHosp, listPats;

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

  var init = function() {
    var that = this;

    console.log("Change codes control initializing...");
    ajaxResp = new DelPatsAjaxResponse ();

    $('.fileupload').init();
    /*
    simulation = $("#chkSimulation").is(":checked");
    $("label.checkbox").tooltip({
      title: "Does not perform a real delete",
      placement: "bottom"
    });

    $("#chkSimulation").change(function () {
      simulation = !simulation;
    });
    */
    comboProj = $("#frmPrj");
    comboCountry = $("#frmCountry");
    comboHosp = $("#frmHospital");
    comboType = $("#frmSubjType");
    listPats = $("#frmListPats");

    // theForm = $('#frmDump');

    // Autofill data on change combos
    $(comboProj).change(retrievePats);
    $(comboCountry).change(fillHospitals);
    $(comboHosp).change(retrievePats);
    $(comboType).change(retrievePats);
  }

  return {
    fillHospitals: fillHospitals,
    retrievePats: retrievePats,
//    sendPostReq: sendPostReq,
    init: init
  }
}; // EO DBDumpFormCtrl



var changeCodesCtrl;
var overlay;
$(document).ready(function () {
  overlay = new Overlay ();

  changeCodesCtrl = new ChangeCodesCtrl();
  changeCodesCtrl.init();
});
