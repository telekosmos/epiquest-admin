


/***************************************************************************
 *************************************************************************** 
 * AJAXRESPONSE INNERCLASS TO RESPONDE THE AJAX REQUESTS TO PERFORM
 * CRUD OPERATIONS
 */	

var DBDumpAjaxResponse = function () {


/**
 * Reset parameters from register form. This is a convinient private method to
 * be run after an user registration
 *
  var resetForm = function () { 
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    document.getElementById('re-password').value = '';
    document.getElementById('email').value = '';
    document.getElementById('registered_role').options[0].selected = true;
    
    var aSelectedGroupList = document.getElementById('selected_group').options;
    for (var iIndex = 0; iIndex < aSelectedGroupList.length; iIndex++) {
      aSelectedGroupList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_group'), 
                document.getElementById('registered_group'));
    
    var aSelectedProjectList = document.getElementById('selected_project').options;
    for (var iIndex = 0; iIndex < aSelectedProjectList.length; iIndex++) {
      aSelectedProjectList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_project'), 
                document.getElementById('registered_project'));
                
    document.getElementById('frmGrpName').value = 'New group name';
    document.getElementById('frmPrjName').value = 'New project name';
		
		document.getElementById('frmGrpParent').disabled = "disabled";
  };
	
  
	var onFail = function (o) {
// Access the response object's properties in the
// same manner as listed in responseSuccess( ).
// Please see the Failure Case section and
// Communication Error sub-section for more details on the
// response object's properties.
		var msg = o.responseText;
		
//		document.getElementById("body").appendChild(document.createTextNode(msg));
		alert (msg);
	};

*/

  var clearIntrvCombo = function (theElemId) {
    var intrvSel = $("#"+theElemId);

    $(intrvSel).empty();
  }



  var addOption = function (elemId, id, name) {
    // var intrvSel = $("#frmQuestionnaire");
    var elemSel = $("#"+elemId);
    $(elemSel).append ("<option value=\""+id+"\">"+name+"</option>");
  }



  var onGetSecondaryGroups = function (o) {
    overlay.hide ();
    try {
      var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      var arrGroups = jResponse.groups;
      var res = arrGroups.length > 0;
      if (res) {
        clearIntrvCombo ("frmGroup");
        $("#frmGroup").append('<option value="-1">Choose...</option>');

        for (var i=0; i<arrGroups.length; i++) {
          var grp = arrGroups[i];
          var name = grp.name;
          var id = grp.id;

          addOption("frmGroup", id, decodeURIComponent(name));
        }
        $("#frmGroup").prop("disabled", false);
      }
    }
    catch (exp) {
      alert ("JSON Parse failed: "+exp);
      return;
    }

  }

	
/**
 * Get the interviews for the current group and the current project and fills
 * the source questionnaire combobox
 * @param {Object} o, the response json object
 */
	var onGetIntrvs = function (o) {
		
		// $("#frmQuestionnaire").find('option').remove();
		overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
			var arrIntrvs = jResponse.intrvs;
			var res = arrIntrvs.length > 0;
			if (res) {
				clearIntrvCombo ("frmQuestionnaire");
				$("#frmQuestionnaire").append('<option value="-1">Choose...</option>');
				
				for (var i=0; i<arrIntrvs.length; i++) {
					var intrv = arrIntrvs[i];
					var name = intrv.name;
					var id = intrv.id;
					
					addOption("frmQuestionnaire", id, decodeURIComponent(name));
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
	var onGetSecs = function (o) {
		
		overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			var arrSections = jResponse.sections;
			var res = arrSections.length > 0;
			var sectionSel = $("#frmSection");
			$(sectionSel).empty();
			
			if (res) {
				$(sectionSel).append('<option value="-1">Choose...</option>');
				// addOption (-1, "Choose a questionnaire");
				for (var i=0; i<arrSections.length; i++) {
					var sec = arrSections[i];
					var name = sec.name;
					var id = sec.id;
					var order = sec.order;
					
					$(sectionSel).append ("<option value=\""+order+"\">"+decodeURIComponent(name)+"</option>");
				}
			}
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
		
	}
	
	
	
	
	
	

	return {
		onGetIntrvs: onGetIntrvs,
    onGetSecondaryGroups: onGetSecondaryGroups,
		onGetSections: onGetSecs		
	}
	
};
/// AJAXRESPONSE /////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
