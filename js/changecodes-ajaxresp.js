


/***************************************************************************
 ***************************************************************************
 * AJAXRESPONSE INNERCLASS TO RESPONDE THE AJAX REQUESTS TO PERFORM
 * CRUD OPERATIONS
 */

var ChangeCodesAjaxResponse = function () {

  var displayMsg = function(o) {
    var jResponse = YAHOO.lang.JSON.parse(o.responseText);
    var innerContent = "", count=0;
    var subjectsChanged = jResponse.subjects;
    $.each(subjectsChanged, function(i, pair) {
      $.each(pair, function(key, val) {
        innerContent += '<li>'+key+' -> '+val+'</li>';
        count++;
      });
    })

    innerContent = count + " subject codes found in file:<br/><ul>"+innerContent+"</ul>";
    innerContent += "Subjects affected: "+ jResponse.rows_affected+"<br/>";
    var subjectsWithSamples = jResponse.subjs_with_samples.join(",");
    subjectsWithSamples = (subjectsWithSamples == "")? "None": subjectsWithSamples;

    innerContent += "Subjects with samples: "+ subjectsWithSamples+"<br/>";
    var subjectsUnchanged = jResponse.subjects_unchanged.join(",");
    subjectsUnchanged = (subjectsUnchanged == "")? "None": subjectsUnchanged;
    innerContent += "Subjects unchanged: " + subjectsUnchanged;

    $("#responseDiv").empty();
    // $("#responseDiv").append(innerContent);
    $("#responseDiv").html(innerContent);

    return jResponse;
  }


  /**
   * Function callback for single subject code change
   */
  var onSubjectChange = function(o) {
    overlay.hide();

    try {
      var parsedJson = displayMsg(o);
      var subjectChange = parsedJson.subjects[0], oldCode, newCode;
      $.each(subjectChange, function(key, val) {
        oldCode = key;
        newCode = val;
      });
      $("#frmListPats option:selected").val(newCode).text(newCode).css('color', 'green');
    }
    catch (exp) {
      alert ("JSON Parse failed: "+exp);
      return;
    }

  }


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

          // Maybe id is better than name for option value...
          $("#frmListPats").append("<option value=\""+name+"\">"+decodeURIComponent(name)+"</option>");
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

  var onFail = function (o) {
// Access the response object's properties in the
// same manner as listed in responseSuccess( ).
// Please see the Failure Case section and
// Communication Error sub-section for more details on the
// response object's properties.
    var msg = o.responseText;

//		document.getElementById("body").appendChild(document.createTextNode(msg));
    console.error(msg);
  };

  return {
    onGetSubjects: onGetSubjects,
    onGetHospitals: onGetHospitals,
    onSubjectChange: onSubjectChange,
    onFail: onFail
  }

};
/// AJAXRESPONSE /////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
