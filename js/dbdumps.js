/*
 * Global Ajax Response from Register server
 */
var DBDumpFormCtrl = function () {

  var ajaxResp;
  var txtNewName;
  var comboProj, comboCountry, comboGrp, comboIntrv, comboSec, repeatingCheck;
  var usrId;
  var theForm;



  var fillSecondaryGroups = function () {
    var xReq = new AjaxReq();
    var postData = "what=hosp";

    var grpid = $(comboCountry).val();
    grpid = grpid == -1 ? '' : grpid;
    var prjid = comboProj.val();

    if (grpid != '') {
      postData += "&grpid=" + grpid + "&prjid=" + prjid;
      xReq.setUrl(APP_ROOT + "/servlet/AjaxUtilServlet");
      xReq.setMethod("GET");
      xReq.setPostdata(postData);
      xReq.setCallback(ajaxResp.onGetSecondaryGroups, ajaxResp.onFail, ajaxResp, null);
      xReq.startReq();
    }
  }


  /**
   * Get questionnaires based on the project and group they belong to
   */
  var fillIntrvs = function () {
    var xReq = new AjaxReq();
    var postData = "what=intrvs";

    var grpid = $(comboCountry).val();
    grpid = grpid == -1 ? '' : grpid;
    var prjid = $(comboProj).val();

    if (prjid != -1) {
      postData += "&grpid=" + grpid + "&prjid=" + prjid;
      xReq.setUrl(APP_ROOT + "/servlet/AjaxUtilServlet");
      xReq.setMethod("GET");
      xReq.setPostdata(postData);
      xReq.setCallback(ajaxResp.onGetIntrvs, ajaxResp.onFail, ajaxResp, null);
      xReq.startReq();
    }

  }


  /**
   * Get the sections for an interview based on intrvid
   */
  var fillSections = function () {
    var xReq = new AjaxReq();
    var postData = "what=secs";

    var intrvid = $(comboIntrv).val();

    if (intrvid != -1) {
      postData += "&intrvid=" + intrvid;
      xReq.setUrl(APP_ROOT + "/servlet/AjaxUtilServlet");
      xReq.setMethod("GET");
      xReq.setPostdata(postData);
      xReq.setCallback(ajaxResp.onGetSections, ajaxResp.onFail, ajaxResp, null);
      xReq.startReq();
    }
    /*
     else {
     $(txtNewName).attr("disabled", "disabled");
     $(trgPrj).attr("disabled", "disabled");
     $(trgGrp).attr("disabled", "disabled");
     }
     */

  }


  /*
   var getDump = function () {
   var xReq = new AjaxReq();
   var postData = "what=dump";

   var grpid = $(comboGrp).val();
   var prjid = $(comboProj).val();
   var intrvid = $(comboIntrv).val();
   var secOrder = $(comboSec).val();

   if (prjid != -1 && intrvid != -1 && secid != -1) {
   grpid = grpid == -1? '': grpid;
   postData += "&intrvid="+intrvid+"&grpid="+grpid+"&sec-ord="+secOrder;
   postData += "&prjid="+prjid;

   xReq.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
   xReq.setMethod("GET");
   xReq.setPostdata(postData);
   xReq.setCallback(ajaxResp.onGetDump, ajaxResp.onFail, ajaxResp, null);
   xReq.startReq();
   }
   else {
   $(txtNewName).attr("disabled", "disabled");
   $(trgPrj).attr("disabled", "disabled");
   $(trgGrp).attr("disabled", "disabled");
   }

   }
   */


  var init = function () {
    console.log("Dump control initializing...");
    ajaxResp = new DBDumpAjaxResponse();

    comboProj = $("#frmProject");
    comboCountry = $("#frmCountry");
    comboGrp = $("#frmGroup");
    comboIntrv = $("#frmQuestionnaire");
    comboSec = $("#frmSection");
    repeatingCheck = $("#frmRepCheck");

    theForm = $('#frmDump');

    var me = this;
    // $(comboProj).change(fillIntrvs);
    // $(comboCountry).change(fillSecondaryGroups);
    // $(comboCountry).change(fillIntrvs);
    $(comboCountry).change(function () {
      me.fillSecondaryGroups();
      me.fillIntrvs();
    });
    $(comboGrp).change(fillIntrvs);
    $(comboIntrv).change(fillSections);

//		$(srcIntrv).change(enableTarget);


    $("#btnSend").click(function (ev) {
      // getDump();
      var prjid = $(comboProj).val();
      var secondaryGrp = $(comboGrp).val();
      var mainGrp = $(comboCountry).val();
      var intrvid = $(comboIntrv).val();
      var secOrder = $(comboSec).val();
      var repeatingDump = repeatingCheck.prop('checked');
      var errMsg = "";
      //  what=dump&prjid=-1&grpid=&intrvid=-1&secid=-1"
      if (prjid === undefined || prjid == -1)
        errMsg = "A project must be selected";

      else if (mainGrp === undefined || mainGrp == -1)
        errMsg = "At least a main group must be chosen to retrieve a dump";

      else if (intrvid === undefined || intrvid == -1)
        errMsg = "A questionnaire must be chosen to retrieve a dump";

      else if (secOrder === undefined || secOrder == -1)
        errMsg = "One section must be chosen to retrieve a dump";

      else
        errMsg = "";

      if (errMsg != "") {
        $("div.alert").removeClass('alert-success').addClass('alert-error').
          css('visibility', 'visible');
        $("div#msg").html(errMsg);
        return;
      }
      else {
        var okMsg = 'Dump requested with the parameters:<br/>';
        okMsg += '<b>Project</b>: '+$("#frmProject option:selected").html()+'<br/>';
        okMsg += '<b>Country</b>: '+$("#frmCountry option:selected").html()+'<br/>';
        var secGrp = $("#frmGroup option:selected").val() == -1? "": $("#frmGroup option:selected").html();
        okMsg += '<b>Group</b>: '+ secGrp +'<br/>';
        okMsg += '<b>Questionnaire</b>: '+$("#frmQuestionnaire option:selected").html()+'<br/>';
        okMsg += '<b>Section</b>: '+$("#frmSection option:selected").html()+'<br/>';

        $("div.alert").removeClass('alert-error').addClass('alert-success').
          css('visibility', 'visible');
        $("div#msg").html(okMsg);
      }

      var grpid = secondaryGrp == -1? (mainGrp == -1? '': mainGrp): secondaryGrp;
      var dumpUrl = APP_ROOT + '/servlet/AjaxUtilServlet?what=dump&prjid=' + prjid + '&grpid=';
      dumpUrl += grpid + '&intrvid=' + intrvid + '&secid=' + secOrder;

      var questionaireName = $('#frmQuestionnaire option:selected').text();
      questionaireName = questionaireName.replace(/ /g, '_');
      var fileName = $('#frmProject option:selected').text()+'-'+questionaireName+'-sec'+secOrder;
      var fileExt = repeatingDump? '.xlsx': '.csv';
      fileName += fileExt;

      dumpUrl = APP_ROOT + '/datadump/'+fileName+'?what=dump&prjid=' + prjid + '&grpid=';
      dumpUrl += grpid + '&intrvid=' + intrvid + '&secid=' + secOrder;
      if (repeatingDump)
        dumpUrl += '&repd=1';

      document.location.href = dumpUrl;
      $(theForm)[0].reset();
    });


    $("#btnReset").focus(function (ev) {
//			$(this).css('border', '1px solid darkblue');
      $(theForm)[0].reset();
    });

    $("#alertDiv").css('visibility', 'hidden');
  }


  return {
    fillIntrvs: fillIntrvs,
    fillSections: fillSections,
    fillSecondaryGroups: fillSecondaryGroups,
//		createClone: createClone,
//		enableTarget: enableTarget,

    init: init
  }
} // EO DBDumpFormCtrl


var dbdumpCtrl;
var overlay;
$(document).ready(function () {
  overlay = new Overlay();

  dbdumpCtrl = new DBDumpFormCtrl();
  dbdumpCtrl.init();
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


