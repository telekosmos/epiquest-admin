

/***************************************************************************
 *************************************************************************** 
 * AJAXRESPONSE INNERCLASS TO RESPONDE THE AJAX REQUESTS TO PERFORM
 * CRUD OPERATIONS
 */	

var ClonAjaxResponse = function () {


/**
 * Reset parameters from register form. This is a convinient private method to
 * be run after an user registration
 */
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
	



	var clearIntrvCombo = function () {
		var intrvSel = $("#frmSrcIntrvs");
		
		$(intrvSel).empty();
	}
  
	
	
	var addOption = function (id, name) {
		var intrvSel = $("#frmSrcIntrvs");
		
		$(intrvSel).append ("<option value=\""+id+"\">"+name+"</option>");
	}
	
	
	
/**
 * Get the interviews for the current group and the current project and fills
 * the source questionnaire combobox
 * @param {Object} o, the response json object
 */
	var onGetIntrvs = function (o) {
		
		overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      var res = jResponse.res;
			var arrIntrvs = jResponse.intrvs;
			if (res) {
				clearIntrvCombo ();
				addOption (-1, "Choose a questionnaire");
				for (i=0; i<arrIntrvs.length; i++) {
					var intrv = arrIntrvs[i];
					var name = intrv.name;
					var id = intrv.id;
					
					addOption (id, name);
				}
			}
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
		
	}
	
	
	var onCloned = function (o) {
		overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      var res = jResponse.res;
			
			alert (jResponse.msg);
			if (res == 1) {
				$("frmTargetIntrv").attr("disabled", "disabled");
				$("frmTargetPrj").attr("disabled", "disabled");
				$("frmTargetGrp").attr("disabled", "disabled");
			
				clearIntrvCombo ();
				$("#frmCloning")[0].reset ();
			}		
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
	}
	
	
	

	return {
		onGetIntrvs: onGetIntrvs,
		onCloned: onCloned,
		
		onFail: onFail
	}
	
};
/// AJAXRESPONSE /////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
