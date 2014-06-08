


/***************************************************************************
 ***************************************************************************
 * AJAXRESPONSE INNERCLASS TO RESPONDE THE AJAX REQUESTS TO PERFORM
 * CRUD OPERATIONS
 */

var DelPatsAjaxResponse = function () {

  var clearIntrvCombo = function () {
    var intrvSel = $("#frmListPats");

    $(intrvSel).empty();
  }



  /**
   * Get the interviews for the current group and the current project and fills
   * the source questionnaire combobox
   * @param {Object} o, the response json object
   */
  var onGetSubjects = function (o) {

    // $("#frmQuestionnaire").find('option').remove();
    overlay.hide ();
    try {
      var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      var arrSubjects = jResponse.subjects;
      var res = arrSubjects.length > 0;
      clearIntrvCombo ();
      if (res) {
        // $("#frmListPats").append('<option value="-1">Choose...</option>');

        for (i=0; i<arrSubjects.length; i++) {
          var pat = arrSubjects[i];
          var name = pat.codpatient;
          var id = pat.id;

          addOption(name, name);
        }
      }
    }
    catch (exp) {
      alert ("JSON Parse failed: "+exp);
      return;
    }

  }



  /**
   * Get the sections for the current interview and fills the sections combobox.
   * @param {Object} o, the response json object
   */
  var onGetHospitals = function (o) {

    overlay.hide ();
    try {
      var jResponse = YAHOO.lang.JSON.parse(o.responseText);

      var arrGroups = jResponse.groups;
      var res = arrGroups.length > 0;
      var hospCombo = $("#frmHospital");
      $(hospCombo).empty();

      if (res) {
        $(hospCombo).append('<option value="-1">Choose...</option>');
        // addOption (-1, "Choose a questionnaire");
        for (i=0; i<arrGroups.length; i++) {
          var hosp = arrGroups[i];
          var name = hosp.name;
          var id = hosp.id;
          var code = hosp.code;

          $(hospCombo).append("<option value=\""+code+"\">"+decodeURIComponent(name)+"</option>");
        }
      }
    }
    catch (exp) {
      alert ("JSON Parse failed: "+exp);
      return;
    }

  }

  return {
    onGetSubjects: onGetSubjects,
    onGetHospitals: onGetHospitals
  }

};
/// AJAXRESPONSE /////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
