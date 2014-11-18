

var UploadFileCtrl = function() {
  var files;
  var ajaxRespObj;
  var simulation;

  var prepareUpload = function(ev) {
    files = ev.target.files;
  }

  var setSimulation = function (sim) {
    simulation = sim;
  }

  var uploadCodesFile = function() {
    // START A LOADING SPINNER HERE

    // Create a formdata object and add the files
    var data = new FormData();
    if (files.length == 0)
      return;

    $.each(files, function(key, value) {
      data.append(key, value);
    });
    data.append("sim", simulation);

    console.log("Starting $.ajax request");
    $.ajax({
      url: APP_ROOT+"/servlet/ChangeCodesServlet",
      type: 'POST',
      data: data,
      cache: false,
      dataType: 'json', // datatype from servlet
      processData: false, // Don't process the files
      contentType: false, // Set content type to false as jQuery will tell the server its a query string request

      success: ajaxRespObj.onFileUpload,
      error: ajaxRespObj.onFail

    }); // EO ajax
    overlay.show();
  }

  /*
  var test = function() {
    var data = new FormData();
    $.each(files, function(key, value) {
      data.append(key, value);
    });

    console.log("UploadFileCtrl.test: data="+data);
  }
  */
  var init = function() {
    console.log('Initializing UploadFileCtrl');
    $('input[type="file"]').on('change', prepareUpload);
    $("#btnUpl").bind('click', uploadCodesFile);

    simulation = $("#chkSimulation").is(":checked");
    $("label.checkbox").tooltip({
      title: "Does not perform a real delete",
      placement: "bottom"
    });

    $("#chkSimulation").change(function () {
      simulation = !simulation;
    });

    $("#btnReset").click(function (ev) {
      $("#uploadform")[0].reset();
      simulation = true;
      $("#chkSimulation").prop('checked', true);
    });

    ajaxRespObj = new UploadFileAjaxResponse();
  }

  return {
    init: init,
    setSimulation: setSimulation,
    uploadCodesFile: uploadCodesFile
  }
}
